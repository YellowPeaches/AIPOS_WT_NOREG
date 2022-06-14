package com.wintec.lamp.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.wintec.aiposui.utils.CommUtils;
import com.wintec.lamp.R;
import com.wintec.lamp.base.Const;

/**
 * @author 赵冲
 * @description:
 * @date :2021/2/22 14:12
 */
public class ImgDialog {
    private Dialog dialog;
    Context context;
    DialogPositiveListener positiveListener;
    ImageView imageView;
    TextView name_id;
    TextView price_id;
    TextView unitPrice_id;
    TextView weigth_id;
    TextView unitName;
    TextView unitPriceText;
    Button button;
    CountDownTimer timer;

    private int dialogWidth, dialogHeight;
    private int screenWidth, screenHeight;

    public ImgDialog(Context context) {
        super();
        this.context = context;
    }

    public void setPositiveListener(DialogPositiveListener positiveListener) {
        this.positiveListener = positiveListener;
    }


    public void initDialog() {
        //屏幕宽度
        View view = LayoutInflater.from(context).inflate(R.layout.goods_sacle_view, null);
        screenWidth = QMUIDisplayHelper.getScreenWidth(context);
        screenHeight = QMUIDisplayHelper.getScreenHeight(context);
        if (screenWidth > screenHeight) {
            dialogWidth = (int) (screenWidth * 0.35);
        } else {
            dialogWidth = (int) (screenWidth * 0.8);
        }
        dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT;

//        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        dialog = ResultDialog.creatAlertDialog(context, view);
        dialog.setContentView(view, new ViewGroup.LayoutParams(dialogWidth, dialogHeight));
        imageView = (ImageView) view.findViewById(R.id.img_id);
        name_id = (TextView) view.findViewById(R.id.name_id);
        price_id = (TextView) view.findViewById(R.id.price_id);
        unitPrice_id = (TextView) view.findViewById(R.id.unit_price_id);
        weigth_id = (TextView) view.findViewById(R.id.weight_id);
        button = (Button) view.findViewById(R.id.quit_id);
        unitName = (TextView) view.findViewById(R.id.unit_name);
        unitPriceText = view.findViewById(R.id.unit_text);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveListener != null) {
                    positiveListener.onClick();
                }
                dialog.dismiss();
                if (timer != null) {
                    timer.cancel();
                }
            }
        });

    }

    public void show(String url, String name, String price, String unit_price, String weight, int flag, boolean isKg) {
        String priceTemp = unit_price;
        if (!isKg) {
            priceTemp = CommUtils.Float2String(Float.parseFloat(unit_price) / 2, 2);
        }
        if (flag == 0) {
            if (isKg) {
                unitPriceText.setText("单价(元/kg)");
                unitName.setText("重量(kg)");
            } else {
                unitPriceText.setText("单价(元/500g)");
                unitName.setText("重量(kg)");
            }
        } else {
            priceTemp = unit_price;
            unitPriceText.setText("单价(元/件)");
            unitName.setText("件数(个)");
        }
        name_id.setText(name);
        price_id.setText(price);
        unitPrice_id.setText(priceTemp);
        weigth_id.setText(weight);
        Glide.with(context).load(url).placeholder(R.drawable.img_def).error(R.drawable.img_def).into(imageView);
        long millisInFuture = 0;
        if (!"".equals(Const.getSettingValue(Const.RESULT_DISPLAY_TIME))) {
            millisInFuture = Long.valueOf(Const.getSettingValue(Const.RESULT_DISPLAY_TIME));
        } else {
            millisInFuture = 1000 * 3;
        }

        timer = new CountDownTimer(millisInFuture, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                button.setText(String.format("退出（%d）s", millisUntilFinished / 1000 + 1));
            }

            @Override
            public void onFinish() {
                positiveListener.onClick();
                dialog.dismiss();
            }
        };
        timer.start();
        dialog.show();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public interface DialogPositiveListener {
        void onClick();
    }

}
