package cloud.tianai.captcha.autoconfiguration;

import cloud.tianai.captcha.template.slider.generator.SliderCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.impl.CacheSliderCaptchaGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Author: 天爱有情
 * @date 2020/10/20 10:16
 * @Description 缓存验证码监听器， 主要做项目启动完成后初始化缓存调度器
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class CacheCaptchaTemplateListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        try {
            SliderCaptchaGenerator bean = applicationContext.getBean(SliderCaptchaGenerator.class);
            if (bean instanceof CacheSliderCaptchaGenerator) {
                CacheSliderCaptchaGenerator cacheSliderCaptchaTemplate = (CacheSliderCaptchaGenerator) bean;
                // 初始化调度器
                cacheSliderCaptchaTemplate.initSchedule();
            }
        } catch (BeansException e) {
            log.debug("CaptchaTemplateListener 获取 SliderCaptchaTemplate 失败");
        }
    }
}
