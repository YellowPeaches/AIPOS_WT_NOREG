package com.wintec.lamp.view;

import android.content.Context;
import android.media.MediaRouter;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.utils.CommUtils;
import com.wintec.aiposui.view.AiPosAccountList;

public class PresentationWrapper {

    private SecondScreenPresentation mPresentation = null;

    public void initPresentation(Context mContext) {
        MediaRouter mMediaRouter = (MediaRouter) mContext.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        if (mPresentation != null && mPresentation.getDisplay() != presentationDisplay) {
            mPresentation.dismiss();
            mPresentation = null;
        }
        if (mPresentation == null && presentationDisplay != null) {
            mPresentation = new SecondScreenPresentation(mContext, presentationDisplay);
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mPresentation = null;
            }
        }
    }

    public void setGoods(GoodsModel model, String net, int num, String total, boolean isKg, String tare) {
        if (mPresentation != null) {
            mPresentation.setGoods(model, net, num, total, isKg, tare);
        }
    }

    public void setWeight(String weight) {
        if (mPresentation != null) {
            mPresentation.setWeight(weight);
        }
    }

    public void visible() {
        if (mPresentation != null) {
            mPresentation.accountList.visible();
        }
    }

    public void hide() {
        if (mPresentation != null) {
            mPresentation.accountList.hide();
        }
    }

    public void addData(GoodsModel goodsModel) {
        if (mPresentation != null) {
            mPresentation.accountList.addData(goodsModel);
        }
    }

    public void clear() {
        if (mPresentation != null) {
            mPresentation.accountList.clear();
        }
    }
}
