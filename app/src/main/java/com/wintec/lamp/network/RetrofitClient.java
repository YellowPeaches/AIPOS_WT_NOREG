package com.wintec.lamp.network;

import com.wintec.lamp.base.BaseConstance;
import com.wintec.lamp.base.Const;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:38
 */
public class RetrofitClient {

    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = createRetrofit();
    }

    private static class RetrofitClientHolder {
        private static RetrofitClient instance = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return RetrofitClientHolder.instance;
    }

    /**
     * 生成接口实现类的实例
     */
    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BaseConstance.BASE_URL)
                .client(OkHttpUtils.getInstance().initClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                //是否在调用create(Class)时检测接口定义是否正确，而不是在调用方法才检测，适合在开发、测试时使用
                .validateEagerly(true)
                .build();
    }
}
