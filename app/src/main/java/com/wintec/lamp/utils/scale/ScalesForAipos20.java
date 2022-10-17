package com.wintec.lamp.utils.scale;

import com.elvishew.xlog.XLog;
import com.wintec.lamp.utils.ComIO;
import com.wintec.lamp.utils.PriceUtils;

import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.listener.ISerialPortDataListener;

public class ScalesForAipos20 extends ScalesObject {

    private ComIO comIO;
    private final static int SCALES_BAUDRATE = 9600;

    public ScalesForAipos20(ScalesCallback callback) {
        super(callback);
        this.callback = callback;
        this.comIO = new ComIO(SCALES_DEVICES, SCALES_BAUDRATE);
        if (this.comIO.open()) {
            readData();
        }

    }

    private float preNet = 0;
    private String buffer_ = "";

    /**
     * SOH	STX	STA	SIGN	WEIGHT	UNIT	BCC	ETX	EOT	STA2
     * STX(02H)	开始字符
     * SOH(01H)	开始符
     * STA	1字节，STA状态值：'F'(46H):重量溢出或没有开机归零；'S'(53H):重量稳定；'U'(55H):重量不稳定。
     * SIGN	1字节，符号位：'-'(2dH):重量为负；' '(20H):重量非负
     * WEIGHT	6字节，重量"W4W3.W2W1W0",6位ASCII数字
     * UNIT	2字节，重量单位如"kg"
     * BCC	STA~UNIT的BCC校验
     * ETX(03H)	结束字符
     * EOT(04H)	结束符
     * STA2	1字节，状态
     * Bit0-3:0
     * Bit4:当值为1：当前重量为0
     * Bit5:当值为1：当前为去皮模式
     * Bit6:当值为1：重量溢出或开机未归零
     */
    public void readData() {

        SerialPortHelper serialPortHelper = comIO.getSerialPortHelper();
        serialPortHelper.setISerialPortDataListener(new ISerialPortDataListener() {

            @Override
            public void onDataReceived(byte[] bytes) {
                try {
                    String temp = bytesToHex(bytes);
                    buffer_ += temp;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (buffer_.contains("0102") && buffer_.contains("0304")) {
                    try {
                        buffer_ = buffer_.trim();
                        int pos1 = buffer_.indexOf("0102");
                        int pos2 = buffer_.indexOf("0304");
                        buffer_ = buffer_.substring(pos1 + 4, pos2);
                        if (buffer_.length() == 22) {
                            // 状态
                            String status = "";
                            switch (buffer_.substring(0, 2)) {
                                case "46":
                                    status = "OL";
                                    break;
                                case "53":
                                    status = "ST";
                                    break;
                                case "55":
                                    status = "CH";
                                    break;
                            }
                            // 正负符号
                            int sign;
                            switch (buffer_.substring(2, 4)) {
                                case "2d":
                                    sign = -1;
                                    break;
                                default:
                                    sign = 1;
                                    break;
                            }
                            // 净重
                            String netHex = buffer_.substring(4, 16);
                            float net = Float.valueOf(hexStringToString(netHex)) * sign;
                            // 更新状态值
                            // 秤盘清空
                            if (status.equals("ST") && net <= 0) {
                                status = "EM";
                            }
                            //欠载
                            else if (status.equals("OL") && net <= 0) {
                                status = "LL";
                            } else {
                            }
                            // 回调
                            callback.getData(net, preNet, 0, status);
                            // 记录上次净重
                            preNet = net;
                        }
                    } catch (Exception e) {
                        XLog.e(e);
                    }
                    buffer_ = "";
                }

            }

            @Override
            public void onDataSend(byte[] bytes) {

            }
        });


    }


    public void sendZero() {
        String cmd = "3C5A4B3E09";
        comIO.sendHex(cmd);
    }


    public void sendTare() {
        String cmd = "3C544B3E09";
        comIO.sendHex(cmd);
    }

    @Override
    public void sendYtare(float tare) {
        String t = stringToHexString(PriceUtils.toPrinterPrice(tare + ""));
        String cmd = "3C5450" + t + "3E09";
        comIO.sendHex(cmd);
    }


    public void close() {
        comIO.close();
    }

    /**
     * 字节转十六进制
     *
     * @param b 需要进行转换的byte字节
     * @return 转换后的Hex字符串
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


}


