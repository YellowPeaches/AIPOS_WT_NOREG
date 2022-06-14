package com.wintec.lamp.network.rxweaver.retry;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;

/**
 * class_name: ObservableRetryDelay
 * package_name: com.runo.basemodule.rx.RxWeaver.retry
 * acthor: 小仙女
 * time: 2019/9/6 13:17
 */
public class ObservableRetryDelay implements Function<Observable<Throwable>, ObservableSource<?>> {
    private Function<Throwable, RetryConfig> provider;
    private int retryCount;

    public ObservableRetryDelay(@Nullable Function<Throwable, RetryConfig> provider) {
        if (provider == null) {
            throw new NullPointerException("The paramter preovider can't be null!");
        }
        this.provider = provider;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable
                .flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        RetryConfig retryConfig = provider.apply(throwable);
                        final long delay = retryConfig.getDelay();
                        final Throwable error = throwable;
                        if (++retryCount <= retryConfig.getMaxRetries()) {
                            return retryConfig
                                    .getRetryCondition()
                                    .call()
                                    .flatMapObservable(new Function<Boolean, ObservableSource<?>>() {
                                        @Override
                                        public ObservableSource<?> apply(Boolean retry) throws Exception {
                                            if (retry) {
                                                return Observable.timer(delay, TimeUnit.MILLISECONDS);
                                            } else {
                                                return Observable.error(error);
                                            }
                                        }
                                    });
                        }
                        return Observable.error(throwable);
                    }
                });
    }
}
