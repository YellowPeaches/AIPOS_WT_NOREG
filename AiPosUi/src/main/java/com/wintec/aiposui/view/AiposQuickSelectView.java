package com.wintec.aiposui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wintec.aiposui.R;
import com.wintec.aiposui.adapter.CommonViewItemAdapter;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.utils.CommUtils;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AiposQuickSelectView extends AiPosLayout{

    private RecyclerView recycler_recommend_goods;

    private CommonViewItemAdapter<GoodsModel> recommondAdapter;

    public AiposQuickSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandScape) {
        recycler_recommend_goods = view.findViewById(R.id.recycler_recommend_goods);
        recommondAdapter=new CommonViewItemAdapter<GoodsModel>(R.layout.item_recommond_goods) {
            @Override
            protected void convert(BaseViewHolder helper, GoodsModel item) {
                helper.setText(R.id.item_b_name, item.getGoodsName());
                helper.setText(R.id.tv_recommnd_goods_name, item.getScalesCode());
//                helper.setText(R.id.tv_recommnd_goods_code, item.getScalesCode());
                if(item.isKg())
                {
                    helper.setText(R.id.tv_recommnd_goods_price, item.getPrice());
                    helper.setText(R.id.tv_recommnd_goods_unit, "/kg");
                }else {
                    helper.setText(R.id.tv_recommnd_goods_price, CommUtils.Float2String(Float.parseFloat(item.getPrice())/2,2));
                    helper.setText(R.id.tv_recommnd_goods_unit, "/500g");
                }
                if(item.getUnitId() == 1)
                {
                    helper.setText(R.id.tv_recommnd_goods_unit, "/件");
                }
                Glide.with(mContext).load(item.getGoodsImg()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .apply(new RequestOptions().placeholder(R.drawable.img_def).dontAnimate())
                        .into((ImageView) helper.getView(R.id.iv_recommnd_goods));
            }
        };
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(mContext,5);
        recycler_recommend_goods.setLayoutManager(layoutManager);
        recycler_recommend_goods.setAdapter(recommondAdapter);
        recommondAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (clickListener != null) {
                clickListener.onItemClick((GoodsModel) adapter.getData().get(position), view1, position);
            }
        });
    }

    /**
     * 给推荐列表增加数据
     *
     * @param model
     */
    public void addRecommondData(GoodsModel model) {
        recommondAdapter.addData(model);
        Log.i("test", "add" + model.getGoodsName());
    }

    public void clearRecommondList() {
        recommondAdapter.setList(new ArrayList<>());
    }

    /**
     * 点击事件
     */
    public void setClickListener(ClickListener listener){
        clickListener=listener;
    }


    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_quick_select;
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_quick_select;
    }

    private ClickListener clickListener;
    public interface ClickListener{
        void onItemClick(GoodsModel model, View view, int position);
    }
}
