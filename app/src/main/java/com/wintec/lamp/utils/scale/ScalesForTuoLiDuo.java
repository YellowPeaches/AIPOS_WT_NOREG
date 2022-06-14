package com.wintec.lamp.utils.scale;

import android.util.Log;

import com.wintec.lamp.utils.ComIO;

import java.io.UnsupportedEncodingException;
import java.util.List;

import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.entity.Device;
import me.f1reking.serialportlib.listener.ISerialPortDataListener;

/**
 * @author 赵冲
 * @description:
 * @date :2021/6/3 10:26
 */
public class ScalesForTuoLiDuo extends ScalesObject {
    private final static int SCALES_BAUDRATE = 9600;
    private ScalesCallback callback;
    private ComIO comIO;

    public ScalesForTuoLiDuo(ScalesCallback callback) {
        super(callback);
        this.callback = callback;
        this.comIO = new ComIO(SCALES_DEVICES1, SCALES_BAUDRATE);
        Boolean open = comIO.open();
        Log.i("test", "串口打开状态" + open);
        readData();
    }

    private int STcount = 0;
    private int EMcount = 0;

    @Override
    protected void readData() {
        List<Device> allDevices = comIO.getAllDevices();
        for (Device item : allDevices) {
            Log.i("test", item.getName());
        }
        SerialPortHelper serialPortHelper = comIO.getSerialPortHelper();
        send();
        serialPortHelper.setISerialPortDataListener(new ISerialPortDataListener() {


            @Override
            public void onDataReceived(byte[] bytes) {
                String buffer_ = "";
                try {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    buffer_ = new String(bytes, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                int i = buffer_.indexOf(".");
                if (buffer_.length() > i + 4 && i - 1 > 0) {
                    float net = Float.valueOf(buffer_.substring((i - 1), i + 4));

                    if (net == preNet && net > 0.02f) {
                        STcount++;
                    } else {
                        STcount = 0;
                    }

                    if (STcount > 5) {
                        callback.getData(net, preNet, 0, "ST");

                    } else if (net == 0f) {
                        callback.getData(net, preNet, 0, "EM");
                    } else {
                        callback.getData(net, preNet, 0, "WT");
                    }
                    preNet = net;
                }
//                if (buffer_.contains("kg")) {
//                    // try {
//                    String finalBuffer_ = buffer_;
//
//                    String[] s = finalBuffer_.split("S S");
//                    if (s.length >= 3) {
//                        String str = s[1].substring(0, s[1].length() - 5);
//                        String kg = str.replace("kg", "");
//                        float net = Float.valueOf(kg);
//                        callback.getData(net, preNet, 0, "ST");
//                        preNet = net;
//                    }
//                }


//                        }
//                    } catch (Exception e) {
//                        Log.i("test", "read data error:" + e.toString());
//                    }


            }

            @Override
            public void onDataSend(byte[] bytes) {

            }
        });
    }

    @Override
    public void sendZero() {

    }

    @Override
    public void sendTare() {

    }

    @Override
    public void sendYtare(float tare) {

    }

    public void close() {
        comIO.close();
    }

    public void sendPlu(String plu) {
        Log.i("test", "send1: 发送数据");
        String cmd = plu;
        comIO.send(cmd);

//        String cmd = "31313131310D0A";
//        comIO.sendHex(cmd);

    }

    public void send() {
        String cmd = "5349520D0A";
        comIO.sendHex(cmd);
//        String cmd = "SIR\r\n";
//        comIO.sendHex(cmd);
    }
}
