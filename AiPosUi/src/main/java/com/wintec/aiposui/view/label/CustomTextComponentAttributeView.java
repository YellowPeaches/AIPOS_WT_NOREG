package com.wintec.aiposui.view.label;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.wintec.aiposui.R;
import com.wintec.aiposui.view.AiPosLayout;

public class CustomTextComponentAttributeView extends AiPosLayout {
    private EditText edtX, edtY;

    public CustomTextComponentAttributeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {

    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_custom_text_attribute;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_custom_text_attribute;
    }
}
