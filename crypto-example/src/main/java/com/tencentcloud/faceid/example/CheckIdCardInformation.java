package com.tencentcloud.faceid.example;

import com.alibaba.fastjson.JSON;
import com.tencentcloud.faceid.CryptoUtil;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.core.CryptoProvider;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.CheckIdCardInformationRequest;
import com.tencentcloudapi.faceid.v20180301.models.CheckIdCardInformationResponse;

/**
 * 身份证人像照片验真
 */
public class CheckIdCardInformation {

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

        // Step 3. TODO 根据您的业务需要，设置请求参数，详情参考api文档：https://cloud.tencent.com/document/product/1007/47276
        CheckIdCardInformationRequest request = AbstractModel.fromJsonString(ciphertextDesc, CheckIdCardInformationRequest.class);
        request.setImageBase64("");
        request.setIsEncryptResponse(true);

        // Step 4. 发起调用
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient client = new FaceidClient(credential, REGION);
        CheckIdCardInformationResponse response = client.CheckIdCardInformation(request);
        System.out.println("ciphertext response: " + AbstractModel.toJsonString(response));

        // Step 5. 解密响应体
        String plaintext;
        plaintext = CryptoUtil.bodyDecrypt(algorithm, key, response.getEncryption().getIv(),
                response.getEncryption().getTagList(), response.getEncryptedBody());
        response = AbstractModel.fromJsonString(plaintext, CheckIdCardInformationResponse.class);
        System.out.println("plaintext response: " + AbstractModel.toJsonString(response));
    }
}
