package cloud.tianai.captcha.readme;

import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class Test3 {
    @Autowired
    private ImageCaptchaApplication sca;

    @GetMapping("/check2")
    @ResponseBody
    public boolean check2Captcha(@RequestParam("id") String id) {
        // 如果开启了二次验证
        if (sca instanceof SecondaryVerificationApplication) {
            return ((SecondaryVerificationApplication) sca).secondaryVerification(id);
        }
        return false;
    }
}
