package com.wintec.lamp.dao.entity;

import com.wintec.aiposui.model.GoodsModel;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.utils.CommUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

@Entity
public class Commdity {
    // 自增ID
    @Id(autoincrement = true)
    private Long _id;
    // 打秤码
    private String id;
    // 商品编码
    private String itemCode;
    // 商品名
    private String name;
    // 单价
    private float price;
    // 单位 4-kg  9-件
    private Integer unitId;
    // 品类ID
    private Integer classifyId;
    // 品类名
    private String classifyName;
    // 预览图地址
    private String previewImage;
    // 搜索关键字
    private String searchKey;
    // 上级品类ID
    private Integer parentClassifyId;
    // 上级品类名
    private String parentClassifyName;
    // 打印单位
    private String unitPrint;
    // 点击次数
    private Integer click;
    // 首字母缩写
    private String initials;
    //创建时间
    private String createTime;
    //网络连接
    private Integer netFlag;
    //门店
    private Integer branchId;
    // Top1
    private Integer top1Click;
    // Top2-5
    private Integer top2T5Click;
    // error
    private Integer errorClick;
    // 精确度
    private float accuracy;

    @Generated(hash = 566183508)
    public Commdity(Long _id, String id, String itemCode, String name, float price,
                    Integer unitId, Integer classifyId, String classifyName,
                    String previewImage, String searchKey, Integer parentClassifyId,
                    String parentClassifyName, String unitPrint, Integer click,
                    String initials, String createTime, Integer netFlag, Integer branchId,
                    Integer top1Click, Integer top2T5Click, Integer errorClick,
                    float accuracy) {
        this._id = _id;
        this.id = id;
        this.itemCode = itemCode;
        this.name = name;
        this.price = price;
        this.unitId = unitId;
        this.classifyId = classifyId;
        this.classifyName = classifyName;
        this.previewImage = previewImage;
        this.searchKey = searchKey;
        this.parentClassifyId = parentClassifyId;
        this.parentClassifyName = parentClassifyName;
        this.unitPrint = unitPrint;
        this.click = click;
        this.initials = initials;
        this.createTime = createTime;
        this.netFlag = netFlag;
        this.branchId = branchId;
        this.top1Click = top1Click;
        this.top2T5Click = top2T5Click;
        this.errorClick = errorClick;
        this.accuracy = accuracy;
    }

    @Generated(hash = 2047568758)
    public Commdity() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemCode() {
        return this.itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Integer getUnitId() {
        return this.unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getClassifyId() {
        return this.classifyId;
    }

    public void setClassifyId(Integer classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyName() {
        return this.classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public String getPreviewImage() {
        return this.previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public String getSearchKey() {
        return this.searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Integer getParentClassifyId() {
        return this.parentClassifyId;
    }

    public void setParentClassifyId(Integer parentClassifyId) {
        this.parentClassifyId = parentClassifyId;
    }

    public String getParentClassifyName() {
        return this.parentClassifyName;
    }

    public void setParentClassifyName(String parentClassifyName) {
        this.parentClassifyName = parentClassifyName;
    }

    public String getUnitPrint() {
        return this.unitPrint;
    }

    public void setUnitPrint(String unitPrint) {
        this.unitPrint = unitPrint;
    }

    public Integer getClick() {
        return this.click;
    }

    public void setClick(Integer click) {
        this.click = click;
    }

    public String getInitials() {
        return this.initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 重写比较方法
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commdity co = (Commdity) o;
        if (co.getItemCode() == null) return false;
        return Objects.equals(itemCode, co.getItemCode());
    }

    public Integer getNetFlag() {
        return this.netFlag;
    }

    public void setNetFlag(Integer netFlag) {
        this.netFlag = netFlag;
    }

    public Integer getBranchId() {
        return this.branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Integer getTop1Click() {
        return this.top1Click;
    }

    public void setTop1Click(Integer top1Click) {
        this.top1Click = top1Click;
    }

    public Integer getTop2T5Click() {
        return this.top2T5Click;
    }

    public void setTop2T5Click(Integer top2T5Click) {
        this.top2T5Click = top2T5Click;
    }

    public Integer getErrorClick() {
        return this.errorClick;
    }

    public void setErrorClick(Integer errorClick) {
        this.errorClick = errorClick;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }


}
