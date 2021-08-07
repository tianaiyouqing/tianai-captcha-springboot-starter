package cloud.tianai.captcha.autoconfiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:41
 * @Description 滑块验证码属性
 */
@Data
@ConfigurationProperties(prefix = "captcha.slider")
public class SliderCaptchaProperties {
    /** 过期key prefix. */
    private String prefix = "captcha:slider";
    /** 过期时间. */
    private long expire = 60000;
    /** 缓存大小. */
    private Integer cacheSize = 20;
    /** 缓存拉取失败后等待时间. */
    private Integer waitTime = 1000;
    /** 缓存检查间隔. */
    private Integer period = 100;
    /** 是否初始化默认资源. */
    private Boolean initDefaultResource = true;
}