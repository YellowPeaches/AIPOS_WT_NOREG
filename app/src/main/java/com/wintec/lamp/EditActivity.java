package com.wintec.lamp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.wintec.lamp.base.BaseActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.data.EditType;
import com.wintec.lamp.entity.EditEntity;
import com.wintec.lamp.service.WintecServiceSingleton;
import com.wintec.lamp.utils.CommUtils;

import butterknife.BindView;
import cn.wintec.aidl.LabelPrinterService;
import cn.wintec.aidl.WintecManagerService;

public class EditActivity extends BaseActivity {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.edit_value)
    EditText edit_value;

    private EditEntity entity;
    private WintecManagerService wintecManagerService;  // wintec服务
    private LabelPrinterService labelPrinterService;    // 标签打印机服务

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        Intent intent = getIntent();
        entity = (EditEntity) intent.getSerializableExtra("value");
    }

    @Override
    public void onStart() {
        super.onStart();
        WintecServiceSingleton.getInstance().bind();
    }

    @Override
    public void initView() {
        mTopBar.setTitle(entity.getTitle());
        edit_value.setText(entity.getDetailText());
        edit_value.setSelection(edit_value.length());//将光标移至文字末尾
        mTopBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Button btn = mTopBar.addRightTextButton("保存", 0);
        btn.setBackground(getDrawable(R.drawable.bg_topbar_btn));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存
                String value = edit_value.getText().toString().trim();
                if (TextUtils.isEmpty(value) && entity.getType() != EditType.Edit_TYPE_COORDINATE) {
                    MyToast(entity.getTitle() + "不能为空");
                    return;
                }
                if (entity.getType() == EditType.EDIT_TYPE_IP) {
                    if (!ipCheck(value)) {
                        MyToast("IP地址不合法");
                        return;
                    }
                }
                if (entity.getType() == EditType.Edit_TYPE_NUMBER) {
                    if (!CommUtils.isNumeric(value)) {
                        MyToast("输入不合法");
                        return;
                    }
                }
                if (entity.getType() == EditType.Edit_TYPE_INTEGER) {
                    if (!CommUtils.isNumeric2(value)) {
                        MyToast("输入不合法");
                        return;
                    }
                }
                if (entity.getType() == EditType.Edit_TYPE_COORDINATE) {
                    if (!CommUtils.isCoordonate(value)) {
                        MyToast("输入不合法");
                        return;
                    }
                }
                //修改宁致打印机速度.浓度
                if ("PRINTERER_SPEED".equals(entity.getKey()) || "PRINTER_CONCENTRATION".equals(entity.getKey())) {
                    if ("宁致打印机".equals(Const.getSettingValue(Const.PRINT_SETTING))) {
                        int speed = Integer.parseInt(value);
                        boolean status = false;
                        if ("PRINTERER_SPEED".equals(entity.getKey())) {
                            if (speed < 0 || speed > 15) {
                                MyToast("打印速度在[0-15]设置");
                                return;
                            }
                            status = WintecServiceSingleton.getInstance().commandSetting(new byte[]{0x1F, 0x60, 0x01, (byte) speed});
                        } else {
                            if (speed < 0 || speed > 7) {
                                MyToast("打印浓度在[0-7]设置");
                                return;
                            }
                            status = WintecServiceSingleton.getInstance().commandSetting(new byte[]{0x1F, 0x70, 0x01, (byte) speed});
                        }
                        if (status) {
                            Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        MyToast("当前品牌打印机不支持修改");
                        return;
                    }

                }
                entity.setDetailText(value);
                Intent intent = new Intent();
                intent.putExtra("value", entity);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public int getView() {
        return R.layout.activity_edit;
    }

    /**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法
     */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }
}