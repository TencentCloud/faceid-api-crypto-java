package com.tencentcloud.faceid.core;

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
import java.util.stream.Collectors;

public class CryptoProvider {

    private static final String SM2_KEY = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUZrd0V3WUhLb1pJemowQ0FRWUlLb0VjejFVQmdpMERRZ0FFeTJRamJLQzRMNkxKcHc1MW1qWDkwWDQxTllHYQpNcCtPR1g3ZUpCZnM4Szk4TU90S044d1BqajFpcUhVbFc2cXlQR2dnTlBJNVJHRW9BWGFvak9WeWNnPT0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0t";
    private static final String RAS_KEY = "LS0tLS1CRUdJTiBSU0EgUFVCTElDIEtFWS0tLS0tCk1JR0pBb0dCQU1SSm9hVWtaTjFkNU1wOEY1VjZpdFhtU0xOTTVaNzcxYWZheW9JNDlTbmRrRnRzc3BIUzQwMloKRVVVUFFmcWJ1WmsvVnVTaDU5THRBL2ZCS3piNEJQNWJWOGFxb2dWaEc0ZS9xK05Ea3dsYXEwaTMxSHdMeUJsYQpFb2pFL0VFSHBYQnN1RWtWVGJLRXk1ZWxScTl0b0w3SVo4MGkrSDJtdGZVNUNQc2FyK1IzQWdNQkFBRT0KLS0tLS1FTkQgUlNBIFBVQkxJQyBLRVktLS0tLQ==";


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static PublicKey rsaPublicKey;
    private static PublicKey sm2PublicKey;


    public static String encryptKey(Algorithm algorithm, String key) throws Exception {
        byte[] bytes;
        switch (algorithm) {
            case AES256CBC:
                if (rsaPublicKey == null) {
                    rsaPublicKey = AES256CBC.loadRSAPublicKey(read(RAS_KEY));
                }
                bytes = AES256CBC.rsaEncrypt(key.getBytes(StandardCharsets.UTF_8), rsaPublicKey);
                return Base64.getEncoder().encodeToString(bytes);
            case SM4GCM:
                if (sm2PublicKey == null) {
                    sm2PublicKey = SM4GCM.loadSM2PublicKey(read(SM2_KEY));
                }
                bytes = SM4GCM.sm2Encrypt(key.getBytes(StandardCharsets.UTF_8), (BCECPublicKey) sm2PublicKey);
                return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }


    public static CiphertextEntity encryptData(Algorithm algorithm, byte[] key, byte[] plaintext, byte[] iv) throws Exception {
        CiphertextEntity entity = new CiphertextEntity();
        switch (algorithm) {
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
                return entity;
        }
    }

    public static byte[] decryptData(Algorithm algorithm, byte[] key, byte[] ciphertext, byte[] iv, byte[] tag) throws Exception {
        switch (algorithm) {
            case AES256CBC:
                return AES256CBC.aesDecrypt(key, ciphertext, iv);
            case SM4GCM:
                return SM4GCM.sm4Decrypt(key, ciphertext, iv, tag);
            default:
                return null;
        }
    }


    public static String read(String pem) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(pem))));
        return reader.lines().filter(line -> !line.startsWith("-----")).collect(Collectors.joining());
    }

    public static byte[] generateIv(Algorithm algorithm) {
        int n = algorithm == Algorithm.AES256CBC ? 16 : 12;
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[n];
        random.nextBytes(iv);
        return iv;
    }

    public static String generateKey(Algorithm algorithm) {
        int length = algorithm == Algorithm.AES256CBC ? 32 : 16;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static class CiphertextEntity {
        public byte[] ciphertext;
        public byte[] tag;
    }
}