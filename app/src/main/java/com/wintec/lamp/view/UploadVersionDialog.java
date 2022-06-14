package com.wintec.lamp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.wintec.lamp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadVersionDialog extends Dialog {

    @BindView(R.id.version_title_up)
    AppCompatTextView versionTitleUp;
    @BindView(R.id.version_message)
    AppCompatTextView versionMessage;
    @BindView(R.id.version_cancel)
    AppCompatTextView versionCancel;
    @BindView(R.id.version_sure)
    AppCompatTextView versionSure;


    private Context context;
    private OnClickListener onClickListener;

    public UploadVersionDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.upload_version_dialog, null);
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
        versionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(1);
                }
            }
        });

        versionSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(2);
                }
            }
        });

    }

    public void setTitleAndMsg(String title, String msg) {
        StringBuffer sb = new StringBuffer();
        if (!TextUtils.isEmpty(msg) && msg != null) {
            String[] s = msg.split("。");
            for (int i = 0; i < s.length; i++) {
                sb.append(s[i] + "\n");
            }
        }
        versionMessage.setText(sb);
        versionTitleUp.setText(title);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int type);
    }
}
