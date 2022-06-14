package com.wintec.aiposui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.wintec.aiposui.R;

import butterknife.ButterKnife;

/**
 * @描述：
 * @文件名: AiPos.AiPosLayout
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/15 14:19
 */
public abstract class AiPosLayout  extends ConstraintLayout {
    protected Context mContext;
    public boolean mIsLandscape = false;
    public AiPosLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        // 获取屏幕状态
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        View view = null;
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori==mConfiguration.ORIENTATION_LANDSCAPE){
            mIsLandscape = true;
            view = LayoutInflater.from(context).inflate(getLandscapeLayout(), this);
            init(view, true);
        }else{
            mIsLandscape = false;
            view = LayoutInflater.from(context).inflate(getPortraitLayout(), this);
            init(view, false);
        }

    }
    protected abstract void init(View view, boolean isLandscape);
    protected abstract int getPortraitLayout();
    protected abstract int getLandscapeLayout();

}
