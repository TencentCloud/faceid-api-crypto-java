package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.DetectAuthRequest;
import com.tencentcloudapi.faceid.v20180301.models.DetectAuthResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 实名核身鉴权
 */
public class DetectAuth {

    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.SM4GCM; // 选择加密算法  Algorithm.AES256CBC、Algorithm.SM4GCM


    public static void main(String[] args) throws Exception {
        // Step 1. 组装加密参数并对敏感数据加密
        Map<String, String> m = new HashMap<>();
        m.put("IdCard", "28618593937376473abc");
        m.put("Name", "张三");
        String reqJson = JSON.toJSONString(CryptoUtil.encrypt(algorithm, null, m));
        System.out.println("req json: " + reqJson);

        // Step 2. 使用Tencent Cloud API SDK组装请求体，填充参数
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient faceidClient = new FaceidClient(credential, REGION);
        DetectAuthRequest request = DetectAuthRequest.fromJsonString(reqJson, DetectAuthRequest.class);

        // Step 3. TODO 根据您的业务需要，设置其他参数，详情参考api文档：https://cloud.tencent.com/document/product/1007/31816
        request.setRuleId("");
        request.setRedirectUrl("");

        // Step 4. 调用接口
        DetectAuthResponse response = faceidClient.DetectAuth(request);
        System.out.println("SDK response: " + DetectAuthResponse.toJsonString(response));
    }


}
