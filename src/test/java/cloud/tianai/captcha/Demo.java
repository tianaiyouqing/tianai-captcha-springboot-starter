package cloud.tianai.captcha;

import cloud.tianai.captcha.slider.LocalCacheSliderCaptchaApplication;
import cloud.tianai.captcha.vo.CaptchaResponse;
import cloud.tianai.captcha.vo.SliderCaptchaVO;
import vip.tianai.ExpiringMap;
import vip.tianai.listener.ClearExpireEntityListener;
import vip.tianai.util.ExpiringMapUtils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Demo {


    public static void main(String[] args) throws InterruptedException {
        LocalCacheSliderCaptchaApplication application = new LocalCacheSliderCaptchaApplication(6000);
        CaptchaResponse<SliderCaptchaVO> response = application.generateSliderCaptchaForWebp();
        System.out.println(response);

        ClearExpireEntityListener listener = ExpiringMapUtils.createClearExpireEntityListener(200, 1000L);
        // 构建一个带有过期key的本地缓存
        ExpiringMap<String, Float> expiringMap = ExpiringMapUtils.<String, Float>builder()
                .addListener(listener)
                .build();
        expiringMap.init();
        for (int i = 0; i < 10000; i++) {
            expiringMap.put("aa" + i, 123f, ThreadLocalRandom.current().nextLong(100, 6000));
        }

        TimeUnit.SECONDS.sleep(100);


    }
}
