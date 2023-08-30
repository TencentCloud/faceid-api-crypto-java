package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.PhoneVerificationCMCCRequest;
import com.tencentcloudapi.faceid.v20180301.models.PhoneVerificationCMCCResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机号三要素核验（移动）
 */
public class PhoneVerificationCMCC {

    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.SM4GCM; // 选择加密算法  Algorithm.AES256CBC、Algorithm.SM4GCM


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
        PhoneVerificationCMCCRequest request = PhoneVerificationCMCCRequest.fromJsonString(reqJson, PhoneVerificationCMCCRequest.class);

        // Step 3. 调用接口
        PhoneVerificationCMCCResponse response = faceidClient.PhoneVerificationCMCC(request);
        System.out.println("SDK response: " + PhoneVerificationCMCCResponse.toJsonString(response));
    }


}
