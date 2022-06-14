package com.wintec.lamp.entity;

/**
 * @author 赵冲
 * @description:
 * @date :2021/6/25 9:40
 */
public class Total {

    //总价
    private String total;
    private String price;
    private int tradeMode;

    public int getTradeMode() {
        return tradeMode;
    }

    public void setTradeMode(int tradeMode) {
        this.tradeMode = tradeMode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Total(String total, String price, int tradeMode) {
        this.total = total;
        this.price = price;
        this.tradeMode = tradeMode;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
