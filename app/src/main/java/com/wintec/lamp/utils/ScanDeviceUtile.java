package com.wintec.lamp.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wintec.lamp.base.Const;

import androidx.annotation.RequiresApi;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * @author 赵冲
 * @description:
 * @date :2021/1/11 13:59
 */

public class ScanDeviceUtile {

    private static final String TAG = ScanDeviceUtile.class.getSimpleName();

    /**
     * 核心池大小
     **/
    private static final int CORE_POOL_SIZE = 1;
    /**
     * 线程池最大线程数
     **/
    private static final int MAX_IMUM_POOL_SIZE = 255;

    private String mDevAddress;// 本机IP地址-完整
    private String mLocAddress;// 局域网IP地址头,如：192.168.1.
    private Runtime mRun = Runtime.getRuntime();// 获取当前运行环境，来执行ping，相当于windows的cmd
    private Process mProcess = null;// 进程
    private String mPing = "ping -c 1 -w 3 ";// 其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
    private List<String> mIpList = new ArrayList<String>();// ping成功的IP地址
    private ThreadPoolExecutor mExecutor;// 线程池对象
    private Handler handler;
    private ExecutorService executorService;

    public ScanDeviceUtile(Handler handler) {
        this.handler = handler;
    }

    /**
     * TODO<扫描局域网内ip，找到对应服务器>
     *
     * @return void
     */
    public List<String> scan() {
        mDevAddress = getHostIP();// 获取本机IP地址
        mLocAddress = getLocAddrIndex(mDevAddress);// 获取本地ip前缀
        Log.e(TAG, "开始扫描设备,本机Ip为：" + mDevAddress);

        if (TextUtils.isEmpty(mLocAddress)) {
            Log.e(TAG, "扫描失败，请检查wifi网络");
            return null;
        }
        /**
         * 1.核心池大小 2.线程池最大线程数 3.表示线程没有任务执行时最多保持多久时间会终止
         * 4.参数keepAliveTime的时间单位，有7种取值,当前为毫秒
         * 5.一个阻塞队列，用来存储等待执行的任务，这个参数的选择也很重要，会对线程池的运行过程产生重大影响
         * ，一般来说，这里的阻塞队列有以下几种选择：
         */
        executorService = Executors.newCachedThreadPool();
        /*mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE,
                2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
                CORE_POOL_SIZE));*/
        // 新建线程池
        for (int i = 1; i < 255; i++) {// 创建256个线程分别去ping
            final int lastAddress = i;// 存放ip最后一位地址 1-255
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String ping = ScanDeviceUtile.this.mPing + mLocAddress
                            + lastAddress;
                    String currnetIp = mLocAddress + lastAddress;
                    if (mDevAddress.equals(currnetIp)) // 如果与本机IP地址相同,跳过
                        return;

                    try {
                        mProcess = mRun.exec(ping);
                        int result = mProcess.waitFor();
//                        ALog.e(TAG, "正在扫描的IP地址为：" + currnetIp + "返回值为：" + result);
                        if (result == 0) {
//                            ALog.e(TAG, "扫描成功,Ip地址为：" + currnetIp);
                            mIpList.add(currnetIp);
                            // sendMsg(currnetIp);
                        } else {
                            // 扫描失败
//                            ALog.e(TAG, "扫描失败");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "扫描异常" + e.toString());
                    } finally {
                        if (mProcess != null)
                            mProcess.destroy();
                    }
                }
            };
//            mExecutor.execute(run);
            executorService.execute(run);
        }
//        mExecutor.shutdown();
        executorService.shutdown();
        while (true) {
            try {
//                if (mExecutor.isTerminated()) {// 扫描结束,开始验证
                if (executorService.isTerminated()) {
                    Log.e(TAG, "扫描结束,总共成功扫描到" + mIpList.size() + "个设备.");
//                    this.shutdown();
                    return mIpList;
                }
//                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO<销毁正在执行的线程池>
     *
     * @return void
     */
    public void shutdown() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
    }

    /**
     * TODO<获取本地ip地址>
     *
     * @return String
     */
    private String getLocAddress() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && (ip instanceof Inet4Address)) {
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("", "获取本地ip地址失败");
            e.printStackTrace();
        }

        Log.e(TAG, "本机IP:" + ipaddress);
        return ipaddress;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    /**
     * TODO<获取本机IP前缀>
     *
     * @param devAddress // 本机IP地址
     * @return String
     */
    private String getLocAddrIndex(String devAddress) {
        try {
            if (!devAddress.equals("")) {
                return devAddress.substring(0, devAddress.lastIndexOf(".") + 1);
            }
        } catch (Exception e) {
            Log.i("error", "获取ip失败,请打开网络");
        }

        return null;
    }

    //ip寻址
    public void sendMsg() {
        //判断已存在的ip是否可用
        if (ipCheck()) {
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        List<String> scan = scan();
        //如果没扫到Ip继续
        while (scan == null) {
            scan = scan();
            if (scan != null) {
                break;
            }
            try {
                Thread.sleep(1000);

            } catch (Exception e) {
                Log.i("error", e.getMessage());
            }
        }
        //循环异步请求
        for (String ip : scan) {
            String url = "http://" + ip + ":8080/huashangservice/appVersion/ping/1";
            Log.i("test", url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: " + ip);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.i("test", json);
                    if (response.code() == 200 && json.contains("code") && json.contains("msg")) {
                        Log.d(TAG, "onResponse: " + ip);
                        Const.setSettingValue(Const.KEY_BASE_IP, ip);
                        handler.sendEmptyMessage(0x01);
                    }
                }
            });
        }
    }

    //检验存储Ip是否改变
    public boolean ipCheck() {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (Const.getSettingValue(Const.KEY_BASE_IP) != null && !"".equals(Const.getSettingValue(Const.KEY_BASE_IP))) {
            String ip = Const.getSettingValue(Const.KEY_BASE_IP);
            String url = "http://" + ip + ":8080/huashangservice/appVersion/ping/1";
            Log.i("test", url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            try {
                Response execute = okHttpClient.newCall(request).execute();
                if (execute.code() == 200) {
                    handler.sendEmptyMessage(0x01);
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

}