package cloud.tianai.captcha.autoconfiguration;


import cloud.tianai.captcha.aop.CaptchaAdvisor;
import cloud.tianai.captcha.aop.CaptchaInterceptor;
import cloud.tianai.captcha.slider.LocalCacheSliderCaptchaApplication;
import cloud.tianai.captcha.slider.RedisCacheSliderCaptchaApplication;
import cloud.tianai.captcha.slider.SliderCaptchaApplication;
import cloud.tianai.captcha.template.slider.CacheSliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.DefaultSliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 9:49
 * @Description 滑块验证码自动装配
 */
@Order
@Configuration
@EnableConfigurationProperties(SliderCaptchaProperties.class)
public class SliderCaptchaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SliderCaptchaTemplate sliderCaptchaTemplate(SliderCaptchaProperties prop) {
        SliderCaptchaTemplate template = new DefaultSliderCaptchaTemplate(prop.getTargetFormatName(), prop.getMatrixFormatName(), prop.getInitDefaultResource());
        // 增加缓存处理
        return new CacheSliderCaptchaTemplate(template, prop.getCacheSize());
    }

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    @ConditionalOnMissingBean
    public SliderCaptchaApplication redis(StringRedisTemplate redisTemplate, SliderCaptchaTemplate template, SliderCaptchaProperties properties) {
        return new RedisCacheSliderCaptchaApplication(redisTemplate, template, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("org.springframework.data.redis.core.StringRedisTemplate")
    public SliderCaptchaApplication local(SliderCaptchaTemplate template, SliderCaptchaProperties properties) {
        return new LocalCacheSliderCaptchaApplication(template, properties);
    }


    @Bean
    @ConditionalOnMissingBean
    public CaptchaInterceptor captchaInterceptor(SliderCaptchaApplication application) {
        return new CaptchaInterceptor(application);
    }

    @Bean
    @ConditionalOnMissingBean
    public CaptchaAdvisor captchaAdvisor(CaptchaInterceptor interceptor) {
        return new CaptchaAdvisor(interceptor);
    }


}
