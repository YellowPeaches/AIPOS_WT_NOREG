package com.wintec.aiposui.view.label;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.wintec.aiposui.R;
import com.wintec.aiposui.view.AiPosLayout;

public class TextComponentAttributeView extends AiPosLayout {
    public TextComponentAttributeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {

    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_text_attribute;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_text_attribute;
    }
}
