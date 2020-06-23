package cloud.tianai.captcha.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author: 天爱有情
 * @Date 2020/6/19 16:26
 * @Description 验证码请求对象
 */
@Data
public class CaptchaRequest<T> {

    @NotEmpty(message = "验证码ID不能为空")
    private String id;

    @NotNull(message = "验证码百分比不能为空")
    @Min(value = 0, message = "百分比不能小于0")
    private Float percentage;

    @Valid
    private T form;
}