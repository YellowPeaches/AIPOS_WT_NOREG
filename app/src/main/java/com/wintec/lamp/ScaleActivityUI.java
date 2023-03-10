package com.wintec.lamp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.bigkoo.pickerview.TimePickerView;
import com.elvishew.xlog.XLog;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.wintec.ScaleTrancefer;
import com.wintec.ScaleTranceferListener;
import com.wintec.ThreadCacheManager;
import com.wintec.aiposui.model.BtnType;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.utils.RxBus;
import com.wintec.aiposui.view.AiPosAccountList;
import com.wintec.aiposui.view.AiPosAllView;
import com.wintec.aiposui.view.AiPosOperatingView;
import com.wintec.aiposui.view.AiposQuickSelectView;
import com.wintec.aiposui.view.control.NUIKeyView;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.aiposui.view.dialog.NUIKeyDialog;
import com.wintec.aiposui.view.keyboard.KeyBoardEditText;
import com.wintec.detection.WtAISDK;
import com.wintec.detection.bean.DetectResult;
import com.wintec.detection.utils.StringUtils;
import com.wintec.domain.Acc;
import com.wintec.domain.DataBean;
import com.wintec.domain.Plu;
import com.wintec.domain.TraceCode;
import com.wintec.lamp.base.BaseMvpActivityYM;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.contract.ScaleContract;
import com.wintec.lamp.dao.entity.AccDto;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.entity.TraceabilityCode;
import com.wintec.lamp.dao.helper.AccDtoHelper;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.dao.helper.TraceabilityCodeHelper;
import com.wintec.lamp.entity.Total;
import com.wintec.lamp.network.schedulers.BaseSchedulerProvider;
import com.wintec.lamp.network.schedulers.SchedulerProvider;
import com.wintec.lamp.network.yunnetwork.HttpRequestClient;
import com.wintec.lamp.presenter.ScalePresenter;
import com.wintec.lamp.server.DuoDianSocketHandler;
import com.wintec.lamp.server.SocketHandler;
import com.wintec.lamp.service.WintecServiceSingleton;
import com.wintec.lamp.utils.ComIO;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.lamp.utils.DBUtil;
import com.wintec.lamp.utils.DateUtils;
import com.wintec.lamp.utils.FileUtil;
import com.wintec.lamp.utils.NetWorkUtil;
import com.wintec.lamp.utils.RSAUtils;
import com.wintec.lamp.utils.TTS.TTSpeaker;
import com.wintec.lamp.utils.ThreadPoolManagerUtils;
import com.wintec.lamp.utils.ToastUtils;
import com.wintec.lamp.utils.UserManager;
import com.wintec.lamp.utils.log.Logging;
import com.wintec.lamp.utils.pinyin.PinyinUtil;
import com.wintec.lamp.utils.scale.ScalesForTuoLiDuo;
import com.wintec.lamp.utils.sound.SoundManager;
import com.wintec.lamp.view.FormDialog;
import com.wintec.lamp.view.GoodsRegisterDialog;
import com.wintec.lamp.view.ImgDialog;
import com.wintec.lamp.view.NUIBottomSheet;
import com.wintec.lamp.view.PresentationWrapper;
import com.wintec.lamp.view.PrintDialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ScaleActivityUI extends BaseMvpActivityYM<ScalePresenter> implements ScaleContract.IView {

    public final static int MODE_NORMAL_TRADE = 1;    // ??????????????????
    public final static int MODE_DISCOUNT_TRADE = 3;    // ?????????????????????
    public final static int MODE_CHANGE_PRICE_TRADE = 4;    // ?????????????????????
    public final static int MODE_CHANGE_TOTAL_TRADE = 5;    // ?????????????????????
    private final static int SCALES_CLEAR = 10;    // ????????????
    private final static int SCALES_SHOW_WHITE = 11;    // ???????????????
    private final static int SCALES_SHOW_RED = 12;    // ???????????????
    private final static int SCALES_SHOW_PLU = 14;
    private final static int SCALES_DETECT = 13;    // ????????????
    private final static int FLUSH_TOTAL = 15;    // ????????????
    public final static int SHOW_FAIL = 16;    // ????????????
    public final static int CREATE_BARCODE_ERROR = 17;    // ??????????????????


    private Logging logging;
    @BindView(R.id.aipos)
    AiPosAllView aiPosAllView;

    private float mNet;
    private PresentationWrapper mPresentation; //????????????
    private QMUIPopup mGlobalAction;
    private NUIKeyDialog printKeyboard;        // ???????????????
    private NUIKeyDialog codeKeyboard;        // ???????????????
    private NUIKeyDialog numberKeyboard;      // ????????????
    private NUIKeyDialog discountKeyboard;    // ????????????
    private NUIKeyDialog itemCodeKeyboard;    // ???????????????
    private NUIKeyDialog priceKeyboard;       // ????????????
    private NUIKeyDialog tempPriceKeyboard;   // ????????????
    private NUIKeyDialog resStudyKeyboard;    // ??????????????????
    private NUIKeyDialog deleteOnePLUKeyboard;    // ??????????????????
    private QMUIBottomSheet quitBottomSheet;  // ????????????????????????
    private NUIKeyDialog tareKeyboard;        // ????????????
    private NUIKeyDialog lockByNumNumKeyboard;        // ????????????????????????
    private NUIKeyDialog sellByQuantityKeyboard;        // ?????????????????????????????????????????????
    private AiTipDialog aiTipDialog;          // ?????????
    private PluDto printCommdity;           //????????????
    private AtomicBoolean isContinuityPrintFlag = new AtomicBoolean(false);  // ??????????????????
    private CompositeDisposable compositeDisposable;
    private BaseSchedulerProvider schedulerProvider;
    private float wendingNet = 0f;

    private ComIO comIO = null;
    private float net = 0;                // ?????????
    private String scaleStatus = "US";    // ?????????,?????????????????? ST???????????????????????? US??????????????? OL
    //  private ScalesObject scaleObject;
    private ScalesForTuoLiDuo scalesForTuoLiDuo;
    private String codeCollect = "";      // ??????????????????????????????
    private int collectCount = 0;         // ????????????
    private boolean isCollect = false;    // ?????????,??????????????????????????????
    private AtomicBoolean isWritingCom = new AtomicBoolean(false);    // ?????????,???????????????????????????
    private AtomicBoolean isCanDectect = new AtomicBoolean(false);     // ?????????,??????????????????
    private AtomicBoolean isCanClear = new AtomicBoolean(true);     // ??????????????????????????????????????????
    private AtomicBoolean isCanReDetect = new AtomicBoolean(false);
    private AtomicBoolean isCanPrint = new AtomicBoolean(false);
    private AtomicBoolean isPrint = new AtomicBoolean(false);     // ?????????,??????????????????

    private volatile boolean isCameraShow = false;   // ?????????????????????????????????
    private String taskId = "";         // ????????????ID
    private String preTaskId = "";     // ?????????????????????ID
    private Context context;
    private GoodsModel tempGoods;
    private PluDto commdity_res;    //?????????
    private PluDto commdity_res_printer;    //?????????
    private NUIKeyDialog dialog_res;  //????????????
    private float discount = 1f;         // ??????
    private float tempPrice = 0;        // ???????????? ??????
    private float tempTotal = 0;         // ???????????? ??????
    private int tradeMode = 1;                         // ?????????,??????????????????
    private PluDto commdityDisPrice; //????????????
    private ImgDialog imgDialog;  //????????????
    private FormDialog formDialog;  //????????????
    private GoodsRegisterDialog goodsRegisterDialog;
    private Bitmap mBitmap;

    private NUIBottomSheet nuiBottomSheet;

    private String goodName;
    private boolean isNewData = false;//???????????????????????????????????????
    private Disposable mDisposable;
    private List<Commdity> commdityListByNetFlag;
    private static final int PERIOD = 15 * 60 * 1000;
    private static final int DELAY = 15 * 60 * 1000;

    private boolean isST = false;
    private boolean isZero = false;
    private boolean isCanRefreshTotal = false;  // ??????????????????????????????
    private boolean isItemReadyToPrint = false; // ??????????????????????????????
    private GoodsModel itemReadyToPrint = null; // ???????????????

    //?????????????????????
    private String sPrice;
    private String registerCode;
    private String nameRegister;
    private boolean isNum;//???????????????????????????????????????

    //???????????????
    private TimePickerView pvTime;
    //????????????
    private DetectResult detectRe;

    //??????
    private int scalesCount = 0;
    // ???????????????????????????????????????????????????1000??????
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    private int netChangeCount = 0;
    //??????????????????
    private PrintDialog printDialog;
    float weight;

    private int maxDetectNum = 5;

    private boolean isKg = "kg".equals(Const.getSettingValue(Const.WEIGHT_UNIT));
    private QMUIPopup mNormalPopup;

    private Canvas canvasTag;
    private Bitmap bitmap;
    //??????co,?????????????????????????????????????????????
    PluDto currentCo = null;

    public static boolean detectFlagOver = true;    //??????????????????
    public static int quantityOfLockNum = 1;    //??????????????????

//    private ImageView imageView;

    // ??????????????????
    private boolean tempPriceFlag = false;
    private boolean discountFlag = false;
    private boolean priceChangeFlag = false;
    private boolean priceChangeFlagFoever = false;  //?????????????????????
    //????????????flag
    private boolean allGoodsOneTagFlag = false;
    private GoodsModel disTempModel;
    private Handler scalesHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@androidx.annotation.NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCALES_CLEAR:
                    XLog.d("????????????");
                    resetPage();
                    aiPosAllView.getListView().idle();
                    aiPosAllView.getListView().clear();
                    break;
                case SCALES_SHOW_RED:
                    aiPosAllView.getTitleView().setWeight((String) msg.obj, 0);
                    break;
                case FLUSH_TOTAL:
                    isCanRefreshTotal = false;
                    List<GoodsModel> data = aiPosAllView.getListView().getData();
//                    List<GoodsModel> temp = new ArrayList<>();
//                    temp.addAll(data);
//                    aiPosAllView.getListView().clear();
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getUnitId() == 1) {
                            // ??????,??????
                        } else {
                            Total total = setTotalandDisdiscountPrice(data.get(i), "0");
                            aiPosAllView.getListView().getData().get(i).setTotal(total.getTotal());
                        }
                    }
                    // ??????????????????
                    aiPosAllView.getListView().refreshListViewTotal();
                    break;
                case SCALES_SHOW_WHITE:
                    aiPosAllView.getTitleView().setWeight((String) msg.obj, 1);
                    aiPosAllView.getTitleView().setGoods(null, "0", 0, "0", isKg);

                    mPresentation.setGoods(null, "0", 0, "0", isKg, aiPosAllView.getTitleView().getTareBySecend());

                    break;
                case SCALES_SHOW_PLU:
                    taskId = detectRe.getTaskId();
                    mPresenter.detect(detectRe, aiPosAllView.getListView(), mNet, tradeMode, discount, tempPrice, tempTotal, maxDetectNum);
                    break;
                case SCALES_DETECT:
                    if (!Const.DATA_LOADING_OK) {
                        if (Const.IS_NOT_LOADING) {
                            Const.IS_NOT_LOADING = false;
                            aiTipDialog.dataLoading("????????????????????????", aiPosAllView, Const.DATA_LOADING_TIME);
                        }
                    } else {
                        aiPosAllView.getListView().recognizing();
                        resetPage();
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            aiPosAllView.getListView().noResult();
                            return;
                        }
                        long starttime = System.currentTimeMillis();

                        final DetectResult[] detectResult = {null};
                        int detectCount = 0;
                        if (detectFlagOver) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    detectResult[0] = api_Detect();
                                }
                            }).start();
                            detectFlagOver = false;
                        } else {
                            String detectTime = (System.currentTimeMillis() - starttime) + "ms";
                            XLog.i("????????????????????????:" + detectTime);
                        }
                        while (detectResult[0] == null && System.currentTimeMillis() - starttime < 460) {
                            detectCount++;
                        }
                        detectFlagOver = true;
