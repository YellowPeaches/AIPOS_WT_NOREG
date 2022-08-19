package com.wintec.lamp.base;

import android.os.Build;
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
public abstract class BaseMvpActivityYM<T extends BaseMvpPresenter> extends BaseActivityNewYM implements BaseMvpView {

    protected CompositeDisposable mCompositeDisposable;
    protected T mPresenter;
    private Unbinder bind;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        StatusBarUtils.setColor(this,0x000000);
//        StatusBarUtils.setTextDark(this,true);
        setTheme(R.style.BlueBG);
        setContentView(contentResId());
        ButterKnife.bind(this);
        mPresenter = createPresenter();
        if (null != mPresenter) {
            mPresenter.attachView(this);
        }
        initView(savedInstanceState);
        initData();
        initEvent();
        loadData();

        int countFeature = WtAISDK.api_getFeatureCount();
        double sleepTime = (26000.0 / 18000 * countFeature) + 1000;
        Const.DATA_LOADING_TIME = (int) sleepTime;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep((int) sleepTime);//休眠
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Const.DATA_LOADING_OK = true;
            }
        }.start();


        hideBottomUIMenu();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    hideBottomUIMenu();
                } else {
                }
            }
        });
    }

    protected void hideBottomUIMenu() {
        int flags;
        int curApiVersion = android.os.Build.VERSION.SDK_INT;
        // This work only for android 4.4+
        if (curApiVersion >= Build.VERSION_CODES.KITKAT) {
            // This work only for android 4.4+
            // hide navigation bar permanently in android activity
            // touch the screen, the navigation bar will not show
            flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        } else {
            // touch the screen, the navigation bar will show
            flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }

        // must be executed in main thread :)
        getWindow().getDecorView().setSystemUiVisibility(flags);
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

    protected abstract void initData();

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
