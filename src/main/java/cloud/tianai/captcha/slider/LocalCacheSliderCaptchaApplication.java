package cloud.tianai.captcha.slider;


import cloud.tianai.captcha.cache.ConCurrentExpiringMap;
import cloud.tianai.captcha.cache.ExpiringMap;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:54
 * @Description 本地缓存户口验证码方案
 */
public class LocalCacheSliderCaptchaApplication extends AbstractSliderCaptchaApplication {

    private ExpiringMap<String, Float> cache;


    private long expire;

    public LocalCacheSliderCaptchaApplication(long expire) {
        this.expire = expire;
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
