package com.wintec.lamp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.TimePickerView;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.popup.QMUIFullScreenPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.wintec.aiposui.view.control.NUIKeyView;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.aiposui.view.dialog.NUIKeyDialog;
import com.wintec.lamp.base.BaseMvpActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.contract.SettingContract;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.helper.AccDtoHelper;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.data.EditType;
import com.wintec.lamp.entity.EditEntity;
import com.wintec.lamp.presenter.SettingPresenter;
import com.wintec.lamp.utils.ToastUtils;
import com.wintec.lamp.utils.log.Logging;
import com.wintec.lamp.view.NUIBottomSheet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SettingActivity extends BaseMvpActivity<SettingPresenter> implements SettingContract.IView {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_groupList)
    QMUIGroupListView groupListView;

    private QMUIPopup mNormalPopup;

    private NUIKeyDialog numberKeyboard;

    private NUIKeyDialog clearKeyboard;

    AiTipDialog aiTipDialog;

    //???????????????
    private TimePickerView pvTime;

    private Map<String, QMUICommonListItemView> itemViewMap;

    private NUIBottomSheet nuiBottomSheet;

    // private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;


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

    @Override
    protected int contentResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //????????????
        numberKeyboard = new NUIKeyDialog.Builder(mContext)
                .addKeyView("???????????????", NUIKeyView.KEY_TYPE_PASSWORD)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        checkPassword(dialog, code);
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        dialog.dismiss();
                    }

                }).create();
        clearKeyboard = new NUIKeyDialog.Builder(mContext)
                .addKeyView("???????????????", NUIKeyView.KEY_TYPE_PASSWORD)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        checkPasswordAndClear(dialog, code);
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        dialog.dismiss();
                    }

                }).create();

        aiTipDialog = new AiTipDialog();
        nuiBottomSheet = new NUIBottomSheet(mContext);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("??????");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem("WeightSetting", "????????????", height, false);
        //   addSettingItem("UISetting", "????????????", height, false);
        addSettingItem("ButtonSetting", "???????????????", height, false);
        addSettingItem("BarCodeSetting", "????????????", height, false);
        addSettingItem("BackUpSetting", "??????????????????", height, false);
        addSettingItem("GetPluSetting", "???????????????", height, false);
        addSettingItem("DownloadSku", "??????????????????", height, true);
        addSettingItem("UpLog", "????????????", height, true);
        addSettingItem(Const.KEY_LABEL_TYPE, "??????????????????", height, true);
        //  addSettingItem(Const.KEY_SCALE, "????????????", height, true);
        addSettingItem("ClearPlu", "??????????????????", height, true);
        addSettingItem("ShowInfo", "????????????", height, true);
        addSettingItem("ToCalibrate", "??????????????????", height, false);
        addSettingItem("UpdateAPP", "????????????", height, true);
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("????????????")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get("WeightSetting"), v -> {
                    startActivity(WeightSettingActivity.class);
                })
//                .addItemView(itemViewMap.get("UISetting"), v -> {
//                    startActivity(UiSettingActivity.class);
//                })
                .addItemView(itemViewMap.get("BackUpSetting"), v -> {
                    startActivity(BackUpActivity.class);
                })
                .addItemView(itemViewMap.get("GetPluSetting"), v -> {
                    startActivity(GetPluSettingActivty.class);
                })
                .addItemView(itemViewMap.get("ButtonSetting"), v -> {
                    numberKeyboard.show();
                })
                .addItemView(itemViewMap.get("BarCodeSetting"), v -> {
                    startActivity(SimBarCodeSettingActivity.class);
                })
