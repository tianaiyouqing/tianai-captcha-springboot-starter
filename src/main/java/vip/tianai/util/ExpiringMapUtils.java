package vip.tianai.util;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import vip.tianai.ConCurrentExpiringMap;
import vip.tianai.ExpiringMap;
import vip.tianai.listener.ClearExpireEntityListener;

/**
 * @Auther: 天爱有情
 * @Date: 2019/9/11 11:23
 * @Description: 过期map键值对
 */
public class ExpiringMapUtils {

    public static <K,V> ExpiringMap<K,V> create() {
        ClearExpireEntityListener clearExpireEntityListener = createClearExpireEntityListener(20, 200L);
        ExpiringMap<K,V> build = ExpiringMapUtils.<K, V>builder()
                .addListener(clearExpireEntityListener)
                .build();
        build.init();
        return build;
    }

    public static ClearExpireEntityListener createClearExpireEntityListener(Integer maxReadExporeKeys, Long intervalTime) {
        ClearExpireEntityListener clearExpireEntityListener = new ClearExpireEntityListener();
        clearExpireEntityListener.setMaxReadExpireKeys(maxReadExporeKeys);
        clearExpireEntityListener.setIntervalTime(intervalTime);
        clearExpireEntityListener.init();
        return clearExpireEntityListener;
    }


    public static <K,V> ExpireEntityBuilder<K,V> builder() {
        return new ExpireEntityBuilder<K,V>();
    }

    public static class ExpireEntityBuilder<K,V> {
        SimpleApplicationEventMulticaster eventPublisher = new SimpleApplicationEventMulticaster();

        public ExpireEntityBuilder addListener(ApplicationListener<?> applicationListener) {
            eventPublisher.addApplicationListener(applicationListener);
            return this;
        }

        public ExpiringMap<K,V> build() {
            ConCurrentExpiringMap<K,V> conCurrentExpiringMap = new ConCurrentExpiringMap<K,V>();
            conCurrentExpiringMap.setApplicationEventPublisher(eventPublisher);
            return conCurrentExpiringMap;
        }
    }
}
