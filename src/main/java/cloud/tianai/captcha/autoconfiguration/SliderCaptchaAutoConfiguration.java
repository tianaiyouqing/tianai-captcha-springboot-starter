package cloud.tianai.captcha.autoconfiguration;


import cloud.tianai.captcha.aop.CaptchaAdvisor;
import cloud.tianai.captcha.aop.CaptchaInterceptor;
import cloud.tianai.captcha.slider.LocalCacheSliderCaptchaApplication;
import cloud.tianai.captcha.slider.RedisCacheSliderCaptchaApplication;
import cloud.tianai.captcha.slider.SliderCaptchaApplication;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@EnableConfigurationProperties(SliderCaptchaAutoConfiguration.RedisSliderCaptchaProperties.class)
public class SliderCaptchaAutoConfiguration {

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    @ConditionalOnMissingBean
    public SliderCaptchaApplication redis(StringRedisTemplate redisTemplate, RedisSliderCaptchaProperties properties) {
        return new RedisCacheSliderCaptchaApplication(redisTemplate, properties.getPrefix(), properties.getExpire());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass("org.springframework.data.redis.core.StringRedisTemplate")
    public SliderCaptchaApplication local() {
        return new LocalCacheSliderCaptchaApplication();
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

    @Data
    @ConfigurationProperties(prefix = "captcha.slider")
    public class RedisSliderCaptchaProperties {
        private String prefix = "captcha:slider";
        private long expire = 60000;
    }
}
