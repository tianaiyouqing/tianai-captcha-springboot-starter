package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.slider.store.CacheStore;
import cloud.tianai.captcha.template.slider.GenerateParam;
import cloud.tianai.captcha.template.slider.SliderCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaTrack;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:22
 * @Description 用于SliderCaptchaApplication增加附属功能
 */
public class FilterSliderCaptchaApplication implements SliderCaptchaApplication {


    protected SliderCaptchaApplication target;

    public FilterSliderCaptchaApplication(SliderCaptchaApplication target) {
        this.target = target;
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha() {
        return target.generateSliderCaptcha();
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(CaptchaImageType captchaImageType) {
        return target.generateSliderCaptcha(captchaImageType);
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(GenerateParam param) {
        return target.generateSliderCaptcha(param);
    }

    @Override
    public boolean matching(String id, SliderCaptchaTrack sliderCaptchaTrack) {
        return target.matching(id, sliderCaptchaTrack);
    }

    @Override
    public SliderCaptchaResourceManager getSliderCaptchaResourceManager() {
        return target.getSliderCaptchaResourceManager();
    }

    @Override
    public void setSliderCaptchaValidator(SliderCaptchaValidator sliderCaptchaValidator) {
        target.setSliderCaptchaValidator(sliderCaptchaValidator);
    }

    @Override
    public void setSliderCaptchaTemplate(SliderCaptchaTemplate sliderCaptchaTemplate) {
        target.setSliderCaptchaTemplate(sliderCaptchaTemplate);
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        target.setCacheStore(cacheStore);
    }

    @Override
    public SliderCaptchaValidator getSliderCaptchaValidator() {
        return target.getSliderCaptchaValidator();
    }

    @Override
    public SliderCaptchaTemplate getSliderCaptchaTemplate() {
        return target.getSliderCaptchaTemplate();
    }

    @Override
    public CacheStore getCacheStore() {
        return target.getCacheStore();
    }
}
