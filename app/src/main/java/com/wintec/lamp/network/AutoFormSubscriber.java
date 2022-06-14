package com.wintec.lamp.network;

import android.text.TextUtils;
import android.util.Log;

import com.wintec.lamp.BuildConfig;
import com.wintec.lamp.mvp.ModelImpl;
import com.wintec.lamp.result.HttpResponse;
import com.wintec.lamp.utils.ContextUtils;
import com.wintec.lamp.utils.NetworkUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:34
 */
public abstract class AutoFormSubscriber<T> implements Observer<T> {

    private ModelImpl modelImpl;


    protected AutoFormSubscriber(ModelImpl modelImpl) {
        super();
        this.modelImpl = modelImpl;

    }

    @Override
    public void onSubscribe(Disposable d) {
        if (!NetworkUtils.isConnected(ContextUtils.getApp())) {
            d.dispose();
            modelImpl.onDataError(false);
//            modelImpl.onMessage(ContextUtils.getApp().getString(R.string.no_net));
        } else {
            modelImpl.addSubscribe(d);
        }
    }

    @Override
    public void onComplete() {
        modelImpl.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        NetWorkError.dealError(modelImpl, e);
        if (BuildConfig.DEBUG && !TextUtils.isEmpty(e.getMessage())) {
            Log.e("request", e.getMessage());
        }

    }

    @Override
    public void onNext(T response) {
        if (response instanceof HttpResponse) {
            if (((HttpResponse) response).isRequestSuccess()) {
                onSuccess(response);
            } else if (((HttpResponse) response).isNeedRestartLogin()) {
                modelImpl.onRestartLogin();
            } else {
                onError(((HttpResponse) response).getCode(), ((HttpResponse) response).getMsg());
                modelImpl.onDataError(true);
            }
        }
    }

    protected abstract void onSuccess(T response);

    protected abstract void onError(String code, String message);
}
