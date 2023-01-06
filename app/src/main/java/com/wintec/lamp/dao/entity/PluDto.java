package com.wintec.lamp.dao.entity;

import com.wintec.aiposui.model.GoodsModel;
import com.wintec.domain.Plu;
import com.wintec.lamp.ScaleActivityUI;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.entity.Total;
import com.wintec.lamp.utils.CommUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/5 14:00
 */
@Entity
public class PluDto {

    // 自增ID
    @Id(autoincrement = true)
    private Long _id;

    private int barcodeNo;
    private String bestBeforeDate;
    private String bestBeforeDateFormat;
    private String bestBeforeDateUnit;
    private int deptNo;
    private boolean discountFlagA;
    private boolean discountFlagB;
    private int etNo;    //附加信息号
    private double fixWeight;
    private String graphicNoA;
    private String graphicNoB;
    private String graphicNoC;
    private int groupNo;
    private boolean isPrintBestBeforeDate = true;
    private boolean isPrintPackedDate = true;
    private boolean isPrintSellByDate = true;
    private boolean isSpecialPrice;
    private boolean isTraceable;
    private String itemNo;
    private int labelNoA;
    private int labelNoB;
    private int labelNoC;
    private String nameTextA; //名称
    private String nameTextB;
    private int nutritionNo;
    private String packedDate;
    private String packedDateFormat;
    private String packedDateUnit;
    @Unique
    private String pluNo;
    private boolean priceChangeFlagA;
    private boolean priceChangeFlagB;
    private int priceRuleNo;
    private int priceUnitA;  //单位 计重-0 计价-1
    private int priceUnitB;
    private String priceUnitDesA;
    private String priceUnitDesB;
    private int safeHandingNo;
    private String sellByDate;  //保质期
    private String sellByDateFormat;
    private String sellByDateUnit;
    private double specialPrice;
    private String specialPriceEndDate;
    private String specialPriceStartDate;
    private int tareNoA;
    private float unitPriceA;  //单价
    private int unitPriceB;

    // 点击次数
    private Integer click;
    // 首字母缩写
    private String initials;
    //门店
    private String branchId;
    // 预览图地址
    private String previewImage;

    public PluDto(Plu plu) {
        this.barcodeNo = plu.getBarcodeNo();
        this.bestBeforeDate = plu.getBestBeforeDate();
        this.bestBeforeDateFormat = plu.getBestBeforeDateFormat();
        this.bestBeforeDateUnit = plu.getBestBeforeDateUnit();
        this.deptNo = plu.getDeptNo();
        this.discountFlagA = plu.isDiscountFlagA();
        this.discountFlagB = plu.isDiscountFlagB();
        this.etNo = plu.getEtNo();
        this.fixWeight = plu.getFixWeight();
        this.graphicNoA = plu.getGraphicNoA();
        this.graphicNoB = plu.getGraphicNoB();
        this.graphicNoC = plu.getGraphicNoC();
        this.groupNo = plu.getGroupNo();
        this.isPrintBestBeforeDate = plu.isPrintBestBeforeDate();
        this.isPrintPackedDate = plu.isPrintPackedDate();
        this.isPrintSellByDate = plu.isPrintSellByDate();
        this.isSpecialPrice = plu.isSpecialPrice();
        this.isTraceable = plu.isTraceable();
        this.itemNo = plu.getItemNo();
        this.labelNoA = plu.getLabelNoA();
        this.labelNoB = plu.getLabelNoB();
        this.labelNoC = plu.getLabelNoC();
        this.nameTextA = plu.getNameTextA();
        this.nameTextB = plu.getNameTextB();
        this.nutritionNo = plu.getNutritionNo();
        this.packedDate = plu.getPackedDate();
        this.packedDateFormat = plu.getPackedDateFormat();
        this.packedDateUnit = plu.getPackedDateUnit();
        this.pluNo = plu.getPluNo() + "";
        this.priceChangeFlagA = plu.isPriceChangeFlagA();
        this.priceChangeFlagB = plu.isPriceChangeFlagB();
        this.priceRuleNo = plu.getPriceRuleNo();
        this.priceUnitA = plu.getPriceUnitA();
        this.priceUnitB = plu.getPriceUnitB();
        this.priceUnitDesA = plu.getPriceUnitDesA();
        this.priceUnitDesB = plu.getPriceUnitDesB();
        this.safeHandingNo = plu.getSafeHandingNo();
        this.sellByDate = plu.getSellByDate();
        this.sellByDateFormat = plu.getSellByDateFormat();
        this.sellByDateUnit = plu.getSellByDateUnit();
        this.specialPrice = plu.getSpecialPrice();
        this.specialPriceEndDate = plu.getSpecialPriceEndDate();
        this.specialPriceStartDate = plu.getSpecialPriceStartDate();
        this.tareNoA = plu.getTareNoA();
        //
//        if("kg".equals(Const.getSettingValue(Const.KEY_SEND_UNIT)))
//        {
//            this.unitPriceA = ((float) Integer.valueOf(plu.getUnitPriceA())) / 100;
//        }else {
//            if(plu.getPriceUnitA() == 1)
//            {
//                this.unitPriceA = ((float) Integer.valueOf(plu.getUnitPriceA())) / 100;
//            }else {
//                this.unitPriceA = ((float) Integer.valueOf(plu.getUnitPriceA())) / 50;
//            }
//        }
        if ("kg".equals(Const.getSettingValue(Const.KEY_SEND_UNIT))) {
            this.unitPriceA = (new BigDecimal(plu.getUnitPriceA()).divide(new BigDecimal(100)).floatValue());
        } else {
            if (plu.getPriceUnitA() == 1) {
                this.unitPriceA = (new BigDecimal(plu.getUnitPriceA()).divide(new BigDecimal(100)).floatValue());
            } else {
                this.unitPriceA = (new BigDecimal(plu.getUnitPriceA()).divide(new BigDecimal(50)).floatValue());
            }
        }

        this.unitPriceB = plu.getUnitPriceB();
    }


