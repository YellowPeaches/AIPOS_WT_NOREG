package com.wintec.lamp.utils;

import android.Manifest;

import androidx.fragment.app.FragmentActivity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PicSelectorUtils {
    public static void openCamera(FragmentActivity context, int requestCode) {
        RxPermissions rxPermissions = new RxPermissions(context);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean booleans) {
                        if (booleans) {
                            PictureSelector.create(context)
                                    .openCamera(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                                    .theme(R.style.picture_cas_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                                    .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                                    .isPreviewImage(true)// 是否可预览图片
                                    .querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())
                                    .isCompress(true)//是否压缩
                                    .compressQuality(85)//压缩质量
//                                    .compressSavePath(FileUtils.getRootAbPath(context))
                                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                                    .isEnableCrop(true)
                                    .withAspectRatio(16, 9)
                                    .freeStyleCropEnabled(true)
                                    .showCropFrame(true)
                                    .scaleEnabled(true)
                                    .isDragFrame(true)
                                    .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                                    .isDragFrame(false)// 是否可拖动裁剪框(固定)
                                    .minimumCompressSize(2048)// 小于100kb的图片不压缩
                                    .rotateEnabled(true) // 裁剪是否可旋转图片
                                    .scaleEnabled(true)// 裁剪是否可放大缩小图片
                                    .forResult(requestCode);//结果回调onActivityResult code
                        } else {
                            ToastUtils.showToast("请开启相关权限");
//                            Toast.makeText(context, "请开启相关权限", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

}
