package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.core.CryptoProvider;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.GetDetectInfoEnhancedRequest;
import com.tencentcloudapi.faceid.v20180301.models.GetDetectInfoEnhancedResponse;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取实名核身结果信息增强版
 */
public class GetDetectInfoEnhanced {

    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.SM4GCM; // 选择加密算法  Algorithm.AES256CBC、Algorithm.SM4GCM

    public static void main(String[] args) throws Exception {
        // Step 1. 生成对称密钥，用于加解密敏感信息
        String key = CryptoProvider.generateKey(algorithm);

        // Step 2. 组装加密参数并对敏感数据加密
        String reqJson = JSON.toJSONString(CryptoUtil.encrypt(algorithm, key, null));
        System.out.println(reqJson);

        // Step 3. 使用Tencent Cloud API SDK发起调用
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient faceidClient = new FaceidClient(credential, REGION);
        GetDetectInfoEnhancedRequest request = GetDetectInfoEnhancedRequest.fromJsonString(reqJson, GetDetectInfoEnhancedRequest.class);

        // Step 4. TODO 根据您的业务需要，设置其他参数，详情参考api文档：https://cloud.tencent.com/document/product/1007/41957
        request.setRuleId("");
        request.setInfoType("1");
        request.setBizToken("");

        // Step 5. 调用接口
        GetDetectInfoEnhancedResponse response = faceidClient.GetDetectInfoEnhanced(request);
        System.out.println("SDK response: " + GetDetectInfoEnhancedResponse.toJsonString(response));

        // Step 6. 组装需要解密的参数
        Map<String, String> m = new LinkedHashMap<>();
        m.put("Response.Text.IdCard", response.getText().getIdCard());
        m.put("Response.Text.Name", response.getText().getName());
        m.put("Response.Text.OcrIdCard", response.getText().getOcrIdCard());
        m.put("Response.Text.OcrName", response.getText().getOcrName());
        m.put("Response.Text.LivenessDetail[0].Idcard", response.getText().getLivenessDetail()[0].getIdcard());
        m.put("Response.Text.LivenessDetail[0].Name", response.getText().getLivenessDetail()[0].getName());
        List<String> tagList = Arrays.asList(response.getEncryption().getTagList());
        String iv = response.getEncryption().getIv();

        // Step 7. 解密接敏感信息
        Map<String, String> resultMap = CryptoUtil.decrypt(algorithm, key, iv, tagList, m);
        resultMap.forEach((k, v) -> System.out.printf("key:%s value:%s \n", k, v));
    }

}
