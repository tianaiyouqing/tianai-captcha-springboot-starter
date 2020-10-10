package cloud.tianai.captcha.slider;

import vip.tianai.ExpiringMap;
import vip.tianai.listener.ClearExpireEntityListener;
import vip.tianai.util.ExpiringMapUtils;

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
        ClearExpireEntityListener listener = ExpiringMapUtils.createClearExpireEntityListener(200, 1000L);
        // 构建一个带有过期key的本地缓存
        cache = ExpiringMapUtils.<String, Float>builder()
                .addListener(listener)
                .build();
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
        cache.put(id, xPercent, expire);
    }
}
