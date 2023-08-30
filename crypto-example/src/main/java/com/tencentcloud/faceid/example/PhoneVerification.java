package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.Encryption;
import com.tencentcloudapi.faceid.v20180301.models.PhoneVerificationRequest;
import com.tencentcloudapi.faceid.v20180301.models.PhoneVerificationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机号三要素核验
 */
public class PhoneVerification {

    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.AES256CBC; // 该接口仅支持 Algorithm.AES256CBC 加密算法


    public static void main(String[] args) throws Exception {
        // Step 1. 组装加密参数并对敏感数据加密
        Map<String, String> m = new HashMap<>();
        m.put("Phone", "13800000000");
        m.put("Name", "张三");
        m.put("IdCard", "340103202308176095");
        String reqJson = JSON.toJSONString(CryptoUtil.encrypt(algorithm, null, m));
        System.out.println("req json: " + reqJson);

        // Step 2. 使用Tencent Cloud API SDK组装请求体，填充参数
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient faceidClient = new FaceidClient(credential, REGION);
        PhoneVerificationRequest request = PhoneVerificationRequest.fromJsonString(reqJson, PhoneVerificationRequest.class);
        JSONObject jsonObject = JSONObject.parseObject(reqJson);
        Encryption encryption = JSONObject.parseObject(JSONObject.toJSONString(jsonObject.get("Encryption")), Encryption.class);
        request.setCiphertextBlob(encryption.getCiphertextBlob());
        request.setEncryptList(encryption.getEncryptList());
        request.setIv(encryption.getIv());

        // Step 3. 调用接口
        PhoneVerificationResponse response = faceidClient.PhoneVerification(request);
        System.out.println("SDK response: " + PhoneVerificationResponse.toJsonString(response));
    }


}
