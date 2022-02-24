package cloud.tianai.captcha.slider;

import cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties;
import cloud.tianai.captcha.template.slider.SliderCaptchaTemplate;
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 天爱有情
 * @Date 2020/5/29 9:20
 * @Description 基于Redis的缓存策略
 */
public class RedisCacheSliderCaptchaApplication extends AbstractSliderCaptchaApplication {

    private static final RedisScript<String> SCRIPT_GET_CACHE = new DefaultRedisScript<>("local res = redis.call('get',KEYS[1])  if res == nil  then return nil  else  redis.call('del',KEYS[1]) return res end", String.class);
    private StringRedisTemplate redisTemplate;
    private String prefix;
    private long expire;
    private Gson gson = new Gson();

    public RedisCacheSliderCaptchaApplication(StringRedisTemplate redisTemplate, SliderCaptchaTemplate template, SliderCaptchaValidator sliderCaptchaValidator, SliderCaptchaProperties prop) {
        super(template, sliderCaptchaValidator, prop);
        this.redisTemplate = redisTemplate;
        this.prefix = prop.getPrefix();
        this.expire = prop.getExpire();
    }

    @Override
    protected Map<String, Object> getVerification(String id) {
        String key = getKey(id);
        String json = redisTemplate.execute(SCRIPT_GET_CACHE, Collections.singletonList(key));
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    @Override
    protected void cacheVerification(String id, Map<String, Object> validData) {
        String key = getKey(id);
        redisTemplate.opsForValue().set(key, gson.toJson(validData), expire, TimeUnit.MILLISECONDS);
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

