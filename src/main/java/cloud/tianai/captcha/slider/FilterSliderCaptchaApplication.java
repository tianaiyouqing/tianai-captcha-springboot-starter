package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.slider.store.CacheStore;
import cloud.tianai.captcha.template.slider.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.template.slider.validator.common.model.dto.SliderCaptchaTrack;
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
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(String type) {
        return target.generateSliderCaptcha(type);
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
    public ImageCaptchaResourceManager getImageCaptchaResourceManager() {
        return target.getImageCaptchaResourceManager();
    }

    @Override
    public void setImageCaptchaValidator(ImageCaptchaValidator sliderCaptchaValidator) {
        target.setImageCaptchaValidator(sliderCaptchaValidator);
    }

    @Override
    public void setSliderCaptchaTemplate(ImageCaptchaGenerator sliderCaptchaTemplate) {
        target.setSliderCaptchaTemplate(sliderCaptchaTemplate);
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        target.setCacheStore(cacheStore);
    }

    @Override
    public ImageCaptchaValidator getImageCaptchaValidator() {
        return target.getImageCaptchaValidator();
    }

    @Override
    public ImageCaptchaGenerator getSliderCaptchaTemplate() {
        return target.getSliderCaptchaTemplate();
    }

    @Override
    public CacheStore getCacheStore() {
        return target.getCacheStore();
    }
}
