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

public class UiSettingActivity extends BaseActivity {


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
        mTopBar.setTitle("页面设置");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem(Const.KEY_REDETECT_AND_PRINT, "重新识别与打印", height, true);
        addSettingItem(Const.KEY_GOODS_COUNT, "结果显示个数", height, true);
        addSettingItem(Const.KEY_SHORTCUT_GOODS_COUNT, "快捷选项显示个数", height, true);
        //设置switch
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("基本设置")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get(Const.KEY_REDETECT_AND_PRINT), v -> {
                    String[] listItems = new String[]{
                            "重新识别",
                            "打印",
                    };
                    // popupsShow(v,listItems,Constants.WEIGHT_UNIT, itemViewMap.get(Constants.WEIGHT_UNIT));
                    showSimpleBottomSheetList(
                            true, false, "重新识别与打印",
                            listItems, true, false, Const.KEY_REDETECT_AND_PRINT, itemViewMap.get(Const.KEY_REDETECT_AND_PRINT));
                })
                .addItemView(itemViewMap.get(Const.KEY_GOODS_COUNT), v -> {
                    String[] listItems = new String[]{
                            "4",
                            "5",
                            "6",
                            "7"
                    };
                    // popupsShow(v,listItems,Constants.WEIGHT_UNIT, itemViewMap.get(Constants.WEIGHT_UNIT));
                    showSimpleBottomSheetList(
                            true, false, "结果显示个数",
                            listItems, true, false, Const.KEY_GOODS_COUNT, itemViewMap.get(Const.KEY_GOODS_COUNT));
                })
                .addItemView(itemViewMap.get(Const.KEY_SHORTCUT_GOODS_COUNT), v -> {
                    String[] listItems = new String[]{
                            "4",
                            "5",
                            "6",
                            "7"
                    };
                    // popupsShow(v,listItems,Constants.WEIGHT_UNIT, itemViewMap.get(Constants.WEIGHT_UNIT));
                    showSimpleBottomSheetList(
                            true, false, "快捷选项显示个数",
                            listItems, true, false, Const.KEY_SHORTCUT_GOODS_COUNT, itemViewMap.get(Const.KEY_SHORTCUT_GOODS_COUNT));
                })
//                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(mContext, 16), 0)
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);

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

    private Context getActivity() {
        return mContext;
    }

    private Context getContext() {
        return mContext;
    }
}