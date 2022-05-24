package cloud.tianai.captcha.readme;

import cloud.tianai.captcha.spring.annotation.Captcha;
import cloud.tianai.captcha.spring.request.CaptchaRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Test {
    // 只需要在需要验证的controller层加入 @Captcha 注解，
    // 并且接受的参数指定成CaptchaRequest即可自动进行校验
    // 自己真实的参数可以写到 CaptchaRequest对象的泛型中
    // 如果校验失败，会抛出CaptchaValidException异常
    // 对校验失败的处理，可以使用sping的全局异常拦截CaptchaValidException异常进行处理

    @Captcha("SLIDER")
    @PostMapping("/login")
    public String login(@RequestBody CaptchaRequest<Map> request) {
        // 进入这个方法就说明已经校验成功了
        return "success";
    }
}
