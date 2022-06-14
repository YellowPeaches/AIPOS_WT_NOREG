package com.wintec.lamp.network.rxweaver.retry;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * class_name: FlowableRetryDelay
 * package_name: com.runo.basemodule.rx.RxWeaver.retry
 * acthor: 小仙女
 * time: 2019/9/6 13:17
 */
public class FlowableRetryDelay implements Function<Flowable<Throwable>, Publisher<?>> {
    private Function<Throwable, RetryConfig> provider;
    private int retryCount;

    public FlowableRetryDelay(Function<Throwable, RetryConfig> provider) {
        if (provider == null)
            throw new NullPointerException("The paramter provider can't be null!");
        this.provider = provider;
    }

    @Override
    public Publisher<?> apply(Flowable<Throwable> throwableFlowable) {
        return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Throwable throwable) throws Exception {
                RetryConfig retryConfig = provider.apply(throwable);
                final long delay = retryConfig.getDelay();
                final Throwable error = throwable;
                if (++retryCount <= retryConfig.getMaxRetries()) {
                    return retryConfig.getRetryCondition().call()
                            .flatMapPublisher(new Function<Boolean, Publisher<?>>() {
                                @Override
                                public Publisher<?> apply(Boolean retry) throws Exception {
                                    if (retry) {
                                        return Flowable.timer(delay, TimeUnit.MILLISECONDS);
                                    } else {
                                        return Flowable.error(error);
                                    }
                                }
                            });
                }
                return Flowable.error(throwable);
            }
        });
    }
}
