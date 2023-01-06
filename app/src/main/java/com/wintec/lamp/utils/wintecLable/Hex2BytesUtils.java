package com.wintec.lamp.utils.wintecLable;


/**
 * @description:
 * @projectName:tuoliduo
 * @see:com.zc
 * @author:赵冲
 * @createTime:2021/4/1 9:19
 * @version:1.0
 */
public class Hex2BytesUtils {
    private static final String HEX_CHARSET = "0123456789ABCDEF";
    private static final String BLANK = " ";

    /**
     * @Author: 赵冲
     * @param:  [bytes]
     * @return: java.lang.String
     * @date：   2021/3/31 15:45
     * @Description: 转16进制
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    /**
     * @Author: 赵冲
     * @param:  [bytes]
     * @return: java.lang.String
     * @date：   2021/3/31 15:45
     * @Description: 转16进制
     */
    public static String bytesToHexString2(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex+" ");
        }
        return sb.toString();
    }

    public static Integer hexStringToInteger(String hid) {
        String[] s = hid.split(" ");
        String id = "";
        for (int i = s.length-1; i >=0 ; i--) {
            id += s[i];
        }
        Integer i = Integer.parseInt(id, 16);
        return  i;
    }

    /**
     * @Author: 赵冲
     * @param:  [hexString]
     * @return: byte[]
     * @date：   2021/4/23 8:57
     * @Description:16进制字符串转换成byte数组
     */
    public static byte[] hex2Bytes(String hexString){
        int size = 2 ;
        byte[] arrB = hexString.getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        String strTmp = null;
        for (int i = 0; i < iLen; i += size)
        {
            strTmp = new String(arrB, i, 2);
            arrOut[(i / 2)] = ((byte)Integer.parseInt(strTmp, 16));
        }
        return arrOut;
    }
    /**
     * 16进制转GBK
     * @param s
     * @return
     */
    public static String hexToStringGBK(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        try {
            s = new String(baKeyword, "GBK");
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
        return s;
    }

    public static String hexToStringUni(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        try {
            s = new String(baKeyword, "UTF8");
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
        return s;
    }



}
