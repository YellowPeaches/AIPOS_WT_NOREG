package com.wintec.lamp.bean;

public class registerBean {

    private String code;
    private String key;
    private String posKey;
    private String msg;
    private String httpCode;

    public String getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(String httpCode) {
        this.httpCode = httpCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPosKey() {
        return posKey;
    }

    public void setPosKey(String posKey) {
        this.posKey = posKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
