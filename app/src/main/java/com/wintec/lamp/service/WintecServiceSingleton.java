package com.wintec.lamp.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.elvishew.xlog.XLog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.OneDimensionalCodeWriter;
import com.wintec.ThreadCacheManager;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.detection.utils.StringUtils;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.AccDto;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.dao.helper.AccDtoHelper;
import com.wintec.lamp.dao.helper.TagMiddleHelper;
import com.wintec.lamp.dao.helper.TraceabilityCodeHelper;
import com.wintec.lamp.entity.Total;
import com.wintec.lamp.utils.BmpUtil;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.lamp.utils.DBUtil;
import com.wintec.lamp.utils.DateUtils;
import com.wintec.lamp.utils.PriceUtils;
import com.wintec.lamp.utils.StrToBrCode;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import cn.wintec.aidl.LabelPrinterService;
import cn.wintec.aidl.ScaleInstructionListener;
import cn.wintec.aidl.ScaleListener;
import cn.wintec.aidl.ScaleService;
import cn.wintec.aidl.WintecManagerService;
import me.f1reking.serialportlib.util.ByteUtils;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @author ??????
 * @description:
 * @date :2021/7/20 10:47
 */
public class WintecServiceSingleton {
    private static class SingletonHolder {
        private static final WintecServiceSingleton INSTANCE = new WintecServiceSingleton();
    }

    public static final WintecServiceSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    protected static String SCALES_DEVICES = Const.getSettingValue("GET_WEIGHT_PORT");
    private WintecManagerService wintecManagerService;  // wintec??????
    private LabelPrinterService labelPrinterService;    // ?????????????????????
    private ScaleService scaleService;
    private Handler mhandler;
    public Context mContext;
    private static int SHOW_FAIL = 16;
    private ScalesCallback scalesCallback;
    private List<TagMiddle> tagMiddles; //??????????????????
    private List<TagMiddle> tagMiddles1; //???????????????
    private List<TagMiddle> muchTagMiddles; //?????????????????????
    private List<TagMiddle> muchTagMiddles1; //???????????????

    private WintecServiceSingleton() {
    }

    public interface ScalesCallback {
        void getData(float net, float tare, String status);
    }

    // ??????????????????
    public ServiceConnection connection;


