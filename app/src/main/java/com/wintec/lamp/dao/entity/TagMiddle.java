package com.wintec.lamp.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TagMiddle implements Serializable {
    private static final long serialVersionUID = -4621715087305128172L;
    // 自增ID
    @Id(autoincrement = true)
    private Long _id;

    private Integer id;
    private Integer templateid;
    private Integer abscissa;
    private Integer ordinate;
    private Integer fontSize;
    private String fontFormat;
    private Integer overstriking;
    private Integer underline;
    private Integer italic;
    private String tagName;
    private Integer length;
    private Integer breadth;
    private Integer codeSystem;
    private Integer componentType;
    private String divId;
    private Integer templateId;
    private Integer tenantId;
    private Integer branchId;
    private String name;
    private Integer lengths;
    private Integer breadths;
    private String unit;
    private String dateFormat;
    private String units;
    private String bz1;
    private String bz2;
    private Integer labelNo;
    //isDelLine  0  否 1 是 折扣下划线
//    private String isDelLine;

    @Generated(hash = 213228707)
    public TagMiddle(Long _id, Integer id, Integer templateid, Integer abscissa,
            Integer ordinate, Integer fontSize, String fontFormat, Integer overstriking,
            Integer underline, Integer italic, String tagName, Integer length,
            Integer breadth, Integer codeSystem, Integer componentType, String divId,
            Integer templateId, Integer tenantId, Integer branchId, String name,
            Integer lengths, Integer breadths, String unit, String dateFormat, String units,
            String bz1, String bz2, Integer labelNo) {
        this._id = _id;
        this.id = id;
        this.templateid = templateid;
        this.abscissa = abscissa;
        this.ordinate = ordinate;
        this.fontSize = fontSize;
        this.fontFormat = fontFormat;
        this.overstriking = overstriking;
        this.underline = underline;
        this.italic = italic;
        this.tagName = tagName;
        this.length = length;
        this.breadth = breadth;
        this.codeSystem = codeSystem;
        this.componentType = componentType;
        this.divId = divId;
        this.templateId = templateId;
        this.tenantId = tenantId;
        this.branchId = branchId;
        this.name = name;
        this.lengths = lengths;
        this.breadths = breadths;
        this.unit = unit;
        this.dateFormat = dateFormat;
        this.units = units;
        this.bz1 = bz1;
        this.bz2 = bz2;
        this.labelNo = labelNo;
    }

    @Generated(hash = 1145394703)
    public TagMiddle() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTemplateid() {
        return this.templateid;
    }

    public void setTemplateid(Integer templateid) {
        this.templateid = templateid;
    }

    public Integer getAbscissa() {
        return this.abscissa;
    }

    public void setAbscissa(Integer abscissa) {
        this.abscissa = abscissa;
    }

    public Integer getOrdinate() {
        return this.ordinate;
    }

    public void setOrdinate(Integer ordinate) {
        this.ordinate = ordinate;
    }

    public Integer getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFormat() {
        return this.fontFormat;
    }

    public void setFontFormat(String fontFormat) {
        this.fontFormat = fontFormat;
    }

    public Integer getOverstriking() {
        return this.overstriking;
    }

    public void setOverstriking(Integer overstriking) {
        this.overstriking = overstriking;
    }

    public Integer getUnderline() {
        return this.underline;
    }

    public void setUnderline(Integer underline) {
        this.underline = underline;
    }

    public Integer getItalic() {
        return this.italic;
    }

    public void setItalic(Integer italic) {
        this.italic = italic;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getBreadth() {
        return this.breadth;
    }

    public void setBreadth(Integer breadth) {
        this.breadth = breadth;
    }

    public Integer getCodeSystem() {
        return this.codeSystem;
    }

    public void setCodeSystem(Integer codeSystem) {
        this.codeSystem = codeSystem;
    }

    public Integer getComponentType() {
        return this.componentType;
    }

    public void setComponentType(Integer componentType) {
        this.componentType = componentType;
    }

    public String getDivId() {
        return this.divId;
    }

    public void setDivId(String divId) {
        this.divId = divId;
    }

    public Integer getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getBranchId() {
        return this.branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLengths() {
        return this.lengths;
    }

    public void setLengths(Integer lengths) {
        this.lengths = lengths;
    }

    public Integer getBreadths() {
        return this.breadths;
    }

    public void setBreadths(Integer breadths) {
        this.breadths = breadths;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getUnits() {
        return this.units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getBz1() {
        return this.bz1;
    }

    public void setBz1(String bz1) {
        this.bz1 = bz1;
    }

    public String getBz2() {
        return this.bz2;
    }

    public void setBz2(String bz2) {
        this.bz2 = bz2;
    }

    public Integer getLabelNo() {
        return this.labelNo;
    }

    public void setLabelNo(Integer labelNo) {
        this.labelNo = labelNo;
    }

//



}