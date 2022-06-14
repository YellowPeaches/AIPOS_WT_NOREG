package com.wintec.lamp;


import butterknife.BindView;

import android.os.Bundle;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.aiposui.view.dialog.NUIKeyDialog;
import com.wintec.detection.WtAISDK;
import com.wintec.lamp.R;
import com.wintec.lamp.base.BaseMvpActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.bean.DiscernData;
import com.wintec.lamp.contract.BackUpContract;
import com.wintec.lamp.httpdownload.DownInfo;
import com.wintec.lamp.httpdownload.HttpDownManager;
import com.wintec.lamp.httpdownload.downloadlistener.HttpProgressOnNextListener;
import com.wintec.lamp.presenter.BackPresenter;
import com.wintec.lamp.presenter.BarSettingPresenter;
import com.wintec.lamp.utils.ApkUtils;
import com.wintec.lamp.utils.DownloadUtil;
import com.wintec.lamp.utils.StrUtils;
import com.wintec.lamp.view.DownLoadApkDialog;
import com.wintec.lamp.view.NUIBottomSheet;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BackUpActivity extends BaseMvpActivity<BackPresenter> implements BackUpContract.IView {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_groupList)
    QMUIGroupListView groupListView;


    AiTipDialog aiTipDialog;

    private Map<String, QMUICommonListItemView> itemViewMap;


    @Override
    protected int contentResId() {
        return R.layout.activity_back_up;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        aiTipDialog = new AiTipDialog();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("同步设置");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem(Const.LAN_SYNCHRONIZATION, "局域网学习同步", height, true);
        addSettingItem("BACK_UP_LOCAL", "备份到本地", height, true);
        addSettingItem("IMPORT_LOCAL", "本地导入", height, true);
        addSettingItem("EXPORT_LOCAL_CLOUD", "备份到云端", height, true);
        addSettingItem("IMPORT_LOCAL_CLOUD", "云端导入", height, true);

        QMUICommonListItemView LANSynchronization = setSwith(Const.LAN_SYNCHRONIZATION);
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.newSection(mContext)
                .setTitle("基本设置")
                .setLeftIconSize(size, size)
                .addItemView(itemViewMap.get(Const.LAN_SYNCHRONIZATION), v -> {
                    LANSynchronization.getSwitch().performClick();
                })
                .addItemView(itemViewMap.get("BACK_UP_LOCAL"), v -> {
                    //todo
                    mPresenter.exportData();
                })
                .addItemView(itemViewMap.get("IMPORT_LOCAL"), v -> {
                    mPresenter.importData();
                })
                .addItemView(itemViewMap.get("EXPORT_LOCAL_CLOUD"), v -> {
                    mPresenter.exportDataCloud();
                })
                .addItemView(itemViewMap.get("IMPORT_LOCAL_CLOUD"), v -> {
                    mPresenter.importDataByCloud();
                })
                .setMiddleSeparatorInset(0, 0)
                .addTo(groupListView);
        itemViewMap.get(Const.LAN_SYNCHRONIZATION).setDetailText("");

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
    protected BackPresenter createPresenter() {
        return new BackPresenter();
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

    @Override
    public void showFail(String msg) {
        aiTipDialog.showFail(msg, groupListView);
    }

    @Override
    public void showSuccess(String msg) {
        aiTipDialog.showSuccess(msg, groupListView);
    }

    @Override
    public void downLoadData(DiscernData discernData) {
        String saveDir = "/storage/emulated/0/database/";
        DownloadUtil.getInstance().download(discernData.getDataUrl(), saveDir, new DownloadUtil.OnDownloadListener() {

            @Override
            public void onDownloadSuccess(String path) {
                mPresenter.importData();
                Const.setSettingValue(Const.BACK_VERSION, discernData.getVersion() + "");
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                aiTipDialog.showFail("下载失败", groupListView);
            }


        });

    }

    @Override
    public void loading() {
        aiTipDialog.showLoading("正在下载", mContext);
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
                    WtAISDK.api_startServer();
                    Toast.makeText(getApplicationContext(), "本地同步打开",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Const.setSettingValue(key, "0");
                    WtAISDK.api_stopServer();
                    Toast.makeText(getApplicationContext(), "本地同步关闭",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        return itemWithSwitch;
    }


}