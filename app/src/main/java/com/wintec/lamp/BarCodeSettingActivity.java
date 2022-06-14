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

public class BarCodeSettingActivity extends BaseActivity {


    @BindView(R.id.bar_code_topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_bar_code_groupList)
    QMUIGroupListView groupListView;

    private QMUIPopup mNormalPopup;
    View rootLayout;
    AiTipDialog aiTipDialog;

    private Map<String, QMUICommonListItemView> itemViewMap;

    @Override
    public int getView() {
        return R.layout.activity_bar_code_setting;
    }

    @Override
    public void initView() {
        super.initView();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("条码格式设置");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem(Const.BAR_CODE_LENGTH, "条码位数", height, false);
        addSettingItem(Const.BAR_CODE_PREFIX, "称重码前缀", height, false);
        addSettingItem(Const.BAR_CODE_PREFIX_COORDINATE, "称重码前缀下标(2位)", height, false);
        addSettingItem(Const.BAR_CODE_PLU_COORDINATE, "plu下标(5位)", height, false);
        addSettingItem(Const.BAR_CODE_TOTAL_COORDINATE, "总价下标(5位)", height, false);
        addSettingItem(Const.BAR_CODE_WEIGHT_COORDINATE, "重量下标(5位)", height, false);
        addSettingItem(Const.BAR_CODE_PRICE_COORDINATE, "单价下标(5位)", height, false);
        addSettingItem(Const.BAR_CODE_PIECT_COORDINATE, "计件下标(5位)", height, false);
        addSettingItem(Const.BAR_CODE_PIECT_FLAG, "计数商品数量位", height, false);
        addSettingItem(Const.KEY_ODD_EVEN_CHECK, "校验位设置", height, true);
        addSettingItem(Const.SCALE_NO, "称号", height, true);
        addSettingItem("CHECK_BAR_CODE", "条码检测", height, true);
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("传称设置")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get(Const.BAR_CODE_LENGTH), v -> {
                    startEdit(Const.BAR_CODE_LENGTH, "条码位数", Const.getSettingValue(Const.BAR_CODE_LENGTH), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PREFIX), v -> {
                    startEdit(Const.BAR_CODE_PREFIX, "称重码前缀", Const.getSettingValue(Const.BAR_CODE_PREFIX), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PREFIX_COORDINATE), v -> {
                    startEdit(Const.BAR_CODE_PREFIX_COORDINATE, "称重码前缀下标(2位)", Const.getSettingValue(Const.BAR_CODE_PREFIX_COORDINATE), EditType.Edit_TYPE_COORDINATE);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PLU_COORDINATE), v -> {
                    startEdit(Const.BAR_CODE_PLU_COORDINATE, "plu下标(5位)", Const.getSettingValue(Const.BAR_CODE_PLU_COORDINATE), EditType.Edit_TYPE_COORDINATE);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_TOTAL_COORDINATE), v -> {
                    startEdit(Const.BAR_CODE_TOTAL_COORDINATE, "总价下标(5位)", Const.getSettingValue(Const.BAR_CODE_TOTAL_COORDINATE), EditType.Edit_TYPE_COORDINATE);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_WEIGHT_COORDINATE), v -> {
                    startEdit(Const.BAR_CODE_WEIGHT_COORDINATE, "重量下标(5位)", Const.getSettingValue(Const.BAR_CODE_WEIGHT_COORDINATE), EditType.Edit_TYPE_COORDINATE);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PRICE_COORDINATE), v -> {
                    startEdit(Const.BAR_CODE_PRICE_COORDINATE, "单价下标(5位)", Const.getSettingValue(Const.BAR_CODE_PRICE_COORDINATE), EditType.Edit_TYPE_COORDINATE);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PIECT_COORDINATE), v -> {
                    startEdit(Const.BAR_CODE_PIECT_COORDINATE, "计件下标(5位)", Const.getSettingValue(Const.BAR_CODE_PIECT_COORDINATE), EditType.Edit_TYPE_COORDINATE);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PIECT_FLAG), v -> {
                    String[] listItems = new String[]{
                            "个位开始",
                            "千位开始",
                    };
                    showSimpleBottomSheetList(
                            true, false, "计数商品数量位",
                            listItems, true, false, Const.BAR_CODE_PIECT_FLAG, itemViewMap.get(Const.BAR_CODE_PIECT_FLAG));
                })
                .addItemView(itemViewMap.get(Const.KEY_ODD_EVEN_CHECK), v -> {
                    String[] listItems = new String[]{
                            "奇校验",
                            "偶校验",
                    };
                    showSimpleBottomSheetList(
                            true, false, "校验位设置",
                            listItems, true, false, Const.KEY_ODD_EVEN_CHECK, itemViewMap.get(Const.KEY_ODD_EVEN_CHECK));
                })
                .addItemView(itemViewMap.get(Const.SCALE_NO), v -> {
                    startEdit(Const.SCALE_NO, "称号", Const.getSettingValue(Const.SCALE_NO), EditType.Edit_TYPE_NUMBER);
                })
                .addItemView(itemViewMap.get("CHECK_BAR_CODE"), v -> {
                    //todo 条码检测
                })
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    private void addSettingItem(String key, String title, int height, Boolean isBtn) {
        QMUICommonListItemView itemView = null;
        if (!isBtn) {
            itemView = groupListView.createItemView(null, title, Const.getSettingValue(key), QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, height);
        } else {
            itemView = groupListView.createItemView(null, title, Const.getSettingValue(key), QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE, height);
        }
        itemViewMap.put(key, itemView);

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