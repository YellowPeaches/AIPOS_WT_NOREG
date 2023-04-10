package com.wintec.lamp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;
import com.wintec.detection.WtAISDK;
import com.wintec.lamp.base.BaseMvpActivity;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.contract.WelcomeContract;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.dao.helper.TagMiddleHelper;
import com.wintec.lamp.httpdownload.DownInfo;
import com.wintec.lamp.network.schedulers.BaseSchedulerProvider;
import com.wintec.lamp.network.schedulers.SchedulerProvider;
import com.wintec.lamp.presenter.WelcomePresenter;
import com.wintec.lamp.utils.NetWorkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WelcomeNoReg extends BaseMvpActivity<WelcomePresenter> implements WelcomeContract.IView {

    Button enterButton;
    Integer clickNum = 1;
    ImageView welcomeBgImageView;
    Boolean allowEnter = true;


    private BaseSchedulerProvider schedulerProvider;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_welcome_no_reg);
//        enterButton = (Button) findViewById(R.id.welcome_no_reg_button);
//        welcomeBgImageView = (ImageView) findViewById(R.id.welcome_no_reg_bg);
//        welcomeBgImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clickNum++;
//                if (clickNum == 1) {
//                    welcomeBgImageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_welcome1));
//                } else if (clickNum == 2) {
//                    welcomeBgImageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_welcome2));
//                } else {
//                    jumpToMainActivity();
//                }
//            }
//        });
//    }

    @Override
    protected int contentResId() {
        return R.layout.activity_welcome_no_reg;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        setContentView(R.layout.activity_welcome_no_reg);
//        enterButton = (Button) findViewById(R.id.welcome_no_reg_button);
        welcomeBgImageView = (ImageView) findViewById(R.id.welcome_no_reg_bg);
        welcomeBgImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickNum++;
                if (allowEnter) {
                    if (clickNum == 1) {
                        welcomeBgImageView.setImageDrawable(getResources().getDrawable(R.drawable.scale_demo));
                    } else if (clickNum == 2) {
                        welcomeBgImageView.setImageDrawable(getResources().getDrawable(R.drawable.scale_demo));
                    } else {
                        jumpToMainActivity();
                    }
                }
            }
        });
        schedulerProvider = SchedulerProvider.getInstance();

        if (Const.getSettingValue(Const.PREVIEW_FLAG).equals("1") && NetWorkUtil.isNetworkAvailable(this)) {
            try {
                new WelcomePresenter().upPLUDto();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Const.getSettingValue(Const.PREVIEW_FLAG).equals("1") && NetWorkUtil.isNetworkAvailable(this)) {
            mPresenter.getImgUrl();
        }
        if (NetWorkUtil.isNetworkAvailable(this) && "1".equals(Const.getSettingValue(Const.ERROR_LOG_FLAG))) {
            Toast.makeText(this, "正在上传日志", Toast.LENGTH_LONG).show();
            File file = new File(Const.getSettingValue(Const.ERROR_LOG_PATH));
            try {
                mPresenter.upLogTxt(file, file.getName(), Const.SN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 首次加载默认配置
        firstOpenApp();
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
    protected WelcomePresenter createPresenter() {
        return new WelcomePresenter();
    }

    public void jumpToMainActivity() {
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        String snCode = Const.SN;
        final boolean noReg = checkSN(snCode);

        if (noReg) {
            if (WtAISDK.api_getCameraSetting()) {
                Intent intent = new Intent(this, ScaleActivityUI.class);
                startActivity(intent);
                finish();
            } else {
                startActivity(new Intent(this, CorpPicActivaty.class));
                finish();
            }
        } else {
//            startActivity(new Intent(this, WelcomeActivity.class));
            allowEnter = false;
            welcomeBgImageView.setImageDrawable(getResources().getDrawable(R.drawable.welcome_blue_logo_no));
        }


    }

    @Override
    public void showCheckUpDate(File file) {

    }

    @Override
    public void showDownloading(int progress) {

    }

    @Override
    public void showDownloadFailed() {

    }

    @Override
    public void showRegister(registerBean registerBean) {

    }

    @Override
    public void showCheckVersion(VersionBean bean) {

    }

    @Override
    public void showAppState() {

    }

    @Override
    public void showAppDownInfo(DownInfo downInfo) {

    }

    /**
     * 首次打开软件，设置默认配置
     */
    private void firstOpenApp() {
        SharedPreferences setting = getSharedPreferences("First.ini", 0);
        boolean isfirst = setting.getBoolean("FIRST", true);
        if (isfirst) {// 第一次则跳转到欢迎页面
            setting.edit().putBoolean("FIRST", false).commit();
            firstSetting();
//            new BarSettingPresenter().getUpdatePriceTar(branchId, Const.SN);
        } else {
            welcomeBgImageView.setImageDrawable(getResources().getDrawable(R.drawable.scale_demo));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jumpToMainActivity();
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
        Const.setSettingValue(Const.KEY_POS_PASSWORD, "11111111");

        Const.setSettingValue(Const.RESULT_DISPLAY, "0");
        //Const.setSettingValue(Const.KEY_SEND_SCALE,"1");
        Const.setSettingValue(Const.KEY_GET_DATA_MODE, "托利多传秤");
        Const.setSettingValue(Const.KEY_GET_DATA_DB, "oracle");
        Const.setSettingValue(Const.KEY_GET_DATA_ADDITIONAL_SQL, "SELECT * FROM dbo.v_sk_extratext");
        Const.setSettingValue(Const.KEY_GET_DATA_SQL, "SELECT * FROM dbo.v_sk_item");
        Const.setSettingValue(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL, "INSERT INTO TESTONLINE.CLERK_LOG (CODE, SCALE_CODE, PLU_NUMBER,COMMODITY_NAME,EDITDATE,TIME,STATUS,SCALE_IP,OLD_UNIT_PRICE,NEW_UNIT_PRICE,PRINTED_EAN_DATA) VALUES( #{CODE}, #{SCALE_CODE},  #{PLU_NUMBER},#{COMMODITY_NAME},#{EDITDATE},#{TIME},#{STATUS},#{SCALE_IP},#{OLD_UNIT_PRICE},#{NEW_UNIT_PRICE},#{PRINTED_EAN_DATA})");

        //临时默认配置
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
        Const.setSettingValue(Const.KEY_REDETECT_AND_PRINT, "重新识别");
        //条码初始化
        Const.setSettingValue(Const.KEY_ODD_EVEN_CHECK, "奇校验");
        Const.setSettingValue(Const.BAR_CODE_PREFIX, "28");
        Const.setSettingValue(Const.BAR_CODE_PREFIX_PIECE, "28");
        Const.setSettingValue(Const.BAR_CODE_FORMAT, "前缀-PLU-总价-重量");
        Const.setSettingValue(Const.BAR_CODE_PIECT_FLAG, "个位开始");
        Const.setSettingValue(Const.BAR_CODE_MULTI_PRICE_SIGN, "0");
        Const.setSettingValue(Const.ITEM_NO_REPLACE_PLU, "0");

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
        Const.setSettingValue(Const.SEARCH_BY, "PLU");
        //获取称重串口
        if (Build.VERSION.SDK_INT < 30)
            Const.setSettingValue(Const.GET_WEIGHT_PORT, "/dev/ttySAC1");
        else
            Const.setSettingValue(Const.GET_WEIGHT_PORT, "/dev/ttySAC4");
        Const.setSettingValue(Const.ERROR_LOG_FLAG, "0");
        setDefaultTag();
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(24);
        tagMiddle.setOverstriking(0);
        tagMiddle.setCodeSystem(0);
        tagMiddle.setTagName("单价");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnderline(0);
        tagMiddle.setUnit("元/kg");
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(68);
        tagMiddle.setOverstriking(0);
        tagMiddle.setTagName("商品条码");
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(53);
        tagMiddle.setOverstriking(0);
        tagMiddle.setTagName("包装日期");
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(36);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("总价");
        tagMiddle.setTemplateId(89);
        tagMiddle.setUnit("元");
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(4);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("商品名称");
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(48);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("价");
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(37);
        tagMiddle.setOverstriking(0);
        tagMiddle.setTagName("重量");
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
        tagMiddle.setName("默认标签");
        tagMiddle.setOrdinate(33);
        tagMiddle.setOverstriking(1);
        tagMiddle.setTagName("总");
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

    private boolean checkSN(String sn) {
//        boolean ans = false;
//        if (StringUtils.isNotEmpty(sn)) {
//            String year = sn.substring(6, 7);
//            String month = sn.substring(7, 8);
//            String day = sn.substring(8, 10);
//            if (Integer.parseInt(year) > 2) {
//                ans = true;
//            } else if (Integer.parseInt(year) == 2 && (month.equals("A") || month.equals("B") || month.equals("C")
//                    || month.equals("8") || month.equals("9"))) {
//                ans = true;
//            }
//        } else {
//
//        }
//        if("0213Z1106G2007".equals(sn)||"1650SC10694063".equals(sn)||"1652SC101203T42009".equals(sn)){
//            ans =true;
//        }
        return true;
    }
}