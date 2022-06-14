package com.wintec.aiposui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.wintec.aiposui.R;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.utils.CommUtils;
import com.wintec.aiposui.view.control.CommonCustomTextView;
import com.wintec.aiposui.view.keyboard.KeyBoardEditText;


/**
 * @描述：
 * @文件名: AiDiscern.AiDiscernTitleView
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/15 10:45
 */
public class AiPosTitleView extends AiPosLayout {
    //品名
    private TextView tv_goods_name,tv_goods_type;
    //单价
    private TextView tv_price,tv_price_txt;
    //单位
    private TextView tv_unit,tv_unit_txt;
    //总计
    private TextView tv_total,tv_total_txt;
    //称重
    private CommonCustomTextView tv_weight;
    //皮重
    private TextView tv_tare;
    //键盘输入框
    private KeyBoardEditText keyBoardEditText;
    //提示语句
    private TextView tv_hint;
    //净重文字标题
    private TextView tv_txt_net;
    //重量状态
    private TextView tv_scales_status;
    //皮重状态
    private TextView tv_tare_status;

    private ConstraintLayout layout_weight;

    public AiPosTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        if (isLandscape){
            tv_txt_net = view.findViewById(R.id.tv_txt_net);
            tv_goods_name=view.findViewById(R.id.tv_goods_name);
            tv_goods_type=view.findViewById(R.id.tv_goods_type);
            //单价
            tv_price=view.findViewById(R.id.tv_price);
            tv_price_txt=view.findViewById(R.id.tv_price_txt);
            //单位
            tv_unit=view.findViewById(R.id.tv_unit);
            tv_unit_txt=view.findViewById(R.id.tv_unit_txt);
            //总计
            tv_total=view.findViewById(R.id.tv_total);
            tv_total_txt=view.findViewById(R.id.tv_total_txt);
            tv_hint = view.findViewById(R.id.tv_hint_text);
            tv_scales_status = view.findViewById(R.id.tv_scales_status);
            tv_tare_status = view.findViewById(R.id.tv_tare_status);
            layout_weight = view.findViewById(R.id.layout_weight);
        }
        else{
            keyBoardEditText = view.findViewById(R.id.edit_scalesCode);
            tv_hint = view.findViewById(R.id.hint_text);
        }
        //皮重
        tv_tare=view.findViewById(R.id.tv_tare);
        //称重
        tv_weight=view.findViewById(R.id.tv_weight);

    }

    public KeyBoardEditText getKeyBoardEditText() {
        return keyBoardEditText;
    }

    public void clearInput(){
        keyBoardEditText.setText("");
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_title;
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_title_port;
    }

    public void setScaleOverload(String weight){
        if (mIsLandscape){
            tv_scales_status.setText("过载");
            layout_weight.setBackground(this.getResources().getDrawable(R.drawable.bg_title_item_red));
            setWeight("---");
        }
    }

    public void setScaleLoseload(String weight){
        if (mIsLandscape){
            tv_scales_status.setText("欠载");
            layout_weight.setBackground(this.getResources().getDrawable(R.drawable.bg_title_item_red));
            setWeight(weight);
        }
    }

    public void setScalesStatusZero(){
        tv_scales_status.setText("零点 稳定");
    }

    public void clearScalesStatus(){
        tv_scales_status.setText("");
    }

    public void setScalesStatusStable(){
        tv_scales_status.setText("稳定");
    }

    public void setGoods(GoodsModel model, String net, int num, String total ,boolean isKg){
        if(!mIsLandscape){
            return;
        }
        if(model==null){

            tv_goods_name.setText("— — — —");
            //tv_goods_type.setText("暂无");
            tv_price.setText("0.00");
            if(isKg){
                tv_price_txt.setText("单价(元/kg)");
            }else {
                tv_price_txt.setText("单价(元/500g)");
            }
            tv_unit.setText("0.000");
            tv_unit_txt.setText("重量(kg)");
            tv_total.setText("0.00");
        }else{
            tv_goods_name.setText(model.getGoodsName());
            tv_goods_type.setText(model.getGoodsType());
            if(isKg){
                tv_price.setText(model.getPrice());
            }else {
                tv_price.setText(CommUtils.Float2String(Float.parseFloat(model.getPrice())/2,2));
            }
            if(model.getUnitId()==0){
                if(isKg){
                    tv_price_txt.setText("单价(元/kg)");
                }else {
                    tv_price_txt.setText("单价(元/500g)");
                }
                tv_unit_txt.setText("重量(kg)");
                tv_unit.setText(net);
            }
            else{
                tv_price_txt.setText("单价(元/件)");
                tv_unit_txt.setText("件");
                tv_unit.setText(num+"");
            }

            tv_total.setText(total);
        }

    }

    public void setWeight(String weight){
        tv_weight.setText(weight);
    }

    public void setWeight(String weight, int type){
        layout_weight.setBackground(this.getResources().getDrawable(R.drawable.bg_title_item_2));
        if (mIsLandscape){
            tv_weight.setText(weight);
            if (!tv_txt_net.getText().toString().equals("净重(kg)")){
                tv_txt_net.setText("净重(kg)");
                tv_txt_net.setTextColor(Color.WHITE);
            }

        }
        else{
            tv_weight.setText(weight);
        }
        switch (type){
            // 变化中
            case 0:
                tv_weight.setTextColor(Color.YELLOW);
                break;
                // 1 稳重 2清零
            case 1:
            case 2:
                tv_weight.setTextColor(Color.WHITE);
                break;
        }

    }

    public TextView getWeightView(){
        return tv_weight;
    }

    public TextView getTv_total() {
        return tv_total;
    }

    public String getWeight(){
        String weight = "0.000";
        try{
            String tmp = tv_weight.getText().toString();
            weight = tmp;
//            if (mIsLandscape){
//                weight = tmp.substring(0,tmp.indexOf("kg"));
//            }
//            else{
//                weight = tmp;
//            }

        }catch (Exception e){
            weight = "0.000";
        }
        return weight;
    }

    public void setTare(String tare, boolean isPreSetTare){
        if (isPreSetTare){
            tv_tare_status.setText("预置");
        }else{
            tv_tare_status.setText("");
        }

        tv_tare.setText(tare);
    }

    public String getTare(){
        String tare = tv_tare.getText().toString();
//        String tare = text.replace("kg", "");
        try {
            Float i = Float.parseFloat(tare) * 1000;
            return i.intValue()+"";
        }catch (Exception e)
        {
            return  "0";
        }

    }
    public String getTareBySecend(){

//        String tare = text.replace("kg", "");
        try {
           return tv_tare.getText().toString();
        }catch (Exception e)
        {
            return  "0";
        }

    }
    public void setHint(String hint){
        if (tv_hint!=null && hint!=null){
            tv_hint.setText(hint);
        }
    }

    public void clearHint(){
        if (tv_hint!=null){
            tv_hint.setText("");
        }
    }
    public void setTvPriceTxt(boolean isKg)
    {
        if(!isKg)
        {
            tv_price_txt.setText("单价(元/500g)");
        }
    }
}
