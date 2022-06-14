package com.wintec.aiposui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.repackaged.com.google.common.base.Strings;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wintec.aiposui.R;
import com.wintec.aiposui.adapter.CommonViewItemAdapter;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.utils.CommUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/16 9:17
 */
public class AiPosAccountList  extends AiPosLayout{

    private TextView clearButton;
    private Button saleButton;
    private RecyclerView accountList;
    private TextView tvTotal;
    private AccountCallBack accountCallBack;
    CommonViewItemAdapter<GoodsModel> adapter;
    public AiPosAccountList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        clearButton = view.findViewById(R.id.account_clear_btn);
        saleButton  = view.findViewById(R.id.account_sale_btn);
        accountList = view.findViewById(R.id.account_list);
        tvTotal = view.findViewById(R.id.text_total);

        adapter = new CommonViewItemAdapter<GoodsModel>(R.layout.item_sale_item) {
            @Override
            protected void convert(BaseViewHolder helper, GoodsModel item) {
                item.setUid(UUID.randomUUID().toString().replaceAll("-",""));
                helper.setText(R.id.item_goods_name,item.getGoodsName());
                helper.setText(R.id.item_total,  item.getTotal()+"");
//                if(item.isKg())
//                {
//                    helper.setText(R.id.item_price, item.getPrice());
//                    helper.setText(R.id.item_price_title, "单价(元/kg)");
//
//
//                }else {
//                    helper.setText(R.id.item_price, CommUtils.Float2String(Float.parseFloat(item.getPrice())/2,2));
//                    helper.setText(R.id.item_price_title, "单价(元/500g)");
//                }
                if(item.getUnitId() == 1)
                {
                    helper.setText(R.id.item_weight, item.getCount());
                   // helper.setText(R.id.item_weight_title,  "件数");
                }else {
                    helper.setText(R.id.item_weight, item.getNet());
                  //  helper.setText(R.id.item_weight_title, "重量(kg)");
                }
                TextView delBtn = (TextView)helper.getView(R.id.item_img_btn);
                delBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<GoodsModel> list = getData();
                        Iterator<GoodsModel> iterator = list.iterator();
                        while (iterator.hasNext())
                        {
                            GoodsModel next = iterator.next();
                            if(next.getUid().equals(item.getUid()))
                            {
                                iterator.remove();
                            }
                        }
                        addListData(list);
                    }
                });

            }
        };
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(mContext,1);
        accountList.setLayoutManager(layoutManager);
        accountList.setAdapter(adapter);
        clearButton.setOnClickListener(v -> {
            if(accountCallBack != null)
            {
                accountCallBack.clearCallBack();
            }
            clear();
        });
        saleButton.setOnClickListener(v -> {
            if(accountCallBack != null)
            {
                accountCallBack.saleCallBack();
            }
        });
    }

    public void addData(GoodsModel model){
        adapter.addData(model);
        float total = 0;
        for (GoodsModel m: getData()){
            total += Float.valueOf(m.getTotal());
        }
        tvTotal.setText(CommUtils.Float2String(total,2));
        accountList.scrollToPosition(adapter.getItemCount()-1);
    }
    public void addListData(List<GoodsModel> data){
        adapter.setList(data);
        float total = 0;
        for (GoodsModel m: getData()){
            total += Float.valueOf(m.getTotal());
        }
        tvTotal.setText(CommUtils.Float2String(total,2));
    }
    public void visible()
    {
        clear();
        this.setVisibility(View.VISIBLE);
    }
    public void hide()
    {
        clear();
        this.setVisibility(View.GONE);
    }
    public void clear(){
        adapter.setList(new ArrayList<>());
        tvTotal.setText("0.00");
    }
    public List<GoodsModel> getData()
    {
        return adapter.getData();
    }

    public void  setCallBack (AccountCallBack accountCallBack)
    {
        this.accountCallBack = accountCallBack;
    }
    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_account;
    }

    @Override
    protected int getLandscapeLayout() {
        return  R.layout.view_aiposui_account;
    }

    public interface AccountCallBack{
        void clearCallBack();
        void saleCallBack();
    }
}
