package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.exception.CaptchaValidException;
import cloud.tianai.captcha.slider.store.CacheStore;
import cloud.tianai.captcha.template.slider.common.exception.SliderCaptchaException;
import cloud.tianai.captcha.template.slider.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.template.slider.generator.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.template.slider.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.template.slider.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;
import cloud.tianai.captcha.template.slider.validator.common.model.dto.SliderCaptchaTrack;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:52
 * @Description 抽象的 滑块验证码
 */
@Slf4j
public class DefaultSliderCaptchaApplication implements SliderCaptchaApplication {

    private ImageCaptchaGenerator template;
    private SliderCaptchaValidator sliderCaptchaValidator;
    private SliderCaptchaProperties prop;
    private CacheStore cacheStore;

    public DefaultSliderCaptchaApplication(ImageCaptchaGenerator template,
                                           SliderCaptchaValidator sliderCaptchaValidator,
                                           CacheStore cacheStore,
                                           SliderCaptchaProperties prop) {
        this.prop = prop;
        setSliderCaptchaTemplate(template);
        setSliderCaptchaValidator(sliderCaptchaValidator);
        setCacheStore(cacheStore);
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha() {
        // 生成滑块验证码
        return generateSliderCaptcha(CaptchaTypeConstant.SLIDER);
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(String type) {
        return afterGenerateSliderCaptcha(getSliderCaptchaTemplate().generateCaptchaImage(type));
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(GenerateParam param) {
        ImageCaptchaInfo slideImageInfo = getSliderCaptchaTemplate().generateCaptchaImage(param);
        return afterGenerateSliderCaptcha(slideImageInfo);
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha(CaptchaImageType captchaImageType) {
        GenerateParam param = new GenerateParam();
        param.setObfuscate(prop.getObfuscate());
        if (CaptchaImageType.WEBP.equals(captchaImageType)) {
            param.setBackgroundFormatName("webp");
            param.setSliderFormatName("webp");
        } else {
            param.setBackgroundFormatName("jpeg");
            param.setSliderFormatName("png");
        }
        return generateSliderCaptcha(param);
    }


    public CaptchaResponse<SliderCaptchaVO> afterGenerateSliderCaptcha(ImageCaptchaInfo slideImageInfo) {
        if (slideImageInfo == null) {
            // 要是生成失败
            throw new SliderCaptchaException("生成滑块验证码失败，验证码生成为空");
        }
        // 生成ID
        String id = generatorId();
        // 生成校验数据
        Map<String, Object> validData = getSliderCaptchaValidator().generateSliderCaptchaValidData(slideImageInfo);
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
        return getSliderCaptchaValidator().valid(sliderCaptchaTrack, cachePercentage);
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
    protected Map<String, Object> getVerification(String id) {
        return getCacheStore().getAndRemoveCache(getKey(id));
    }

    /**
     * 缓存验证码
     *
     * @param id        id
     * @param validData validData
     */
    protected void cacheVerification(String id, Map<String, Object> validData) {
        if (!getCacheStore().setCache(getKey(id), validData, prop.getExpire(), TimeUnit.MILLISECONDS)) {
            log.error("缓存验证码数据失败， id={}, validData={}", id, validData);
            throw new CaptchaValidException("缓存验证码数据失败");
        }
    }

    protected String getKey(String id) {
        return prop.getPrefix().concat(":").concat(id);
    }

    @Override
    public ImageCaptchaResourceManager getImageCaptchaResourceManager() {
        return getSliderCaptchaTemplate().getImageResourceManager();
    }

    @Override
    public void setSliderCaptchaValidator(SliderCaptchaValidator sliderCaptchaValidator) {
        this.sliderCaptchaValidator = sliderCaptchaValidator;
    }

    @Override
    public void setSliderCaptchaTemplate(ImageCaptchaGenerator sliderCaptchaTemplate) {
        this.template = sliderCaptchaTemplate;
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @Override
    public SliderCaptchaValidator getSliderCaptchaValidator() {
        return this.sliderCaptchaValidator;
    }

    @Override
    public ImageCaptchaGenerator getSliderCaptchaTemplate() {
        return this.template;
    }

    @Override
    public CacheStore getCacheStore() {
        return this.cacheStore;
    }
}
