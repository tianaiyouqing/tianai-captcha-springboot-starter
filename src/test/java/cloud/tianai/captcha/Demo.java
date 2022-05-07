package cloud.tianai.captcha;

import cloud.tianai.captcha.spring.cache.ConCurrentExpiringMap;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

public class Demo {


    public static void main(String[] args) throws InterruptedException {
        ConCurrentExpiringMap<Object, Object> expiringMap = new ConCurrentExpiringMap<>();
        expiringMap.init();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 1000000; i++) {
            expiringMap.put(i + "", 1212, 6000L, TimeUnit.MILLISECONDS);
        }
        stopWatch.stop();

        System.out.println("耗时:" + stopWatch.getTotalTimeMillis());


        TimeUnit.HOURS.sleep(1);

//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        for (int i = 0; i < 1000000; i++) {
//            long l = System.nanoTime();
//        }
//        stopWatch.stop();
//
//        System.out.println("耗时:" + stopWatch.getTotalTimeMillis());
    }
}
