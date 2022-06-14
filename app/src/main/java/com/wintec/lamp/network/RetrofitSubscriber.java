package com.wintec.lamp.network;

import android.text.TextUtils;
import android.util.Log;

import com.wintec.aiposui.model.BtnType;
import com.wintec.aiposui.utils.RxBus;
import com.wintec.lamp.BuildConfig;
import com.wintec.lamp.R;
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
 * Time: 下午4:12
 */
public abstract class RetrofitSubscriber<T> implements Observer<T> {
    private ModelImpl modelImpl;

    protected RetrofitSubscriber(ModelImpl modelImpl) {
        super();
        this.modelImpl = modelImpl;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (!NetworkUtils.isConnected(ContextUtils.getApp())) {
            d.dispose();
            modelImpl.onError();
            modelImpl.onMessage(ContextUtils.getApp().getString(R.string.no_net));
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
                modelImpl.onMessage(((HttpResponse) response).getMsg());
                modelImpl.onDataError(true);
                modelImpl.onCustomError(((HttpResponse) response).getCode());
                RxBus.getInstance().post(new BtnType("requestError"));
            }
        }
    }

    protected abstract void onSuccess(T response);
}