    public PluDto(HashMap<String, Object> plu) {

        if ("oracle".equals(Const.getSettingValue(Const.KEY_GET_DATA_DB))) {
            String name11 = plu.get("NAME1") + "";
            this.nameTextA = plu.get("NAME1") != null ? name11 : "";
            this.pluNo = plu.get("PLU") != null ? plu.get("PLU").toString() : "";
            this.labelNoA = plu.get("LABELNO") != null ? Integer.parseInt((plu.get("LABELNO") + "")) : -1;
            if (plu.get("ISBJ") != null) {
                this.priceChangeFlagA = Integer.parseInt(plu.get("ISBJ") + "") == 1 ? true : false;
            }
            this.priceUnitA = plu.get("PRICETYPE") != null ? Integer.parseInt(plu.get("PRICETYPE") + "") : 0;
            this.labelNoA = plu.get("LABELNO") != null ? Integer.parseInt(plu.get("LABELNO") + "") : -1;
            this.unitPriceA = plu.get("PRICE1") != null ? Float.parseFloat(plu.get("PRICE1") + "") : -1;
            this.itemNo = plu.get("GOODSID") != null ? plu.get("GOODSID") + "" : "";
            this.sellByDate = plu.get("SELLYDAYS") != null ? plu.get("SELLYDAYS") + "" : "";
            this.deptNo = plu.get("DEPARTNO") != null ? Integer.parseInt(plu.get("DEPARTNO") + "") : -1;

        } else {
            this.deptNo = plu.get("dept_no") != null ? new Integer(plu.get("dept_no").toString()) : -1;
            this.pluNo = String.valueOf(plu.get("plu_no") != null ? plu.get("plu_no").toString() : "");
            this.itemNo = plu.get("item_no") != null ? plu.get("item_no").toString() : "";
            this.nameTextA = plu.get("name_text_a") != null ? plu.get("name_text_a").toString() : "";
            this.priceUnitA = getUnit((String) plu.get("price_unit_a"));
            this.labelNoA = plu.get("label_no_a") != null ? new Integer(plu.get("label_no_a").toString()) : -1;
            this.etNo = plu.get("et_no") != null ? new Integer(plu.get("et_no").toString()) : -1;
            this.tareNoA = plu.get("tare_no_a") != null ? new Integer(plu.get("tare_no_a").toString()) : -1;
            this.packedDate = plu.get("packed_date") != null ? plu.get("packed_date").toString() : "";
            this.sellByDate = plu.get("sell_by_date") != null ? plu.get("sell_by_date").toString() : "";
            this.bestBeforeDate = plu.get("best_before_date") != null ? plu.get("best_before_date").toString() : "";
            if ("PCS".equals(plu.get("price_unit_a")) || "KGM".equals(plu.get("unit_price_a"))) {
                this.unitPriceA = Float.parseFloat(plu.get("unit_price_a") + "");//.divide(new BigDecimal(100)).floatValue();
            } else {
                this.unitPriceA = Float.parseFloat(plu.get("unit_price_a") + "");//.divide(new BigDecimal(50)).floatValue();
            }
            if ("kg".equals(Const.getSettingValue(Const.KEY_SEND_UNIT))) {
                this.unitPriceA = Float.parseFloat(plu.get("unit_price_a") + "");//.divide(new BigDecimal(100)).floatValue();
            } else {
                if (this.getPriceUnitA() == 1) {
                    this.unitPriceA = ((BigDecimal) plu.get("unit_price_a")).divide(new BigDecimal(1)).floatValue();
                } else {
                    this.unitPriceA = ((BigDecimal) plu.get("unit_price_a")).divide(new BigDecimal(2)).floatValue();
                }
            }
        }
    }

