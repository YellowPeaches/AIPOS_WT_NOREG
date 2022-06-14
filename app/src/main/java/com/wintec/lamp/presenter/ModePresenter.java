package com.wintec.lamp.presenter;

import android.content.Context;
import android.os.Handler;

import com.wintec.aiposui.view.dialog.NUIKeyDialog;
import com.wintec.lamp.entity.vo.ModeVo;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.lamp.view.ScaleView;

/**
 * @author 赵冲
 * @description:
 * @date :2021/2/23 14:25
 */
public class ModePresenter {

    public static ModeVo modeVo;
    private static ModePresenter modePresenter;

    private ScaleView scaleView;
    private Context context;
    private Handler handler = new Handler();

    private ModePresenter(ScaleView scaleView) {
        this.scaleView = scaleView;
        this.context = (Context) scaleView;
        modeVo = new ModeVo();
    }

    public static ModePresenter getInstance(ScaleView scaleView) {
        if (modePresenter == null) {
            modePresenter = new ModePresenter(scaleView);
            return modePresenter;
        } else {
            return modePresenter;
        }
    }

    /**
     * 判断下一单是否折扣
     *
     * @return
     */
    public Boolean isDisCount() {
        if (modeVo.getTradeMode() == ModeVo.MODE_DISCOUNT_TRADE) {
            modeVo.setTradeMode(ModeVo.MODE_NORMAL_TRADE);
            modeVo.setDiscount(1f);
            modeVo.setTempPrice(0);
            scaleView.clearMode();
            return false;
        }
        return true;
    }

    /**
     * 折扣
     *
     * @param code
     */
    public void disCount(String code, NUIKeyDialog dialog) {
        float discount;
        if (!code.contains(".")) {
            if (Integer.parseInt(code) == 0) {
                return;
            }
            discount = Float.parseFloat(code.replaceAll("^(0+)", "")) / 10;
            if (discount <= 0 || discount >= 1) {
                modeVo.setDiscount(1f);
                return;
            }
        }
        // 处理带小数的折扣
        else {
            if (!code.startsWith(".")) {
                if (CommUtils.isNumeric(code)) {
                    discount = Float.parseFloat(code) / 10;
                    if (discount <= 0 || discount >= 1) {
                        modeVo.setDiscount(1f);
                        return;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        dialog.dismiss();
        modeVo.setDiscount(discount);
        modeVo.setTradeMode(ModeVo.MODE_DISCOUNT_TRADE);
        scaleView.disCount(String.valueOf(discount));
    }

    /**
     * 判断下一单是否改价
     *
     * @return
     */
    public Boolean isChangPrice() {
        if (modeVo.getTradeMode() == ModeVo.MODE_CHANGE_PRICE_TRADE) {
            modeVo.setTradeMode(ModeVo.MODE_NORMAL_TRADE);
            modeVo.setDiscount(1f);
            modeVo.setTempPrice(0);
            scaleView.clearMode();
            return false;
        }
        return true;
    }

    /**
     * 临时改价
     *
     * @param price
     * @param dialog
     */
    public void changPrice(String price, NUIKeyDialog dialog) {
        if (!opinionPrice(price)) {
            return;
        }
        modeVo.setTempPrice(Float.parseFloat(price));
        modeVo.setTradeMode(ModeVo.MODE_CHANGE_PRICE_TRADE);
        dialog.dismiss();
        //  hintText.setText("下一单商品的单价为"+tempPrice+"元/KG");
    }


    //价格判断
    private Boolean opinionPrice(String price) {
        if (price == null || price.equals("")) {
            //todo 提示
            return false;
        }
        // 对价格合理性进行检测
        if (!price.contains(".")) {
            price = price.replaceAll("^(0+)", "");
            if (price == null || price.equals("")) {
                price = "0";
            }
        } else {
            if (!price.startsWith(".")) {
                if (!CommUtils.isNumeric(price)) {
                    // 提示输入正确单价
                    CommUtils.showMessage(context, "请输入正确单价");
                    return false;
                }

            } else {
                // 提示输入正确单价
                CommUtils.showMessage(context, "请输入正确单价");
                return false;
            }
        }
        if (Float.parseFloat(price) == 0) {
            // CommUtils.showMessage(mContext, "请输入正确单价");
            return false;
        }
        return true;
    }

}
