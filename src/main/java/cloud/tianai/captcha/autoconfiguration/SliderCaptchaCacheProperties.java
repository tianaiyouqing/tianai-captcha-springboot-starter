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
    /** 其中webp占用缓存大小比例为 70%(一般用谷歌内核的用户多). */
    private Integer webpCacheSize;
    /** 缓存拉取失败后等待时间. */
    private Integer waitTime = 1000;
    /** 缓存检查间隔. */
    private Integer period = 100;

    /**
     * 其中webp占用缓存大小比例为 70%(一般用谷歌内核的用户多).
     *
     * @return default cacheSize*0.7
     */
    public Integer getWebpCacheSize() {
        if (webpCacheSize == null && cacheSize != null) {
            webpCacheSize = (int) (cacheSize * 0.7);
        }
        return webpCacheSize;
    }
}