//                .addItemView(itemViewMap.get(Const.KEY_LABEL_TYPE), v -> {
//                    // ??????????????????
//                    showSimpleBottomSheetList(
//                            true, false, "??????????????????",
//                            new String[]{"40mm*30mm", "60mm*40mm"}, true, false, Const.KEY_LABEL_TYPE, itemViewMap.get(Const.KEY_LABEL_TYPE));
//
//                })
//                .addItemView(itemViewMap.get(Const.KEY_SCALE), v -> {
//                    // ????????????
//                    //  showSingleChoiceDialog(new String[]{"S100", "POS20", "?????????"},Const.KEY_SCALE);
////                    showSimpleBottomSheetList(
////                            true, false, "????????????(????????????????????????)",
////                            new String[]{"S100C 15.6???", "POS20", "?????????"}, true, false, Const.KEY_SCALE, itemViewMap.get(Const.KEY_SCALE));
//                })'
                .addItemView(itemViewMap.get("ToCalibrate"), v -> {
                    startActivity(CorpPicActivaty.class, 3);
                })
                .addItemView(itemViewMap.get("ClearPlu"), v -> {
                    clearKeyboard.show();
                })
                .addItemView(itemViewMap.get("UpLog"), v -> {
                    // ????????????
                    upLog();
                })