    private int getUnit(String price_unit_a) {
        if ("PCS".equals(price_unit_a)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Generated(hash = 100654831)
    public PluDto(Long _id, int barcodeNo, String bestBeforeDate, String bestBeforeDateFormat,
                  String bestBeforeDateUnit, int deptNo, boolean discountFlagA, boolean discountFlagB, int etNo,
                  double fixWeight, String graphicNoA, String graphicNoB, String graphicNoC, int groupNo,
                  boolean isPrintBestBeforeDate, boolean isPrintPackedDate, boolean isPrintSellByDate,
                  boolean isSpecialPrice, boolean isTraceable, String itemNo, int labelNoA, int labelNoB, int labelNoC,
                  String nameTextA, String nameTextB, int nutritionNo, String packedDate, String packedDateFormat,
                  String packedDateUnit, String pluNo, boolean priceChangeFlagA, boolean priceChangeFlagB,
                  int priceRuleNo, int priceUnitA, int priceUnitB, String priceUnitDesA, String priceUnitDesB,
                  int safeHandingNo, String sellByDate, String sellByDateFormat, String sellByDateUnit,
                  double specialPrice, String specialPriceEndDate, String specialPriceStartDate, int tareNoA,
                  float unitPriceA, int unitPriceB, Integer click, String initials, String branchId,
                  String previewImage) {
        this._id = _id;
        this.barcodeNo = barcodeNo;
        this.bestBeforeDate = bestBeforeDate;
        this.bestBeforeDateFormat = bestBeforeDateFormat;
        this.bestBeforeDateUnit = bestBeforeDateUnit;
        this.deptNo = deptNo;
        this.discountFlagA = discountFlagA;
        this.discountFlagB = discountFlagB;
        this.etNo = etNo;
        this.fixWeight = fixWeight;
        this.graphicNoA = graphicNoA;
        this.graphicNoB = graphicNoB;
        this.graphicNoC = graphicNoC;
        this.groupNo = groupNo;
        this.isPrintBestBeforeDate = isPrintBestBeforeDate;
        this.isPrintPackedDate = isPrintPackedDate;
        this.isPrintSellByDate = isPrintSellByDate;
        this.isSpecialPrice = isSpecialPrice;
        this.isTraceable = isTraceable;
        this.itemNo = itemNo;
        this.labelNoA = labelNoA;
        this.labelNoB = labelNoB;
        this.labelNoC = labelNoC;
        this.nameTextA = nameTextA;
        this.nameTextB = nameTextB;
        this.nutritionNo = nutritionNo;
        this.packedDate = packedDate;
        this.packedDateFormat = packedDateFormat;
        this.packedDateUnit = packedDateUnit;
        this.pluNo = pluNo;
        this.priceChangeFlagA = priceChangeFlagA;
        this.priceChangeFlagB = priceChangeFlagB;
        this.priceRuleNo = priceRuleNo;
        this.priceUnitA = priceUnitA;
        this.priceUnitB = priceUnitB;
        this.priceUnitDesA = priceUnitDesA;
        this.priceUnitDesB = priceUnitDesB;
        this.safeHandingNo = safeHandingNo;
        this.sellByDate = sellByDate;
        this.sellByDateFormat = sellByDateFormat;
        this.sellByDateUnit = sellByDateUnit;
        this.specialPrice = specialPrice;
        this.specialPriceEndDate = specialPriceEndDate;
        this.specialPriceStartDate = specialPriceStartDate;
        this.tareNoA = tareNoA;
        this.unitPriceA = unitPriceA;
        this.unitPriceB = unitPriceB;
        this.click = click;
        this.initials = initials;
        this.branchId = branchId;
        this.previewImage = previewImage;
    }


    @Generated(hash = 168493247)
    public PluDto() {
    }


    public GoodsModel parseNet(float mNet, int status, float discount, float tempPrice, float tempTotal) {
        int totalPricePoint = Integer.valueOf(Const.getSettingValue(Const.TOTAL_PRICE_POINT));
        String preflag = Const.getSettingValue(Const.PREVIEW_FLAG);
        boolean isKg = "kg".equals(Const.getSettingValue(Const.WEIGHT_UNIT));
        GoodsModel goodsModel = new GoodsModel(itemNo, this.pluNo, nameTextA, "",
                CommUtils.Float2String(unitPriceA, totalPricePoint), "0",
                previewImage, priceUnitA, "", sellByDate, preflag, itemNo, discountFlagA, priceChangeFlagA);
        goodsModel.setKg(isKg);
        Total total = getTotal(goodsModel, status, discount, tempPrice, tempTotal, mNet);
        goodsModel.setTotal(total.getTotal());
        goodsModel.setPreviewFlag(preflag);
        return goodsModel;
    }

    public GoodsModel parse() {
        String preflag = Const.getSettingValue(Const.PREVIEW_FLAG);
        return new GoodsModel(itemNo, this.pluNo, nameTextA, "",
                CommUtils.Float2String(unitPriceA, 2), "0",
                previewImage, priceUnitA, "", sellByDate, preflag, itemNo, discountFlagA, priceChangeFlagA);

    }

    /**
     * @param goodsModel
     * @param status     折扣状态
     * @param discount   折扣 范围0-1
     * @param tempPrice  单价
     * @param tempTotal  总价
     * @param mNet       重量
     * @description: 获取商品Total
     * @return:Total
     * @time: 2022/3/3 10:45
     */
    private Total getTotal(GoodsModel goodsModel, int status, float discount, float tempPrice, float tempTotal, float mNet) {
        PluDto co = PluDtoDaoHelper.getCommdityByScalesCodeLocal(goodsModel.getGoodsId());
        if (co == null) {
            return null;
        }
        float price = 0.00f;
        int unitPricePoint = Integer.valueOf(Const.getSettingValue(Const.UNIT_PRICE_POINT));
        int totalPricePoint = Integer.valueOf(Const.getSettingValue(Const.TOTAL_PRICE_POINT));
        if (status == ScaleActivityUI.MODE_DISCOUNT_TRADE && discount < 1 && discount > 0) {
            price = Float.parseFloat(CommUtils.Float2String(co.getUnitPriceA() * discount, unitPricePoint));
        } else if (status == ScaleActivityUI.MODE_CHANGE_PRICE_TRADE) {
            price = Float.parseFloat(CommUtils.Float2String(tempPrice, unitPricePoint));
        } else {
            price = Float.parseFloat(CommUtils.Float2String(co.getUnitPriceA(), unitPricePoint));
        }

        float total = 0;
        if (goodsModel.getUnitId() == 0) {
            total = price * mNet;
        }
        if (status == ScaleActivityUI.MODE_CHANGE_TOTAL_TRADE) {
            total = tempTotal;
        }
        final String tempTotel = CommUtils.Float2String(total, totalPricePoint);
        String mTotal = CommUtils.priceToString(Float.valueOf(tempTotel));
        String discountPrice = String.valueOf(price);
        return new Total(mTotal, discountPrice, status);
    }


    public Long get_id() {
        return this._id;
    }


    public void set_id(Long _id) {
        this._id = _id;
    }


    public int getBarcodeNo() {
        return this.barcodeNo;
    }


    public void setBarcodeNo(int barcodeNo) {
        this.barcodeNo = barcodeNo;
    }


    public String getBestBeforeDate() {
        return this.bestBeforeDate;
    }


    public void setBestBeforeDate(String bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }


    public String getBestBeforeDateFormat() {
        return this.bestBeforeDateFormat;
    }


    public void setBestBeforeDateFormat(String bestBeforeDateFormat) {
        this.bestBeforeDateFormat = bestBeforeDateFormat;
    }


    public String getBestBeforeDateUnit() {
        return this.bestBeforeDateUnit;
    }


    public void setBestBeforeDateUnit(String bestBeforeDateUnit) {
        this.bestBeforeDateUnit = bestBeforeDateUnit;
    }


    public int getDeptNo() {
        return this.deptNo;
    }


    public void setDeptNo(int deptNo) {
        this.deptNo = deptNo;
    }


    public boolean getDiscountFlagA() {
        return this.discountFlagA;
    }


    public void setDiscountFlagA(boolean discountFlagA) {
        this.discountFlagA = discountFlagA;
    }


    public boolean getDiscountFlagB() {
        return this.discountFlagB;
    }


    public void setDiscountFlagB(boolean discountFlagB) {
        this.discountFlagB = discountFlagB;
    }


    public int getEtNo() {
        return this.etNo;
    }


    public void setEtNo(int etNo) {
        this.etNo = etNo;
    }


    public double getFixWeight() {
        return this.fixWeight;
    }


    public void setFixWeight(double fixWeight) {
        this.fixWeight = fixWeight;
    }


    public String getGraphicNoA() {
        return this.graphicNoA;
    }


    public void setGraphicNoA(String graphicNoA) {
        this.graphicNoA = graphicNoA;
    }


    public String getGraphicNoB() {
        return this.graphicNoB;
    }


    public void setGraphicNoB(String graphicNoB) {
        this.graphicNoB = graphicNoB;
    }


    public String getGraphicNoC() {
        return this.graphicNoC;
    }


    public void setGraphicNoC(String graphicNoC) {
        this.graphicNoC = graphicNoC;
    }


    public int getGroupNo() {
        return this.groupNo;
    }


    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }


    public boolean getIsPrintBestBeforeDate() {
        return this.isPrintBestBeforeDate;
    }


    public void setIsPrintBestBeforeDate(boolean isPrintBestBeforeDate) {
        this.isPrintBestBeforeDate = isPrintBestBeforeDate;
    }


    public boolean getIsPrintPackedDate() {
        return this.isPrintPackedDate;
    }


    public void setIsPrintPackedDate(boolean isPrintPackedDate) {
        this.isPrintPackedDate = isPrintPackedDate;
    }


    public boolean getIsPrintSellByDate() {
        return this.isPrintSellByDate;
    }


    public void setIsPrintSellByDate(boolean isPrintSellByDate) {
        this.isPrintSellByDate = isPrintSellByDate;
    }


    public boolean getIsSpecialPrice() {
        return this.isSpecialPrice;
    }


    public void setIsSpecialPrice(boolean isSpecialPrice) {
        this.isSpecialPrice = isSpecialPrice;
    }


    public boolean getIsTraceable() {
        return this.isTraceable;
    }


    public void setIsTraceable(boolean isTraceable) {
        this.isTraceable = isTraceable;
    }


    public String getItemNo() {
        return this.itemNo;
    }


    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }


