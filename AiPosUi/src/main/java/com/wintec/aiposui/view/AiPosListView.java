package com.wintec.aiposui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wintec.aiposui.R;
import com.wintec.aiposui.adapter.CommonViewItemAdapter;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.utils.BitmapUtils;
import com.wintec.aiposui.utils.CommUtils;
import com.wintec.aiposui.utils.GlideCacheUtil;
import com.wintec.aiposui.utils.ImageUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @描述：
 * @文件名: AiDiscern.AiDiscernListView
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/15 10:46
 */
public class AiPosListView extends AiPosLayout {

    private final int STATUS_IDLE = 1;
    private final int STATUS_RECOGNIZING = 2;
    private final int STATUS_NO_RESULT = 3;
    private final int STATUS_SHOW = 4;
    private final int STATUS_PHOTO = 5;

    public static final String ROOT_PATH = Environment.getExternalStoragePublicDirectory("skuimg_d").getPath() + "/";
    public static final String LOGO_PATH = Environment.getExternalStoragePublicDirectory("logoImg").getPath() + "//";

    private RecyclerView rv_aipos_list;
    private FloatingActionButton actionButton;
    private ConstraintLayout parentView;
    private TextView txt_status;
    private TextView tv_version;
    private TextView tv_describe;
    private ImageView img_background;
    private ImageView logo_image;
    //    private Animation anim_out, anim_in;
    CommonViewItemAdapter<GoodsModel> adapter;
    private int status;

    public AiPosListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        status = STATUS_IDLE;
        rv_aipos_list = view.findViewById(R.id.rv_aipos_list);
        actionButton = view.findViewById(R.id.fab);
        txt_status = view.findViewById(R.id.txt_status);
        tv_version = view.findViewById(R.id.tv_version);
        parentView = view.findViewById(R.id.list_parent);
        txt_status.setVisibility(View.INVISIBLE);
        img_background = view.findViewById(R.id.img_background);
        tv_describe = view.findViewById(R.id.txt_describe);
        if (isLandscape) {
            logo_image = view.findViewById(R.id.logo_image);
            File file = new File(LOGO_PATH + "logo.jpg");
            if (file.exists()) {
                logo_image.setImageBitmap(getLoacalBitmap(LOGO_PATH + "logo.jpg"));
            }
        }
        // 根据横竖屏加载不同UI
        adapter = new CommonViewItemAdapter<GoodsModel>(isLandscape ? R.layout.item_goods : R.layout.item_goods_port) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, GoodsModel item, @NotNull List<?> payloads) {
                if (payloads.isEmpty()) {
                    super.convert(holder, item, payloads);
                    return;
                }
                for (Object payload : payloads) {
                    switch (String.valueOf(payload)) {
                        // 刷新总价
                        case "TOTAL":
                            if (item.getUnitId() != 1) {
                                holder.setText(R.id.tv_goods_total, item.getTotal());
                            }
                            break;
                        default:
                            break;
                    }
                }

            }

            // 非多点使用这个逻辑
            @Override
            protected void convert(BaseViewHolder helper, GoodsModel item) {
                String icon = ROOT_PATH + item.getGoodsId() + ".jpg";
                String icon1 = ROOT_PATH + item.getItemNo() + ".jpg";
                if (new File(icon1).exists()) {
                    icon = icon1;
                }
                if ("".equals(item.getPreviewFlag())
                        || "0".equals(item.getPreviewFlag())
                        || item.getPreviewFlag() == null
                        || !new File(icon).exists()) {
                    helper.getView(R.id.item_big_name).setVisibility(VISIBLE);
                    helper.getView(R.id.iv_goods).setVisibility(INVISIBLE);
                    helper.setText(R.id.item_big_name, item.getGoodsName());
                } else {
                    helper.getView(R.id.item_big_name).setVisibility(INVISIBLE);
                    helper.getView(R.id.iv_goods).setVisibility(VISIBLE);
                    Glide.with(mContext)
                            .asBitmap()
                            .format(DecodeFormat.PREFER_RGB_565)
//                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(false)
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .dontAnimate()
                            .load(icon)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    helper.setImageBitmap(R.id.iv_goods, resource);
                                }
                            });

                }
                helper.setText(R.id.tv_scales_code, item.getScalesCode());
                helper.setText(R.id.tv_goods_name, item.getGoodsName());
                helper.setText(R.id.tv_goods_total, item.getTotal());
                if (item.isKg()) {
                    helper.setText(R.id.tv_goods_price, item.getPrice());
                    helper.setText(R.id.tv_goods_unit, "/kg");
                } else {
                    helper.setText(R.id.tv_goods_price, CommUtils.Float2String(Float.parseFloat(item.getPrice()) / 2, 2));
                    helper.setText(R.id.tv_goods_unit, "/500g");
                }
                if (item.getUnitId() == 1) {
                    helper.setText(R.id.tv_goods_price, item.getPrice());
                    helper.setText(R.id.tv_goods_unit, "/件");
                    helper.setText(R.id.tv_goods_total, "--.--");
                }
                if (item.getIsFirst()) {
                    helper.setBackgroundResource(R.id.iv_goods_layout_main, R.drawable.edge);
                } else {
                    helper.setBackgroundResource(R.id.iv_goods_layout_main, 0);
                }
            }
        };
        // 多点使用以下代码
