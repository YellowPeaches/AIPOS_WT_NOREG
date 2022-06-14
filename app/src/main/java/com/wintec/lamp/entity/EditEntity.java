package com.wintec.lamp.entity;

import com.wintec.lamp.data.EditType;

import java.io.Serializable;

public class EditEntity implements Serializable {
    //唯一标识
    private String key;
    //标题
    private String title;
    //默认值
    private String detailText;
    //编辑类型
    private EditType type;
    //


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public EditType getType() {
        return type;
    }

    public void setType(EditType type) {
        this.type = type;
    }
}