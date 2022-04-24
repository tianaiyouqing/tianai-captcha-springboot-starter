package cloud.tianai.captcha.plugins;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaCacheProperties;
import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.template.slider.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.generator.impl.CacheImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
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
@Getter
@Setter
public class DynamicSliderCaptchaGenerator implements ImageCaptchaGenerator, ApplicationListener<ApplicationReadyEvent> {

    protected SliderCaptchaProperties prop;
    protected ImageCaptchaResourceManager captchaResourceManager;

    protected CacheImageCaptchaGenerator cacheImageCaptchaGenerator;
    protected ImageCaptchaGenerator imageCaptchaGenerator;

    protected boolean webApplication;
    @Getter
    @Setter
    protected String captchaTypeKey = "captcha-type";

    public DynamicSliderCaptchaGenerator(SliderCaptchaProperties prop,
                                         ImageCaptchaResourceManager captchaResourceManager) {
        this.prop = prop;
        this.captchaResourceManager = captchaResourceManager;
        imageCaptchaGenerator = new MultiImageCaptchaGenerator(captchaResourceManager, prop.getInitDefaultResource());
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
        Integer cacheSize = cacheProp.getCacheSize();
        if (cacheSize != null && cacheSize > 0) {
            cacheImageCaptchaGenerator = new CacheImageCaptchaGenerator(imageCaptchaGenerator, cacheSize, cacheProp.getWaitTime(), cacheProp.getPeriod());
            cacheImageCaptchaGenerator.setRequiredGetCaptcha(false);
            cacheImageCaptchaGenerator.initSchedule();
        }
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(String t) {
        // 判断是ie内核还是谷歌内核
        GenerateParam generateParam = new GenerateParam();
        generateParam.setType(t);
        if (webApplication) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String type = getImageTypeByRequest(request);
                if ("webp".equalsIgnoreCase(type)) {
                    generateParam.setBackgroundFormatName("webp");
                    generateParam.setSliderFormatName("webp");
                }
            }
        }
        // 如果不是web应用，默认读标准图片
        return requiredGetSliderCaptchaInfo(generateParam);
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
    public ImageCaptchaInfo generateCaptchaImage(String t, String backgroundFormatName, String sliderFormatName) {
        GenerateParam generateParam = GenerateParam.builder()
                .type(t)
                .backgroundFormatName(backgroundFormatName)
                .sliderFormatName(sliderFormatName)
                .build();
        return requiredGetSliderCaptchaInfo(generateParam);
    }

    @Override
    public ImageCaptchaInfo generateCaptchaImage(GenerateParam param) {
        if (prop.getObfuscate().equals(param.getObfuscate())) {
            return generateCaptchaImage(param.getType(), param.getBackgroundFormatName(), param.getSliderFormatName());
        }
        return imageCaptchaGenerator.generateCaptchaImage(param);
    }


    @Override
    public ImageCaptchaResourceManager getImageResourceManager() {
        return captchaResourceManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initCache();
    }

    public ImageCaptchaInfo requiredGetSliderCaptchaInfo(GenerateParam param) {
        ImageCaptchaInfo sliderCaptchaInfo = null;
        if (cacheImageCaptchaGenerator != null) {
            sliderCaptchaInfo = cacheImageCaptchaGenerator.generateCaptchaImage(param, false);
        }
        if (sliderCaptchaInfo == null) {
            sliderCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(param);
        }
        return sliderCaptchaInfo;
    }
}
