package com.wintec.aiposui.view.control;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wintec.aiposui.R;
import com.wintec.aiposui.adapter.CommonViewItemAdapter;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.view.AiPosLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @描述：
 * @文件名: AiPos.NUIKeyView
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/22 15:34
 */
public class NUIKeyView extends AiPosLayout {
    /**
     * 数字键盘，带 .
     */
    public final static int KEY_TYPE_NUMBER = 0;

    /**
     * 数字键盘，带 . 临时改价
     */
    public final static int KEY_TYPE_NUMBER_DISCOUNT = 3;

    /**
     * 编码键盘，没有小数
     */
    public final static int KEY_TYPE_CODE = 1;
    /**
     * 密码键盘
     */
    public final static int KEY_TYPE_PASSWORD = 2;

    @IntDef({KEY_TYPE_NUMBER, KEY_TYPE_CODE, KEY_TYPE_PASSWORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NUIKeyType {
    }

    /**
     * 键盘类型
     */
    @NUIKeyType
    private int mKeyType;

    private boolean isFirstInput = true;    // 是否是第一次输入,取代默认值1

    CommonViewItemAdapter<String> keyAdapter;
    private RecyclerView rv_keys;

    private TextView edit_keyValue;
    private TextView tv_rightBtn1, tv_rightBtn2, tv_rightBtn3, tv_rightBtn4,tv_keyName,tv_keyPluNo,tv_keyPrice;
    private LinearLayout tv_keyPlu;
    private TextView tv_keyTitle;

    public NUIKeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        edit_keyValue = view.findViewById(R.id.edit_keyValue);
        tv_rightBtn1 = view.findViewById(R.id.tv_rightBtn1);
        tv_rightBtn2 = view.findViewById(R.id.tv_rightBtn2);
        tv_rightBtn3 = view.findViewById(R.id.tv_rightBtn3);
        tv_keyTitle = view.findViewById(R.id.tv_keyTitle);
        tv_keyName = view.findViewById(R.id.tv_keyName);
        tv_keyPluNo = view.findViewById(R.id.tv_keyPluNo);
        tv_keyPrice = view.findViewById(R.id.tv_keyPrice);
        tv_keyPlu = view.findViewById(R.id.tv_keyPlu);

        rv_keys = view.findViewById(R.id.rv_keys);
        rv_keys.setLayoutManager(new GridLayoutManager(mContext, 3));
        keyAdapter = new CommonViewItemAdapter<String>(R.layout.item_key) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_keyBtn, item);
            }
        };
        rv_keys.setAdapter(keyAdapter);
        keyAdapter.setOnItemClickListener((adapter, view1, position) -> {
            switch (position) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 10:
                    addChar(adapter.getItem(position).toString());
                    break;
                case 9:
                    deleteChar();
                    break;
                case 11:
                    if (mKeyType == KEY_TYPE_NUMBER || mKeyType == KEY_TYPE_NUMBER_DISCOUNT) {
                        addChar(adapter.getItem(position).toString());
                    } else {
                        cleanChar();
                    }
                    break;
                default:
            }
        });
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.dialog_key;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.dialog_key;
    }

    /**
     * 抬起
     * btn_operate_pressed
     * bg_key_select_light
     * bg_blue_select_light
     * bg_blue_select_light
     * bg_blue_select_light
     * bg_blue_select_light
     * <p>
     * 选项   white   grey
     */

    public void setKeyModel(String title, @NUIKeyType int type) {
        tv_keyTitle.setText(title);
        mKeyType = type;
        if (mKeyType == KEY_TYPE_NUMBER) {
            tv_rightBtn3.setVisibility(VISIBLE);
            tv_rightBtn3.setTextSize(36);
            tv_rightBtn3.setText("清空");
            keyAdapter.setList(Arrays.asList("7", "8", "9", "4", "5", "6", "1", "2", "3", "退格", "0", "."));
        } else if (mKeyType == KEY_TYPE_NUMBER_DISCOUNT) {
            tv_rightBtn3.setVisibility(VISIBLE);
            tv_rightBtn3.setTextSize(30);
            tv_rightBtn3.setText("按总价打印");
            keyAdapter.setList(Arrays.asList("7", "8", "9", "4", "5", "6", "1", "2", "3", "退格", "0", "."));
        } else {
            tv_rightBtn3.setVisibility(GONE);
            keyAdapter.setList(Arrays.asList("7", "8", "9", "4", "5", "6", "1", "2", "3", "退格", "0", "清空"));
        }

    }

    public void goodModelDismiss()
    {
        tv_keyPlu.setVisibility(View.VISIBLE);
    }

    public void setGoodModel(GoodsModel goodsModel)
    {
        tv_keyPlu.setVisibility(View.INVISIBLE);
        tv_keyName.setText(goodsModel.getGoodsName());
        tv_keyPrice.setText(goodsModel.getPrice());
        tv_keyPluNo.setText(goodsModel.getGoodsId());
    }
    public void clearInput() {
        cleanChar();
    }

    public void setInput(String number) {

        edit_keyValue.setText(number);
        realValue = number;
    }

    public interface ConfirmListner {
        void onConfirm(String code, int param);

        void onCancel();
        //取消
    }

    //确认按钮
    public void setConfirmBtnListner(ConfirmListner listner) {
        tv_rightBtn1.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(realValue)) {
                isFirstInput = true;
                listner.onConfirm(realValue, 0);
            }
        });
        tv_rightBtn2.setOnClickListener(v -> {
            isFirstInput = true;
            listner.onCancel();
        });

        tv_rightBtn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKeyType == KEY_TYPE_NUMBER_DISCOUNT) {
                    listner.onConfirm(realValue,1);
                } else {
                    cleanChar();
                }
            }
        });
    }

    //真实数据
    private String realValue = "";

    //添加字符
    private void addChar(String value) {
        if (isFirstInput){
            cleanChar();
            isFirstInput = false;
        }
        realValue += value;

        if (mKeyType == KEY_TYPE_PASSWORD) {
            edit_keyValue.setText(edit_keyValue.getText().toString() + "*");
//            edit_keyValue.post(() -> {
//                String editValue=edit_keyValue.getText().toString().trim();
//                edit_keyValue.setText(editValue.replace(editValue.substring(editValue.length()-1),"*"));
//            });
        } else {
            edit_keyValue.setText(edit_keyValue.getText().toString() + value);
        }
    }

    //删除字符
    private void deleteChar() {
        if (realValue.length() > 0) {
            realValue = realValue.substring(0, realValue.length() - 1);
            String editValue = edit_keyValue.getText().toString().trim();
            edit_keyValue.setText(editValue.substring(0, editValue.length() - 1));
        }
    }

    //清空字符
    private void cleanChar() {
        edit_keyValue.setText("");
        realValue = "";
    }


}
