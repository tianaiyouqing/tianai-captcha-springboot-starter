package vip.tianai.spring.boot;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import vip.tianai.ConCurrentExpiringMap;
import vip.tianai.ExpiringMap;
import vip.tianai.listener.ClearExpireEntityListener;


/**
 * @Auther: 天爱有情
 * @Date: 2019/9/3 10:59
 * @Description: ExpiringMap 自动配置类
 */
@Data
@Configuration
@ConditionalOnProperty(name = "expiring-map.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(ExpiringMapProperties.class)
public class ExpiringMapAutoConfiguration {

    private ExpiringMapProperties expiringMapProperties;

    public ExpiringMapAutoConfiguration(ExpiringMapProperties expiringMapProperties) {
        this.expiringMapProperties = expiringMapProperties;
    }

    @Bean(initMethod = "init")
    public ExpiringMap timeMap(SimpleApplicationEventMulticaster applicationEventMulticaster) {
        ExpiringMap<String, Object> expiringMap = new ConCurrentExpiringMap<>(applicationEventMulticaster);
        return expiringMap;
    }

    @Bean(initMethod = "init")
    public ClearExpireEntityListener clearExpireEntityListener() {
        ClearExpireEntityListener entityListener = new ClearExpireEntityListener();
        entityListener.setIntervalTime(expiringMapProperties.getIntervalTime());
        entityListener.setMaxReadExpireKeys(expiringMapProperties.getMaxReadExpireKeys());
        entityListener.setMaxReadExpireTimeout(expiringMapProperties.getMaxReadExpireTimeout());
        return entityListener;
    }
}
