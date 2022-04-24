package cloud.tianai.captcha.autoconfiguration;

import lombok.Data;

/**
 * @Author: 天爱有情
 * @date 2020/10/19 18:41
 * @Description 滑块验证码缓存属性
 */
@Data
public class SliderCaptchaCacheProperties {
    private Boolean enabled;
    /** 缓存大小. */
    private Integer cacheSize = 20;
    /** 缓存拉取失败后等待时间. */
    private Integer waitTime = 1000;
    /** 缓存检查间隔. */
    private Integer period = 100;
}