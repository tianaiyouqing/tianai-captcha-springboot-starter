package cloud.tianai.captcha.exception;

/**
 * @Author: 天爱有情
 * @Date 2020/6/19 16:36
 * @Description 验证码验证失败异常
 */
public class CaptchaValidException extends RuntimeException{

    public CaptchaValidException() {
    }

    public CaptchaValidException(String message) {
        super(message);
    }

    public CaptchaValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaValidException(Throwable cause) {
        super(cause);
    }

    public CaptchaValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
