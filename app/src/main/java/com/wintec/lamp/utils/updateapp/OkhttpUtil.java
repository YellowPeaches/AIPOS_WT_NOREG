package com.wintec.lamp.utils.updateapp;

import com.elvishew.xlog.XLog;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author 赵冲
 * @description:
 * @date :2021/1/20 10:13
 */

public class OkhttpUtil {

    OkHttpClient okHttpClient = new OkHttpClient();

    public int get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            if (execute.code() == 200) {
                int responseData = Integer.valueOf(execute.body().string());
//                XLog.d( responseData + "");
                return responseData;
            } else {
                return -1;
            }
        } catch (IOException e) {
            XLog.e("OkhttpUtil" + e);
            return -1;
        }
    }
}
