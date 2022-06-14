package com.wintec.lamp.utils;

import java.util.List;

import me.f1reking.serialportlib.SerialPortHelper;
import me.f1reking.serialportlib.entity.Device;
import me.f1reking.serialportlib.listener.IOpenSerialPortListener;
import me.f1reking.serialportlib.listener.ISerialPortDataListener;

/**
 * @program:
 * @description: usb工具类
 * @author: zcct
 * @create: 2020-12-17 10：14
 **/
public class ComIO {
    private final String TAG = "UsbUtil";
    private SerialPortHelper serialPortHelper = new SerialPortHelper();


    //初始化方法
    public ComIO(String port, int bunRate) {
        // serialPortHelper = new SerialPortHelper.Builder(port, bunRate).build();//支持配置串口号，波特率（默认值115200）
        serialPortHelper.setPort(port);
        serialPortHelper.setBaudRate(bunRate);
//        serialPortHelper.setStopBits(stopBits);// 支持设置停止位 默认值为2
//        serialPortHelper.setDataBits(dataBits); // 支持设置数据位 默认值为8
//        serialPortHelper.setParity(parity); // 支持设置检验位 默认值为0
//        serialPortHelper.setFlowCon(flowCon); // 支持设置流控 默认值为0
//        serialPortHelper. setFlags(flags); // 支持设置标志 默认值为0，O_RDWR  读写方式打开
    }

    // 查询串口设备列表
    public List<Device> getAllDevices() {
        return serialPortHelper.getAllDevices();
    }

    // 查询串口设备地址列表
    public String[] getAllDeicesPath() {
        return serialPortHelper.getAllDeicesPath();
    }

    //打开串口
    public Boolean open() {
        return serialPortHelper.open();
    }

    //关闭串口
    public void close() {
        serialPortHelper.close();
    }

    //发送数据
    public void send(String txt) {
        try {
            serialPortHelper.sendTxt(txt);
//            serialPortHelper.sendBytes(new byte[]{0,0,0,0,0,0,0,0,0,0,0,});
//            serialPortHelper.sendHex("234324131213");
        } catch (Exception e) {
            throw new RuntimeException("发送数据失败");
        }
    }

    public void sendBytes(String txt) {
        try {
            serialPortHelper.sendBytes(txt.getBytes("ASCII"));
        } catch (Exception e) {
            throw new RuntimeException("发送数据失败");
        }
    }

    public void sendHex(String txt) {
        try {
            serialPortHelper.sendHex(txt);
        } catch (Exception e) {
            throw new RuntimeException("发送数据失败");
        }
    }

    //查看串口状态
    public Boolean isOpen() {
        return serialPortHelper.isOpen();
    }


    //获取getSerialPortHelper
    public SerialPortHelper getSerialPortHelper() {
        return this.serialPortHelper;
    }
}
