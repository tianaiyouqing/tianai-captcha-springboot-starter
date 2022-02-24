package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.template.slider.GenerateParam;
import cloud.tianai.captcha.template.slider.SliderCaptchaInfo;
import cloud.tianai.captcha.template.slider.SliderCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.exception.SliderCaptchaException;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaTrack;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;

import java.util.Map;
import java.util.UUID;


/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:52
 * @Description 抽象的 滑块验证码
 */
public abstract class AbstractSliderCaptchaApplication implements SliderCaptchaApplication {

    protected SliderCaptchaTemplate template;
    protected SliderCaptchaValidator sliderCaptchaValidator;

    protected SliderCaptchaProperties prop;

    public AbstractSliderCaptchaApplication(SliderCaptchaTemplate template,
                                            SliderCaptchaValidator sliderCaptchaValidator,
                                            SliderCaptchaProperties prop) {
        this.prop = prop;
        this.template = template;
        this.sliderCaptchaValidator = sliderCaptchaValidator;
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha() {
        // 生成滑块验证码
        SliderCaptchaInfo slideImageInfo = template.getSlideImageInfo();
        if (slideImageInfo == null) {
            // 要是生成失败
            throw new SliderCaptchaException("生成滑块验证码失败，验证码生成为空");
        }
        // 生成ID
        String id = generatorId();
        // 生成校验数据
        Map<String, Object> validData = sliderCaptchaValidator.generateSliderCaptchaValidData(slideImageInfo);
        // 存到缓存里
        cacheVerification(id, validData);
        SliderCaptchaVO verificationVO = new SliderCaptchaVO(slideImageInfo.getBackgroundImage(), slideImageInfo.getSliderImage());
        return CaptchaResponse.of(id, verificationVO);
    }

    @Override
    public boolean matching(String id, SliderCaptchaTrack sliderCaptchaTrack) {
        Map<String, Object> cachePercentage = getVerification(id);
        if (cachePercentage == null) {
            return false;
        }
        return sliderCaptchaValidator.valid(sliderCaptchaTrack, cachePercentage);
    }


    protected String generatorId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 通过缓存获取百分比
     *
     * @param id 验证码ID
     * @return Map<String, Object>
     */
    protected abstract Map<String, Object> getVerification(String id);

    /**
     * 缓存验证码
     *
     * @param id       id
     * @param validData validData
     */
    protected abstract void cacheVerification(String id, Map<String, Object> validData);

    @Override
    public SliderCaptchaResourceManager getSliderCaptchaResourceManager() {
        return template.getSlideImageResourceManager();
    }
}
