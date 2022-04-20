package cloud.tianai.captcha.plugins.secondary;

import cloud.tianai.captcha.autoconfiguration.SecondaryVerificationProperties;
import cloud.tianai.captcha.slider.FilterSliderCaptchaApplication;
import cloud.tianai.captcha.slider.SliderCaptchaApplication;
import cloud.tianai.captcha.template.slider.validator.common.model.dto.SliderCaptchaTrack;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:16
 * @Description 二次验证
 */
public class SecondaryVerificationApplication extends FilterSliderCaptchaApplication {
    private SecondaryVerificationProperties prop;

    public SecondaryVerificationApplication(SliderCaptchaApplication target, SecondaryVerificationProperties prop) {
        super(target);
        this.prop = prop;
    }

    @Override
    public boolean matching(String id, SliderCaptchaTrack sliderCaptchaTrack) {
        boolean match = super.matching(id, sliderCaptchaTrack);
        if (match) {
            // 如果匹配成功， 添加二次验证记录
            addSecondaryVerification(id, sliderCaptchaTrack);
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
     * @param sliderCaptchaTrack sliderCaptchaTrack
     */
    protected void addSecondaryVerification(String id, SliderCaptchaTrack sliderCaptchaTrack) {
        target.getCacheStore().setCache(getKey(id), Collections.emptyMap(), prop.getExpire(), TimeUnit.MILLISECONDS);
    }

    protected String getKey(String id) {
        return prop.getKeyPrefix().concat(":").concat(id);
    }
}
