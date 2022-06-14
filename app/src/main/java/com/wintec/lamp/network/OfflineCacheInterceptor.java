package com.wintec.lamp.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:42
 */
public class OfflineCacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        int onlineCacheTime = 30;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
        return chain.proceed(chain.request()).newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=" + onlineCacheTime)
                .build();
    }
}
