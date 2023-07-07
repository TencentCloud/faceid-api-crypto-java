package com.tencentcloud.faceid;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AES256CBC {

    public static PublicKey loadRSAPublicKey(String publicKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(publicKey);
        RSAPublicKey rsaPublicKey = RSAPublicKey.getInstance(bytes);
        SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), rsaPublicKey);
        byte[] pkcs1 = publicKeyInfo.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pkcs1);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey loadRSAPrivateKey(String privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey rsaPrivateKey = RSAPrivateKey.getInstance(bytes);
        PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), rsaPrivateKey);
        byte[] pkcs1 = privateKeyInfo.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs1);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static byte[] rsaEncrypt(byte[] plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }


    public static byte[] rsaDecrypt(byte[] ciphertext, PrivateKey privateKey) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        return rsa.doFinal(ciphertext);
    }

    public static byte[] aesEncrypt(byte[] key, byte[] plaintext, byte[] iv) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec spec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        return cipher.doFinal(plaintext);
    }

    public static byte[] aesDecrypt(byte[] key, byte[] ciphertext, byte[] iv) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec spec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        return cipher.doFinal(ciphertext);
    }

}
