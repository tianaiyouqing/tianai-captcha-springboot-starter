package cloud.tianai.captcha.aop;


import cloud.tianai.captcha.exception.CaptchaValidException;
import cloud.tianai.captcha.request.CaptchaRequest;
import cloud.tianai.captcha.slider.SliderCaptchaApplication;
import cloud.tianai.captcha.template.slider.validator.common.model.dto.SliderCaptchaTrack;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @Author: 天爱有情
 * @Date 2020/6/19 16:40
 * @Description 验证码拦截器
 */
@Slf4j
public class CaptchaInterceptor implements MethodInterceptor, BeanFactoryAware {
    private SliderCaptchaApplication captchaApplication;
    private BeanFactory beanFactory;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        CaptchaRequest captchaRequest = null;
        for (Object arg : arguments) {
            if (arg instanceof CaptchaRequest) {
                captchaRequest = (CaptchaRequest) arg;
                break;
            }
        }
        if (captchaRequest == null) {
            log.warn("验证码验证 方法名称:{} 没有找到CaptchaRequest<?> 对象", invocation.getMethod().getName());
            return invocation.proceed();
        }

        String id = captchaRequest.getId();
        SliderCaptchaTrack sliderCaptchaTrack = captchaRequest.getSliderCaptchaTrack();
        if (sliderCaptchaTrack == null) {
            throw new CaptchaValidException("sliderCaptchaTrack 不能为空");
        }

        boolean matching = getSliderCaptchaApplication().matching(id, sliderCaptchaTrack);
        if (matching) {
            return invocation.proceed();
        }

        throw new CaptchaValidException("验证失败");
    }

    public SliderCaptchaApplication getSliderCaptchaApplication() {
        if (captchaApplication == null) {
            this.captchaApplication = beanFactory.getBean(SliderCaptchaApplication.class);
        }
        return captchaApplication;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
