package cloud.tianai.captcha.slider;


import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.cache.ConCurrentExpiringMap;
import cloud.tianai.captcha.cache.ExpiringMap;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:54
 * @Description 本地缓存户口验证码方案
 */
public class LocalCacheSliderCaptchaApplication extends AbstractSliderCaptchaApplication {

    private ExpiringMap<String, Float> cache;


    private long expire;

    public LocalCacheSliderCaptchaApplication(SliderCaptchaTemplate template,
                                              SliderCaptchaValidator sliderCaptchaValidator,
                                              SliderCaptchaProperties prop) {
        super(template, sliderCaptchaValidator,prop);
        this.expire = prop.getExpire();
        cache = new ConCurrentExpiringMap<>();
        cache.init();
    }


    @Override
    protected Float getPercentForCache(String id) {
        Float xPercent = cache.remove(id);
        return xPercent;
    }

    @Override
    protected void cacheVerification(String id, Float xPercent) {
        cache.remove(id);
        cache.put(id, xPercent, expire, TimeUnit.MILLISECONDS);
    }
}
