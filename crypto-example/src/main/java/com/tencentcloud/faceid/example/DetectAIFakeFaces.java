package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.core.CryptoProvider;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.DetectAIFakeFacesRequest;
import com.tencentcloudapi.faceid.v20180301.models.DetectAIFakeFacesResponse;

import java.util.HashMap;
import java.util.Map;

public class DetectAIFakeFaces {
    private static final String REGION = "ap-guangzhou";
    private static final String SECRET_ID = ""; // TODO 腾讯云密钥
    private static final String SECRET_KEY = ""; // TODO 腾讯云密钥
    private static final Algorithm algorithm = Algorithm.SM4GCM; // 选择加密算法  Algorithm.AES256CBC、Algorithm.SM4GCM


    public static void main(String[] args) throws Exception {
        // Step 1. 生成对称密钥，用于加解密敏感信息
        String key = CryptoProvider.generateKey(algorithm);

        // Step 2. 组装加密参数并对敏感数据加密，根据您的业务需要，设置请求参数，详情参考api文档：https://cloud.tencent.com/document/product/1007/101561
        Map<String, Object> m = new HashMap<>();
        m.put("FaceInput", "xxx"); // TODO  传入需要进行检测的带有人脸的图片或视频，使用base64编码的形式。
        m.put("FaceInputType", 1L); // TODO 传入的类型。1：传入的是图片类型。2：传入的是视频类型。
        String reqJson = JSON.toJSONString(m);
        System.out.println("req json: " + reqJson);

        // Step 3. 生成加密请求参数
        String ciphertextDesc = JSON.toJSONString(CryptoUtil.bodyEncrypt(algorithm, key, reqJson));
        DetectAIFakeFacesRequest request = AbstractModel.fromJsonString(ciphertextDesc, DetectAIFakeFacesRequest.class);
        System.out.println("ciphertext desc: " + JSON.toJSONString(request));

        // Step 4. 发起调用
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient client = new FaceidClient(credential, REGION);
        DetectAIFakeFacesResponse response = client.DetectAIFakeFaces(request);
        System.out.println("ciphertext response: " + AbstractModel.toJsonString(response));
    }
}
