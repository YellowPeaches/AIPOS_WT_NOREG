package com.wintec.lamp.network;

import com.wintec.aiposui.model.BtnType;
import com.wintec.aiposui.utils.RxBus;
import com.wintec.lamp.mvp.ModelImpl;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:20
 * 错误归类
 */
public class NetWorkError {

    private static final int NOT_FOUND = 404;

    public static void dealError(ModelImpl modelImpl, Throwable throwable) {
        String msg = null;
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            if (httpException.code() == NOT_FOUND) {
                msg = "接口未找到";
            }
        } else if (throwable instanceof ServerException) {
            msg = "服务器错误";
        } else if (throwable instanceof JSONException
                || throwable instanceof ParseException) {
            msg = "解析错误";
        } else if (throwable instanceof ConnectException
                || throwable instanceof SocketTimeoutException
                || throwable instanceof UnknownHostException) {
            msg = "服务器不可用";
        } else if (throwable instanceof FileNotFoundException) {
            msg = "文件未找到";
        } else if (throwable instanceof SSLHandshakeException) {
            msg = "证书验证失败";
        } else {
            msg = "未知错误";
        }
        modelImpl.onError();
        modelImpl.onMessage(msg);
        RxBus.getInstance().post(new BtnType("requestError"));
    }

}
