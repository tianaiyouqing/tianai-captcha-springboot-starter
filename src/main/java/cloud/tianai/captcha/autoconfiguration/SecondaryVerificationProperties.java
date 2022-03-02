package cloud.tianai.captcha.autoconfiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "captcha.slider.secondary")
public class SecondaryVerificationProperties {

    private Boolean enabled;
    private Long expire = 120000L;
    private String keyPrefix = "captcha:slider:secondary";

}
