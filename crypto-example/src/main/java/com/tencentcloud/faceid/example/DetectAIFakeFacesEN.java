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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DetectAIFakeFaces {
    private static final String REGION = "ap-jakarta";// TODO Region
    private static final String SECRET_ID = ""; // TODO Tencent Cloud Secret ID
    private static final String SECRET_KEY = ""; // TODO Tencent Cloud Secret Key
    private static final Algorithm algorithm = Algorithm.AES256CBC; // Select encryption algorithm: Algorithm.AES256CBC, Algorithm.SM4GCM

    /**
     * Read image file and convert to base64 string
     * @param imagePath Image file path
     * @return base64 encoded image string
     * @throws IOException File read exception
     */
    public static String readImageAsBase64(String imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static void main(String[] args) throws Exception {
        // Step 1. Generate symmetric key for encrypting/decrypting sensitive information
        String key = CryptoProvider.generateKey(algorithm);

        // Read image file
        String base64Image = "";
        try {
            base64Image = readImageAsBase64("");// TODO Input the image or video with face for detection, encoded in base64 format.
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
            return;
        }

        // Step 2. Assemble encryption parameters and encrypt sensitive data. Set request parameters according to your business needs. For details, refer to API documentation: https://cloud.tencent.com/document/product/1007/101561
        Map<String, Object> m = new HashMap<>();
        m.put("FaceInput", base64Image); // TODO Input the image or video with face for detection, encoded in base64 format.
        m.put("FaceInputType", 1L); // TODO Input type. 1: Image type. 2: Video type.
        String reqJson = JSON.toJSONString(m);
        System.out.println("req json: " + reqJson);

        // Step 3. Generate encrypted request parameters
        String ciphertextDesc = JSON.toJSONString(CryptoUtil.bodyEncrypt(algorithm, key, reqJson));
        DetectAIFakeFacesRequest request = AbstractModel.fromJsonString(ciphertextDesc, DetectAIFakeFacesRequest.class);
        System.out.println("ciphertext desc: " + JSON.toJSONString(request));

        // Step 4. Initiate API call
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        FaceidClient client = new FaceidClient(credential, REGION);
        DetectAIFakeFacesResponse response = client.DetectAIFakeFaces(request);
        System.out.println("ciphertext response: " + AbstractModel.toJsonString(response));
    }
}