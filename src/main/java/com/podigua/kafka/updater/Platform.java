package com.podigua.kafka.updater;

/**
 * 平台
 *
 * @author podigua
 * @date 2025/03/27
 */
public class Platform {
    /**
     * 签名
     */
    private String signature;
    /**
     * 网址
     */
    private String url;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