//                        detectResult = api_Detect();

                        if (detectResult[0] == null) {
                            String detectTime = (System.currentTimeMillis() - starttime) + "ms";
                            XLog.i("????????????:" + detectTime);
                            aiPosAllView.getListView().noResult();
                            return;
                        }
                        String detectTime = (System.currentTimeMillis() - starttime) + "ms";
                        XLog.i("????????????:" + detectTime + "-ResultCode:" + detectResult[0].getErrorCode() + " || GoodsIds:" + detectResult[0].getGoodsIds() + " || ModelIds:" + detectResult[0].getModelIds());

                        if (detectResult[0].getErrorCode() == 0) {
                            detectRe = detectResult[0];
                            if ("????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(context)) {
                                ThreadPoolManagerUtils.getInstance().execute(() -> {
                                    getPriceBypluList(detectRe.getGoodsIds());
                                });
                            }
                            scalesHandler.sendEmptyMessage(SCALES_SHOW_PLU);
                        } else {
                            aiPosAllView.getListView().noResult();
                        }
                    }
                    break;
                case SHOW_FAIL:
                    String str = (String) msg.obj;
                    aiTipDialog.showFail(str, aiPosAllView);
                    break;
                case CREATE_BARCODE_ERROR:
                    String msgError = (String) msg.obj;
                    aiTipDialog.showFail(msgError, aiPosAllView);
                    break;
            }
        }
    };

    // ????????????????????????
    private int searchItemTotal = 0;
    // ???????????????????????????
    private int searchItemTotalPage = 0;
    // ????????????????????????
    private int currentItemPage = 0;
    // ?????????????????????
    private int itemNumPerPage = 10;
    private FileWriter fileWriter;

    // ????????????
    private void resetPage() {
        searchItemTotal = 0;
        searchItemTotalPage = 0;
        currentItemPage = 0;
        itemNumPerPage = allGoodsOneTagFlag ? 8 : 10;
    }

    // ???????????????
    private void calTotalPage(String key, boolean isPlu) {
        if (isPlu) {
            searchItemTotal = PluDtoDaoHelper.getCommdityTotalByScalesCode2(key);
        } else {
            searchItemTotal = PluDtoDaoHelper.getCommdityTotalByInitials(key);
        }

        if (searchItemTotal % itemNumPerPage > 0) {
            searchItemTotalPage = searchItemTotal / itemNumPerPage + 1;
        } else {
            searchItemTotalPage = searchItemTotal / itemNumPerPage;
        }
    }

    @Override
    protected int contentResId() {
        return R.layout.activity_scale_ui_new;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mPresentation = new PresentationWrapper();
        mPresentation.initPresentation(mContext);
//        imageView = findViewById(R.id.imgage);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        aiPosAllView.getTitleView().setTvPriceTxt(isKg);
        if (!aiPosAllView.mIsLandscape)
            aiPosAllView.getTitleView().getKeyBoardEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                //?????????
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void afterTextChanged(Editable s) {
                    resetPage();
                    String key = (s == null) ? "" : s.toString();
                    refreshItemListBySearch(key);

                }
            });

        //  timeLoop();
        aiPosAllView.getOperatingView().getBtn_print().setOnClickListener(r -> {
            if (net >= weight / 1000) {
                isCanDectect.set(false);
                // ????????????
                scalesHandler.sendEmptyMessage(SCALES_DETECT);
            } else {
                aiTipDialog.showFail("??????????????????", aiPosAllView);
            }
        });
        aiPosAllView.getOperatingView().getBtn_kb_print().setOnClickListener(r -> {
            // ??????????????????
            if (allGoodsOneTagFlag) {
                //???????????????
                StringBuilder stringBuilder = new StringBuilder();
                String connector = Const.getSettingValue(Const.BAR_ONECODE_CONNECTOR);
                List<GoodsModel> data = aiPosAllView.getAiPosAccountList().getData();
                stringBuilder.append(Const.getSettingValue(Const.BAR_ONECODE_PREFIX));
                stringBuilder.append(connector);
                data.forEach(item -> {
                    stringBuilder.append(item.getTagCode());
                    stringBuilder.append(connector);
                });
                stringBuilder.append(Const.getSettingValue(Const.BAR_ONECODE_POSTFIX));
                WintecServiceSingleton.getInstance().printAllLable(data, stringBuilder.toString());
                aiPosAllView.getAiPosAccountList().clear();
                mPresentation.clear();
                return;
            }
            // ??????????????????
            if (!isNum) {
                return;
            }
            PluDto dto = PluDtoDaoHelper.getCommdityByScalesCode(aiPosAllView.getOperatingView().getKeyBoardEditText().getText().toString());
            if (dto == null) {
                aiTipDialog.showFail("??????????????????", aiPosAllView);
                return;
            }
            if (!priceChangeFlagFoever) {
                if (taskId != null || "".equals(taskId)) {
                    if (!taskId.equals(detectRe.getTaskId())) {
                        XLog.tag("error").e("?????????????????????id???sessionid?????????");
                    }
                    api_confirmResult(taskId, dto.parse().getGoodsId(), dto.parse().getGoodsName(), false);
                    XLog.i("?????????");
                    if (net <= 0.0f && dto.getPriceUnitA() == 0) {
                        aiTipDialog.showFail("??????????????????", aiPosAllView);
                        return;
                    }

                    if (detectRe != null && taskId != null && !"".equals(taskId) && !isZero && !taskId.equals(preTaskId)) {
                        preTaskId = taskId;
                        boolean isDetect = false;
                        if (this.detectRe.getTaskId() == null || !this.detectRe.getTaskId().contains(dto.getPluNo())) {
                            isDetect = false;
                        } else {
                            if (detectRe.getGoodsIds().get(0).equals(dto.getPluNo()) && !Const.keyFromInput) {
                                isDetect = true;
                            } else {
                                isDetect = false;
                            }
                        }
                        long current_time = System.currentTimeMillis();
                        api_confirmResult(taskId, dto.parse().getGoodsId(), dto.parse().getGoodsName(), isDetect);
                        long current_time2 = System.currentTimeMillis();

                        Const.keyFromInput = false;
                        XLog.d("??????????????????" + (current_time2 - current_time));
                        if (NetWorkUtil.isNetworkAvailable(this)) {
                            try {
                                dealInsert(dto.parse(), isDetect ? 3 : 4);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                                upImg(taskId, dto.parse().getGoodsName(), dto.parse().getGoodsId(), isDetect ? 4 : 5);
                        }
                    }
                    if (dto.getPriceUnitA() == 0) {
                        Total total = setTotalandDisdiscountPrice(dto.parse(), "1");
                        isAfterPrint = true;
                        sendScale(dto.getPriceUnitA(), dto.parse(), "1", total);
                        setTitleData(dto.parse(), 0, total);
                    } else {
                        tempGoods = dto.parse();
                        numberKeyboard.showWithParams(1);
                    }

                }
            }
        });
        aiPosAllView.getOperatingView().getKeyBoardEditText()
                .setEvent(new KeyBoardEditText.OnPageChangeClickEvent() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void previewPage() {
                        if (currentItemPage <= 0) {
                            return;
                        }
                        // ????????????
                        currentItemPage--;
                        refreshItemListBySearch(aiPosAllView.getOperatingView().getKeyBoardEditText().getText().toString());
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void nextPage() {
                        if (currentItemPage >= (searchItemTotalPage - 1)) {
                            return;
                        }
                        // ????????????
                        currentItemPage++;
                        refreshItemListBySearch(aiPosAllView.getOperatingView().getKeyBoardEditText().getText().toString());
                    }
                });

    }


    /**
     * ???????????????????????????????????????
     *
     * @param key
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refreshItemListBySearch(String key) {

        if (key == null || "".equals(key.toString())) {
            aiPosAllView.getListView().clear();
            aiPosAllView.getListView().idle();
            return;
        }
        List<PluDto> commdityBySearchKey;
        isNum = isNumeric(key);
        if (isNum) {
            if (key.length() < Integer.valueOf(Const.getSettingValueWithDef(Const.SEARCH_LENGHT, "0")) &&
                    key.length() > 8) {
                return;
            }
            aiPosAllView.getListView().clear();
            commdityBySearchKey = PluDtoDaoHelper.getCommdityByScalesCode2(key.toString(), allGoodsOneTagFlag ? 8 : 10, currentItemPage);
            calTotalPage(key.toString(), true);

        } else {
            aiPosAllView.getListView().clear();
            commdityBySearchKey = PluDtoDaoHelper.getCommdityBySearchKey(key.toString(), aiPosAllView.mIsLandscape ? (allGoodsOneTagFlag ? 8 : 10) : 6, currentItemPage);
            calTotalPage(key.toString(), false);
        }

        if (commdityBySearchKey.size() > 0) {
            aiPosAllView.getListView().showing();
            Const.keyFromInput = true;
        } else {
            aiPosAllView.getListView().idle();
        }
        commdityBySearchKey.forEach(item -> {
            aiPosAllView.getListView().addData(item.parseNet(mNet, tradeMode, discount, tempPrice, tempTotal));
        });
    }

    /**
     * ??????RxBus?????????
     */
    Disposable disposable = RxBus.getInstance().doDisposable(BtnType.class, Schedulers.newThread(), new Consumer<BtnType>() {
        @Override
        public void accept(BtnType btnType) throws Exception {
            if (null == btnType) {
                return;
            }
            String s = btnType.getmType();
            switch (s) {
                case "requestError":
                    aiTipDialog.dismiss();
                    break;
            }
            Log.d("TAG", "RxBusActivity??????msg???" + s);
        }
    }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {

        }
    });

    int count = 0;

    @Override
    protected void initEvent() {
        TTSpeaker.getInstance(mContext);
        //????????????????????????
        aiPosAllView.getAiPosAccountList().setCallBack(new AiPosAccountList.AccountCallBack() {
            @Override
            public void clearCallBack() {

            }

            @Override
            public void saleCallBack() {

            }
        });
        // ????????????
        tareKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("???????????????(???)", NUIKeyView.KEY_TYPE_CODE).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                handleYtare(code, dialog);
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        // ??????????????????
        // ??????
        aiPosAllView.getOperatingView().addOperatingBtn("??????", R.mipmap.icon_zero, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPreSetTare = false;
                WintecServiceSingleton.getInstance().setYTare(0);
                setZero();
            }
        });
        // ??????
        aiPosAllView.getOperatingView().addOperatingBtn("??????", R.mipmap.icon_tare, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTare();
            }
        });
        // ????????????
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getOperatingView().addOperatingBtn("????????????", R.mipmap.icon_ytare, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tareKeyboard.show();
                }
            });
        }
        // ????????????
        aiPosAllView.getOperatingView().addOperatingBtn("????????????", R.mipmap.icon_duopinyiqian, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(Const.getSettingValue(Const.KEY_ALL_ONE_FLAG))) {
                    aiPosAllView.getListView().clear();
                    aiPosAllView.getListView().idle();
                    allGoodsOneTagFlag = !allGoodsOneTagFlag;
                    resetPage();
                    if (allGoodsOneTagFlag) {
                        aiPosAllView.getAiPosAccountList().visible();
                        mPresentation.visible();
                        aiPosAllView.getListView().reSetListViewColumn(4);
                    } else {
                        aiPosAllView.getAiPosAccountList().hide();
                        mPresentation.hide();
                        aiPosAllView.getListView().reSetListViewColumn(5);
                    }
                    aiPosAllView.getOperatingView().setButtonSelected(3);
                } else {
                    aiTipDialog.showFail("???????????????????????????", aiPosAllView);
                }

            }
        });
        // ????????????
        if (!aiPosAllView.mIsLandscape) {
            /*aiPosAllView.getOperatingView().addOperatingBtn("????????????", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (net >= weight / 1000) {
                        isCanDectect.set(false);
                        // ????????????
                        scalesHandler.sendEmptyMessage(SCALES_DETECT);
                    } else {
                        aiTipDialog.showFail("??????????????????", aiPosAllView);
                    }
                }
            });*/
        }

        // ????????????
        aiPosAllView.getOperatingView().addOperatingBtn("????????????", R.mipmap.icon_linshigaijia, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(Const.getSettingValue(Const.KEY_TEMPPRICE_FLAG))) {
                    aiPosAllView.getOperatingView().setButtonSelected(4, 5, 6);
                    tempPriceFlag = !tempPriceFlag;
                    discountFlag = false;
                    priceChangeFlag = false;
                } else {
                    aiTipDialog.showFail("???????????????????????????", aiPosAllView);
                }

            }
        });

        // ??????
        aiPosAllView.getOperatingView().addOperatingBtn("??????", R.mipmap.icon_zhekou, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(Const.getSettingValue(Const.KEY_DISCOUNT_FLAG))) {
                    aiPosAllView.getOperatingView().setButtonSelected(5, 4, 6);
                    discountFlag = !discountFlag;
                    tempPriceFlag = false;
                    priceChangeFlag = false;
                } else {
                    aiTipDialog.showFail("????????????????????????", aiPosAllView);
                }

            }
        });

        aiPosAllView.getOperatingView().addOperatingBtn("????????????", R.mipmap.icon_yongjiugaijia, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(Const.getSettingValue(Const.KEY_UPPRICE_FLAG))) {
                    aiPosAllView.getOperatingView().setButtonSelected(6, 5, 4);
                    priceChangeFlag = !priceChangeFlag;
                    discountFlag = false;
                    tempPriceFlag = false;
                    priceChangeFlagFoever = !priceChangeFlagFoever;
                } else {
                    aiTipDialog.showFail("???????????????????????????", aiPosAllView);
                }

            }
        });
        aiPosAllView.getOperatingView().addOperatingBtn("??????", R.mipmap.icon_zouzhi, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadPoolManagerUtils.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        WintecServiceSingleton.getInstance().roll();

                        if ("????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) ||
                                "????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
                            getDataOnline();
                        }
                    }
                });

            }
        });
        // ????????????????????????
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getOperatingView().setInputListner(new AiPosOperatingView.InputListner() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onInput(String content) {
                    resetPage();
                    refreshItemListBySearch(content);
                    /*isNum = isNumeric(content.toString());
                    if (isNum) {
                        if (!TextUtils.isEmpty(content.toString()) && content.toString() != null) {
                            try {
                                resetPage();
                                String plu = content.toString();
                                List<PluDto> list = new ArrayList<>();
                                list = PluDtoDaoHelper.getCommdityByScalesCode2(plu, allGoodsOneTagFlag?8:10, currentItemPage);
                                calTotalPage(plu, true);
                                aiPosAllView.getListView().clear();
                                if (list.size() <= 0) {
                                    aiPosAllView.getListView().idle();
                                    return;
                                }
                                aiPosAllView.getListView().showing();
                                for (PluDto co : list) {
                                    aiPosAllView.getListView().addData(co.parseNet(mNet, tradeMode, discount, tempPrice, tempTotal));
                                }
                            } catch (Exception e) {
                                Log.i("test", e.toString());
                            }
                        } else {

                        }

                    } else {
                        inputListenEvent(content.toString());
                    }*/
                }
            });
        }

        // ???????????????
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getQuickSelectView().setClickListener(new AiposQuickSelectView.ClickListener() {
                @Override
                public void onItemClick(GoodsModel model, View view, int position) {
                    disTempModel = model;
                    onGoodsItemClick(model, position, false);
                    aiPosAllView.getListView().clear();
                    aiPosAllView.getListView().idle();
                    resetPage();
                }
            });
        }


        aiPosAllView.getListView().setVersionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count >= 3) {
                    // hiddenEvent();
                    count = 0;
                }
            }
        });

    }


    @Override
    protected void loadData() {

    }

    /**
     * ?????????????????????
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        if ("".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ??????????????????
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * dp???px
     *
     * @param context
     * @param dpVal
     * @return dip
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }


    @Override
    protected View loadingStatusView() {
        return null;
    }

    @Override
    protected ScalePresenter createPresenter() {
        return new ScalePresenter();
    }

    @Override
    protected void initData() {
        // ?????????????????????
        try {
            fileWriter = initDisCountFile();
        } catch (IOException e) {
            XLog.e(e);
            e.printStackTrace();
        }
        context = this;
        SoundManager.getInstance().init(context);
        WintecServiceSingleton.getInstance().init(context, scalesHandler, new WintecServiceSingleton.ScalesCallback() {
            @Override
            public void getData(float net, float tare, String status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scalesListener(net, tare, status);
                    }
                });
            }
        });
        nuiBottomSheet = new NUIBottomSheet(mContext);
//        logging = new Logging(mContext);
//        logging.d("???????????????");
//        printer = new Printer(mContext);
        if (TextUtils.isEmpty(Const.versionName)) {
            aiPosAllView.getListView().setVersion("v:");
        } else {
            aiPosAllView.getListView().setVersion("v:" + Const.versionName);
        }
        aiTipDialog = new AiTipDialog();
        compositeDisposable = new CompositeDisposable();
        schedulerProvider = SchedulerProvider.getInstance();
        tradeMode = MODE_NORMAL_TRADE;
        permanentPriceChange();

        //  ???????????????
        initReceiveCommdity();
        // ?????????
        codeKeyboard = new NUIKeyDialog
                .Builder(context)
                .addKeyView("?????????????????????", NUIKeyView.KEY_TYPE_CODE)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        String keySize = Const.getSettingValue(Const.KEY_ITEMCODE_SIZE);
                        int codeC = Integer.valueOf(code);
                        if (!TextUtils.isEmpty(keySize)) {
                            String s = String.format("%0" + keySize + "d", codeC);
                            PluDto commdity = PluDtoDaoHelper.getCommdityByScalesCodeLocal(s);
                            if (commdity == null) {
                                aiTipDialog.showFail("?????????????????????", aiPosAllView);
                                dialog.dismiss();
                                return;
                            }
                            registerCode = commdity.getPluNo();
                            //PicSelectorUtils.openCamera(ScaleActivityUI.this, 202);
                            commdity_res = commdity;
                            commdity_res_printer = commdity;
                            dialog_res = dialog;
                            goodsRegisterDialog = new GoodsRegisterDialog(mContext);
                            goodsRegisterDialog.show();
                            String name = commdity.getNameTextA();
                            Const.setSettingValue(Const.KEY_RECORD_COUNT, String.valueOf(5)); //??????????????????
                            String key_record_count = Const.getSettingValue(Const.KEY_RECORD_COUNT);
                            goodsRegisterDialog.setTvGoodsName("????????????\"" + name + "\"");
                            goodsRegisterDialog.setOnClickListener(new GoodsRegisterDialog.OnClickListener() {
                                @Override
                                public void onClick(int type) {
                                    switch (type) {
                                        case 1://??????
                                            long curClickTime = System.currentTimeMillis();
                                            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                                                // ???????????????????????????lastClickTime???????????????????????????
                                                lastClickTime = curClickTime;
                                                itemRegisterEvent(true, dialog_res, commdity_res);
                                            }
                                            break;
                                        case 2://??????
                                            File file = FileUtil.getFile(mBitmap);
                                            if (commdity_res == null) {
                                                break;
                                            }
                                            goodsRegisterDialog.dismiss();
                                            commdity_res = null;
                                            break;
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        codeKeyboard.dismiss();
                    }
                }).create();
        // ????????????
        discountKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("???????????????", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                boolean b = handleDiscount(code, dialog);
                if (isContinuityPrintFlag.get() && b) {
                    if (printCommdity.getPriceUnitA() == 0) {
                        isPrint.set(true);
                        Total total = setTotalandDisdiscountPrice(printCommdity.parse(), "1");
                        printDialog.show(printCommdity, total, isKg);
                        printDialog.setTotalandWeight(mNet + "", total.getPrice(), total.getTotal());
                    } else {
                        printKeyboard.show();
                    }
                }
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        //????????????????????????
        lockByNumNumKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("???????????????", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                if (code.contains(".")) {
                    Toast.makeText(mContext, "???????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                quantityOfLockNum = Integer.valueOf(code);
                dialog.dismiss();
                printKeyboard.show();
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        //????????????
        resStudyKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("??????????????????", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                code = CommUtils.deleteZero(code);
                toResStudy(code);
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        //????????????????????????
        deleteOnePLUKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("??????????????????????????????", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                deleteOnePLU(code);
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        // ??????????????????
        tempPriceKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("?????????????????????", NUIKeyView.KEY_TYPE_NUMBER_DISCOUNT).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                boolean b = handleTempPrice(code, dialog, param);
                if (isContinuityPrintFlag.get() && b) {
                    if (printCommdity.getPriceUnitA() == 0) {
                        isPrint.set(true);
                        Total total = setTotalandDisdiscountPrice(printCommdity.parse(), "1");
                        printDialog.show(printCommdity, total, isKg);
                        printDialog.setTotalandWeight(mNet + "", total.getPrice(), total.getTotal());

                    } else {
                        printKeyboard.show();
                    }
                }
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        //?????????
        itemCodeKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("??????????????????", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                code = CommUtils.deleteZero(code);
                itemCodeInput(code, dialog);
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        //????????????
        printKeyboard = new NUIKeyDialog.Builder(context)
                .addKeyView("??????????????????????????????", NUIKeyView.KEY_TYPE_NUMBER)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        XLog.d("??????????????????:" + code);
                        if (code.contains(".")) {
                            Toast.makeText(mContext, "???????????????", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        onPrintInputEvent(dialog, code);
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        dialog.dismiss();
                    }
                }).create();
        //????????????
        numberKeyboard = new NUIKeyDialog.Builder(context)
                .addKeyView("???????????????", NUIKeyView.KEY_TYPE_NUMBER)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        onNumberInputEvent(true, dialog, code);
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        onNumberInputEvent(false, dialog, "");
                    }
                }).create();
        //?????????????????? ????????????
        sellByQuantityKeyboard = new NUIKeyDialog.Builder(context)
                .addKeyView("???????????????", NUIKeyView.KEY_TYPE_NUMBER)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        onSellByQuantityKeyboardInputEvent(true, dialog, code);
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        onSellByQuantityKeyboardInputEvent(false, dialog, "");
                    }
                }).create();
        // ???????????????
        quitBottomSheet = new NUIBottomSheet(context).createQuitBottomSheet(new NUIBottomSheet.ClickListener() {
            @Override
            public void onItemClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                if (position == 0) {
                    finish();
                    System.exit(0);
                } else {
                    quitBottomSheet.dismiss();
                }
            }

        }, "???????????????");

        //???????????????
        weight = 30;
        try {
            weight = Float.parseFloat(Const.getSettingValue(Const.DELECT_WEIGHT));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // ???????????????????????????
        aiPosAllView.getListView().setClickListener(new com.wintec.aiposui.view.AiPosListView.ClickListener() {
            @Override
            public void onItemClick(GoodsModel model, View view, int position) {
                Const.fromClick = true;
                disTempModel = model;
                long start = System.currentTimeMillis();
                onGoodsItemClick(model, position, false);
                long end = System.currentTimeMillis();
                XLog.d("?????????????????????????????????: " + (end - start) + "ms");
                Const.fromClick = false;
//                PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(model.getGoodsId());
//                if (commdityByScalesCode.getPreviewImage() == null || "".equals(commdityByScalesCode.getPreviewImage())) {
//                    updatePrevirwImg(model);
//                }
            }

            @Override
            public void onFabclick() {

            }

            //????????????
            @Override
            public void onLongClick(GoodsModel model, View view, int position) {
//                boolean b = onGoodsItemClick(model, position, false);
//
//                if (b) {
//                    updatePrevirwImg(model);
//                    aiPosAllView.getListView().clear();
//                    aiPosAllView.getListView().idle();
//                }
                if (aiPosAllView.mIsLandscape) {
//                    popupsShow(view, new String[]{"??????", "????????????", "????????????"}, model);
                    popupsShow(view, new String[]{"??????",}, model);
                } else {
                    //  popupsShow(view, new String[]{"???????????????"}, model);
                }


            }
        });
        // ???????????????
       /* aiPosTitleView.setInputListener(new AiPosTitleView.OnTextChangedListener() {
            @Override
            public void onInput(String text) {
                inputListenEvent(text);
            }
        });*/

        aiPosAllView.getListView().idle();
        // ???????????????
        printDialog = new PrintDialog(context);
        printDialog.initDialog();
        printDialog.setPositiveListener(new PrintDialog.PrintDialogListener() {
            @Override
            public void pintOnClick() {
                printSendScale();
            }

            @Override
            public void dismissClick() {
                aiPosAllView.getTitleView().setHint("");
                isContinuityPrintFlag.set(false);
                clearMode();
                isPrint.set(false);

            }
        });
        imgDialog = new ImgDialog(context);
        imgDialog.initDialog();
        formDialog = new FormDialog(context, aiPosAllView);
        formDialog.initDialog();
        imgDialog.setPositiveListener(new ImgDialog.DialogPositiveListener() {
            @Override
            public void onClick() {
//                isCanDectect.set(true);//xyq
                isCanReDetect.set(false);
            }
        });


        isCanDectect.set(true);
        aiPosAllView.getTitleView().getWeightView().setOnClickListener(new View.OnClickListener() {
            final static int COUNTS = 5;//????????????
            final static long DURATION = 3 * 1000;//??????????????????
            long[] mHits = new long[COUNTS];

            @Override
            public void onClick(View view) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    if (mHits.length == 5) {
                        startActivity(SettingActivity.class);
                    }
                }

            }
        });
        aiPosAllView.getTitleView().getTv_price().setOnClickListener(new View.OnClickListener() {
            final static int COUNTS = 5;//????????????
            final static long DURATION = 3 * 1000;//??????????????????
            long[] mHits = new long[COUNTS];

            @Override
            public void onClick(View view) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    if (mHits.length == 5) {
                        deleteOnePLUKeyboard.show();
                    }
                }

            }
        });
        aiPosAllView.getTitleView().getTv_total().setOnClickListener(new View.OnClickListener() {
            final static int COUNTS = 5;//????????????
            final static long DURATION = 3 * 1000;//??????????????????
            long[] mHits = new long[COUNTS];

            @Override
            public void onClick(View view) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    if (mHits.length == 5) {
                        if ("1".equals(Const.getSettingValue(Const.KEY_RESTUDY_FLAG))) {
                            resStudyKeyboard.show();
                        } else {
                            aiTipDialog.showFail("???????????????????????????", aiPosAllView);
                        }
                    }
                }
            }
        });
    }


    //????????????
    private void onPrintInputEvent(NUIKeyDialog dialog, String code) {
        dialog.dismiss();
        int count = Integer.parseInt(code);
        Total total = setTotalandDisdiscountPrice(printCommdity.parse(), quantityOfLockNum + "");
        if ("1".equals(Const.getSettingValue(Const.VOIDCE_BROADCAST_FLAG))) {
            TTSpeaker.getInstance(mContext).speak(printCommdity.getNameTextA());
        }
        for (int i = 0; i < count; i++) {
            dealInsert(printCommdity.parse(), 5);
            WintecServiceSingleton.getInstance().printImgLable(printCommdity, total, quantityOfLockNum, isKg, tradeMode, aiPosAllView.getTitleView().getTare(), mNet);
            try {
                TimeUnit.MILLISECONDS.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        quantityOfLockNum = 1;
        isContinuityPrintFlag.set(false);
        clearMode();

    }

//    private void updatePrevirwImg(GoodsModel model) {
//        ScaleBitmap clearestScaleBitmap = api_getScaleBitmap(true);
//
//
//        File imagesFile = new File(mContext.getFilesDir() + "/images/");
//        if (!imagesFile.exists()) {
//            imagesFile.mkdir();
//        }
//
//
//        File file = new File(mContext.getFilesDir() + "/images/" + model.getGoodsId() + ".jpg");
//        if (file.exists()) {
//            file.delete();
//        }
//        BitmapUtils.saveBitmapFile(clearestScaleBitmap.getCrop(), new File(mContext.getFilesDir() + "/images/" + model.getGoodsId() + ".jpg"));
//
//        PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCodeLocal(model.getGoodsId());
//        commdityByScalesCode.setPreviewImage(mContext.getFilesDir() + "/images/" + model.getGoodsId() + ".jpg");
//        PluDtoDaoHelper.updateCommdity(commdityByScalesCode);
//        GlideCacheUtil.getInstance().clearImageAllCache(mContext);
//    }

    /**
     * ???????????????
     */
    private void initReceiveCommdity() {
        if ("???????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
            //  ???????????????
            try {
                ScaleTrancefer.getInstance().startLoadPluData(new ScaleTranceferListener() {
                    @Override
                    public void onConnected() {
                    }

                    @Override
                    public void onDataReceived(String str) {
                    }

                    @Override
                    public void onException(String str) {
                    }

                    @Override
                    public void onIOException(String str) {
                    }

                    @Override
                    public void onStart(String str) {
                    }

                    @Override
                    public void pluList(List<DataBean> dataBeans) {
                        insert(dataBeans);
                    }

                    @Override
                    public void zsmList(List<DataBean> list) {
                        int a = 0;
                    }

                    @Override
                    public void bpluspluList(List<DataBean> list) {
                        insert(list);
                    }

                    @Override
                    public void bpluszsmList(List<DataBean> list) {
                        //?????????
//                        System.out.println(list.toString());
                        insertbpluszsm(list);
                    }

                    @Override
                    public void bpluszsmpluList(List<DataBean> list) {
                        //??????????????????
//                        System.out.println(list.toString());
                        insertbpluszsmgbm(list);
                    }
                }, Const.getSettingValue(Const.KEY_SCALE_PORT) == null || "".equals(Const.getSettingValue(Const.KEY_SCALE_PORT)) ? 3001 : Integer.valueOf(Const.getSettingValue(Const.KEY_SCALE_PORT)));
            } catch (NumberFormatException e) {
                XLog.e(e);
            }
            initTaceability();
        } else if ("????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) || "????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
            //todo ???????????? ????????????
            ThreadPoolManagerUtils.getInstance().execute(() -> {
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(5000L);
                        getDataOnline();
                    }
                }, 17, Long.valueOf(Const.getSettingValue("DELAY_TIME")), TimeUnit.SECONDS);
            });
        }
    }

    /**
     * @param null
     * @description: ??????????????????
     * @return:
     * @author: dean
     * @time: 2022/3/9 16:31
     */

    private void getDataOnline() {
        XLog.i("????????????????????????");
        if (NetWorkUtil.isNetworkAvailable(this)) {
            DBUtil.init(scalesHandler);
            ThreadCacheManager.getExecutorService().execute(() -> {
                Long startTime = System.currentTimeMillis();
                String settingValue = Const.getSettingValue(Const.KEY_GET_DATA_SQL);
                String settingValue1 = Const.getSettingValue(Const.KEY_GET_DATA_ADDITIONAL_SQL);
                List plus = null;
                try {
                    plus = DBUtil.Query(settingValue);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                List accs = null;
                if (("sql Server".equals(Const.getSettingValue(Const.KEY_GET_DATA_DB)))) {
                    try {
                        accs = DBUtil.Query(settingValue1);
                    } catch (SQLException throwables) {
                        XLog.e(throwables);
                    }
                }
                saveSqlDate(plus, accs);
                Long endTime = System.currentTimeMillis();
                XLog.i("???????????????????????????,?????? " + (endTime - startTime) / 1000 + " s");
            });
        } else {
            //?????????
            XLog.i("??????????????????????????????????????????");
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param plus
     * @param accs
     */
    private void saveSqlDate(List plus, List accs) {
        HashMap<String, String> pluAndItem = new HashMap<>();
        if (plus != null && plus.size() == 2) {
            for (HashMap map : (List<HashMap>) plus.get(1)) {
                PluDto commdity = new PluDto(map);
                commdity.setInitials(PinyinUtil.getFirstSpell(commdity.getNameTextA()));
                commdity.setBranchId(Const.getSettingValue(Const.KEY_BRANCH_ID));
                PluDto commdityByItemCode = PluDtoDaoHelper.getCommdityByScalesCodeLocal(commdity.getPluNo());

                pluAndItem.put(commdity.getPluNo(), commdity.getItemNo());

                if (commdityByItemCode == null) {
                    PluDtoDaoHelper.insertCommdity(commdity);
                } else {
                    // CommdityHelper.deleteCommdityByKey(commdityByItemCode.get_id());
                    commdity.set_id(commdityByItemCode.get_id());
                    commdity.setPreviewImage(commdityByItemCode.getPreviewImage());
                    PluDtoDaoHelper.updateCommdity(commdity);
                }
            }
        }
        if (accs != null && accs.size() == 2) {
            for (HashMap map : (List<HashMap>) accs.get(1)) {
                AccDto acc = new AccDto(map);
                AccDtoHelper.deleteByAccNo(acc.getAccNo());
                AccDtoHelper.insert(acc);

                TraceabilityCode traceabilityCode = new TraceabilityCode(acc.getAccNo() + "", pluAndItem.get(acc.getAccNo() + ""), acc.getContent());
                TraceabilityCode traceabilityCodeTemp = TraceabilityCodeHelper.selectByPLU(acc.getAccNo() + "");
//                ???????????????
                if (traceabilityCodeTemp == null) {
                    TraceabilityCodeHelper.insert(traceabilityCode);
                } else {
                    traceabilityCode.set_id(traceabilityCodeTemp.get_id());
                    TraceabilityCodeHelper.update(traceabilityCode);
                }
            }

        }
    }

    /**
     * ?????????????????????
     */
    private void initTaceability() {
        if ("1".equals(Const.getSettingValue(Const.TRACEABILITY_CODE_FLAG))) {
            int port = 0;
            try {
                port = Integer.valueOf(Const.getSettingValue(Const.TRACEABILITY_CODE_PORT));
            } catch (NumberFormatException e) {
                XLog.e("????????????????????????" + e);
                return;
            }

            int finalPort = port;
            ThreadPoolManagerUtils.getInstance().execute(() -> {
                try {
                    ServerSocket serverSocket = new ServerSocket(finalPort);
                    while (true) {
                        Socket accept = serverSocket.accept();
                        ThreadPoolManagerUtils.getInstance().execute(new SocketHandler(accept));
                    }
                } catch (IOException e) {
                    XLog.e("???????????????????????????" + e);

                }
            });

        }
    }


    /**
     * ??????????????????????????????
     */
    private void initDuoDianPlu() {
        try {
            ServerSocket serverSocket = new ServerSocket(3005);
            while (true) {
                Socket accept = serverSocket.accept();
                ThreadPoolManagerUtils.getInstance().execute(new DuoDianSocketHandler(accept));
            }
        } catch (IOException e) {
            XLog.e("???????????????????????????");

        }


    }


    private void upLog() {
        initTimePicker1();
        pvTime.show();
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
        startDate.set(1900, 0, 1);
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
                    aiTipDialog.showFail("?????????????????????", aiPosAllView);
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false}) //?????????????????? ????????????????????????????????????????????????
                .setLabel("???", "???", "???", "", "", "")//?????????????????????????????????
                .isCenterLabel(false)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)//????????????????????????
                .setTextColorOut(Color.GRAY)//?????????????????????????????????
                .setSubmitColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setContentSize(30)
                .setLineSpacingMultiplier(1.5f)
                .setTextXOffset(-10, 0, 10, 0, 0, 0)//??????X???????????????[ -90 , 90??]
                .setRangDate(startDate, endDate)
                .setDate(endDate)
                .setSubCalSize(30)
//                .setBackgroundId(0x00FFFFFF) //????????????????????????
                .setDecorView(null)
                .build();
    }

    private String getTime(Date date) {//???????????????????????????????????????
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void toResStudy(String code) {
        if (code == null || code.equals("")) {
            CommUtils.showMessage(mContext, "?????????????????????");
            return;

        }
        if ("1".equals(Const.getSettingValue(Const.KEY_RESTUDY_FLAG))) {
            int i = api_setNoRecommend(code);
            api_updateLearningData();
            if (i == 0) {
                aiTipDialog.showSuccess("??????????????????,???????????????", aiPosAllView);
            } else {
                aiTipDialog.showSuccess("??????????????????????????????", aiPosAllView);
            }
        } else {
            aiTipDialog.showFail("???????????????????????????", aiPosAllView);
        }
    }

    /**
     * @param code ??????plu
     * @description:
     * @return: void
     * @author: dean
     * @time: 2022/11/10 10:38
     */
    private void deleteOnePLU(String code) {
        if (code == null || code.equals("")) {
            CommUtils.showMessage(mContext, "??????PLU????????????");
            return;
        }
        final PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(code);
        if (commdityByScalesCode == null) {
            CommUtils.showMessage(mContext, "??????PLU???????????????????????????");
            return;
        }
        PluDtoDaoHelper.deleteOneByPLU(commdityByScalesCode);
        aiTipDialog.showSuccess("????????? " + code, aiPosAllView);
    }

    private void setTitleData(GoodsModel model, int count, Total total) {
        if (total == null) {
            return;
        }
        String unit = String.valueOf(mNet);
        if (!TextUtils.isEmpty(unit)) {
            //model.setPrice(  total.getPrice());
            aiPosAllView.getTitleView().setGoods(model, mNet + "", count, total.getTotal(), isKg);

            //  mPresentation.setGoods(model, mNet + "", count, total.getTotal(), isKg, aiPosAllView.getTitleView().getTareBySecend());

        }
    }

    /**
     * ????????????
     */
    private void dealInsert(GoodsModel model, int proportion) {
        Map<String, Object> map = new HashMap<>();
        String content = model.getGoodsName();//???????????????????????????
        int pricingManner = model.getUnitId();
        String transactionValue = model.getPrice();//???????????????
        String branchCode = Const.getSettingValue(Const.KEY_BRANCH_ID);
        String weight = aiPosAllView.getTitleView().getWeight();

        map.put("posSn", Const.SN);//?????????????????????
        map.put("content", content);
        map.put("transactionValue", transactionValue);
        map.put("pricingManner", pricingManner);//9-??????  4-??????
        map.put("weight", weight);
        map.put("branchCode", StringUtils.isEmpty(branchCode) ? "210629001" : branchCode);
        map.put("plu", model.getGoodsId());
        map.put("SD", Const.getSettingValue(Const.TRACEABILITY_CODE_PORT));
        map.put("proportion", proportion);
//        RequestBody fileRQ = RequestBody.create(MediaType.parse("image/*"), file);
//        MultipartBody.Part part = MultipartBody.Part.createFormData("picture", file.getName(), fileRQ);

        mPresenter.dealInsert(map);
    }

    private void permanentPriceChange() {
        //????????????
        priceKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("?????????????????????", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                handlePrice(code, dialog);
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
    }

    /**
     * ??????????????????
     *
     * @param isConfirm
     * @param dialog
     * @param code
     */
    void onNumberInputEvent(Boolean isConfirm, NUIKeyDialog dialog, String code) {
        if (!isConfirm) {
            dialog.dismiss();
            return;
        }
        if (code == null || "".equals(code) || code.contains(".")) {
            return;
        }
        int number = Integer.valueOf(code);
        if (number <= 0) {
            return;
        }
        try {
            Total total = setTotalandDisdiscountPrice(tempGoods, number + "");
            isAfterPrint = false;
            sendScale(1, tempGoods, number + "", total);
            setTitleData(tempGoods, number, total);
        } catch (Exception e) {
            XLog.e(e);
        }
        dialog.dismiss();
    }

    /**
     * onSellByQuantityKeyboardInputEvent
     * ??????????????????
     *
     * @param isConfirm
     * @param dialog
     * @param code
     */
    void onSellByQuantityKeyboardInputEvent(Boolean isConfirm, NUIKeyDialog dialog, String code) {
        if (!isConfirm) {
            dialog.dismiss();
            return;
        }
        if (code == null || code.equals("") || code.contains(".")) {
            return;
        }
        int number = Integer.valueOf(code);
        if (number <= 0) {
            return;
        }
        try {
            if (tempGoods == null) {
                tempGoods = new GoodsModel();
            }
            int priceUnitA = printCommdity.getPriceUnitA();
            String pluNo = printCommdity.getPluNo();
            tempGoods.setGoodsId(pluNo);
            tempGoods.setUnitId(priceUnitA);
            Total total = setTotalandDisdiscountPrice(tempGoods, "1");
            //????????????
            total.setTotal(String.format("%.2f", printCommdity.getUnitPriceA() * number));
            //????????????
            WintecServiceSingleton.getInstance().printImgLable(printCommdity, total, number, isKg, tradeMode, aiPosAllView.getTitleView().getTare(), mNet);
        } catch (Exception e) {
            XLog.e(e);
        }
        clearMode();
        dialog.dismiss();

    }


    /**
     * ????????????????????????
     *
     * @param goodsModel
     */
    void onGoodsItemClick(GoodsModel goodsModel, int position, boolean isRecommond) {
        // ???KG???
        if (net <= 0.0f && goodsModel.getUnitId() == 0 && !priceChangeFlagFoever) {
            aiTipDialog.showFail("??????????????????", aiPosAllView);
            return;
        }
        if (!isST && !isZero) {
//            aiTipDialog.showFail("???????????????", aiPosAllView);
            itemReadyToPrint = goodsModel;
            isItemReadyToPrint = true;
            return;
        }
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getOperatingView().clearInput();
        } else {
            aiPosAllView.getTitleView().clearInput();
        }
        if (!priceChangeFlagFoever) {
            if (detectRe != null && !isZero) {
                int detectResult = 0;
                preTaskId = taskId;
                Const.KEY_GET_TASK_ID = taskId;
                Boolean flagNull = this.detectRe.getTaskId() == null;
                List<String> detectReGoodsIds = this.detectRe.getGoodsIds();
                String trueGoodsId = goodsModel.getGoodsId();
                Boolean flagContains = (detectReGoodsIds.contains(trueGoodsId));
                if (flagNull || !flagContains) {
                    api_confirmResult(this.taskId, goodsModel.getGoodsId(), goodsModel.getGoodsName(), false);
                    detectResult = -1;
                    XLog.d("?????????");
                } else {
                    long current_time = System.currentTimeMillis();
                    if (detectReGoodsIds.size() > 0 && detectReGoodsIds.get(0).equals(trueGoodsId) && !Const.keyFromInput) {
                        detectResult = 3;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                api_confirmResult(taskId, goodsModel.getGoodsId(), goodsModel.getGoodsName(), true);
                            }
                        }).start();

                    } else {
                        detectResult = 1;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                api_confirmResult(taskId, goodsModel.getGoodsId(), goodsModel.getGoodsName(), false);
                            }
                        }).start();
                    }
                    Const.keyFromInput = false;
                    long current_time2 = System.currentTimeMillis();
                    XLog.d("???????????????????????????:" + (current_time2 - current_time));
                }
                int detectResultNew = detectResult;
                try {
                    if (NetWorkUtil.isNetworkAvailable(this)) {
//                        int finalDetectResult = detectResult;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                dealInsert(goodsModel, detectResultNew);
                                try {
                                    dealInsert(goodsModel, detectResultNew);
//                                    upImg(taskId, goodsModel.getGoodsName(), goodsModel.getGoodsId(), finalDetectResult);
                                } catch (Exception e) {
                                    XLog.e(e);
                                }
                            }
                        }).start();
                    }
