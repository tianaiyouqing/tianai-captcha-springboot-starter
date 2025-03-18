# 这是 滑块验证码(tianai-captcha)的springboot启动类

## [在线体验](https://www.tianai.cloud)
## 在线文档 [在线文档](http://doc.captcha.tianai.cloud)
## 验证码demo移步 [tianai-captcha-demo](https://gitee.com/tianai/tianai-captcha-demo)

## 快速上手

```xml
<!-- maven导入 -->
<dependency>
    <groupId>cloud.tianai.captcha</groupId>
    <artifactId>tianai-captcha-springboot-starter</artifactId>
    <version>1.5.2</version>
</dependency>
```

```java
package cloud.tianai.captcha.readme;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.spring.application.CaptchaImageType;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import org.springframework.beans.factory.annotation.Autowired;

public class Test2 {
    @Autowired
    private ImageCaptchaApplication application;

    public void test() {
        // 生成滑块验证码 
        CaptchaResponse<ImageCaptchaVO> res1 = application.generateCaptcha(CaptchaTypeConstant.SLIDER);


        // 匹配验证码是否正确
        // 该参数包含了滑动轨迹滑动时间等数据，用于校验滑块验证码。 由前端传入
        ImageCaptchaTrack sliderCaptchaTrack = new ImageCaptchaTrack();
        ApiResponse<?> match = application.matching(res1.getId(), sliderCaptchaTrack);
    }

}

```

- 前端展示效果

![](https://minio.tianai.cloud/public/1.png)
![](https://minio.tianai.cloud/public/2.png)
## springboot配置文件说明

```yaml
# 滑块验证码配置， 详细请看 cloud.tianai.captcha.autoconfiguration.ImageCaptchaProperties 类
captcha:
  # 如果项目中使用到了redis，滑块验证码会自动把验证码数据存到redis中， 这里配置redis的key的前缀,默认是captcha:slider
  prefix: captcha
  # 验证码过期时间，默认是2分钟,单位毫秒， 可以根据自身业务进行调整
  expire:
    # 默认缓存时间 2分钟
    default: 10000
    # 针对 点选验证码 过期时间设置为 2分钟， 因为点选验证码验证比较慢，把过期时间调整大一些
    WORD_IMAGE_CLICK: 20000
  # 使用加载系统自带的资源， 默认是 false
  init-default-resource: false
  # 缓存控制， 默认为false不开启
  local-cache-enabled: true
  # 验证码会提前缓存一些生成好的验证数据， 默认是20
  local-cache-size: 20
  # 缓存拉取失败后等待时间 默认是 5秒钟
  local-cache-wait-time: 5000
  # 缓存检查间隔 默认是2秒钟
  local-cache-period: 2000
  # 配置字体库，文字点选验证码的字体库，可以配置多个
  font-path:
    - classpath:font/simhei.ttf
  secondary:
    # 二次验证， 默认false 不开启
    enabled: false
    # 二次验证过期时间， 默认 2分钟
    expire: 120000
    # 二次验证缓存key前缀，默认是 captcha:secondary
    keyPrefix: "captcha:secondary"
```
## 自定义扩展
> 依赖于 tianai-captcha 的高扩展性，
> 可以自定义 如下实现 然后直接注入到spring中即可替换默认实现,实现自定义扩展
- 生成器(`ImageCaptchaGenerator`) -- 主要负责生成滑块验证码所需的图片
- 校验器(`ImageCaptchaValidator`) -- 主要负责校验用户滑动的行为轨迹是否合规
- 资源管理器(`ImageCaptchaResourceManager`) -- 主要负责读取验证码背景图片和模板图片等
- 资源存储(`ResourceStore`) -- 负责存储背景图和模板图
- 资源提供者(`ResourceProvider`) -- 负责将资源存储器中对应的资源转换为文件流
- 滑块应用程序(`ImageCaptchaApplication`) ，上面一些接口的组合和增强，比如负责把验证的数据存到缓存中，用户一般直接使用这个接口方便的生成滑块图片和校验数据

## 基于 tianai-captcha 的一些默认扩展
- `SpringMultiImageCaptchaGenerator` 基于Spring的多验证码生成器
    - 基于 `MultiImageCaptchaGenerator`进行扩展
    - 可以通过手动实现`ImageCaptchaGeneratorProvider` 然后注入到spring中实现自定义验证码扩展，也可以通过该方法替换掉默认的实现

- `SecondaryVerificationApplication` 二次验证扩展
    - 基于 `ImageCaptchaApplication`进行扩展 实现了二次验证功能，
    - 该功能默认不开启
    - 可以在配置文件中配置 `captcha.secondary.endbled=true`进行手动开启
    - 使用例子

```java
package cloud.tianai.captcha.readme;

import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class Test3 {
    @Autowired
    private ImageCaptchaApplication sca;

    @GetMapping("/check2")
    @ResponseBody
    public boolean check2Captcha(@RequestParam("id") String id) {
        // 如果开启了二次验证
        if (sca instanceof SecondaryVerificationApplication) {
            return ((SecondaryVerificationApplication) sca).secondaryVerification(id);
        }
        return false;
    }
}

```
## 其它
- 该自动装配器可以自动选择redis做缓存还是缓存到本地，自动进行识别装配
- 关于[tianai-captcha](https://gitee.com/tianai/tianai-captcha)

## qq群: 1021884609
## 微信群: ![](https://minio.tianai.cloud/public/qun2.jpg?t=12313)