    public int getLabelNoA() {
        return this.labelNoA;
    }


    public void setLabelNoA(int labelNoA) {
        this.labelNoA = labelNoA;
    }


    public int getLabelNoB() {
        return this.labelNoB;
    }


    public void setLabelNoB(int labelNoB) {
        this.labelNoB = labelNoB;
    }


    public int getLabelNoC() {
        return this.labelNoC;
    }


    public void setLabelNoC(int labelNoC) {
        this.labelNoC = labelNoC;
    }


    public String getNameTextA() {
        return this.nameTextA;
    }


    public void setNameTextA(String nameTextA) {
        this.nameTextA = nameTextA;
    }


    public String getNameTextB() {
        return this.nameTextB;
    }


    public void setNameTextB(String nameTextB) {
        this.nameTextB = nameTextB;
    }


    public int getNutritionNo() {
        return this.nutritionNo;
    }


    public void setNutritionNo(int nutritionNo) {
        this.nutritionNo = nutritionNo;
    }


    public String getPackedDate() {
        return this.packedDate;
    }


    public void setPackedDate(String packedDate) {
        this.packedDate = packedDate;
    }


    public String getPackedDateFormat() {
        return this.packedDateFormat;
    }


    public void setPackedDateFormat(String packedDateFormat) {
        this.packedDateFormat = packedDateFormat;
    }


