package cloud.tianai.captcha.spring.application;

import cloud.tianai.captcha.spring.store.CacheStore;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;

/**
 * @Author: 天爱有情
 * @date 2022/3/2 14:22
 * @Description 用于SliderCaptchaApplication增加附属功能
 */
public class FilterImageCaptchaApplication implements ImageCaptchaApplication {


    protected ImageCaptchaApplication target;

    public FilterImageCaptchaApplication(ImageCaptchaApplication target) {
        this.target = target;
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha() {
        return target.generateCaptcha();
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type) {
        return target.generateCaptcha(type);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType) {
        return target.generateCaptcha(captchaImageType);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType) {
        return target.generateCaptcha(type, captchaImageType);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(GenerateParam param) {
        return target.generateCaptcha(param);
    }

    @Override
    public boolean matching(String id, ImageCaptchaTrack ImageCaptchaTrack) {
        return target.matching(id, ImageCaptchaTrack);
    }

    @Override
    public boolean matching(String id, Float percentage) {
        return target.matching(id, percentage);
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
    public void setImageCaptchaTemplate(ImageCaptchaGenerator imageCaptchaGenerator) {
        target.setImageCaptchaTemplate(imageCaptchaGenerator);
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
    public ImageCaptchaGenerator getImageCaptchaTemplate() {
        return target.getImageCaptchaTemplate();
    }

    @Override
    public CacheStore getCacheStore() {
        return target.getCacheStore();
    }
}
