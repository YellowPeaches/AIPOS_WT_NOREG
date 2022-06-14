package com.wintec.lamp.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wintec.detection.WtAISDK;
import com.wintec.lamp.R;
import com.wintec.lamp.utils.TextTools;
import com.wintec.lamp.utils.ToastUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午3:20
 */
public abstract class BaseMvpActivity<T extends BaseMvpPresenter> extends BaseActivityNew implements BaseMvpView {

    protected CompositeDisposable mCompositeDisposable;
    protected T mPresenter;
    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        StatusBarUtils.setColor(this,0x000000);
//        StatusBarUtils.setTextDark(this,true);
        setContentView(contentResId());
        ButterKnife.bind(this);
        mPresenter = createPresenter();
        if (null != mPresenter) {
            mPresenter.attachView(this);
        }
        initView(savedInstanceState);
        initEvent();
        loadData();
    }

    public void jumpToActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * 布局文件
     *
     * @return
     */
    protected abstract int contentResId();

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 事件处理
     */
    protected abstract void initEvent();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    protected abstract View loadingStatusView();


    @Override
    public void showLoading() {
//        showDialog();
    }

    /**
     * 显示正常布局
     */
    @Override
    public void showContent() {
//        if (mHolder != null) {
//            mHolder.showLoadSuccess();
//        }
    }

    /**
     * 显示空布局
     */
    @Override
    public void showEmptyData() {
//        closeDialog();
//        if (loadingStatusView() != null) {
//            if (mHolder != null) {
//                mHolder.showEmpty();
//            }
//        }
    }

    @Override
    public void onCustomError(String code) {
//        closeDialog();
    }

    /**
     * 显示错误布局
     */
    @Override
    public void showError() {
//        closeDialog();
//        if (loadingStatusView() != null) {
//            if (mHolder != null) {
//                mHolder.showLoadFailed();
//            }
//        }
    }


    /**
     * 显示无网络布局
     */
    @Override
    public void showNetWorkError() {
//        closeDialog();
//        if (loadingStatusView() != null) {
//            if (mHolder != null) {
//                mHolder.showLoadFailed();
//            }
//        }
    }

    /**
     * 处理不是列表页,但需要根据错误进行不同处理的回调(子类自己处理)
     *
     * @param isNet 是否有网络
     */
    @Override
    public void onDataError(boolean isNet) {
//        closeDialog();
//        if (loadingStatusView() != null) {
//            if (mHolder != null) {
//                mHolder.showLoadFailed();
//            }
//        }
    }

    /**
     * 需要重新登录(处理token失效)
     */
    @Override
    public void restart() {
//        UserManager.getInstance().logout();
    }


    /**
     * RxJava 添加订阅者
     */
    @Override
    public void addSubscribe(Disposable subscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    /**
     * RxJava 解除所有订阅者
     */
    public void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    /**
     * 隐藏输入法
     */
    @Override
    public void hideSoftInput() {
//        TextTools.hideInput(this);
    }

    /**
     * 显示输入法
     */
    @Override
    public void showSoftInput() {
//        TextTools.showInput(this);
    }

    /**
     * 弹出Toast消息
     */
    @Override
    public void showMsg(String msg) {
        closeDialog();
        if (!TextTools.checkIsEmpty(msg)) {
            ToastUtils.showToast(msg);
        } else {
            ToastUtils.showToast("服务器出错了");
        }
    }

    /**
     * 创建Presenter
     *
     * @return
     */
    protected abstract T createPresenter();
}
