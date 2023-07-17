API加解密SDK说明

### 1. 引入依赖

```
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
<dependency>
     <groupId>com.jayway.jsonpath</groupId>
     <artifactId>json-path</artifactId>
     <version>2.8.0</version>
</dependency>

```

### 2. SDK初始化

```
// publicKey：登陆人脸核身控制台获取公钥，如果使用AES-256-CBC算法，公钥选择RSA公钥；如果使用SM4-GCM算法，公钥选择SM2公钥
// algorithm：指定加密算法（AES-256-CBC或SM4GCM）
// keyExpireTime：对称密钥过期时间，在过期时间内生成的对称密钥可复用。0表示不复用密钥
APICryptoTool tool = new APICryptoTool(publicKey, Algorithm.AES256CBC, 0);
```

### 3. 入参加密

```
// reqBody：明文请求参数
// fields：要加密的字段列表
// res[0]:加密的请求参数
// res[1]:对称密钥明文 
List<String> res = tool.encrypt(reqBody, fields)

example:
输入：
reqBody := `{
    "Action": "BankCardVerification",
    "Version": "2018-03-01",
    "IdCard": "621103145623471011",
    "Name": "张三",
    "BankCard": "6214865712375011",
    "CertType": 0
}`
List<String> fields = new ArrayList<String>();
fields.Add("IdCard");
fields.Add("Name");

List<String> res = tool.encrypt(reqBody, fields)
list.forEach(System.out::println);
```

### 4. 出参加密

```
// reqBody：明文请求参数
// fields：要加密的字段列表
// res[0]:加密的请求参数
// res[1]:对称密钥明文 
List<String> res = tool.encrypt(reqBody, null)

// rspBody：接口响应
// plaintextKey：对称密钥明文
// resp：解密后的明文响应
String resp = tool.decrypt(rspBody, plaintextKey)
example:
{
  "Action": "GetDetectInfoEnhanced",
  "RuleId": '2',
  "BizToken": '37C8960C-4673-4152-8122-1433C305C144'
}
List<String> res = tool.encrypt(reqBody, fields)
String encryptReq = res.get(0);
String plaintextKey = res.get(1);

// 发送请求获得回包rsp
// 此处mock一个，rsp一定带有Encryption字段
rsp = {
  Response: {
    "Encryption": {
      "Algorithm": "AES-256-CBC",
      "CiphertextBlob": "DCaa541gYPA8ybDaAasY4C17K5CHo3s8/ZDNsaS8hH8Gr+qnA9RY53QswVOY4smcJsv5ToXPN6qOqruT9QVw5VPVglQ5YO60RjWabZKA+sF3BxDRMmrnuTKMNPwswen1mG4SfotyJ4IVv4PHomPZwzlZtGjm0CkXvgmnaHLxkck=",
      "EncryptList": [
        "Response.Text.IdCard",
        "Response.Text.Name",
      ],
      "Iv": "vTjCqg1Xz6Lh0pJZCNjAAQ==",
      "TagList": [],
    },
    "RequestId": "d55782f3-dc0f-4484-a067-ff2046fe659e",
    "Text": {
      "IdCard": "8TEJyC4YWALmK5U9cw+R+1Rvs4LuNRAAm8LQkwrJEa4=",
      "Name": "QR3meQHDzArXCIuJIyETLzRtOjg0vjRxcYdKQTOE7vw=",
    },
  }
}

String resp = tool.decrypt(rsp, plaintextKey)
System.out.print(resp);
```

### 5. 出入参都加密

```
// reqBody：明文请求参数
// fields：要加密的字段列表
// res[0]:加密的请求参数
// res[1]:对称密钥明文 
List<String> res = tool.encrypt(reqBody, fields)

// rspBody：接口响应
// plaintextKey：对称密钥明文
// resp：解密后的明文响应
String resp = tool.decrypt(rspBody, plaintextKey)

example:
req :={
  "IdCard": "440111111111111111",
  "Name": "爱新觉罗永琪",
  "RuleId": "2",
  "BizToken": "37C8960C-4673-4152-8122-1433C305C144"
}
List<String> fields = new ArrayList<String>();
fields.Add("IdCard");
fields.Add("Name");

List<String> res = tool.encrypt(reqBody, fields)
String encryptReq = res.get(0);
String plaintextKey = res.get(1);

// 发送请求获得回包
// 此处mock一个，rsp一定带有Encryption字段。
rsp = {
  "Response: {
    "Encryption: {
      "Algorithm: 'SM4-GCM',
      "CiphertextBlob: 'BC3JNqinBaASuOhjP/WCkrCgtLm03d/stJMh1QgPKfdFoVdpySbZNah6iUIhoSI+EPML8dDgXJE2wkSZv8x029v+t2VoC6Lc6RW1gowi2tqwz2SNmb4qN/VrqMi1a3m/T3gXY42AbvORP90Jxqgr3hE=',
      "EncryptList: [
        "Response.Text.IdCard",
        "Response.Text.Name",
      ],
      "Iv": "cHNm8k09p2d80owr",
      "TagList": [
        "meBiloynTRhQtOtLR2xccQ==",
        "Anrq6V9s4jwBg+/mxW9Zeg==",
      ],
    },
    "Text: {
      "IdCard": "oUfaRWLLjR9MclkyFF68M7Ot",
      "Name": "cvtbksVKVIn0pNWUw9815RI2",
    }
  }
};

String resp = tool.decrypt(rsp, plaintextKey)
System.out.print(resp);
```



