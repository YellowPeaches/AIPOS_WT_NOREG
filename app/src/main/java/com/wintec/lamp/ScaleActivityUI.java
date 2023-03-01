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

    public final static int MODE_NORMAL_TRADE = 1;    // 正常交易状态
    public final static int MODE_DISCOUNT_TRADE = 3;    // 下一单折扣状态
    public final static int MODE_CHANGE_PRICE_TRADE = 4;    // 下一单改价状态
    public final static int MODE_CHANGE_TOTAL_TRADE = 5;    // 下一单总价状态
    private final static int SCALES_CLEAR = 10;    // 清空秤盘
    private final static int SCALES_SHOW_WHITE = 11;    // 展示秤示数
    private final static int SCALES_SHOW_RED = 12;    // 展示秤示数
    private final static int SCALES_SHOW_PLU = 14;
    private final static int SCALES_DETECT = 13;    // 通知识别
    private final static int FLUSH_TOTAL = 15;    // 刷新总价
    public final static int SHOW_FAIL = 16;    // 展示失败
    public final static int CREATE_BARCODE_ERROR = 17;    // 条码生成错误


    private Logging logging;
    @BindView(R.id.aipos)
    AiPosAllView aiPosAllView;

    private float mNet;
    private PresentationWrapper mPresentation; //副屏显示
    private QMUIPopup mGlobalAction;
    private NUIKeyDialog printKeyboard;        // 商品码键盘
    private NUIKeyDialog codeKeyboard;        // 商品码键盘
    private NUIKeyDialog numberKeyboard;      // 数量键盘
    private NUIKeyDialog discountKeyboard;    // 折扣键盘
    private NUIKeyDialog itemCodeKeyboard;    // 商品码键盘
    private NUIKeyDialog priceKeyboard;       // 价格键盘
    private NUIKeyDialog tempPriceKeyboard;   // 价格键盘
    private NUIKeyDialog resStudyKeyboard;    // 重新学习键盘
    private NUIKeyDialog deleteOnePLUKeyboard;    // 重新学习键盘
    private QMUIBottomSheet quitBottomSheet;  // 退出确认底部菜单
    private NUIKeyDialog tareKeyboard;        // 皮重键盘
    private NUIKeyDialog lockByNumNumKeyboard;        // 锁定输入份数键盘
    private NUIKeyDialog sellByQuantityKeyboard;        // 根据数量卖，利群四方店特殊需求
    private AiTipDialog aiTipDialog;          // 等待框
    private PluDto printCommdity;           //打印变量
    private AtomicBoolean isContinuityPrintFlag = new AtomicBoolean(false);  // 连续打印标志
    private CompositeDisposable compositeDisposable;
    private BaseSchedulerProvider schedulerProvider;
    private float wendingNet = 0f;

    private ComIO comIO = null;
    private float net = 0;                // 秤重量
    private String scaleStatus = "US";    // 秤状态,重量稳定发送 ST，重量不稳定发送 US，超重发送 OL
    //  private ScalesObject scaleObject;
    private ScalesForTuoLiDuo scalesForTuoLiDuo;
    private String codeCollect = "";      // 记录要采集商品的编码
    private int collectCount = 0;         // 采集计数
    private boolean isCollect = false;    // 标识符,控制是否进行图像采集
    private AtomicBoolean isWritingCom = new AtomicBoolean(false);    // 标识符,控制是否正在写串口
    private AtomicBoolean isCanDectect = new AtomicBoolean(false);     // 标识符,控制能否识别
    private AtomicBoolean isCanClear = new AtomicBoolean(true);     // 标识符，控制是否可以清空秤盘
    private AtomicBoolean isCanReDetect = new AtomicBoolean(false);
    private AtomicBoolean isCanPrint = new AtomicBoolean(false);
    private AtomicBoolean isPrint = new AtomicBoolean(false);     // 标识符,是否连续打印

    private volatile boolean isCameraShow = false;   // 标识符，摄像头是否显示
    private String taskId = "";         // 识别任务ID
    private String preTaskId = "";     // 记录上一次任务ID
    private Context context;
    private GoodsModel tempGoods;
    private PluDto commdity_res;    //注册类
    private PluDto commdity_res_printer;    //注册类
    private NUIKeyDialog dialog_res;  //注册动画
    private float discount = 1f;         // 折扣
    private float tempPrice = 0;        // 临时改价 单价
    private float tempTotal = 0;         // 临时改价 总价
    private int tradeMode = 1;                         // 标识符,记录交易状态
    private PluDto commdityDisPrice; //改价实体
    private ImgDialog imgDialog;  //商品弹框
    private FormDialog formDialog;  //商品弹框
    private GoodsRegisterDialog goodsRegisterDialog;
    private Bitmap mBitmap;

    private NUIBottomSheet nuiBottomSheet;

    private String goodName;
    private boolean isNewData = false;//标记是否有未上传的改价数据
    private Disposable mDisposable;
    private List<Commdity> commdityListByNetFlag;
    private static final int PERIOD = 15 * 60 * 1000;
    private static final int DELAY = 15 * 60 * 1000;

    private boolean isST = false;
    private boolean isZero = false;
    private boolean isCanRefreshTotal = false;  // 控制是否可以刷新总价
    private boolean isItemReadyToPrint = false; // 标识是否有待打印商品
    private GoodsModel itemReadyToPrint = null; // 待打印商品

    //输入框搜索模块
    private String sPrice;
    private String registerCode;
    private String nameRegister;
    private boolean isNum;//判断是数字搜索还是字母搜索

    //日期选择器
    private TimePickerView pvTime;
    //识别结果
    private DetectResult detectRe;

    //累加
    private int scalesCount = 0;
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    private int netChangeCount = 0;
    //连续打印页面
    private PrintDialog printDialog;
    float weight;

    private int maxDetectNum = 5;

    private boolean isKg = "kg".equals(Const.getSettingValue(Const.WEIGHT_UNIT));
    private QMUIPopup mNormalPopup;

    private Canvas canvasTag;
    private Bitmap bitmap;
    //当前co,避免在线取数时重复查，减少耗时
    PluDto currentCo = null;

    public static boolean detectFlagOver = true;    //是否识别完成
    public static int quantityOfLockNum = 1;    //锁定连打几个

