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
import com.wintec.lamp.presenter.WelcomePresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import butterknife.BindView;

public class WeightSettingActivity extends BaseActivity {


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
        aiTipDialog = new AiTipDialog();
        super.initView();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("称重设置");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem(Const.WEIGHT_UNIT, "重量单位", height, true);
        addSettingItem(Const.UNIT_PRICE_POINT, "单价小数点", height, true);
        addSettingItem(Const.WEIGHT_POINT, "重量小数点", height, true);
        addSettingItem(Const.TOTAL_PRICE_POINT, "总价小数点", height, true);
        addSettingItem(Const.TOTAL_PRICE_MODE, "总价圆整", height, true);
        addSettingItem(Const.ERP_BRANCH_ID, "门店编码", height, true);
        addSettingItem(Const.KEY_GOODS_COUNT, "结果显示个数", height, true);
        addSettingItem(Const.RESULT_DISPLAY, "显示结果", height, true);
        addSettingItem(Const.RESULT_DISPLAY_TIME, "结果显示时间", height, true);
        addSettingItem(Const.DELECT_WEIGHT, "识别触发重量(g)", height, true);
        addSettingItem(Const.PRINT_SETTING, "打印机设置", height, true);
        addSettingItem(Const.IMAGE_PRE_TIME, "轮播图间隔时间", height, true);
        addSettingItem(Const.DETECT_THRESHOLD, "识别阈值", height, true);
//        addSettingItem(Const.ROTATION_SETTING, "回转距离(单位 1/8mm)", height, true);
        addSettingItem(Const.VOIDCE_BROADCAST_FLAG, "语音播报", height, true);
        addSettingItem(Const.PREVIEW_FLAG, "商品预览图", height, true);
        addSettingItem("UpPLUS", "上传商品匹配", height, true);
        addSettingItem("DownLoadIMG", "下载商品预览图", height, true);
        addSettingItem(Const.SEARCH_LENGHT, "模糊搜索位数", height, true);

        addSettingItem(Const.KEY_MODE, "收银模式", height, true);
        QMUICommonListItemView resultDisplaySwith = setSwith(Const.RESULT_DISPLAY);
        QMUICommonListItemView voidceBroadcastSwith = setSwith(Const.VOIDCE_BROADCAST_FLAG);
        QMUICommonListItemView prviewSwith = setSwith(Const.PREVIEW_FLAG);
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("基本设置")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get(Const.WEIGHT_UNIT), v -> {
                    String[] listItems = new String[]{
                            "kg",
                            "500g",
                    };
                    // popupsShow(v,listItems,Constants.WEIGHT_UNIT, itemViewMap.get(Constants.WEIGHT_UNIT));
                    showSimpleBottomSheetList(
                            true, false, "重量单位",
                            listItems, true, false, Const.WEIGHT_UNIT, itemViewMap.get(Const.WEIGHT_UNIT));
                })
                .addItemView(itemViewMap.get(Const.ERP_BRANCH_ID), v -> {
                    startEdit(Const.ERP_BRANCH_ID, "门店编码", Const.getSettingValue(Const.ERP_BRANCH_ID), EditType.Edit_TYPE_TEXT);
                })
//                .addItemView(itemViewMap.get(Const.UNIT_PRICE_POINT), v -> {
//                    String[] listItems = new String[]{
//                            "2",
//                            "3",
//                    };
//                    // popupsShow(v,listItems,Constants.UNIT_PRICE_POINT, itemViewMap.get(Constants.UNIT_PRICE_POINT));
////                    showSimpleBottomSheetList(
////                            true, false, "单价小数点",
////                            listItems, true, false, Const.UNIT_PRICE_POINT, itemViewMap.get(Const.UNIT_PRICE_POINT));
//                })

//                .addItemView(itemViewMap.get(Const.WEIGHT_POINT), v -> {
//                    String[] listItems = new String[]{
//                            "2",
//                            "3",
//                    };
//                    // popupsShow(v,listItems,Constants.WEIGHT_POINT, itemViewMap.get(Constants.WEIGHT_POINT));
////                    showSimpleBottomSheetList(
////                            true, false, "重量小数点",
////                            listItems, true, false, Const.WEIGHT_POINT, itemViewMap.get(Const.WEIGHT_POINT));
//                })

