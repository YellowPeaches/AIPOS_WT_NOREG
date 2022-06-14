package com.wintec.lamp.network.yunnetwork;

import android.util.Log;


import com.wintec.lamp.network.yunnetwork.request.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequestClient {

    private final static String TAG = "HTTPREQUESTCLIENT";

    private static Retrofit retrofit;

    private static volatile ApiService request = null;

    private static OkHttpClient getOkHttpClient() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        loggingInterceptor.setLevel(level);
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        return httpClientBuilder.build();
    }

    public static Retrofit getRetrofitHttpClient() {
        if (null == retrofit) {
            synchronized (HttpRequestClient.class) {
                if (null == retrofit) {
                    retrofit = new Retrofit.Builder()
                            .client(getOkHttpClient())
//                                                       .baseUrl("http://192.168.2.64:8088/")
                            .baseUrl("http://114.115.174.123/webservice/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static ApiService getRequest() {
        if (request == null) {
            synchronized (ApiService.class) {
                request = getRetrofitHttpClient().create(ApiService.class);
            }
        }
        return request;
    }

}
