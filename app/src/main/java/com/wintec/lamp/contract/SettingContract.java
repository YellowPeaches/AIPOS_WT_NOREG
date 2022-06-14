package com.wintec.lamp.contract;

import android.content.Context;

import com.wintec.lamp.base.BaseMvpPresenter;
import com.wintec.lamp.base.BaseMvpView;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.httpdownload.DownInfo;

import java.io.File;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author 赵冲
 * @description:
 * @date :2021/6/15 13:59
 */
public interface SettingContract {

    interface IView extends BaseMvpView {
        void showDownloadCommdities(List<PluDto> commdities);

        void showUpImgSuccess();

    }

    abstract class Presenter extends BaseMvpPresenter<SettingContract.IView> {

        public abstract void getDownloadCommdities(String branchId);

        public abstract void upLog(File file, String fileName, String ode);
    }
}
