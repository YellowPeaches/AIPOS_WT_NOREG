package com.wintec.lamp.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.google.zxing.common.BitMatrix;
import com.wintec.aiposui.utils.CommUtils;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.dao.helper.TagMiddleHelper;
import com.wintec.lamp.utils.DateUtils;
import com.wintec.lamp.utils.PriceUtils;

import java.util.List;

import cn.wintec.aidl.LabelPrinterService;
import cn.wintec.aidl.ScaleInstructionListener;
import cn.wintec.aidl.ScaleListener;
import cn.wintec.aidl.ScaleService;
import cn.wintec.aidl.WintecManagerService;
import me.f1reking.serialportlib.util.ByteUtils;

import static android.content.Context.BIND_AUTO_CREATE;

public class WintecService {
    public volatile static WintecService instance;
    protected final static String SCALES_DEVICES = "/dev/ttySAC1";
    private static WintecManagerService wintecManagerService;  // wintec服务
    private static LabelPrinterService labelPrinterService;    // 标签打印机服务
    private static ScaleService scaleService;
    private static Handler mhandler;
    private final static int FLUSH_TOTAL = 15;    // 通知识别
    private final static int SHOW_FAIL = 16;    //
    public static Context mContext;

    public interface ScalesCallback {
        void getData(float net, float tare, String status);
    }

