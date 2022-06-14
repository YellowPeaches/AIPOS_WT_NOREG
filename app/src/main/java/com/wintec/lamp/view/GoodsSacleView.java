package com.wintec.lamp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wintec.lamp.R;

import androidx.annotation.Nullable;

/**
 * @author 赵冲
 * @description:
 * @date :2021/2/22 13:44
 */
public class GoodsSacleView extends ViewGroup {

    public GoodsSacleView(Context context) {
        super(context);
    }

    public GoodsSacleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.goods_sacle_view, this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
