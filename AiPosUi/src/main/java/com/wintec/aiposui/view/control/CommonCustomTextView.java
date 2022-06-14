package com.wintec.aiposui.view.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * @描述：
 * @文件名: AiPos.CommonCustomTextView
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/10/10 10:42
 */
@SuppressLint("AppCompatCustomView")
public class CommonCustomTextView extends TextView
{
    public CommonCustomTextView(Context context)
    {
        super(context);
        init(context);
    }

    public CommonCustomTextView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public CommonCustomTextView(Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CommonCustomTextView(Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * 定制字体
     */
    private void init(Context context)
    {
        // 获取资源文件
        AssetManager assets = context.getAssets();
        Typeface font =
//                Typeface.createFromAsset(assets, "fonts/digital-7.ttf");
//                Typeface.createFromAsset(assets, "fonts/msyh.ttf");
                Typeface.createFromAsset(assets, "fonts/Helvetica Bold.ttf");
        setTypeface(font);
    }
}
