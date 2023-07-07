package com.tencentcloud.faceid;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.*;

public class APICryptoTool {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private PublicKey rsaPublicKey;
    private PublicKey sm2PublicKey;
    private KeyCache cache;
    private final Algorithm algorithm;
    private final int keyExpireTime;


    public APICryptoTool(String publicKey, Algorithm algorithm, int keyExpireTime) throws Exception {
        if (Objects.equals(publicKey, "")) {
            throw new Exception("public key is illegal");
        }
        String pem = read(publicKey);
        switch (algorithm) {
            case AES256CBC:
                this.rsaPublicKey = AES256CBC.loadRSAPublicKey(pem);
                break;
            case SM4GCM:
                this.sm2PublicKey = SM4GCM.loadSM2PublicKey(pem);
                break;
        }
        this.algorithm = algorithm;
        this.keyExpireTime = keyExpireTime;
        this.cache = new KeyCache();
    }


    public List<String> encrypt(String reqBody, List<String> fields) throws Exception {
        if (Objects.isNull(reqBody)) {
            return null;
        }
        if (fields == null) {
            fields = new ArrayList<>();
        }
        DocumentContext context = JsonPath.parse(reqBody);
        Map<String, String> params = new HashMap<>();
        fields.forEach(field -> {
            String val = context.read("$." + field);
            params.put(field, val);
        });

        Encryption encryption = new Encryption();
        encryption.encryptList = new ArrayList<>();
        encryption.tagList = new ArrayList<>();

        // 从缓存读取密钥
        String key = "";
        String encryptionKey = "";
        if (this.keyExpireTime > 0 && this.cache != null) {
            if (System.currentTimeMillis() - this.cache.getTimestamp() <= this.keyExpireTime) {
                key = this.cache.getPlaintextKey();
                encryptionKey = this.cache.getCiphertextKey();
            }
        }

        // 生成新的密钥
        if (Objects.equals(key, "") || Objects.equals(encryptionKey, "")) {
            // 生成对称密钥
            key = randomString(algorithm == Algorithm.AES256CBC ? 32 : 16);
            // 加密对称密钥
            byte[] ciphertextKey = encryptKey(key);
            encryptionKey = Base64.getEncoder().encodeToString(ciphertextKey);
            KeyCache c = new KeyCache();
            c.setCiphertextKey(encryptionKey);
            c.setPlaintextKey(key);
            c.setTimestamp(System.currentTimeMillis());
            this.cache = c;
        }
        // 生成随机iv
        byte[] iv = ivRandom(this.algorithm == Algorithm.AES256CBC ? 16 : 12);
        // 加密数据
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Map<String, byte[]> map = encryptData(key.getBytes(StandardCharsets.UTF_8), entry.getValue().getBytes(StandardCharsets.UTF_8), iv);
            byte[] ciphertext = map.getOrDefault("ciphertext", null);
            byte[] tag = map.getOrDefault("tag", null);
            context.set("$." + entry.getKey(), Base64.getEncoder().encodeToString(ciphertext));
            encryption.encryptList.add(entry.getKey());
            if (tag != null) {
                encryption.tagList.add(Base64.getEncoder().encodeToString(tag));
            }
        }

        encryption.setIv(Base64.getEncoder().encodeToString(iv));
        encryption.setAlgorithm(algorithm.getValue());
        encryption.setCiphertextBlob(encryptionKey);

        context.put("$", "Encryption", encryption);

