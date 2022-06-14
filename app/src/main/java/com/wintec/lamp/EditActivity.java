package com.wintec.lamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.wintec.lamp.base.BaseActivity;
import com.wintec.lamp.data.EditType;
import com.wintec.lamp.entity.EditEntity;
import com.wintec.lamp.utils.CommUtils;

public class EditActivity extends BaseActivity {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.edit_value)
    EditText edit_value;

    private EditEntity entity;

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        Intent intent = getIntent();
        entity = (EditEntity) intent.getSerializableExtra("value");
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