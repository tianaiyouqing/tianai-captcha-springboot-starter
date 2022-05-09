package cloud.tianai.captcha.spring.application;

import cloud.tianai.captcha.spring.autoconfiguration.ImageCaptchaProperties;
import cloud.tianai.captcha.spring.exception.CaptchaValidException;
import cloud.tianai.captcha.spring.store.CacheStore;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.exception.ImageCaptchaException;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:52
 * @Description 默认的 图片验证码应用程序
 */
@Slf4j
public class DefaultImageCaptchaApplication implements ImageCaptchaApplication {

    /** 图片验证码生成器. */
    private ImageCaptchaGenerator template;
    /** 图片验证码校验器. */
    private ImageCaptchaValidator imageCaptchaValidator;
    /** 缓冲存储. */
    private CacheStore cacheStore;
    /** 验证码配置属性. */
    private final ImageCaptchaProperties prop;
    /** 默认的过期时间. */
    private long defaultExpire = 20000L;

    public DefaultImageCaptchaApplication(ImageCaptchaGenerator template,
                                          ImageCaptchaValidator imageCaptchaValidator,
                                          CacheStore cacheStore,
                                          ImageCaptchaProperties prop) {
        this.prop = prop;
        setImageCaptchaTemplate(template);
        setImageCaptchaValidator(imageCaptchaValidator);
        setCacheStore(cacheStore);
        // 默认过期时间
        Long defaultExpire = prop.getExpire().get("default");
        if (defaultExpire != null && defaultExpire > 0) {
            this.defaultExpire = defaultExpire;
        }
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha() {
        // 生成滑块验证码
        return generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type) {
        ImageCaptchaInfo slideImageInfo = getImageCaptchaTemplate().generateCaptchaImage(type);
        return afterGenerateSliderCaptcha(slideImageInfo);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(GenerateParam param) {
        ImageCaptchaInfo slideImageInfo = getImageCaptchaTemplate().generateCaptchaImage(param);
        return afterGenerateSliderCaptcha(slideImageInfo);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType) {
        return generateCaptcha(CaptchaTypeConstant.SLIDER, captchaImageType);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType) {
        GenerateParam param = new GenerateParam();
        if (CaptchaImageType.WEBP.equals(captchaImageType)) {
            param.setBackgroundFormatName("webp");
            param.setSliderFormatName("webp");
        } else {
            param.setBackgroundFormatName("jpeg");
            param.setSliderFormatName("png");
        }
        param.setType(type);
        return generateCaptcha(param);
    }


    public CaptchaResponse<ImageCaptchaVO> afterGenerateSliderCaptcha(ImageCaptchaInfo slideImageInfo) {
        if (slideImageInfo == null) {
            // 要是生成失败
            throw new ImageCaptchaException("生成滑块验证码失败，验证码生成为空");
        }
        // 生成ID
        String id = generatorId();
        // 生成校验数据
        Map<String, Object> validData = getImageCaptchaValidator().generateImageCaptchaValidData(slideImageInfo);
        // 存到缓存里
        cacheVerification(id, slideImageInfo.getType(), validData);
        ImageCaptchaVO verificationVO = new ImageCaptchaVO();
        verificationVO.setBackgroundImage(slideImageInfo.getBackgroundImage());
        verificationVO.setSliderImage(slideImageInfo.getSliderImage());
        verificationVO.setBackgroundImageWidth(slideImageInfo.getBgImageWidth());
        verificationVO.setBackgroundImageHeight(slideImageInfo.getBgImageHeight());
        verificationVO.setSliderImageWidth(slideImageInfo.getSliderImageWidth());
        verificationVO.setSliderImageHeight(slideImageInfo.getSliderImageHeight());
        verificationVO.setData(slideImageInfo.getData());
        return CaptchaResponse.of(id, verificationVO);
    }

    @Override
    public boolean matching(String id, ImageCaptchaTrack imageCaptchaTrack) {
        Map<String, Object> cachePercentage = getVerification(id);
        if (cachePercentage == null) {
            return false;
        }
        return getImageCaptchaValidator().valid(imageCaptchaTrack, cachePercentage);
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
     * @param type
     * @param validData validData
     */
    protected void cacheVerification(String id, String type, Map<String, Object> validData) {
        Long expire = prop.getExpire().getOrDefault(type, defaultExpire);
        if (!getCacheStore().setCache(getKey(id), validData, expire, TimeUnit.MILLISECONDS)) {
            log.error("缓存验证码数据失败， id={}, validData={}", id, validData);
            throw new CaptchaValidException(type, "缓存验证码数据失败");
        }
    }

    protected String getKey(String id) {
        return prop.getPrefix().concat(":").concat(id);
    }

    @Override
    public ImageCaptchaResourceManager getImageCaptchaResourceManager() {
        return getImageCaptchaTemplate().getImageResourceManager();
    }

    @Override
    public void setImageCaptchaValidator(ImageCaptchaValidator imageCaptchaValidator) {
        this.imageCaptchaValidator = imageCaptchaValidator;
    }

    @Override
    public void setImageCaptchaTemplate(ImageCaptchaGenerator imageCaptchaGenerator) {
        this.template = imageCaptchaGenerator;
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @Override
    public ImageCaptchaValidator getImageCaptchaValidator() {
        return this.imageCaptchaValidator;
    }

    @Override
    public ImageCaptchaGenerator getImageCaptchaTemplate() {
        return this.template;
    }

    @Override
    public CacheStore getCacheStore() {
        return this.cacheStore;
    }
}
