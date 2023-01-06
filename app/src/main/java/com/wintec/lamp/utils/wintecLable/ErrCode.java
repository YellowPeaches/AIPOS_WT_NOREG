package com.wintec.lamp.utils.wintecLable;

/**
 * @description:
 * @projectName:tuoliduo
 * @see:pojo
 * @author:赵冲
 * @createTime:2021/3/31 14:23
 * @version:1.0
 */
public enum ErrCode {


    PORT_OCCUPIED_EXCEPTION("0001", "端口被占用"),
    OUT_TIME_EXCEPTION("0002", "连接超时"),
    RECEIVE_DATA_EXCEPTION("0003", "连接异常");


    private String code;
    private String msg;

    private ErrCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }




}
