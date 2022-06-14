package com.wintec.lamp.utils;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

public class Usb {

    private HashMap<String, UsbDevice> deviceList;
    //USB管理器:负责管理USB设备的类
    private UsbManager manager;
    //找到的USB设备
    private UsbDevice mUsbDevice;
    //代表USB设备的一个接口
    private UsbInterface mInterface;
    private UsbDeviceConnection mDeviceConnection;
    //代表一个接口的某个节点的类:写数据节点
    private UsbEndpoint usbEpOut;
    //代表一个接口的某个节点的类:读数据节点
    private UsbEndpoint usbEpIn;
    //要发送信息字节
    private byte[] sendbytes;
    //接收到的信息字节
    private byte[] receiveytes;
    //上下文
    private Context context;

    public Usb(Context context) {
        this.context = context;
        // 获取USB设备
        manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //获取到设备列表
        deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        int a = 0;
        while (deviceIterator.hasNext()) {
//            if(a==2)break;
//            Log.e("ldm", "vid=" + usb.getVendorId() + "---pid=" + usb.getProductId());
            //if (mVendorID == usb.getVendorId() && mProductID == usb.getProductId()) {//找到指定设备
            mUsbDevice = deviceIterator.next();
            a++;
        }
        //获取设备接口
        for (int i = 0; i < mUsbDevice.getInterfaceCount(); ) {
            // 一般来说一个设备都是一个接口，你可以通过getInterfaceCount()查看接口的个数
            // 这个接口上有两个端点，分别对应OUT 和 IN
            UsbInterface usbInterface = mUsbDevice.getInterface(i);
            mInterface = usbInterface;
            break;
        }
        //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
        if (mInterface.getEndpoint(1) != null) {
            usbEpOut = mInterface.getEndpoint(1);
        }
        if (mInterface.getEndpoint(0) != null) {
            usbEpIn = mInterface.getEndpoint(0);
        }
        if (mInterface != null) {
            // 判断是否有权限
            if (manager.hasPermission(mUsbDevice)) {
                // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                mDeviceConnection = manager.openDevice(mUsbDevice);
                if (mDeviceConnection == null) {
                    return;
                }
                if (mDeviceConnection.claimInterface(mInterface, true)) {
                    Log.i("test", "找到设备接口");
                } else {
                    mDeviceConnection.close();
                }
            } else {
                Log.i("test", "没有权限");
            }
        } else {
            Log.i("test", "没有找到设备接口！");
        }

    }

    public void sendToUsb(String str) {
        sendbytes = str.getBytes();
        int ret = -1;
        // 发送准备命令
        ret = mDeviceConnection.bulkTransfer(usbEpOut, sendbytes, sendbytes.length, 5000);
        // 接收发送成功信息(相当于读取设备数据)
        receiveytes = new byte[128];  //根据设备实际情况写数据大小
        ret = mDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 10000);

    }

}
