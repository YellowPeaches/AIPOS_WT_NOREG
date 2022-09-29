package com.wintec.lamp.utils.scale;

import android.content.Context;
import android.util.Log;

import com.wintec.lamp.utils.log.Logging;

import cn.wintec.wtandroidjar.SCL;

public class ScaleForS100 extends ScalesObject {

    private SCL scl;
    private Logging logging;

    public ScaleForS100(ScalesCallback callback, Context context) {
        super(callback);
        logging = new Logging(context);
        scl = new SCL(SCALES_DEVICES);
        runnable = new Runnable() {
            @Override
            public void run() {
                readData();
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    /**
     * WGT:1 0.182P 0.000
     * status bit0 表示稳定标志，值1为秤已经稳定 &1
     * bit1 表示零点标志，值1为秤在零点 &2
     * bit2 表示去皮标志，值1位秤已去皮 &4
     * 过载 WGT:0--OL--P 0.000
     */
    protected void readData() {
        String buffer_ = "";
        while (isReadScales) {
            try {
                buffer_ = new String(scl.ReadData(), "GBK");
                int pos1 = buffer_.indexOf("WGT:");
                int pos2 = buffer_.indexOf("\r\n", pos1);
//                logging.i(buffer_+"|"+pos1+"|"+pos2);
                if (pos1 >= 0 && pos2 - pos1 == 18) {
                    buffer_ = buffer_.substring(pos1, pos2);
                    // 净重
                    float net = Float.valueOf(buffer_.substring(5, 11));
                    // 状态
                    int status = Integer.parseInt(buffer_.substring(4, 5));
                    String scaleStatus = "";
                    if (status == 0 || status == 4) {
                        scaleStatus = "CH";
                    } else if ((status == 1 || status == 5) && net != 0) {
                        scaleStatus = "ST";
                    } else if (status == 3 && net < 0) {
                        scaleStatus = "LL";
                    } else if (status == 3 || status == 7) {
                        scaleStatus = "EM";
                    } else {
                        scaleStatus = "EM";
                    }
                    // 皮重
                    float tare = Float.valueOf(buffer_.substring(12));
                    // 回调
                    callback.getData(net, preNet, tare, scaleStatus);
                    // 记录这一次重量
                    preNet = net;
                }
            } catch (Exception e) {
                Log.i("test", e.toString());
//                logging.i(e.toString());
                if (e.toString().contains("OL")) {
                    callback.getData(15.000f, 15.000f, 0, "OL");
                }
            }
        }
    }

    /**
     * 秤归零
     */
    public void sendZero() {
        scl.send_zero();
    }

    /**
     * 设置皮重
     */
    public void sendTare() {
        scl.send_tare();
    }

    /**
     * 设置固定皮重
     */
    public void sendYtare(float tare) {
        scl.send_ytare(tare);
    }

    public void close() {
        isReadScales = false;
        scl.SCL_close();
    }
}
