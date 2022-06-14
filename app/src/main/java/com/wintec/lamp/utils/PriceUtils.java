package com.wintec.lamp.utils;

import android.util.Log;

import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.service.WintecService;

public class PriceUtils {
//    public static String getTagCode(Commdity commdity, String total, String net, int num) {
//
//        String itemCode = commdity.getItemCode();
//        if (itemCode.length()==4)
//        {
//            itemCode = 0 + itemCode;
//        }
//        String str0 = "28"+itemCode  + toPrinter(net)+ toPrinter(total);
//        String str1 = str0 + GetCode128Checksumm(str0, true);
//        if (str1.length() == 0) {
//
//        }
//
//        return  str1;
//    }

    //	true为奇校验；false为偶校验
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

    public static String toPrinterWeight(String number) {
        int coordinateCount = WintecService.getCoordinateCount(Const.getSettingValue(Const.BAR_CODE_WEIGHT_LENGTH));
        String net = number.replace(".", "");
        for (int i = 0; coordinateCount - net.length() > 0; i++) {
            net = "0" + net;
        }
        return net;
    }

    /**
     * 计件
     *
     * @param number
     * @return
     */
    public static String toPrinterWeight2(String number) {
        int coordinateCount = WintecService.getCoordinateCount(Const.getSettingValue(Const.BAR_CODE_WEIGHT_LENGTH));
        for (int i = 0; coordinateCount - number.length() > 0; i++) {
            number = "0" + number;
        }
        return number;
    }

    public static String toPrinterPrice(String number) {
        int coordinateCount = WintecService.getCoordinateCount(Const.getSettingValue(Const.BAR_CODE_PRICE_LENGTH));
        int index = number.indexOf(".");
        if (number.length() - index == 2) {
            number = number + "0";
        }
        String price = number.replace(".", "");

        for (int i = 0; coordinateCount - price.length() > 0; i++) {
            price = "0" + price;
        }
        return price;
    }

    public static String toCodeBarPLU(String pluNo) {
        int coordinateCount = WintecService.getCoordinateCount(Const.getSettingValue(Const.BAR_CODE_PLU_LENGTH));
        for (int i = 0; coordinateCount - pluNo.length() > 0; i++) {
            pluNo = "0" + pluNo;
        }
        return pluNo;
    }

    public static String toPrinterAmount(String number) {
        String d = number.replace(".", "");
        System.out.println("d:" + d);
        String use = "";
        switch (d.length()) {
            case 4:
                use = d + "0";

                break;
            case 3:
                use = "00" + d;
                break;
            default:
                use = d;
                break;


        }
        return use;
    }


    public static String toItemNo(String itemNo) {
        StringBuilder str = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < itemNo.length(); i++) {
            if (itemNo.charAt(i) != '0') {
                flag = true;
            }
            if (flag) {
                str.append(itemNo.charAt(i));
            }
        }
        return str.toString();
    }
}
