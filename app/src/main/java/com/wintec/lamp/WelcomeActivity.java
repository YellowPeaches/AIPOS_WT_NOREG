package com.wintec.lamp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.wang.avi.BuildConfig;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.detection.WtAISDK;
import com.wintec.detection.http.OnRegSDKListener;
import com.wintec.lamp.base.BaseMvpActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.contract.WelcomeContract;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.dao.helper.TagMiddleHelper;
import com.wintec.lamp.httpdownload.DownInfo;
import com.wintec.lamp.httpdownload.HttpDownManager;
import com.wintec.lamp.httpdownload.downloadlistener.HttpProgressOnNextListener;
import com.wintec.lamp.network.schedulers.BaseSchedulerProvider;
import com.wintec.lamp.network.schedulers.SchedulerProvider;
import com.wintec.lamp.presenter.WelcomePresenter;
import com.wintec.lamp.utils.ApkUtils;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.lamp.utils.GeneratePassword;
import com.wintec.lamp.utils.NetWorkUtil;
import com.wintec.lamp.utils.StrUtils;
import com.wintec.lamp.utils.UserManager;
import com.wintec.lamp.view.DownLoadApkDialog;
import com.wintec.lamp.view.UploadVersionDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.RequestBody;

public class WelcomeActivity extends BaseMvpActivity<WelcomePresenter> implements WelcomeContract.IView {

    @BindView(R.id.res_layout)
    ConstraintLayout linearLayout;
    @BindView(R.id.branch_id)
    EditText branchEditext;
    @BindView(R.id.pos_edit)
    EditText posEdit;

    @BindView(R.id.mac_text)
    TextView macText;

    @BindView(R.id.account_company)
    EditText accountcompany;

    @BindView(R.id.get_mac_text)
    TextView getMacText;

    @BindView(R.id.tv_sign)
    TextView tvSign;


    @BindView(R.id.register_layout)
    LinearLayout registerLayout;


    View rootLayout;
    AiTipDialog aiTipDialog;

    private BaseSchedulerProvider schedulerProvider;

    private UploadVersionDialog dialog;
    private DownLoadApkDialog downLoadApkDialog;
    private String mVersionCode;
    private String mCurrentVersionCode;
    private boolean isChenckVersion;//????????????????????????????????????
    private DownInfo downInfo;
    private String brachId;

    //  ??????????????????
    private String mIsCancel;//???????????????????????????
    private boolean isRegister = false;//???????????????????????????

    private String url = null;
    private String ApkUrl;

    @Override
    protected int contentResId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        // ????????????????????????
        firstOpenApp();
        schedulerProvider = SchedulerProvider.getInstance();

