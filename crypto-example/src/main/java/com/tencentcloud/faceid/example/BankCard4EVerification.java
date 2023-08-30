package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.BankCard4EVerificationRequest;
import com.tencentcloudapi.faceid.v20180301.models.BankCard4EVerificationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 银行卡四要素核验
 */
public class BankCard4EVerification {

    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.SM4GCM; // TODO 选择加密算法  Algorithm.AES256CBC、Algorithm.SM4GCM


    public static void main(String[] args) throws Exception {
        // Step 1. 组装加密参数并对敏感数据加密
        Map<String, String> m = new HashMap<>();
        m.put("BankCard", "9802897613438987");
        m.put("Name", "张三");
        m.put("IdCard", "340103202308176095");
        m.put("Phone", "13800000000");
        String reqJson = JSON.toJSONString(CryptoUtil.encrypt(algorithm, null, m));
        System.out.println("req json: " + reqJson);

        // Step 2. 使用Tencent Cloud API SDK组装请求体，填充参数
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient faceidClient = new FaceidClient(credential, REGION);
        BankCard4EVerificationRequest request = BankCard4EVerificationRequest.fromJsonString(reqJson, BankCard4EVerificationRequest.class);

        // Step 3. TODO 根据您的业务需要，设置其他参数，详情参考api文档：https://cloud.tencent.com/document/product/1007/35775

        // Step 4. 调用接口
        BankCard4EVerificationResponse response = faceidClient.BankCard4EVerification(request);
        System.out.println("SDK response: " + BankCard4EVerificationResponse.toJsonString(response));
    }


}
