package cloud.tianai.captcha.aop;


import cloud.tianai.captcha.exception.CaptchaValidException;
import cloud.tianai.captcha.request.CaptchaRequest;
import cloud.tianai.captcha.slider.SliderCaptchaApplication;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: 天爱有情
 * @Date 2020/6/19 16:40
 * @Description 验证码拦截器
 */
@Slf4j
public class CaptchaInterceptor implements MethodInterceptor {
    private SliderCaptchaApplication captchaApplication;

    public CaptchaInterceptor(SliderCaptchaApplication captchaApplication) {
        this.captchaApplication = captchaApplication;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        CaptchaRequest captchaRequest = null;
        for (Object arg : arguments) {
            if (arg instanceof  CaptchaRequest) {
                captchaRequest = (CaptchaRequest) arg;
                break;
            }
        }
        if (captchaRequest == null) {
            log.warn("验证码验证 方法名称:{} 没有找到CaptchaRequest<?> 对象", invocation.getMethod().getName());
            return invocation.proceed();
        }

        String id = captchaRequest.getId();
        Float percentage = captchaRequest.getPercentage();
        if (StringUtils.isBlank(id) || percentage == null) {
            throw new CaptchaValidException("id 或者 percentage 不能为空");
        }

        boolean matching = captchaApplication.matching(id, percentage);
        if (matching) {
            return invocation.proceed();
        }

        throw new CaptchaValidException("验证失败");
    }


}
