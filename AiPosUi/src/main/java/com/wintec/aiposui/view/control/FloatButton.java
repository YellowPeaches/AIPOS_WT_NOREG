package com.wintec.aiposui.view.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView2;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.wintec.aiposui.R;
import com.wintec.aiposui.view.AiPosLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloatButton extends AiPosLayout {
    private QMUIRadiusImageView2 button;
    private QMUIPopup mGlobalAction;
    private Animation animRotate;       // 旋转动画
    private String[] listItems;
    private AdapterView.OnItemClickListener listener;

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QMUIRadiusImageView2 getButton() {
        return button;
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        button = view.findViewById(R.id.fltt_menu);
        //初始化动画
        animRotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_90);
    }

    public void initMenuAndClickEvent(String[] titles, AdapterView.OnItemClickListener listener){
        this.listItems = titles;
        this.listener = listener;
        initMenu();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.startAnimation(animRotate);
                show(view);
            }
        });
    }

    protected void initMenu() {
        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(mContext, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = listener;
        mGlobalAction = QMUIPopups.listPopup(mContext,
                QMUIDisplayHelper.dp2px(mContext, 250),
                QMUIDisplayHelper.dp2px(mContext, 550), adapter, onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .edgeProtection(QMUIDisplayHelper.dp2px(mContext, 30))
                .offsetYIfTop(QMUIDisplayHelper.dp2px(mContext, 30))
                .skinManager(QMUISkinManager.defaultInstance(mContext));
    }

    protected void show(View v){
        mGlobalAction.show(v);
    }

    protected void dismiss(){
        mGlobalAction.dismiss();
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_float_button;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_float_button;
    }
}