//                } catch (InvalidKeySpecException e) {
//                    e.printStackTrace();
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dealInsert(goodsModel, 5);
            }
        }


        //????????????
        if (discountFlag) {
            if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !goodsModel.getDisCount()) {
                aiTipDialog.showFail("????????????????????????", aiPosAllView);
            } else {
                discountKeyboard.show();
            }
        } else if (tempPriceFlag) { //????????????
            if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !goodsModel.getTempPrice()) {
                aiTipDialog.showFail("????????????????????????", aiPosAllView);
            } else {
                //??????????????????
                tempPriceKeyboard.showWithParams(goodsModel.getPrice());
//                tempPriceKeyboard.show();
            }
        } else if (priceChangeFlag) {
            if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !goodsModel.getTempPrice()) {
                aiTipDialog.showFail("????????????????????????", aiPosAllView);
            } else {
                commdityDisPrice = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
                priceKeyboard.show();
            }
        } else {
            printGood();
        }


    }

    private void upImg(String taskId, String itemName, String itemCode, int w) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // ????????????????????????????????????????????????
        // ????????????
        List<MultipartBody.Part> params = new ArrayList<>();
        params.add(MultipartBody.Part.createFormData("name", itemName));
        params.add(MultipartBody.Part.createFormData("weight", w + ""));
        params.add(MultipartBody.Part.createFormData("terId", Const.getSettingValue(Const.KEY_TER_CODE)));
        params.add(MultipartBody.Part.createFormData("branchId", Const.getSettingValue(Const.KEY_BRANCH_ID)));
        params.add(MultipartBody.Part.createFormData("itemCode", itemCode));
        params.add(MultipartBody.Part.createFormData("sn", Const.getSettingValue(Const.SN)));
        int keyLength = 1024;
        String publicKeyS = UserManager.getInstance().getPublicKey();
        RSAPublicKey publicKeySR = RSAUtils.getPublicKey(publicKeyS);
        String pb = RSAUtils.publicEncrypt("e644c6e5983cad92dbe4ad7f9807a018," + DateUtils.getTime(DateUtils.getCurrentTime()), publicKeySR);
        if (detectRe == null) {
            return;
        }
        // todo ??????token,abc??????????????????????????????
        String token = pb;
        // ????????????
