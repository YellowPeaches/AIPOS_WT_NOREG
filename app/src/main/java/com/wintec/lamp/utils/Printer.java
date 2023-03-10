//package com.wintec.lamp.utils;
//
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbInterface;
//import android.hardware.usb.UsbManager;
//import android.util.Log;
//
//import com.wintec.lamp.dao.entity.Commdity;
//
//import java.util.HashMap;
//import java.util.Iterator;
//
//import HPRTAndroidSDK.HPRTPrinterHelper;
//
//public class Printer {
//    private String Printer_Device = "TP801";
//    private static final String ACTION_USB_PERMISSION = "com.HPRTSDKSample";
//    private UsbDevice device = null;
//    private HPRTPrinterHelper hprtPrinterHelper;
//    private UsbManager mUsbManager = null;
//    private PendingIntent mPermissionIntent = null;
//    private Context context;
//
//    public Printer(Context context) {
//        this.context = context;
//        hprtPrinterHelper = new HPRTPrinterHelper(context, Printer_Device);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        this.context.registerReceiver(mUsbReceiver, filter);
//        mPermissionIntent = PendingIntent.getBroadcast(this.context, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        mUsbManager = (UsbManager) this.context.getSystemService(Context.USB_SERVICE);
//        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//
//        boolean HavePrinter = false;
//        while (deviceIterator.hasNext()) {
//            device = deviceIterator.next();
//            int count = device.getInterfaceCount();
//            for (int i = 0; i < count; i++) {
//                UsbInterface intf = device.getInterface(i);
//                if (intf.getInterfaceClass() == 7) {
//                    HavePrinter = true;
//                    mUsbManager.requestPermission(device, mPermissionIntent);
//                }
//            }
//        }
//    }
//
//    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            try {
//                String action = intent.getAction();
//                if (ACTION_USB_PERMISSION.equals(action)) {
//                    synchronized (this) {
//                        device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                            if (HPRTPrinterHelper.PortOpen(device) != 0) {
//                                hprtPrinterHelper = null;
//                                return;
//                            } else {
//                            }
//                        } else {
//                            return;
//                        }
//                    }
//                }
//                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    if (device != null) {
//                        int count = device.getInterfaceCount();
//                        for (int i = 0; i < count; i++) {
//                            UsbInterface intf = device.getInterface(i);
//                            //Class ID 7???????????????
//                            if (intf.getInterfaceClass() == 7) {
//                                HPRTPrinterHelper.PortClose();
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//            }
//        }
//    };
//
//    /**
//     * ????????????
//     *
//     * @param commdity
//     * @param total
//     * @param net
//     * @param num
//     */
//    public void getTagCode(Commdity commdity, String total, String net, int num, String discountPrice) {
//        try {
//            String str0 = commdity.getItemCode();//.substring(0,7) + PriceUtils.toPrinter(total) + PriceUtils.toPrinter(net);
//            String str1 = str0 + GetCode128Checksumm(str0, true);
//            if (str1.length() == 0) {
//                throw new Exception("???????????????");
//            }
//            // ??????
//            // ???????????????
//            HPRTPrinterHelper.SelectPageMode();
//            // ?????????????????????????????????????????????????????????????????????;???????????????20?????????????????????????????????????????????;???????????????????????????
//            HPRTPrinterHelper.SetPageModePrintArea(210, 0, 200, 300);
//            // ??????????????????????????????
//            HPRTPrinterHelper.ClearPageModePrintAreaData();
//            // ??????????????????
//            HPRTPrinterHelper.SetPageModePrintDirection(3);
//            // ???????????????????????????????????????100??????????????????????????????????????????????????????x,y?????????
//            HPRTPrinterHelper.SetPageModeAbsolutePosition(30, 80);
//            // ?????????????????????????????????72??????code93,sText????????????????????????????????????????????????????????????2????????????????????????0???????????????
//            HPRTPrinterHelper.PrintBarCode(73, "{C" + toEightSun(str1), 2, 85, 2, 0);
//            // ??????????????????
//            // ????????????????????????100???????????????400?????????400
//            HPRTPrinterHelper.SetPageModePrintArea(0, 0, 300, 250);
//            HPRTPrinterHelper.ClearPageModePrintAreaData();
//
//            HPRTPrinterHelper.SetPageModePrintDirection(2);
//            // iAlignment????????????1?????????(0?????????) iAttribute 1?????????????????? iTextSize 1-8??????????????????
//            HPRTPrinterHelper.PrintText(commdity.getName() + "", 1, 2, 1);
//            // ????????????
//            String time = DateUtils.stampToDate(System.currentTimeMillis() + "");
//            if (commdity.getUnitId() == 0) {
//                HPRTPrinterHelper.PrintText("????????????:" + time + "\n?????????" + discountPrice + "???/??????\n?????????" + net + "??????\n", 0, 0, 0);
//            } else {
//                HPRTPrinterHelper.PrintText("????????????:" + time + "\n?????????" + discountPrice + "???/???\n?????????" + num + "???\n", 0, 0, 0);
//            }
//
//            HPRTPrinterHelper.PrintText("?????????" + total + "???", 0, 2, 1);
//            HPRTPrinterHelper.PrintText(SPUtils.getInstance(context).getString("branchName", "?????????????????????"), 0, 0, 0);
//            //????????????????????????100???????????????400?????????400
//            HPRTPrinterHelper.SetPageModePrintArea(0, 0, 60, 250);
//            HPRTPrinterHelper.SetPageModePrintDirection(2);
//            HPRTPrinterHelper.PrintText(PriceUtils.toCodeBarPLU(commdity.getId() + ""), 0, 1, 0);
//            HPRTPrinterHelper.PrintAndReturnStandardMode();
//        } catch (Exception e) {
//            Log.i("test", "????????????" + e.toString());
//        }
//    }
//
//    //	true???????????????false????????????
//    public String GetCode128Checksumm(String barcode, Boolean isOdd) {
//        //Luhn??????
//        long numb = 0;
//        long node = 0;
//        long total = 0;
//        for (int i = barcode.length() - 1; i >= 0; i--) {
////            if(!Common.StringValidate.IsInt(barcode[i].ToString()))
////            {
////                continue;
////            }
////            numb = Convert.ToInt32(barcode[i].ToString());
//            numb = Long.parseLong(barcode.toCharArray()[i] + "");
//            if (isOdd) {
//                //ODD
//                if (i % 2 == 0) {
//                    node = numb * 3;
//                } else {
//                    node = numb;
//                }
//            } else {
//                //EVEN
//                if (i % 2 != 0) {
//                    node = numb * 3;
//                } else {
//                    node = numb;
//                }
//            }
//
//            total += node;
//        }
//
//        long checksum = total % 10;
//        if (checksum == 0) {
//            return "0";
//        } else {
//            return (10 - checksum) + "";
//        }
//    }
//
//    public static String toEightSun(String s) {
//        String r = "";
//        byte cmd[] = new byte[s.length() / 2];
//        int i = 0;
//        String subs;
//        for (int k = 0; k < s.length() / 2; k++) {
//
//            cmd[k] = Integer.valueOf(s.substring(i, i + 2), 10).byteValue();
//            i += 2;
//
//        }
//        subs = new String(cmd);
//
//        return subs;
//    }
//}