        List<String> result = new ArrayList<>();
        result.add(context.jsonString());
        result.add(key);
        return result;
    }

    public String decrypt(String rspBody, String plaintextKey) throws Exception {
        DocumentContext context = JsonPath.parse(rspBody);
        LinkedHashMap<String, Object> map = context.read("$.Response.Encryption");
        Encryption encryption = new Encryption();
        encryption.algorithm = (String) map.get("Algorithm");
        encryption.ciphertextBlob = (String) map.get("CiphertextBlob");
        encryption.encryptList = (List<String>) map.get("EncryptList");
        encryption.iv = (String) map.get("Iv");
        encryption.tagList = (List<String>) map.get("TagList");

        List<String> encryptList = encryption.encryptList;
        for (int i = 0; i < encryptList.size(); i++) {
            String field = encryptList.get(i);
            String val = context.read("$." + field);
            if (Objects.equals(encryption.algorithm, Algorithm.AES256CBC.getValue())) {
                byte[] iv = Base64.getDecoder().decode(encryption.iv);
                byte[] ciphertext = Base64.getDecoder().decode(val);
                byte[] plaintext = AES256CBC.aesDecrypt(plaintextKey.getBytes(), ciphertext, iv);
                context.set("$." + field, new String(plaintext));
            }

            if (Objects.equals(encryption.algorithm, Algorithm.SM4GCM.getValue())) {
                if (encryption.tagList.size() != encryption.encryptList.size()) {
                    throw new RuntimeException("encryption parameter value error");
                }
                byte[] iv = Base64.getDecoder().decode(encryption.iv);
                byte[] ciphertext = Base64.getDecoder().decode(val);
                byte[] tag = Base64.getDecoder().decode(encryption.tagList.get(i));
                byte[] plaintext = SM4GCM.sm4Decrypt(plaintextKey.getBytes(), ciphertext, iv, tag);
                context.set("$." + field, new String(plaintext));
            }
        }
        return context.jsonString();
    }


    private byte[] encryptKey(String key) throws Exception {
        switch (this.algorithm) {
            case AES256CBC:
                if (this.rsaPublicKey == null) {
                    throw new Exception("rsa public key not initialized");
                }
                return AES256CBC.rsaEncrypt(key.getBytes(StandardCharsets.UTF_8), this.rsaPublicKey);
            case SM4GCM:
                if (this.sm2PublicKey == null) {
                    throw new Exception("sm2 public key not initialized");
                }
                return SM4GCM.sm2Encrypt(key.getBytes(StandardCharsets.UTF_8), (BCECPublicKey) this.sm2PublicKey);
        }
        return null;
    }

    private Map<String, byte[]> encryptData(byte[] key, byte[] plaintext, byte[] iv) throws Exception {
        Map<String, byte[]> map = new HashMap<>();
        byte[] ciphertext = null;
        byte[] tag = null;
        switch (this.algorithm) {
            case AES256CBC:
                ciphertext = AES256CBC.aesEncrypt(key, plaintext, iv);
                break;
            case SM4GCM:
                ciphertext = SM4GCM.sm4Encrypt(key, plaintext, iv);
                tag = new byte[16];
                System.arraycopy(ciphertext, ciphertext.length - tag.length, tag, 0, tag.length);
                break;
            default:
        }
        map.put("ciphertext", ciphertext);
        map.put("tag", tag);
        return map;
    }

    private static byte[] ivRandom(int n) {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[n];
        random.nextBytes(iv);
        return iv;
    }


    static class KeyCache {

        private String plaintextKey;
        private String ciphertextKey;
        private long timestamp;

        public String getPlaintextKey() {
            return plaintextKey;
        }

        public void setPlaintextKey(String plaintextKey) {
            this.plaintextKey = plaintextKey;
        }

        public String getCiphertextKey() {
            return ciphertextKey;
        }

        public void setCiphertextKey(String ciphertextKey) {
            this.ciphertextKey = ciphertextKey;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    static class Encryption {
        List<String> encryptList;// 加密的字段名称
        String ciphertextBlob;   // 加密后的对称密钥
        String iv;   // 初始向量
        String algorithm;   // 加密算法
        List<String> tagList; // 消息摘要

        public List<String> getEncryptList() {
            return encryptList;
        }

        public void setEncryptList(List<String> encryptList) {
            this.encryptList = encryptList;
        }

        public String getCiphertextBlob() {
            return ciphertextBlob;
        }

        public void setCiphertextBlob(String ciphertextBlob) {
            this.ciphertextBlob = ciphertextBlob;
        }

        public String getIv() {
            return iv;
        }

        public void setIv(String iv) {
            this.iv = iv;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public List<String> getTagList() {
            return tagList;
        }

        public void setTagList(List<String> tagList) {
            this.tagList = tagList;
        }
    }

    public static String read(String pem) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(pem))));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("-----")) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}