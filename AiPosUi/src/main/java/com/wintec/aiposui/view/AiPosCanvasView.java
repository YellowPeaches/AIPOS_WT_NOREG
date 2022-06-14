package com.wintec.aiposui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wintec.aiposui.R;
import com.wintec.aiposui.model.CanvasLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 赵冲
 * @description:
 * @date :2021/6/30 8:53
 */
public class AiPosCanvasView extends AiPosLayout implements View.OnTouchListener {

    private int sx;  //手触碰屏幕的坐标 x
    private int sy;  //手触碰屏幕的坐标 y
    Map<Integer, TextView> viewMap;
    RelativeLayout relativeLayout;

    public AiPosCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        relativeLayout = view.findViewById(R.id.canvas_id);
        viewMap = new HashMap<>();

    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_canvas;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_canvas;
    }

    //获取坐标
    public CanvasLocation getLocation() {
        CanvasLocation canvasLocation = new CanvasLocation(relativeLayout.getLeft(), relativeLayout.getTop(), relativeLayout.getRight(), relativeLayout.getBottom());
        return canvasLocation;
    }


    public void addTextView(TextView textView) {
        viewMap.put(textView.getId(), textView);
        relativeLayout.addView(textView);
        textView.setOnTouchListener(this);
    }

    public TextView getTextView(Integer id) {
        TextView textView = viewMap.get(id);
        return textView;
    }

    public int delTextView(Integer id) {
        if (viewMap.containsKey(id)) {
            viewMap.remove(id);
            return 1;
        } else {
            return 0;
        }
    }

    //移动事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!(v instanceof TextView)) {
            return false;
        }
        TextView textView = viewMap.get(v.getId());
        if(textView == null)
        {
            return false;
        }
        // event.getRawX(); //获取手指第一次接触屏幕在x方向的坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                sx = (int) event.getRawX();
                sy = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                moveTextView(textView,event);
                break;
            case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                // 记录最后图片在窗体的位置
                break;


        }
        return true;// 不会中断触摸事件的返回
    }
    //移动组件
    private void moveTextView(TextView textView, MotionEvent event) {

        CanvasLocation location = getLocation();

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        // 获取手指移动的距离
        int dx = x - sx;
        int dy = y - sy;
        // 得到imageView最开始的各顶点的坐标
        int l = textView.getLeft();
        int r = textView.getRight();
        int t = textView.getTop();
        int b = textView.getBottom();
        // 更改imageView在窗体的位置
        int left = l + dx;
        int top = t + dy;
        int right = r + dx;
        int bottom = b + dy;

        if (left <= location.getxOffset() || top <= location.getyOffset() || right >= location.getWidht()+location.getxOffset() || bottom >= location.getHeigth()+location.getyOffset()) {
            return;
        }
        textView.layout(l + dx, t + dy, r + dx, b + dy);
        // 获取移动后的位置
        sx = (int) event.getRawX();
        sy = (int) event.getRawY();
    }
}
