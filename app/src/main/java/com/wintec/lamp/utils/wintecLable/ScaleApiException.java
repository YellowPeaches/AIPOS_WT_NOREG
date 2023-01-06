package com.wintec.lamp.utils.wintecLable;

/**
 * @description:
 * @projectName:winteclabel
 * @see:com.wintec.Excepetion
 * @author:赵冲
 * @createTime:2022/10/25 13:12
 * @version:1.0
 */
public class ScaleApiException extends RuntimeException{
    // 错误消息内容
    private String errMsg;
    // 错误码
    private String errCode;

    public ScaleApiException(ErrCode errCode) {
        super(errCode.getMsg());
        this.errCode = errCode.getCode();
        this.errMsg = errCode.getMsg();
    }
}
