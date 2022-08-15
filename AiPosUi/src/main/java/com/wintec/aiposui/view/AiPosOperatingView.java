package com.wintec.aiposui.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.Resource;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wintec.aiposui.R;
import com.wintec.aiposui.adapter.CommonViewItemAdapter;
import com.wintec.aiposui.model.OperatingBtn;
import com.wintec.aiposui.view.keyboard.KeyBoardEditText;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


/**
 * @描述：
 * @文件名: AiDiscern.AiDiscernOperatingView
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/15 10:46
 */
public class AiPosOperatingView extends AiPosLayout implements View.OnClickListener {

    private static final int msgKey1 = 1;

    CommonViewItemAdapter<OperatingBtn> operatingAdapter;
    CommonViewItemAdapter<String> keyAdapter;
    private RecyclerView rv_key_btn;
    private RecyclerView rv_operatingBtns;

    private AppCompatEditText edit_scalesCode;
    private KeyBoardEditText keyBoardEditText;
    private TextView btn_print;
    private TextView btn_clear;
    private TextView btn_kb_print;
    private TextView myTime;
    private View myTimeBackGroundView;

    public AiPosOperatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***************************对外接口start********************************/

    /**
     * 添加操作按钮
     */
    public void addOperatingBtn(String title, int icon, OnClickListener listener) {
        operatingAdapter.addData(new OperatingBtn(title, icon, listener));
    }


    public void addOperatingBtn(String title, OnClickListener listener, OnLongClickListener longClickListener) {
        operatingAdapter.addData(new OperatingBtn(title, listener, longClickListener));
    }

    public void getOperatingBtn(String name) {
        List<OperatingBtn> data = operatingAdapter.getData();

    }

    public CommonViewItemAdapter<OperatingBtn> getOperatingAdapter() {
        return operatingAdapter;
    }

