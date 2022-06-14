package com.wintec.lamp.network.yunnetwork.schedulers;

import androidx.annotation.NonNull;

import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

public interface BaseSchedulerProvider {
    @NonNull
    Scheduler computation();

    @NonNull
    Scheduler io();

    @NonNull
    Scheduler ui();

    @NonNull
    <T> ObservableTransformer<T, T> applySchedulers();
}
