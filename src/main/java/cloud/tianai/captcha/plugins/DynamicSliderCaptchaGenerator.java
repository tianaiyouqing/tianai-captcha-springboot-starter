package cloud.tianai.captcha.plugins;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaCacheProperties;
import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.slider.CaptchaImageType;
import cloud.tianai.captcha.template.slider.generator.SliderCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.SliderCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.impl.CacheSliderCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.impl.StandardSliderCaptchaGenerator;
import cloud.tianai.captcha.template.slider.resource.SliderCaptchaResourceManager;
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
public class DynamicSliderCaptchaGenerator implements SliderCaptchaGenerator, ApplicationListener<ApplicationReadyEvent> {

    protected SliderCaptchaProperties prop;
    protected SliderCaptchaResourceManager captchaResourceManager;

    protected CacheSliderCaptchaGenerator webpCacheCaptchaTemplate;
    protected CacheSliderCaptchaGenerator standardCacheCaptchaTemplate;
    protected SliderCaptchaGenerator captchaTemplate;

    protected GenerateParam standardGenerateParam;
    protected GenerateParam webpGenerateParam;
    protected boolean webApplication;
    @Getter
    @Setter
    protected String captchaTypeKey = "captcha-type";

    public DynamicSliderCaptchaGenerator(SliderCaptchaProperties prop,
                                         SliderCaptchaResourceManager captchaResourceManager) {
        this.prop = prop;
        this.captchaResourceManager = captchaResourceManager;
        captchaTemplate = new StandardSliderCaptchaGenerator(captchaResourceManager, prop.getInitDefaultResource());
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
        SliderCaptchaCacheProperties cacheProp = prop.getCache();
        if (cacheProp == null || !Boolean.TRUE.equals(cacheProp.getEnabled())) {
            return;
        }
        Integer allCacheSize = cacheProp.getCacheSize();
        int webpCacheSize = cacheProp.getWebpCacheSize();
        if (webpCacheSize > 0) {
            webpCacheCaptchaTemplate = new CacheSliderCaptchaGenerator(captchaTemplate, webpGenerateParam, webpCacheSize, cacheProp.getWaitTime(), cacheProp.getPeriod());
            webpCacheCaptchaTemplate.setRequiredGetCaptcha(false);
            webpCacheCaptchaTemplate.initSchedule();
        }
        int ordinaryCacheSize = allCacheSize - webpCacheSize;
        standardCacheCaptchaTemplate = new CacheSliderCaptchaGenerator(captchaTemplate,
                standardGenerateParam, ordinaryCacheSize, cacheProp.getWaitTime(), cacheProp.getPeriod());
        standardCacheCaptchaTemplate.setRequiredGetCaptcha(false);
        standardCacheCaptchaTemplate.initSchedule();
    }

    @Override
    public SliderCaptchaInfo generateSlideImageInfo() {
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
                } else {
                    type = "jpg-png";
                }
            }
        }
        return type;
    }

    @Override
    public SliderCaptchaInfo generateSlideImageInfo(String backgroundFormatName, String sliderFormatName) {
        CaptchaImageType type = CaptchaImageType.getType(backgroundFormatName, sliderFormatName);
        if (CaptchaImageType.WEBP.equals(CaptchaImageType.getType(backgroundFormatName, sliderFormatName))) {
            return requiredGetSliderCaptchaInfo(true, false);
        } else if (CaptchaImageType.JPEG_PNG.equals(type)) {
            return requiredGetSliderCaptchaInfo(false, true);
        }
        return captchaTemplate.generateSlideImageInfo(backgroundFormatName, sliderFormatName);
    }

    @Override
    public SliderCaptchaInfo generateSlideImageInfo(GenerateParam param) {
        if (prop.getObfuscate().equals(param.getObfuscate())) {
            return generateSlideImageInfo(param.getBackgroundFormatName(), param.getSliderFormatName());
        }
        return captchaTemplate.generateSlideImageInfo(param);
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
            sliderCaptchaInfo = webpCacheCaptchaTemplate.generateSlideImageInfo();
        }
        if (sliderCaptchaInfo == null && standardCacheCaptchaTemplate != null && tryStandardCacheRead) {
            sliderCaptchaInfo = standardCacheCaptchaTemplate.generateSlideImageInfo();
        }
        if (sliderCaptchaInfo == null && captchaTemplate != null) {
            GenerateParam generateParam;
            if (tryWebpCacheRead) {
                generateParam = webpGenerateParam;
            }else if (tryStandardCacheRead) {
                generateParam = standardGenerateParam;
            }else {
                generateParam = standardGenerateParam;
            }
            sliderCaptchaInfo = captchaTemplate.generateSlideImageInfo(generateParam);
        }
        return sliderCaptchaInfo;
    }
}
