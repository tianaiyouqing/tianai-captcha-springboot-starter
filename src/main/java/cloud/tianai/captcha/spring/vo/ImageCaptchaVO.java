package cloud.tianai.captcha.spring.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageCaptchaVO implements Serializable {
    /** 背景图.*/
    private String backgroundImage;

    /** 移动图.*/
    private String sliderImage;
    /** 背景图片宽度.*/
    private Integer backgroundImageWidth;
    /** 背景图片高度.*/
    private Integer backgroundImageHeight;
    /** 滑动图片宽度.*/
    private Integer sliderImageWidth;
    /** 滑动图片高度.*/
    private Integer sliderImageHeight;
    /** data 扩展数据.*/
    private Object data;
}
