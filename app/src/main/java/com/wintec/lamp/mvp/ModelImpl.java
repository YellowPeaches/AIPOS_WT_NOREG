package com.wintec.lamp.mvp;

import io.reactivex.disposables.Disposable;

/**
 * @author 李贺翔
 * @date 2019/7/15
 * Description:
 */
public interface ModelImpl {

    void addSubscribe(Disposable d);

    void onComplete();

    void onMessage(String msg);

    void onRestartLogin();

    void onNetError();

    void onError();

    void onEmpty();

    void onCustomError(String code);

    /**
     * 处理不是列表页,但需要根据错误进行不同处理的回调
     *
     * @param isNet 是否有网络
     */
    void onDataError(boolean isNet);
}
