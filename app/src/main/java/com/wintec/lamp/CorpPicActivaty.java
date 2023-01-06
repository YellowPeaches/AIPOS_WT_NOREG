package com.wintec.lamp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.wintec.detection.WtAISDK;
import com.wintec.detection.bean.CameraSetting;
import com.wintec.detection.bean.ScaleBitmap;
import com.wintec.detection.camera.support.CamSupportActivity;
import com.wintec.lamp.base.BaseActivityNew;
import com.wintec.lamp.view.CropView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CorpPicActivaty extends CamSupportActivity implements View.OnClickListener {

    @BindView(R.id.tv_sure)
    TextView tvSure;

    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    @BindView(R.id.tv_crop)
    TextView tvCrop;

    @BindView(R.id.rl_Preview)
    RelativeLayout rlPreview;

    @BindView(R.id.text_camera_view)
    TextureView textureView;

    @BindView(R.id.rl_crop)
    RelativeLayout rlCrop;

    @BindView(R.id.crop_img)
    CropView cropImg;

    private static final String TAG = "CorpPicActivaty";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corp_pic_activaty);
        ButterKnife.bind(this);
        tvSure.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvCrop.setOnClickListener(this);
    }

    @Override
    public TextureView getTextureView() {
        return textureView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_crop:
                rlPreview.setVisibility(View.GONE);
                rlCrop.setVisibility(View.VISIBLE);
                final ScaleBitmap clearestScaleBitmap = api_getScaleBitmap(false);
                Bitmap raw = clearestScaleBitmap.getRaw();
                if (raw != null) {
                    if (raw.isRecycled()) {
                        raw.recycle();
                    }
                    cropImg.setImageBitmap(raw);
                } else {
                    XLog.tag(TAG).e("裁剪图片时未获取到图片");
                }
                break;
            case R.id.tv_sure:
                cropImg.cropImage(new CropView.OnCropListener() {
                    @Override
                    public void onCropFinished(final Bitmap bitmap, final int left, final int top, final int width, final int height) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                CameraSetting scaleSetting = new CameraSetting(left, top, width, height);
                                int code = -99;
                                try {
                                    code = WtAISDK.api_SaveCameraParam(scaleSetting);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    XLog.tag(TAG).e(e);
                                }

                                if (code == 0) {
                                    Intent settingIntent = getIntent();
                                    Bundle bundle = settingIntent.getExtras();
                                    if (bundle != null) {
                                        int flag = bundle.getInt(BaseActivityNew.PARAMS_RC);
                                        if (flag == 3) {
                                            Toast.makeText(CorpPicActivaty.this, "设置成功", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(CorpPicActivaty.this, "设置成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CorpPicActivaty.this, ScaleActivityUI.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(CorpPicActivaty.this, "设置成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CorpPicActivaty.this, ScaleActivityUI.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    XLog.tag(TAG).e("标定设置失败，错误码：" + code);
                                    Toast.makeText(CorpPicActivaty.this, "设置失败，错误码：" + code, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }


}