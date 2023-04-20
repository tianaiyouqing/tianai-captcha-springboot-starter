package cloud.tianai.captcha.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.application.CaptchaImageType;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import org.springframework.beans.factory.annotation.Autowired;

public class Test2 {
    @Autowired
    private ImageCaptchaApplication application;

    public void test() {
        // 生成滑块验证码 该方法会通过request获取浏览器内核是否是谷歌内核，如果是则返回webp类型的图片 否则返回jpeg+png类型的图片
        // 也可以手动指定返回哪种类型的 只需要在request中提供 key为captcha-type的参数(可以放到参数中或者header中) ， 值为 webp、jpeg-png
        // 来通过参数选择返回哪种类型的图片
        CaptchaResponse<ImageCaptchaVO> res1 = application.generateCaptcha(CaptchaTypeConstant.SLIDER);

        // 也可以用编码判断返回哪种类型的图片, 返回的图片,
        // 返回 底图是jpeg，滑块部分是png类型的图片
        res1 = application.generateCaptcha(CaptchaImageType.JPEG_PNG);
        // *** 注意，要生成webp类型的图片时需要手动将webp的扩展导入到系统中 ，参考: https://bitbucket.org/luciad/webp-imageio
        // res1 = application.generateCaptcha(CaptchaTypeConstant.SLIDER, CaptchaImageType.WEBP);
        // 其它扩展方法可以自己在源码中查看,都有详细注释

        // 匹配验证码是否正确
        // 该参数包含了滑动轨迹滑动时间等数据，用于校验滑块验证码。 由前端传入
        ImageCaptchaTrack sliderCaptchaTrack = new ImageCaptchaTrack();
        ApiResponse<?> match = application.matching(res1.getId(), sliderCaptchaTrack);
    }

}
