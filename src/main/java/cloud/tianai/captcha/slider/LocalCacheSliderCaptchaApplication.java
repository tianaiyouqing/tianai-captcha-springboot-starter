package cloud.tianai.captcha.slider;


import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.cache.ConCurrentExpiringMap;
import cloud.tianai.captcha.cache.ExpiringMap;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:54
 * @Description 本地缓存户口验证码方案
 */
public class LocalCacheSliderCaptchaApplication extends AbstractSliderCaptchaApplication {

    private ExpiringMap<String, Map<String, Object>> cache;


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
    protected Map<String, Object> getVerification(String id) {
        Map<String, Object> validData = cache.remove(id);
        return validData;
    }

    @Override
    protected void cacheVerification(String id, Map<String, Object> validData) {
        cache.remove(id);
        cache.put(id, validData, expire, TimeUnit.MILLISECONDS);
    }
}