    public String getPackedDateUnit() {
        return this.packedDateUnit;
    }


    public void setPackedDateUnit(String packedDateUnit) {
        this.packedDateUnit = packedDateUnit;
    }


    public String getPluNo() {
        return this.pluNo;
    }


    public void setPluNo(String pluNo) {
        this.pluNo = pluNo;
    }


    public boolean getPriceChangeFlagA() {
        return this.priceChangeFlagA;
    }


    public void setPriceChangeFlagA(boolean priceChangeFlagA) {
        this.priceChangeFlagA = priceChangeFlagA;
    }


    public boolean getPriceChangeFlagB() {
        return this.priceChangeFlagB;
    }


    public void setPriceChangeFlagB(boolean priceChangeFlagB) {
        this.priceChangeFlagB = priceChangeFlagB;
    }


    public int getPriceRuleNo() {
        return this.priceRuleNo;
    }


    public void setPriceRuleNo(int priceRuleNo) {
        this.priceRuleNo = priceRuleNo;
    }


    public int getPriceUnitA() {
        return this.priceUnitA;
    }


    public void setPriceUnitA(int priceUnitA) {
        this.priceUnitA = priceUnitA;
    }


    public int getPriceUnitB() {
        return this.priceUnitB;
    }


    public void setPriceUnitB(int priceUnitB) {
        this.priceUnitB = priceUnitB;
    }


