package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.template.slider.GenerateParam;
import cloud.tianai.captcha.template.slider.SliderCaptchaInfo;
import cloud.tianai.captcha.template.slider.SliderCaptchaResourceManager;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.exception.SliderCaptchaException;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;

import java.util.UUID;


/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:52
 * @Description 抽象的 滑块验证码
 */
public abstract class AbstractSliderCaptchaApplication implements SliderCaptchaApplication {

    protected SliderCaptchaTemplate template;

    protected SliderCaptchaProperties prop;

    public AbstractSliderCaptchaApplication(SliderCaptchaTemplate template, SliderCaptchaProperties prop) {
        this.prop = prop;
        this.template = template;
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha() {
        // 生成滑块验证码
        SliderCaptchaInfo slideImageInfo = template.getSlideImageInfo(GenerateParam.builder()
                .backgroundFormatName("jpeg")
                .sliderFormatName("png")
                .obfuscate(prop.getObfuscate())
                .build());
        if (slideImageInfo == null) {
            // 要是生成失败
            throw new SliderCaptchaException("生成滑块验证码失败，验证码生成为空");
        }
        // 生成ID
        String id = generatorId();

        // 存到缓存里
        cacheVerification(id, slideImageInfo.getXPercent());
        SliderCaptchaVO verificationVO = new SliderCaptchaVO(slideImageInfo.getBackgroundImage(), slideImageInfo.getSliderImage());
        return CaptchaResponse.of(id, verificationVO);
    }


    @Override
    public boolean matching(String id, Float percentage) {
        Float cachePercentage = getPercentForCache(id);
        if (cachePercentage == null || cachePercentage < 0) {
            return false;
        }
        return template.percentageContrast(percentage, cachePercentage);
    }


    protected String generatorId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 通过缓存获取百分比
     *
     * @param id 验证码ID
     * @return Float
     */
    protected abstract Float getPercentForCache(String id);

    /**
     * 缓存验证码
     *
     * @param id       id
     * @param xPercent ID对应的百分比
     */
    protected abstract void cacheVerification(String id, Float xPercent);

    @Override
    public SliderCaptchaResourceManager getSliderCaptchaResourceManager() {
        return template.getSlideImageResourceManager();
    }
}
