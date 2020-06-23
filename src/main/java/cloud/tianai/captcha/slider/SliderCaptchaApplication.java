package cloud.tianai.captcha.slider;


import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:33
 * @Description 滑块验证码应用程序
 */
public interface SliderCaptchaApplication {

    /**
     * 生成滑块验证码
     * @return
     */
    CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha();

    /**
     * 生成webp滑块验证码
     * @return
     */
    CaptchaResponse<SliderCaptchaVO> generateSliderCaptchaForWebp();

    /**
     * 匹配
     * @param id 验证码的ID
     * @param percentage 百分比
     * @return 匹配成功返回true， 否则返回false
     */
    boolean matching(String id, Float percentage);
}
