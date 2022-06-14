package com.wintec.lamp;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Toast;

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
import com.wintec.lamp.base.BaseActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.data.EditType;
import com.wintec.lamp.entity.EditEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import butterknife.BindView;

public class SendSettingActivity extends BaseActivity {


    @BindView(R.id.send_topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_send_groupList)
    QMUIGroupListView groupListView;

    private QMUIPopup mNormalPopup;
    View rootLayout;
    AiTipDialog aiTipDialog;

    private Map<String, QMUICommonListItemView> itemViewMap;

    @Override
    public int getView() {
        return R.layout.activity_send_setting;
    }

    @Override
    public void initView() {
        super.initView();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("传秤设置");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
//        addSettingItem(Const.KEY_SEND_SCALE, "传秤设置开启", height, false);
        addSettingItem(Const.KEY_SCALE_PORT, "端口号", height, false);
        addSettingItem(Const.KEY_SEND_UNIT, "传秤单位", height, false);
        addSettingItem(Const.TRACEABILITY_CODE_FLAG, "开启追溯码传秤", height, false);
        addSettingItem(Const.TRACEABILITY_CODE_PORT, "追溯码传秤端口号", height, false);
        // QMUICommonListItemView scaleSwith = setSwith(Const.KEY_SEND_SCALE);
        QMUICommonListItemView traceabilitySwith = setSwith(Const.TRACEABILITY_CODE_FLAG);
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("传秤设置")
                .setLeftIconSize(size, size)
//                .addItemView(itemViewMap.get(Const.KEY_SEND_SCALE), v -> {
//                    scaleSwith.getSwitch().performClick();
//                })
                .addItemView(itemViewMap.get(Const.TRACEABILITY_CODE_FLAG), v -> {
                    traceabilitySwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.KEY_SCALE_PORT), v -> {
                    startEdit(Const.KEY_SCALE_PORT, "传秤端口", Const.getSettingValue(Const.KEY_SCALE_PORT), EditType.Edit_TYPE_NUMBER);
                })
                .addItemView(itemViewMap.get(Const.TRACEABILITY_CODE_PORT), v -> {
                    startEdit(Const.TRACEABILITY_CODE_PORT, "追溯码传秤端口号", Const.getSettingValue(Const.TRACEABILITY_CODE_PORT), EditType.Edit_TYPE_NUMBER);
                })
                .addItemView(itemViewMap.get(Const.KEY_SEND_UNIT), v -> {
                    showSimpleBottomSheetList(
                            true, false, "传秤单位",
                            new String[]{"kg", "500g"}, true, false, Const.KEY_SEND_UNIT, itemViewMap.get(Const.KEY_SEND_UNIT));
                })
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);
//        itemViewMap.get(Const.KEY_SEND_SCALE).setDetailText("");
        itemViewMap.get(Const.TRACEABILITY_CODE_FLAG).setDetailText("");

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

    @Override
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

    private void showSimpleBottomSheetList(boolean gravityCenter,
                                           boolean addCancelBtn,
                                           CharSequence title,
                                           String[] itemList,
                                           boolean allowDragDismiss,
                                           boolean withMark,
                                           String key,
                                           QMUICommonListItemView view) {
        QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(mContext);
        builder.setGravityCenter(gravityCenter)
                .setSkinManager(QMUISkinManager.defaultInstance(mContext))
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
}