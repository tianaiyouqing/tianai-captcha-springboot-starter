# 这是 滑块验证码(tianai-captcha)的springboot启动类

## [在线体验](https://www.tianai.cloud)

## 验证码demo移步 [tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)

## 快速上手

```xml
<!-- maven导入 -->
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha-springboot-starter</artifactId>
    <version>1.3.0.RELEASE</version>
</dependency>
```

- 注解版风格

```java
import cloud.tianai.captcha.annotation.Captcha;
import cloud.tianai.captcha.request.CaptchaRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class Test {
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
}
```

- 编码式风格

```java
import cloud.tianai.captcha.template.slider.validator.SliderCaptchaTrack;

public class Test {
    @Autowired
    private SliderCaptchaApplication application;

    public void test() {
        // 生成滑块验证码 该方法会通过request获取浏览器内核是否是谷歌内核，如果是则返回webp类型的图片 否则返回jpeg+png类型的图片
        // 也可以手动指定返回哪种类型的 只需要在request中提供 key为captcha-type的参数(可以放到参数中或者header中) ， 值为 webp、jpeg-png
        // 来通过参数选择返回哪种类型的图片
        CaptchaResponse<SliderCaptchaVO> res1 = application.generateSliderCaptcha();

        // 也可以用编码判断返回哪种类型的图片, 返回webp类型的图片
        res1 = application.generateSliderCaptcha(CaptchaImageType.WEBP);
        // 返回 底图是jpeg，滑块部分是png类型的图片
        res1 = application.generateSliderCaptcha(CaptchaImageType.JPEG_PNG);
        // 其它扩展方法可以自己在源码中查看,都有详细注释

        // 匹配验证码是否正确
        // 该参数包含了滑动轨迹滑动时间等数据，用于校验滑块验证码。 由前端传入
        SliderCaptchaTrack sliderCaptchaTrack = new SliderCaptchaTrack();
        boolean match = application.matching(res1.getId(), sliderCaptchaTrack);
    }

}
```

- 前端展示效果

![](image/1.png)
![](image/2.png)
## springboot配置文件说明

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
    # 是否加入混淆滑块，默认不加入
    obfuscate: false
    cache:
      # 缓存控制， 默认为false不开启
      enabled: true
      # 验证码会提前缓存一些生成好的验证数据， 默认是20
      cacheSize: 20
      # 因为缓存池会缓存 webp 和jpg+png 两种类型的图片， 所有这里可以配置webp生成的数量， 默认是 总缓存的70%(captcha.cacheSize*0.7)
      webp-cache-size: 16
      # 缓存拉取失败后等待时间 默认是 5秒钟
      wait-time: 5000
      # 缓存检查间隔 默认是2秒钟
      period: 2000
    secondary:
      # 二次验证， 默认false 不开启
      enabled: true
      # 二次验证过期时间， 默认 2分钟
      expire: 120000
      # 二次验证缓存key前缀，默认是 captcha:slider:secondary
      keyPrefix: captcha:slider:secondary
```

## 扩展
### 二次验证操作

```java
import org.springframework.beans.factory.annotation.Autowired;

public class Demo {
    @Autowired
    private SecondaryVerificationApplication sva;

    // 如果开启了二次验证 ， 进行二次验证校验的时候 
    public void test() {
        // 该id是生成滑块验证码时候的id
        String id = "";
        // 进行二次验证
        boolean valid = sva.secondaryVerification(id);
        System.out.println("二次验证结果:" + valid);
    }
}

```
## 自定义扩展
> 依赖于 tianai-captcha 的高扩展性，
> 可以自定义 如下实现 然后直接注入到spring中即可替换默认实现,实现自定义扩展
- 生成器(`SliderCaptchaGenerator`) -- 主要负责生成滑块验证码所需的图片
- 校验器(`SliderCaptchaValidato`r) -- 主要负责校验用户滑动的行为轨迹是否合规
- 资源管理器(`SliderCaptchaResourceManager`) -- 主要负责读取验证码背景图片和模板图片等
- 资源存储(`ResourceStore`) -- 负责存储背景图和模板图
- 资源提供者(`ResourceProvider`) -- 负责将资源存储器中对应的资源转换为文件流
- SliderCaptchaApplication 滑块应用程序，上面一些接口的组合和增强，比如负责把验证的数据存到缓存中，用户一般直接使用这个接口方便的生成滑块图片和校验数据
## 其它
- 该自动装配器可以自动选择redis做缓存还是缓存到本地，自动进行识别装配
- 本地缓存参考了本人写的 [expiring-map](https://gitee.com/tianai/expiring-map) (使用redis淘汰策略) 做过期处理, 有兴趣可以看一下
- 关于[tianai-captcha](https://gitee.com/tianai/tianai-captcha)

## qq群: 1021884609
 