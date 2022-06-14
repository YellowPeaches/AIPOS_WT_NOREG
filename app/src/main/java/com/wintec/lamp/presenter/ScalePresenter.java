package com.wintec.lamp.presenter;

import android.content.Context;

import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.view.AiPosListView;
import com.wintec.aiposui.view.AiPosTitleView;
import com.wintec.detection.bean.DetectResult;
import com.wintec.lamp.api.ComModel;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.contract.ScaleContract;
import com.wintec.lamp.dao.PluDtoDao;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.helper.CommdityHelper;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.network.ModelRequestCallBack;
import com.wintec.lamp.result.HttpResponse;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.detection.bean.DetectResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.RequestBody;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/3
 * Time: 下午4:08
 */
public class ScalePresenter extends ScaleContract.Presenter {
    private ComModel comModel;
    private String mTaskId;
    private boolean mIsEvoReady;

    @Override
    protected void createModel() {
        comModel = new ComModel(this);
    }

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
    public void feedBack(Context context, GoodsModel goods, AiPosTitleView aiPosTitleView, String taskId) {
        this.mTaskId = taskId;
        String weight = aiPosTitleView.getWeight();

    }


    /*@Override
    public void itemRegisterEvent(Context context, RxEvo evo, boolean isConfirm, NUIKeyDialog dialog, Commdity commdity, AiTipDialog aiTipDialog, AiposCore core) {
        if (!isConfirm || commdity == null) {
            dialog.dismiss();
            return;
        }
        String name = commdity.getName();
        String code = commdity.getItemCode();
//        aiTipDialog.showLoading("注册中", context);
        *//*evo.doWork(new RxEvo.RxEvent() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                // 注册
                CameraImage res = EvoCore.getInstance(context).registerItemID(code);
                if (res == null) {
                    emitter.onNext(-1);
                    return;
                }
                // 提交
                int r = EvoCore.getInstance(context).commitRegisteredItemID(code);
                if (r == -1) {
                    emitter.onNext(-1);
                    return;
                }
                emitter.onNext(1);
            }

            @Override
            public void onNext(Object object) {
                getView().showItemRegisterEvent(object, commdity, dialog);
            }
        }, null);
        dialog.dismiss();*//*
        // 注册
        int ret = core.Aipos_Api_RegisterItem(commdity.getItemCode());
        if (ret == 10000) {
            getView().showItemRegisterEvent(1, commdity, dialog);
        } else {
            getView().showItemRegisterEvent(-1, commdity, dialog);
        }
    }*/

    @Override
    public void dealInsert(Map<String, Object> map) {
        comModel.dealInsert(map, new ModelRequestCallBack<String>() {
            @Override
            public void onSuccess(HttpResponse<String> response) {
                getView().showDealInsert(response.getData());
            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void dealImg(File file, String id) {
        comModel.dealImg(file, id, "image/jpeg", new ModelRequestCallBack<String>() {

            @Override
            public void onSuccess(HttpResponse<String> response) {
                getView().showDealImg(response.getData());
            }

            @Override
            public void onFail() {

            }
        });
    }


    @Override
    public void updateRecommondList(boolean isKg) {
        List<PluDto> list = PluDtoDaoHelper.queryRecommondCommdityByClick();
        if (list.size() > 0) {
            getView().clearRecommondList();
        }
        for (PluDto tmp : list) {
            GoodsModel parse = tmp.parse();
            parse.setKg(isKg);
            getView().addRecommondItem(parse);
        }
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


    public void detect(DetectResult detectResult, AiPosListView aiPosListView, float mNet, int status, float discount, float tempPrice, float tempTotal, int maxShow) {
        this.mTaskId = detectResult.getTaskId();
        // List<ProductModel> models = detectResult.getModels();
        List<String> productIds = detectResult.getGoodsIds();
        List<GoodsModel> data = new ArrayList<>();
        if (productIds == null) {
            aiPosListView.noResult();
            return;
        }

        int num = (productIds.size() > maxShow) ? maxShow : productIds.size();
        for (int i = 0; i < num; i++) {
            PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(productIds.get(i));
            if (commdityByScalesCode == null) {
                continue;
            }
            GoodsModel goodsModel = commdityByScalesCode.parseNet(mNet, status, discount, tempPrice, tempTotal);
            if (i == 0) {
                goodsModel.setIsFirst(true);
            }

            data.add(goodsModel);
        }
        if (data.size() == 0) {
            aiPosListView.noResult();
        } else {
            aiPosListView.showing();
        }
        aiPosListView.setData(data);
    }


}
