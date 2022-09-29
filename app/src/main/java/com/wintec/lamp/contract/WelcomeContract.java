package com.wintec.lamp.contract;

import android.content.Context;

import com.wintec.lamp.base.BaseMvpPresenter;
import com.wintec.lamp.base.BaseMvpView;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.httpdownload.DownInfo;

import java.io.File;

import okhttp3.RequestBody;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午5:33
 */
public interface WelcomeContract {

    interface IView extends BaseMvpView {

        void showCheckUpDate(File file);

        void showDownloading(int progress);

        void showDownloadFailed();


        void showRegister(registerBean registerBean);

        void showCheckVersion(VersionBean bean);

        void showAppState();

        void showAppDownInfo(DownInfo downInfo);


    }

    abstract class Presenter extends BaseMvpPresenter<WelcomeContract.IView> {

        public abstract void getCheckUpDate(Context context, String url);

        //public abstract void updatePriceAll(RequestBody body);

        public abstract void posRegister(RequestBody body);

        public abstract void checkVersion();

        public abstract void getAppState(RequestBody body);

        public abstract void getAppDownInfo();

        public abstract void upplus();

        public abstract void getImgUrl();

        public abstract void upLogTxt(File file, String fileName, String code);
    }
}
