package com.wintec.lamp.entity;

import com.wintec.domain.Plu;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.PluDto;

import java.math.BigDecimal;
import java.util.Date;

public class DuoDianPlu {

    /**
     * U:新增或修改；D：删除
     */
    private String cmd;
    /**
     * 类目编号；使用ASCII编码
     */
    private int deptCode;
    /**
     * 类目名称
     */
    private String deptName;
    private String imgUrl;
    /**
     * 货号：生成磅秤签条码用
     */
    private String itemCode;
    /**
     * 标签号：1-经销；2-联营；
     * 5-经销不打印；6-联营见外包装；8-肉品；转换逻辑见下
     */
    private int lable;
    /**
     * 商品名称
     */
    private String line1;
    /**
     * 成分
     */
    private String line2;
    /**
     * 食用方式
     */
    private String line3;
    /**
     * 存储类型
     */
    private String line4;
    /**
     * 生产厂商
     */
    private String line5;
    /**
     * 更新时间
     */
    private Date modifyDT;
    /**
     * 是否允许改价：0允许改价；1禁止变价
     */
    private int openPrice;
    /**
     * 规格：默认值“1
     */
    private String packQuant;
    /**
     * PLU码
     */
    private String plu_No;
    /**
     * 条码：0-自营18码;
     * 1-联营18码;4-8码；转换逻辑见下
     */
    private String posSelect;
    /**
     * Pos标识：写死“2”
     */
    private String posflag;
    /**
     * 价格下限：默认“0”
     */
    private String priceLow;
    /**
     * 价格上限：默认“0”
     */
    private String priceUp;
    /**
     * 售卖方式：0称重；1计数
     */
    private String salesMode;
    /**
     * 保质期
     */
    private String shelfLife;
    /**
     * 皮重（单位克），默认6克
     */
    private String tare;
    /**
     * 价格（单位：分）
     */
    private String unitPrice;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(int deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getLable() {
        return lable;
    }

    public void setLable(int lable) {
        this.lable = lable;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getLine4() {
        return line4;
    }

    public void setLine4(String line4) {
        this.line4 = line4;
    }

    public String getLine5() {
        return line5;
    }

    public void setLine5(String line5) {
        this.line5 = line5;
    }

    public Date getModifyDT() {
        return modifyDT;
    }

    public void setModifyDT(Date modifyDT) {
        this.modifyDT = modifyDT;
    }

    public int getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(int openPrice) {
        this.openPrice = openPrice;
    }

    public String getPackQuant() {
        return packQuant;
    }

    public void setPackQuant(String packQuant) {
        this.packQuant = packQuant;
    }

    public String getPlu_No() {
        return plu_No;
    }

    public void setPlu_No(String plu_No) {
        this.plu_No = plu_No;
    }

    public String getPosSelect() {
        return posSelect;
    }

    public void setPosSelect(String posSelect) {
        this.posSelect = posSelect;
    }

    public String getPosflag() {
        return posflag;
    }

    public void setPosflag(String posflag) {
        this.posflag = posflag;
    }

    public String getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(String priceLow) {
        this.priceLow = priceLow;
    }

    public String getPriceUp() {
        return priceUp;
    }

    public void setPriceUp(String priceUp) {
        this.priceUp = priceUp;
    }

    public String getSalesMode() {
        return salesMode;
    }

    public void setSalesMode(String salesMode) {
        this.salesMode = salesMode;
    }

    public String getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    public String getTare() {
        return tare;
    }

    public void setTare(String tare) {
        this.tare = tare;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public PluDto toPlu() {
        PluDto plu = new PluDto();

        plu.setNameTextA(this.line1);
        plu.setPreviewImage(this.imgUrl);
        plu.setItemNo(this.itemCode);
        plu.setPluNo(this.plu_No);
        plu.setPriceUnitA(Integer.parseInt(this.salesMode));
//        if("kg".equals(Const.getSettingValue(Const.KEY_SEND_UNIT)))
//        {
//            plu.setUnitPriceA((float) Integer.valueOf(this.unitPrice) / 100);
//        }else {
//            if(plu.getPriceUnitA() == 1)
//            {
//                plu.setUnitPriceA(((float) Integer.valueOf(this.unitPrice)) / 100);
//            }else {
//                plu.setUnitPriceA( ((float) Integer.valueOf(this.unitPrice)) / 50);
//            }
//        }
        if ("kg".equals(Const.getSettingValue(Const.KEY_SEND_UNIT))) {
            plu.setUnitPriceA(new BigDecimal(this.unitPrice).divide(new BigDecimal(100)).floatValue());
        } else {
            if (plu.getPriceUnitA() == 1) {
                plu.setUnitPriceA(new BigDecimal(this.unitPrice).divide(new BigDecimal(100)).floatValue());
            } else {
                plu.setUnitPriceA(new BigDecimal(this.unitPrice).divide(new BigDecimal(50)).floatValue());
            }
        }
        plu.setSellByDate(this.shelfLife);
        // 其他字段目前没有使用功能，后续添加
        return plu;
    }
}
