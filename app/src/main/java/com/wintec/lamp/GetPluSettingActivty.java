package com.wintec.lamp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.lamp.base.BaseActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.data.EditType;
import com.wintec.lamp.entity.EditEntity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class GetPluSettingActivty extends BaseActivity {


    @BindView(R.id.weight_topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_weight_groupList)
    QMUIGroupListView groupListView;

    private QMUIPopup mNormalPopup;
    View rootLayout;
    AiTipDialog aiTipDialog;

    private Map<String, QMUICommonListItemView> itemViewMap;

    @Override
    public int getView() {
        return R.layout.activity_weight_setting;
    }

    @Override
    public void initView() {
        super.initView();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("数据源设置");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem(Const.KEY_GET_DATA_MODE, "获取数据方式", height, true);
        addSettingItem(Const.DELAY_TIME, "定时取数时间间隔(单位秒)", height, true);
        addSettingItem(Const.KEY_GET_DATA_DB, "数据库选择", height, true);
        addSettingItem("ScaleSend", "传秤设置", height, false);
        addSettingItem(Const.KEY_GET_DATA_IP, "数据库ip", height, false);
        addSettingItem(Const.KEY_GET_DATA_PORT, "数据库端口", height, false);
        addSettingItem(Const.KEY_GET_DATA_DB_NAME, "数据库或服务名称", height, false);
        addSettingItem(Const.KEY_GET_DATA_USER, "账号", height, false);
        addSettingItem(Const.KEY_GET_DATA_PWD, "密码", height, false);
        addSettingItem(Const.KEY_GET_DATA_SQL, "商品查询sql", height, false);
        addSettingItem(Const.KEY_GET_DATA_ADDITIONAL_SQL, "附加文本查询sql", height, false);
        addSettingItem(Const.KEY_GET_DATA_UP_PRICE_CHANGE, "改价上报", height, true);
        addSettingItem(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL, "改价上报SQL", height, true);
        QMUICommonListItemView priceChangeSwitch = setSwith(Const.KEY_GET_DATA_UP_PRICE_CHANGE);
        //设置switch
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("基本设置")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_MODE), v -> {
                    String[] listItems = new String[]{
                            "托利多传秤",
                            "批量取数",
                            "在线取数",
                    };
                    // popupsShow(v,listItems,Constants.WEIGHT_UNIT, itemViewMap.get(Constants.WEIGHT_UNIT));
                    showSimpleBottomSheetList(
                            true, false, "获取数据方式",
                            listItems, true, false, Const.KEY_GET_DATA_MODE, itemViewMap.get(Const.KEY_GET_DATA_MODE));
                })
                .addItemView(itemViewMap.get(Const.DELAY_TIME), v -> {
                    startEdit(Const.DELAY_TIME, "定时取数间隔", Const.getSettingValue(Const.DELAY_TIME), EditType.Edit_TYPE_NUMBER);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_DB), v -> {
                    String[] listItems = new String[]{
                            "sql Server",
                            "oracle",
                    };
                    // popupsShow(v,listItems,Constants.WEIGHT_UNIT, itemViewMap.get(Constants.WEIGHT_UNIT));
                    showSimpleBottomSheetList(
                            true, false, "数据库选择",
                            listItems, true, false, Const.KEY_GET_DATA_DB, itemViewMap.get(Const.KEY_GET_DATA_DB));
                })
                .addItemView(itemViewMap.get("ScaleSend"), v -> {
                    Intent intent = new Intent(this, SendSettingActivity.class);
                    startActivity(intent);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_IP), v -> {
                    startEdit(Const.KEY_GET_DATA_IP, "数据库ip", Const.getSettingValue(Const.KEY_GET_DATA_IP), EditType.EDIT_TYPE_IP);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_PORT), v -> {
                    startEdit(Const.KEY_GET_DATA_PORT, "数据库端口", Const.getSettingValue(Const.KEY_GET_DATA_PORT), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_DB_NAME), v -> {
                    startEdit(Const.KEY_GET_DATA_DB_NAME, "数据库或服务名称", Const.getSettingValue(Const.KEY_GET_DATA_DB_NAME), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_USER), v -> {
                    startEdit(Const.KEY_GET_DATA_USER, "账号", Const.getSettingValue(Const.KEY_GET_DATA_USER), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_PWD), v -> {
                    startEdit(Const.KEY_GET_DATA_PWD, "密码", Const.getSettingValue(Const.KEY_GET_DATA_PWD), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_SQL), v -> {
                    startEdit(Const.KEY_GET_DATA_SQL, "查询sql", Const.getSettingValue(Const.KEY_GET_DATA_SQL), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_ADDITIONAL_SQL), v -> {
                    startEdit(Const.KEY_GET_DATA_ADDITIONAL_SQL, "查询sql", Const.getSettingValue(Const.KEY_GET_DATA_ADDITIONAL_SQL), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_UP_PRICE_CHANGE), v -> {
                    priceChangeSwitch.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL), v -> {
                    startEdit(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL, "改价上报SQL", Const.getSettingValue(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL), EditType.Edit_TYPE_TEXT);
                })
//                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(mContext, 16), 0)
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);
        itemViewMap.get(Const.KEY_GET_DATA_UP_PRICE_CHANGE).setDetailText("");

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

    private Context getActivity() {
        return mContext;
    }

    private Context getContext() {
        return mContext;
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
}