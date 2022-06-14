package com.wintec.lamp.network.rxweaver.retry;


import com.wintec.lamp.network.rxweaver.func.Suppiler;

import io.reactivex.Single;
import io.reactivex.annotations.Nullable;

/**
 * class_name: RetryConfig
 * package_name: com.runo.basemodule.rx.RxWeaver.retry
 * acthor: 小仙女
 * time: 2019/9/6 13:17
 */
public class RetryConfig {
    private static int DEFAULT_RETRY_TIMES = 1;
    private static int DEFAULT_DELAY_DURATION = 1000;
    private static Suppiler<Single<Boolean>> DEFAULT_FUNCTION = () -> Single.just(false);
    private int maxRetries;
    private int delay;
    private Suppiler<Single<Boolean>> retryCondition;

    public RetryConfig() {
        this(DEFAULT_RETRY_TIMES, DEFAULT_DELAY_DURATION, DEFAULT_FUNCTION);
    }

    public RetryConfig(int maxRetries) {
        this(maxRetries, DEFAULT_RETRY_TIMES);
    }

    public RetryConfig(int maxRetries, int delay) {
        this(maxRetries, delay, DEFAULT_FUNCTION);
    }

    public RetryConfig(Suppiler<Single<Boolean>> retryCondition) {
        this(DEFAULT_RETRY_TIMES, DEFAULT_DELAY_DURATION, retryCondition);
    }

    public RetryConfig(int maxRetries, int delay, @Nullable Suppiler<Single<Boolean>> retryCondition) {
        if (retryCondition == null) {
            throw new NullPointerException("the paramter retryCondition can't be null!");
        }
        this.maxRetries = maxRetries;
        this.delay = delay;
        this.retryCondition = retryCondition;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getDelay() {
        return delay;
    }

    public Suppiler<Single<Boolean>> getRetryCondition() {
        return retryCondition;
    }
}
