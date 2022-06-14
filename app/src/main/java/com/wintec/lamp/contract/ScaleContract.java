package com.wintec.lamp.contract;

import android.content.Context;

import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.view.AiPosTitleView;
import com.wintec.aiposui.view.dialog.NUIKeyDialog;
import com.wintec.lamp.base.BaseMvpPresenter;
import com.wintec.lamp.base.BaseMvpView;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.PluDto;


import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/3
 * Time: 下午4:09
 */
public interface ScaleContract {

    interface IView extends BaseMvpView {

        void showDownloadCommdities(List<PluDto> commdities);

        void showDealInsert(String id);

        void showDealImg(String id);

        void showUpdatePriceAll();

        void clearRecommondList();

        void addRecommondItem(GoodsModel goodsModel);

        void showUpImgSuccess();


    }

    abstract class Presenter extends BaseMvpPresenter<ScaleContract.IView> {

        public abstract void getDownloadCommdities(String branchId);

        public abstract void feedBack(Context context, GoodsModel goods, AiPosTitleView aiPosTitleView, String taskId);

//        public abstract void detectFeedBack(GoodsModel goods, String taskId, AiposCore core, boolean isInTop1, boolean isInTop5);
//
//        public abstract void detectFeedBackRegist(String name, String itemCode, String taskId, AiposCore core, boolean isInTop1, boolean isInTop5,String file,Context context);
//
//        public abstract void detectObject(AiposCore core);
//
//        public abstract void itemRegisterEvent(Context context, RxEvo evo, boolean isConfirm, NUIKeyDialog dialog, Commdity commdity, AiTipDialog aiTipDialog, AiposCore core);
//
//        public abstract void setImage(Context context, RxEvo evo, boolean isCameraShow);
//
//        public abstract void checkDetectReady(Context context, RxEvo evo);
//
//        public abstract void checkDoWork(Context context, RxEvo evo, boolean isEvoReady);
//
//        public abstract void FullScreenPopupWindow(Context context, RxEvo evo, AiTipDialog aiTipDialog);

        public abstract void dealInsert(Map<String, Object> map);

        public abstract void dealImg(File file, String id);


        public abstract void updateRecommondList(boolean isKg);


        public abstract void upLog(File file, String fileName, String ode);
    }

}
