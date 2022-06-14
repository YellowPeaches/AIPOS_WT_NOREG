package com.wintec.lamp.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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

    @Generated(hash = 1038793500)
    public TraceabilityCode(Long _id, String pluNo, String itemNo,
                            String traceabilityCode) {
        this._id = _id;
        this.pluNo = pluNo;
        this.itemNo = itemNo;
        this.traceabilityCode = traceabilityCode;
    }

    @Generated(hash = 560989865)
    public TraceabilityCode() {
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


}
