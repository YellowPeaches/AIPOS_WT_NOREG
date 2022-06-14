package com.wintec.lamp.utils;

import com.alibaba.fastjson.JSON;
import com.wintec.detection.WtAISDK;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.bean.MapDepot;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpUtil {
    public static String post(String url, List<MapDepot> params) throws IOException {
        RequestBody body;
        String str = null;
        try {
            str = JSON.toJSONString(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), str))
                .build();

        String result = okHttpClient.newCall(request).execute().body().string();
        System.out.println(result);
        return result;
    }

    public static void postJson(String url, String json) {
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            RequestBody requestBody = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}