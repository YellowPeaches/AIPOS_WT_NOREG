package com.wintec.lamp.network.rxweaver;

import android.app.AlertDialog;
import android.content.Context;

import com.wintec.lamp.network.rxweaver.core.GlobalErrorTrabsformer;
import com.wintec.lamp.network.rxweaver.func.ConnectFailedAlertDialogException;
import com.wintec.lamp.network.rxweaver.func.Suppiler;
import com.wintec.lamp.network.rxweaver.func.TokenExpiredException;
import com.wintec.lamp.network.rxweaver.retry.RetryConfig;
import com.wintec.lamp.result.HttpResponse;
import com.wintec.lamp.view.NavigatorFragment;

import java.net.ConnectException;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * class_name: RxSchedulers
 * package_name: com.runo.baselib.net
 * acthor: 小仙女
 * time: 2020/2/24 9:24
 */
public class RxSchedulers {
    private static final int STATUS_UNAUTHPRIZED = 401;

    public static <T extends HttpResponse> GlobalErrorTrabsformer<T> handleGlobalError(Context activity) {
        return new GlobalErrorTrabsformer<T>(
                new Function<T, Observable<T>>() {
                    @Override
                    public Observable<T> apply(T t) throws Exception {
                        if ("101".equals(t.getCode()) || "102".equals(t.getCode())) { //未登录或者登陆失效返回code
                            return Observable.error(new TokenExpiredException());
                        }
                        return Observable.just(t);
                    }
                },
                new Function<Throwable, Observable<T>>() {
                    @Override
                    public Observable<T> apply(Throwable throwable) throws Exception {
                        if (throwable instanceof ConnectException) {
                            return Observable.error(new ConnectFailedAlertDialogException());
                        }
                        return Observable.error(throwable);
                    }
                },
                new Function<Throwable, RetryConfig>() {
                    @Override
                    public RetryConfig apply(Throwable throwable) throws Exception {
                        if (throwable instanceof TokenExpiredException) {
                            return new RetryConfig(1, 400,
                                    new Suppiler<Single<Boolean>>() {
                                        @Override
                                        public Single<Boolean> call() {
                                            return new NavigatorFragment()
                                                    .startLoginForResult(activity)
                                                    .flatMap(new Function<Boolean, SingleSource<? extends Boolean>>() {
                                                        @Override
                                                        public SingleSource<? extends Boolean> apply(Boolean aBoolean) throws Exception {
                                                            return Single.just(aBoolean);
                                                        }
                                                    });
                                        }
                                    });
                        }
                        return new RetryConfig();
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }
        ) {
        };
    }

    public Single<Boolean> showErrorDialog(Context context, String message) {
        return Single.create(emitter -> {
            new AlertDialog.Builder(context)
                    .setTitle("错误")
                    .setMessage("您收到了一个错误，是否重试本次请求")
                    .setCancelable(false)
                    .setPositiveButton("重试", (dialog, which) -> {
                        emitter.onSuccess(true);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        emitter.onSuccess(false);
                    })
                    .show();
        });
    }
}
