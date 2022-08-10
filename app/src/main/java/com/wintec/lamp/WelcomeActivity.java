package com.wintec.lamp;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.wang.avi.BuildConfig;
import com.wintec.aiposui.view.AiPosLayout;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.detection.bean.CameraSetting;
import com.wintec.detection.http.OnRegSDKListener;
import com.wintec.lamp.base.BaseMvpActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.contract.WelcomeContract;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.dao.helper.CommdityHelper;
import com.wintec.lamp.dao.helper.TagMiddleHelper;
import com.wintec.lamp.httpdownload.DownInfo;
import com.wintec.lamp.httpdownload.HttpDownManager;
import com.wintec.lamp.httpdownload.downloadlistener.HttpProgressOnNextListener;
import com.wintec.lamp.network.NetWorkManager;
import com.wintec.lamp.network.response.ResponseTransformer;
import com.wintec.lamp.network.schedulers.BaseSchedulerProvider;
import com.wintec.lamp.network.schedulers.SchedulerProvider;
import com.wintec.lamp.presenter.BarSettingPresenter;
import com.wintec.lamp.presenter.WelcomePresenter;
import com.wintec.lamp.service.WintecService;
import com.wintec.lamp.utils.ApkUtils;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.lamp.utils.GeneratePassword;
import com.wintec.lamp.utils.NetWorkUtil;
import com.wintec.lamp.utils.StrUtils;
import com.wintec.lamp.utils.ThreadPoolManagerUtils;
import com.wintec.lamp.utils.UserManager;
import com.wintec.lamp.view.DownLoadApkDialog;
import com.wintec.lamp.view.UploadVersionDialog;
import com.wintec.detection.WtAISDK;
import com.wintec.detection.http.OnUnRegSDKListener;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
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
    //   private List<Commdity> commdityListByNetFlag;

    private UploadVersionDialog dialog;
    private DownLoadApkDialog downLoadApkDialog;
    private String mVersionCode;
    private String mCurrentVersionCode;
    private boolean isChenckVersion;//检查当前版本是否是最新版
    private DownInfo downInfo;
    private String brachId;

    //  判断是否停止
    private String mIsCancel;//判断是否暂停了下载
    private boolean isRegister = false;//判断是否是注册界面

    private String url = null;
    private String ApkUrl;

    @Override
    protected int contentResId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        schedulerProvider = SchedulerProvider.getInstance();
        //权限
        requestPermissions();

        if (Const.getSettingValue(Const.PREVIEW_FLAG).equals("1") && NetWorkUtil.isNetworkAvailable(this)) {
//            mPresenter.upplus();
            new WelcomePresenter().upPLUDto();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Const.getSettingValue(Const.PREVIEW_FLAG).equals("1") && NetWorkUtil.isNetworkAvailable(this)) {
            mPresenter.getImgUrl();
        }
    }

    @Override
    protected void onRestart() {
        if (NetWorkUtil.isNetworkAvailable(this)) {
            //批量上传
            checkVersionUpload();
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
//            //批量上传
//            checkVersionUpload();
//            mPresenter.getAppDownInfo();
//        } else {
//            showCheckVersion(null);
//        }
    }

    /**
     * 检查版本更新
     */
    private void updataVersion(String describe) {
        dialog = new UploadVersionDialog(mContext);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleAndMsg("检测到新版本，是否立即升级？", describe);
        dialog.setOnClickListener(new UploadVersionDialog.OnClickListener() {
            @Override
            public void onClick(int type) {
                switch (type) {
                    case 1://取消
                        if (isRegister) {//如果是在注册界面
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
     * 开启下载apk功能
     */
    private void downloadApk() {
        downLoadApkDialog = new DownLoadApkDialog(mContext);
        downLoadApkDialog.show();
        downLoadApkDialog.setTitleAndMsg("正在下载");
        downLoadApkDialog.setCanceledOnTouchOutside(false);

        if (url != null) {
            downLoadApkDialog.setBtnText("暂停");
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
                    downLoadApkDialog.setBtnText("继续");
                    downLoadApkDialog.setTag("false");
                    HttpDownManager.getInstance().pause(downInfo);
                    String strEntity = StrUtils.getJsonStringByEntity(downInfo);
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
                    mPresenter.getAppState(body);
                } else {
                    downLoadApkDialog.setBtnText("暂停");
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


    //注册和更新操作
    private void updateAndRegister(boolean isCheck) {
        brachId = Const.getSettingValue(Const.KEY_BRANCH_ID);
        String isBind = Const.getSettingValue(Const.KEY_IS_BIND);
        if ("".equals(brachId) || brachId == null || "".equals(isBind) || isBind == null) {
            isRegister = true;
            rootLayout = new View(mContext);
            aiTipDialog = new AiTipDialog();
            registerLayout.setVisibility(View.VISIBLE);
            tvSign.setOnClickListener(r -> {
                registerPos();
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
     * 退出应用
     */
    private void quitAPP() {
        finish();
    }

    /**
     * 注册pos
     */
    private void registerPos() {

        String pos = posEdit.getText().toString();
        String companyCode = accountcompany.getText().toString();
        String branchCode = branchEditext.getText().toString();
        if (TextUtils.isEmpty(pos)) {
            aiTipDialog.showFail("pos编码不能为空", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(companyCode)) {
            aiTipDialog.showFail("组号不能为空", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(branchCode)) {
            aiTipDialog.showFail("门店编码不能为空", registerLayout);
            return;
        }
        RequestBody requestBody = loadParams();
        mPresenter.posRegister(requestBody);

    }

    /**
     * 整理参数
     */
    private RequestBody loadParams() {
        String posId = posEdit.getText().toString();
        String branchCode = branchEditext.getText().toString();

        if (branchCode == null || "".equals(branchCode)) {
            CommUtils.showMessage(mContext, "门店编码不能为空");
            return null;
        }

        // aiTipDialog.showLoading("正在注册", mContext);

        //整理参数
        HashMap<String, String> params = new HashMap<String, String>();
        String safeguardPassword = GeneratePassword.generatePassword(8);
        Const.setSettingValue(Const.KEY_SAFEGUARD_PASSWORD, safeguardPassword);
        params.put("branchId", branchCode);
        params.put("passWord", safeguardPassword);
        params.put("posCode", Const.SN);
        params.put("state", "1");
        params.put("posId", posId);//pos编号
        Gson gson = new Gson();
        String strEntity = gson.toJson(params);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        return body;
    }

    /**
     * 请求权限
     */
    public void requestPermissions() {

        // 加载默认配置
        SharedPreferences setting = getSharedPreferences("First.ini", 0);
        boolean isfirst = setting.getBoolean("FIRST", true);
        if (isfirst) {// 第一次则跳转到欢迎页面
            setting.edit().putBoolean("FIRST", false).commit();
            firstSetting();
        }
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(WelcomeActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WelcomeActivity.this,
                        new String[]{android.Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.LOCATION_HARDWARE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_SETTINGS,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_CONTACTS,}, 1);
            } else {
//                jumpToMainActivity();
            }
        }

    }

    /**
     * 默认配置
     */
    private void firstSetting() {
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        if (mConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Const.setSettingValue(Const.KEY_SCALE, "S100C 15.6寸");
        } else {
            Const.setSettingValue(Const.KEY_SCALE, "POS20");
        }
        Const.setSettingValue(Const.KEY_MODE, "价签模式");
        Const.setSettingValue(Const.WEIGHT_UNIT, "kg");
        Const.setSettingValue(Const.TOTAL_PRICE_POINT, "2");
        Const.setSettingValue(Const.UNIT_PRICE_POINT, "2");
        Const.setSettingValue(Const.WEIGHT_POINT, "3");
        Const.setSettingValue(Const.TOTAL_PRICE_MODE, "不圆整(18.16)");

        Const.setSettingValue(Const.RESULT_DISPLAY, "0");
        //Const.setSettingValue(Const.KEY_SEND_SCALE,"1");
        Const.setSettingValue(Const.KEY_GET_DATA_MODE, "托利多传秤");
        Const.setSettingValue(Const.KEY_GET_DATA_DB, "oracle");
        Const.setSettingValue(Const.KEY_GET_DATA_ADDITIONAL_SQL, "SELECT * FROM dbo.v_sk_extratext");
        Const.setSettingValue(Const.KEY_GET_DATA_SQL, "SELECT * FROM JAVAPOS.POSDZCPRICE");
        Const.setSettingValue(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL, "INSERT INTO TESTONLINE.CLERK_LOG (CODE, SCALE_CODE, PLU_NUMBER,COMMODITY_NAME,EDITDATE,TIME,STATUS,SCALE_IP,OLD_UNIT_PRICE,NEW_UNIT_PRICE,PRINTED_EAN_DATA) VALUES( #{CODE}, #{SCALE_CODE},  #{PLU_NUMBER},#{COMMODITY_NAME},#{EDITDATE},#{TIME},#{STATUS},#{SCALE_IP},#{OLD_UNIT_PRICE},#{NEW_UNIT_PRICE},#{PRINTED_EAN_DATA})");

        //临时默认配置
        Const.setSettingValue(Const.KEY_GET_DATA_IP, "10.254.3.156");
        Const.setSettingValue(Const.KEY_GET_DATA_PORT, "1521");
        Const.setSettingValue(Const.KEY_GET_DATA_DB_NAME, "JG04");
        Const.setSettingValue(Const.KEY_GET_DATA_USER, "dbusrinf");
        Const.setSettingValue(Const.KEY_GET_DATA_PWD, "inf");


        Const.setSettingValue(Const.KEY_SCALE_PORT, "3001");
        Const.setSettingValue(Const.KEY_SEND_UNIT, "kg");

        Const.setSettingValue(Const.RESULT_DISPLAY_TIME, "3000");
        Const.setSettingValue(Const.KEY_LABEL_TYPE, "40mm*30mm");
        Const.setSettingValue(Const.KEY_REDETECT_AND_PRINT, "重新识别");
        //条码初始化
        Const.setSettingValue(Const.KEY_ODD_EVEN_CHECK, "奇校验");
        Const.setSettingValue(Const.BAR_CODE_PREFIX, "28");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_PIECE, "28");
        Const.setSettingValue(Const.BAR_CODE_FORMAT, "前缀-PLU-总价-重量");
        Const.setSettingValue(Const.BAR_CODE_PIECT_FLAG, "个位开始");
        Const.setSettingValue(Const.BAR_CODE_MULTI_PRICE_SIGN, "0");

        Const.setSettingValue(Const.KEY_ODD_EVEN_CHECK, "奇校验");
        Const.setSettingValue(Const.BAR_CODE_LENGTH, "18位");
        Const.setSettingValue(Const.BAR_CODE_IS_CHECK, "1");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_COORDINATE, "1");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_LENGTH, "二位");
        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
        Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "五位");
        Const.setSettingValue(Const.BAR_CODE_TOTAL_COORDINATE, "8");
        Const.setSettingValue(Const.BAR_CODE_TOTAL_LENGTH, "五位");
        Const.setSettingValue(Const.BAR_CODE_WEIGHT_COORDINATE, "13");
        Const.setSettingValue(Const.BAR_CODE_WEIGHT_LENGTH, "五位");
        Const.setSettingValue(Const.BAR_CODE_PRICE_COORDINATE, "");
        Const.setSettingValue(Const.BAR_CODE_PRICE_LENGTH, "五位");

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
        // 宁致打印机回转
        //Const.setSettingValue(Const.ROTATION_SETTING,"32");
        Const.setSettingValue(Const.PRINT_SETTING, "宁致打印机");
        //  语音播报
        Const.setSettingValue(Const.VOIDCE_BROADCAST_FLAG, "0");

        Const.setSettingValue(Const.TAG_DIRECTION, "逆向打印");

        //识别阈值默认值
        Const.setSettingValue(Const.DETECT_THRESHOLD, "0.65");
        //获取称重串口
        Const.setSettingValue(Const.GET_WEIGHT_PORT, "/dev/ttySAC1");

    }

    public void jumpToMainActivity() {
        try {
            Thread.sleep(8000);
//            List<TagMiddle> tagMiddles = TagMiddleHelper.selectToLable();
//            if (tagMiddles == null || tagMiddles.size() == 0){
//                if(!"".equals(Const.getSettingValue(Const.KEY_BRANCH_ID))||Const.getSettingValue(Const.KEY_BRANCH_ID)!=null){
//
//                }
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (WtAISDK.api_getCameraSetting()) {
            //startActivity(new Intent(this, ScaleActivityUI.class));
            Intent intent = new Intent(this, ScaleActivityUI.class);
            /*intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);*/
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
                Toast.makeText(this, "请同意权限授予", Toast.LENGTH_SHORT).show();
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
            showMsg("注冊成功");
            UserManager.getInstance().editPublicKey(registerBean.getKey());
            String branchCode = branchEditext.getText().toString();
            String password = "11111111";

            Const.setSettingValue(Const.KEY_BRANCH_ID, branchCode);
            // Const.setSettingValue(Const.KEY_BRANCH_GROUP_ID, registerBean.getCode());   code 变化为商户编码
            Const.setSettingValue(Const.KEY_TER_CODE, registerBean.getCode());
            Const.setSettingValue(Const.KEY_POS_PASSWORD, password);
            //绑定门店和设备
//            jumpToMainActivity();
            if ("500".equals(registerBean.getHttpCode())) {
                aiTipDialog.showSuccess(registerBean.getMsg(), registerLayout);
                return;
            }
            if (registerBean.getPosKey() == null || "".equals(registerBean.getPosKey())) {
                aiTipDialog.showSuccess("请等待审核", registerLayout);
                return;
            } else {
                Const.setSettingValue(Const.KEY_SN_CODE, registerBean.getPosKey());
            }
            ymInit();

        }
        // aiTipDialog.dismiss();
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
                if (i == -1) {//需要更新
                    isChenckVersion = true;
                    updataVersion(describe);
                } else {
                    isChenckVersion = false;
                }
            }

        }
        //判断注册和更新
        updateAndRegister(isChenckVersion);
    }

    @Override
    public void showAppState() {
        Log.i(TAG, "");
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
            aiTipDialog.showFail("pos编码不能为空", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(companyCode)) {
            aiTipDialog.showFail("组号不能为空", registerLayout);
            return;
        }
        if (TextUtils.isEmpty(branchCode)) {
            aiTipDialog.showFail("门店编码不能为空", registerLayout);
            return;
        }

        String snCode = Const.getSettingValue(Const.KEY_SN_CODE);
        if (Const.getSettingValue(Const.KEY_IS_BIND).equals("1")) {
            Log.i("test", "不再需要初始化");
            return;
        }
        WtAISDK.api_regSDK(branchCode, pos, companyCode, snCode, new OnRegSDKListener() {
            @Override
            public void regFail(int i, String s) {
                aiTipDialog.showLoading(s+i, mContext);
            }

            @Override
            public void regLoading() {
                aiTipDialog.showLoading("审核中，请联系客服人员", mContext);
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


}