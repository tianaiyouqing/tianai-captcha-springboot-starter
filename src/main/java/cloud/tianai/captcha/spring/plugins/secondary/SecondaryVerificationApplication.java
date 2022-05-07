package cloud.tianai.captcha.spring.plugins.secondary;

import cloud.tianai.captcha.spring.autoconfiguration.SecondaryVerificationProperties;
import cloud.tianai.captcha.spring.application.FilterImageCaptchaApplication;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:16
 * @Description 二次验证
 */
public class SecondaryVerificationApplication extends FilterImageCaptchaApplication {
    private SecondaryVerificationProperties prop;

    public SecondaryVerificationApplication(ImageCaptchaApplication target, SecondaryVerificationProperties prop) {
        super(target);
        this.prop = prop;
    }

    @Override
    public boolean matching(String id, ImageCaptchaTrack imageCaptchaTrack) {
        boolean match = super.matching(id, imageCaptchaTrack);
        if (match) {
            // 如果匹配成功， 添加二次验证记录
            addSecondaryVerification(id, imageCaptchaTrack);
        }
        return match;
    }

    /**
     * 二次缓存验证
     * @param id id
     * @return boolean
     */
    public boolean secondaryVerification(String id) {
        Map<String, Object> cache = target.getCacheStore().getAndRemoveCache(getKey(id));
        return cache != null;
    }

    /**
     * 添加二次缓存验证记录
     * @param id id
     * @param imageCaptchaTrack sliderCaptchaTrack
     */
    protected void addSecondaryVerification(String id, ImageCaptchaTrack imageCaptchaTrack) {
        target.getCacheStore().setCache(getKey(id), Collections.emptyMap(), prop.getExpire(), TimeUnit.MILLISECONDS);
    }

    protected String getKey(String id) {
        return prop.getKeyPrefix().concat(":").concat(id);
    }
}
