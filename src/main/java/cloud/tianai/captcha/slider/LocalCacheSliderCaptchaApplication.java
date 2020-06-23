package cloud.tianai.captcha.slider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:54
 * @Description 本地缓存户口验证码方案
 */
public class LocalCacheSliderCaptchaApplication extends AbstractSliderCaptchaApplication {

    private Map<String, Float> cache = new ConcurrentHashMap<>(255);

    @Override
    protected Float getPercentForCache(String id) {
        Float xPercent = cache.remove(id);
        return xPercent;
    }

    @Override
    protected void cacheVerification(String id, Float xPercent) {
        cache.remove(id);
        cache.put(id, xPercent);
    }
}
