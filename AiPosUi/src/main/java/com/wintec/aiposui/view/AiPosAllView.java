package com.wintec.aiposui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wintec.aiposui.R;
import com.wintec.aiposui.view.control.FloatButton;
import com.wintec.aiposui.view.keyboard.KeybroadView;


/**
 * @描述：
 * @文件名: AiDiscern.AiDiscernAllView
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/15 9:32
 * AiDiscernTitleView
 * AiDiscernListView
 * AiDiscernOperatingView
 *
 */
public class AiPosAllView extends AiPosLayout {
    private AiPosTitleView titleView;
    private AiPosOperatingView operatingView;
    private AiPosListView listView;
    private AiposQuickSelectView quickSelectView;
    private KeybroadView keybroadView;
    private FloatButton floatButton;
    private AiPosAccountList aiPosAccountList;



    public AiPosAllView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        // 根据不同的屏幕状态加载VIEW
        titleView=view.findViewById(R.id.ap_title);
        listView = view.findViewById(R.id.ap_list);
        keybroadView = view.findViewById(R.id.ap_keyboard);
        operatingView = view.findViewById(R.id.ap_opv);
        aiPosAccountList = view.findViewById(R.id.ap_account_list);
        // 横屏
        if(isLandscape){
            quickSelectView = view.findViewById(R.id.ap_quick_list);
            operatingView.getKeyBoardEditText().setKeyboardType(keybroadView, keybroadView.getKb(), false);
        }
        // 竖屏
        else{
            floatButton = view.findViewById(R.id.ap_floatbutton);
            titleView.getKeyBoardEditText().setKeyboardType(keybroadView, keybroadView.getKb(), false);
        }
    }

    /**
     * 获取View
     * @return
     */
    public AiPosTitleView getTitleView(){
        return titleView;
    }
    public AiPosOperatingView getOperatingView(){
        return operatingView;
    }
    public AiPosListView getListView(){
        return listView;
    }
    public AiposQuickSelectView getQuickSelectView() { return quickSelectView; }
    public KeybroadView getKeybroadView() { return keybroadView; }
    public FloatButton getFloatButton() { return floatButton; }
    public AiPosAccountList getAiPosAccountList() {
        return aiPosAccountList;
    }
    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_all;
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_all_port;
    }

}
