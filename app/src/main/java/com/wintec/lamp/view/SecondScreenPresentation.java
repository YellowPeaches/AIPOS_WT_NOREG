package com.wintec.lamp.view;

import android.app.Presentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.utils.BitmapUtils;
import com.wintec.aiposui.utils.CommUtils;
import com.wintec.aiposui.view.AiPosAccountList;
import com.wintec.aiposui.view.AiPosTitleView;
import com.wintec.aiposui.view.SecondAccountList;
import com.wintec.lamp.R;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.utils.BmpUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/28 9:59
 */
public class SecondScreenPresentation extends Presentation implements OnBannerListener {

    TextView textTare, textPrice, textTotal, textWeight, tareValue, priceValue, totalValue, weightValue, nameValue, codeValue, item_big_name;
    ConstraintLayout tvGoodModel;
    Banner mBanner;
    SecondAccountList accountList;
    private List<String> imageList;
    private List<File> images;
    private Context mContext;
    public static final String RIMG_PATH = Environment.getExternalStoragePublicDirectory("img_pre").getPath();
    public Integer time;

    public SecondScreenPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.mContext = outerContext;
        String settingValue = Const.getSettingValue(Const.IMAGE_PRE_TIME);
        if ("".equals(settingValue)) {
            time = 3000;
        } else {
            time = Integer.valueOf(settingValue);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_presentation);
        textTare = findViewById(R.id.text_tare);
        textPrice = findViewById(R.id.tv_price_txt);
        textTotal = findViewById(R.id.tv_total_txt);
        textWeight = findViewById(R.id.tv_txt_net);
        tareValue = findViewById(R.id.tare_value);
        priceValue = findViewById(R.id.price_value);
        totalValue = findViewById(R.id.total_value);
        weightValue = findViewById(R.id.weigh_value);
        nameValue = findViewById(R.id.name_vale);
        codeValue = findViewById(R.id.code_vale);
        tvGoodModel = findViewById(R.id.tv_good_model);
        accountList = findViewById(R.id.pre_account_list);
        mBanner = findViewById(R.id.mBanner);
//        item_big_name =findViewById(R.id.item_big_name);
        initBananer();
    }


    public void setWelcome(String textWelcome) {
        weightValue.setText(textWelcome);
    }

    public void setGoods(GoodsModel model, String net, int num, String total, boolean isKg, String tare) {
        String icon = "";
        if ("0".equals(tare)) {
            tareValue.setText("0.000");
        } else {
            tareValue.setText(tare);
        }
        if (model == null) {
            tvGoodModel.setVisibility(View.INVISIBLE);
//            nameValue.setText("— — — —");
//            codeValue
            priceValue.setText("0.00");
            if (isKg) {
                textPrice.setText("单价(元/kg)");
            } else {
                textPrice.setText("单价(元/500g)");
            }
            weightValue.setText("0.000");
            textWeight.setText("重量(kg)");
            totalValue.setText("0.00");
        } else {
//            icon = ROOT_PATH + model.getItemNo() + ".jpg";
//            if ("".equals(model.getPreviewFlag())
//                    || "0".equals(model.getPreviewFlag())
//                    || model.getPreviewFlag() == null
//                    || !new File(icon).exists()){
//                item_big_name.setText(model.getGoodsName());
//            }else{
//                Glide.with(mContext)
//                        .asBitmap()
//                        .format(DecodeFormat.PREFER_RGB_565)
//                        .skipMemoryCache(false)
//                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                        .dontAnimate()
//                        .load(icon)
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                itemPreview.setImageBitmap(resource);
//                            }
//                        });
//
//            }
            tvGoodModel.setVisibility(View.VISIBLE);
            nameValue.setText(model.getGoodsName());
            codeValue.setText(model.getGoodsId());
            if (isKg) {
                priceValue.setText(model.getPrice());
            } else {
                priceValue.setText(CommUtils.Float2String(Float.parseFloat(model.getPrice()) / 2, 2));
            }
            if (model.getUnitId() == 0) {
                if (isKg) {
                    textPrice.setText("单价(元/kg)");
                } else {
                    textPrice.setText("单价(元/500g)");
                }
                textWeight.setText("重量(kg)");
                weightValue.setText(net);
            } else {
                textPrice.setText("单价(元/件)");
                textPrice.setText("件");
                priceValue.setText(num + "");
            }

            totalValue.setText(total);
        }
    }

    public SecondAccountList getAccountList() {
        return accountList;
    }

    public void setWeight(String weight) {
        weightValue.setText(weight);
    }


    private void initImgDate() {
        imageList = new ArrayList<>();
        File file = new File(RIMG_PATH);
        if (file.exists() && file.isDirectory()) {
            List<File> files = Arrays.asList(file.listFiles());
            List<File> imgs = files.stream().filter(file1 -> {
                return file1.getName().contains("jpg") || file1.getName().contains("png");
            }).collect(Collectors.toList());

            imgs.forEach(item -> {
                imageList.add(item.getAbsolutePath());
            });
        }
        if (imageList.size() == 0) {

            //把控件添加到集合ImageViews中去,以方便在VIewPager的适配器里instantiateItem方法获取.
            imageList.add(imageTranslateUri(R.mipmap.kexian));
        }
    }

    private void initBananer() {
        initImgDate();
        //设置轮播的动画效果,里面有很多种特效,可以到GitHub上查看文档。
        mBanner.setBannerAnimation(Transformer.Accordion);
        mBanner.setImages(imageList);//设置图片资源
        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);//设置banner显示样式（带标题的样式）
        //设置指示器位置（即图片下面的那个小圆点）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);

        mBanner.setDelayTime(time);//设置轮播时间3秒切换下一图
        mBanner.setOnBannerListener(this);//设置监听
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        mBanner.start();//开始进行banner渲染

//            //设置图片加载器，通过Glide加载图片


    }

    @Override
    public void OnBannerClick(int position) {

    }

    private String imageTranslateUri(int resId) {

        Resources r = getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId));

        return uri.toString();
    }

}
