package com.wintec.lamp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.wintec.lamp.base.BaseMvpActivity;
import com.wintec.lamp.base.BaseMvpActivityYM;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.contract.BarSettingContart;
import com.wintec.lamp.contract.ScaleContract;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.data.EditType;
import com.wintec.lamp.entity.EditEntity;
import com.wintec.lamp.presenter.BarSettingPresenter;
import com.wintec.lamp.presenter.ScalePresenter;
import com.wintec.lamp.presenter.SettingPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

import butterknife.BindView;

public class SimBarCodeSettingActivity extends BaseMvpActivity<BarSettingPresenter> implements BarSettingContart.IView {


    @BindView(R.id.sim_bar_code_topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_sim_bar_code_groupList)
    QMUIGroupListView groupListView;

    private QMUIPopup mNormalPopup;
    View rootLayout;
    AiTipDialog aiTipDialog;

    private Map<String, QMUICommonListItemView> itemViewMap;
    String[] listItems13 = new String[]{
            "前缀-PLU-总价",
            "前缀-PLU-重量",

    };
    String[] listItems18 = new String[]{
            "前缀-PLU-总价-重量",
            "前缀-PLU-单价-重量",
            "前缀-PLU-重量-单价",
            "前缀-PLU-重量-总价",
            "货号-总价-重量",
//            "货号-重量-总价",
    };
    String[] listItems19 = new String[]{
            "前缀-货号-重量-单价",
            "前缀-PLU-重量-单价",
    };

    String[] listItems20 = new String[]{
            "前缀-PLU-重量6位-总价6位",
    };

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
                        if (key.equals(Const.BAR_CODE_FORMAT)) {
                            //整理参数
                            saveData(itemList[position]);
                        }
                        if (key.equals(Const.BAR_CODE_LENGTH)) {
                            if ("13位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                                Const.setSettingValue(Const.BAR_CODE_FORMAT, "前缀-PLU-总价");
                                showSimpleBottomSheetList(
                                        true, false, "条码格式",
                                        listItems13, false, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                            } else if ("18位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                                Const.setSettingValue(Const.BAR_CODE_FORMAT, "前缀-PLU-总价-重量");
                                showSimpleBottomSheetList(
                                        true, false, "条码格式",
                                        listItems18, false, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                            }else if ("20位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                                Const.setSettingValue(Const.BAR_CODE_FORMAT, "前缀-PLU-重量6位-总价6位");
                                showSimpleBottomSheetList(
                                        true, false, "条码格式",
                                        listItems20, false, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                            }
                            else {
                                Const.setSettingValue(Const.BAR_CODE_FORMAT, "前缀-PLU-重量-单价");
                                showSimpleBottomSheetList(
                                        true, false, "条码格式",
                                        listItems19, false, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                            }
                        }
                        if (key.equals(Const.TAG_DIRECTION)) {
                            Const.setSettingValue(Const.TAG_DIRECTION, itemList[position]);
                        }
                        if (key.equals(Const.BAR_CODE_PLU_LENGTH)) {
                            switch (Const.getSettingValue(Const.BAR_CODE_PLU_LENGTH)) {
                                case "五位":
                                    Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                                    break;
                                case "六位":
                                    if ("19位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                                        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                                    } else {
                                        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "2");
                                    }
                                    break;
                                case "七位":
                                    Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "1");
                                    break;
                                case "九位":
                                    Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "1");
                                    break;
                                default:
                                    Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                                    Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "五位");
                                    break;
                            }
                        }
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

