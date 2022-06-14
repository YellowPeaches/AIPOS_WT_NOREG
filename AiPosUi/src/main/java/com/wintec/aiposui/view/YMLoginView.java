package com.wintec.aiposui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wintec.aiposui.R;

import butterknife.BindView;

/**
 * @author 赵冲
 * @description:
 * @date :2021/5/31 16:05
 */
public class YMLoginView extends AiPosLayout {

    /**
     * pos编码
     */
    private EditText posEdit;
    /**
     * mac地址
     */
    private TextView macText;

    /**
     * 组号
     */
    private EditText accountcompany;

    /**
     * 获取mac地址
     */
    private  TextView getMacText;

    /**
     * 注册
     */
    private TextView tvSign;

    /**
     * 门店编码
     */
    private TextView branchId;

    public YMLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        posEdit = view.findViewById(R.id.pos_edit);
        macText = view.findViewById(R.id.mac_text);
        accountcompany = view.findViewById(R.id.account_company);
        getMacText = view.findViewById(R.id.get_mac_text);
        tvSign = view.findViewById(R.id.tv_sign);
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_ym_login_port;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_ym_login;
    }
}
