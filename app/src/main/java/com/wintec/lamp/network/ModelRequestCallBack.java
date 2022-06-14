package com.wintec.lamp.network;

import com.wintec.lamp.result.HttpResponse;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:06
 */
public interface ModelRequestCallBack<T> {

    /**
     * 请求成功
     *
     * @param response
     */
    void onSuccess(HttpResponse<T> response);

    void onFail();


}
