package com.wintec.aiposui.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private final Subject<Object> bus;
    private HashMap<String, CompositeDisposable> hashMap;

    private RxBus() {
        bus = PublishSubject.create().toSerialized();
    }

    public static RxBus getInstance() {
        return Holder.BUS;
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }

    /**
     * 发送消息
     */

    public void post(Object obj) {
        bus.onNext(obj);
    }

    /**
     * 转换为特定类型的Obserbale
     */

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return bus.ofType(tClass);
    }

    /**
     * 订阅
     */

    public <T> Disposable doDisposable(Class<T> type, Scheduler scheduler, Consumer<T> next, Consumer<Throwable> error) {
        if (null == type || null == scheduler || null == next || null == error) {
            return null;
        }

        return toObservable(type)
                .observeOn(scheduler)
                .subscribe(next, error);
    }

    /**
     * 保存Disposable
     */

    public void saveDisposable(Object o, Disposable disposable) {
        if (null == o || null == disposable) {
            return;
        }

        if (hashMap == null) {
            hashMap = new HashMap<>();
        }

        String key = o.getClass().getName();
        if (!TextUtils.isEmpty(key)) {
            if (hashMap.get(key) != null) {
                hashMap.get(key).add(disposable);
            } else {
                CompositeDisposable compositeDisposable = new CompositeDisposable();
                compositeDisposable.add(disposable);
                hashMap.put(key, compositeDisposable);
            }
        }
    }

    /**
     * 取消订阅
     */

    public void dispose(Object o) {
        if (null == hashMap || null == o) {
            return;
        }

        String key = o.getClass().getName();
        Log.d("TAG", "取消订阅 key----:" + key);
        if (TextUtils.isEmpty(key)) {
            return;
        }

        if (!hashMap.containsKey(key)) {
            return;
        }

        CompositeDisposable compositeDisposable = hashMap.get(key);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        Log.d("TAG", "取消订阅remove key----:" + key);
        hashMap.remove(key);
    }
}
