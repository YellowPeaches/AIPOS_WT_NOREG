package com.wintec.lamp.network.rxweaver.core;

import com.wintec.lamp.network.rxweaver.func.Suppiler;
import com.wintec.lamp.network.rxweaver.retry.FlowableRetryDelay;
import com.wintec.lamp.network.rxweaver.retry.ObservableRetryDelay;
import com.wintec.lamp.network.rxweaver.retry.RetryConfig;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * class_name: GlobalErrorTrabsformer
 * package_name: com.runo.baselib.net
 * acthor: 小仙女
 * time: 2020/2/24 9:28
 */
public class GlobalErrorTrabsformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer {
    private static Suppiler<Scheduler> SCHEDULER_PROVITDER_DEFAULT = new Suppiler<Scheduler>() {
        @Override
        public Scheduler call() {
            return AndroidSchedulers.mainThread();
        }
    };
    private static Suppiler<Scheduler> SCHEDULER_NETWORK_DEFAULT = new Suppiler<Scheduler>() {
        @Override
        public Scheduler call() {
            return Schedulers.io();
        }
    };
    private Suppiler<Scheduler> upStreamSchedulerProvider;
    private Suppiler<Scheduler> downStreamSchedulerProvider;
    private Suppiler<Scheduler> netWorkSchedulerProvider;
    private Function<T, Observable<T>> globalOnnextRetryIntercepter;
    private Function<Throwable, Observable<T>> globalOnErrorResume;
    private Function<Throwable, RetryConfig> retryConfigProvider;
    private Consumer<Throwable> globalDoOnErrorConsumer;

    public GlobalErrorTrabsformer(Function<T, Observable<T>> globalOnNextRetryIntercepter,
                                  Function<Throwable, Observable<T>> globalOnErrorResume,
                                  Function<Throwable, RetryConfig> retryConfigProvider,
                                  Consumer<Throwable> globalDoOnErrorConsumer
    ) {
        this(
                SCHEDULER_PROVITDER_DEFAULT,
                SCHEDULER_PROVITDER_DEFAULT,
                SCHEDULER_NETWORK_DEFAULT,
                globalOnNextRetryIntercepter,
                globalOnErrorResume,
                retryConfigProvider,
                globalDoOnErrorConsumer
        );
    }

    public GlobalErrorTrabsformer(Suppiler<Scheduler> upStreamSchedulerProvider,
                                  Suppiler<Scheduler> downStreamSchedulerProvider,
                                  Suppiler<Scheduler> networkSchedulerProvider,
                                  Function<T, Observable<T>> globalOnNextRetryInterceptor,
                                  Function<Throwable, Observable<T>> globalOnErrorResume,
                                  Function<Throwable, RetryConfig> retryConfigProvider,
                                  Consumer<Throwable> globalDoOnErrorConsumer) {
        this.upStreamSchedulerProvider = upStreamSchedulerProvider;
        this.downStreamSchedulerProvider = downStreamSchedulerProvider;
        this.netWorkSchedulerProvider = networkSchedulerProvider;
        this.globalOnnextRetryIntercepter = globalOnNextRetryInterceptor;
        this.globalOnErrorResume = globalOnErrorResume;
        this.retryConfigProvider = retryConfigProvider;
        this.globalDoOnErrorConsumer = globalDoOnErrorConsumer;
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .onErrorResumeNext(new Function<Throwable, CompletableSource>() {
                    @Override
                    public CompletableSource apply(Throwable throwable) throws Exception {
                        return globalOnErrorResume.apply(throwable)
                                .ignoreElements();
                    }
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .subscribeOn(netWorkSchedulerProvider.call())
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap(new Function<T, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(T t) throws Exception {
                        return globalOnnextRetryIntercepter.apply(t)
                                .toFlowable(BackpressureStrategy.BUFFER);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(Throwable throwable) throws Exception {
                        return globalOnErrorResume.apply(throwable)
                                .toFlowable(BackpressureStrategy.BUFFER);
                    }
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .subscribeOn(netWorkSchedulerProvider.call())
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap(new Function<T, MaybeSource<T>>() {
                    @Override
                    public MaybeSource<T> apply(T t) throws Exception {
                        return globalOnnextRetryIntercepter.apply(t)
                                .firstElement();
                    }
                })
                .onErrorResumeNext(new Function<Throwable, MaybeSource<? extends T>>() {
                    @Override
                    public MaybeSource<? extends T> apply(Throwable throwable) throws Exception {
                        return globalOnErrorResume.apply(throwable)
                                .firstElement();
                    }
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .subscribeOn(netWorkSchedulerProvider.call())
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T t) throws Exception {
                        return globalOnnextRetryIntercepter.apply(t);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                        return globalOnErrorResume.apply(throwable);
                    }
                })
                .retryWhen(new ObservableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .subscribeOn(netWorkSchedulerProvider.call())
                .observeOn(downStreamSchedulerProvider.call());
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .observeOn(upStreamSchedulerProvider.call())
                .flatMap(new Function<T, SingleSource<? extends T>>() {
                    @Override
                    public SingleSource<? extends T> apply(T t) throws Exception {
                        return globalOnnextRetryIntercepter.apply(t)
                                .firstOrError();
                    }
                })
                .onErrorResumeNext(new Function<Throwable, SingleSource<? extends T>>() {
                    @Override
                    public SingleSource<? extends T> apply(Throwable throwable) throws Exception {
                        return globalOnErrorResume.apply(throwable)
                                .firstOrError();
                    }
                })
                .retryWhen(new FlowableRetryDelay(retryConfigProvider))
                .doOnError(globalDoOnErrorConsumer)
                .subscribeOn(netWorkSchedulerProvider.call())
                .observeOn(downStreamSchedulerProvider.call());
    }
}
