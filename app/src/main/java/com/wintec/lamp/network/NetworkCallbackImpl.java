package com.wintec.lamp.network;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:15
 * 网络监听类
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        //  ToastUtils.showToast(NetworkUtils.getNetWorkTypeName(ContextUtils.getApp()));
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        //  ToastUtils.showToast("网络已断开连接！");
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // ToastUtils.showToast("当前使用的wifi网络！");
                // Log.d(TAG, "onCapabilitiesChanged: 网络类型为wifi");
                // post(NetType.WIFI);
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                // ToastUtils.showToast("当前使用的蜂窝网络！");
                //  Log.d(TAG, "onCapabilitiesChanged: 蜂窝网络");
                // post(NetType.CMWAP);
            } else {
                // Log.d(TAG, "onCapabilitiesChanged: 其他网络");
                //  post(NetType.AUTO);
            }
        }
    }
}