//    private ImageView imageView;

    // 改价折扣状态
    private boolean tempPriceFlag = false;
    private boolean discountFlag = false;
    private boolean priceChangeFlag = false;
    private boolean priceChangeFlagFoever = false;  //永久改价不学习
    //多品一签flag
    private boolean allGoodsOneTagFlag = false;
    private GoodsModel disTempModel;
    private Handler scalesHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@androidx.annotation.NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCALES_CLEAR:
                    XLog.d("清空秤盘");
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
                            // 按件,跳过
                        } else {
                            Total total = setTotalandDisdiscountPrice(data.get(i), "0");
                            aiPosAllView.getListView().getData().get(i).setTotal(total.getTotal());
                        }
                    }
                    // 通知刷新总价
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
                            aiTipDialog.dataLoading("正在加载识别数据", aiPosAllView, Const.DATA_LOADING_TIME);
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
                            XLog.i("上次识别还未完成:" + detectTime);
                        }
                        while (detectResult[0] == null && System.currentTimeMillis() - starttime < 460) {
                            detectCount++;
                        }
                        detectFlagOver = true;
//                        detectResult = api_Detect();

                        if (detectResult[0] == null) {
                            String detectTime = (System.currentTimeMillis() - starttime) + "ms";
                            XLog.i("识别超时:" + detectTime);
                            aiPosAllView.getListView().noResult();
                            return;
                        }
                        String detectTime = (System.currentTimeMillis() - starttime) + "ms";
                        XLog.i("识别耗时:" + detectTime + "-ResultCode:" + detectResult[0].getErrorCode() + " || GoodsIds:" + detectResult[0].getGoodsIds() + " || ModelIds:" + detectResult[0].getModelIds());

                        if (detectResult[0].getErrorCode() == 0) {
                            detectRe = detectResult[0];
                            if ("在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(context)) {
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

    // 当前搜索商品总数
    private int searchItemTotal = 0;
    // 当前搜索商品总页数
    private int searchItemTotalPage = 0;
    // 当前搜索商品页码
    private int currentItemPage = 0;
    // 每一页商品数量
    private int itemNumPerPage = 10;
    private FileWriter fileWriter;

    // 重置分页
    private void resetPage() {
        searchItemTotal = 0;
        searchItemTotalPage = 0;
        currentItemPage = 0;
        itemNumPerPage = allGoodsOneTagFlag ? 8 : 10;
    }

    // 计算总页数
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

                //搜索框
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
                // 请求识别
                scalesHandler.sendEmptyMessage(SCALES_DETECT);
            } else {
                aiTipDialog.showFail("请先放置商品", aiPosAllView);
            }
        });
        aiPosAllView.getOperatingView().getBtn_kb_print().setOnClickListener(r -> {
            // 多品一签流程
            if (allGoodsOneTagFlag) {
                //打印二维码
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
            // 正常打印流程
            if (!isNum) {
                return;
            }
            PluDto dto = PluDtoDaoHelper.getCommdityByScalesCode(aiPosAllView.getOperatingView().getKeyBoardEditText().getText().toString());
            if (dto == null) {
                aiTipDialog.showFail("未查询到商品", aiPosAllView);
                return;
            }
            if (!priceChangeFlagFoever) {
                if (taskId != null || "".equals(taskId)) {
                    if (!taskId.equals(detectRe.getTaskId())) {
                        XLog.tag("error").e("存在问题，任务id和sessionid不一致");
                    }
                    api_confirmResult(taskId, dto.parse().getGoodsId(), dto.parse().getGoodsName(), false);
                    XLog.i("未命中");
                    if (net <= 0.0f && dto.getPriceUnitA() == 0) {
                        aiTipDialog.showFail("重量不能为零", aiPosAllView);
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
                        XLog.d("点击确认花费" + (current_time2 - current_time));
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
                        // 页码减一
                        currentItemPage--;
                        refreshItemListBySearch(aiPosAllView.getOperatingView().getKeyBoardEditText().getText().toString());
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void nextPage() {
                        if (currentItemPage >= (searchItemTotalPage - 1)) {
                            return;
                        }
                        // 页码加一
                        currentItemPage++;
                        refreshItemListBySearch(aiPosAllView.getOperatingView().getKeyBoardEditText().getText().toString());
                    }
                });

    }


    /**
     * 通过关键字搜索刷新商品列表
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
     * 接收RxBus的传值
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
            Log.d("TAG", "RxBusActivity接收msg：" + s);
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
        //初始话购物车回调
        aiPosAllView.getAiPosAccountList().setCallBack(new AiPosAccountList.AccountCallBack() {
            @Override
            public void clearCallBack() {

            }

            @Override
            public void saleCallBack() {

            }
        });
        // 皮重键盘
        tareKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入皮重(克)", NUIKeyView.KEY_TYPE_CODE).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                handleYtare(code, dialog);
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        // 初始化操作栏
        // 置零
        aiPosAllView.getOperatingView().addOperatingBtn("置零", R.mipmap.icon_zero, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPreSetTare = false;
                WintecServiceSingleton.getInstance().setYTare(0);
                setZero();
            }
        });
        // 去皮
        aiPosAllView.getOperatingView().addOperatingBtn("去皮", R.mipmap.icon_tare, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTare();
            }
        });
        // 固定皮重
        if (aiPosAllView.mIsLandscape) {
            aiPosAllView.getOperatingView().addOperatingBtn("预置皮重", R.mipmap.icon_ytare, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tareKeyboard.show();
                }
            });
        }
        // 多品一签
        aiPosAllView.getOperatingView().addOperatingBtn("多品一签", R.mipmap.icon_duopinyiqian, new View.OnClickListener() {
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
                    aiTipDialog.showFail("请开启多品一签权限", aiPosAllView);
                }

            }
        });
        // 重新识别
        if (!aiPosAllView.mIsLandscape) {
            /*aiPosAllView.getOperatingView().addOperatingBtn("重新识别", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (net >= weight / 1000) {
                        isCanDectect.set(false);
                        // 请求识别
                        scalesHandler.sendEmptyMessage(SCALES_DETECT);
                    } else {
                        aiTipDialog.showFail("请先放置商品", aiPosAllView);
                    }
                }
            });*/
        }

        // 临时改价
        aiPosAllView.getOperatingView().addOperatingBtn("临时改价", R.mipmap.icon_linshigaijia, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(Const.getSettingValue(Const.KEY_TEMPPRICE_FLAG))) {
                    aiPosAllView.getOperatingView().setButtonSelected(4, 5, 6);
                    tempPriceFlag = !tempPriceFlag;
                    discountFlag = false;
                    priceChangeFlag = false;
                } else {
                    aiTipDialog.showFail("请开启临时改价权限", aiPosAllView);
                }

            }
        });

        // 折扣
        aiPosAllView.getOperatingView().addOperatingBtn("折扣", R.mipmap.icon_zhekou, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(Const.getSettingValue(Const.KEY_DISCOUNT_FLAG))) {
                    aiPosAllView.getOperatingView().setButtonSelected(5, 4, 6);
                    discountFlag = !discountFlag;
                    tempPriceFlag = false;
                    priceChangeFlag = false;
                } else {
                    aiTipDialog.showFail("请开启改折扣权限", aiPosAllView);
                }

            }
        });

        aiPosAllView.getOperatingView().addOperatingBtn("永久改价", R.mipmap.icon_yongjiugaijia, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("1".equals(Const.getSettingValue(Const.KEY_UPPRICE_FLAG))) {
                    aiPosAllView.getOperatingView().setButtonSelected(6, 5, 4);
                    priceChangeFlag = !priceChangeFlag;
                    discountFlag = false;
                    tempPriceFlag = false;
                    priceChangeFlagFoever = !priceChangeFlagFoever;
                } else {
                    aiTipDialog.showFail("请开启永久改价权限", aiPosAllView);
                }

            }
        });
        aiPosAllView.getOperatingView().addOperatingBtn("走纸", R.mipmap.icon_zouzhi, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadPoolManagerUtils.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        WintecServiceSingleton.getInstance().roll();

                        if ("批量取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) ||
                                "在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
                            getDataOnline();
                        }
                    }
                });

            }
        });
        // 操作栏输入框监听
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

        // 快捷栏点击
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
     * 判断是否是数字
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
     * 获得屏幕高度
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
     * dp转px
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
        // 变价日志初始化
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
//        logging.d("软件初始化");
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

        //  初始化传称
        initReceiveCommdity();
        // 初始化
        codeKeyboard = new NUIKeyDialog
                .Builder(context)
                .addKeyView("请输入商品编码", NUIKeyView.KEY_TYPE_CODE)
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
                                aiTipDialog.showFail("未查询到该商品", aiPosAllView);
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
                            Const.setSettingValue(Const.KEY_RECORD_COUNT, String.valueOf(5)); //商品注册数量
                            String key_record_count = Const.getSettingValue(Const.KEY_RECORD_COUNT);
                            goodsRegisterDialog.setTvGoodsName("正在采集\"" + name + "\"");
                            goodsRegisterDialog.setOnClickListener(new GoodsRegisterDialog.OnClickListener() {
                                @Override
                                public void onClick(int type) {
                                    switch (type) {
                                        case 1://拍照
                                            long curClickTime = System.currentTimeMillis();
                                            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                                                // 超过点击间隔后再将lastClickTime重置为当前点击时间
                                                lastClickTime = curClickTime;
                                                itemRegisterEvent(true, dialog_res, commdity_res);
                                            }
                                            break;
                                        case 2://提交
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
        // 折扣键盘
        discountKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入折扣", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
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
        //锁定按份打印个数
        lockByNumNumKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入数量", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                if (code.contains(".")) {
                    Toast.makeText(mContext, "请输入整数", Toast.LENGTH_SHORT).show();
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
        //重新学习
        resStudyKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入打称码", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
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
        //删除单个指定商品
        deleteOnePLUKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入要删除的打称码", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
            @Override
            public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                deleteOnePLU(code);
            }

            @Override
            public void onCancel(NUIKeyDialog dialog) {
                dialog.dismiss();
            }
        }).create();
        // 临时价格键盘
        tempPriceKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入商品单价", NUIKeyView.KEY_TYPE_NUMBER_DISCOUNT).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
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
        //商品码
        itemCodeKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入商品码", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
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
        //打印键盘
        printKeyboard = new NUIKeyDialog.Builder(context)
                .addKeyView("输入批量打印价签份数", NUIKeyView.KEY_TYPE_NUMBER)
                .addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
                    @Override
                    public void onConfirm(String code, NUIKeyDialog dialog, int param) {
                        XLog.d("输入的数量是:" + code);
                        if (code.contains(".")) {
                            Toast.makeText(mContext, "请输入整数", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        onPrintInputEvent(dialog, code);
                    }

                    @Override
                    public void onCancel(NUIKeyDialog dialog) {
                        dialog.dismiss();
                    }
                }).create();
        //数量键盘
        numberKeyboard = new NUIKeyDialog.Builder(context)
                .addKeyView("请输入数量", NUIKeyView.KEY_TYPE_NUMBER)
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
        //根据数量售卖 数量键盘
        sellByQuantityKeyboard = new NUIKeyDialog.Builder(context)
                .addKeyView("请输入数量", NUIKeyView.KEY_TYPE_NUMBER)
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
        // 底部弹出框
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

        }, "是否退出？");

        //初始化最小
        weight = 30;
        try {
            weight = Float.parseFloat(Const.getSettingValue(Const.DELECT_WEIGHT));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // 列表点击、刷新事件
        aiPosAllView.getListView().setClickListener(new com.wintec.aiposui.view.AiPosListView.ClickListener() {
            @Override
            public void onItemClick(GoodsModel model, View view, int position) {
                Const.fromClick = true;
                disTempModel = model;
                long start = System.currentTimeMillis();
                onGoodsItemClick(model, position, false);
                long end = System.currentTimeMillis();
                XLog.d("点击到下发打印指令耗时: " + (end - start) + "ms");
                Const.fromClick = false;
//                PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(model.getGoodsId());
//                if (commdityByScalesCode.getPreviewImage() == null || "".equals(commdityByScalesCode.getPreviewImage())) {
//                    updatePrevirwImg(model);
//                }
            }

            @Override
            public void onFabclick() {

            }

            //长按事件
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
//                    popupsShow(view, new String[]{"锁定", "按份计价", "切换单位"}, model);
                    popupsShow(view, new String[]{"锁定",}, model);
                } else {
                    //  popupsShow(view, new String[]{"更换预览图"}, model);
                }


            }
        });
        // 输入框监听
       /* aiPosTitleView.setInputListener(new AiPosTitleView.OnTextChangedListener() {
            @Override
            public void onInput(String text) {
                inputListenEvent(text);
            }
        });*/

        aiPosAllView.getListView().idle();
        // 初始化弹框
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
            final static int COUNTS = 5;//点击次数
            final static long DURATION = 3 * 1000;//规定有效时间
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
            final static int COUNTS = 5;//点击次数
            final static long DURATION = 3 * 1000;//规定有效时间
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
            final static int COUNTS = 5;//点击次数
            final static long DURATION = 3 * 1000;//规定有效时间
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
                            aiTipDialog.showFail("请开启重新学习权限", aiPosAllView);
                        }
                    }
                }
            }
        });
    }


    //打印计件
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
     * 托利多传秤
     */
    private void initReceiveCommdity() {
        if ("托利多传秤".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
            //  初始化传称
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
                        //追溯码
//                        System.out.println(list.toString());
                        insertbpluszsm(list);
                    }

                    @Override
                    public void bpluszsmpluList(List<DataBean> list) {
                        //追溯码国标码
//                        System.out.println(list.toString());
                        insertbpluszsmgbm(list);
                    }
                }, Const.getSettingValue(Const.KEY_SCALE_PORT) == null || "".equals(Const.getSettingValue(Const.KEY_SCALE_PORT)) ? 3001 : Integer.valueOf(Const.getSettingValue(Const.KEY_SCALE_PORT)));
            } catch (NumberFormatException e) {
                XLog.e(e);
            }
            initTaceability();
        } else if ("批量取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) || "在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
            //todo 批量取数 定时任务
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
     * @description: 调用在线取值
     * @return:
     * @author: dean
     * @time: 2022/3/9 16:31
     */

    private void getDataOnline() {
        XLog.i("开始批量读数据库");
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
                XLog.i("写入本地数据库完成,花费 " + (endTime - startTime) / 1000 + " s");
            });
        } else {
            //不处理
            XLog.i("无网络连接，批量下载数据失败");
        }
    }

    /**
     * 保存数据库查询的商品信息
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
//                追溯码插入
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
     * 速德追溯码传秤
     */
    private void initTaceability() {
        if ("1".equals(Const.getSettingValue(Const.TRACEABILITY_CODE_FLAG))) {
            int port = 0;
            try {
                port = Integer.valueOf(Const.getSettingValue(Const.TRACEABILITY_CODE_PORT));
            } catch (NumberFormatException e) {
                XLog.e("追溯码端口号异常" + e);
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
                    XLog.e("追溯码服务开启异常" + e);

                }
            });

        }
    }


    /**
     * 多点本地文件获取数据
     */
    private void initDuoDianPlu() {
        try {
            ServerSocket serverSocket = new ServerSocket(3005);
            while (true) {
                Socket accept = serverSocket.accept();
                ThreadPoolManagerUtils.getInstance().execute(new DuoDianSocketHandler(accept));
            }
        } catch (IOException e) {
            XLog.e("追溯码服务开启异常");

        }


    }


    private void upLog() {
        initTimePicker1();
        pvTime.show();
    }

    private void initTimePicker1() {//选择出生年月日
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter_year = new SimpleDateFormat("yyyy ");
        String year_str = formatter_year.format(curDate);
        int year_int = (int) Double.parseDouble(year_str);


        SimpleDateFormat formatter_mouth = new SimpleDateFormat("MM ");
        String mouth_str = formatter_mouth.format(curDate);
        int mouth_int = (int) Double.parseDouble(mouth_str);

        SimpleDateFormat formatter_day = new SimpleDateFormat("dd ");
        String day_str = formatter_day.format(curDate);
        int day_int = (int) Double.parseDouble(day_str);


        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(year_int, mouth_int - 1, day_int);

        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(date);
                File file = new File(Logging.ROOT_PATH + "//aipos_log//" + format + ".txt");
                if (file.exists()) {
                    mPresenter.upLog(file, file.getName(), Const.SN);
                } else {
                    aiTipDialog.showFail("日志文件不存在", aiPosAllView);
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false}) //年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel("年", "月", "日", "", "", "")//默认设置为年月日时分秒
                .isCenterLabel(false)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                .setTextColorOut(Color.GRAY)//设置没有被选中项的颜色
                .setSubmitColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setContentSize(30)
                .setLineSpacingMultiplier(1.5f)
                .setTextXOffset(-10, 0, 10, 0, 0, 0)//设置X轴倾斜角度[ -90 , 90°]
                .setRangDate(startDate, endDate)
                .setDate(endDate)
                .setSubCalSize(30)
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void toResStudy(String code) {
        if (code == null || code.equals("")) {
            CommUtils.showMessage(mContext, "打称码不能为空");
            return;

        }
        if ("1".equals(Const.getSettingValue(Const.KEY_RESTUDY_FLAG))) {
            int i = api_setNoRecommend(code);
            api_updateLearningData();
            if (i == 0) {
                aiTipDialog.showSuccess("重新学习成功,重启后生效", aiPosAllView);
            } else {
                aiTipDialog.showSuccess("重新学习失败！请重试", aiPosAllView);
            }
        } else {
            aiTipDialog.showFail("请开启重新学习权限", aiPosAllView);
        }
    }

    /**
     * @param code 商品plu
     * @description:
     * @return: void
     * @author: dean
     * @time: 2022/11/10 10:38
     */
    private void deleteOnePLU(String code) {
        if (code == null || code.equals("")) {
            CommUtils.showMessage(mContext, "输入PLU不能为空");
            return;
        }
        final PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(code);
        if (commdityByScalesCode == null) {
            CommUtils.showMessage(mContext, "输入PLU有误，未查询到商品");
            return;
        }
        PluDtoDaoHelper.deleteOneByPLU(commdityByScalesCode);
        aiTipDialog.showSuccess("已删除 " + code, aiPosAllView);
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
     * 数据上传
     */
    private void dealInsert(GoodsModel model, int proportion) {
        Map<String, Object> map = new HashMap<>();
        String content = model.getGoodsName();//识别出的商品的名字
        int pricingManner = model.getUnitId();
        String transactionValue = model.getPrice();//商品的价格
        String branchCode = Const.getSettingValue(Const.KEY_BRANCH_ID);
        String weight = aiPosAllView.getTitleView().getWeight();

        map.put("posSn", Const.SN);//设备的唯一标识
        map.put("content", content);
        map.put("transactionValue", transactionValue);
        map.put("pricingManner", pricingManner);//9-计件  4-计重
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
        //改价键盘
        priceKeyboard = new NUIKeyDialog.Builder(mContext).addKeyView("请输入商品单价", NUIKeyView.KEY_TYPE_NUMBER).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
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
     * 数量输入事件
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
     * 数量输入事件
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
            //设置总价
            total.setTotal(String.format("%.2f", printCommdity.getUnitPriceA() * number));
            //调用打印
            WintecServiceSingleton.getInstance().printImgLable(printCommdity, total, number, isKg, tradeMode, aiPosAllView.getTitleView().getTare(), mNet);
        } catch (Exception e) {
            XLog.e(e);
        }
        clearMode();
        dialog.dismiss();

    }


    /**
     * 商品列表点击事件
     *
     * @param goodsModel
     */
    void onGoodsItemClick(GoodsModel goodsModel, int position, boolean isRecommond) {
        // 按KG卖
        if (net <= 0.0f && goodsModel.getUnitId() == 0 && !priceChangeFlagFoever) {
            aiTipDialog.showFail("重量不能为零", aiPosAllView);
            return;
        }
        if (!isST && !isZero) {
//            aiTipDialog.showFail("重量不稳定", aiPosAllView);
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
                    XLog.d("未命中");
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
                    XLog.d("命中，点击确认花费:" + (current_time2 - current_time));
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


        //打印价钱
        if (discountFlag) {
            if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !goodsModel.getDisCount()) {
                aiTipDialog.showFail("该商品不允许折扣", aiPosAllView);
            } else {
                discountKeyboard.show();
            }
        } else if (tempPriceFlag) { //临时改价
            if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !goodsModel.getTempPrice()) {
                aiTipDialog.showFail("该商品不允许改价", aiPosAllView);
            } else {
                //折扣显示原价
                tempPriceKeyboard.showWithParams(goodsModel.getPrice());
//                tempPriceKeyboard.show();
            }
        } else if (priceChangeFlag) {
            if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !goodsModel.getTempPrice()) {
                aiTipDialog.showFail("该商品不允许改价", aiPosAllView);
            } else {
                commdityDisPrice = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
                priceKeyboard.show();
            }
        } else {
            printGood();
        }


    }

    private void upImg(String taskId, String itemName, String itemCode, int w) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // 异步上传识别结果和图片到云服务器
        // 组装参数
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
        // todo 生成token,abc测试用，修改生成规则
        String token = pb;
        // 组装文件
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
        // 请求
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
     * 商品注册
     */
    void itemRegister() {
        codeKeyboard.show();
    }

    /**
     * 点击三次版本号
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
                    aiTipDialog.showSuccess("切换成功", aiPosAllView);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        }, "是否切换标签纸类型").show();
    }

    private boolean isPreSetTare = false;   // 是否预置皮重

    /**
     * 置零
     */
    void setZero() {
        WintecServiceSingleton.getInstance().setZero();
    }

    /**
     * 去皮
     */
    void setTare() {
        isPreSetTare = false;
        WintecServiceSingleton.getInstance().setTare();
    }


    /**
     * 商品注册、提交
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
    //记录上一次net
    float[] lastNet = new float[2];
    int index = 0;
    boolean ol = false;

    /**
     * 秤监听
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
            //刷新总价
            if (isCanRefreshTotal) {
                aiPosAllView.getTitleView().setScalesStatusStable();
                scalesHandler.sendEmptyMessage(FLUSH_TOTAL);
            }
            //连续打签
            if (isCanPrint.get() && mNet >= 0.030f) {
                isCanPrint.set(false);
//                Log.i("Test","稳重触发连续打印");
//                netChangeCount = 0;
                printSendScale();
            }
            //打印待打印商品
            if (isItemReadyToPrint) {
                isItemReadyToPrint = false;
                if (itemReadyToPrint != null) {
                    disTempModel = itemReadyToPrint;
                    onGoodsItemClick(itemReadyToPrint, 0, false);
                }

            }
            //通知识别
            if (!isZeroAfterPrint && isAfterPrint) {
                isAfterPrint = false;
                isZeroAfterPrint = true;
//                Log.i("test","稳重识别"+net+","+prePrintNet);
                isCanDectect.set(false);
                // 请求识别
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
            //title的文字没有商品时 置空
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
        // 过载
        else if (status.equals("OL")) {
            isZero = true;
            aiPosAllView.getTitleView().setScaleOverload(CommUtils.Float2String(net, point));
            return;
        }
        // 欠载
        else if (status.equals("LL")) {
            isZero = true;
            aiPosAllView.getTitleView().setScaleLoseload(CommUtils.Float2String(net, point));
            isCanDectect.set(true);
            return;
        }
        // 变化中
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

        // 根据重量差,发送识别请求
        if (net >= weight / 1000 && isCanDectect.get() && !isPrint.get()) {
//            isCanDetectWithoutZero = false;
            isCanDectect.set(false);
            // 请求识别
            scalesHandler.sendEmptyMessage(SCALES_DETECT);
        }

        preNet = net;
    }

    //连续打印价签
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
     * 显示设备信息
     */
    void showDeviceInfo() {
        showFullScreenPopupWindow();
    }

    /**
     * 显示全屏弹出框
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
        //  ToastUtils.showToast("上传成功");
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
     * 处理折扣
     *
     * @param code
     * @param dialog
     */
    public boolean handleDiscount(String code, NUIKeyDialog dialog) {
        // 输入为空
        if (code == null || code.equals("")) {
            return false;
        }
        // 处理不带小数的折扣
        if (!code.contains(".")) {
            if (Integer.parseInt(code) == 0) {
                CommUtils.showMessage(mContext, "折扣不能为0");
                return false;
            }
            discount = Float.parseFloat(code.replaceAll("^(0+)", "")) / 10;
            if (discount <= 0 || discount >= 1) {
                CommUtils.showMessage(mContext, "请输入正确折扣");
                discount = 1f;
                return false;
            }
        }
        // 处理带小数的折扣
        else {
            if (!code.startsWith(".")) {
                if (CommUtils.isNumeric(code)) {
                    discount = Float.parseFloat(code) / 10;
                    if (discount <= 0 || discount >= 1) {
                        CommUtils.showMessage(mContext, "请输入正确折扣");
                        discount = 1f;
                    }
                } else {
                    CommUtils.showMessage(mContext, "请输入正确折扣");
                }
            } else {
                CommUtils.showMessage(mContext, "请输入正确折扣");
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
            XLog.i("无法清除改价状态");
            return;
        }
        // 重置交易模式
        tradeMode = MODE_NORMAL_TRADE;
        // 初始化折扣
        discount = 1f;
        // 初始化临时价格
        tempPrice = 0;
        // 初始化总价
        tempTotal = 0;
    }


    /**
     * 处理临时变价
     *
     * @param price
     * @param dialog
     */
    public boolean handleTempPrice(String price, NUIKeyDialog dialog, int param) {
        if (!opinionPrice(price)) {
            return false;
        }
        dialog.dismiss();
        //CommUtils.showMessage(mContext, "请选择使用该价格的商品");
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
            CommUtils.showMessage(mContext, "商品码不能为空");
            return;

        }
        //查询商品
        commdityDisPrice = PluDtoDaoHelper.getCommdityByScalesCode(code);
        if (commdityDisPrice != null) {
            goodName = commdityDisPrice.getNameTextA();
        }
        if (commdityDisPrice == null) {
            CommUtils.showMessage(mContext, "该商品不存在");
            return;
        }
        dialog.dismiss();
        permanentPriceChange();
        setDisPrice();
        //CommUtils.showMessage(mContext, "请选择使用该价格的商品");
        //aiposUI.getListView().setDescribeText("下一单商品的单价为"+tempPrice+"元/KG");
    }

    /**
     * 变价
     */
    public void setDisPrice() {
        priceKeyboard.show();
    }

    /**
     * 处理价格
     *
     * @param price
     * @param dialog
     */
    public void handlePrice(String price, NUIKeyDialog dialog) {
        if (!opinionPrice(price)) {
            return;
        }
        if ("".equals(commdityDisPrice.getPluNo()) || commdityDisPrice.getPluNo() == null) {
            CommUtils.showMessage(mContext, "商品信息不正确");
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


    //价格判断
    private Boolean opinionPrice(String price) {
        if (price == null || price.equals("")) {
            return false;
        }
        // 对价格合理性进行检测
        if (!price.contains(".")) {
            price = price.replaceAll("^(0+)", "");
            if (price == null || price.equals("")) {
                price = "0";
            }
        } else {
            if (!price.startsWith(".")) {
                if (!CommUtils.isNumeric(price)) {
                    // 提示输入正确单价
                    CommUtils.showMessage(mContext, "请输入正确单价");
                    return false;
                }

            } else {
                // 提示输入正确单价
                CommUtils.showMessage(mContext, "请输入正确单价");
                return false;
            }
        }
        if (Float.parseFloat(price) == 0) {
            CommUtils.showMessage(mContext, "请输入正确单价");
            return false;
        }
        return true;
    }


    /**
     * 上传改价数据
     */
    void updatePriceOnServer(NUIKeyDialog dialog, PluDto commdity) {

        PluDtoDaoHelper.updateCommdity(commdity);
        aiTipDialog.dismiss();
        aiTipDialog.showSuccess("更新成功", aiPosAllView);
        isNewData = true;


    }

    /*
     * 发送商品数据
     */
    private void sendScale(int unitId, GoodsModel goodsModel, String count, Total total) {
        PluDto co;
        if ("在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(this)) {
            if (currentCo == null) {
                co = PluDtoDaoHelper.getCommdityByScalesCodeOnline(goodsModel.getGoodsId());
            } else {
                co = currentCo;
            }
        } else {
            co = PluDtoDaoHelper.getCommdityByScalesCode(goodsModel.getGoodsId());
        }
        if (co == null) {
            aiTipDialog.showFail("商品查询错误", aiPosAllView);
            return;
        }
        //是否语音播报
        if ("1".equals(Const.getSettingValue(Const.VOIDCE_BROADCAST_FLAG))) {
            TTSpeaker.getInstance(mContext).speak(co.getNameTextA() + " " + total.getTotal() + "元");
        }
        switch (Const.getSettingValue(Const.KEY_MODE)) {
            case "价签模式":
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
                    //副屏展示信息
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
                // 检查是否需要关闭折扣和临时改价状态
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
            case "收银台模式(ttySAC4)":
                if (comIO == null) {
                    this.comIO = new ComIO("/dev/ttySAC4", 9600);
                }
                if (!comIO.isOpen()) {
                    comIO.open();
                }
                String barcode1 = WintecServiceSingleton.getInstance().createBarCode(co, total.getTotal(), net, Integer.valueOf(count), total.getPrice());
                comIO.send(barcode1 + "\r");
                break;
            case "收银台模式(ttySAC3)":
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
     * 清除数据
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
        ToastUtils.showToast("上传成功");
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
        aiTipDialog.showSuccess("下载成功", aiPosAllView);
        nuiBottomSheet.dismiss();
    }

//    private void continuousClick(int count, long time) {
//        //每次点击时，数组向前移动一位
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//        //为数组最后一位赋值
//        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
//        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
//            mHits = new long[COUNTS];//重新初始化数组
//            resStudyKeyboard.show();
//        }
//    }

    /**
     * 皮重处理
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
            aiTipDialog.showSuccess("皮重设置成功", aiPosAllView);
        } catch (Exception e) {
            aiTipDialog.showFail("请输入正确皮重", aiPosAllView);
        }
    }

    private void insert(List<DataBean> dataBeans) {
//        ThreadPoolManagerUtils.getInstance().execute(() ->{
        dataBeans.forEach(item -> {
            if (item instanceof Plu) {
                Plu plu = (Plu) item;
                PluDto commdity = new PluDto(plu);
                String pluName = plu.getNameTextA();
                //去除商品名里的字母
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
        if (Const.NetworkReachable && Const.fromClick && "在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(this)) {
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
//        if ("在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE)) && NetWorkUtil.isNetworkAvailable(this)) {
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
//                    Toast.makeText(mContext, "已更换预览图重启生效", Toast.LENGTH_SHORT).show();
//                } else
                if (i == 0) {
                    PluDto commdityByScalesCode = PluDtoDaoHelper.getCommdityByScalesCode(model.getGoodsId());
                    printCommdity = commdityByScalesCode;
                    isContinuityPrintFlag.set(true);
                    if (discountFlag) {
                        if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !commdityByScalesCode.getDiscountFlagA()) {
                            aiTipDialog.showFail("该商品不允许折扣", aiPosAllView);
                        } else {
                            discountKeyboard.show();
                        }
                    } else if (tempPriceFlag) {
                        if ("1".equals(Const.getSettingValueWithDef(Const.PLU_ATTR_DISPRICE_FLAG, "0")) && !commdityByScalesCode.getPriceChangeFlagA()) {
                            aiTipDialog.showFail("该商品不允许改价", aiPosAllView);
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


    // 初始化副屏
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
            aiTipDialog.showFail("未查到该商品", aiPosAllView);
            return;
        }
        if (co.getUnitPriceA() == 0.00f) {
            aiTipDialog.showFail("单价不能为零", aiPosAllView);
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
        //在线价格和本地价格不一样时，更新本地价格
        if (co.getUnitPriceA() != Float.valueOf(total.getPrice()) && "在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
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
     * 变价日志初始化
     */
    private FileWriter initDisCountFile() throws IOException {
        String path = "";
        Boolean flag = false;
        File file;

        path = Environment.getExternalStoragePublicDirectory("disCount").getPath();
        file = new File(path);
        if (!file.exists()) {
            //创建文件夹
            file.mkdirs();
        }
        path = path + File.separator + DateUtils.getDate(null) + ".txt";

        file = new File(path);
        if (!file.exists()) {
            //创建文件
            file.createNewFile();
        }
        FileWriter writer = null;
        // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
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
                stringBuilder.append(netOrCount + "件");
                stringBuilder.append("    ");
            }
            switch (flag) {
                // 改价
                case 1:
                    stringBuilder.append("改价");
                    stringBuilder.append("    ");
                    break;
                // 折扣
                case 2:
                    stringBuilder.append("折扣");
                    stringBuilder.append("    ");
                    break;
                // 永久改价
                case 3:
                    stringBuilder.append("永久改价");
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
                    Log.w("sql", "完成提前查询  花费 " + (System.currentTimeMillis() - s) + " ms  " + queryOneByPLU.toString());
                } catch (Exception e) {
                    Const.pulAndPricesMapOk = false;
                    Const.NetworkReachable = false;
                    e.printStackTrace();
                    Log.w("sql", "完成提前查询失败: " + e.toString());
                    XLog.e("完成提前查询失败: " + e);
                }
            }

        }
    }
}
