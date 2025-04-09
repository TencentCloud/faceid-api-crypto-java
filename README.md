## API加解密DEMO

### 引入依赖

#### 公共依赖

```xml
# 腾讯云SDK的版本必须在3.1.1086以上
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-sdk-java</artifactId>
    <version>3.1.1086</version>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcpkix-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
```

#### 人脸核身加解密SDK

下载最新的[release](https://github.com/TencentCloud/faceid-api-crypto-java/releases)版本jar包，并在项目工程中引入。
参考下方的接口Demo实现敏感信息加解密功能。

### 接口敏感信息加解密DEMO

实名核身鉴权
[DetectAuth](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FDetectAuth.java)

获取实名核身结果信息增强版
[GetDetectInfoEnhanced](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FGetDetectInfoEnhanced.java)

获取E证通Token
[GetFaceIdToken](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FGetFaceIdToken.java)

照片人脸核身
[ImageRecognition](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FImageRecognition.java)

银行卡四要素核验
[BankCard4EVerification](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FBankCard4EVerification.java)

银行卡三要素核验
[BankCardVerification](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FBankCardVerification.java)

银行卡基础信息查询
[CheckBankCardInformation](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FCheckBankCardInformation.java)

身份信息及有效期核验
[CheckIdNameDate](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FCheckIdNameDate.java)

手机号二要素核验
[CheckPhoneAndName](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FCheckPhoneAndName.java)

身份证识别及信息核验
[IdCardOCRVerification](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FIdCardOCRVerification.java)

身份证二要素核验
[IdCardVerification](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FIdCardVerification.java)

手机号在网时长核验
[MobileNetworkTimeVerification](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FMobileNetworkTimeVerification.java)

手机号状态查询
[MobileStatus](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FMobileStatus.java)

手机号三要素核验
[PhoneVerification](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FPhoneVerification.java)

手机号三要素核验（移动）
[PhoneVerificationCMCC](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FPhoneVerificationCMCC.java)

手机号三要素核验（电信）
[PhoneVerificationCTCC](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FPhoneVerificationCTCC.java)

手机号三要素核验（联通）
[PhoneVerificationCUCC](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FPhoneVerificationCUCC.java)

身份证人像照片验真
[CheckIdCardInformation](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FCheckIdCardInformation.java)

AI人脸防护盾
[DetectAIFakeFaces](crypto-example%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftencentcloud%2Ffaceid%2Fexample%2FDetectAIFakeFaces.java)