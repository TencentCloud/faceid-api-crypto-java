package com.tencentcloud.faceid;

import com.tencentcloud.faceid.core.CryptoProvider;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.core.Encryption;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;


public class CryptoUtil {


    public static Map<String, Object> encrypt(Algorithm algorithm, String key, Map<String, String> args) throws Exception {
        // 生成对称密钥
        key = isBlank(key) ? CryptoProvider.generateKey(algorithm) : key;
        // 生成随机IV
        byte[] iv = CryptoProvider.generateIv(algorithm);

        List<String> encryptList = new ArrayList<>();
        List<String> tagList = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<>();
        if (args != null) {
            for (Entry<String, String> entry : args.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                encryptList.add(k);
                CryptoProvider.CiphertextEntity entity = CryptoProvider.encryptData(algorithm, key.getBytes(), v.getBytes(), iv);
                if (!Objects.isNull(entity.tag)) {
                    tagList.add(Base64.getEncoder().encodeToString(entity.tag));
                }
                map.put(k, Base64.getEncoder().encodeToString(entity.ciphertext));
            }
        }

        // 组装Encryption对象
        Encryption encryption = new Encryption();
        encryption.setAlgorithm(algorithm.getValue()); // 指定加密算法
        encryption.setEncryptList(encryptList); // 指定加密的字段名
        encryption.setCiphertextBlob(CryptoProvider.encryptKey(algorithm, key)); // 加密的对称密钥
        encryption.setIv(Base64.getEncoder().encodeToString(iv)); // 初始向量
        encryption.setTagList(tagList); // SM4GCM算法生成的验证消息

        map.put("Encryption", encryption);
        return map;

    }


    public static Map<String, String> decrypt(Algorithm algorithm, String key, String iv, List<String> tagList, Map<String, String> args) throws Exception {
        if (args == null) {
            throw new RuntimeException("parameter error.");
        }
        if (algorithm == Algorithm.SM4GCM) {
            if (tagList == null || tagList.size() != args.size()) {
                throw new RuntimeException("parameter error.");
            }
        }
        Map<String, String> map = new LinkedHashMap<>();
        List<String> keys = new ArrayList<>(args.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            String v = args.get(k);
            String tag = null;
            if (algorithm == Algorithm.SM4GCM) {
                tag = tagList.get(i);
            }
            byte[] plaintext = CryptoProvider.decryptData(algorithm, key.getBytes(), Base64.getDecoder().decode(v),
                    Base64.getDecoder().decode(iv),
                    Base64.getDecoder().decode(tag));
            if (plaintext != null) {
                map.put(k, new String(plaintext));
            }
        }
        return map;
    }

    private static boolean isBlank(String str) {
        return str == null || str.equals("");
    }


}
