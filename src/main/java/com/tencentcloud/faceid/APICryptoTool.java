package com.tencentcloud.faceid;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Random;

public class APICryptoTool {

    private static final String SM2_KEY = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUZrd0V3WUhLb1pJemowQ0FRWUlLb0VjejFVQmdpMERRZ0FFeTJRamJLQzRMNkxKcHc1MW1qWDkwWDQxTllHYQpNcCtPR1g3ZUpCZnM4Szk4TU90S044d1BqajFpcUhVbFc2cXlQR2dnTlBJNVJHRW9BWGFvak9WeWNnPT0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0t";
    private static final String RAS_KEY = "LS0tLS1CRUdJTiBSU0EgUFVCTElDIEtFWS0tLS0tCk1JR0pBb0dCQU1SSm9hVWtaTjFkNU1wOEY1VjZpdFhtU0xOTTVaNzcxYWZheW9JNDlTbmRrRnRzc3BIUzQwMloKRVVVUFFmcWJ1WmsvVnVTaDU5THRBL2ZCS3piNEJQNWJWOGFxb2dWaEc0ZS9xK05Ea3dsYXEwaTMxSHdMeUJsYQpFb2pFL0VFSHBYQnN1RWtWVGJLRXk1ZWxScTl0b0w3SVo4MGkrSDJtdGZVNUNQc2FyK1IzQWdNQkFBRT0KLS0tLS1FTkQgUlNBIFBVQkxJQyBLRVktLS0tLQ==";


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private PublicKey rsaPublicKey;
    private PublicKey sm2PublicKey;
    private final Algorithm algorithm;


    public APICryptoTool(Algorithm algorithm) throws Exception {
        String pem;
        switch (algorithm) {
            case AES256CBC:
                pem = read(RAS_KEY);
                this.rsaPublicKey = AES256CBC.loadRSAPublicKey(pem);
                break;
            case SM4GCM:
                pem = read(SM2_KEY);
                this.sm2PublicKey = SM4GCM.loadSM2PublicKey(pem);
                break;
        }
        this.algorithm = algorithm;
    }


    public String encryptKey(String key) throws Exception {
        byte[] bytes;
        switch (this.algorithm) {
            case AES256CBC:
                if (this.rsaPublicKey == null) {
                    throw new Exception("rsa public key not initialized");
                }
                bytes = AES256CBC.rsaEncrypt(key.getBytes(StandardCharsets.UTF_8), this.rsaPublicKey);
                return Base64.getEncoder().encodeToString(bytes);
            case SM4GCM:
                if (this.sm2PublicKey == null) {
                    throw new Exception("sm2 public key not initialized");
                }
                bytes = SM4GCM.sm2Encrypt(key.getBytes(StandardCharsets.UTF_8), (BCECPublicKey) this.sm2PublicKey);
                return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }

    public CiphertextEntity encryptData(byte[] key, byte[] plaintext, byte[] iv) throws Exception {
        CiphertextEntity entity = new CiphertextEntity();
        switch (this.algorithm) {
            case AES256CBC:
                entity.ciphertext = AES256CBC.aesEncrypt(key, plaintext, iv);
                return entity;
            case SM4GCM:
                byte[] bytes = SM4GCM.sm4Encrypt(key, plaintext, iv);
                byte[] tag = new byte[16];
                System.arraycopy(bytes, bytes.length - tag.length, tag, 0, tag.length);
                byte[] ciphertext = new byte[bytes.length - tag.length];
                System.arraycopy(bytes, 0, ciphertext, 0, ciphertext.length);
                entity.ciphertext = ciphertext;
                entity.tag = tag;
                return entity;
            default:
                return null;
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

    public byte[] generateIv() {
        int n = this.algorithm == Algorithm.AES256CBC ? 16 : 12;
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[n];
        random.nextBytes(iv);
        return iv;
    }

    public String generateKey() {
        int length = this.algorithm == Algorithm.AES256CBC ? 32 : 16;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    static class CiphertextEntity {
        byte[] ciphertext;
        byte[] tag;
    }
}