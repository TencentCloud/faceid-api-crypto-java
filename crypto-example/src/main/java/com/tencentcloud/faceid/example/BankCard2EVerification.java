package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.BankCard2EVerificationRequest;
import com.tencentcloudapi.faceid.v20180301.models.BankCard2EVerificationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 银行卡二要素核验
 */
public class BankCard2EVerification {

    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.SM4GCM; // 选择加密算法  Algorithm.AES256CBC、Algorithm.SM4GCM


    public static void main(String[] args) throws Exception {
        // Step 1. 使用工具类对敏感报文进行加密
        Map<String, String> m = new HashMap<>();
        m.put("BankCard", "9802897613438987");
        m.put("Name", "张三");
        String reqJson = JSON.toJSONString(CryptoUtil.encrypt(algorithm, null, m));
        System.out.println("req json: " + reqJson);

        // Step 2. 使用Tencent Cloud API SDK组装请求体，填充参数
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient faceidClient = new FaceidClient(credential, REGION);
        BankCard2EVerificationRequest request = BankCard2EVerificationRequest.fromJsonString(reqJson, BankCard2EVerificationRequest.class);

        // Step 3. 调用接口
        BankCard2EVerificationResponse response = faceidClient.BankCard2EVerification(request);
        System.out.println("SDK response: " + BankCard2EVerificationResponse.toJsonString(response));
    }


}
