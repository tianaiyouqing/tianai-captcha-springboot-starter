# 这是 滑块验证码(tianai-captcha)的springboot启动类
## [在线体验](http://101.42.239.82:8080/)
## 验证码demo移步 [tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)
## 不说废话，直接上代码

```xml
<!-- maven导入 -->
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha-springboot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
```

- 注解版风格
```java
// 只需要在需要验证的controller层加入 @Captcha 注解，
// 并且接受的参数指定成CaptchaRequest即可自动进行校验
// 自己真实的参数可以写到 CaptchaRequest对象的泛型中
// 如果校验失败，会抛出CaptchaValidException异常
// 对校验失败的处理，可以使用sping的全局异常拦截CaptchaValidException异常进行处理

@Captcha
@PostMapping("/login")
public ApiResponse login(@RequestBody CaptchaRequest<LoginForm> request) {
    // 进入这个方法就说明已经校验成功了
}
```
-  编码式风格
```java
public class Test {
    @Autowired
    private SliderCaptchaApplication application;
    
    public void test() {
        // 生成滑块验证码
        CaptchaResponse<SliderCaptchaVO> res1 = application.generateSliderCaptcha();
        // 匹配验证码是否正确
        boolean match =  application.matching(res1.getId(), 0.35665);        
    }

}
```


- 前端展示效果

![](image/1.png)
![](image/2.png)

- springboot配置文件说明
```yaml
# 滑块验证码配置， 详细请看 cloud.tianai.captcha.autoconfiguration.SliderCaptchaProperties 类
captcha:
  slider:
    # 如果项目中使用到了redis，滑块验证码会自动把验证码数据存到redis中， 这里配置redis的key的前缀,默认是captcha:slider
    prefix: |-
      captcha:slider
    # 验证码过期时间，默认是1分钟,单位毫秒， 可以根据自身业务进行调整
    expire: 60000
    # 使用加载系统自带的资源， 默认是true
    init-default-resource: false
    # 验证码会提前缓存一些生成好的验证数据， 默认是20
    cacheSize: 20
    # 缓存拉取失败后等待时间 默认是 5秒钟
    wait-time: 5000
    # 缓存检查间隔 默认是2秒钟
    period: 2000
```
- 该自动装配器可以自动选择redis做缓存还是缓存到本地，自动进行识别装配
- 本地缓存参考了本人写的 [expiring-map](https://gitee.com/tianai/expiring-map) (使用redis淘汰策略) 做过期处理, 有兴趣可以看一下

- 关于[tianai-captcha](https://gitee.com/tianai/tianai-captcha)
- qq群: 1021884609
 