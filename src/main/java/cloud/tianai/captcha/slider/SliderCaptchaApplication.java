package cloud.tianai.captcha.slider;


import cloud.tianai.captcha.slider.store.CacheStore;
import cloud.tianai.captcha.template.slider.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;
import cloud.tianai.captcha.template.slider.validator.common.model.dto.SliderCaptchaTrack;
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
     *
     * @return
     */
    CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha();

    /**
     * 生成滑块验证码
     * @param type type类型
     * @return CaptchaResponse<SliderCaptchaVO>
     */
    CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(String type);

    /**
     * 生成滑块验证码
     *
     * @param captchaImageType 要生成webp还是jpg类型的图片
     * @return CaptchaResponse<SliderCaptchaVO>
     */
    CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(CaptchaImageType captchaImageType);

    /**
     * 生成滑块验证码
     *
     * @param param param
     * @return CaptchaResponse<SliderCaptchaVO>
     */
    CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(GenerateParam param);

    /**
     * 匹配
     *
     * @param id                 验证码的ID
     * @param sliderCaptchaTrack 滑动轨迹
     * @return 匹配成功返回true， 否则返回false
     */
    boolean matching(String id, SliderCaptchaTrack sliderCaptchaTrack);

    /**
     * 获取验证码资源管理器
     *
     * @return SliderCaptchaResourceManager
     */
    ImageCaptchaResourceManager getImageCaptchaResourceManager();

    /**
     * 设置 SliderCaptchaValidator 验证码验证器
     *
     * @param sliderCaptchaValidator sliderCaptchaValidator
     */
    void setSliderCaptchaValidator(SliderCaptchaValidator sliderCaptchaValidator);

    /**
     * 设置 SliderCaptchaGenerator 验证码生成器
     *
     * @param sliderCaptchaGenerator SliderCaptchaGenerator
     */
    void setSliderCaptchaTemplate(ImageCaptchaGenerator sliderCaptchaGenerator);

    /**
     * 设置 缓存存储器
     *
     * @param cacheStore cacheStore
     */
    void setCacheStore(CacheStore cacheStore);

    /**
     * 获取验证码验证器
     *
     * @return SliderCaptchaValidator
     */
    SliderCaptchaValidator getSliderCaptchaValidator();

    /**
     * 获取验证码生成器
     *
     * @return SliderCaptchaTemplate
     */
    ImageCaptchaGenerator getSliderCaptchaTemplate();

    /**
     * 获取缓存存储器
     *
     * @return CacheStore
     */
    CacheStore getCacheStore();

}
