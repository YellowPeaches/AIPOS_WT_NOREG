package com.wintec.lamp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.wintec.aiposui.view.control.NUIKeyView;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.aiposui.view.dialog.NUIKeyDialog;
import com.wintec.lamp.base.BaseActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.utils.updateapp.DownloadUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ShowInfo extends BaseActivity {


    @BindView(R.id.info_topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.qmui_info_groupList)
    QMUIGroupListView groupListView;

    private QMUIPopup mNormalPopup;
    View rootLayout;
    AiTipDialog aiTipDialog;

    private Map<String, QMUICommonListItemView> itemViewMap;

    private NUIKeyDialog passwordKeyboard;
    private NUIKeyDialog safeguardPasswordKeyboard;

    List<Long> clickNum = new ArrayList<>();

    @Override
    public int getView() {
        return R.layout.activity_show_info;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initView() {
        aiTipDialog = new AiTipDialog();
        passwordKeyboard = new NUIKeyDialog.Builder(mContext)
                .addKeyView("请输入新密码", NUIKeyView.KEY_TYPE_PASSWORD)
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
        safeguardPasswordKeyboard = new NUIKeyDialog.Builder(mContext)
                .addKeyView("请输入维护密码", NUIKeyView.KEY_TYPE_PASSWORD)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        checkSafeguardPassword(dialog, code);
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        dialog.dismiss();
                    }

                }).create();
        super.initView();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mTopBar.setTitle("详细信息");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemViewMap = new HashMap<>();
        int height = QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_list_item_height_higher);
        addSettingItem("SN", "设备SN", height, Const.SN);
        addSettingItem("BranchId", "门店编码", height, Const.getSettingValue(Const.KEY_BRANCH_ID));
        addSettingItem("ScaleInfo", "秤台信息", height, Const.getSettingValue(Const.KEY_SCALE));
        addSettingItem("APPvERSION", "应用版本", height, DownloadUtil.getAppVersionName(mContext));
        addSettingItem("PosCode", "POS编码", height, Const.getSettingValue(Const.KEY_POS_ID));
        addSettingItem("BACK_VERSION", "使用备份版本", height, Const.getSettingValue(Const.BACK_VERSION));
        addSettingItem("POS_IP", "POS IP", height, getIP(mContext));
        //设置switch
        int size = QMUIDisplayHelper.dp2px(mContext, 60);
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(mContext)
                .setLeftIconSize(size, size)
                .setMiddleSeparatorInset(0, 0);
        itemViewMap.forEach((item, value) -> {
            if (item.equals("SN")) {
                section.addItemView(value, v -> {
                    if (straightChick()) {
                        safeguardPasswordKeyboard.show();
                    }
                });

            } else {
                section.addItemView(value, null);
            }
        });
        section.setMiddleSeparatorInset(0, 0).addTo(groupListView);
    }

    private void checkSafeguardPassword(NUIKeyDialog dialog, String code) {
        String settingValue = Const.getSettingValue(Const.KEY_SAFEGUARD_PASSWORD);
        dialog.dismiss();
        if (code.equals(settingValue)) {
            passwordKeyboard.show();
        } else if (settingValue == null || "".equals(settingValue)) {
            Const.setSettingValue(Const.KEY_SAFEGUARD_PASSWORD, "86667889");
            if (code.equals(Const.getSettingValue(Const.KEY_SAFEGUARD_PASSWORD))) {
                passwordKeyboard.show();
            }
        } else {
            aiTipDialog.showFail("维护密码不正确，请联系管理员", groupListView);
        }
    }

    private void checkPassword(NUIKeyDialog dialog, String code) {
        dialog.dismiss();
        if (code.equals(Const.getSettingValue(Const.KEY_POS_PASSWORD))) {
            aiTipDialog.showFail("新密码和旧密码相同", groupListView);
        } else {
            Const.setSettingValue(Const.KEY_POS_PASSWORD, code);
            aiTipDialog.showSuccess("设置密码成功", groupListView);

        }
    }

    private Boolean straightChick() {
        if (clickNum.size() == 0) {
            clickNum.add(SystemClock.uptimeMillis());
            return false;
        } else if (clickNum.size() > 7) {
            clickNum.clear();
            Log.i("test", "initView: 点击了7次");
            return true;
        } else if (clickNum.get(clickNum.size() - 1) >= SystemClock.uptimeMillis() - 500) {
            clickNum.add(SystemClock.uptimeMillis());
            return false;
        } else {
            clickNum.clear();
            return false;
        }
    }

    private void addSettingItem(String key, String title, int height, String value) {

        itemViewMap.put(key, groupListView.createItemView(null, title, value,
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE, height));


    }

    @Override
    protected void initData() {
        super.initData();
    }

    public String getIP(Context context) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}