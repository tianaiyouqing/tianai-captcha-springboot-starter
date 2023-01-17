package cloud.tianai.captcha.spring.plugins.chain.validators;

import cloud.tianai.captcha.spring.common.util.IPUtils;
import cloud.tianai.captcha.spring.store.CacheStore;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.util.TrackUtils;
import cloud.tianai.captcha.validator.impl.chain.ChainCustomValidator;
import cloud.tianai.captcha.validator.impl.chain.ChainImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.chain.TransmitData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator.USER_CURRENT_PERCENTAGE_STD;

/**
 * @Author: 天爱有情
 * @date 2023/1/4 11:21
 * @Description 一定时间内差异校验, 基于IP校验， 必须是web应用， 且能获取到request
 */
@Slf4j
@RequiredArgsConstructor
public class DifferenceChainValidator implements ChainCustomValidator {

    private final CacheStore cacheStore;

    @Setter
    @Getter
    /** 过期时间. */
    private Long timeWindow = TimeUnit.HOURS.toMillis(1);
    @Getter
    @Setter
    private String prefix = "captcha:valid:difference:";
    @Getter
    @Setter
    /** 检测在距离上一个验证码验证成功后的间隔时间. */
    private Long checkTimeInterval = 500L;
    @Getter
    @Setter
    /** 检测一定时间内验证次数超过一定数量后验证失败. */
    private Integer checkSuccessNum = 100;

    @Override
    public boolean valid(TransmitData transmitData, ChainImageCaptchaValidator context) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return true;
        }
        Map<String, Object> objectMap = transmitData.getCaptchaValidData();
        String type = transmitData.getType();
        List<Double> features = transmitData.getFeatures();
        ImageCaptchaTrack imageCaptchaTrack = transmitData.getImageCaptchaTrack();
        if (features == null) {
            features = TrackUtils.features(imageCaptchaTrack.getTrackList());
            transmitData.setFeatures(features);
        }
        long currentTimeMillis = System.currentTimeMillis();
        HttpServletRequest request = requestAttributes.getRequest();
        // 获取当前ip
        String ipAddr = IPUtils.getIpAddr(request);
        String percentageStd = (String) objectMap.get(USER_CURRENT_PERCENTAGE_STD);

        String key = prefix + ipAddr + ":" + type;
        Map<String, Object> cache = cacheStore.getCache(key);
        if (cache == null) {
            cache = Collections.emptyMap();
        }
        if (!CollectionUtils.isEmpty(cache)) {
            // 校验std
            Object std = cache.get("std");
            if (percentageStd.equals(std)) {
                log.warn("[验证码差异拦截器]在{}ms时间内，该IP:[{}]距离上次验证码std一致[type:{}, std:{}]，触发风控，进行拦截", timeWindow, ipAddr, type, std);
                return false;
            }
            Double xSameQuantityPercentage = Double.valueOf((String) cache.get("xSameQuantityPercentage"));
            Double ySameQuantityPercentage = Double.valueOf((String) cache.get("ySameQuantityPercentage"));
            Double tSameQuantityPercentage = Double.valueOf((String) cache.get("tSameQuantityPercentage"));
            if (features.get(13).equals(xSameQuantityPercentage)) {
                log.warn("[验证码差异拦截器]在{}ms时间内，该IP:[{}]距离上次验证码xSameQuantityPercentage一致[type:{}, xSameQuantityPercentage:{}]，触发风控，进行拦截", timeWindow, ipAddr, type, xSameQuantityPercentage);
                return false;
            }
            if (features.get(14).equals(ySameQuantityPercentage)) {
                log.warn("[验证码差异拦截器]在{}ms时间内，该IP:[{}]距离上次验证码ySameQuantityPercentage一致[type:{}, ySameQuantityPercentage:{}]，触发风控，进行拦截", timeWindow, ipAddr, type, ySameQuantityPercentage);
                return false;
            }
            if (features.get(15).equals(tSameQuantityPercentage)) {
                log.warn("[验证码差异拦截器]在{}ms时间内，该IP:[{}]tSameQuantityPercentage[type:{}, tSameQuantityPercentage:{}]，触发风控，进行拦截", timeWindow, ipAddr, type, tSameQuantityPercentage);
                return false;
            }
            // 校验时间
            String preCheckTime = (String) cache.get("preCheckTime");
            if (Long.parseLong(preCheckTime) + checkTimeInterval > currentTimeMillis) {
                // 500毫秒内重复验证成功, 绝对不可能
                log.warn("[验证码差异拦截器]在{}ms时间内，该IP:[{}]距离上次验证成功不足{}ms，触发风控，进行拦截", timeWindow, ipAddr, checkTimeInterval);
                return false;
            }
            // 校验同一时间内验证次数异常
            String checkSuccessNumStr = (String) cache.get("checkSuccessNum");
            if (StringUtils.isNotBlank(checkSuccessNumStr) && Integer.parseInt(checkSuccessNumStr) > checkSuccessNum) {
                // 闲的没事老滑验证码干什么
                log.warn("[验证码差异拦截器]在{}ms时间内，该IP:[{}]共校验成功{}次，触发风控，进行拦截", timeWindow, ipAddr, checkSuccessNumStr);
                return false;
            }
        }
        // 全都存成字符串，防止某些原因导致序列化错误
        Map<String, Object> map = new HashMap<>();
        map.put("std", String.valueOf(percentageStd));
        map.put("xSameQuantityPercentage", String.valueOf(features.get(13)));
        map.put("ySameQuantityPercentage", String.valueOf(features.get(14)));
        map.put("tSameQuantityPercentage", String.valueOf(features.get(15)));
        map.put("preCheckTime", String.valueOf(currentTimeMillis));
        String checkSuccessNum = (String) cache.getOrDefault("checkSuccessNum", String.valueOf(0));
        map.put("checkSuccessNum", String.valueOf(Integer.parseInt(checkSuccessNum) + 1));
        // 存储
        cacheStore.setCache(key, map, timeWindow, TimeUnit.MILLISECONDS);
        return true;
    }
}
