package cloud.tianai.captcha;

import cloud.tianai.captcha.slider.LocalCacheSliderCaptchaApplication;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

public class CaptchaTest {

    public static void main(String[] args) throws InterruptedException {
//        LocalCacheSliderCaptchaApplication captchaApplication = new LocalCacheSliderCaptchaApplication(TimeUnit.MINUTES.toMillis(1));
//
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                for (int a = 0; a < 100; a++) {
//                    StopWatch stopWatch = new StopWatch();
//                    stopWatch.start();
//                    CaptchaResponse<SliderCaptchaVO> response = captchaApplication.generateSliderCaptcha();
//                    stopWatch.stop();
//
//                    long totalTimeMillis = stopWatch.getTotalTimeMillis();
//                    System.out.println("耗时:" + totalTimeMillis);
//                    System.out.println(response);
//                }
//            }).start();
//        }
//
//
//        TimeUnit.SECONDS.sleep(10_000);
    }
}