//                .addItemView(itemViewMap.get(Const.TOTAL_PRICE_POINT), v -> {
//                    String[] listItems = new String[]{
//                            "2",
//                            "3",
//                    };
//                    // popupsShow(v,listItems,Constants.TOTAL_PRICE_POINT, itemViewMap.get(Constants.TOTAL_PRICE_POINT));
////                    showSimpleBottomSheetList(
////                            true, false, "总价小数点",
////                            listItems, true, false, Const.TOTAL_PRICE_POINT, itemViewMap.get(Const.TOTAL_PRICE_POINT));
//                })
                .addItemView(itemViewMap.get(Const.KEY_GOODS_COUNT), v -> {
                    String[] listItems = new String[]{
                            "1",
                            "2",
                            "3",
                            "4",
                            "5",
                            "6",
                            "7",
                            "8",
                            "9",
                            "10"
                    };
                    showSimpleBottomSheetList(
                            true, false, "结果显示个数",
                            listItems, true, false, Const.KEY_GOODS_COUNT, itemViewMap.get(Const.KEY_GOODS_COUNT));
                })
                .addItemView(itemViewMap.get(Const.KEY_MODE), v -> {
                    String[] listItems = new String[]{
                            "价签模式",
                            "收银台模式",
                    };
                    // popupsShow(v,listItems,Constants.WEIGHT_UNIT, itemViewMap.get(Constants.WEIGHT_UNIT));
                    showSimpleBottomSheetList(
                            true, false, "收银模式",
                            listItems, true, false, Const.KEY_MODE, itemViewMap.get(Const.KEY_MODE));
                })
                .addItemView(itemViewMap.get(Const.TOTAL_PRICE_MODE), v -> {
                    String[] listItems = new String[]{
                            "不圆整(18.16)",
                            "四舍五入(18.2)",
                            "强舍(18.1)",
                            "强入(18.2)",
                            "四舍五入(18.22)"
                    };
                    // popupsShow(v,listItems,Constants.TOTAL_PRICE_MODE, itemViewMap.get(Constants.TOTAL_PRICE_MODE));
                    showSimpleBottomSheetList(
                            true, false, "总价圆整(18.16)",
                            listItems, true, false, Const.TOTAL_PRICE_MODE, itemViewMap.get(Const.TOTAL_PRICE_MODE));
                })
                .addItemView(itemViewMap.get(Const.DELECT_WEIGHT), v -> {
                    startEdit(Const.DELECT_WEIGHT, "识别触发重量(g)", Const.getSettingValue(Const.DELECT_WEIGHT), EditType.Edit_TYPE_NUMBER);
                })
                .addItemView(itemViewMap.get(Const.RESULT_DISPLAY), v -> {
                    resultDisplaySwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.RESULT_DISPLAY_TIME), v -> {
                    startEdit(Const.RESULT_DISPLAY_TIME, "展示时间", Const.getSettingValue(Const.RESULT_DISPLAY_TIME), EditType.Edit_TYPE_NUMBER);
                })
                .addItemView(itemViewMap.get(Const.PRINT_SETTING), v -> {
                    String[] listItems = new String[]{
                            "普瑞特打印机",
                            "宁致打印机",
                    };
                    // popupsShow(v,listItems,Constants.TOTAL_PRICE_MODE, itemViewMap.get(Constants.TOTAL_PRICE_MODE));
                    showSimpleBottomSheetList(
                            true, false, "打印机设置",
                            listItems, true, false, Const.PRINT_SETTING, itemViewMap.get(Const.PRINT_SETTING));
                })
//                .addItemView(itemViewMap.get(Const.ROTATION_SETTING), v -> {
//                    if(!"宁致打印机".equals(Const.getSettingValue(Const.PRINT_SETTING)))
//                    {
//                        aiTipDialog.showFail("请选择宁致打印机",v);
//                        return;
//                    }
//                    startEdit(Const.ROTATION_SETTING, "回转距离(单位 1/8mm)", Const.getSettingValue(Const.ROTATION_SETTING), EditType.Edit_TYPE_INTEGER);
//                })
                .addItemView(itemViewMap.get(Const.VOIDCE_BROADCAST_FLAG), v -> {
                    voidceBroadcastSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.PREVIEW_FLAG), v -> {
                    prviewSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get("UpPLUS"), v -> {
                    //todo
                    aiTipDialog.showLoading("正在上传商品信息", mContext);
//                    new WelcomePresenter().upPLUDto();
                    aiTipDialog.dismiss();

                })
                .addItemView(itemViewMap.get("DownLoadIMG"), v -> {
                    //todo
                    aiTipDialog.showLoading("正在下载预览图", mContext);
//                    new WelcomePresenter().getImgUrl();
                    aiTipDialog.dismiss();
                })
                .addItemView(itemViewMap.get(Const.SEARCH_LENGHT), v -> {
                    startEdit(Const.SEARCH_LENGHT, "模糊搜索位数", Const.getSettingValue(Const.SEARCH_LENGHT), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.IMAGE_PRE_TIME), v -> {
                    startEdit(Const.IMAGE_PRE_TIME, "轮播图间隔时间(ms)", Const.getSettingValue(Const.IMAGE_PRE_TIME), EditType.Edit_TYPE_NUMBER);
                })
                .addItemView(itemViewMap.get(Const.DETECT_THRESHOLD), v -> {
                    startEdit(Const.DETECT_THRESHOLD, "设置识别阈值(0-0.8)", Const.getSettingValue(Const.DETECT_THRESHOLD), EditType.Edit_TYPE_TEXT);
                })
//                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(mContext, 16), 0)
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);
        itemViewMap.get(Const.RESULT_DISPLAY).setDetailText("");
        itemViewMap.get(Const.VOIDCE_BROADCAST_FLAG).setDetailText("");
        itemViewMap.get(Const.PREVIEW_FLAG).setDetailText("");
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
}