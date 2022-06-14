package com.wintec.lamp.network;

import android.util.Log;

import com.wang.avi.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:39
 */
public class OkHttpUtils {

    private OkHttpClient.Builder builder;

    private OkHttpUtils() {
    }

    private static class OkHttpClientHolder {
        private static OkHttpUtils instance = new OkHttpUtils();
    }

    public static OkHttpUtils getInstance() {
        return OkHttpClientHolder.instance;
    }

    public OkHttpClient initClient() {
        if (null == builder) {
            builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addNetworkInterceptor(loggingInterceptor);
            builder.connectTimeout(15, TimeUnit.SECONDS);
            builder.readTimeout(15, TimeUnit.SECONDS);
            builder.writeTimeout(15, TimeUnit.SECONDS);
            builder.addInterceptor(new NetCacheInterceptor());
            builder.addNetworkInterceptor(new OfflineCacheInterceptor());
//            File httpCacheDirectory = new File(FileUtils.getAppStorageFile(FileUtils.StorageEnum.APP_IN_PACKAGE, FileUtils.FileEnum.FILE), "OkHttpCache");
//            //设置缓存路径和缓存容量(20 MiB)
//            builder.cache(new Cache(httpCacheDirectory, 20 * 1024 * 1024));
//            builder.retryOnConnectionFailure(true);
        }
        return builder.build();
    }

    private static class HttpLogger implements HttpLoggingInterceptor.Logger {

        @Override
        public void log(@NotNull String message) {
            if (BuildConfig.DEBUG) {
                Log.d("request", message);
            }
        }
    }
}
