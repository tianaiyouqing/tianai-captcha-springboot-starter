package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.template.slider.SliderCaptchaInfo;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.exception.SliderCaptchaException;
import cloud.tianai.captcha.util.Sequence;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;

import java.net.URL;
import java.util.List;
import java.util.Map;


/**
 * @Author: 天爱有情
 * @Date 2020/5/29 8:52
 * @Description 抽象的 滑块验证码
 */
public abstract class AbstractSliderCaptchaApplication implements SliderCaptchaApplication {

    private SliderCaptchaTemplate template;
    private Sequence sequence = new Sequence();

    protected SliderCaptchaProperties prop;

    public AbstractSliderCaptchaApplication(SliderCaptchaTemplate template, SliderCaptchaProperties prop) {
        this.prop = prop;
        this.template = template;
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


    @Override
    public void addResource(URL url) {
        template.addResource(url);
    }

    @Override
    public void addTemplate(Map<String, URL> t) {
        template.addTemplate(t);
    }

    @Override
    public void setResource(List<URL> resources) {
        template.setResource(resources);
    }

    @Override
    public void setTemplates(List<Map<String, URL>> imageTemplates) {
        template.setTemplates(imageTemplates);
    }

    @Override
    public void deleteResource(URL resource) {
        template.deleteResource(resource);
    }

    @Override
    public void deleteTemplate(Map<String, URL> t) {
        template.deleteTemplate(t);
    }

    private String generatorId() {
        return String.valueOf(sequence.nextId());
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
}