//            @Override
//            protected void convert(BaseViewHolder helper, GoodsModel item) {
//
//                if ("".equals(item.getGoodsImg()) || item.getGoodsImg() == null || "0".equals(item.getPreviewFlag())) {
//                    helper.getView(R.id.item_big_name).setVisibility(VISIBLE);
//                    helper.getView(R.id.iv_goods).setVisibility(INVISIBLE);
//                    helper.setText(R.id.item_big_name, item.getGoodsName());
//                } else {
//
//                    Glide.with(mContext)
//                            .asBitmap()
//                            .format(DecodeFormat.PREFER_RGB_565)
//                            .skipMemoryCache(false)
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                            .dontAnimate()
//                            .load("https://img.dmallcdn.com/"+item.getGoodsImg())
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                    helper.setImageBitmap(R.id.iv_goods, resource);
//                                }
//                            });
//
//                }
//                helper.setText(R.id.tv_scales_code, item.getScalesCode());
//                helper.setText(R.id.tv_goods_name, item.getGoodsName());
//                helper.setText(R.id.tv_goods_total, item.getTotal());
//                if (item.isKg()) {
//                    helper.setText(R.id.tv_goods_price, item.getPrice());
//                    helper.setText(R.id.tv_goods_unit, "/kg");
//                } else {
//                    helper.setText(R.id.tv_goods_price, CommUtils.Float2String(Float.parseFloat(item.getPrice()) / 2, 2));
//                    helper.setText(R.id.tv_goods_unit, "/500g");
//                }
//                if (item.getUnitId() == 1) {
//                    helper.setText(R.id.tv_goods_price, item.getPrice());
//                    helper.setText(R.id.tv_goods_unit, "/件");
//                    helper.setText(R.id.tv_goods_total, "--.--");
//                }
//                if (item.getIsFirst()) {
//                    helper.setBackgroundResource(R.id.iv_goods_layout_main, R.drawable.edge);
//                } else {
//                    helper.setBackgroundResource(R.id.iv_goods_layout_main, 0);
//                }
//            }
//        };

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, isLandscape ? 5 : 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_aipos_list.setLayoutManager(layoutManager);
        rv_aipos_list.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view1, position) -> {
            if (clickListener != null) {
                clickListener.onItemClick((GoodsModel) adapter.getData().get(position), view1, position);
            }
        });
        adapter.setOnItemLongClickListener((adapter, view1, position) -> {
            if (clickListener != null) {
                clickListener.onLongClick((GoodsModel) adapter.getData().get(position), view1, position);
            }
            return false;
        });
        actionButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onFabclick();
            }
        });

    }

    public void reSetListViewColumn(int column) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, column) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_aipos_list.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
    }

    public void refreshListViewTotal() {
        adapter.notifyItemRangeChanged(0, getData().size(), "TOTAL");
    }

    public ConstraintLayout getParentView() {
        return parentView;
    }

    public RecyclerView getRv_aipos_list() {
        return rv_aipos_list;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_list;
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_list_port;
    }

    public void setVersionOnClick(View.OnClickListener listener) {
        tv_version.setOnClickListener(listener);
    }

    public void setDescribeText(String text) {
        tv_describe.setText(text);
    }

    /**
     * 空闲状态
     */
    public void idle() {
        if (status != STATUS_IDLE) {
            status = STATUS_IDLE;
            img_background.setVisibility(View.VISIBLE);
            setBitmap(img_background, R.mipmap.background1);
//            txt_status.setVisibility(View.INVISIBLE);
//            actionButton.setVisibility(View.GONE);
        }
    }

    /**
     * 识别中
     */
    public void recognizing() {
        clear();
       /* if(status!=STATUS_RECOGNIZING) {
            status = STATUS_RECOGNIZING;
            txt_status.setVisibility(View.INVISIBLE);
            actionButton.setVisibility(View.GONE);
        }*/
    }

    /**
     * 展示中
     */
    public void showing() {
        if (status != STATUS_SHOW) {
            status = STATUS_SHOW;
            img_background.setVisibility(View.INVISIBLE);
            txt_status.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
        }
    }

    /**
     * 照片采集
     */
    public void takingPhoto() {
        if (status != STATUS_PHOTO) {
            status = STATUS_PHOTO;
            img_background.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.VISIBLE);
        }
    }

    public void noResult() {
        if (status != STATUS_NO_RESULT) {
            status = STATUS_NO_RESULT;
            img_background.setVisibility(View.VISIBLE);
            setBitmap(img_background, R.mipmap.bg_no_result);
            actionButton.setVisibility(View.GONE);
        }
    }

    public void setVersion(String version) {
        tv_version.setText(version);
    }

    private View emptyDataView;
    private View loadingView;
    private View errorView;

    /**
     * 设置无数据View
     */
    public void initEmptyDataView(@LayoutRes int view) {
        emptyDataView = LayoutInflater.from(mContext).inflate(view, rv_aipos_list, false);
    }

    public void initEmptyDataView() {
        emptyDataView = LayoutInflater.from(mContext).inflate(R.layout.dialog_key, rv_aipos_list, false);
    }

    /**
     * 设置错误View
     */
    public void initErrorView(@LayoutRes int view) {
        errorView = LayoutInflater.from(mContext).inflate(view, rv_aipos_list, false);
    }

    public void initErrorView() {
        errorView = LayoutInflater.from(mContext).inflate(R.layout.dialog_key, rv_aipos_list, false);
    }

    /**
     * 设置加载View
     */
    public void initLoadingView(@LayoutRes int view) {
        loadingView = LayoutInflater.from(mContext).inflate(view, rv_aipos_list, false);
    }

    public void initLoadingView() {
        loadingView = LayoutInflater.from(mContext).inflate(R.layout.dialog_key, rv_aipos_list, false);
    }

    public void setText(String text) {
        txt_status.setText(text);
    }

    /**
     * 添加数据
     */
    public void addData(GoodsModel model) {
        adapter.addData(model);

    }

    public List<GoodsModel> getData() {
        return adapter.getData();
    }


    public void addData(int position, GoodsModel model) {
        adapter.addData(position, model);
    }

    public void setData(List<GoodsModel> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        adapter.setList(data);
    }

    /**
     * 清空数据
     */
    public void clear() {
        adapter.setList(new ArrayList<>());
    }

    protected void setBitmap(ImageView imageView, int resId) {

        imageView.setImageDrawable(getResources().getDrawable(resId));

    }

    /**
     * 点击事件
     */
    public void setClickListener(ClickListener listener) {
        clickListener = listener;
    }

    private ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(GoodsModel model, View view, int position);

        void onFabclick();

        void onLongClick(GoodsModel model, View view, int position);
    }

    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
