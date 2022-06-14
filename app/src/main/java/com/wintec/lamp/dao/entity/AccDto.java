package com.wintec.lamp.dao.entity;

import com.wintec.domain.Acc;
import com.wintec.domain.DataBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.util.HashMap;
import java.util.Map;

@Entity
public class AccDto {

    // 自增ID
    @Id(autoincrement = true)
    private Long _id;
    @Unique
    private int accNo;
    private String content;
    private String content1; //段落1
    private String content2;
    private String content3;
    private String content4;
    private int depNo; // 部门号
    private int font1; // 字体一
    private int font2;
    private int font3;
    private int font4;
    private int groupNo; //分组号


    public AccDto(Acc acc) {
        this.accNo = acc.accNo;
        this.content = acc.content;
        this.content1 = acc.content1;
        this.content2 = acc.content2;
        this.content3 = acc.content3;
        this.content4 = acc.content4;
        this.depNo = acc.depNo;
        this.font1 = acc.font1;
        this.font2 = acc.font2;
        this.font3 = acc.font3;
        this.font4 = acc.font4;
        this.groupNo = acc.groupNo;
    }

    public AccDto(HashMap<String, Object> acc) {
        this.accNo = acc.get("et_no") != null ? new Integer(acc.get("et_no").toString()) : -1;
        this.content = acc.get("text_a") != null ? acc.get("text_a").toString() : "";
        this.content1 = acc.get("text_b") != null ? acc.get("text_b").toString() : "";
        this.content2 = acc.get("text_c") != null ? acc.get("text_c").toString() : "";
        this.content3 = acc.get("text_d") != null ? acc.get("text_d").toString() : "";
        this.content4 = acc.get("text_e") != null ? acc.get("text_e").toString() : "";
        this.depNo = acc.get("dept_no") != null ? new Integer(acc.get("dept_no").toString()) : -1;
        this.font1 = acc.get("font_size_a") != null ? new Integer(acc.get("font_size_a").toString()) : -1;
        this.font2 = acc.get("font_size_b") != null ? new Integer(acc.get("font_size_b").toString()) : -1;
        this.font3 = acc.get("font_size_c") != null ? new Integer(acc.get("font_size_c").toString()) : -1;
        this.font4 = acc.get("font_size_d") != null ? new Integer(acc.get("font_size_d").toString()) : -1;
        //this.groupNo = Integer.valueOf(acc.get(""));
    }

    @Generated(hash = 405780166)
    public AccDto(Long _id, int accNo, String content, String content1,
                  String content2, String content3, String content4, int depNo, int font1,
                  int font2, int font3, int font4, int groupNo) {
        this._id = _id;
        this.accNo = accNo;
        this.content = content;
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;
        this.content4 = content4;
        this.depNo = depNo;
        this.font1 = font1;
        this.font2 = font2;
        this.font3 = font3;
        this.font4 = font4;
        this.groupNo = groupNo;
    }

    @Generated(hash = 1252654869)
    public AccDto() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public int getAccNo() {
        return this.accNo;
    }

    public void setAccNo(int accNo) {
        this.accNo = accNo;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent1() {
        return this.content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        return this.content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public String getContent3() {
        return this.content3;
    }

    public void setContent3(String content3) {
        this.content3 = content3;
    }

    public String getContent4() {
        return this.content4;
    }

    public void setContent4(String content4) {
        this.content4 = content4;
    }

    public int getDepNo() {
        return this.depNo;
    }

    public void setDepNo(int depNo) {
        this.depNo = depNo;
    }

    public int getFont1() {
        return this.font1;
    }

    public void setFont1(int font1) {
        this.font1 = font1;
    }

    public int getFont2() {
        return this.font2;
    }

    public void setFont2(int font2) {
        this.font2 = font2;
    }

    public int getFont3() {
        return this.font3;
    }

    public void setFont3(int font3) {
        this.font3 = font3;
    }

    public int getFont4() {
        return this.font4;
    }

    public void setFont4(int font4) {
        this.font4 = font4;
    }

    public int getGroupNo() {
        return this.groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }


}
