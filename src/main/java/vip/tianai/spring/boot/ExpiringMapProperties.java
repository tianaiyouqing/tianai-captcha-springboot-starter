package vip.tianai.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "expiring-map")
public class ExpiringMapProperties {

    /** 每次最大读取的key的数量. */
    private Integer maxReadExpireKeys = 20;
    /** 最大读取超时时间. */
    private Long maxReadExpireTimeout = 200L;
    /** 每次读取的间隔时间. */
    private Long intervalTime = 200L;
}
