package com.wintec.lamp.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class TraceabilityCode {
    // 自增ID
    @Id(autoincrement = true)
    private Long _id;
    // 打秤码
    private String pluNo;
    // 货号
    private String itemNo;
    // 追溯码
    private String traceabilityCode;

    private int TraceCodeNum;   //追溯码号
    private int MrySymbol;      //助记符号
    private String TraceCode;   //追溯码
    private String GolCode;   //国标码

    @Override
    public String toString() {
        return "TraceabilityCode{" +
                "_id=" + _id +
                ", pluNo='" + pluNo + '\'' +
                ", itemNo='" + itemNo + '\'' +
                ", traceabilityCode='" + traceabilityCode + '\'' +
                '}';
    }

    public TraceabilityCode(String pluNo, String itemNo, String traceabilityCode) {
        this.pluNo = pluNo;
        this.itemNo = itemNo;
        this.traceabilityCode = traceabilityCode;
    }

    @Generated(hash = 365192897)
    public TraceabilityCode(Long _id, String pluNo, String itemNo,
            String traceabilityCode, int TraceCodeNum, int MrySymbol,
            String TraceCode, String GolCode) {
        this._id = _id;
        this.pluNo = pluNo;
        this.itemNo = itemNo;
        this.traceabilityCode = traceabilityCode;
        this.TraceCodeNum = TraceCodeNum;
        this.MrySymbol = MrySymbol;
        this.TraceCode = TraceCode;
        this.GolCode = GolCode;
    }

    @Generated(hash = 560989865)
    public TraceabilityCode() {
    }

    public TraceabilityCode(com.wintec.domain.TraceCode traceCode) {
        this.TraceCodeNum =traceCode.getTraceCodeNum();
        this.MrySymbol=traceCode.getMrySymbol();
        this.TraceCode =traceCode.getTraceCode();
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getPluNo() {
        return this.pluNo;
    }

    public void setPluNo(String pluNo) {
        this.pluNo = pluNo;
    }

    public String getItemNo() {
        return this.itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getTraceabilityCode() {
        return this.traceabilityCode;
    }
    public void setTraceabilityCode(String traceabilityCode) {
        this.traceabilityCode = traceabilityCode;
    }

    public int getTraceCodeNum() {
        return this.TraceCodeNum;
    }

    public void setTraceCodeNum(int TraceCodeNum) {
        this.TraceCodeNum = TraceCodeNum;
    }

    public int getMrySymbol() {
        return this.MrySymbol;
    }

    public void setMrySymbol(int MrySymbol) {
        this.MrySymbol = MrySymbol;
    }

    public String getTraceCode() {
        return this.TraceCode;
    }

    public void setTraceCode(String TraceCode) {
        this.TraceCode = TraceCode;
    }

    public String getGolCode() {
        return this.GolCode;
    }

    public void setGolCode(String GolCode) {
        this.GolCode = GolCode;
    }


}
