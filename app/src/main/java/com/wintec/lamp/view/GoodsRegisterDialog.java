package com.wintec.lamp.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.wintec.lamp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsRegisterDialog extends Dialog {

    @BindView(R.id.iv_close)
    AppCompatImageView ivClose;
    @BindView(R.id.tv_goods_name_dialog)
    AppCompatTextView tvGoodsName;
    @BindView(R.id.tv_goods_camera)
    AppCompatTextView tvGoodsCamera;
    @BindView(R.id.tv_goods_submit)
    AppCompatTextView tvGoodsSubmit;
    @BindView(R.id.iv_img)
    AppCompatImageView ivImg;

    private Context context;
    private OnClickListener onClickListener;

    public GoodsRegisterDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.view_goods_register_dialog, null);
        ButterKnife.bind(this, view);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = 1700;
        layoutParams.height = 800;
        window.setAttributes(layoutParams);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();
    }

    private void initView() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        tvGoodsCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(1);
                }
            }
        });

        tvGoodsSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(2);
                }
            }
        });
    }

    public void setTvGoodsName(String name) {
        tvGoodsName.setText(name);
    }

    public void setBitmap(Bitmap bitmap) {
        ivImg.setImageBitmap(bitmap);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int type);
    }
}
