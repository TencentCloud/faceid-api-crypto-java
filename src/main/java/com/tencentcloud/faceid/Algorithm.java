package com.tencentcloud.faceid;

public enum Algorithm {

    AES256CBC("AES-256-CBC"), SM4GCM("SM4-GCM");

    private final String value;

    Algorithm(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
