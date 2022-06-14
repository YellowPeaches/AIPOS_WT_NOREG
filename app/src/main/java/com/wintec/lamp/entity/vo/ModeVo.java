package com.wintec.lamp.entity.vo;

/**
 * @author 赵冲
 * @description:
 * @date :2021/2/23 13:39
 */
public class ModeVo {

    public final static int MODE_NORMAL_TRADE = 1;    // 正常交易状态
    public final static int MODE_DISCOUNT_TRADE = 3;    // 下一单折扣状态
    public final static int MODE_CHANGE_PRICE_TRADE = 4;    // 下一单改价状态
    public final static int MODE_CHANGE_TOTAL_TRADE = 5;    // 下一单总价状态
    private float discount = 1f;         // 折扣
    private float tempPrice = 0;
    private int tradeMode;

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getTempPrice() {
        return tempPrice;
    }

    public void setTempPrice(float tempPrice) {
        this.tempPrice = tempPrice;
    }

    public int getTradeMode() {
        return tradeMode;
    }

    public void setTradeMode(int tradeMode) {
        this.tradeMode = tradeMode;
    }
}
