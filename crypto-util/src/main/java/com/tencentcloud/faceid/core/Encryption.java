package com.tencentcloud.faceid.core;

import java.util.List;

public class Encryption {

    public List<String> EncryptList;// 加密的字段名称
    public String CiphertextBlob;   // 加密后的对称密钥
    public String Iv;   // 初始向量
    public String Algorithm;   // 加密算法
    public List<String> TagList; // 消息摘要


    public void setEncryptList(List<String> encryptList) {
        EncryptList = encryptList;
    }


    public void setCiphertextBlob(String ciphertextBlob) {
        CiphertextBlob = ciphertextBlob;
    }


    public void setIv(String iv) {
        Iv = iv;
    }


    public void setAlgorithm(String algorithm) {
        Algorithm = algorithm;
    }

    public void setTagList(List<String> tagList) {
        TagList = tagList;
    }
}