//        File file = FileUtil.getFile(detectRe.getCropBitmap());
        if (api_getScaleBitmap(false).getRaw() == null) {
            return;
        }

        File file;
        try {
            file = new File(WtAISDK.api_getLocalImagePath(detectRe.getTaskId()));
            if (file == null) {
                return;
            }
        } catch (Exception e) {
            return;
        }
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("imgFile", file.getName(), fileRequestBody);
        // ??????
        Disposable disposable = HttpRequestClient
                .getRequest()
                .uploadImage(params, part, token)
                .compose(schedulerProvider.applySchedulers())
                .subscribe(result -> {
                }, throwable -> {
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void inputListenEvent(String text) {
        if (text == null) {
            return;
        }
        resetPage();
        if (text.equals("")) {
            aiPosAllView.getListView().clear();
            aiPosAllView.getListView().idle();
            return;
        }

        text = text.toLowerCase();
        List<PluDto> list = PluDtoDaoHelper.getCommdityBySearchKey(text, aiPosAllView.mIsLandscape ? (allGoodsOneTagFlag ? 8 : 10) : 6, currentItemPage);
        calTotalPage(text, false);
        aiPosAllView.getListView().clear();
        if (list.size() <= 0) {
            aiPosAllView.getListView().idle();
            return;
        }
        aiPosAllView.getListView().showing();
        for (PluDto co : list) {
            aiPosAllView.getListView().addData(co.parseNet(mNet, tradeMode, discount, tempPrice, tempTotal));
        }

    }

    /**
     * ????????????
     */
    void itemRegister() {
        codeKeyboard.show();
    }

    /**
     * ?????????????????????
     */
    void hiddenEvent() {
        nuiBottomSheet.createQuitBottomSheet(new NUIBottomSheet.ClickListener() {
            @Override
            public void onItemClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                if (position == 0) {
                    if (Const.getSettingValue(Const.KEY_LABEL_TYPE).equals("")) {
                        Const.setSettingValue(Const.KEY_LABEL_TYPE, "1");
                    } else if (Const.getSettingValue(Const.KEY_LABEL_TYPE).equals("0")) {
                        Const.setSettingValue(Const.KEY_LABEL_TYPE, "1");
                    } else {
                        Const.setSettingValue(Const.KEY_LABEL_TYPE, "0");
                    }
                    aiTipDialog.showSuccess("????????????", aiPosAllView);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        }, "???????????????????????????").show();
    }

    private boolean isPreSetTare = false;   // ??????????????????

    /**
     * ??????
     */
    void setZero() {
        WintecServiceSingleton.getInstance().setZero();
    }

    /**
     * ??????
     */
    void setTare() {
        isPreSetTare = false;
        WintecServiceSingleton.getInstance().setTare();
    }


    /**
     * ?????????????????????
     *
     * @param isConfirm
     * @param dialog
     * @param commdity
     */
    void itemRegisterEvent(boolean isConfirm, NUIKeyDialog dialog, PluDto commdity) {
        //   mPresenter.itemRegisterEvent(mContext, evo, isConfirm, dialog, commdity, aiTipDialog, aiposCore);
    }


    float prePrintNet = 0f;
    float preNet = 0f;
    boolean isAfterPrint = false;
    //    boolean isCanDetectWithoutZero = false;
    boolean isZeroAfterPrint = true;
    //???????????????net
    float[] lastNet = new float[2];
    int index = 0;
    boolean ol = false;

    /**
     * ?????????
     *
     * @param net
     * @param tare
     * @param status
     */
    public void scalesListener(float net, float tare, String status) {
//        System.out.println(status+"  -------------    "+net);
        scaleStatus = status;
        if (net <= 0) {
            this.net = 0;
            mNet = 0;
        } else {
            this.net = net;
            mNet = net;
        }
        if (status.equals("")) {
            return;
        }
        int point = Integer.valueOf(Const.getSettingValue(Const.WEIGHT_POINT));
        aiPosAllView.getTitleView().setTare(CommUtils.Float2String(tare, point), isPreSetTare);
        mPresentation.setWeight(CommUtils.Float2String(net, point));
        if (status.equals("ST")) {
            aiPosAllView.getTitleView().setWeight(CommUtils.Float2String(net, point), 1);
            isST = true;
            isZero = false;
            //????????????
            if (isCanRefreshTotal) {
                aiPosAllView.getTitleView().setScalesStatusStable();
                scalesHandler.sendEmptyMessage(FLUSH_TOTAL);
            }
            //????????????
            if (isCanPrint.get() && mNet >= 0.030f) {
                isCanPrint.set(false);
//                Log.i("Test","????????????????????????");
//                netChangeCount = 0;
                printSendScale();
            }
            //?????????????????????
            if (isItemReadyToPrint) {
                isItemReadyToPrint = false;
                if (itemReadyToPrint != null) {
                    disTempModel = itemReadyToPrint;
                    onGoodsItemClick(itemReadyToPrint, 0, false);
                }

            }
            //????????????
            if (!isZeroAfterPrint && isAfterPrint) {
                isAfterPrint = false;
                isZeroAfterPrint = true;
//                Log.i("test","????????????"+net+","+prePrintNet);
                isCanDectect.set(false);
                // ????????????
//                scalesHandler.sendEmptyMessage(SCALES_DETECT);
            }
            wendingNet = mNet;

            if (mNet < weight / 1000) {
                isCanDectect.set(true);
            }

        } else if (status.equals("EM")) {
            isCanPrint.set(true);
            isZero = true;
            isAfterPrint = false;
//            isCanDetectWithoutZero = false;
            isZeroAfterPrint = true;
            aiPosAllView.getTitleView().setWeight(CommUtils.Float2String(net, point) + "", 1);
            aiPosAllView.getTitleView().setScalesStatusZero();
            //title???????????????????????? ??????
            if (isCanClear.get()) {
                itemReadyToPrint = null;
                isItemReadyToPrint = false;
                isCanClear.set(false);
                aiPosAllView.getTitleView().setGoods(null, "0", 0, null, isKg);

                mPresentation.setGoods(null, "0", 0, "0", isKg, aiPosAllView.getTitleView().getTareBySecend());


                if (aiPosAllView.mIsLandscape) {
                    aiPosAllView.getOperatingView().clearInput();
                } else {
                    aiPosAllView.getTitleView().clearInput();
                }

                isCanDectect.set(true);
            }
            isST = false;
        }
        // ??????
        else if (status.equals("OL")) {
            isZero = true;
            aiPosAllView.getTitleView().setScaleOverload(CommUtils.Float2String(net, point));
            return;
        }
        // ??????
        else if (status.equals("LL")) {
            isZero = true;
            aiPosAllView.getTitleView().setScaleLoseload(CommUtils.Float2String(net, point));
            isCanDectect.set(true);
            return;
        }
        // ?????????
        else {
            isZero = false;
            isCanRefreshTotal = true;
            aiPosAllView.getTitleView().clearScalesStatus();
            /*netChangeCount++;
            if (netChangeCount >= 5) {
                isCanPrint.set(true);
                netChangeCount = 0;
            }*/

            aiPosAllView.getTitleView().setWeight(CommUtils.Float2String(net, point) + "", 0);
            isCanClear.set(true);
            isST = false;
            if (isAfterPrint && Math.abs(net - prePrintNet) > 0.15f) {
                isZeroAfterPrint = false;
            }

        }

        // ???????????????,??????????????????
        if (net >= weight / 1000 && isCanDectect.get() && !isPrint.get()) {
//            isCanDetectWithoutZero = false;
            isCanDectect.set(false);
            // ????????????
            scalesHandler.sendEmptyMessage(SCALES_DETECT);
        }

        preNet = net;
    }

    //??????????????????
    private void printSendScale() {
        if (printDialog.getPrint()) {
//            PluDto commdity = printDialog.getCommdity();
//            String disPrice = printDialog.getTotal().getPrice();
//            float totalPrice = Float.parseFloat(disPrice) * mNet;
//            int totalPricePoint = Integer.valueOf(Const.getSettingValue(Const.TOTAL_PRICE_POINT));
//            String total = CommUtils.priceToString(Float.valueOf(CommUtils.Float2String(totalPrice, totalPricePoint)));
//            printDialog.setTotalandWeight(mNet + "", total);
            Total total = setTotalandDisdiscountPrice(printCommdity.parse(), "1");
            printDialog.setTotalandWeight(mNet + "", total.getPrice(), total.getTotal());
            dealInsert(printCommdity.parse(), 5);
            mPresentation.setGoods(printCommdity.parse(), mNet + "", count, total.getTotal(), isKg, aiPosAllView.getTitleView().getTareBySecend());
            if (aiPosAllView.mIsLandscape) {
                ThreadPoolManagerUtils.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        WintecServiceSingleton.getInstance().printImgLable(printCommdity, total, 1, isKg, tradeMode, aiPosAllView.getTitleView().getTare(), mNet);
                    }
                });
            } else {
                String barcode = WintecServiceSingleton.getInstance().createBarCode(printCommdity, total.getTotal(), mNet, 1, total.getPrice());
                comIO.send(barcode + "\r");
            }
        }
    }


    /**
     * ??????????????????
     */
    void showDeviceInfo() {
        showFullScreenPopupWindow();
    }

    /**
     * ?????????????????????
     */
    void showFullScreenPopupWindow() {
        //       mPresenter.FullScreenPopupWindow(mContext, evo, aiTipDialog);
    }

    @Override
    public void showDealInsert(String id) {
//        if (!TextUtils.isEmpty(id)) {
//            String image = EvoCore.getInstance(mContext).getImage().getImage();
//            Rect rect = EvoCore.getInstance(mContext).getCalib();
//            byte[] decode = Base64.decode(image, Base64.DEFAULT);
//            Bitmap preBmp = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//
//            File file = FileUtil.getFile(preBmp);
//            mPresenter.dealImg(file, id);
//        }
        //  ToastUtils.showToast("????????????");
    }

    @Override
    public void showDealImg(String id) {
    }

    @Override
    public void showUpdatePriceAll() {
//        isNewData = false;
//        for (int i = 0; i < commdityListByNetFlag.size(); i++) {
//            Commdity commdity = commdityListByNetFlag.get(i);
//            commdity.setNetFlag(0);
//            CommdityHelper.updateCommdity(commdity);
//        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        quitBottomSheet.show();
    }

    @Override
    public void finish() {
        WintecServiceSingleton.getInstance().scaleClose();
        if (comIO != null) {
            comIO.close();
        }
        super.finish();
    }

    /**
     * ????????????
     *
     * @param code
     * @param dialog
     */
    public boolean handleDiscount(String code, NUIKeyDialog dialog) {
        // ????????????
        if (code == null || code.equals("")) {
            return false;
        }
        // ???????????????????????????
        if (!code.contains(".")) {
            if (Integer.parseInt(code) == 0) {
                CommUtils.showMessage(mContext, "???????????????0");
                return false;
            }
            discount = Float.parseFloat(code.replaceAll("^(0+)", "")) / 10;
            if (discount <= 0 || discount >= 1) {
                CommUtils.showMessage(mContext, "?????????????????????");
                discount = 1f;
                return false;
            }
        }
        // ????????????????????????
        else {
            if (!code.startsWith(".")) {
                if (CommUtils.isNumeric(code)) {
                    discount = Float.parseFloat(code) / 10;
                    if (discount <= 0 || discount >= 1) {
                        CommUtils.showMessage(mContext, "?????????????????????");
                        discount = 1f;
                    }
                } else {
                    CommUtils.showMessage(mContext, "?????????????????????");
                }
            } else {
                CommUtils.showMessage(mContext, "?????????????????????");
            }
        }
        dialog.dismiss();
        tradeMode = MODE_DISCOUNT_TRADE;
        if (!isContinuityPrintFlag.get()) {
            printGood();
        }
        scalesHandler.sendEmptyMessage(FLUSH_TOTAL);
        return true;
    }

    public void clearMode() {
        if (isContinuityPrintFlag.get()) {
            XLog.i("????????????????????????");
            return;
        }
        // ??????????????????
        tradeMode = MODE_NORMAL_TRADE;
        // ???????????????
        discount = 1f;
        // ?????????????????????
        tempPrice = 0;
        // ???????????????
        tempTotal = 0;
    }


    /**
     * ??????????????????
     *
     * @param price
     * @param dialog
     */
    public boolean handleTempPrice(String price, NUIKeyDialog dialog, int param) {
        if (!opinionPrice(price)) {
            return false;
        }
        dialog.dismiss();
        //CommUtils.showMessage(mContext, "?????????????????????????????????");
        if (param == 0) {
            tradeMode = MODE_CHANGE_PRICE_TRADE;
            tempPrice = Float.parseFloat(price);
        } else {
            tradeMode = MODE_CHANGE_TOTAL_TRADE;
            tempTotal = Float.parseFloat(price);
        }
        if (!isContinuityPrintFlag.get()) {
            printGood();
        }

        scalesHandler.sendEmptyMessage(FLUSH_TOTAL);
        return true;
    }


    public void itemCodeInput(String code, NUIKeyDialog dialog) {
        if (code == null || code.equals("")) {
            CommUtils.showMessage(mContext, "?????????????????????");
            return;

        }
        //????????????
        commdityDisPrice = PluDtoDaoHelper.getCommdityByScalesCode(code);
        if (commdityDisPrice != null) {
            goodName = commdityDisPrice.getNameTextA();
        }
        if (commdityDisPrice == null) {
            CommUtils.showMessage(mContext, "??????????????????");
            return;
        }
        dialog.dismiss();
        permanentPriceChange();
        setDisPrice();
        //CommUtils.showMessage(mContext, "?????????????????????????????????");
        //aiposUI.getListView().setDescribeText("???????????????????????????"+tempPrice+"???/KG");
    }

    /**
     * ??????
     */
    public void setDisPrice() {
        priceKeyboard.show();
    }

    /**
     * ????????????
     *
     * @param price
     * @param dialog
     */
    public void handlePrice(String price, NUIKeyDialog dialog) {
        if (!opinionPrice(price)) {
            return;
        }
        if ("".equals(commdityDisPrice.getPluNo()) || commdityDisPrice.getPluNo() == null) {
            CommUtils.showMessage(mContext, "?????????????????????");
        } else {
            dialog.dismiss();
            if (isKg) {
                disCountAppand(3, Float.parseFloat(price) + "", "0", commdityDisPrice);
                commdityDisPrice.setUnitPriceA(Float.parseFloat(price));
            } else {
                disCountAppand(3, Float.parseFloat(price) * 2 + "", "0", commdityDisPrice);
                commdityDisPrice.setUnitPriceA(Float.parseFloat(price) * 2);
            }
            updatePriceOnServer(dialog, commdityDisPrice);

        }
    }


    //????????????
    private Boolean opinionPrice(String price) {
        if (price == null || price.equals("")) {
            return false;
        }
        // ??????????????????????????????
        if (!price.contains(".")) {
            price = price.replaceAll("^(0+)", "");
            if (price == null || price.equals("")) {
                price = "0";
            }
        } else {
            if (!price.startsWith(".")) {
                if (!CommUtils.isNumeric(price)) {
                    // ????????????????????????
                    CommUtils.showMessage(mContext, "?????????????????????");
                    return false;
                }

            } else {
                // ????????????????????????
                CommUtils.showMessage(mContext, "?????????????????????");
                return false;
            }
        }
        if (Float.parseFloat(price) == 0) {
            CommUtils.showMessage(mContext, "?????????????????????");
            return false;
        }
        return true;
    }


    /**
     * ??????????????????
     */
    void updatePriceOnServer(NUIKeyDialog dialog, PluDto commdity) {

        PluDtoDaoHelper.updateCommdity(commdity);
        aiTipDialog.dismiss();
        aiTipDialog.showSuccess("????????????", aiPosAllView);
        isNewData = true;


    }

    /*
     * ??????????????????
     */
    private void sendScale(int unitId, GoodsModel goodsModel, String count, Total total) {
        PluDto co;
        if ("????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(this)) {
            if (currentCo == null) {
                co = PluDtoDaoHelper.getCommdityByScalesCodeOnline(goodsModel.getGoodsId());
            } else {
                co = currentCo;
            }
        } else {
            co = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
        }
        if (co == null) {
            aiTipDialog.showFail("??????????????????", aiPosAllView);
            return;
        }
        //??????????????????
        if ("1".equals(Const.getSettingValue(Const.VOIDCE_BROADCAST_FLAG))) {
            TTSpeaker.getInstance(mContext).speak(co.getNameTextA() + " " + total.getTotal() + "???");
        }
        switch (Const.getSettingValue(Const.KEY_MODE)) {
            case "????????????":
                prePrintNet = mNet;
                if (allGoodsOneTagFlag) {
                    String code = "";
                    try {
                        code = WintecServiceSingleton.getInstance().getTagCode(co, total.getTotal(), mNet, Integer.valueOf(count), total.getPrice());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    goodsModel.setTagCode(code);
                    goodsModel.setNet(mNet + "");
                    goodsModel.setTotal(total.getTotal());
                    goodsModel.setPrice(total.getPrice());

                    aiPosAllView.getAiPosAccountList().addData(goodsModel);
                    mPresentation.addData(goodsModel);
                } else {
                    //??????????????????
                    float unitprice = co.getUnitPriceA();
                    co.setUnitPriceA(Float.parseFloat(total.getPrice()));
                    mPresentation.setGoods(co.parse(), mNet + "", Integer.valueOf(count), total.getTotal(), isKg, aiPosAllView.getTitleView().getTareBySecend());
                    co.setUnitPriceA(unitprice);

//                    Transaction transaction =new Transaction();
//                    transaction.setGoodName(co.getNameTextA());
//                    transaction.setPLU(co.getPluNo());
//                    transaction.setItemNo(co.getItemNo());
//                    transaction.setNet(mNet);
//                    transaction.setTotalPrice(Double.parseDouble(total.getTotal()));
//                    transaction.setTransactionType(0);
//                    transaction.setUnitPrice(Double.parseDouble(total.getPrice()));
//                    transaction.setCreateDate(1111111);

//                    TransactionHelper.insert(transaction);

                    ThreadPoolManagerUtils.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            WintecServiceSingleton.getInstance().printImgLable(co, total, Integer.valueOf(count), isKg, tradeMode, aiPosAllView.getTitleView().getTare(), mNet);

                        }
                    });
                }
                // ???????????????????????????????????????????????????
                if (tempPriceFlag) {
                    if ("1".equals(Const.getSettingValueWithDef(Const.ONE_TEMPPRICE_FLAG, "0"))) {
                        aiPosAllView.getOperatingView().setButtonSelected(4);
                        tempPriceFlag = false;
                    }
                }
                if (discountFlag) {
                    if ("1".equals(Const.getSettingValueWithDef(Const.ONE_DISCOUNT_FLAG, "0"))) {
                        aiPosAllView.getOperatingView().setButtonSelected(5);
                        discountFlag = false;
                    }
                }
                break;
            case "POS20":
                String barcode = WintecServiceSingleton.getInstance().createBarCode(co, total.getTotal(), net, Integer.valueOf(count), total.getPrice());
                comIO.send(barcode + "\r");
                break;
            case "???????????????(ttySAC4)":
                if (comIO == null) {
                    this.comIO = new ComIO("/dev/ttySAC4", 9600);
                }
                if (!comIO.isOpen()) {
                    comIO.open();
                }
                String barcode1 = WintecServiceSingleton.getInstance().createBarCode(co, total.getTotal(), net, Integer.valueOf(count), total.getPrice());
                comIO.send(barcode1 + "\r");
                break;
            case "???????????????(ttySAC3)":
                if (comIO == null) {
                    this.comIO = new ComIO("/dev/ttySAC3", 9600);
                }
                if (!comIO.isOpen()) {
                    comIO.open();
                }
                String barcode2 = WintecServiceSingleton.getInstance().createBarCode(co, total.getTotal(), net, Integer.valueOf(count), total.getPrice());
                comIO.send(barcode2 + "\r");
                break;
        }
        String settingValue = Const.getSettingValue(Const.RESULT_DISPLAY);
        if ("1".equals(settingValue)) {
            imgDialog.show(goodsModel.getGoodsImg(), co.getNameTextA(), total.getTotal(), total.getPrice(), unitId == 0 ? String.valueOf(net) : count, unitId, isKg);
        }
        PluDto commdity = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());

        if (commdity.get_id() == null) {
            PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByItemCode(goodsModel.getGoodsId());
            if (commdityByScalesCode != null)
                commdity.set_id(commdityByScalesCode.get_id());
        }

        if (commdity.getClick() == null) {
            commdity.setClick(1);
        } else {
            // if(commdity.getClick() == 2147483647)
            if (commdity.getClick() < 2000000000) {
                commdity.setClick(1 + commdity.getClick());
            }
        }
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getOperatingView().clearInput();
        } else {
            aiPosAllView.getTitleView().clearInput();
        }

        PluDtoDaoHelper.updateCommdity(commdity);
