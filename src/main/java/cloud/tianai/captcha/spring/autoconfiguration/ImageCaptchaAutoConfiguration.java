package cloud.tianai.captcha.spring.autoconfiguration;


import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.impl.CacheImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultResourceStore;
import cloud.tianai.captcha.spring.aop.CaptchaAdvisor;
import cloud.tianai.captcha.spring.aop.CaptchaInterceptor;
import cloud.tianai.captcha.spring.application.DefaultImageCaptchaApplication;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.SpringMultiImageCaptchaGenerator;
import cloud.tianai.captcha.spring.plugins.chain.validators.DifferenceChainValidator;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cloud.tianai.captcha.spring.store.CacheStore;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;
import cloud.tianai.captcha.validator.impl.chain.ChainImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.chain.validators.LRChainValidator;
import cloud.tianai.captcha.validator.impl.chain.validators.ParamCheckChainValidator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 9:49
 * @Description 滑块验证码自动装配
 */
@Order
@Configuration
@AutoConfigureAfter(CacheStoreAutoConfiguration.class)
@EnableConfigurationProperties({ImageCaptchaProperties.class})
public class ImageCaptchaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ResourceStore resourceStore() {
        return new DefaultResourceStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaResourceManager imageCaptchaResourceManager(ResourceStore resourceStore) {
        return new DefaultImageCaptchaResourceManager(resourceStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageTransform imageTransform() {
        return new Base64ImageTransform();
    }


    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaGenerator imageCaptchaTemplate(ImageCaptchaProperties prop,
                                                      ImageCaptchaResourceManager captchaResourceManager,
                                                      ImageTransform imageTransform,
                                                      BeanFactory beanFactory) {
        // 构建多验证码生成器
        ImageCaptchaGenerator captchaGenerator = new SpringMultiImageCaptchaGenerator(captchaResourceManager, imageTransform, beanFactory);
        SliderCaptchaCacheProperties cache = prop.getCache();
        if (cache != null && Boolean.TRUE.equals(cache.getEnabled()) && cache.getCacheSize() > 0) {
            // 增加缓存处理
            captchaGenerator = new CacheImageCaptchaGenerator(captchaGenerator, cache.getCacheSize(), cache.getWaitTime(), cache.getPeriod());
        }
        // 初始化
        captchaGenerator.init(prop.getInitDefaultResource());
        return captchaGenerator;
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaValidator imageCaptchaValidator() {
        // 默认使用 LR 校验
        ChainImageCaptchaValidator imageCaptchaValidator = new ChainImageCaptchaValidator();
        imageCaptchaValidator.addValidator(new ParamCheckChainValidator());
        imageCaptchaValidator.addValidator(new LRChainValidator());
        return imageCaptchaValidator;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public CaptchaInterceptor captchaInterceptor() {
        return new CaptchaInterceptor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public CaptchaAdvisor captchaAdvisor(CaptchaInterceptor interceptor) {
        return new CaptchaAdvisor(interceptor);
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public CacheCaptchaTemplateListener captchaTemplateListener() {
        return new CacheCaptchaTemplateListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaApplication imageCaptchaApplication(ImageCaptchaGenerator captchaGenerator,
                                                           ImageCaptchaValidator imageCaptchaValidator,
                                                           CacheStore cacheStore,
                                                           ImageCaptchaProperties prop) {
        ImageCaptchaApplication target = new DefaultImageCaptchaApplication(captchaGenerator, imageCaptchaValidator, cacheStore, prop);
        if (prop.getSecondary() != null && Boolean.TRUE.equals(prop.getSecondary().getEnabled())) {
            target = new SecondaryVerificationApplication(target, prop.getSecondary());
        }
        return target;
    }

}