    /**
     * ???????????????
     *
     * @param context
     * @param hander
     * @param scalesCallback
     */
    public void init(Context context, Handler hander, WintecServiceSingleton.ScalesCallback scalesCallback) {
        this.scalesCallback = scalesCallback;
        mhandler = hander;
        mContext = context;
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    wintecManagerService = WintecManagerService.Stub.asInterface(service);
                    // ?????????????????????
                    labelPrinterService = LabelPrinterService.Stub.asInterface(wintecManagerService.getLabelPrinterService());
                    labelPrinterService.PRN_OpenUSB(0, 0);
                    if ("???????????????".equals(Const.getSettingValue(Const.PRINT_SETTING))) {
                        wintecManagerService.SetKey("NZOUTPAPER");
                    }
                    // ?????????
                    scaleService = ScaleService.Stub.asInterface(wintecManagerService.getScaleService());
                    // ??????????????????
                    openScale();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    XLog.e(e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("???????????????", "onServiceDisconnected: ??????");
            }
        };

    }

    public void bind() {
        if (mContext != null && connection != null) {
            Intent intent = new Intent();
            intent.setPackage("cn.wintec.sdk");
            intent.setAction("cn.wintec.SERVICE");
            boolean isBindService = mContext.bindService(intent, connection, BIND_AUTO_CREATE);
            if (isBindService) {

            }
        }
    }

    public void unbind() {
        try {
            labelPrinterService.PRN_Close();
            scaleService.SCL_Close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (mContext != null && connection != null) {
            mContext.unbindService(connection);
        }
    }

    /**
     * ???
     * ????????????????????????
     */
    public void openScale() {
        if (StringUtils.isEmpty(SCALES_DEVICES)) {
            SCALES_DEVICES = "/dev/ttySAC1";
        }
        try {
            scaleService.SCL_Close();
            scaleService.SCL_Open(SCALES_DEVICES, new ScaleListener.Stub() {
                @Override
                public void onWeightResult(double v, double v1, boolean b, int i, boolean b1, boolean b2) throws RemoteException {
                    // v??????,v1??????,b????????????
                    // i?????? -1??????????????? 1???????????? 0???????????????
                    // b1:?????????0???
                    // b2:????????????
//                    Log.i("WintecServiceSingleton", " ?????????"+v+" ?????????"+v1 +" ?????????"+ i +" b:" +b+" b1:" +b1 +" b2:" +b2);
//                    Log.i("WintecServiceSingleton", (float) v +"");
//
//                    String str1 = v+"";
//                    String str2 = (float) v +"";
//                    if(!str1.equals(str2))
//                    {
//                        Log.i("WintecServiceSingleton", "*******************************************************************************************************");
//                        Log.i("WintecServiceSingleton", "*******************************************************************************************************");
//                    }
                    String status = "";
                    // ??????
                    if (b && !b1) {
                        status = "ST";
                    }
                    // ??????
                    else if (b1 || v + v1 == 0) {
                        // ??????
                        if (v < 0 && v + v1 < 0) {
                            status = "LL";
                        } else {
                            status = "EM";
                        }
                    }
                    // ??????
                    else if (i == 1) {
                        status = "OL";
                    }
                    // ?????????
                    else {
                        status = "CH";
                    }
                    if (scalesCallback != null) {
                        scalesCallback.getData((float) v, (float) v1, status);
                    }
                }

            });
        } catch (Exception e) {
            XLog.e(e);
        }

    }

    public void setZero() {
        try {
            scaleService.SCL_send_zero(new ScaleInstructionListener.Stub() {
                @Override
                public void onResult(int i) {

                }
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    public void setTare() {
        try {
            scaleService.SCL_send_tare(new ScaleInstructionListener.Stub() {
                @Override
                public void onResult(int i) {

                }
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    public void setYTare(float tare) {
        try {
            scaleService.SCL_send_ytare(tare, new ScaleInstructionListener.Stub() {
                @Override
                public void onResult(int i) throws RemoteException {

                }
            });
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    public void roll() {
        try {
            labelPrinterService.PRN_SetPageModePrintArea(10, 10);
        } catch (RemoteException e) {
            e.printStackTrace();
            XLog.e(e);
        }
        try {
            if ("???????????????".equals(Const.getSettingValue(Const.PRINT_SETTING))) {
                //?????????sdk2.1.8??????
//                labelPrinterService.PRN_Hex(new byte[]{0x1F, 0x11, (byte) 0x81});
                labelPrinterService.PRN_Hex(new byte[]{0x1D, 0x0C});
                boolean b = labelPrinterService.PRN_Hex(new byte[]{0x1F, 0x60, 0x01, 0x1});
                if (b) {

                }
                //?????????sdk2.1.8??????
//                labelPrinterService.PRN_Hex(new byte[]{0x1F, 0x11, (byte) 0x80});
            } else {
                labelPrinterService.PRN_print(true);
                labelPrinterService.PRN_Hex(ByteUtils.hexToByteArr("0C"));
            }
        } catch (Exception e) {
            XLog.e(e);
        }

    }

    /**
     * @description:
     * @param byte[] cmd  ??????
     * @return: boolean   ??????????????????
     * @author: dean
     * @time: 2023/1/12 15:10
     */
    public boolean commandSetting(byte[] cmd) {
        boolean success = false;
        try {
//            new byte[]{0x1F, 0x60, 0x01, 0x10}
            success = labelPrinterService.PRN_Hex(cmd);
        } catch (Exception e) {
            XLog.e("??????????????????\n" + e.toString());
        }
        return success;
    }

    public void scaleClose() {
        try {
            scaleService.SCL_Close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param commdity
     * @param total
     * @param net
     * @param num
     * @param discountPrice
     * @return
     * @throws Exception
     */
    public String getTagCode(PluDto commdity, String total, float net, int num, String discountPrice) throws Exception {

        String barCode = createBarCode(commdity, total, net, num, discountPrice);
        if (barCode.length() == 0) {
            throw new Exception("????????????");
        }
        return barCode;
    }

    /**
     * ?????????????????????
     * 13????????? code ?????????6901234567892
     * ???????????? int ??? = getChecksum(code);
     *
     * @return checksum
     */
    public Integer getChecksum(String code) {
//                 char a= code.charAt(0);//??????????????????????????????
//                 int numa = Integer.parseInt(String.valueOf(a));//char?????????int
        int checksum;
        //????????????a
        //????????????????????????????????????????????????
        int checkA = Integer.parseInt(String.valueOf(code.charAt(1))) + Integer.parseInt(String.valueOf(code.charAt(3))) +
                Integer.parseInt(String.valueOf(code.charAt(5))) + Integer.parseInt(String.valueOf(code.charAt(7))) +
                Integer.parseInt(String.valueOf(code.charAt(9))) + Integer.parseInt(String.valueOf(code.charAt(11)));
        //????????????b
        int checkB = checkA * 3;
        //????????????c
        //????????????????????????????????????????????????
        int checkC = Integer.parseInt(String.valueOf(code.charAt(0))) + Integer.parseInt(String.valueOf(code.charAt(2))) +
                Integer.parseInt(String.valueOf(code.charAt(4))) + Integer.parseInt(String.valueOf(code.charAt(6))) +
                Integer.parseInt(String.valueOf(code.charAt(8))) + Integer.parseInt(String.valueOf(code.charAt(10)));
        //????????????d
        int checkD = checkB + checkC;
        //????????????e
        if (checkD % 10 == 0) {
            checksum = 0;
        } else {
            checksum = 10 - checkD % 10;
        }
        return checksum;
    }

    //	true???????????????false????????????   18??????????????????
    public String GetCode128Checksumm(String barcode, Boolean isOdd) {
        //Luhn??????
        long numb = 0;
        long node = 0;
        long total = 0;
        for (int i = barcode.length() - 1; i >= 0; i--) {
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


    public WintecManagerService getWintecManagerService() {
        return wintecManagerService;
    }

    public LabelPrinterService getLabelPrinterService() {
        return labelPrinterService;
    }

    /*
     *
     * */
    public void jointBarCode(char[] barCode, String key, String keyLength, String item) {
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

    public int getCoordinateCount(String settingValue) {
        int i = 5;
        switch (settingValue) {
            case "??????":
                i = 5;
                break;
            case "??????":
                i = 6;
                break;
            case "??????":
                i = 7;
                break;
            case "??????":
                i = 9;
                break;
            case "??????":
                i = 2;
                break;
            default:
                break;

        }
        return i;
    }

    public String charArrayToString(char[] arr) {
        String str = "";
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != '\0') {
                str += arr[i];
            }
        }
        return str;
    }

    public String createBarCode(PluDto commdity, String total, float net, int num, String discountPrice) {
//        int weightPoint = Integer.valueOf(Const.getSettingValue(Const.WEIGHT_POINT));
        String currentNet = com.wintec.lamp.utils.CommUtils.weightToString(net);
        char[] barCode;
        if ("13???".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
            barCode = new char[13];
        } else if (("18???".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH)))) {
            barCode = new char[18];
        } else {
            barCode = new char[19];
        }
//        jointBarCode(barCode, Const.BAR_CODE_PREFIX_COORDINATE, Const.BAR_CODE_PREFIX_LENGTH, Const.getSettingValue(Const.BAR_CODE_PREFIX));
        if (commdity.getPriceUnitA() == 0) {
            String prefix = Const.getSettingValue(Const.BAR_CODE_PREFIX);
            for (int i = 0; i < prefix.length(); i++) {
                barCode[i] = prefix.charAt(i);
            }
        } else {
            String prefix = Const.getSettingValue(Const.BAR_CODE_PREFIX_PIECE);
            for (int i = 0; i < prefix.length(); i++) {
                barCode[i] = prefix.charAt(i);
            }
        }
        if ("1".equals(Const.getSettingValue(Const.ITEM_NO_REPLACE_PLU))) {
            String itemNo = commdity.getItemNo();
            int pluLength = getCoordinateCount(Const.getSettingValue(Const.BAR_CODE_PLU_LENGTH));
            if (itemNo.length() > pluLength)
                itemNo = itemNo.substring(itemNo.length() - pluLength);
            jointBarCode(barCode, Const.BAR_CODE_PLU_COORDINATE, Const.BAR_CODE_PLU_LENGTH, PriceUtils.toCodeBarPLU(itemNo));
        } else {
            jointBarCode(barCode, Const.BAR_CODE_PLU_COORDINATE, Const.BAR_CODE_PLU_LENGTH, PriceUtils.toCodeBarPLU(commdity.getPluNo()));
        }
        jointBarCode(barCode, Const.BAR_CODE_TOTAL_COORDINATE, Const.BAR_CODE_TOTAL_LENGTH, PriceUtils.toPrinterPrice(total));
        jointBarCode(barCode, Const.BAR_CODE_WEIGHT_COORDINATE, Const.BAR_CODE_WEIGHT_LENGTH, PriceUtils.toPrinterWeight(currentNet));
        jointBarCode(barCode, Const.BAR_CODE_PRICE_COORDINATE, Const.BAR_CODE_PRICE_LENGTH, PriceUtils.toPrinterPrice(discountPrice));
        jointBarCode(barCode, Const.BAR_CODE_ARTNO_COORDINATE, Const.BAR_CODE_ARTNO_LENGTH, PriceUtils.toItemNo(commdity.getItemNo()));
        if (commdity.getPriceUnitA() != 0) {
            if ("????????????".equals(Const.getSettingValue(Const.BAR_CODE_PIECT_FLAG))) {
                jointBarCode(barCode, Const.BAR_CODE_WEIGHT_COORDINATE, Const.BAR_CODE_WEIGHT_LENGTH, PriceUtils.toPrinterWeight2(num + ""));
            } else if ("????????????".equals(Const.getSettingValue(Const.BAR_CODE_PIECT_FLAG))) {
                jointBarCode(barCode, Const.BAR_CODE_WEIGHT_COORDINATE, Const.BAR_CODE_WEIGHT_LENGTH, PriceUtils.toPrinterWeight2(num * 1000 + ""));
            } else {
                jointBarCode(barCode, Const.BAR_CODE_WEIGHT_COORDINATE, Const.BAR_CODE_WEIGHT_LENGTH, PriceUtils.toPrinterWeight2(num * 10 + ""));
            }
        }
        String check = "";
        if ("18???".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
            if ("1".equals(Const.getSettingValue(Const.BAR_CODE_IS_CHECK))) {
                if ("?????????".equals(Const.getSettingValue(Const.KEY_ODD_EVEN_CHECK))) {
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

    public Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    // ??????????????????????????????????????????????????????????????????????????????
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * ????????????????????????
     *
     * @param commdity
     * @param totalObj
     * @param num
     * @param isKg
     * @param status
     * @param tare
     * @param mNet
     */
    public void printImgLable(PluDto commdity, Total totalObj, int num, boolean isKg, int status, String tare, float mNet) {
        String discountPrice = totalObj.getPrice();
        String total = totalObj.getTotal();
        String code = "";
        Canvas canvasTag;
        Bitmap bitmap;
        selectCacheLableTag(commdity.getLabelNoA());
        try {
            code = WintecServiceSingleton.getInstance().getTagCode(commdity, total, mNet, num, discountPrice);
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = mhandler.obtainMessage();
            msg.what = SHOW_FAIL;
            msg.obj = "????????????";
            mhandler.sendMessage(msg);
            return;
        }
        if (tagMiddles == null || tagMiddles.size() == 0) {
            Message msg = mhandler.obtainMessage();
            msg.what = SHOW_FAIL;
            msg.obj = "?????????????????????";
            mhandler.sendMessage(msg);
            return;
        }
        //?????????????????? ??????????????????
        try {
            if ("1".equals(Const.getSettingValue(Const.KEY_GET_DATA_UP_PRICE_CHANGE))) {
                uploadChangePriceLog(commdity, totalObj, code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int width = tagMiddles.get(0).getLengths() * 8;
        int height = (tagMiddles.get(0).getBreadths() - 2) * 8;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasTag = new Canvas(bitmap);
        // ?????????
        String finalTotal = total;
        tagMiddles.forEach(item -> {
            Paint paint = new Paint();
            if (item.getOverstriking() == 1) {
                paint.setFakeBoldText(true);
            }
            paint.setTextSize(Float.valueOf(item.getFontSize()));
            List<String> list = new ArrayList<>();

            //drag19??????drag20??????
            //?????????????????????
            {
                if ("drag19".equals(item.getDivId()) && status != 1 && "1".equals(Const.getSettingValueWithDef("DISCOUNT_LINEATION", "0"))) {
                    String lable = null;
                    if (!isKg && commdity.getPriceUnitA() == 0) {
                        lable = CommUtils.Float2String(new BigDecimal(discountPrice).divide(new BigDecimal(2)).floatValue(), 2);
                    } else {
                        lable = " " + commdity.getUnitPriceA() + " ";
                    }
                    list.add(lable);
                } else if ("drag20".equals(item.getDivId()) && status != 1 && "1".equals(Const.getSettingValueWithDef("DISCOUNT_LINEATION", "0"))) {
                    float price = Float.parseFloat(CommUtils.Float2String(commdity.getUnitPriceA(), 2));
                    float total1 = 0;
                    if (commdity.getPriceUnitA() == 0) {
                        total1 = price * mNet;
                    } else {
                        total1 = price * Integer.valueOf(num);
                    }
                    String mTotal = CommUtils.priceToString(Float.valueOf(CommUtils.Float2String(total1, 2)));
                    list.add(" " + mTotal + " ");
                }
            }

            String lable = getLable(commdity, finalTotal, mNet + "", num, discountPrice, isKg, status, tare, item);
            //?????????????????????????????????????????????
            if (Const.sellByNum) {
                if ("drag4".equals(item.getDivId()) && commdity.getPriceUnitA() == 0) {
                    lable = " ";
                    Const.sellByNum = false;
                }
            }
            list.add(lable);
            Collections.reverse(list);
            for (int i = 0; i < list.size(); i++) {
                String lable1 = list.get(i);
                if (list.size() == 2 && i == 1) {
                    paint.setFakeBoldText(false);
                    paint.setStrikeThruText(true);
                    Integer fontSize = item.getFontSize();
                    paint.setTextSize(fontSize > 36 ? (float) (fontSize * 0.6) : fontSize);
                }
                Path path = new Path();
                switch (item.getUnderline()) {
                    case 0:
                        if (item.getItalic() == 1) {
                            if (i == 1) {
                                int x = (width - (int) item.getFontSize() * lable1.length()) / 2;
                                canvasTag.drawText(lable1, x, Float.valueOf(item.getOrdinate()), paint);
                            } else {
                                int x = (width - (int) item.getFontSize() * lable1.length()) / 2;
                                canvasTag.drawText(lable1, x, item.getOrdinate() + Float.valueOf(item.getFontSize()), paint);
                            }

                        } else {
                            if (i == 1) {
                                canvasTag.drawText(lable1, item.getAbscissa(), Float.valueOf(item.getOrdinate()), paint);
                            } else {
                                canvasTag.drawText(lable1, item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()), paint);
                            }
                        }
                        break;
                    case 1:
                        if (item.getItalic() == 1) {
                            int y = (height - (int) item.getFontSize() * lable1.length()) / 2;
                            path.moveTo(item.getAbscissa(), y);
                            path.lineTo(item.getAbscissa(), y + Float.valueOf(item.getFontSize()) * lable1.length());
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        } else {
                            path.moveTo(item.getAbscissa(), item.getOrdinate());
                            path.lineTo(item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()) * lable1.length());
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        }

                        break;
                    case 2:
                        if (item.getItalic() == 1) {
                            int x = (width - (int) item.getFontSize() * lable1.length()) / 2;
                            path.moveTo(x + Float.valueOf(item.getFontSize()) * lable1.length(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            path.lineTo(x, item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        } else {
                            path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()) * lable1.length(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            path.lineTo(item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        }
                        break;
                    case 3:
                        if (item.getItalic() == 1) {
                            int y = (height - (int) item.getFontSize() * lable1.length()) / 2;
                            path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), y + Float.valueOf(item.getFontSize()) * lable1.length());
                            path.lineTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), y);
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        } else {
                            path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), item.getOrdinate() + Float.valueOf(item.getFontSize()) * lable1.length());
                            path.lineTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), item.getOrdinate());
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        }
                        break;
                    case 4:
                        canvasTag.drawText(lable1, item.getAbscissa(), item.getOrdinate(), paint);
                        break;
                }
            }
        });
//        tagMiddles.forEach(item -> {
//            Paint paint = new Paint();
//            if (item.getOverstriking() == 1) {
//                paint.setFakeBoldText(true);
//            }
//            paint.setTextSize(Float.valueOf(item.getFontSize()));
//
//
//            String lable1 = getLable(commdity, total, mNet + "", num, discountPrice, isKg, status, tare, item);
//            Path path = new Path();
//            switch (item.getUnderline()) {
//                case 0:
//                    if (item.getItalic() == 1) {
//                        int x = (width - (int) item.getFontSize() * lable1.length()) / 2;
//                        canvasTag.drawText(lable1, x, item.getOrdinate() + Float.valueOf(item.getFontSize()), paint);
//                    } else {
//                        canvasTag.drawText(lable1, item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()), paint);
//                    }
//                    break;
//                case 1:
//                    if (item.getItalic() == 1) {
//                        int y = (height - (int) item.getFontSize() * lable1.length()) / 2;
//                        path.moveTo(item.getAbscissa(), y);
//                        path.lineTo(item.getAbscissa(), y + Float.valueOf(item.getFontSize()) * lable1.length());
//                        canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
//                    } else {
//                        path.moveTo(item.getAbscissa(), item.getOrdinate());
//                        path.lineTo(item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()) * lable1.length());
//                        canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
//                    }
//
//                    break;
//                case 2:
//                    if (item.getItalic() == 1) {
//                        int x = (width - (int) item.getFontSize() * lable1.length()) / 2;
//                        path.moveTo(x + Float.valueOf(item.getFontSize()) * lable1.length(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
//                        path.lineTo(x, item.getOrdinate() + Float.valueOf(item.getFontSize()));
//                        canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
//                    } else {
//                        path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()) * lable1.length(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
//                        path.lineTo(item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
//                        canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
//                    }
//                    break;
//                case 3:
//                    if (item.getItalic() == 1) {
//                        int y = (height - (int) item.getFontSize() * lable1.length()) / 2;
//                        path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), y + Float.valueOf(item.getFontSize()) * lable1.length());
//                        path.lineTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), y);
//                        canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
//                    } else {
//                        path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), item.getOrdinate() + Float.valueOf(item.getFontSize()) * lable1.length());
//                        path.lineTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), item.getOrdinate());
//                        canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
//                    }
//                    break;
//                case 4:
//                    canvasTag.drawText(lable1, item.getAbscissa(), item.getOrdinate(), paint);
//                    break;
//            }
//        });
        //??????????????????
        BarcodeFormat barcodeFormat;
        OneDimensionalCodeWriter oneDimensionalCodeWriter;
        if (tagMiddles1.size() == 1) {
            tagMiddles1.get(0).setAbscissa(tagMiddles1.get(0).getAbscissa());
            tagMiddles1.get(0).setOrdinate(tagMiddles1.get(0).getOrdinate());
            Integer barLeft = tagMiddles1.get(0).getAbscissa();
            Integer barTop = tagMiddles1.get(0).getOrdinate();
            Integer barWidth = tagMiddles1.get(0).getLength();
            Integer barHeight = tagMiddles1.get(0).getBreadth();
            Bitmap tempBitmap = null;
            if ("1".equals(Const.getSettingValue(Const.BAR_CODE_OR_QRCODE_FLAG))) {
                tempBitmap = StrToBrCode.createQRCode(code, barHeight, barHeight);
                switch (tagMiddles1.get(0).getUnderline()) {
                    case 0:
                        break;
                    case 1:
                        tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 90);
                        break;
                    case 2:
                        tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 180);
                        break;
                    case 3:
                        tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 270);
                        break;
                    default:
                        break;
                }
                canvasTag.drawBitmap(tempBitmap, barLeft, barTop, null);

                if ("1".equals(Const.getSettingValue(Const.QRCODE_NUMBER_FLAG))) {
                    Paint paint1 = new Paint();
                    Path path = new Path();
                    int textSize = tempBitmap.getHeight() / 5;
                    paint1.setTextSize(textSize);

                    switch (tagMiddles1.get(0).getUnderline()) {
                        case 0:
                            paint1.setTextAlign(Paint.Align.CENTER);
                            canvasTag.drawText(code, barLeft + tempBitmap.getWidth() / 2, barTop + tempBitmap.getHeight() + textSize, paint1);
                            break;
                        case 1:
                            int x = barLeft - tempBitmap.getHeight() / 4;// * lable1.length()) / 2;
                            int y = barTop - barHeight / 2;
                            path.moveTo(x, y);
                            path.lineTo(x, 7 * y + tagMiddles1.get(0).getOrdinate() + tagMiddles1.get(0).getFontSize() * 2);
                            canvasTag.drawTextOnPath(code, path, 0, 0, paint1);
                            break;
                        case 2:
                            tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 180);
                            break;
                        case 3:
                            tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 270);
                            break;
                        default:
                            break;
                    }
                }
            } else {
                if ("13???".equals(Const.getSettingValue(Const.BAR_CODE_LENGTH))) {
                    barcodeFormat = BarcodeFormat.EAN_13;
                    oneDimensionalCodeWriter = new EAN13Writer();
                } else {
                    barcodeFormat = BarcodeFormat.CODE_128;
                    oneDimensionalCodeWriter = new Code128Writer();
                }
                try {
                    tempBitmap = StrToBrCode.createBarcode(code, barWidth, barHeight, true, barcodeFormat, oneDimensionalCodeWriter);
                } catch (Exception e) {
                    Message msg = mhandler.obtainMessage();
                    msg.what = SHOW_FAIL;
                    msg.obj = "??????????????????";
                    mhandler.sendMessage(msg);
                    e.printStackTrace();
                    return;
                }
                // ????????????
                switch (tagMiddles1.get(0).getUnderline()) {
                    case 0:
                        break;
                    case 1:
                        tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 90);
                        break;
                    case 2:
                        tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 180);
                        break;
                    case 3:
                        tempBitmap = BmpUtil.rotateBitmap(tempBitmap, 270);
                        break;
                    default:
                        break;
                }
                canvasTag.drawBitmap(tempBitmap, barLeft, barTop, null);
            }

            if (tempBitmap != null) {
                tempBitmap.recycle();
            }
        }
        // ?????????
        List<TagMiddle> traceabilityCode = TagMiddleHelper.selectTraceabilityCode();
        if (traceabilityCode != null && traceabilityCode.size() == 1) {
            traceabilityCode.get(0).setAbscissa(traceabilityCode.get(0).getAbscissa());
            traceabilityCode.get(0).setOrdinate(traceabilityCode.get(0).getOrdinate());
            Integer barLeft = traceabilityCode.get(0).getAbscissa();
            Integer barTop = traceabilityCode.get(0).getOrdinate();
            Integer barWidth = traceabilityCode.get(0).getLength();
            Integer barHeight = traceabilityCode.get(0).getBreadth();
            Bitmap tempBitmap = null;
            String TCode = TraceabilityCodeHelper.selectByCode(commdity.getPluNo(), commdity.getItemNo());
            tempBitmap = StrToBrCode.createQRCode(TCode, barHeight, barHeight);
            canvasTag.drawBitmap(tempBitmap, barLeft, barTop, null);

            canvasTag.drawBitmap(tempBitmap, traceabilityCode.get(0).getAbscissa(), traceabilityCode.get(0).getOrdinate(), null);
            if (tempBitmap != null) {
                tempBitmap.recycle();
            }
        }
        if ("????????????".equals(Const.getSettingValue(Const.TAG_DIRECTION))) {
            bitmap = BmpUtil.rotateBitmap(bitmap, 180);
        }
        printBitMap(width, height, bitmap);

        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    /**
     * ???????????????
     *
     * @param code
     */
    public void printAllLable(List<GoodsModel> goodModels, String code) {
        Canvas canvasTag;
        Bitmap bitmap;
        selectCacheLableTag(-1);
        int width = 0;
        int height = 0;
        if (muchTagMiddles != null && muchTagMiddles.size() > 0) {
            width = muchTagMiddles.get(0).getLengths() * 8;
            height = (muchTagMiddles.get(0).getBreadths() - 2) * 8;
        } else if (muchTagMiddles1 != null && muchTagMiddles1.size() > 0) {
            width = muchTagMiddles1.get(0).getLengths() * 8;
            height = (muchTagMiddles1.get(0).getBreadths() - 2) * 8;
        } else {
            return;
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasTag = new Canvas(bitmap);
        int finalWidth = width;
        int finalHeight = height;
        if (muchTagMiddles != null && muchTagMiddles.size() > 0) {
            muchTagMiddles.forEach(item -> {
                Paint paint = new Paint();
                if (item.getOverstriking() == 1) {
                    paint.setFakeBoldText(true);
                }
                paint.setTextSize(Float.valueOf(item.getFontSize()));
                String lable1 = getLableMuch(goodModels, item);
                Path path = new Path();
                switch (item.getUnderline()) {
                    case 0:
                        if (item.getItalic() == 1) {
                            int x = (finalWidth - (int) item.getFontSize() * lable1.length()) / 2;
                            canvasTag.drawText(lable1, x, item.getOrdinate() + Float.valueOf(item.getFontSize()), paint);
                        } else {
                            canvasTag.drawText(lable1, item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()), paint);
                        }
                        break;
                    case 1:
                        if (item.getItalic() == 1) {
                            int y = (finalHeight - (int) item.getFontSize() * lable1.length()) / 2;
                            path.moveTo(item.getAbscissa(), y);
                            path.lineTo(item.getAbscissa(), y + Float.valueOf(item.getFontSize()) * lable1.length());
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        } else {
                            path.moveTo(item.getAbscissa(), item.getOrdinate());
                            path.lineTo(item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()) * lable1.length());
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        }
                        break;
                    case 2:
                        if (item.getItalic() == 1) {
                            int x = (finalWidth - (int) item.getFontSize() * lable1.length()) / 2;
                            path.moveTo(x, item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            path.lineTo(x - Float.valueOf(item.getFontSize()) * lable1.length(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        } else {
                            path.moveTo(item.getAbscissa(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            path.lineTo(item.getAbscissa() - Float.valueOf(item.getFontSize()) * lable1.length(), item.getOrdinate() + Float.valueOf(item.getFontSize()));
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        }
                        break;
                    case 3:
                        if (item.getItalic() == 1) {
                            int y = (finalHeight - (int) item.getFontSize() * lable1.length()) / 2;
                            path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), y);
                            path.lineTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), y - Float.valueOf(item.getFontSize()) * lable1.length());
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        } else {
                            path.moveTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), item.getOrdinate());
                            path.lineTo(item.getAbscissa() + Float.valueOf(item.getFontSize()), item.getOrdinate() - Float.valueOf(item.getFontSize()) * lable1.length());
                            canvasTag.drawTextOnPath(lable1, path, 0, 0, paint);
                        }
                        break;
                    case 4:
                        canvasTag.drawText(lable1, item.getAbscissa(), item.getOrdinate(), paint);
                        break;
                }
            });
        }
        if (muchTagMiddles1 != null && muchTagMiddles1.size() == 1) {
            muchTagMiddles1.get(0).setAbscissa(muchTagMiddles1.get(0).getAbscissa());
            muchTagMiddles1.get(0).setOrdinate(muchTagMiddles1.get(0).getOrdinate());
            Integer barLeft = muchTagMiddles1.get(0).getAbscissa();
            Integer barTop = muchTagMiddles1.get(0).getOrdinate();
            Integer barWidth = muchTagMiddles1.get(0).getLength();
            Integer barHeight = muchTagMiddles1.get(0).getBreadth();
            Bitmap tempBitmap = null;
            tempBitmap = StrToBrCode.createQRCode("https://blog.csdn.net/u012877472/article/details/51500691", barHeight, barHeight);
            canvasTag.drawBitmap(tempBitmap, barLeft, barTop, null);
            if ("1".equals(Const.getSettingValue(Const.QRCODE_NUMBER_FLAG))) {
                Paint paint1 = new Paint();
                int textSize = tempBitmap.getHeight() / 4;
                paint1.setTextSize(textSize);
                paint1.setTextAlign(Paint.Align.CENTER);
                canvasTag.drawText(code, barLeft + tempBitmap.getWidth() / 2, barTop + tempBitmap.getHeight() + textSize, paint1);
            }
            if (tempBitmap != null) {
                tempBitmap.recycle();
            }
        }
        if ("????????????".equals(Const.getSettingValue(Const.TAG_DIRECTION))) {
            bitmap = BmpUtil.rotateBitmap(bitmap, 180);
        }
        printBitMap(width, height, bitmap);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private String getLableMuch(List<GoodsModel> goodModels, TagMiddle item) {

        if (item.getCodeSystem() == 2) {
            return item.getTagName();
        }
        String value = "";
        String drag = item.getDivId().replaceAll("drag", "");
        switch (drag) {
            case "3":
                value = Const.getSettingValue(Const.SCALE_NO);
                break;
            case "5":
                // todo ????????????
                break;
            case "6":
                // todo ????????????
                break;
            case "7":
                // todo ??????
                break;
            case "8":
                // todo ??????
                break;
            case "9":
                // todo ??????
                break;
            case "10":
                // todo ????????????
                break;
//            case "11":
//                // todo ?????????
//                break;
            case "12":
                // todo ????????????
                value = Const.getSettingValue(Const.KEY_BRANCH_NAME);
                break;
            case "13":
                // todo ????????????
                break;
            case "14":
                // todo ????????????
                break;
            case "15":
                value = Const.getSettingValue(Const.KEY_BRANCH_ID);
                break;
            case "16":
                // todo ???????????????
                break;
            case "17":
                // todo ???????????????
                break;
            case "18":
                // todo ?????????
                break;
            case "20":
                final float[] total = {0f};
                goodModels.forEach(goodsModel -> {
                    total[0] += Float.valueOf(goodsModel.getTotal());
                });
                value = CommUtils.priceToString(total[0]);
                break;
            case "22":
                // todo ???????????????
                break;
            case "23":
                value = DateUtils.getDate(item.getDateFormat());
                break;
            case "24":
                value = DateUtils.getDate(item.getDateFormat());
                break;
            case "27":
                //?????????
                //value = commdity.getSellByDate();
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

    /**
     * ????????????
     *
     * @param bitmap
     */
    public void printBitMap(int width, int height, Bitmap bitmap) {


        // bitmap = BmpUtil.scaleBitmap(bitmap,0.5f);
        try {
            labelPrinterService.PRN_SetPageModePrintArea(460, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if ("????????????".equals(Const.getSettingValue(Const.TAG_DIRECTION))) {
                labelPrinterService.PRN_PrintBitmap(bitmap, 440 - width, 0, bitmap.getWidth(), bitmap.getHeight(), 0);
            } else {
                if (width == 320) {
                    labelPrinterService.PRN_PrintBitmap(bitmap, 440 - width, 0, bitmap.getWidth(), bitmap.getHeight(), 0);
                } else if (width == 480) {
                    labelPrinterService.PRN_PrintBitmap(bitmap, 480 - width, 0, bitmap.getWidth(), bitmap.getHeight(), 0);
                } else {
                    labelPrinterService.PRN_PrintBitmap(bitmap, 460 - width, 0, bitmap.getWidth(), bitmap.getHeight(), 0);
                }
            }

//            if ("???????????????".equals(Const.getSettingValue(Const.PRINT_SETTING))) {
//                //?????????sdk2.1.8??????
////                labelPrinterService.PRN_Hex(new byte[]{0x1F, 0x11, (byte) 0x81}); //????????????
//                boolean printSuccess = labelPrinterService.PRN_print(false);
//                if (printSuccess) {
//                    labelPrinterService.PRN_Hex(new byte[]{0x1D, 0x0C});//??????????????????
//                }
//                //?????????sdk2.1.8??????
////                labelPrinterService.PRN_Hex(new byte[]{0x1F, 0x11, (byte) 0x80});
//            } else if ("??????????????????".equals(Const.getSettingValue(Const.PRINT_SETTING))) {
            boolean printSuccess = labelPrinterService.PRN_print(false);
            XLog.d("bitmap??????w*h:  %d * %d", bitmap.getWidth(), bitmap.getHeight());
            if (printSuccess) {
                labelPrinterService.PRN_Hex(ByteUtils.hexToByteArr("0C"));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //?????????????????????????????????,???????????????ListView??????
    public List<String> getPairedData() {
        List<String> data = new ArrayList<String>();
        //????????????????????????
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // ????????????????????????????????????????????????
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) //??????
            {
                data.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = "no";//getResources().getText(R.string.activity_devicelist_none_paired).toString();
            data.add(noDevices);
        }
        return data;
    }

    public String getLable(PluDto commdity, String total, String thisnet, int num, String discountPrice, boolean isKg, int tradeMode, String tare, TagMiddle item) {
        AccDto accDto;//= AccDtoHelper.selectByAccNo(commdity.getEtNo());
        if (commdity.getEtNo() <= 0 || "".equals(commdity.getEtNo())) {
            accDto = AccDtoHelper.selectByAccNo(Integer.parseInt(commdity.getPluNo()));
        } else {
            accDto = AccDtoHelper.selectByAccNo(commdity.getEtNo());
        }
        String unit = item.getUnit();
        String net = thisnet;
        if (unit == null) {
            unit = "";
        }
        String tagName = item.getTagName();
        if (!isKg) {
            if (commdity.getPriceUnitA() == 0) {
                discountPrice = CommUtils.Float2String(Float.parseFloat(discountPrice) / 2, 2);
            }
            net = Float.parseFloat(net) * 2 + "";
            switch (item.getDivId()) {
                case "drag19":
                    unit = item.getUnits();
                    break;
                case "drag4":
                    unit = item.getUnits();
                    break;
                default:
                    break;
            }
        }
        if (commdity.getPriceUnitA() == 1) {
            switch (item.getDivId()) {
                case "drag4":
                    tagName = "??????";
                    if (item.getBz1() == null || "".equals(item.getBz1())) {
                        unit = "pcs";
                    } else {
                        unit = item.getBz1();
                    }

                    break;
                case "drag19":
                    if (item.getBz1() == null || "".equals(item.getBz1())) {
                        unit = "???/pcs";
                    } else {
                        unit = item.getBz1();
                    }
                    break;
                default:
                    break;
            }
        }
        if (item.getCodeSystem() == 2) {
            if (tagName.contains("//")) {
                tagName = " " + tagName + " ";
                final String[] split = tagName.split("//");
                if (commdity.getPriceUnitA() == 1) {
                    tagName = split[1];
                } else {
                    tagName = split[0];
                }
            }
            return tagName;
        }
        String value = "";
        String drag = item.getDivId().replaceAll("drag", "");
        switch (drag) {
            case "1":
                value = commdity.getNameTextA();
                break;
            case "2":
                // todo ???????????????
                break;
            case "3":
                value = Const.getSettingValue(Const.SCALE_NO);
                break;
            case "4":
                if ("1".equals(Const.getSettingValue(Const.BAR_CODE_GRAM_UNIT))) {
                    if (commdity.getPriceUnitA() == 0) {
                        value = (int) (Float.parseFloat(thisnet) * 1000) + "";
                        unit = "g";
                    } else {
                        value = num + "";
                    }
                } else {
                    if (commdity.getPriceUnitA() == 0) {
                        value = net;
                    } else {
                        value = num + "";
                    }
                }
                break;
            case "5":
                // todo ????????????
                break;
            case "6":
                // todo ????????????
                value = commdity.getPluNo();
                break;
            case "7":
                // todo ??????
                break;
            case "8":
                // todo ??????
                break;
            case "9":
                // todo ??????

                break;
            case "10":
                // todo ????????????
                break;
//            case "11":
//                // todo ?????????
//                break;
            case "12":
                // todo ????????????
                value = Const.getSettingValue(Const.KEY_BRANCH_NAME);
                break;
            case "13":
                // todo ????????????
                break;
            case "14":
                // todo ????????????
                break;
            case "15":
                value = Const.getSettingValue(Const.KEY_BRANCH_ID);
                break;
            case "16":
                // todo ???????????????
                break;
            case "17":
                // todo ???????????????
                break;
            case "18":
                // todo ?????????
                break;
            case "19":
                value = discountPrice;
                break;
            case "20":
                value = total;
                break;
            case "22":
                // todo ???????????????
                break;
            case "23":
                value = DateUtils.getDate(item.getDateFormat());
                break;
            case "24":
                value = DateUtils.getDate(item.getDateFormat());
                break;
            case "25":
                value = commdity.getSellByDate();
                break;
            case "26":
                //??????
                value = tare;
                break;
            case "27":
                //?????????
                //value = commdity.getSellByDate();
                break;
            case "29":
                // ??????????????????
                value = DateUtils.getDateAddDays(item.getDateFormat(), Integer.parseInt(commdity.getSellByDate()));
                break;
            case "30":
                // ???????????????
                if (accDto != null) {
                    value = accDto.getContent1();
                    if (value.length() > 20) {
                        value = value.substring(0, 20) + "\n" + value.substring(20);
                    }
                }
                break;
            case "31":
                // ???????????????
                if (accDto != null) {
                    value = accDto.getContent2();
                }
                break;
            case "32":
                // ???????????????
                if (accDto != null) {
                    value = accDto.getContent3();
                }
                break;
            case "33":
                // ???????????????
                if (accDto != null) {
                    value = accDto.getContent4();
                }
                break;
            default:
                break;

        }
        if (item.getUnit() != null) {
            value = value + unit;
        }
        if (item.getCodeSystem() == 1) {
            return value;
        }

        return tagName + ": " + value;
    }

    public void cacheLableTag() {
        tagMiddles1 = TagMiddleHelper.selectBarCode();
        tagMiddles = TagMiddleHelper.selectToLable();
        muchTagMiddles = TagMiddleHelper.selectMuchToLable();
        muchTagMiddles1 = TagMiddleHelper.selectMuchBarCode();
    }

    public void cacheLableTag(Integer labelNo) {
        tagMiddles1 = TagMiddleHelper.selectBarCode(labelNo);
        tagMiddles = TagMiddleHelper.selectToLable(labelNo);
        muchTagMiddles = TagMiddleHelper.selectMuchToLable(labelNo);
        muchTagMiddles1 = TagMiddleHelper.selectMuchBarCode(labelNo);
    }

    public void selectCacheLableTag(Integer labelNo) {
        // if (tagMiddles1 == null || tagMiddles1.size() == 0 || tagMiddles == null || tagMiddles.size() == 0) {
        cacheLableTag();
        //  }
        if (!"1".equals(Const.getSettingValue(Const.BAR_CODE_MULTI_PRICE_SIGN))) {
            //?????????
            cacheLableTag();
        } else {
            //?????????
            if (labelNo == null) {
                cacheLableTag(-1);
            } else {
                cacheLableTag(labelNo);
            }
        }

    }

    public void destory() {
        try {
            labelPrinterService.PRN_Close();
            scaleService.SCL_Close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
      ??????????????????
     */
    private void uploadChangePriceLog(PluDto commdity, Total totalObj, String code) {
        if (totalObj.getTradeMode() == 1) {
            return;
        }
        ThreadCacheManager.getExecutorService().submit(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                String settingValue = Const.getSettingValue(Const.KEY_GET_DATA_UP_PRICE_CHANGE);
                String sql = Const.getSettingValue(Const.KEY_GET_DATA_UP_PRICE_CHANGE_SQL);

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String dateNow = dateFormat.format(date);
                String currentDay = dateNow.substring(0, 8);
                String currentTime = dateNow.substring(8);
                String scaleCode = Const.SN;
                if (!"".equals(scaleCode) || scaleCode != null) {
                    try {
                        scaleCode = scaleCode.replaceAll("[^0-9]", "");
                        scaleCode = scaleCode.substring(scaleCode.length() - 4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    scaleCode = Const.getSettingValue(Const.SCALE_NO);
                }
                String codeNo = Const.KEY_GET_TASK_ID.replaceAll("[^0-9]", "");
                if (codeNo.length() > 4) {
                    codeNo = codeNo.substring(0, 4);
                }

                String oldPrice = totalObj.getPrice() + "00";
                int dIndex = oldPrice.indexOf('.');
                oldPrice = oldPrice.substring(0, dIndex) + oldPrice.substring(dIndex + 1, dIndex + 3);
                String newPrice = (commdity.getUnitPriceA()) + "00";
                dIndex = newPrice.indexOf('.');
                newPrice = newPrice.substring(0, dIndex) + newPrice.substring(dIndex + 1, dIndex + 3);

                if ("1".equals(settingValue) && sql != null) {
                    sql = sql.replace("#{CODE}", "'" + codeNo + "'");
                    sql = sql.replace("#{SCALE_CODE}", "'" + scaleCode + "'");
                    sql = sql.replace("#{PLU_NUMBER}", "'" + commdity.getPluNo() + "'");
                    sql = sql.replace("#{COMMODITY_NAME}", "'" + commdity.getNameTextA() + "'");
                    sql = sql.replace("#{EDITDATE}", "'" + currentDay + "'");
                    sql = sql.replace("#{TIME}", "'" + currentTime + "'");
                    sql = sql.replace("#{STATUS}", "'" + 2 + "'");
//                    sql = sql.replace("#{SUPERVISOR_LOG_ID}", 1+"");
                    sql = sql.replace("#{SCALE_IP}", "'" + getIP() + "'");

                    sql = sql.replace("#{NEW_UNIT_PRICE}", "'" + oldPrice + "'");
                    sql = sql.replace("#{OLD_UNIT_PRICE}", "'" + newPrice + "'");


                    sql = sql.replace("#{OLD_PRICE}", "'" + oldPrice + "'");
                    sql = sql.replace("#{NEW_PRICE}", "'" + newPrice + "'");

                    sql = sql.replace("#{PRINTED_EAN_DATA}", "'" + code + "'");
                }
                try {
                    DBUtil.exesqlint(sql);
                } catch (SQLException throwables) {
                    XLog.e(throwables);
                    throwables.printStackTrace();
                }
            }
        });

    }

    /**
     * ??????ip,??????String
     *
     * @return String
     */
    public String getIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * bitmap????????????
     *
     * @param bitmap
     * @param bitmapQuality ?????? 1-100 int
     * @return String
     */
    private String bitmapToString(Bitmap bitmap, int bitmapQuality) {

        // ??? Bitmap ??????????????????
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
}