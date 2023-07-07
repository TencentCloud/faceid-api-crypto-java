package com.tencentcloud.faceid;

import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SM4GCM {


    public static PublicKey loadSM2PublicKey(String publicKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        return keyFactory.generatePublic(keySpec);

    }

    public static byte[] sm2Encrypt(byte[] plaintext, BCECPublicKey publicKey) throws Exception {
        SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
        ECParameterSpec ecParameterSpec = publicKey.getParameters();
        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN());
        ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(publicKey.getQ(), ecDomainParameters);
        SM2Engine sm2Engine = new SM2Engine(mode);
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));
        return sm2Engine.processBlock(plaintext, 0, plaintext.length);
    }

    public static byte[] sm2Decrypt(byte[] ciphertext, BCECPrivateKey privateKey) throws Exception {
        SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
        ECParameterSpec ecParameterSpec = privateKey.getParameters();
        ECDomainParameters ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN());
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(privateKey.getD(), ecDomainParameters);

        SM2Engine sm2Engine = new SM2Engine(mode);
        sm2Engine.init(false, ecPrivateKeyParameters);
        return sm2Engine.processBlock(ciphertext, 0, ciphertext.length);
    }

    public static byte[] sm4Encrypt(byte[] key, byte[] plaintext, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4/GCM/NoPadding", "BC");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "SM4");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
        return cipher.doFinal(plaintext);
    }

    public static byte[] sm4Decrypt(byte[] key, byte[] ciphertext, byte[] iv, byte[] tag) throws Exception {
        byte[] bytes = new byte[ciphertext.length + tag.length];
        System.arraycopy(ciphertext, 0, bytes, 0, ciphertext.length);
        System.arraycopy(tag, 0, bytes, ciphertext.length, tag.length);

        Cipher cipher = Cipher.getInstance("SM4/GCM/NoPadding", "BC");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "SM4");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
        return cipher.doFinal(bytes);
    }

}
