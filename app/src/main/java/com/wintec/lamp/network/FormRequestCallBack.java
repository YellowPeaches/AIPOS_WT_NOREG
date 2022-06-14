package com.wintec.lamp.network;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:33
 */
public interface FormRequestCallBack<T> {
    /**
     * 请求成功
     *
     * @param response
     */
    void onSuccess(T response);

    void onError(String code, String error);
}