//        if (aiPosAllView.mIsLandscape) {
//            mPresenter.updateRecommondList(isKg);
//        }
        resetPage();
        clearMode();
    }

    /**
     * ????????????
     *
     * @param file
     */
    private void deleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }

    public void clearRecommondList() {
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getQuickSelectView().clearRecommondList();
        }

    }

    @Override
    public void addRecommondItem(GoodsModel goodsModel) {
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getQuickSelectView().addRecommondData(goodsModel);
        }

    }

    @Override
    public void showUpImgSuccess() {
        ToastUtils.showToast("????????????");
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
        aiTipDialog.showSuccess("????????????", aiPosAllView);
        nuiBottomSheet.dismiss();
    }

//    private void continuousClick(int count, long time) {
//        //??????????????????????????????????????????
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//        //???????????????????????????
//        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
//        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
//            mHits = new long[COUNTS];//?????????????????????
//            resStudyKeyboard.show();
//        }
//    }

    /**
     * ????????????
     *
     * @param ytare
     * @param dialog
     */
    public void handleYtare(String ytare, NUIKeyDialog dialog) {
        if (ytare == null || ytare.equals("")) {
            return;
        }
        try {
            int tare = Integer.parseInt(ytare);
            if (tare < 0 || tare >= 5998) {
                throw new Exception();
            }
            if (tare != 0) {
                isPreSetTare = true;
            } else {
                isPreSetTare = false;
            }
            WintecServiceSingleton.getInstance().setYTare((tare * 1.0f) / 1000f);
            dialog.dismiss();
            aiTipDialog.showSuccess("??????????????????", aiPosAllView);
        } catch (Exception e) {
            aiTipDialog.showFail("?????????????????????", aiPosAllView);
        }
    }

    private void insert(List<DataBean> dataBeans) {
//        ThreadPoolManagerUtils.getInstance().execute(() ->{
        dataBeans.forEach(item -> {
            if (item instanceof Plu) {
                Plu plu = (Plu) item;
                PluDto commdity = new PluDto(plu);
                String pluName = plu.getNameTextA();
                //???????????????????????????
//                pluName = pluName.replaceAll("[a-zA-Z]", "");
                commdity.setInitials(PinyinUtil.getFirstSpell(pluName));
                commdity.setBranchId(Const.getSettingValue(Const.KEY_BRANCH_ID));
                PluDto commdityByItemCode = PluDtoDaoHelper.getCommdityByScalesCodeLocal(commdity.getPluNo());
                if (commdityByItemCode == null) {
                    PluDtoDaoHelper.insertCommdity(commdity);
                } else {
                    // CommdityHelper.deleteCommdityByKey(commdityByItemCode.get_id());
                    commdity.set_id(commdityByItemCode.get_id());
                    commdity.setPreviewImage(commdityByItemCode.getPreviewImage());
                    PluDtoDaoHelper.updateCommdity(commdity);
                }
            } else if (item instanceof Acc) {
                Acc acc = (Acc) item;
                AccDtoHelper.deleteByAccNo(acc.accNo);
                AccDtoHelper.insert(new AccDto(acc));
            }

        });
    }

    private void insertbpluszsm(List<DataBean> dataBeans) {
        dataBeans.forEach(item -> {
            TraceCode traceCode = (TraceCode) item;
            TraceabilityCode traceabilityCode = new TraceabilityCode(traceCode);
            TraceabilityCodeHelper.insert(traceabilityCode);
        });

    }

    private void insertbpluszsmgbm(List<DataBean> dataBeans) {
//        GolCodePlu golCodePlu =(GolCodePlu)dataBeans;

    }

    @Override
    public void onResume() {
        try {
            weight = Float.parseFloat(Const.getSettingValue(Const.DELECT_WEIGHT));
        } catch (NumberFormatException e) {
            weight = 30;
            e.printStackTrace();
        }
        ;
        isKg = "kg".equals(Const.getSettingValue(Const.WEIGHT_UNIT));
        try {
            maxDetectNum = Integer.parseInt(Const.getSettingValue(Const.KEY_GOODS_COUNT));
        } catch (Exception e) {
            maxDetectNum = 5;
        }
        super.onResume();
    }

    private Total setTotalandDisdiscountPrice(GoodsModel goodsModel, String count) {
        int flag = 0;
        PluDto co;
        if (Const.NetworkReachable && Const.fromClick && "????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(this)) {
            if (Const.pulAndPricesMapOk && Const.pulAndPricesMap.containsKey(goodsModel.getGoodsId())) {
                co = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
                try {
                    co.setUnitPriceA(Const.pulAndPricesMap.get(goodsModel.getGoodsId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Const.pulAndPricesMap.clear();
                Const.pulAndPricesMapOk = false;
            } else {
                co = PluDtoDaoHelper.getCommdityByScalesCodeOnline(goodsModel.getGoodsId());
                if (co == null) {
                    co = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
                }
            }
            currentCo = co;
        } else {
            co = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
            currentCo = co;
        }
        if (co == null) {
            return null;
        }
        float price = 0.00f;
        int unitPricePoint = Integer.valueOf(Const.getSettingValue(Const.UNIT_PRICE_POINT));
        int totalPricePoint = Integer.valueOf(Const.getSettingValue(Const.TOTAL_PRICE_POINT));
        if (tradeMode == MODE_DISCOUNT_TRADE && discount < 1 && discount > 0) {
            price = Float.parseFloat(CommUtils.Float2String(co.getUnitPriceA() * discount, unitPricePoint));
            flag = 2;
        } else if (tradeMode == MODE_CHANGE_PRICE_TRADE) {
            price = Float.parseFloat(CommUtils.Float2String(tempPrice, unitPricePoint));
            flag = 1;
        } else {
            price = Float.parseFloat(CommUtils.Float2String(co.getUnitPriceA(), unitPricePoint));
        }

        float total = 0;
        if (goodsModel.getUnitId() == 0) {
            total = price * net;
            disCountAppand(flag, price + "", net + "", co);
        } else {
            total = price * Integer.valueOf(count);
            disCountAppand(flag, price + "", count, co);

        }
        String discountPrice = String.format("%.2f", price);
        if (tradeMode == MODE_CHANGE_TOTAL_TRADE) {
            total = tempTotal;
            discountPrice = String.format("%.2f", total / net);
        }
        String mTotal = CommUtils.priceToString(Float.valueOf(CommUtils.Float2String(total, totalPricePoint)));
        return new Total(mTotal, discountPrice, tradeMode);
    }
//        int flag = 0;
//        PluDto co;
//        if ("????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(this)) {
//            co = PluDtoDaoHelper.getCommdityByScalesCodeOnline(goodsModel.getGoodsId());
////            Log.i("test",co.getPluNo()+co.getNameTextA());
//        } else {
//            co = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
//        }
//        if (co == null) {
//            return null;
//        }
//        float price = 0.00f;
//        int unitPricePoint = Integer.valueOf(Const.getSettingValue(Const.UNIT_PRICE_POINT));
//        int totalPricePoint = Integer.valueOf(Const.getSettingValue(Const.TOTAL_PRICE_POINT));
//        if (tradeMode == MODE_DISCOUNT_TRADE && discount < 1 && discount > 0) {
//            price = Float.parseFloat(CommUtils.Float2String(co.getUnitPriceA() * discount, unitPricePoint));
//            flag = 2;
//        } else if (tradeMode == MODE_CHANGE_PRICE_TRADE) {
//            price = Float.parseFloat(CommUtils.Float2String(tempPrice, unitPricePoint));
//            flag = 1;
//        } else {
//            price = Float.parseFloat(CommUtils.Float2String(co.getUnitPriceA(), unitPricePoint));
//        }
//
//        float total = 0;
//        if (goodsModel.getUnitId() == 0) {
//            total = price * net;
//            disCountAppand(flag, price + "", net + "", co);
//        } else {
//            total = price * Integer.valueOf(count);
//            disCountAppand(flag, price + "", count, co);
//
//        }
//        String discountPrice = String.format("%.2f", price);
//        if (tradeMode == MODE_CHANGE_TOTAL_TRADE) {
//            total = tempTotal;
//            discountPrice = String.format("%.2f", total / net);
//        }
//        String mTotal = CommUtils.priceToString(Float.valueOf(CommUtils.Float2String(total, totalPricePoint)));
//        return new Total(mTotal, discountPrice, tradeMode);
//    }

    private void popupsShow(View v, String[] listItems, GoodsModel model) {
        List<String> data = new ArrayList<>();
        Collections.addAll(data, listItems);
        ArrayAdapter adapter = new ArrayAdapter<>(mContext, R.layout.simple_list_item, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    updatePrevirwImg(model);
//                    Toast.makeText(mContext, "??????????????????????????????", Toast.LENGTH_SHORT).show();
//                } else
                if (i == 0) {
                    PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(model.getGoodsId());
                    printCommdity = commdityByScalesCode;
                    isContinuityPrintFlag.set(true);
                    if (discountFlag) {
                        if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !commdityByScalesCode.getDiscountFlagA()) {
                            aiTipDialog.showFail("????????????????????????", aiPosAllView);
                        } else {
                            discountKeyboard.show();
                        }
                    } else if (tempPriceFlag) {
                        if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !commdityByScalesCode.getPriceChangeFlagA()) {
                            aiTipDialog.showFail("????????????????????????", aiPosAllView);
                        } else {
                            tempPriceKeyboard.show();
                        }
                    } else {
                        if (commdityByScalesCode.getPriceUnitA() == 0) {
                            isPrint.set(true);
                            Total total = setTotalandDisdiscountPrice(model, "1");
                            printDialog.show(commdityByScalesCode, total, isKg);
                            printDialog.setTotalandWeight(mNet + "", total.getPrice(), total.getTotal());
                        } else {
                            lockByNumNumKeyboard.show();
                        }
                    }

                } else if (i == 1) {
                    PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(model.getGoodsId());
                    printCommdity = commdityByScalesCode;
                    Const.sellByNum = true;
                    sellByQuantityKeyboard.show();
                } else if (i == 2) {
                    PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(model.getGoodsId());
                    int unitFlag = commdityByScalesCode.getPriceUnitA();
                    if (unitFlag == 0) {
                        commdityByScalesCode.setPriceUnitA(1);
                    } else if (unitFlag == 1) {
                        commdityByScalesCode.setPriceUnitA(0);
                    }
                    PluDtoDaoHelper.updateCommdity(commdityByScalesCode);

                } else {
                    toResStudy(model.getGoodsId());
                }
//                aiPosAllView.getListView().clear();
//                aiPosAllView.getListView().idle();
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };
        mNormalPopup = QMUIPopups.listPopup(mContext,
                QMUIDisplayHelper.dp2px(mContext, 250),
                QMUIDisplayHelper.dp2px(mContext, 300),
                adapter,
                onItemClickListener)
                .animStyle(QMUIPopup.ANIM_GROW_FROM_RIGHT)
                .preferredDirection(QMUIPopup.DIRECTION_BOTTOM)
                .shadow(true)
                .offsetYIfTop(QMUIDisplayHelper.dp2px(mContext, 5))
                .skinManager(QMUISkinManager.defaultInstance(mContext))
                .onDismiss(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .show(v);
    }

    public int openLock(String[] deadends, String target) {
        return -1;
    }


    // ???????????????
//    private void initPresentation() {
//        MediaRouter mMediaRouter = (MediaRouter) mContext.getSystemService(Context.MEDIA_ROUTER_SERVICE);
//        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
//                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
//        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
//        if (mPresentation != null && mPresentation.getDisplay() != presentationDisplay) {
//            mPresentation.dismiss();
//            mPresentation = null;
//        }
//        if (mPresentation == null && presentationDisplay != null) {
//            mPresentation = new SecondScreenPresentation(mContext, presentationDisplay);
//            try {
//                mPresentation.show();
//            } catch (WindowManager.InvalidDisplayException ex) {
//                mPresentation = null;
//            }
//        }
//
//    }

    private void printGood() {
        if (disTempModel == null && isContinuityPrintFlag.get()) {
            return;
        }
        PluDto co = PluDtoDaoHelper.getCommdityByScalesCode(disTempModel.getGoodsId());
        Total total = setTotalandDisdiscountPrice(disTempModel, "1");
        if (co == null) {
            aiTipDialog.showFail("??????????????????", aiPosAllView);
            return;
        }
        if (co.getUnitPriceA() == 0.00f) {
            aiTipDialog.showFail("??????????????????", aiPosAllView);
            clearMode();
            return;
        }
        if (disTempModel.getUnitId() == 0) {
            isAfterPrint = true;
            sendScale(disTempModel.getUnitId(), disTempModel, "1", total);
            disTempModel.setPrice(total.getPrice());
            aiPosAllView.getTitleView().setGoods(disTempModel, mNet + "", count, total.getTotal(), isKg);
            clearMode();
        } else {
            tempGoods = disTempModel;
            numberKeyboard.showWithParams(1);
            return;
        }
        //????????????????????????????????????????????????????????????
        if (co.getUnitPriceA() != Float.valueOf(total.getPrice()) && "????????????".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
            co.setUnitPriceA(Float.valueOf(total.getPrice()));
            PluDtoDaoHelper.updateCommdity(co);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        WintecServiceSingleton.getInstance().bind();
    }

    @Override
    protected void onStop() {
        if (imgDialog.isShowing()) {
            imgDialog.dismiss();
        }
        WintecServiceSingleton.getInstance().unbind();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroy");
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                SoundManager.getInstance().playSound();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * ?????????????????????
     */
    private FileWriter initDisCountFile() throws IOException {
        String path = "";
        Boolean flag = false;
        File file;

        path = Environment.getExternalStoragePublicDirectory("disCount").getPath();
        file = new File(path);
        if (!file.exists()) {
            //???????????????
            file.mkdirs();
        }
        path = path + File.separator + DateUtils.getDate(null) + ".txt";

        file = new File(path);
        if (!file.exists()) {
            //????????????
            file.createNewFile();
        }
        FileWriter writer = null;
        // ????????????????????????????????????????????????????????????true??????????????????????????????
        writer = new FileWriter(file, true);
        return writer;
    }

    private void disCountAppand(int flag, String price, String netOrCount, PluDto pluDto) {
        if (flag == 0) {
            return;
        }
        if (fileWriter != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(DateUtils.getCurrentTime());
            stringBuilder.append(": ");
            stringBuilder.append(pluDto.getPluNo());
            stringBuilder.append("    ");
            stringBuilder.append(pluDto.getNameTextA());
            stringBuilder.append("    ");
            stringBuilder.append(pluDto.getItemNo());
            stringBuilder.append("    ");
            stringBuilder.append(pluDto.getUnitPriceA());
            stringBuilder.append("    ");
            if (pluDto.getPriceUnitA() == 0) {
                stringBuilder.append(netOrCount + "kg");
                stringBuilder.append("    ");
            } else {
                stringBuilder.append(netOrCount + "???");
                stringBuilder.append("    ");
            }
            switch (flag) {
                // ??????
                case 1:
                    stringBuilder.append("??????");
                    stringBuilder.append("    ");
                    break;
                // ??????
                case 2:
                    stringBuilder.append("??????");
                    stringBuilder.append("    ");
                    break;
                // ????????????
                case 3:
                    stringBuilder.append("????????????");
                    stringBuilder.append("  ");
                    break;
                default:
                    break;
            }
            stringBuilder.append(price);
            stringBuilder.append("  \n");
            try {
                fileWriter.append(stringBuilder.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void getPriceBypluList(List<String> plus) {
        Long s = System.currentTimeMillis();
        if (plus != null && plus.size() > 0) {
            String inputQuerySQL = Const.getSettingValue(Const.KEY_GET_DATA_SQL);
            int num = plus.size();
            String tempSql = inputQuerySQL.toUpperCase();
            StringBuffer queryOneByPLU = new StringBuffer();
            if (tempSql.contains("WHERE")) {
                int indexWHERE = tempSql.indexOf("WHERE");
                if (inputQuerySQL.length() > indexWHERE) {
                    queryOneByPLU.append(inputQuerySQL.substring(0, indexWHERE));
                }
            } else {
                queryOneByPLU.append(inputQuerySQL + " ");
            }
            if ("oracle".equals(Const.getSettingValue(Const.KEY_GET_DATA_DB))) {
                queryOneByPLU.append("WHERE PLU IN (");
            } else {
                queryOneByPLU.append("WHERE plu_no IN (");
            }

            for (int i = 0; i < num; i++) {
                String scalesCode = plus.get(i);
                if (i == (num - 1)) {
                    queryOneByPLU.append(scalesCode + ")");
                } else {
                    queryOneByPLU.append(scalesCode + ",");
                }
            }
            List query = null;
            try {
                query = DBUtil.Query(queryOneByPLU.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (query != null && query.size() >= 2 && query.get(0).equals("ok")) {
                List<HashMap<String, Object>> dataQuery = (List<HashMap<String, Object>>) query.get(1);
                try {
                    dataQuery.forEach(item -> {
                        if ("oracle".equals(Const.getSettingValue(Const.KEY_GET_DATA_DB))) {
                            Float price1 = item.get("PRICE1") != null ? Float.parseFloat(item.get("PRICE1") + "") : 0F;
                            String scalesCode = item.get("PLU") != null ? item.get("PLU") + "" : "0000";
                            Const.pulAndPricesMap.put(scalesCode, price1);
                        } else {
                            Float price1 = item.get("unit_price_a") != null ? Float.parseFloat(item.get("unit_price_a") + "") : 0F;
                            String scalesCode = item.get("plu_no") != null ? item.get("plu_no") + "" : "0000";
                            Const.pulAndPricesMap.put(scalesCode, price1);
                        }
                    });
                    Const.pulAndPricesMapOk = true;
                    Const.NetworkReachable = true;
                    Log.w("sql", "??????????????????  ?????? " + (System.currentTimeMillis() - s) + " ms  " + queryOneByPLU.toString());
                } catch (Exception e) {
                    Const.pulAndPricesMapOk = false;
                    Const.NetworkReachable = false;
                    e.printStackTrace();
                    Log.w("sql", "????????????????????????: " + e.toString());
                    XLog.e("????????????????????????: " + e);
                }
            }

        }
    }
}
