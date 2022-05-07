package cloud.tianai.captcha.spring.annotation;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author: 天爱有情
 * @Date 2020/6/19 16:28
 * @Description 验证码注解
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface Captcha {

    @AliasFor("type")
    String value();

    @AliasFor("value")
    String type() default CaptchaTypeConstant.SLIDER;
}
