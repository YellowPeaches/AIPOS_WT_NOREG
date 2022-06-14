package com.wintec.lamp.result;

import com.google.gson.annotations.SerializedName;
import com.wintec.lamp.base.BaseConstance;

import java.io.Serializable;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:08
 */
public class HttpResponse<T> implements Serializable {

    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code == null ? "" : code;
    }

    public void setCode(String code) {
        this.code = code == null ? "" : code;
    }

    /**
     * 处理成功
     *
     * @return
     */
    public boolean isRequestSuccess() {
        if ("404".equals(code) || "500".equals(code)) {
            return false;
        }
        return BaseConstance.HTTP_SUCCESS_CODE.equals(code);
    }

    /**
     * 未登录或登录已过期
     *
     * @return
     */
    public boolean isNeedRestartLogin() {
        return "401".equals(code);
    }
}