//                .addItemView(itemViewMap.get("UpdateAPP"), v -> {
//                    aiTipDialog.showSuccess("???????????????", groupListView);
//                })
                .addItemView(itemViewMap.get("ShowInfo"), v -> {
                    // ??????????????????
                    //showInfo(v);
                    startActivity(ShowInfo.class);
                })
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);

    }

    private void checkPasswordAndClear(NUIKeyDialog dialog, String code) {
        dialog.dismiss();
        if (code.equals(Const.getSettingValueWithDef(Const.KEY_POS_PASSWORD,"11111111"))) {
            QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(getActivity());
            builder.setGravityCenter(true)
                    .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                    .setTitle("???????????????????????????")
                    .setAddCancelBtn(false)
                    .setAllowDrag(true)
                    .setNeedRightMark(false)
                    .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                            dialog.dismiss();
                            if (position == 0) {
                                PluDtoDaoHelper.deleteAll();
                                AccDtoHelper.deleteAll();
                            }
                        }
                    });
            builder.addItem("??????");
            builder.addItem("??????");
            builder.build().show();
        } else {
            aiTipDialog.showFail("???????????????", groupListView);
        }
    }

    private void checkPassword(NUIKeyDialog dialog, String code) {

        dialog.dismiss();
        if (code.equals(Const.getSettingValueWithDef(Const.KEY_POS_PASSWORD,"11111111"))) {
            startActivity(ButtonSettingActivity.class);
        } else {
            aiTipDialog.showFail("???????????????", groupListView);
        }
    }

    private void showInfo(View v) {

        int padding = QMUIDisplayHelper.dp2px(getContext(), 20);

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundResource(R.drawable.text_while);
        TextView textView = new TextView(getContext());
        textView.setLineSpacing(QMUIDisplayHelper.dp2px(getContext(), 4), 1.0f);
        textView.setPadding(padding, padding, padding, padding);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.LEFT);
        textView.setLineSpacing(2, 2);
        textView.append("????????????:" + "\n");
        textView.append("????????????:" + "\n");
        textView.append("????????????:" + "\n");
        textView.append("????????????:" + "\n");
        textView.append("????????????:" + "\n");
        textView.append("????????????:");
        // textView.append(Const.getSettingValue(Const.KEY_BRANCH_ID)+"\n");

        TextView textViewValue = new TextView(getContext());
        textViewValue.setLineSpacing(QMUIDisplayHelper.dp2px(getContext(), 4), 1.0f);
        textViewValue.setPadding(0, padding, padding, padding);
        textViewValue.setTextSize(20);
        textViewValue.setTextColor(Color.BLACK);
        textViewValue.setGravity(Gravity.LEFT);
        textViewValue.setLineSpacing(2, 2);
        textViewValue.append(Const.SN + "\n");
        textViewValue.append(Const.getSettingValue(Const.KEY_BRANCH_ID) + "\n");
        linearLayout.addView(textView);
        linearLayout.addView(textViewValue);

        QMUIPopups.fullScreenPopup(getContext())
                .addView(linearLayout)
                .closeBtn(true)
                .skinManager(QMUISkinManager.defaultInstance(getContext()))
                .onBlankClick(new QMUIFullScreenPopup.OnBlankClickListener() {
                    @Override
                    public void onBlankClick(QMUIFullScreenPopup popup) {
                        popup.dismiss();
                    }
                })
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                    }
                })
                .show(v);

    }

    private Context getContext() {
        return mContext;
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
    protected SettingPresenter createPresenter() {
        return new SettingPresenter();
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

    //???????????????
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

    public void jumpToActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void showDownloadCommdities(List<PluDto> commdities) {
        PluDtoDaoHelper.deleteAll();
        if (commdities.size() > 0) {
            if (commdities.get(0).getPluNo() != null) {
                Const.setSettingValue(Const.KEY_ITEMCODE_SIZE, String.valueOf(commdities.get(0).getPluNo().length()));
            }
        }
        for (PluDto tmp : commdities) {
            PluDtoDaoHelper.insertCommdity(tmp);
        }
        aiTipDialog.dismiss();
        aiTipDialog.showSuccess("????????????", groupListView);
    }


    @Override
    public void showUpImgSuccess() {
        ToastUtils.showToast("????????????");
    }

    private void initTimePicker1() {//?????????????????????
        //??????????????????(?????????????????????????????????????????????1900-2100???????????????????????????)
        //????????????Calendar???????????????0-11???,?????????????????????Calendar???set?????????????????????,???????????????????????????0-11
        Date curDate = new Date(System.currentTimeMillis());//??????????????????
        SimpleDateFormat formatter_year = new SimpleDateFormat("yyyy ");
        String year_str = formatter_year.format(curDate);
        int year_int = (int) Double.parseDouble(year_str);


        SimpleDateFormat formatter_mouth = new SimpleDateFormat("MM ");
        String mouth_str = formatter_mouth.format(curDate);
        int mouth_int = (int) Double.parseDouble(mouth_str);

        SimpleDateFormat formatter_day = new SimpleDateFormat("dd ");
        String day_str = formatter_day.format(curDate);
        int day_int = (int) Double.parseDouble(day_str);


        Calendar selectedDate = Calendar.getInstance();//??????????????????
        Calendar startDate = Calendar.getInstance();
        startDate.set(2020, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(year_int, mouth_int - 1, day_int);

        //???????????????
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//??????????????????

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(date);
                File file = new File(Logging.ROOT_PATH + "//aipos_log//" + format + ".txt");
                if (file.exists()) {
                    mPresenter.upLog(file, file.getName(), Const.SN);
                } else {
                    aiTipDialog.showFail("?????????????????????", groupListView);
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false}) //?????????????????? ????????????????????????????????????????????????
                .setLabel("???", "???", "???", "", "", "")//?????????????????????????????????
                .isCenterLabel(false)
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.BLACK)//????????????????????????
                .setTextColorOut(Color.GRAY)//?????????????????????????????????
                .setSubmitColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setContentSize(30)
                .setLineSpacingMultiplier(1.5f)
                .setTextXOffset(-10, 0, 10, 0, 0, 0)//??????X???????????????[ -90 , 90??]
                .setRangDate(startDate, endDate)
                .setDate(endDate)
                .setSubCalSize(35)
//                .setBackgroundId(0x00FFFFFF) //????????????????????????
                .setDecorView(null)
                .build();
    }

    private void upLog() {
        initTimePicker1();
        pvTime.show();
    }

//    private void showSingleChoiceDialog(String[] items, String key) {
//        int index = 0;
//        String sendMode = Const.getSettingValue(key);
//        if (!(sendMode == null || "".equals(sendMode))) {
//            index = Integer.valueOf(sendMode);
//        }
//        new QMUIDialog.CheckableDialogBuilder(mContext)
//                .setCheckedIndex(index)
//                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
//                .addItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Const.setSettingValue(key, which + "");
//                        Toast.makeText(mContext, "????????? " + items[which], Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                })
//                .create(mCurrentDialogStyle).show();
//    }

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
                        Toast.makeText(mContext, "?????????" + itemList[position], Toast.LENGTH_SHORT).show();
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

    private Context getActivity() {
        return mContext;
    }

    /**
     * ??????????????????
     */
    void downloadItemData() {
        nuiBottomSheet.createQuitBottomSheet(new NUIBottomSheet.ClickListener() {
            @Override
            public void onItemClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                if (position == 0) {
                    aiTipDialog.showLoading("?????????", mContext);
                    mPresenter.getDownloadCommdities(Const.getSettingValue(Const.KEY_BRANCH_ID));
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        }, "?????????????????????????????????????????????").show();
    }

}