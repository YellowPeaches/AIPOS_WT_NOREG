package com.wintec.lamp.base;

import com.wintec.lamp.mvp.ModelImpl;
import com.wintec.lamp.mvp.MvpPresenter;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.Disposable;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午3:22
 */
public abstract class BaseMvpPresenter<V extends BaseMvpView> extends MvpPresenter implements ModelImpl {

    private WeakReference<V> mViewRef;

    public void attachView(V view) {
        mViewRef = new WeakReference<>(view);
        createModel();
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    protected V getView() {
        return mViewRef == null ? null : (V) mViewRef.get();
    }

    private boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    /**
     * 创建model
     */
    protected abstract void createModel();

    @Override
    public void addSubscribe(Disposable d) {
        if (isViewAttached()) {
            getView().addSubscribe(d);
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onMessage(String msg) {
        if (isViewAttached()) {
            getView().showMsg(msg);
        }
    }

    @Override
    public void onError() {
        if (isViewAttached()) {
            getView().showError();
        }
    }

    @Override
    public void onNetError() {
        if (isViewAttached()) {
            getView().showNetWorkError();
        }
    }

    @Override
    public void onEmpty() {
        if (isViewAttached()) {
            getView().showEmptyData();
        }
    }

    @Override
    public void onRestartLogin() {
        if (isViewAttached()) {
            getView().restart();
        }
    }

    @Override
    public void onDataError(boolean isNet) {
        if (isViewAttached()) {
            getView().onDataError(isNet);
        }
    }

    @Override
    public void onCustomError(String code) {
        if (isViewAttached()) {
            getView().onCustomError(code);
        }
    }
}
