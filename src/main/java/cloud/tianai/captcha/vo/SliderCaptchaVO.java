package cloud.tianai.captcha.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SliderCaptchaVO  implements Serializable {
    /**
     * 背景图
     */
    private String backgroundImage;
    /**
     * 移动图
     */
    private String sliderImage;
}
