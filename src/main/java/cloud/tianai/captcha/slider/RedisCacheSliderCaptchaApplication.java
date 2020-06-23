package cloud.tianai.captcha.slider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 9:20
 * @Description 基于Redis的缓存策略
 */
public class RedisCacheSliderCaptchaApplication extends AbstractSliderCaptchaApplication {

    private static final RedisScript<String> SCRIPT_GET_CACHE = new DefaultRedisScript<>("local res = redis.call('get',KEYS[1])  if res == nil  then return nil  else  redis.call('del',KEYS[1]) return res end", String.class);
    private StringRedisTemplate redisTemplate;
    private String prefix ;
    private long expire ;

    public RedisCacheSliderCaptchaApplication(StringRedisTemplate redisTemplate, String prefix, long expire) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
        this.expire = expire;
    }

    @Override
    protected Float getPercentForCache(String id) {
        String key = getKey(id);
        String percentStr = redisTemplate.execute(SCRIPT_GET_CACHE, Collections.singletonList(key));
        if (StringUtils.isBlank(percentStr)) {
            return null;
        }
        return Float.valueOf(percentStr);
    }

    @Override
    protected void cacheVerification(String id, Float xPercent) {
        String key = getKey(id);
        redisTemplate.opsForValue().set(key, String.valueOf(xPercent), expire, TimeUnit.MILLISECONDS);
    }

    private String getKey(String id) {
        return prefix.concat(":").concat(id);
    }

    public static void main(String[] args) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(new LettuceConnectionFactory());
        redisTemplate.afterPropertiesSet();
        redisTemplate.opsForValue().set("a", "b");
    }
}

