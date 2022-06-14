package com.wintec.lamp.presenter;

import com.wintec.lamp.api.ComModel;
import com.wintec.lamp.contract.SettingContract;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.network.ModelRequestCallBack;
import com.wintec.lamp.result.HttpResponse;

import java.io.File;
import java.util.List;

/**
 * @author 赵冲
 * @description:
 * @date :2021/6/15 13:59
 */
public class SettingPresenter extends SettingContract.Presenter {

    private ComModel comModel;

    @Override
    public void getDownloadCommdities(String branchId) {
        comModel.downloadCommdities(branchId, new ModelRequestCallBack<List<PluDto>>() {

            @Override
            public void onSuccess(HttpResponse<List<PluDto>> response) {
                getView().showDownloadCommdities(response.getData());
            }

            @Override
            public void onFail() {

            }

        });
    }

    @Override
    protected void createModel() {
        comModel = new ComModel(this);
    }


    @Override
    public void upLog(File file, String fileName, String code) {
        comModel.upLog(file, fileName, code, new ModelRequestCallBack<String>() {

            @Override
            public void onSuccess(HttpResponse<String> response) {
                getView().showUpImgSuccess();
            }

            @Override
            public void onFail() {

            }
        });
    }
}
