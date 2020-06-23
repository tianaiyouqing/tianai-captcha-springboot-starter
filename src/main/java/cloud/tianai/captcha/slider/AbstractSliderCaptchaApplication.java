package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.tianaicaptcha.template.slider.SliderCaptchaInfo;
import cloud.tianai.captcha.tianaicaptcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.util.Sequence;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;


/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:52
 * @Description 抽象的 滑块验证码
 */
public abstract class AbstractSliderCaptchaApplication implements SliderCaptchaApplication {

    private SliderCaptchaTemplate template;
    private Sequence sequence = new Sequence();

    public AbstractSliderCaptchaApplication() {
        this.template = new SliderCaptchaTemplate();
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptcha() {
        // 生成滑块验证码
        SliderCaptchaInfo slideImageInfo = template.getSlideImageInfo();
        // 生成ID
        String id = generatorId();

        // 存到缓存里
        cacheVerification(id, slideImageInfo.getXPercent());
        SliderCaptchaVO verificationVO = new SliderCaptchaVO(slideImageInfo.getBackgroundImage(), slideImageInfo.getSliderImage());
        return CaptchaResponse.of(id, verificationVO);
    }

    @Override
    public CaptchaResponse<SliderCaptchaVO> generateSliderCaptchaForWebp() {
        SliderCaptchaInfo slideImageInfo = template.getSlideImageInfoForWebp();
        // 生成ID
        String id = generatorId();

        // 存到缓存里
        cacheVerification(id, slideImageInfo.getXPercent());
        SliderCaptchaVO verificationVO = new SliderCaptchaVO(slideImageInfo.getBackgroundImage(), slideImageInfo.getSliderImage());
        return CaptchaResponse.of(id, verificationVO);
    }

    @Override
    public boolean matching(String id, Float percentage) {
        Float cachePercentage =  getPercentForCache(id);
        if (cachePercentage == null || cachePercentage < 0) {
            return false;
        }
        return template.percentageContrast(percentage, cachePercentage);
    }


    private String generatorId() {
        return String.valueOf(sequence.nextId());
    }

    /**
     * 通过缓存获取百分比
     * @param id 验证码ID
     * @return Float
     */
    protected abstract Float getPercentForCache(String id);

    /**
     * 缓存验证码
     * @param id id
     * @param xPercent ID对应的百分比
     */
    protected abstract void cacheVerification(String id, Float xPercent);
}
