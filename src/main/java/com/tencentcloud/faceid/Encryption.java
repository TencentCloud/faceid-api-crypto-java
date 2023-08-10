package com.tencentcloud.faceid;

import java.util.List;

public class Encryption {

    List<String> EncryptList;// 加密的字段名称
    String CiphertextBlob;   // 加密后的对称密钥
    String Iv;   // 初始向量
    String Algorithm;   // 加密算法
    List<String> TagList; // 消息摘要

    public List<String> getEncryptList() {
        return EncryptList;
    }

    public void setEncryptList(List<String> encryptList) {
        EncryptList = encryptList;
    }

    public String getCiphertextBlob() {
        return CiphertextBlob;
    }

    public void setCiphertextBlob(String ciphertextBlob) {
        CiphertextBlob = ciphertextBlob;
    }

    public String getIv() {
        return Iv;
    }

    public void setIv(String iv) {
        Iv = iv;
    }

    public String getAlgorithm() {
        return Algorithm;
    }

    public void setAlgorithm(String algorithm) {
        Algorithm = algorithm;
    }

    public List<String> getTagList() {
        return TagList;
    }

    public void setTagList(List<String> tagList) {
        TagList = tagList;
    }
}
