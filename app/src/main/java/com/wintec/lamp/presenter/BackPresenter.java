package com.wintec.lamp.presenter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.wintec.lamp.api.ComModel;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.bean.DiscernData;
import com.wintec.lamp.contract.BackUpContract;
import com.wintec.lamp.network.ModelRequestCallBack;
import com.wintec.lamp.result.HttpResponse;
import com.wintec.lamp.utils.NetWorkUtil;
import com.wintec.detection.WtAISDK;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/21 11:30
 */
public class BackPresenter extends BackUpContract.Presenter {
    private ComModel comModel;

    @Override
    protected void createModel() {
        comModel = new ComModel(this);
    }


    @Override
    public void exportData() {
        int code = WtAISDK.api_exportData();
        if (code == 0) {
            getView().showSuccess("导出成功");
        } else {
            getView().showFail("导出失败，错误码为：" + code);
        }
    }

    @Override
    public void importData() {
        int code = WtAISDK.api_importData();
        if (code == 0) {
            getView().showSuccess("导入成功,请重新启动");
        } else {
            getView().showFail("导入失败，错误码为：" + code);
        }
    }

    @Override
    public void importDataByCloud() {
        int version = 0;
        String settingValue = Const.getSettingValue(Const.BACK_VERSION);
        if (settingValue == null || "".equals(settingValue)) {
            version = 0;
        } else {
            version = Integer.valueOf(settingValue);
        }
        comModel.importData(Const.getSettingValue(Const.KEY_BRANCH_ID), Const.SN, version, new ModelRequestCallBack() {

            @Override
            public void onSuccess(HttpResponse response) {
                DiscernData data = (DiscernData) response.getData();
                if (data == null) {
                    getView().showFail("云端无备份请在后台进行同步");
                } else {
                    Log.i("test", "下载版本号：" + data.getVersion());

                    String version = Const.getSettingValue(Const.BACK_VERSION);
                    Log.i("test", "本地版本号：" + version);
                    if (version == null || "".equals(version)) {
                        getView().downLoadData(data);
                    } else if (data.getVersion() >= Integer.valueOf(version)) {
                        getView().downLoadData(data);
                    } else {
                        getView().showFail("云端版本号小于本地版本");
                    }
                }
            }

            @Override
            public void onFail() {
                getView().showFail("云端无备份请在后台进行同步");
            }
        });
    }

    @Override
    public void upCloud() {
        File file = new File("/storage/emulated/0/database/Exp_WtAISDK.db");
        if (!file.exists()) {
            getView().showFail("备份云失败文件不存在");
            return;
        }
        String settingValue = Const.getSettingValue(Const.BACK_VERSION);
        int version = 0;
        if (settingValue == null || "".equals(settingValue)) {
            version = 1;
        } else {
            version = Integer.valueOf(settingValue) + 1;
        }
        Const.setSettingValue(Const.BACK_VERSION, version + "");
        Log.i("test", "上传版本号：" + version);
        comModel.exportData(file, Const.getSettingValue(Const.KEY_BRANCH_ID), Const.SN, version, new ModelRequestCallBack() {

            @Override
            public void onSuccess(HttpResponse response) {
                getView().showSuccess("成功备份云端");
            }

            @Override
            public void onFail() {
                getView().showFail("云端备份失败,请检查网络连接");

            }
        });
    }

    @Override
    public void exportDataCloud() {
        int code =0;// WtAISDK.api_exportData();
        if (code == 0) {
            upCloud();
        } else {
            getView().showFail("导出失败，错误码为：" + code);
        }
    }


}
