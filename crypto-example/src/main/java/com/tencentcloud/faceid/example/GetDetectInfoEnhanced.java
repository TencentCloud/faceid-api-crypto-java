package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.core.CryptoProvider;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.DetectAuthResponse;
import com.tencentcloudapi.faceid.v20180301.models.GetDetectInfoEnhancedRequest;
import com.tencentcloudapi.faceid.v20180301.models.GetDetectInfoEnhancedResponse;

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

        // Step 2. 生成加密参数
        String ciphertextDesc = JSON.toJSONString(CryptoUtil.bodyEncrypt(algorithm, key, null));
        System.out.println("ciphertext desc: " + JSON.toJSONString(ciphertextDesc));

        // Step 3. TODO 根据您的业务需要，设置请求参数，详情参考api文档：https://cloud.tencent.com/document/product/1007/31816
        GetDetectInfoEnhancedRequest request = AbstractModel.fromJsonString(ciphertextDesc, GetDetectInfoEnhancedRequest.class);
        request.setRuleId("1");
        request.setInfoType("13");
        request.setBizToken("95F6C73E-C533-47A2-A7CF-9B3A319F91");
        request.setIsEncryptResponse(true); // true表示对response的全报文内容加密

        // Step 5. 发起调用
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient client = new FaceidClient(credential, REGION);
        GetDetectInfoEnhancedResponse response = client.GetDetectInfoEnhanced(request);
        System.out.println("ciphertext response: " + AbstractModel.toJsonString(response));

        // Step 6. 解密响应体
        String plaintext = CryptoUtil.bodyDecrypt(algorithm, key, response.getEncryption().getIv(),
                response.getEncryption().getTagList(), response.getEncryptedBody());
        response = AbstractModel.fromJsonString(plaintext, GetDetectInfoEnhancedResponse.class);
        System.out.println("plaintext response: " + AbstractModel.toJsonString(response));
    }

}


