package com.wintec.lamp.mvp;

import io.reactivex.disposables.Disposable;

/**
 * @author 李贺翔
 * @date 2019/6/29
 * Description:
 */
public interface MvpView {
    /**
     * 显示正在加载进度框
     */
    void showLoading();

    /**
     * 显示内容布局
     */
    void showContent();

    /**
     * 显示无数据布局
     */
    void showEmptyData();

    /**
     * 显示加载错误布局
     */
    void showError();

    /**
     * 显示网络错误布局
     */
    void showNetWorkError();

    /**
     * 重新登录
     */
    void restart();

    /**
     * 隐藏软键盘
     */
    void hideSoftInput();

    /**
     * 显示软键盘
     */
    void showSoftInput();

    /**
     * 提示消息
     */
    void showMsg(String msg);

    /**
     * Rx事件管理
     */
    void addSubscribe(Disposable subscription);

    /**
     * 处理不是列表页,但需要根据错误进行不同处理的回调
     *
     * @param isNet 是否有网络
     */
    void onDataError(boolean isNet);

    /**
     * 自定义的错误码
     *
     * @param code
     */
    void onCustomError(String code);
}
