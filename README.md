# 一、项目介绍
yqfw-common-third-ali是云祺框架的阿里云服务接入模块，提供了阿里云OSS对象存储、支付宝支付、阿里云短信、阿里云RAM权限管理、阿里云直播等服务的统一接入封装。该模块基于Spring Boot开发，提供了简洁易用的API接口，帮助开发者快速集成阿里云相关服务。

# 二、项目结构
## 1. 通用结构
第三方模块通用结构如下，每个子模块遵循统一的包及类命名规范：
```
yqfw-common-third-{第三方名}
└── src/main/java
    └── cn.jzyunqi.common.third.{第三方名} # 第三方主包名
        ├── common #模块通用包
        │   ├── constant #模块通用常量包
        │   ├── enums #模块通用枚举包
        │   ├── model #模块通用模型包
        │   ├── utils #模块通用工具包
        │   ├── {第三方名}HttpExchange.java #模块通用切面注解类
        │   └── {第三方名}HttpExchangeWrapper.java #模块通用切面业务类
        └── {子模块名} #子模块
            │── {子模块业务名} #具体业务包，可以没有这一层
            │   ├── enums #业务枚举包
            │   ├── model #业务模型包
            │   ├── utils #业务工具包
            │   ├── A{第三方名}{子模块名}MsgCbController.java #第三方消息回调接口，抽象类，定义消息回调处理方法
            │   ├── {第三方名}{子模块名}{子模块业务名}Api.java #第三方API接口类，封装第三方API接口参数、返回结果及异常处理
            │   └── {第三方名}{子模块名}{子模块业务名}ApiProxy.java #第三方API接口代理类，负责调用第三方Api接口
            │── {第三方名}{子模块名}Auth.java #第三方账户信息类（AppID、AppSecret等）
            │── {第三方名}{子模块名}AuthHelper.java #第三方账户供应助手接口（由业务侧实现，提供认证信息）
            │── {第三方名}{子模块名}Client.java #第三方客户端类（对外暴露服务调用入口）
            └── {第三方名}{子模块名}Config.java #第三方客户端配置类（注册Bean到Spring容器）
```

## 2. 本项目结构
```
yqfw-common-third-ali
└── src/main/java
    └── cn.jzyunqi.common.third.ali
        ├── common #模块通用包
        │   ├── constant #模块通用常量包
        │   └── model #模块通用模型包
        │── live #阿里云直播子模块
        │   │── enums #枚举包
        │   │── AliLiveAuth.java #直播账户信息
        │   │── AliLiveAuthHelper.java #直播账户供应助手接口
        │   │── AliLiveClient.java #直播客户端
        │   └── AliLiveConfig.java #直播客户端配置类
        │── oss #阿里云对象存储子模块
        │   │── object #对象操作包
        │   │── AliOssAuth.java #OSS账户信息
        │   │── AliOssAuthHelper.java #OSS账户供应助手接口
        │   │── AliOssClient.java #OSS客户端
        │   └── AliOssConfig.java #OSS客户端配置类
        │── pay #支付宝支付子模块
        │   │── order #订单包
        │   │   ├── enums #枚举包
        │   │   └── model #模型类
        │   │── AliPayAuth.java #支付宝账户信息
        │   │── AliPayAuthHelper.java #支付宝账户供应助手接口
        │   │── AliPayClient.java #支付宝客户端
        │   └── AliPayConfig.java #支付宝客户端配置类
        │── ram #阿里云RAM权限管理子模块
        │   │── ims #IMS用户管理包
        │   │── ram #RAM角色管理包
        │   │── sts #STS临时授权包
        │   │   └── model #模型类
        │   │── AliRamAuth.java #RAM账户信息
        │   │── AliRamAuthHelper.java #RAM账户供应助手接口
        │   │── AliRamClient.java #RAM客户端
        │   └── AliRamConfig.java #RAM客户端配置类
        └── sms #阿里云短信子模块
            │── send #短信发送包
            │   ├── enums #枚举包
            │   └── model #模型类
            │── AliSmsAuth.java #短信账户信息
            │── AliSmsAuthHelper.java #短信账户供应助手接口
            │── AliSmsClient.java #短信客户端
            └── AliSmsConfig.java #短信客户端配置类
```

# 三、使用说明

## 1. 安装依赖
运行mvn clean install命令安装当前包，然后在个人项目中引入如下依赖：
```xml
<dependency>
    <groupId>cn.jzyunqi</groupId>
    <artifactId>yqfw-common-third-ali</artifactId>
    <version>${yqfw.version}</version>
</dependency>
```

## 2. 账号配置方法
以OSS为例，在个人项目中引入OSS配置如下：
```java
@Import({AliOssConfig.class})
```

配置OSS认证信息：
```java
@Bean
public AliOssAuthHelper aliOssAuthHelper() {
    return (clientId) -> ...; //根据clientId查询认证信息并返回AliOssAuth对象
}
```

## 3. 使用方法
### * OSS对象存储
```java
@Resource
private AliOssClient aliOssClient;

// 获取上传令牌
AliOssToken token = aliOssClient.uploadToken("user-id");

// 抓取远程文件到OSS
aliOssClient.obj.fetch("http://example.com/file.jpg", "bucket-name", "file.jpg");
```

### * 支付宝支付
引入支付宝配置：
```java
@Import({AliPayConfig.class})
```

配置认证信息：
```java
@Bean
public AliPayAuthHelper aliPayAuthHelper() {
    return (clientId) -> ...; //根据clientId查询认证信息并返回AliPayAuth对象
}
```

### * 阿里云短信
引入短信配置：
```java
@Import({AliSmsConfig.class})
```

配置认证信息：
```java
@Bean
public AliSmsAuthHelper aliSmsAuthHelper() {
    return (accessKeyId) -> ...; //根据accessKeyId查询认证信息并返回AliSmsAuth对象
}
```

发送短信：
```java
@Resource
private AliSmsClient aliSmsClient;

Map<String, String> params = new HashMap<>();
params.put("code", "123456");
aliSmsClient.sender.sendSms("access-key-id", "sign-name", "13800138000", "SMS_123456789", params);
```

### * 阿里云RAM
引入RAM配置：
```java
@Import({AliRamConfig.class})
```

配置认证信息：
```java
@Bean
public AliRamAuthHelper aliRamAuthHelper() {
    return (clientId) -> ...; //根据clientId查询认证信息并返回AliRamAuth对象
}
```