    // 服务绑定回调
    public static ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                wintecManagerService = WintecManagerService.Stub.asInterface(service);
                // 标签打印机服务
                labelPrinterService = LabelPrinterService.Stub.asInterface(wintecManagerService.getLabelPrinterService());
                // 电子秤
                scaleService = ScaleService.Stub.asInterface(wintecManagerService.getScaleService());
                // 开启秤盘监听
                openScale(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public WintecService() {
    }

    public static void init(Context context, Handler hander) {
        mhandler = hander;
        mContext = context;
        Intent intent = new Intent();
        intent.setPackage("cn.wintec.sdk");
        intent.setAction("cn.wintec.SERVICE");
        context.bindService(intent, connection, BIND_AUTO_CREATE);
    }

    public static void unbind() {
        if (mContext != null && connection != null) {
            mContext.unbindService(connection);
        }
    }

    public static void openScale(ScalesCallback scalesCallback) {
        try {
            scaleService.SCL_Close();
            scaleService.SCL_Open(SCALES_DEVICES, new ScaleListener.Stub() {
                @Override
                public void onWeightResult(double v, double v1, boolean b, int i, boolean b1, boolean b2) throws RemoteException {
                    // v净重,v1皮重,b是否稳重
                    // i状态 -1：小于量程 1大于量程 0表示量程内
                    // b1:是否在0点
                    // b2:是否去皮
                    String status = "";
                    // 稳重
                    if (b && !b1) {
                        status = "ST";
                    }
                    // 零点
                    else if (b1) {
                        status = "EM";
                    }
                    // 超载
                    else if (i == 1) {
                        status = "OL";
                    }
                    // 欠载
                    else if (i == -1) {
                        status = "LL";
                    }
                    // 变化中
                    else {
                        status = "CH";
                    }
                    Log.i("test", v + "");
//                    scalesCallback.getData((float) v,(float) v1,status);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setZero() {
        try {
            scaleService.SCL_send_zero(new ScaleInstructionListener.Stub() {
                @Override
                public void onResult(int i) throws RemoteException {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTare() {
        try {
            scaleService.SCL_send_tare(new ScaleInstructionListener.Stub() {
                @Override
                public void onResult(int i) throws RemoteException {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setYTare(float tare) {
        try {
            scaleService.SCL_send_ytare(tare, new ScaleInstructionListener.Stub() {
                @Override
                public void onResult(int i) throws RemoteException {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTagCode(PluDto commdity, String total, String net, int num, String discountPrice) throws Exception {

        String str1 = createBarCode(commdity, total, net, num, discountPrice);
        if (str1.length() == 0) {
            throw new Exception("校验错误");
        }
        return str1;
    }

    public static void printPriceTag(Bitmap bitmap, int width, int height, String code) {
        try {

            labelPrinterService.PRN_OpenUSB(0, 0);
            Thread.sleep(300);
            labelPrinterService.PRN_SetPageModePrintArea(460, height);
            labelPrinterService.PRN_PrintBitmap(bitmap, 0, 0, width, height, 0);
            List<TagMiddle> tagMiddles = TagMiddleHelper.selectBarCode();
            if (tagMiddles.size() == 1) {
                Integer barLeft = tagMiddles.get(0).getAbscissa();
                Integer barTop = tagMiddles.get(0).getOrdinate();
                Integer barWidth = tagMiddles.get(0).getLength();
                Integer barHeight = tagMiddles.get(0).getBreadth();
                if ("13位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                    labelPrinterService.PRN_PrintBarCode(code, 67, barLeft, barTop, barWidth, barHeight, 0);
                    // labelPrinterService.PRN_PrintBarCode(code, 67, 30, 150, 280, 25, 0);//13位 EAN13
                } else {
                    labelPrinterService.PRN_PrintBarCode(code, 73, barLeft, barTop, barWidth, barHeight, 0);
                }
            }
            labelPrinterService.PRN_print(true);
            labelPrinterService.PRN_Hex(ByteUtils.hexToByteArr("0C"));
            labelPrinterService.PRN_Close();
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取条码校验值
     * 13位条码 code 示例：6901234567892
     * 调用方法 int 值 = getChecksum(code);
     *
     * @return checksum
     */
    public static Integer getChecksum(String code) {
//                 char a= code.charAt(0);//取字符串中某一个字符
//                 int numa = Integer.parseInt(String.valueOf(a));//char转换为int
        int checksum;
        //校验步骤a
        //也可以遍历判断获取偶数位的值的和
        int checkA = Integer.parseInt(String.valueOf(code.charAt(1))) + Integer.parseInt(String.valueOf(code.charAt(3))) +
                Integer.parseInt(String.valueOf(code.charAt(5))) + Integer.parseInt(String.valueOf(code.charAt(7))) +
                Integer.parseInt(String.valueOf(code.charAt(9))) + Integer.parseInt(String.valueOf(code.charAt(11)));
        //校验步骤b
        int checkB = checkA * 3;
        //校验步骤c
        //也可以遍历判断获取奇数位的值的和
        int checkC = Integer.parseInt(String.valueOf(code.charAt(0))) + Integer.parseInt(String.valueOf(code.charAt(2))) +
                Integer.parseInt(String.valueOf(code.charAt(4))) + Integer.parseInt(String.valueOf(code.charAt(6))) +
                Integer.parseInt(String.valueOf(code.charAt(8))) + Integer.parseInt(String.valueOf(code.charAt(10)));
        //校验步骤d
        int checkD = checkB + checkC;
        //校验步骤e
        if (checkD % 10 == 0) {
            checksum = 0;
        } else {
            checksum = 10 - checkD % 10;
        }
        return checksum;
    }

    //	true为奇校验；false为偶校验   18校验生成规则
    public static String GetCode128Checksumm(String barcode, Boolean isOdd) {
        //Luhn算法
        long numb = 0;
        long node = 0;
        long total = 0;
        for (int i = barcode.length() - 1; i >= 0; i--) {
//            if(!Common.StringValidate.IsInt(barcode[i].ToString()))
//            {
//                continue;
//            }
//            numb = Convert.ToInt32(barcode[i].ToString());
            numb = Long.parseLong(barcode.toCharArray()[i] + "");
            if (isOdd) {
                //ODD
                if (i % 2 == 0) {
                    node = numb * 3;
                } else {
                    node = numb;
                }
            } else {
                //EVEN
                if (i % 2 != 0) {
                    node = numb * 3;
                } else {
                    node = numb;
                }
            }

            total += node;
        }

        long checksum = total % 10;
        if (checksum == 0) {
            return "0";
        } else {
            return (10 - checksum) + "";
        }
    }

    public static String toEightSun(String s) {
        String r = "";
        byte cmd[] = new byte[s.length() / 2];
        int i = 0;
        String subs;
        for (int k = 0; k < s.length() / 2; k++) {

            cmd[k] = Integer.valueOf(s.substring(i, i + 2), 10).byteValue();
            i += 2;

        }
        subs = new String(cmd);

        return subs;
    }

    public static WintecManagerService getWintecManagerService() {
        return wintecManagerService;
    }

    public static LabelPrinterService getLabelPrinterService() {
        return labelPrinterService;
    }

//    public static ScaleService getScaleService() {
//        return scaleService;
//    }

    public static void jointBarCode(char[] barCode, String key, String keyLength, String item) {
        if ("".equals(Const.getSettingValue(key)) || Const.getSettingValue(key) == null) {
            return;
        }
        int index = Integer.valueOf(Const.getSettingValue(key));
        int length = getCoordinateCount(Const.getSettingValue(keyLength));
        int size = item.length();
        if (size < length) {
            for (int i = 0; i < length - size; i++) {
                item = "0" + item;
            }
        }
        for (int i = index - 1; i < index + length - 1; i++) {
            barCode[i] = item.charAt(i - index + 1);
        }
    }

    public static int getCoordinateCount(String settingValue) {
        int i = 5;
        switch (settingValue) {
            case "五位":
                i = 5;
                break;
            case "六位":
                i = 6;
                break;
            case "七位":
                i = 7;
                break;
            case "九位":
                i = 9;
                break;
            case "二位":
                i = 2;
                break;
            default:
                break;

        }
        return i;
    }

    public static String charArrayToString(char[] arr) {
        String str = "";
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != '\0') {
                str += arr[i];
            }
        }
        return str;
    }

    public static String createBarCode(PluDto commdity, String total, String net, int num, String discountPrice) {
        char[] barCode;
        if ("13位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
            barCode = new char[13];
        } else {
            barCode = new char[18];

        }
//        jointBarCode(barCode, Const.BAR_CODE_PREFIX_COORDINATE, Const.BAR_CODE_PREFIX_LENGTH, Const.getSettingValue(Const.BAR_CODE_PREFIX));
        String prefix = Const.getSettingValue(Const.BAR_CODE_PREFIX);
        for (int i = 0; i < prefix.length(); i++) {
            barCode[i] = prefix.charAt(i);
        }
        if ("1".equals(Const.getSettingValue(Const.ITEM_NO_REPLACE_PLU))) {
            jointBarCode(barCode, Const.BAR_CODE_PLU_COORDINATE, Const.BAR_CODE_PLU_LENGTH, PriceUtils.toCodeBarPLU(commdity.getItemNo()));
        } else {
            jointBarCode(barCode, Const.BAR_CODE_PLU_COORDINATE, Const.BAR_CODE_PLU_LENGTH, PriceUtils.toCodeBarPLU(commdity.getPluNo()));
        }
        jointBarCode(barCode, Const.BAR_CODE_TOTAL_COORDINATE, Const.BAR_CODE_TOTAL_LENGTH, PriceUtils.toPrinterPrice(total));
        jointBarCode(barCode, Const.BAR_CODE_WEIGHT_COORDINATE, Const.BAR_CODE_WEIGHT_LENGTH, PriceUtils.toPrinterWeight(net));
        jointBarCode(barCode, Const.BAR_CODE_PRICE_COORDINATE, Const.BAR_CODE_PRICE_LENGTH, PriceUtils.toPrinterPrice(discountPrice));
        if (commdity.getPriceUnitA() != 0) {
            if ("个位开始".equals(Const.getSettingValue(Const.BAR_CODE_PIECT_FLAG))) {
                jointBarCode(barCode, Const.BAR_CODE_WEIGHT_COORDINATE, Const.BAR_CODE_WEIGHT_LENGTH, PriceUtils.toPrinterWeight((float) num / 1000 + ""));
            } else {
                jointBarCode(barCode, Const.BAR_CODE_WEIGHT_COORDINATE, Const.BAR_CODE_WEIGHT_LENGTH, PriceUtils.toPrinterWeight(num * 1000 + ""));
            }
        }
        String check = "";

        if ("18位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
            if ("1".equals(Const.getSettingValue(Const.BAR_CODE_IS_CHECK))) {
                if ("奇校验".equals(Const.getSettingValue(Const.KEY_ODD_EVEN_CHECK))) {
                    check = GetCode128Checksumm(charArrayToString(barCode), true);
                } else {
                    check = GetCode128Checksumm(charArrayToString(barCode), false);
                }
            }
        } else {
            check = String.valueOf(getChecksum(charArrayToString(barCode)));
        }

        return charArrayToString(barCode) + check;
    }

    public static Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    // 有内容的部分，颜色设置为黑色，可以自己修改成其他颜色
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public static void printLable(PluDto commdity, String total, int num, String discountPrice, boolean isKg, int status, String tare, float mNet) {
        String code = "";
        try {
            code = WintecService.getTagCode(commdity, total, mNet + "", num, discountPrice);
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = mhandler.obtainMessage();
            msg.what = SHOW_FAIL;
            msg.obj = "校验错误";
            mhandler.sendMessage(msg);
            return;
        }
        //打印文字
        List<TagMiddle> tagMiddles = TagMiddleHelper.selectToLable();
        if (tagMiddles == null || tagMiddles.size() == 0) {
            Message msg = mhandler.obtainMessage();
            msg.what = SHOW_FAIL;
            msg.obj = "条码格式不存在";
            mhandler.sendMessage(msg);
            return;
        }
        int width = (tagMiddles.get(0).getLengths() - 2) * 8;
        int height = (tagMiddles.get(0).getBreadths() - 2) * 8;
        try {
            labelPrinterService.PRN_OpenUSB(0, 0);
            Thread.sleep(300);
            labelPrinterService.PRN_SetPageModePrintArea(460, height - height % 10 + 10);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tagMiddles.forEach(item -> {
            String lable1 = getLable(commdity, total, mNet + "", num, discountPrice, isKg, status, tare, item);
            if ("drag1".equals(item.getDivId())) {
                if (lable1.length() * item.getFontSize() > width) {
                    item.setFontSize(width / lable1.length());
                }
            }
            try {
                labelPrinterService.PRN_PrintText(lable1, item.getAbscissa(), item.getOrdinate(), item.getFontSize(), item.getUnderline(), item.getOverstriking() == 1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        List<TagMiddle> tagMiddles1 = TagMiddleHelper.selectBarCode();
        if (tagMiddles1.size() == 1) {
            Integer barLeft = tagMiddles1.get(0).getAbscissa();
            Integer barTop = tagMiddles1.get(0).getOrdinate();
            Integer barWidth = tagMiddles1.get(0).getLength();
            Integer barHeight = tagMiddles1.get(0).getBreadth();
            try {
                if ("13位".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                    labelPrinterService.PRN_PrintBarCode(code, 67, barLeft, barTop, barWidth, barHeight, tagMiddles1.get(0).getUnderline());
                    // labelPrinterService.PRN_PrintBarCode(code, 67, 30, 150, 280, 25, 0);//13位 EAN13
                } else {
                    labelPrinterService.PRN_PrintBarCode(code, 73, barLeft, barTop, barWidth, barHeight, tagMiddles1.get(0).getUnderline());
                }
//                if("宁致打印机".equals(Const.getSettingValue(Const.PRINT_SETTING)))
//                {
//                    String settingValue = Const.getSettingValue(Const.ROTATION_SETTING);
//                    if(settingValue !=null && !"".equals(settingValue))
//                    {
//                        int i = Integer.parseInt(settingValue);
//                        labelPrinterService.PRN_Hex(new byte[]{0x1E,0x52, 0x01, (byte) i});
//                    }
//                }
                labelPrinterService.PRN_print(true);
                labelPrinterService.PRN_Hex(ByteUtils.hexToByteArr("0C"));
                labelPrinterService.PRN_Close();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public static void printAllLable(String code) {


        try {
            labelPrinterService.PRN_OpenUSB(0, 0);
            Thread.sleep(300);
            labelPrinterService.PRN_SetPageModePrintArea(460, 230);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            labelPrinterService.PRN_PrintQRCode(code, 20, 20, 100, 100, 0);
            labelPrinterService.PRN_print(true);
            labelPrinterService.PRN_Hex(ByteUtils.hexToByteArr("0C"));
            labelPrinterService.PRN_Close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public static String getLable(PluDto commdity, String total, String thisnet, int num, String discountPrice, boolean isKg, int tradeMode, String tare, TagMiddle item) {

        if (!isKg) {
            if (commdity.getPriceUnitA() == 0) {
                discountPrice = CommUtils.Float2String(Float.parseFloat(discountPrice) / 2, 2);
            }
            thisnet = Float.parseFloat(thisnet) * 2 + "";
            switch (item.getDivId()) {
                case "drag19":
                    item.setUnit(item.getUnit().replace("kg", "斤"));
                    break;
                case "drag4":
                    item.setUnit(item.getUnit().replace("kg", "斤"));
                    break;
                default:
                    break;
            }
        }
        if (commdity.getPriceUnitA() == 1) {
            switch (item.getDivId()) {
                case "drag4":
                    item.setTagName("件数");
                    item.setUnit(item.getUnit().replace("kg", "件"));
                    item.setUnit(item.getUnit().replace("斤", "件"));
                    break;
                case "drag19":
                    item.setUnit(item.getUnit().replace("kg", "件"));
                    item.setUnit(item.getUnit().replace("斤", "件"));
                    break;
                default:
                    break;
            }
        }
        if (item.getCodeSystem() == 2) {
            return item.getTagName();
        }
        String value = "";
        String drag = item.getDivId().replaceAll("drag", "");
        switch (drag) {
            case "1":
                value = commdity.getNameTextA();
                break;
            case "2":
                // todo 单位不确定
                break;
            case "3":
                value = Const.getSettingValue(Const.SCALE_NO);
                break;
            case "4":
                if (commdity.getPriceUnitA() == 0) {
                    value = thisnet;
                } else {
                    value = num + "";
                }

                break;
            case "5":
                // todo 商品代码
                break;
            case "6":
                // todo 商品编码
                break;
            case "7":
                // todo 产地
                break;
            case "8":
                // todo 等级
                break;
            case "9":
                // todo 配料
                break;
            case "10":
                // todo 存储方式
                break;
            case "11":
                // todo 追溯码
                break;
            case "12":
                // todo 门店名称
                break;
            case "13":
                // todo 门店地址
                break;
            case "14":
                // todo 门店电话
                break;
            case "15":
                value = Const.getSettingValue(Const.KEY_BRANCH_ID);
                break;
            case "16":
                // todo 供应商名称
                break;
            case "17":
                // todo 供应商地址
                break;
            case "18":
                // todo 用户名
                break;
            case "19":
                value = discountPrice;
                break;
            case "20":
                value = total;
                break;
            case "22":
                // todo 自定义文字
                break;
            case "23":
                value = DateUtils.getDate("yy/MM/dd");
                break;
            case "24":
                value = DateUtils.getDate("yy/MM/dd");
                break;
            case "25":
                value = commdity.getSellByDate();
                break;
            case "26":
                //皮重
                value = tare;
                break;
            case "27":
                //二维码
                //value = commdity.getSellByDate();
                break;
            case "29":
                // 保质截至日期
                value = DateUtils.getDateAddDays("yy/MM/dd", Integer.parseInt(commdity.getSellByDate()));
                break;
            default:
                break;

        }
        if (item.getUnit() != null) {
            value = value + item.getUnit();
        }
        if (item.getCodeSystem() == 1) {
            return value;
        }

        return item.getTagName() + ": " + value;
    }
}