    public String getPriceUnitDesA() {
        return this.priceUnitDesA;
    }


    public void setPriceUnitDesA(String priceUnitDesA) {
        this.priceUnitDesA = priceUnitDesA;
    }


    public String getPriceUnitDesB() {
        return this.priceUnitDesB;
    }


    public void setPriceUnitDesB(String priceUnitDesB) {
        this.priceUnitDesB = priceUnitDesB;
    }


    public int getSafeHandingNo() {
        return this.safeHandingNo;
    }


    public void setSafeHandingNo(int safeHandingNo) {
        this.safeHandingNo = safeHandingNo;
    }


    public String getSellByDate() {
        return this.sellByDate;
    }


    public void setSellByDate(String sellByDate) {
        this.sellByDate = sellByDate;
    }


    public String getSellByDateFormat() {
        return this.sellByDateFormat;
    }


    public void setSellByDateFormat(String sellByDateFormat) {
        this.sellByDateFormat = sellByDateFormat;
    }


    public String getSellByDateUnit() {
        return this.sellByDateUnit;
    }


    public void setSellByDateUnit(String sellByDateUnit) {
        this.sellByDateUnit = sellByDateUnit;
    }


    public double getSpecialPrice() {
        return this.specialPrice;
    }


    public void setSpecialPrice(double specialPrice) {
        this.specialPrice = specialPrice;
    }


    public String getSpecialPriceEndDate() {
        return this.specialPriceEndDate;
    }


    public void setSpecialPriceEndDate(String specialPriceEndDate) {
        this.specialPriceEndDate = specialPriceEndDate;
    }


    public String getSpecialPriceStartDate() {
        return this.specialPriceStartDate;
    }


    public void setSpecialPriceStartDate(String specialPriceStartDate) {
        this.specialPriceStartDate = specialPriceStartDate;
    }


    public int getTareNoA() {
        return this.tareNoA;
    }


    public void setTareNoA(int tareNoA) {
        this.tareNoA = tareNoA;
    }


    public float getUnitPriceA() {
        return this.unitPriceA;
    }


    public void setUnitPriceA(float unitPriceA) {
        this.unitPriceA = unitPriceA;
    }


    public int getUnitPriceB() {
        return this.unitPriceB;
    }


    public void setUnitPriceB(int unitPriceB) {
        this.unitPriceB = unitPriceB;
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


    public String getBranchId() {
        return this.branchId;
    }


    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }


    public String getPreviewImage() {
        return this.previewImage;
    }


    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }


}
