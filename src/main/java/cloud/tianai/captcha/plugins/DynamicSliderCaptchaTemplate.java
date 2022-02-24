package cloud.tianai.captcha.plugins;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.slider.CaptchaImageType;
import cloud.tianai.captcha.template.slider.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 天爱有情
 * @date 2022/2/24 14:58
 * @Description 根据浏览器内核判断返回 webp类型还是jpg+png类型的验证码
 */
@Slf4j
public class DynamicSliderCaptchaTemplate implements SliderCaptchaTemplate, ApplicationListener<ApplicationReadyEvent> {

    protected SliderCaptchaProperties prop;
    protected SliderCaptchaResourceManager captchaResourceManager;

    protected CacheSliderCaptchaTemplate webpCacheCaptchaTemplate;
    protected CacheSliderCaptchaTemplate standardCacheCaptchaTemplate;
    protected SliderCaptchaTemplate captchaTemplate;

    protected GenerateParam standardGenerateParam;
    protected GenerateParam webpGenerateParam;
    protected boolean webApplication;
    @Getter
    @Setter
    protected String captchaTypeKey = "captcha-type";

    public DynamicSliderCaptchaTemplate(SliderCaptchaProperties prop,
                                        SliderCaptchaResourceManager captchaResourceManager) {
        this.prop = prop;
        this.captchaResourceManager = captchaResourceManager;
        captchaTemplate = new StandardSliderCaptchaTemplate(captchaResourceManager, prop.getInitDefaultResource());
        standardGenerateParam = GenerateParam.builder()
                .backgroundFormatName("jpeg")
                .sliderFormatName("png")
                .obfuscate(prop.getObfuscate())
                .build();
        webpGenerateParam = GenerateParam.builder()
                .backgroundFormatName("webp")
                .sliderFormatName("webp")
                .obfuscate(prop.getObfuscate())
                .build();
        // 判断是否是web应用
        webApplication = isWebApplication();
    }

    private boolean isWebApplication() {
        return ClassUtils.isPresent("javax.servlet.http.HttpServletRequest", this.getClass().getClassLoader())
                && ClassUtils.isPresent("org.springframework.web.context.request.RequestContextHolder", this.getClass().getClassLoader());

    }

    public void initCache() {
        Integer allCacheSize = prop.getCacheSize();
        int webpCacheSize = prop.getWebpCacheSize();
        if (webpCacheSize > 0) {
            webpCacheCaptchaTemplate = new CacheSliderCaptchaTemplate(captchaTemplate, webpGenerateParam, webpCacheSize, prop.getWaitTime(), prop.getPeriod());
            webpCacheCaptchaTemplate.setRequiredGetCaptcha(false);
            webpCacheCaptchaTemplate.initSchedule();
        }
        int ordinaryCacheSize = allCacheSize - webpCacheSize;
        standardCacheCaptchaTemplate = new CacheSliderCaptchaTemplate(captchaTemplate,
                standardGenerateParam, ordinaryCacheSize, prop.getWaitTime(), prop.getPeriod());
        standardCacheCaptchaTemplate.setRequiredGetCaptcha(false);
        standardCacheCaptchaTemplate.initSchedule();
    }

    @Override
    public SliderCaptchaInfo getSlideImageInfo() {
        // 判断是ie内核还是谷歌内核
        if (webApplication) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String type = getImageTypeByRequest(request);
                if ("webp".equalsIgnoreCase(type)) {
                    return requiredGetSliderCaptchaInfo(true, true);
                }
            }
        }
        // 如果不是web应用，默认读标准图片
        return requiredGetSliderCaptchaInfo(false, true);
    }

    public String getImageTypeByRequest(HttpServletRequest request) {
        String type = request.getParameter(captchaTypeKey);
        if (StringUtils.isBlank(type)) {
            type = request.getHeader(captchaTypeKey);
            if (StringUtils.isBlank(type)) {
                String userAgent = request.getHeader("user-agent");
                if (StringUtils.isNotBlank(userAgent) && userAgent.contains("Chrome")) {
                    type = "webp";
                }else {
                    type = "jpg-png";
                }
            }
        }
        return type;
    }

    @Override
    public SliderCaptchaInfo getSlideImageInfo(String backgroundFormatName, String sliderFormatName) {
        CaptchaImageType type = CaptchaImageType.getType(backgroundFormatName, sliderFormatName);
        if (CaptchaImageType.WEBP.equals(CaptchaImageType.getType(backgroundFormatName, sliderFormatName))) {
            return requiredGetSliderCaptchaInfo(true, false);
        } else if (CaptchaImageType.JPEG_PNG.equals(type)) {
            return requiredGetSliderCaptchaInfo(false, true);
        }
        return captchaTemplate.getSlideImageInfo(backgroundFormatName, sliderFormatName);
    }

    @Override
    public SliderCaptchaInfo getSlideImageInfo(GenerateParam param) {
        if (prop.getObfuscate().equals(param.getObfuscate())) {
            return getSlideImageInfo(param.getBackgroundFormatName(), param.getSliderFormatName());
        }
        return captchaTemplate.getSlideImageInfo(param);
    }

    @Override
    public boolean percentageContrast(Float newPercentage, Float oriPercentage) {
        return captchaTemplate.percentageContrast(newPercentage, oriPercentage);
    }

    @Override
    public SliderCaptchaResourceManager getSlideImageResourceManager() {
        return captchaResourceManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initCache();
    }

    public SliderCaptchaInfo requiredGetSliderCaptchaInfo(boolean tryWebpCacheRead, boolean tryStandardCacheRead) {
        SliderCaptchaInfo sliderCaptchaInfo = null;
        if (tryWebpCacheRead && webpCacheCaptchaTemplate != null) {
            sliderCaptchaInfo = webpCacheCaptchaTemplate.getSlideImageInfo();
        }
        if (sliderCaptchaInfo == null && standardCacheCaptchaTemplate != null && tryStandardCacheRead) {
            sliderCaptchaInfo = standardCacheCaptchaTemplate.getSlideImageInfo();
        }
        if (sliderCaptchaInfo == null && captchaTemplate != null) {
            log.warn("滑块验证码缓存不足, 读取资源类型为 webp:{}, ordinary:{}", tryWebpCacheRead, tryStandardCacheRead);
            sliderCaptchaInfo = captchaTemplate.getSlideImageInfo(standardGenerateParam);
        }
        return sliderCaptchaInfo;
    }
}