    /**
     * 点击打印按钮
     */
    public void setPrintListner(PrintListner listner) {
        btn_kb_print.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = edit_scalesCode.getText().toString().trim();
                if (!TextUtils.isEmpty(s)) {
                    listner.onPrint(s);
                }
            }
        });
    }


    public interface PrintListner {
        void onPrint(String code);
    }


    public interface InputListner {
        void onInput(String content);
    }

    public TextView getBtn_kb_print() {
        return btn_kb_print;
    }

    public void setInputListner(InputListner listner) {
        if (mIsLandscape) {
            keyBoardEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    keyBoardEditText.setSelection(s.toString().length());
                    listner.onInput(s.toString().trim());
                }
            });
        } else {
            edit_scalesCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    edit_scalesCode.setSelection(s.toString().length());
                    listner.onInput(s.toString().trim());
                }
            });
        }

    }

    /**
     * 设置隐藏软键盘
     */
    public void hideKeyboard() {
        try {
            Class<EditText> cls = EditText.class;
            Method method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(edit_scalesCode, false);
        } catch (Exception e) {
        }
    }


    /*******************************end****************************/
    @Override
    protected void init(View view, boolean isLandscape) {
        btn_print = view.findViewById(R.id.btn_print);
        rv_key_btn = view.findViewById(R.id.rv_key_btn);
        btn_clear = view.findViewById(R.id.tv_clear);
        btn_kb_print = view.findViewById(R.id.tv_print);
        rv_operatingBtns = view.findViewById(R.id.rv_operatingBtns);
        if (isLandscape) {
            keyBoardEditText = view.findViewById(R.id.edit_scalesCode);
            myTime = view.findViewById(R.id.mytime);
            myTimeBackGroundView = view.findViewById(R.id.mytimeBackGround);
            new TimeThread().start();
        } else {
            edit_scalesCode = view.findViewById(R.id.edit_scalesCode);
        }
        btn_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLandscape) {
                    keyBoardEditText.setText("");
                } else {
                    edit_scalesCode.setText("");
                }
            }
        });
        btn_kb_print.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        operatingAdapter = new CommonViewItemAdapter<OperatingBtn>(isLandscape ? R.layout.item_operate_btn : R.layout.item_operate_btn_port) {
            @Override
            protected void convert(BaseViewHolder helper, OperatingBtn item) {
//                TextView tv = helper.getView(R.id.tv_keyBtn);
                helper.setText(R.id.tv_keyBtn, item.getTitle());
                helper.getView(R.id.op_btn).setOnClickListener(item.getListener());

                if (item.isSelected()) {
                    helper.getView(R.id.op_btn).setBackground(getResources().getDrawable(R.drawable.btn_operate_selected));
                    helper.setTextColor(R.id.tv_keyBtn, Color.WHITE);
                } else {
                    helper.getView(R.id.op_btn).setBackground(getResources().getDrawable(R.drawable.keyboard_bg));
                    helper.setTextColor(R.id.tv_keyBtn, Color.BLACK);
                }

                if (item.getIcon() != 0) {
                    helper.setImageBitmap(R.id.op_icon, BitmapFactory.decodeResource(getResources(), item.getIcon()));
                }

            }
        };
        keyAdapter = new CommonViewItemAdapter<String>(R.layout.item_plu_btn, Arrays.asList("7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "00", "⬅")) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_keyBtn, item);
            }
        };
        rv_key_btn.setLayoutManager(new GridLayoutManager(mContext, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rv_operatingBtns.setLayoutManager(new GridLayoutManager(mContext, isLandscape ? 4 : 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rv_key_btn.setAdapter(keyAdapter);
        rv_operatingBtns.setAdapter(operatingAdapter);

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
                case 9:
                case 10:
                    if (isLandscape) {
                        String text = keyBoardEditText.getText().toString() + adapter.getItem(position);
                        keyBoardEditText.setText(text);
                        keyBoardEditText.setSelection(text.length());//将光标移至文字末尾
                    } else {
                        String text = edit_scalesCode.getText().toString() + adapter.getItem(position);
                        edit_scalesCode.setText(text);
                        edit_scalesCode.setSelection(text.length());//将光标移至文字末尾
                    }

                    break;
                case 11:
//                    if (isLandscape){
//                        keyBoardEditText.setText("");
//                    }
//                    else{
//                        edit_scalesCode.setText("");
//                    }
                    if (isLandscape) {
                        String value = keyBoardEditText.getText().toString().trim();
                        if (value.length() > 0) {
                            keyBoardEditText.setText(value.substring(0, value.length() - 1));
                        }
                    } else {
                        String value = edit_scalesCode.getText().toString().trim();
                        if (value.length() > 0) {
                            edit_scalesCode.setText(value.substring(0, value.length() - 1));
                        }
                    }
                    break;
                default:
            }
        });
        // 竖屏下隐藏输入框 键盘 打印按钮
        if (!isLandscape) {
            rv_key_btn.setVisibility(View.GONE);
            btn_print.setVisibility(View.GONE);
            edit_scalesCode.setVisibility(View.GONE);
        }

    }

    public void setButtonSelected(int position, int... prositions) {
        try {
           /* if (position==4){
                operatingAdapter.getItem(4).setSelected(true);
                operatingAdapter.getItem(5).setSelected(false);

            }
            else if (position==5){
                operatingAdapter.getItem(4).setSelected(false);
                operatingAdapter.getItem(5).setSelected(true);
            }
            else{
                operatingAdapter.getItem(4).setSelected(false);
                operatingAdapter.getItem(5).setSelected(false);
            }*/
//            operatingAdapter.notifyItemChanged(4);
//            operatingAdapter.notifyItemChanged(5);
//            operatingAdapter.notifyDataSetChanged();

            operatingAdapter.getItem(position).setSelected(!operatingAdapter.getItem(position).isSelected());
            for (int i = 0; i < prositions.length; i++) {
                operatingAdapter.getItem(prositions[i]).setSelected(false);
            }
            operatingAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public KeyBoardEditText getKeyBoardEditText() {
        return keyBoardEditText;
    }

    @Override
    public void onClick(View view) {
    }

    public void clearInput() {
        if (mIsLandscape) {
            keyBoardEditText.setText("");
        } else {
            edit_scalesCode.setText("");
        }

    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_aiposui_operating;
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_aiposui_operating_port;
    }

    public TextView getBtn_print() {
        return btn_print;
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    myTime.setText(getTime());
                    break;
                default:
                    break;
            }
        }
    };

    //获得当前年月日时分秒星期
    public String getTime() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
//        String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));//时
        String mHour = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));//时
//        String mMinute = String.valueOf(c.get(Calendar.MINUTE));//分
        String mMinute = String.format("%02d", c.get(Calendar.MINUTE));//分
        String mSecond = String.format("%02d", c.get(Calendar.SECOND));//秒
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return mYear + "年" + mMonth + "月" + mDay+"日"+" "+"星期"+mWay+" "+mHour+":"+mMinute+":"+mSecond;
//        return mYear + "年" + mMonth + "月" + mDay + "日" + " " + "星期" + mWay + " " + mHour + ":" + mMinute;
    }
}
