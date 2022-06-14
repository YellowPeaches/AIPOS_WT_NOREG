package com.wintec.aiposui.model;

public class RecommondBean {

    private String url;
    private String string1;
    private String string2;


    public RecommondBean(String s1, String s2) {
        this.string1 = s1;
        this.string2 = s2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }
}
