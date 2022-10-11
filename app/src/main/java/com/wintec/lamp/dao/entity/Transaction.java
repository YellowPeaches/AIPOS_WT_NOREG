package com.wintec.lamp.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Transaction {
    @Id(autoincrement = true)
    private Long id;
    // 打秤码
    private String PLU;
    // 货号
    private String itemNo;
    // 追溯码
    private String goodName;
    //交易时间
    private  Integer createDate;
    //是否打折   0-正常交易， 1-打折 2-零时改价，3-永久改价
    private  Integer transactionType;

    private double net;
    private double unitPrice;
    private double totalPrice;


    @Generated(hash = 390903647)
    public Transaction(Long id, String PLU, String itemNo, String goodName,
            Integer createDate, Integer transactionType, double net,
            double unitPrice, double totalPrice) {
        this.id = id;
        this.PLU = PLU;
        this.itemNo = itemNo;
        this.goodName = goodName;
        this.createDate = createDate;
        this.transactionType = transactionType;
        this.net = net;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
    @Generated(hash = 750986268)
    public Transaction() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPLU() {
        return this.PLU;
    }
    public void setPLU(String PLU) {
        this.PLU = PLU;
    }
    public String getItemNo() {
        return this.itemNo;
    }
    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }
    public String getGoodName() {
        return this.goodName;
    }
    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }
    public Integer getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(Integer createDate) {
        this.createDate = createDate;
    }
    public Integer getTransactionType() {
        return this.transactionType;
    }
    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }
    public double getNet() {
        return this.net;
    }
    public void setNet(double net) {
        this.net = net;
    }
    public double getUnitPrice() {
        return this.unitPrice;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public double getTotalPrice() {
        return this.totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
