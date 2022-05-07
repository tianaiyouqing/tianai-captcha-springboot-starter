package cloud.tianai.captcha.spring.store.impl;

import cloud.tianai.captcha.spring.cache.ConCurrentExpiringMap;
import cloud.tianai.captcha.spring.cache.ExpiringMap;
import cloud.tianai.captcha.spring.store.CacheStore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:39
 * @Description 本地缓存
 */
public class LocalCacheStore implements CacheStore {

    protected ExpiringMap<String, Map<String, Object>> cache;

    public LocalCacheStore() {
        cache = new ConCurrentExpiringMap<>();
        cache.init();
    }

    @Override
    public Map<String, Object> getCache(String key) {
        return cache.get(key);
    }

    @Override
    public Map<String, Object> getAndRemoveCache(String key) {
        return cache.remove(key);
    }

    @Override
    public boolean setCache(String key, Map<String, Object> data, Long expire, TimeUnit timeUnit) {
        cache.remove(key);
        cache.put(key, data, expire, timeUnit);
        return true;
    }
}
