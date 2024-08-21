package com.tencentcloud.faceid;

import com.tencentcloud.faceid.core.CryptoProvider;
import com.tencentcloud.faceid.core.Algorithm;
import com.tencentcloud.faceid.core.Encryption;

import java.util.*;
import java.util.Map.Entry;


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

    /**
     * HTTP Body 加密
     *
     * @param algorithm 算法
     * @param key       对称密钥
     * @param reqBody   HTTP Body明文
     * @return 加密后的数据
     */
    public static Map<String, Object> bodyEncrypt(Algorithm algorithm, String key, String reqBody) throws Exception {
        // 生成对称密钥
        key = isBlank(key) ? CryptoProvider.generateKey(algorithm) : key;
        // 生成随机IV
        byte[] iv = CryptoProvider.generateIv(algorithm);
        Map<String, Object> map = new LinkedHashMap<>();
        if (!isBlank(reqBody)) {
            List<String> tagList = new ArrayList<>();
            CryptoProvider.CiphertextEntity entity = CryptoProvider.encryptData(algorithm, key.getBytes(), reqBody.getBytes(), iv);
            if (algorithm == Algorithm.SM4GCM) {
                tagList.add(Base64.getEncoder().encodeToString(entity.tag));
            }
            Encryption encryption = new Encryption();
            encryption.setAlgorithm(algorithm.getValue()); // 指定加密算法
            encryption.setEncryptList(Collections.singletonList("EncryptionBody")); // 指定加密的字段名
            encryption.setCiphertextBlob(CryptoProvider.encryptKey(algorithm, key)); // 加密的对称密钥
            encryption.setIv(Base64.getEncoder().encodeToString(iv)); // 初始向量
            encryption.setTagList(tagList); // SM4GCM算法生成的验证消息

            map.put("Encryption", encryption);
            map.put("EncryptedBody", entity.ciphertext);
            return map;
        }

        Encryption encryption = new Encryption();
        encryption.setAlgorithm(algorithm.getValue()); // 指定加密算法
        encryption.setEncryptList(null); // 指定加密的字段名
        encryption.setCiphertextBlob(CryptoProvider.encryptKey(algorithm, key)); // 加密的对称密钥
        encryption.setIv(Base64.getEncoder().encodeToString(iv)); // 初始向量
        encryption.setTagList(null); // SM4GCM算法生成的验证消息
        map.put("Encryption", encryption);
        return map;
    }


    /**
     * HTTP Body 解密
     *
     * @param algorithm 算法
     * @param key       对称密钥
     * @param respBody  HTTP Body密文
     * @return 解密后的数据
     */
    public static String bodyDecrypt(Algorithm algorithm, String key, String iv, String[] tag, String respBody) throws Exception {
        if (isBlank(key) || isBlank(respBody)) {
            throw new RuntimeException("parameter error.");
        }
        if (algorithm == Algorithm.SM4GCM) {
            if (tag.length != 1 || isBlank(tag[0])) {
                throw new RuntimeException("parameter error.");
            }
        }
        byte[] plaintext = CryptoProvider.decryptData(algorithm, key.getBytes(),
                Base64.getDecoder().decode(respBody),
                Base64.getDecoder().decode(iv),
                Base64.getDecoder().decode(tag[0]));

        if (plaintext == null) {
            return "";
        }
        return new String(plaintext);
    }

    private static boolean isBlank(String str) {
        return str == null || str.equals("");
    }


}
