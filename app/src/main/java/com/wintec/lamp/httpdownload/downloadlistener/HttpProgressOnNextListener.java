package com.wintec.lamp.httpdownload.downloadlistener;

public abstract class HttpProgressOnNextListener<T> {

    /**
     * 成功后回调方法
     *
     * @param t 下载成功之后文件信息
     */
    public abstract void onNext(T t);


    /**
     * 完成下载
     * 主动调用，更加灵活
     */
    public void onComplete() {

    }


    /**
     * 下载进度
     *
     * @param readLength  当前下载进度
     * @param countLength 文件总长度
     */
    public abstract void updateProgress(long readLength, long countLength);

    /**
     * 失败或者错误方法
     * 主动调用，更加灵活
     *
     * @param e 下载失败异常
     */
    public void onError(Throwable e) {

    }
}
