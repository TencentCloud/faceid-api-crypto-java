package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.ImageRecognitionRequest;
import com.tencentcloudapi.faceid.v20180301.models.ImageRecognitionResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 照片人脸核身
 */
public class ImageRecognition {
    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.AES256CBC; // 选择加密算法  Algorithm.AES256CBC、Algorithm.SM4GCM


    public static void main(String[] args) throws Exception {
        // Step 1. 组装加密参数并对敏感数据加密
        Map<String, String> m = new HashMap<>();
        m.put("IdCard", "340103202308176095");
        m.put("Name", "张三");
        String reqJson = JSON.toJSONString(CryptoUtil.encrypt(algorithm, null, m));
        System.out.println("req json: " + reqJson);

        // Step 2. 使用Tencent Cloud API SDK组装请求体，填充参数
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient faceidClient = new FaceidClient(credential, REGION);
        ImageRecognitionRequest request = ImageRecognitionRequest.fromJsonString(reqJson, ImageRecognitionRequest.class);

        // Step 3. TODO 根据您的业务需要，设置其他参数，详情参考api文档：https://cloud.tencent.com/document/product/1007/31820
        request.setImageBase64("xxx");

        // Step 4. 调用接口
        ImageRecognitionResponse response = faceidClient.ImageRecognition(request);
        System.out.println("SDK response: " + ImageRecognitionResponse.toJsonString(response));
    }


}