        if (NetWorkUtil.isNetworkAvailable(this) && Const.getSettingValue(Const.ERROR_LOG_FLAG).equals("1")) {
            Toast.makeText(this, "??????????????????", Toast.LENGTH_LONG).show();
            File file = new File(Const.getSettingValue(Const.ERROR_LOG_PATH));
            mPresenter.upLogTxt(file, file.getName(), Const.SN);
        }
        if (Const.getSettingValue(Const.PREVIEW_FLAG).equals("1") && NetWorkUtil.isNetworkAvailable(this)) {
//            mPresenter.upplus();
            new WelcomePresenter().upPLUDto();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Const.getSettingValue(Const.PREVIEW_FLAG).equals("1") && NetWorkUtil.isNetworkAvailable(this)) {
            mPresenter.getImgUrl();
        }

//        jumpToMainActivity();
    }

    @Override
    protected void onRestart() {
        if (NetWorkUtil.isNetworkAvailable(this)) {
            //????????????
//            checkVersionUpload();
//            mPresenter.getAppDownInfo();
        } else {
            showCheckVersion(null);
            // jumpToMainActivity();
        }
        super.onRestart();
    }

    private void checkVersionUpload() {
        mIsCancel = Const.getSettingValue(Const.ISCANCELLOAD);
        if (TextUtils.isEmpty(mIsCancel) || mIsCancel == null) {
            mIsCancel = "no";
        }
        mPresenter.checkVersion();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void loadData() {
        showCheckVersion(null);
//        if (NetWorkUtil.isNetworkAvailable(this)) {
//            //????????????
//            checkVersionUpload();
//            mPresenter.getAppDownInfo();
//        } else {
//            showCheckVersion(null);
//        }
    }

    /**
     * ??????????????????
     */
    private void updataVersion(String describe) {
        dialog = new UploadVersionDialog(mContext);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleAndMsg("??????????????????????????????????????????", describe);
        dialog.setOnClickListener(new UploadVersionDialog.OnClickListener() {
            @Override
            public void onClick(int type) {
                switch (type) {
                    case 1://??????
                        if (isRegister) {//????????????????????????
                            dialog.dismiss();
                            return;
                        }
                        jumpToMainActivity();
                        break;
                    case 2:
                        dialog.dismiss();
                        downloadApk();
//                        Intent intent = new Intent(mContext.getApplicationContext(), DownloadService.class);
//                        intent.putExtra(Const.APK_DOWNLOAD_URL, ApkUrl);
//                        mContext.startService(intent);
//                        jumpToMainActivity();
                        break;
                }
            }
        });
    }

    /**
     * ????????????apk??????
     */
    private void downloadApk() {
        downLoadApkDialog = new DownLoadApkDialog(mContext);
        downLoadApkDialog.show();
        downLoadApkDialog.setTitleAndMsg("????????????");
        downLoadApkDialog.setCanceledOnTouchOutside(false);

        if (url != null) {
            downLoadApkDialog.setBtnText("??????");
            downLoadApkDialog.setTag("true");
        }

        HttpDownManager.getInstance().startDown(downInfo, new HttpProgressOnNextListener<DownInfo>() {

            @Override
            public void onNext(DownInfo downInfo) {
                downLoadApkDialog.dismiss();
                ApkUtils.installAPk(mContext, new File(downInfo.getSavePath()));
            }

            @Override
            public void updateProgress(long readLength, long countLength) {
                downLoadApkDialog.setProgressBar(readLength, countLength);
            }

            @Override
            public void onError(Throwable e) {
            }
        });


        downLoadApkDialog.setOnClickListener(new DownLoadApkDialog.OnClickListener() {
            @Override
            public void onClick() {
                String tag = downLoadApkDialog.getTag();
                if (tag.equals("true")) {
//                    jumpToMainActivity();
//                    downLoadApkDialog.dismiss();
                    downLoadApkDialog.setBtnText("??????");
                    downLoadApkDialog.setTag("false");
                    HttpDownManager.getInstance().pause(downInfo);
                    String strEntity = StrUtils.getJsonStringByEntity(downInfo);
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
                    mPresenter.getAppState(body);
                } else {
                    downLoadApkDialog.setBtnText("??????");
                    downLoadApkDialog.setTag("true");
                    HttpDownManager.getInstance().continueDownload(downInfo);
                }
//                    jumpToMainActivity();
                Const.setSettingValue(Const.ISCANCELLOAD, "is");
            }
        });
    }

    @Override
    protected View loadingStatusView() {
        return null;
    }

    @Override
    protected WelcomePresenter createPresenter() {
        return new WelcomePresenter();
    }


    //?????????????????????
    private void updateAndRegister(boolean isCheck) {
        brachId = Const.getSettingValue(Const.KEY_BRANCH_ID);
        String isBind = Const.getSettingValue(Const.KEY_IS_BIND);
        if ("".equals(brachId) || brachId == null || "".equals(isBind) || isBind == null) {
            isRegister = true;
            rootLayout = new View(mContext);
            aiTipDialog = new AiTipDialog();
            registerLayout.setVisibility(View.VISIBLE);
            tvSign.setOnClickListener(r -> {
//                registerPos();
                jumpToMainActivity();
            });
        } else {
            if (isCheck) {
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if ("1".equals(Const.getSettingValue(Const.LAN_SYNCHRONIZATION))) {
                        WtAISDK.api_startServer();
                    }
                    jumpToMainActivity();
                }
            }).start();
        }
    }

    /**
     * ????????????
     */
    private void quitAPP() {
        finish();
    }

    /**
     * ??????pos
     */
    private void registerPos() {

        String pos = posEdit.getText().toString();
        String companyCode = accountcompany.getText().toString();
        String branchCode = branchEditext.getText().toString();
        if (TextUtils.isEmpty(pos)) {
            aiTipDialog.showFail("pos??????????????????", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(companyCode)) {
            aiTipDialog.showFail("??????????????????", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(branchCode)) {
            aiTipDialog.showFail("????????????????????????", registerLayout);
            return;
        }
        RequestBody requestBody = loadParams();
        mPresenter.posRegister(requestBody);

    }

    /**
     * ????????????
     */
    private RequestBody loadParams() {
        String posId = posEdit.getText().toString();
        String branchCode = branchEditext.getText().toString();

        if (branchCode == null || "".equals(branchCode)) {
            CommUtils.showMessage(mContext, "????????????????????????");
            return null;
        }

        // aiTipDialog.showLoading("????????????", mContext);

        //????????????
        HashMap<String, String> params = new HashMap<String, String>();
        String safeguardPassword = GeneratePassword.generatePassword(8);
        Const.setSettingValue(Const.KEY_SAFEGUARD_PASSWORD, safeguardPassword);
        params.put("branchId", branchCode);
        params.put("passWord", safeguardPassword);
        params.put("posCode", Const.SN);
        params.put("state", "1");
        params.put("posId", posId);//pos??????
        params.put("versionCode", Const.getSettingValue(Const.BACK_VERSION));
        Gson gson = new Gson();
        String strEntity = gson.toJson(params);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        return body;
    }

    /**
     * ???????????????????????????????????????
     */
    public void firstOpenApp() {
        SharedPreferences setting = getSharedPreferences("First.ini", 0);
        boolean isfirst = setting.getBoolean("FIRST", true);
        if (isfirst) {// ?????????????????????????????????
            setting.edit().putBoolean("FIRST", false).commit();
            firstSetting();
//            new BarSettingPresenter().getUpdatePriceTar(branchId, Const.SN);
        }
    }

    /**
     * ????????????
     */
    private void firstSetting() {
        Configuration mConfiguration = this.getResources().getConfiguration(); //???????????????????????????
        if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Const.setSettingValue(Const.KEY_SCALE, "S100C 15.6???");
        } else {
            Const.setSettingValue(Const.KEY_SCALE, "POS20");
        }
        Const.setSettingValue(Const.KEY_MODE, "????????????");
        Const.setSettingValue(Const.WEIGHT_UNIT, "kg");
        Const.setSettingValue(Const.TOTAL_PRICE_POINT, "3");
        Const.setSettingValue(Const.UNIT_PRICE_POINT, "2");
        Const.setSettingValue(Const.WEIGHT_POINT, "3");
        Const.setSettingValue(Const.TOTAL_PRICE_MODE, "?????????(18.16)");

        Const.setSettingValue(Const.RESULT_DISPLAY, "0");
        //Const.setSettingValue(Const.KEY_SEND_SCALE,"1");
        Const.setSettingValue(Const.KEY_GET_DATA_MODE, "???????????????");
        Const.setSettingValue(Const.KEY_GET_DATA_DB, "oracle");
        Const.setSettingValue(Const.KEY_GET_DATA_ADDITIONAL_SQL, "SELECT * FROM dbo.v_sk_extratext");
        Const.setSettingValue(Const.KEY_GET_DATA_SQL, "SELECT * FROM dbo.v_sk_item");
        Const.setSettingValue(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL, "INSERT INTO TESTONLINE.CLERK_LOG (CODE, SCALE_CODE, PLU_NUMBER,COMMODITY_NAME,EDITDATE,TIME,STATUS,SCALE_IP,OLD_UNIT_PRICE,NEW_UNIT_PRICE,PRINTED_EAN_DATA) VALUES( #{CODE}, #{SCALE_CODE},  #{PLU_NUMBER},#{COMMODITY_NAME},#{EDITDATE},#{TIME},#{STATUS},#{SCALE_IP},#{OLD_UNIT_PRICE},#{NEW_UNIT_PRICE},#{PRINTED_EAN_DATA})");

        //??????????????????
        Const.setSettingValue(Const.DELAY_TIME, "1800");
        Const.setSettingValue(Const.KEY_GET_DATA_IP, "118.31.114.118");
        Const.setSettingValue(Const.KEY_GET_DATA_PORT, "1433");
        Const.setSettingValue(Const.KEY_GET_DATA_DB_NAME, "get_online");
        Const.setSettingValue(Const.KEY_GET_DATA_USER, "sa");
        Const.setSettingValue(Const.KEY_GET_DATA_PWD, "F%OAft0fnJ");


        Const.setSettingValue(Const.KEY_SCALE_PORT, "3001");
        Const.setSettingValue(Const.KEY_SEND_UNIT, "kg");

        Const.setSettingValue(Const.RESULT_DISPLAY_TIME, "3000");
        Const.setSettingValue(Const.KEY_LABEL_TYPE, "40mm*30mm");
        Const.setSettingValue(Const.KEY_REDETECT_AND_PRINT, "????????????");
        //???????????????
        Const.setSettingValue(Const.KEY_ODD_EVEN_CHECK, "?????????");
        Const.setSettingValue(Const.BAR_CODE_PREFIX, "28");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_PIECE, "28");
        Const.setSettingValue(Const.BAR_CODE_FORMAT, "??????-PLU-??????-??????");
        Const.setSettingValue(Const.BAR_CODE_PIECT_FLAG, "????????????");
        Const.setSettingValue(Const.BAR_CODE_MULTI_PRICE_SIGN, "0");
        Const.setSettingValue(Const.ITEM_NO_REPLACE_PLU, "0");

        Const.setSettingValue(Const.KEY_ODD_EVEN_CHECK, "?????????");
        Const.setSettingValue(Const.BAR_CODE_LENGTH, "18???");
        Const.setSettingValue(Const.BAR_CODE_IS_CHECK, "1");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_COORDINATE, "1");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_LENGTH, "??????");
        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
        Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "??????");
        Const.setSettingValue(Const.BAR_CODE_TOTAL_COORDINATE, "8");
        Const.setSettingValue(Const.BAR_CODE_TOTAL_LENGTH, "??????");
        Const.setSettingValue(Const.BAR_CODE_WEIGHT_COORDINATE, "13");
        Const.setSettingValue(Const.BAR_CODE_WEIGHT_LENGTH, "??????");
        Const.setSettingValue(Const.BAR_CODE_PRICE_COORDINATE, "");
        Const.setSettingValue(Const.BAR_CODE_PRICE_LENGTH, "??????");

        Const.setSettingValue(Const.DELECT_WEIGHT, "30");

        Const.setSettingValue(Const.KEY_UPPRICE_FLAG, "0");
        Const.setSettingValue(Const.KEY_DISCOUNT_FLAG, "0");
        Const.setSettingValue(Const.KEY_TEMPPRICE_FLAG, "0");
        Const.setSettingValue(Const.KEY_RESTUDY_FLAG, "0");
        Const.setSettingValue(Const.ONE_TEMPPRICE_FLAG, "0");
        Const.setSettingValue(Const.ONE_DISCOUNT_FLAG, "0");

        Const.setSettingValue(Const.KEY_GOODS_COUNT, "5");


        Const.setSettingValue(Const.SEARCH_LENGHT, "0");

        // Const.setSettingValue(Const.BAR_ONECODE_FLAG,"0");
        // ?????????????????????
        //Const.setSettingValue(Const.ROTATION_SETTING,"32");
        Const.setSettingValue(Const.PRINT_SETTING, "???????????????");
        //  ????????????
        Const.setSettingValue(Const.VOIDCE_BROADCAST_FLAG, "0");

        Const.setSettingValue(Const.TAG_DIRECTION, "????????????");

        //?????????????????????
        Const.setSettingValue(Const.DETECT_THRESHOLD, "0.65");
        //??????????????????
        if (Build.VERSION.SDK_INT < 30)
            Const.setSettingValue(Const.GET_WEIGHT_PORT, "/dev/ttySAC1");
        else
            Const.setSettingValue(Const.GET_WEIGHT_PORT, "/dev/ttySAC4");

        Const.setSettingValue(Const.ERROR_LOG_FLAG, "0");

        setDefaultTag();

    }

    public void jumpToMainActivity() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (WtAISDK.api_getCameraSetting()) {
            Intent intent = new Intent(this, ScaleActivityUI.class);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, CorpPicActivaty.class));
        }
        finish();

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                //        finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void showCheckUpDate(File file) {
        Log.i("test", Environment.getExternalStorageDirectory() + File.separator + BuildConfig.APPLICATION_ID + File.separator + "AIPOS20.APK");
    }

    @Override
    public void showDownloading(int progress) {
        Log.i("test", "progress:" + progress);
    }

    @Override
    public void showDownloadFailed() {
    }


    @Override
    public void showRegister(registerBean registerBean) {
        if (registerBean != null) {
            showMsg("????????????");
            UserManager.getInstance().editPublicKey(registerBean.getKey());
            String branchCode = branchEditext.getText().toString();
            String password = "11111111";

            Const.setSettingValue(Const.KEY_BRANCH_ID, branchCode);
            // Const.setSettingValue(Const.KEY_BRANCH_GROUP_ID, registerBean.getCode());   code ?????????????????????
            Const.setSettingValue(Const.KEY_TER_CODE, registerBean.getCode());
            Const.setSettingValue(Const.KEY_POS_PASSWORD, password);
            //?????????????????????
//            jumpToMainActivity();
            if ("500".equals(registerBean.getHttpCode())) {
                aiTipDialog.showSuccess(registerBean.getMsg(), registerLayout);
                return;
            }
            if (registerBean.getPosKey() == null || "".equals(registerBean.getPosKey())) {
                aiTipDialog.showSuccess("???????????????", registerLayout);
                return;
            } else {
                Const.setSettingValue(Const.KEY_SN_CODE, registerBean.getPosKey());
            }
            ymInit();
        }
    }

    @Override
    public void showCheckVersion(VersionBean bean) {
        if (bean != null) {
            ApkUrl = bean.getUrl();
            downInfo = new DownInfo(ApkUrl);
            String weixinApkName = ApkUrl.substring(ApkUrl.lastIndexOf("/") + 1, ApkUrl.length());

            File weixinApkFile = new File(getExternalCacheDir(), weixinApkName);
            downInfo.setSavePath(weixinApkFile.getAbsolutePath());
            downInfo.setState("START");
            String downInfoS = new Gson().toJson(downInfo);
            UserManager.getInstance().editVersionData(downInfoS);
            Const.setSettingValue(Const.APKURL, ApkUrl);

            String versionCode = bean.getAppVersion();
            String urlApk = bean.getUrl();
            String currentVersionCode = StrUtils.getVersion(mContext);
            String describe = bean.getIntroduce();
            if (!TextUtils.isEmpty(versionCode) && versionCode != null && !TextUtils.isEmpty(currentVersionCode) && currentVersionCode != null) {
                mVersionCode = versionCode.substring(1, versionCode.length());
                mCurrentVersionCode = currentVersionCode.substring(1, versionCode.length());
            }
            if (!TextUtils.isEmpty(mVersionCode) && mVersionCode != null && !TextUtils.isEmpty(mCurrentVersionCode) && mCurrentVersionCode != null) {
                int i = ApkUtils.compareVersion(mCurrentVersionCode, mVersionCode);
                if (i == -1) {//????????????
                    isChenckVersion = true;
                    updataVersion(describe);
                } else {
                    isChenckVersion = false;
                }
            }

        }
        //?????????????????????
        updateAndRegister(isChenckVersion);
    }

    @Override
    public void showAppState() {
    }

    @Override
    public void showAppDownInfo(DownInfo mDownInfo) {
        if (mDownInfo != null) {
            url = mDownInfo.getUrl();
            if (url != null) {
                this.downInfo = mDownInfo;
            }
        }
    }


    public void ymInit() {
        String pos = posEdit.getText().toString();
        String companyCode = accountcompany.getText().toString();
        String branchCode = branchEditext.getText().toString();
        if (TextUtils.isEmpty(pos)) {
            aiTipDialog.showFail("pos??????????????????", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(companyCode)) {
            aiTipDialog.showFail("??????????????????", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(branchCode)) {
            aiTipDialog.showFail("????????????????????????", registerLayout);
            return;
        }

        String snCode = Const.getSettingValue(Const.KEY_SN_CODE);
        if (Const.getSettingValue(Const.KEY_IS_BIND).equals("1")) {
            XLog.i("?????????????????????");
            return;
        }
        WtAISDK.api_regSDK(branchCode, pos, companyCode, snCode, new OnRegSDKListener() {
            @Override
            public void regFail(int i, String s) {
                aiTipDialog.showLoading(s + i, mContext);
            }

            @Override
            public void regLoading() {
                aiTipDialog.showLoading("?????????????????????????????????", mContext);
            }

            @Override
            public void regSuccess() {
                Const.setSettingValue(Const.KEY_ACCOUNT, companyCode);
                Const.setSettingValue(Const.KEY_POS_ID, pos);
                Const.setSettingValue(Const.KEY_IS_BIND, "1");
                jumpToMainActivity();
            }
        });
    }

    private void setDefaultTag() {
        List<TagMiddle> tagData = new ArrayList<>();
        TagMiddle tagMiddle = new TagMiddle();

        tagMiddle.set_id(4L);
        tagMiddle.setAbscissa(4);
        tagMiddle.setBreadth(23);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setDivId("drag19");
        tagMiddle.setFontSize(23);
        tagMiddle.set_id(1575L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(32);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(24);
        tagMiddle.setOverstriking(0);
        tagMiddle.setCodeSystem(0);
        tagMiddle.setTagName("??????");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagMiddle.setUnit("???/kg");
        tagData.add(tagMiddle);
        tagMiddle = new TagMiddle();

        tagMiddle.set_id(2L);
        tagMiddle.setAbscissa(19);
        tagMiddle.setBreadth(80);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setCodeSystem(0);
        tagMiddle.setDivId("drag21");
        tagMiddle.setFontSize(14);
        tagMiddle.set_id(1577L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(280);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(68);
        tagMiddle.setOverstriking(0);
        tagMiddle.setTagName("????????????");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagData.add(tagMiddle);
        tagMiddle = new TagMiddle();

        tagMiddle.set_id(3L);
        tagMiddle.setAbscissa(4);
        tagMiddle.setBreadth(23);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setCodeSystem(0);
        tagMiddle.setDateFormat("yy/MM/dd");
        tagMiddle.setDivId("drag23");
        tagMiddle.setFontSize(18);
        tagMiddle.set_id(1579L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(60);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(53);
        tagMiddle.setOverstriking(0);
        tagMiddle.setTagName("????????????");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagData.add(tagMiddle);
        tagMiddle = new TagMiddle();


        tagMiddle.set_id(1L);
        tagMiddle.setAbscissa(93);
        tagMiddle.setBreadth(23);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setCodeSystem(1);
        tagMiddle.setDivId("drag20");
        tagMiddle.setFontSize(37);
        tagMiddle.set_id(1576L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(32);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(36);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("??????");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnit("???");
        tagMiddle.setUnderline(0);
        tagData.add(tagMiddle);
        tagMiddle = new TagMiddle();

        tagMiddle.set_id(8L);
        tagMiddle.setAbscissa(8);
        tagMiddle.setBreadth(23);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setCodeSystem(1);
        tagMiddle.setDivId("drag1");
        tagMiddle.setFontSize(34);
        tagMiddle.set_id(1580L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(60);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(4);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("????????????");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagData.add(tagMiddle);
        tagMiddle = new TagMiddle();

        tagMiddle.set_id(11L);
        tagMiddle.setAbscissa(78);
        tagMiddle.setBreadth(23);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setCodeSystem(2);
        tagMiddle.setDivId("drag22");
        tagMiddle.setFontSize(24);
        tagMiddle.set_id(1582L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(18);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(48);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("???");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagData.add(tagMiddle);
        tagMiddle = new TagMiddle();

        tagMiddle.set_id(14L);
        tagMiddle.setAbscissa(3);
        tagMiddle.setBreadth(34);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setCodeSystem(0);
        tagMiddle.setDivId("drag4");
        tagMiddle.setFontSize(23);
        tagMiddle.set_id(1584L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(50);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(37);
        tagMiddle.setOverstriking(0);
        tagMiddle.setTagName("??????");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagMiddle.setUnit("kg");
        tagData.add(tagMiddle);
        tagMiddle = new TagMiddle();

        tagMiddle.set_id(41L);
        tagMiddle.setAbscissa(78);
        tagMiddle.setBreadth(37);
        tagMiddle.setBreadths(30);
        tagMiddle.setBz2("0");
        tagMiddle.setCodeSystem(2);
        tagMiddle.setDivId("drag17");
        tagMiddle.setFontSize(25);
        tagMiddle.set_id(1585L);
        tagMiddle.setItalic(0);
        tagMiddle.setLength(29);
        tagMiddle.setLengths(40);
        tagMiddle.setName("????????????");
        tagMiddle.setOrdinate(33);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("???");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagData.add(tagMiddle);

        float offsetX = 2.01f;
        float offsetY = 2.01f;
        float tarOffsetX = 0.688f;
        float textOffset = 0.78f;
        tagData.forEach(item -> {
            int leftOffset = 0;
            if (item.getFontSize() != null) {
                item.setFontSize((int) (item.getFontSize() * textOffset));
            }
            item.setAbscissa(new Float(item.getAbscissa() * offsetX).intValue());
            item.setOrdinate(new Float(item.getOrdinate() * offsetY).intValue());
            if (item.getLength() != null && item.getBreadth() != null) {
                item.setLength(new Float(item.getLength() * tarOffsetX).intValue());
                item.setBreadth(new Float(item.getBreadth() * tarOffsetX).intValue());
            }
        });
        try {
            TagMiddleHelper.deleteAll();
            TagMiddleHelper.insertList(tagData);
        } catch (Exception e) {
            XLog.e(e);
        }
    }

}