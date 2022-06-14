package com.wintec.lamp.utils.updateapp;

import android.util.Log;

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
                Log.i("responseData", responseData + "");
                return responseData;
            } else {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
