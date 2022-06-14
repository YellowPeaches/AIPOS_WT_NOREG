package com.wintec.aiposui.view.keyboard;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.View;

import com.wintec.aiposui.R;
import com.wintec.aiposui.view.AiPosLayout;

public class KeybroadView extends AiPosLayout {

    private KeyboardView kb;

    public KeybroadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        kb = view.findViewById(R.id.view_keyboard);
    }

    public KeyboardView getKb() {
        return kb;
    }


    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_keyboard;
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_keyboard_port;
    }
}
