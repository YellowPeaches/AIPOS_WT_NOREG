package com.wintec.lamp.base;

import com.wintec.lamp.utils.SPUtils;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Const {
    //服务器IP
    public final static String KEY_BASE_IP = "KEY_BASE_IP";
    //门店名称
    public final static String KEY_BRANCH_NAME = "KEY_BRANCH_NAME";
    //商户编码
    public final static String KEY_TER_CODE = "KEY_TER_CODE";
    //门店ID
    public final static String KEY_BRANCH_ID = "KEY_BRANCH_ID";
    //门店编码
    public final static String ERP_BRANCH_ID = "ERP_BRANCH_ID";
    //用户Pos密码
    public final static String KEY_POS_PASSWORD = "KEY_POS_PASSWORD";
    //维护Pos密码
    public final static String KEY_SAFEGUARD_PASSWORD = "KEY_SAFEGUARD_PASSWORD";
    //posid
    public final static String KEY_POS_ID = "KEY_POS_ID";
    //YM  ISBIND
    public final static String KEY_SN_CODE = "KEY_SN_CODE";
    //YM  SN
    public final static String KEY_IS_BIND = "KEY_IS_BIND";
    //YM  ACCOUNT
    public final static String KEY_ACCOUNT = "KEY_ACCOUNT";
    //是否有秤盘
    public final static String KEY_SCALE_FLAG = "KEY_SCALE_FLAG";
    //是否显示状态
    public final static String KEY_STUTE_FLAG = "KEY_STUTE_FLAG";
    //录入次数
    public final static String KEY_RECORD_COUNT = "KEY_RECORD_COUNT";

    //备份版本
    public final static String BACK_VERSION = "BACK_VERSION";

    //一签多品前缀
    public final static String BAR_ONECODE_PREFIX = "BAR_ONECODE_PREFIX";
    //一签多品后缀
    public final static String BAR_ONECODE_POSTFIX = "SBAR_ONECODE_ POSTFIX";
    //一签多品连接符
    public final static String BAR_ONECODE_CONNECTOR = "BAR_ONECODE_CONNECTOR";
    //一签多品开启标志
//    public final static String BAR_ONECODE_FLAG ="BAR_ONECODE_FLAG";
    //称号
    public final static String SCALE_NO = "SCALE_NO";
    //标签格式
    public final static String BAR_CODE_FORMAT = "BAR_CODE_FORMAT";
    //奇偶校验位odd-even check
    public final static String KEY_ODD_EVEN_CHECK = "KEY_ODD_EVEN_CHECK";
    //条码位数
    public final static String BAR_CODE_LENGTH = "BAR_CODE_LENGTH";
    //称重码前缀
    public final static String BAR_CODE_PREFIX = "BAR_CODE_PREFIX";
    //称重码前缀 计件
    public final static String BAR_CODE_PREFIX_PIECE = "BAR_CODE_PREFIX_PIECE";
    //货号下标(7位)
    public final static String BAR_CODE_ARTNO_COORDINATE = "BAR_CODE_ARTNO_COORDINATE";
    //称重码前缀下标(2位)
    public final static String BAR_CODE_PREFIX_COORDINATE = "BAR_CODE_PREFIX_COORDINATE";
    //plu下标(5位)
    public final static String BAR_CODE_PLU_COORDINATE = "BAR_CODE_PLU_COORDINATE";
    //总价下标(5位)
    public final static String BAR_CODE_TOTAL_COORDINATE = "BAR_CODE_TOTAL_COORDINATE";
    //重量下标(5位)
    public final static String BAR_CODE_WEIGHT_COORDINATE = "BAR_CODE_WEIGHT_COORDINATE";
    //单价下标(5位)
    public final static String BAR_CODE_PRICE_COORDINATE = "BAR_CODE_PRICE_COORDINATE";
    //货号长度
    public final static String BAR_CODE_ARTNO_LENGTH = "BAR_CODE_ARTNO_LENGTH";
    //称重码前缀长度
    public final static String BAR_CODE_PREFIX_LENGTH = "BAR_CODE_PREFIX_LENGTH";
    //plu长度
    public final static String BAR_CODE_PLU_LENGTH = "BAR_CODE_PLU_LENGTH";
    //总价长度
    public final static String BAR_CODE_TOTAL_LENGTH = "BAR_CODE_TOTAL_LENGTH";
    //重量长度
    public final static String BAR_CODE_WEIGHT_LENGTH = "BAR_CODE_WEIGHT_LENGTH";
    //单价长度(5位)
    public final static String BAR_CODE_PRICE_LENGTH = "BAR_CODE_PRICE_LENGTH";
    //计数商品下标
    public final static String BAR_CODE_PIECT_COORDINATE = "BAR_CODE_PIECT_COORDINATE";
    //是否打印校验位
    public final static String BAR_CODE_IS_CHECK = "BAR_CODE_IS_CHECK";
    //打印条码或二维码
    public final static String BAR_CODE_OR_QRCODE_FLAG = "BAR_CODE_OR_QRCODE_FLAG";
    //打印条码以克为单位显示
    public final static String BAR_CODE_GRAM_UNIT = "BAR_CODE_GRAM_UNIT";
    //货号替换PLU
    public final static String ITEM_NO_REPLACE_PLU = "ITEM_NO_REPLACE_PLU";
    //打折改价划横线
    public final static String DISCOUNT_LINEATION = "DISCOUNT_LINEATION";
    //打印二维码数字
    public final static String QRCODE_NUMBER_FLAG = "QRCODE_NUMBER_FLAG";
    //打印方向
    public final static String TAG_DIRECTION = "TAG_DIRECTION";
    //计数商品数量位
    public final static String BAR_CODE_PIECT_FLAG = "BAR_CODE_PIECT_FLAG";
    //多价签模式
    public final static String BAR_CODE_MULTI_PRICE_SIGN = "BAR_CODE_MULTI_PRICE_SIGN";


    //重新识别与打印
    public final static String KEY_REDETECT_AND_PRINT = "KEY_REDETECT_AND_PRINT";
    //结果显示个数
    public final static String KEY_GOODS_COUNT = "KEY_GOODS_COUNT";
    //快捷选项显示个数
    public final static String KEY_SHORTCUT_GOODS_COUNT = "KEY_SHORTCUT_GOODS_COUNT";

    //重量单位
    public final static String WEIGHT_UNIT = "WEIGHT_UNIT";
    //获取称重串口
    public final static String GET_WEIGHT_PORT = "GET_WEIGHT_PORT";
    //总价小数点
    public final static String TOTAL_PRICE_POINT = "TOTAL_PRICE_POINT";
    //单价小数点
    public final static String UNIT_PRICE_POINT = "UNIT_PRICE_POINT";
    //重量小数点
    public final static String WEIGHT_POINT = "WEIGHT_POINT";
    //总价圆整
    public final static String TOTAL_PRICE_MODE = "TOTAL_PRICE_MODE";
    //是否显示结果
    public final static String RESULT_DISPLAY = "RESULT_DISPLAY";
    //结果显示时间
    public final static String RESULT_DISPLAY_TIME = "RESULT_DISPLAY_TIME";
    //识别触发重量
    public final static String DELECT_WEIGHT = "DELECT_WEIGHT";

    // 语音播报开关
    public final static String VOIDCE_BROADCAST_FLAG = "VOIDCE_BROADCAST_FLAG";
    // 预览图开关
    public final static String PREVIEW_FLAG = "PREVIEW_FLAG";
    // 模糊搜索位数
    public final static String SEARCH_LENGHT = "SEARCH_LENGHT";
    //宁致打印机速度
    public final static String PRINTERER_SPEED = "PRINTERER_SPEED";
    //宁致打印机浓度
    public final static String PRINTER_CONCENTRATION = "PRINTER_CONCENTRATION";

    //是否允许永久改价
    public final static String KEY_UPPRICE_FLAG = "KEY_UPPRICE_FLAG";
    //是否允许折扣
    public final static String KEY_DISCOUNT_FLAG = "KEY_DISCOUNT_FLAG";
    //是否允许临时改价
    public final static String KEY_TEMPPRICE_FLAG = "KEY_TEMPPRICE_FLAG";
    //是否允许重新学习
    public final static String KEY_RESTUDY_FLAG = "KEY_RESTUDY_FLAG";
    //是否多品一签
    public final static String KEY_ALL_ONE_FLAG = "KEY_ALL_ONE_FLAG";

    //临时改价仅生效一次
    public final static String ONE_TEMPPRICE_FLAG = "ONE_TEMPPRICE_FLAG";

    //折扣仅生效一次
    public final static String ONE_DISCOUNT_FLAG = "ONE_DISCOUNT_FLAG";

    //商品属性变价
    public final static String PLU_ATTR_DISPRICE_FLAG = "PLU_ATTR_DISPRICE_FLAG";

    //是否开启追溯码传称
    public final static String TRACEABILITY_CODE_FLAG = "TRACEABILITY_CODE_FLAG";
    //追溯码传称端口号
    public final static String TRACEABILITY_CODE_PORT = "TRACEABILITY_CODE_PORT";
    //传称端口号
    public final static String KEY_SCALE_PORT = "KEY_SCALE_PORT";
    //传称是否开启
    public final static String KEY_SEND_SCALE = "KEY_SEND_SCALE";
    // 传秤单位
    public final static String KEY_SEND_UNIT = "KEY_SEND_UNIT";


    //称台设置  1pos20 0s100 2pos20
    public final static String KEY_SCALE = "KEY_SCALE";
    //拍照延迟
    public final static String KEY_PHOTOGRAPH_COUNT = "KEY_PHOTOGRAPH_COUNT";
    //串口发数据延迟
    public final static String KEY_TIME_COUNT = "KEY_TIME_COUNT";
    //商品码长度
    public final static String KEY_ITEMCODE_SIZE = "KEY_ITEMCODE_SIZE";
    //价签类型
    public final static String KEY_LABEL_TYPE = "KEY_LABEL_TYPE";

    //打印机设置
    public static final String PRINT_SETTING = "PRINT_SETTING";

    //打印偏移量
    // public static final String ROTATION_SETTING = "ROTATION_SETTING";

    //播报语音
    public static final String KEY_VOICE = "KEY_VOICE";
    public static final String IMAGE_PRE_TIME = "IMAGE_PRE_TIME";
    //数据库类型
    public static final String KEY_GET_DATA_DB = "KEY_GET_DATA_DB";
    //模式     收银模式/价签秤模式
    public static final String KEY_MODE = "KEY_MODE";

    public static final String ERROR_LOG_FLAG = "ERROR_LOG_FLAG";
    public static final String ERROR_LOG_PATH = "ERROR_LOG_PATH";


    // 服务器IP
    public static String BASE_URL = "http://114.115.174.123:8090/";//云服务器
    //        public static String BASE_URL = "http://192.168.23.130:8090/";
    // 长连接端口号
    public static int Port = 8090;
    // web端口号
    public static int WebPort = 8080;
    // 摄像头分辨率-宽度
    public static int CamWidth = 1920;
    // 摄像头分辨率-高度
    public static int CamHeight = 1080;
    // 摄像头位置,默认0后置,1前置
    public static int CamPosition = 0;
    //按份卖，长按时间，利群特殊需求
    public static boolean sellByNum = false;
    // 版本号
    public static String versionCode = "20000";
    // 版本名
    public static String versionName = "2.0.0";
    // SN
//    public static String SN = android.os.Build.SERIAL;
    public static String SN = getDeviceSN();
    //    public static String MAC = getLocalMacAddress();
    // 数据更新
    public static String UPDATE_URL = "/sku/queryList";
    // 软件更新
    public static String VERSION = "/appVersion/queryLatestVersion";
    //暂停下载
    public final static String ISCANCELLOAD = "iscancelload";
    //下载链接保存
    public final static String APKURL = "apkurl";

    public static final String APK_DOWNLOAD_URL = "downloadUrl";
    //识别阈值
    public final static String DETECT_THRESHOLD = "DETECT_THRESHOLD";

    //获取数据方式
    public final static String KEY_GET_DATA_MODE = "KEY_GET_DATA_MODE";
    //ip
    public final static String KEY_GET_DATA_IP = "KEY_GET_DATA_IP";
    //定时取数间隔
    public final static String DELAY_TIME = "DELAY_TIME";
    //端口
    public final static String KEY_GET_DATA_PORT = "KEY_GET_DATA_PORT";
    //数据库名
    public final static String KEY_GET_DATA_DB_NAME = "KEY_GET_DATA_DB_NAME";
    //user
    public final static String KEY_GET_DATA_USER = "KEY_GET_DATA_USER";
    //pwd
    public final static String KEY_GET_DATA_PWD = "KEY_GET_DATA_PWD";
    //sql
    public final static String KEY_GET_DATA_SQL = "KEY_GET_DATA_SQL";
    public static final String KEY_GET_DATA_ADDITIONAL_SQL = "KEY_GET_DATA_ADDITIONAL_SQL";
    public static final String KEY_GET_DATA_UP_PRICE_CHANGE = "KEY_GET_DATA_UP_PRICE_CHANGE";
    public static final String KEY_GET_DATA_UP_PRICE_CHANGE_SQL = "KEY_GET_DATA_UP_PRICE_CHANGE_SQL";

    //交易序号
    public static String KEY_GET_TASK_ID = "KEY_GET_TASK_ID";
    //局域网同步学习标志
    public static String LAN_SYNCHRONIZATION = "LAN_SYNCHRONIZATION";
    public static Boolean fromClick = false; //


    public static Map<String, Float> pulAndPricesMap = new HashMap<>();
    public static Boolean pulAndPricesMapOk = false;
    public static Connection con = null;
    public static boolean NetworkReachable = false;

    //是否键盘输入
    public static Boolean keyFromInput = false;

    public static Boolean DATA_LOADING_OK = false;
    public static int DATA_LOADING_TIME = 0;
    public static boolean IS_NOT_LOADING = true;

    //快麦打印机相关
    public static boolean isConnet = false;
    public static boolean isConnetFirst = true;
    public static String toothAddress = "";

    //根据KEY获取设置项
    public static String getSettingValue(String key) {
        return SPUtils.getInstance(MyApp.getContext()).getString(key, "");
    }

    public static String getSettingValueWithDef(String key, String def) {
        return SPUtils.getInstance(MyApp.getContext()).getString(key, def);
    }

    //修改设置项
    public static void setSettingValue(String key, String value) {
        SPUtils.getInstance(MyApp.getContext()).putString(key, value);
    }

    //通过反射获取SN,Android11可用
    public static String getDeviceSN() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 通过网络接口取mac,Android11可用
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String mac = "";
        try {
            String path = "sys/class/net/eth0/address";
            FileInputStream fis_name = new FileInputStream(path);
            byte[] buffer_name = new byte[1024 * 8];
            int byteCount_name = fis_name.read(buffer_name);
            if (byteCount_name > 0) {
                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
            }

            if (mac.length() == 0) {
                path = "sys/class/net/eth0/wlan0";
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[1024 * 8];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    mac = new String(buffer, 0, byteCount, "utf-8");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac.trim();
    }

}
