package com.wintec.lamp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.detection.WtAISDK;
import com.wintec.detection.http.OnUnRegSDKListener;
import com.wintec.detection.utils.StringUtils;
import com.wintec.lamp.base.BaseActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.helper.AccDtoHelper;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.data.EditType;
import com.wintec.lamp.entity.EditEntity;
import com.wintec.lamp.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ButtonSettingActivity extends BaseActivity {


    @BindView(R.id.button_topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_button_groupList)
    QMUIGroupListView groupListView;

    private QMUIPopup mNormalPopup;
    View rootLayout;
    AiTipDialog aiTipDialog;

    private Map<String, QMUICommonListItemView> itemViewMap;

    @Override
    public int getView() {
        return R.layout.activity_button_setting;
    }

    @Override
    public void initView() {
        super.initView();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("按钮设置");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem(Const.PRINTERER_SPEED, "打印速度", height, false);
        addSettingItem(Const.PRINTER_CONCENTRATION, "打印浓度", height, false);
        addSettingItem(Const.KEY_UPPRICE_FLAG, "永久改价", height, false);
        addSettingItem(Const.KEY_DISCOUNT_FLAG, "折扣", height, false);
        addSettingItem(Const.KEY_TEMPPRICE_FLAG, "临时改价", height, false);
        addSettingItem(Const.KEY_RESTUDY_FLAG, "重新学习", height, false);
        addSettingItem(Const.KEY_ALL_ONE_FLAG, "多品一签", height, false);

        addSettingItem(Const.ONE_TEMPPRICE_FLAG, "临时改价仅生效一次", height, false);
        addSettingItem(Const.ONE_DISCOUNT_FLAG, "折扣仅生效一次", height, false);
        addSettingItem(Const.PLU_ATTR_DISPRICE_FLAG, "商品属性变价", height, false);

        addSettingItem("UnbindPos", "设备解绑", height, true);

        //设置switch
        QMUICommonListItemView upPriceSwith = setSwith(Const.KEY_UPPRICE_FLAG);
        QMUICommonListItemView disCountSwith = setSwith(Const.KEY_DISCOUNT_FLAG);
        QMUICommonListItemView tempPriceSwith = setSwith(Const.KEY_TEMPPRICE_FLAG);
        QMUICommonListItemView restudySwith = setSwith(Const.KEY_RESTUDY_FLAG);
        QMUICommonListItemView allOneSwith = setSwith(Const.KEY_ALL_ONE_FLAG);
        QMUICommonListItemView oneDisCountSwith = setSwith(Const.ONE_DISCOUNT_FLAG);
        QMUICommonListItemView oneTemPriceSwith = setSwith(Const.ONE_TEMPPRICE_FLAG);
        QMUICommonListItemView pluAttrDispriceFlag = setSwith(Const.PLU_ATTR_DISPRICE_FLAG);

        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("基本设置")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get(Const.PRINTERER_SPEED), v -> {
                    startEdit(Const.PRINTERER_SPEED, "打印速度[0-15]", Const.getSettingValue(Const.PRINTERER_SPEED), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.PRINTER_CONCENTRATION), v -> {
                    startEdit(Const.PRINTER_CONCENTRATION, "打印浓度[0-7]", Const.getSettingValue(Const.PRINTER_CONCENTRATION), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.KEY_UPPRICE_FLAG), v -> {
                    upPriceSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.KEY_DISCOUNT_FLAG), v -> {
                    disCountSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.KEY_TEMPPRICE_FLAG), v -> {
                    tempPriceSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.KEY_RESTUDY_FLAG), v -> {
                    restudySwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.KEY_ALL_ONE_FLAG), v -> {
                    allOneSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.ONE_DISCOUNT_FLAG), v -> {
                    oneDisCountSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.ONE_TEMPPRICE_FLAG), v -> {
                    oneTemPriceSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.PLU_ATTR_DISPRICE_FLAG), v -> {
                    pluAttrDispriceFlag.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get("UnbindPos"), v -> {
                    try {
                        alertDialog();
//                        unbindPos(Const.SN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
//                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(mContext, 16), 0)
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);
        itemViewMap.get(Const.KEY_UPPRICE_FLAG).setDetailText("");
        itemViewMap.get(Const.KEY_DISCOUNT_FLAG).setDetailText("");
        itemViewMap.get(Const.KEY_TEMPPRICE_FLAG).setDetailText("");
        itemViewMap.get(Const.KEY_RESTUDY_FLAG).setDetailText("");
        itemViewMap.get(Const.KEY_ALL_ONE_FLAG).setDetailText("");
        itemViewMap.get(Const.ONE_DISCOUNT_FLAG).setDetailText("");
        itemViewMap.get(Const.ONE_TEMPPRICE_FLAG).setDetailText("");
        itemViewMap.get(Const.PLU_ATTR_DISPRICE_FLAG).setDetailText("");


    }

    @Override
    protected void initData() {
        super.initData();
    }


    private void addSettingItem(String key, String title, int height, Boolean isBtn) {
        if (!isBtn) {
            itemViewMap.put(key, groupListView.createItemView(null, title, Const.getSettingValue(key),
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, height));
        } else {
            itemViewMap.put(key, groupListView.createItemView(null, title, Const.getSettingValue(key),
                    QMUICommonListItemView.HORIZONTAL,
                    QMUICommonListItemView.ACCESSORY_TYPE_NONE, height));
        }

    }

    void popupsShow(View v, String[] listItems, String key, QMUICommonListItemView itemView) {

        List<String> data = new ArrayList<>();

        Collections.addAll(data, listItems);

        ArrayAdapter adapter = new ArrayAdapter<>(mContext, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(mContext, "已选择" + listItems[i], Toast.LENGTH_SHORT).show();
                Const.setSettingValue(key, listItems[i]);
                itemView.setDetailText(listItems[i]);
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };
        mNormalPopup = QMUIPopups.listPopup(getContext(),
                QMUIDisplayHelper.dp2px(getContext(), 250),
                QMUIDisplayHelper.dp2px(getContext(), 300),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_RIGHT)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(getContext(), 5))
                .skinManager(QMUISkinManager.defaultInstance(getContext()))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(v);
    }

    private void showSimpleBottomSheetList(boolean gravityCenter,
                                           boolean addCancelBtn,
                                           CharSequence title,
                                           String[] itemList,
                                           boolean allowDragDismiss,
                                           boolean withMark,
                                           String key,
                                           QMUICommonListItemView view) {
        QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(getActivity());
        builder.setGravityCenter(gravityCenter)
                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                .setTitle(title)
                .setAddCancelBtn(addCancelBtn)
                .setAllowDrag(allowDragDismiss)
                .setNeedRightMark(withMark)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        Toast.makeText(mContext, "已选择" + itemList[position], Toast.LENGTH_SHORT).show();
                        Const.setSettingValue(key, itemList[position]);
                        view.setDetailText(itemList[position]);
                    }
                });
        if (withMark) {
            builder.setCheckedIndex(40);
        }
        for (int i = 0; i < itemList.length; i++) {
            builder.addItem(itemList[i]);
        }
        builder.build().show();
    }

    private QMUICommonListItemView setSwith(String key) {
        QMUICommonListItemView itemWithSwitch = itemViewMap.get(key);
        itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemWithSwitch.getSwitch().setScaleX(1.0f);
        itemWithSwitch.getSwitch().setScaleY(1.0f);
        if (Const.getSettingValue(key).equals("1")) {
            itemWithSwitch.getSwitch().setChecked(true);
        }
        itemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Const.setSettingValue(key, "1");
                } else {
                    Const.setSettingValue(key, "0");
                }

            }
        });
        return itemWithSwitch;
    }

    public void startEdit(String key, String title, String value, EditType type) {
        Intent intent = new Intent(mContext, EditActivity.class);
        EditEntity editEntity = new EditEntity();
        editEntity.setKey(key);
        editEntity.setTitle(title);
        editEntity.setDetailText(value);
        editEntity.setType(type);
        intent.putExtra("value", editEntity);
        jumpToActivityForResult(intent, 0x01);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0x01:
                if (resultCode == RESULT_OK) {
                    EditEntity editEntity = (EditEntity) data.getSerializableExtra("value");
                    itemViewMap.get(editEntity.getKey()).setDetailText(editEntity.getDetailText());
                    Const.setSettingValue(editEntity.getKey(), editEntity.getDetailText());
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Context getActivity() {
        return mContext;
    }

    private Context getContext() {
        return mContext;
    }

    public void jumpToWelcomeActivity() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //startActivity(new Intent(this, ScaleActivityUI.class));
        Intent intent = new Intent(this, WelcomeActivity.class);
        Const.setSettingValue(Const.KEY_IS_BIND, "");
        startActivity(intent);

        finish();
    }

    /**
     * okhttp的post方法
     *
     * @param sn 机器sn
     * @description: 解绑设备
     * @return: void
     * @author: dean
     */
    private void unbindPos(String sn) throws IOException {
        if (StringUtils.isNotEmpty(sn)) {
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("sn", sn).build();
            Request.Builder builder = new Request.Builder();
            Request build = builder.url(Const.BASE_URL + "privateKey/untie").post(body).build();

            Call call = okHttpClient.newCall(build);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtils.showToast("解绑POS失败,请求wintec服务失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String secretKey = jsonObject.get("data") + ""; // 激活码(密钥)
                    String tenant = Const.getSettingValue(Const.KEY_ACCOUNT); //租户号
                    String posNumber = Const.getSettingValue(Const.KEY_POS_ID);   //POS编号
                    if (StringUtils.isNotEmpty(secretKey)) {
                        WtAISDK.api_unregSDK(secretKey, posNumber, new OnUnRegSDKListener() {
                            @Override
                            public void unregFail(int i, String s) {
                                ToastUtils.showToast("未查到机器信息,请联系客服人员,错误代码:" + i);
                            }

                            @Override
                            public void unregSuccess() {
                                PluDtoDaoHelper.deleteAll();
                                AccDtoHelper.deleteAll();
                                WtAISDK.api_clearTrainedData();
                                SharedPreferences setting = getSharedPreferences("First.ini", 0);
                                setting.edit().putBoolean("FIRST", true).commit();
                                jumpToWelcomeActivity();
                            }

                        });
                    } else {
                        ToastUtils.showToast("解绑失败，未查到机器信息，请联系客服人员");
                    }
                }
            });
        }
    }

    //    private QMUIBottomSheet okok;  // 退出确认底部菜单
//    okok = new NUIBottomSheet(context).createQuitBottomSheet(new NUIBottomSheet.ClickListener() {
//        @Override
//        public void onItemClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
//            if (position == 0) {
//                finish();
//                System.exit(0);
//            } else {
//                quitBottomSheet.dismiss();
//            }
//        }
//
//    }, "是否退出？");
    public void alertDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("解绑后数据会清除，确认解绑吗?");
        ab.setPositiveButton("确认解绑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    unbindPos(Const.SN);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
    }

}
