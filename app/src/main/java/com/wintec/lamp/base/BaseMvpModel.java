package com.wintec.lamp.base;

import android.content.Context;

import com.wintec.lamp.mvp.ModelImpl;
import com.wintec.lamp.mvp.MvpModel;
import com.wintec.lamp.network.AutoFormSubscriber;
import com.wintec.lamp.network.FormRequestCallBack;
import com.wintec.lamp.network.ModelRequestCallBack;
import com.wintec.lamp.network.RetrofitSubscriber;
import com.wintec.lamp.network.rxweaver.RxSchedulers;
import com.wintec.lamp.result.HttpResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:11
 */
public class BaseMvpModel extends MvpModel {

    protected ModelImpl modelImpl;

    public BaseMvpModel(ModelImpl modelImpl) {
        this.modelImpl = modelImpl;
    }

    /**
     * 普通的网络请求
     *
     * @param observable
     * @param modelRequestCallBack
     */
    protected <T> void requestNormal(Observable<HttpResponse<T>> observable, ModelRequestCallBack<T> modelRequestCallBack) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitSubscriber<HttpResponse<T>>(modelImpl) {
                    @Override
                    protected void onSuccess(HttpResponse<T> response) {
                        modelRequestCallBack.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        modelRequestCallBack.onFail();
                    }
                });
    }

    /**
     * 需要判断登录验证的网络请求
     *
     * @param observable
     * @param modelRequestCallBack
     */
    protected <T> void requestLoginNormal(Context context, Observable<HttpResponse<T>> observable, ModelRequestCallBack<T> modelRequestCallBack) {
        observable
                .compose(RxSchedulers.handleGlobalError(context))
                .subscribe(new RetrofitSubscriber<HttpResponse<T>>(modelImpl) {
                    @Override
                    protected void onSuccess(HttpResponse<T> response) {
                        modelRequestCallBack.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    /**
     * 表单的网络请求
     *
     * @param observable
     * @param modelRequestCallBack
     * @param <T>
     */
    protected <T> void requestForm(Observable<T> observable, FormRequestCallBack<T> modelRequestCallBack) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new AutoFormSubscriber<T>(modelImpl) {
            @Override
            protected void onSuccess(T response) {
                modelRequestCallBack.onSuccess(response);
            }

            @Override
            protected void onError(String code, String message) {
                modelRequestCallBack.onError(code, message);
            }

        });
    }

    private void test() {

    }
}
