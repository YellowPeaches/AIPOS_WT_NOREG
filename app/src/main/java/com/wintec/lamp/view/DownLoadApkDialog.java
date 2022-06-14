package com.wintec.lamp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.wintec.lamp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownLoadApkDialog extends Dialog {

    @BindView(R.id.version_title_up)
    AppCompatTextView versionTitleUp;
    @BindView(R.id.version_sure)
    AppCompatTextView versionSure;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_text)
    AppCompatTextView tvText;
    @BindView(R.id.iv_close_dialog)
    AppCompatImageView ivCloseDialog;

    private Context context;
    private OnClickListener onClickListener;

    public DownLoadApkDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.download_apk_dialog, null);
        ButterKnife.bind(this, view);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = 700;
        layoutParams.height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.35);
        window.setAttributes(layoutParams);
        initView();
    }

    private void initView() {
        versionSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick();
                }
            }
        });

        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void setTitleAndMsg(String title) {
        versionTitleUp.setText(title);
    }

    public void setTag(String sTag) {
        versionSure.setTag(sTag);
    }

    public String getTag() {
        return (String) versionSure.getTag();
    }

    public void setBtnText(String btnTextn) {
        versionSure.setText(btnTextn);
    }

    public void setProgressBar(long readLength, long countLength) {
        progressBar.setMax((int) countLength);
        progressBar.setProgress((int) readLength);
        tvText.setText(readLength * 100 / countLength + "%");
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick();
    }
}
