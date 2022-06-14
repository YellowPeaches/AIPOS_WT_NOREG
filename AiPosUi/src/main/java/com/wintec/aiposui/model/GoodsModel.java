package com.wintec.aiposui.model;

/**
 * @描述：
 * @文件名: AiPos.GoodsModel
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/18 8:44
 */
public class GoodsModel {

    private String uid;



    private String goodsId;
    //打秤码
    private String scalesCode;
    //货号
    private String itemNo;
    //商品名称
    private String goodsName;
    //商品类别
    private String goodsType;
    //商品单价
    private String price;
    //商品总价
    private String total;

    //商品数量
    private String count;
    //商品图片地址
    private String goodsImg;
    // 单位ID 0-称重  1-计件
    private int unitId;
    // 单位名
    private String unitName;
    // 是否识别第一位
    private boolean isFirst;

    // 单位
    private boolean isKg;

    //保质期
    private String sellByDate;

    //条码
    private String tagCode;

    //重量
    private String net;

    //是否使用预览图
    private String previewFlag;

    // 是否允许折扣
    private Boolean isDisCount;

    // 是否允许改价
    private Boolean isTempPrice;

    public Boolean getDisCount() {
        return isDisCount;
    }

    public void setDisCount(Boolean disCount) {
        isDisCount = disCount;
    }

    public Boolean getTempPrice() {
        return isTempPrice;
    }

    public void setTempPrice(Boolean tempPrice) {
        isTempPrice = tempPrice;
    }

    public String getPreviewFlag() {
        return previewFlag;
    }

    public void setPreviewFlag(String previewFlag) {
        this.previewFlag = previewFlag;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getSellByDate() {
        return sellByDate;
    }

    public void setSellByDate(String sellByDate) {
        this.sellByDate = sellByDate;
    }

    public boolean isKg() {
        return isKg;
    }

    public void setKg(boolean kg) {
        isKg = kg;
    }

    public boolean getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public GoodsModel(){}

    public GoodsModel(String goodsId, String scalesCode, String goodsName, String goodsType,
                      String price, String count, String url, int unitId, String unitName ,
                      String sellByDate , String previewFlag, String itemNo,Boolean isDisCount ,Boolean isTempPrice){
        this.goodsId = scalesCode;
        this.scalesCode = scalesCode;
        this.goodsName = goodsName;
        this.goodsType = goodsType;
        this.price = price;
        this.count =  count;
        this.goodsImg = url;
        this.unitId = unitId;
        this.unitName = unitName;
        this.sellByDate = sellByDate;
        this.previewFlag = previewFlag;
        this.itemNo = itemNo;
        this.isDisCount = isDisCount;
        this.isTempPrice = isTempPrice;
        if (itemNo == null){
            this.itemNo = "";
        }
        else{
            if (itemNo.length()>7){
                this.itemNo = itemNo.substring(itemNo.length()-7);
            }
        }
    }

    public String getUid() {
        return uid;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getScalesCode() {
        return scalesCode;
    }

    public void setScalesCode(String scalesCode) {
        this.scalesCode = scalesCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }
}
