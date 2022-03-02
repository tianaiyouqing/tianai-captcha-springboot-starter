package cloud.tianai.captcha.autoconfiguration;


import cloud.tianai.captcha.aop.CaptchaAdvisor;
import cloud.tianai.captcha.aop.CaptchaInterceptor;
import cloud.tianai.captcha.plugins.DynamicSliderCaptchaTemplate;
import cloud.tianai.captcha.plugins.secondary.SecondaryVerificationApplication;
import cloud.tianai.captcha.slider.DefaultSliderCaptchaApplication;
import cloud.tianai.captcha.slider.SliderCaptchaApplication;
import cloud.tianai.captcha.slider.store.CacheStore;
import cloud.tianai.captcha.slider.store.impl.LocalCacheStore;
import cloud.tianai.captcha.slider.store.impl.RedisCacheStore;
import cloud.tianai.captcha.template.slider.*;
import cloud.tianai.captcha.template.slider.validator.BasicCaptchaTrackValidator;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 9:49
 * @Description 滑块验证码自动装配
 */
@Order
@Configuration
@EnableConfigurationProperties({SliderCaptchaProperties.class, SecondaryVerificationProperties.class})
public class SliderCaptchaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ResourceStore resourceStore() {
        return new DefaultResourceStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public SliderCaptchaResourceManager sliderCaptchaResourceManager(ResourceStore resourceStore) {
        return new DefaultSliderCaptchaResourceManager(resourceStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public SliderCaptchaTemplate sliderCaptchaTemplate(SliderCaptchaProperties prop,
                                                       SliderCaptchaResourceManager captchaResourceManager) {
        // 增加缓存处理
        return new DynamicSliderCaptchaTemplate(prop, captchaResourceManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public SliderCaptchaValidator sliderCaptchaValidator() {
        return new BasicCaptchaTrackValidator();
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
    public SliderCaptchaApplication sliderCaptchaApplication(SliderCaptchaTemplate template,
                                                             SliderCaptchaValidator sliderCaptchaValidator,
                                                             CacheStore cacheStore,
                                                             SliderCaptchaProperties prop) {
        SliderCaptchaApplication target = new DefaultSliderCaptchaApplication(template, sliderCaptchaValidator, cacheStore, prop);
        if (Boolean.TRUE.equals(prop.getSecondary().getEnabled())) {
            target =  new SecondaryVerificationApplication(target, prop.getSecondary());
        }
        return target;
    }
    /**
     * @Author: 天爱有情
     * @date 2020/10/27 14:06
     * @Description RedisCacheSliderCaptchaApplication
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(StringRedisTemplate.class)
    @Import({RedisAutoConfiguration.class})
    @AutoConfigureAfter({RedisAutoConfiguration.class})
    public static class RedisCacheStoreConfiguration {

        @Bean
        @ConditionalOnBean(StringRedisTemplate.class)
        @ConditionalOnMissingBean(CacheStore.class)
        public CacheStore redis(StringRedisTemplate redisTemplate) {
            return new RedisCacheStore(redisTemplate);
        }
    }

    /**
     * @Author: 天爱有情
     * @date 2020/10/27 14:06
     * @Description LocalCacheSliderCaptchaApplication
     */

    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter({RedisCacheStoreConfiguration.class})
    @Import({RedisCacheStoreConfiguration.class})
    public static class LocalCacheStoreConfiguration {

        @Bean
        @ConditionalOnMissingBean(CacheStore.class)
        public CacheStore local() {
            return new LocalCacheStore();
        }
    }

}
