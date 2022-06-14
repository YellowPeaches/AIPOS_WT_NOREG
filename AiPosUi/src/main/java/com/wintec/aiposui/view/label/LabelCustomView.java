package com.wintec.aiposui.view.label;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.wintec.aiposui.R;
import com.wintec.aiposui.view.AiPosLayout;

import androidx.recyclerview.widget.RecyclerView;

public class LabelCustomView extends AiPosLayout {

    private EditText edtTemplateName;   // 模板名称
    private EditText edtTemplateWidth;  // 模板宽度
    private EditText edtTemplateHeight; // 模板高度
    private RecyclerView rlControls;    // 控件列表
    private FrameLayout flTemplate;     // 价签模板
    private LinearLayout llAttribute;   // 属性布局

    private BarcodeComponentAttributeView barcodeView;
    private CustomTextComponentAttributeView customTextView;
    private TextComponentAttributeView textView;


    public LabelCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {

    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_label_custom;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_label_custom;
    }
}