    @Override
    public void saveData(String s) {
        if ("前缀-PLU-重量6位-总价6位".equals(s)) {
            Const.setSettingValue(Const.BAR_CODE_PREFIX_COORDINATE, "1");
            Const.setSettingValue(Const.BAR_CODE_PREFIX_LENGTH, "二位");
            Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
            Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "五位");
            Const.setSettingValue(Const.BAR_CODE_TOTAL_COORDINATE, "14");
            Const.setSettingValue(Const.BAR_CODE_TOTAL_LENGTH, "六位");

            Const.setSettingValue(Const.BAR_CODE_WEIGHT_COORDINATE, "8");
            Const.setSettingValue(Const.BAR_CODE_WEIGHT_LENGTH, "六位");
            return;
        }
        int price = s.indexOf("单");
        int total = s.indexOf("总");
        int weight = s.indexOf("重");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_COORDINATE, "1");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_LENGTH, "二位");

        switch (Const.getSettingValue(Const.BAR_CODE_PLU_LENGTH)) {
            case "五位":
                Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                break;
            case "六位":
                if ("19位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                    Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                } else {
                    Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "2");
                }
                break;
            case "七位":
                Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "1");
                break;
            case "九位":
                Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "1");
                break;
            default:
                Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "五位");
                break;
        }

        Const.setSettingValue(Const.BAR_CODE_TOTAL_COORDINATE, getCoordinate(total));
        Const.setSettingValue(Const.BAR_CODE_TOTAL_LENGTH, "五位");

        Const.setSettingValue(Const.BAR_CODE_WEIGHT_COORDINATE, getCoordinate(weight));
        Const.setSettingValue(Const.BAR_CODE_WEIGHT_LENGTH, "五位");

        Const.setSettingValue(Const.BAR_CODE_PRICE_COORDINATE, getCoordinate(price));
        Const.setSettingValue(Const.BAR_CODE_PRICE_LENGTH, "五位");
        if (s.contains("货号")) {
            if ("19位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                // 多点
                Const.setSettingValue(Const.BAR_CODE_WEIGHT_COORDINATE, "9");
                Const.setSettingValue(Const.BAR_CODE_WEIGHT_LENGTH, "五位");

                Const.setSettingValue(Const.BAR_CODE_PRICE_COORDINATE, "14");
                Const.setSettingValue(Const.BAR_CODE_PRICE_LENGTH, "五位");

                Const.setSettingValue(Const.BAR_CODE_ARTNO_COORDINATE, "3");
                Const.setSettingValue(Const.BAR_CODE_ARTNO_LENGTH, "六位");
            } else {
                // 世纪华联
                Const.setSettingValue(Const.BAR_CODE_TOTAL_COORDINATE, "8");
                Const.setSettingValue(Const.BAR_CODE_TOTAL_LENGTH, "五位");

                Const.setSettingValue(Const.BAR_CODE_WEIGHT_COORDINATE, "13");
                Const.setSettingValue(Const.BAR_CODE_WEIGHT_LENGTH, "五位");

                Const.setSettingValue(Const.BAR_CODE_ARTNO_COORDINATE, "1");
                Const.setSettingValue(Const.BAR_CODE_ARTNO_LENGTH, "七位");
            }

        } else {
            Const.setSettingValue(Const.BAR_CODE_ARTNO_COORDINATE, "");
            Const.setSettingValue(Const.BAR_CODE_ARTNO_LENGTH, "");
        }
    }

    private String getCoordinate(int s) {
        String str = "";
        switch (s) {
            case 7:
                if ("19位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                    str = "9";
                } else {
                    str = "8";
                }

                break;
            case 10:
                if ("19位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                    str = "14";
                } else {
                    str = "13";
                }
                break;
            case -1:
                break;
            default:
                break;
        }
        return str;
    }

    @Override
    protected int contentResId() {
        return R.layout.activity_sim_bar_code_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        aiTipDialog = new AiTipDialog();
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
        addSettingItem(Const.BAR_CODE_PREFIX, "称重码前缀(计重)", height, false);
        addSettingItem(Const.BAR_CODE_PREFIX_PIECE, "称重码前缀(计件)", height, false);
        addSettingItem(Const.BAR_CODE_PLU_LENGTH, "plu长度", height, false);
//        addSettingItem(Const.BAR_CODE_TOTAL_LENGTH, "总价长度", height, false);
//        addSettingItem(Const.BAR_CODE_WEIGHT_LENGTH, "重量长度", height, false);
//        addSettingItem(Const.BAR_CODE_PRICE_LENGTH, "单价长度", height, false);
        addSettingItem(Const.BAR_CODE_FORMAT, "条码格式", height, false);
        addSettingItem(Const.KEY_ODD_EVEN_CHECK, "校验位设置", height, true);
        addSettingItem(Const.BAR_CODE_PIECT_FLAG, "计数商品数量位", height, false);
        addSettingItem(Const.BAR_CODE_IS_CHECK, "是否打印校验位", height, true);
        addSettingItem(Const.BAR_CODE_MULTI_PRICE_SIGN, "多价签模式", height, true);
        addSettingItem(Const.BAR_CODE_OR_QRCODE_FLAG, "是否打印二维码", height, true);
        addSettingItem(Const.QRCODE_NUMBER_FLAG, "二维码是否打印数字编码", height, true);
        addSettingItem("PriceTagSetting", "价签设置", height, true);
        addSettingItem(Const.SCALE_NO, "秤号", height, false);
        addSettingItem(Const.KEY_BRANCH_NAME, "门店名称", height, false);
        addSettingItem(Const.BAR_ONECODE_PREFIX, "多品一签前缀", height, false);
        addSettingItem(Const.BAR_ONECODE_POSTFIX, "多品一签后缀", height, false);
        addSettingItem(Const.BAR_ONECODE_CONNECTOR, "多品一签连接符", height, false);
        addSettingItem(Const.BAR_CODE_GRAM_UNIT, "条码以(g)为单位显示", height, true);
        //  addSettingItem(Const.BAR_ONECODE_FLAG, "是否开启多品一签", height, true);
        addSettingItem("DownLoad", "下载价签信息", height, true);
        addSettingItem(Const.TAG_DIRECTION, "打印方向", height, true);
        QMUICommonListItemView isCheckSwith = setSwith(Const.BAR_CODE_IS_CHECK);
        //  QMUICommonListItemView isOneCode = setSwith(Const.BAR_ONECODE_FLAG);
        QMUICommonListItemView codeFlagSwith = setSwith(Const.BAR_CODE_OR_QRCODE_FLAG);
        QMUICommonListItemView gFlagSwith = setSwith(Const.BAR_CODE_GRAM_UNIT);
        QMUICommonListItemView qrNumberFlagSwith = setSwith(Const.QRCODE_NUMBER_FLAG);
        QMUICommonListItemView multiPriceSign = setSwith(Const.BAR_CODE_MULTI_PRICE_SIGN);
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("条码设置")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get(Const.BAR_CODE_LENGTH), v -> {
                    String[] listItems = new String[]{
                            "13位",
                            "18位",
                            "19位",
                            "20位",
                    };
                    showSimpleBottomSheetList(
                            true, false, "条码位数",
                            listItems, true, false, Const.BAR_CODE_LENGTH, itemViewMap.get(Const.BAR_CODE_LENGTH));
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PREFIX), v -> {
                    startEdit(Const.BAR_CODE_PREFIX, "称重码前缀(计重)", Const.getSettingValue(Const.BAR_CODE_PREFIX), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PREFIX_PIECE), v -> {
                    startEdit(Const.BAR_CODE_PREFIX_PIECE, "称重码前缀(计件)", Const.getSettingValue(Const.BAR_CODE_PREFIX_PIECE), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PLU_LENGTH), v -> {
                    String[] listItems = new String[]{
                            "五位",
                            "六位",
                            "七位",
                    };
                    // popupsShow(v,listItems,Constants.TOTAL_PRICE_POINT, itemViewMap.get(Constants.TOTAL_PRICE_POINT));
                    showSimpleBottomSheetList(
                            true, false, "条码位数",
                            listItems, true, false, Const.BAR_CODE_PLU_LENGTH, itemViewMap.get(Const.BAR_CODE_PLU_LENGTH));
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_FORMAT), v -> {
                    if ("13位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                        showSimpleBottomSheetList(
                                true, false, "条码格式",
                                listItems13, true, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                    } else if ("18位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                        showSimpleBottomSheetList(
                                true, false, "条码格式",
                                listItems18, true, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                    } else if ("20位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                        showSimpleBottomSheetList(
                                true, false, "条码格式",
                                listItems20, true, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                    }else {
                        showSimpleBottomSheetList(
                                true, false, "条码格式",
                                listItems19, true, false, Const.BAR_CODE_FORMAT, itemViewMap.get(Const.BAR_CODE_FORMAT));
                    }
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_PIECT_FLAG), v -> {
                    String[] listItems = new String[]{
                            "个位开始",
                            "千位开始",
                            "十位开始",
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
                .addItemView(itemViewMap.get(Const.BAR_CODE_GRAM_UNIT), v -> {
                    gFlagSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_MULTI_PRICE_SIGN), v -> {
                    multiPriceSign.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get("DownLoad"), v -> {
                    //todo
                    aiTipDialog.showLoading("正在下载", mContext);
                    mPresenter.getUpdateBar(Const.getSettingValue(Const.KEY_BRANCH_ID));
                })
                .addItemView(itemViewMap.get(Const.TAG_DIRECTION), v -> {
                    String[] listItems = new String[]{
                            "正向打印",
                            "逆向打印"
                    };
                    showSimpleBottomSheetList(
                            true, false, "条码打印方向",
                            listItems, true, false, Const.TAG_DIRECTION, itemViewMap.get(Const.TAG_DIRECTION));
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_IS_CHECK), v -> {
                    isCheckSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.BAR_CODE_OR_QRCODE_FLAG), v -> {
                    codeFlagSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.QRCODE_NUMBER_FLAG), v -> {
                    qrNumberFlagSwith.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get(Const.SCALE_NO), v -> {
                    startEdit(Const.SCALE_NO, "秤号", Const.getSettingValue(Const.SCALE_NO), EditType.Edit_TYPE_INTEGER);
                })
                .addItemView(itemViewMap.get(Const.KEY_BRANCH_NAME), v -> {
                    startEdit(Const.KEY_BRANCH_NAME, "门店名称", Const.getSettingValue(Const.KEY_BRANCH_NAME), EditType.Edit_TYPE_TEXT);
                })

                .addItemView(itemViewMap.get(Const.BAR_ONECODE_PREFIX), v -> {
                    startEdit(Const.BAR_ONECODE_PREFIX, "多品一签前缀", Const.getSettingValue(Const.BAR_ONECODE_PREFIX), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.BAR_ONECODE_POSTFIX), v -> {
                    startEdit(Const.BAR_ONECODE_POSTFIX, "多品一签后缀", Const.getSettingValue(Const.BAR_ONECODE_POSTFIX), EditType.Edit_TYPE_TEXT);
                })
                .addItemView(itemViewMap.get(Const.BAR_ONECODE_CONNECTOR), v -> {
                    startEdit(Const.BAR_ONECODE_CONNECTOR, "多品一签连接符", Const.getSettingValue(Const.BAR_ONECODE_CONNECTOR), EditType.Edit_TYPE_TEXT);
                })
//                .addItemView(itemViewMap.get(Const.BAR_ONECODE_FLAG), v -> {
//                    isOneCode.getSwitch().performClick();
//                })
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);
        itemViewMap.get(Const.BAR_CODE_IS_CHECK).setDetailText("");
        itemViewMap.get(Const.QRCODE_NUMBER_FLAG).setDetailText("");
        itemViewMap.get(Const.BAR_CODE_OR_QRCODE_FLAG).setDetailText("");
        itemViewMap.get(Const.BAR_CODE_GRAM_UNIT).setDetailText("");
        itemViewMap.get(Const.BAR_CODE_MULTI_PRICE_SIGN).setDetailText("");
//        itemViewMap.get(Const.BAR_ONECODE_FLAG).setDetailText("");
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected View loadingStatusView() {
        return null;
    }

    @Override
    protected BarSettingPresenter createPresenter() {
        return new BarSettingPresenter();
    }


    @Override
    public void showFile(String msg) {
        aiTipDialog.dismiss();
        aiTipDialog.showFail(msg, groupListView);
    }

    @Override
    public void showSucces(String msg) {
        aiTipDialog.dismiss();
        aiTipDialog.showSuccess(msg, groupListView);
    }
}