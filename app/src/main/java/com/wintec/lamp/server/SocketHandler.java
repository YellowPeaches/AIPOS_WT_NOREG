package com.wintec.lamp.server;

import android.util.Log;

import com.wintec.lamp.dao.entity.TraceabilityCode;
import com.wintec.lamp.dao.helper.TraceabilityCodeHelper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class SocketHandler implements Runnable {

    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            byte[] outPut = new byte[480];
            while (inputStream.read(outPut) > 0) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                String result = new String(outPut);
                // System.out.println(result.trim());
                String[] results = result.split("\n");
                for (int i = 0; i < results.length; i++) {
                    String[] split = results[i].split(" ");
                    if (split.length == 3) {
                        TraceabilityCode traceabilityCode = new TraceabilityCode(split[0], split[1], split[2]);
                        Log.i("traceabilityCode", traceabilityCode.toString());
                        TraceabilityCode temp = TraceabilityCodeHelper.selectByCodeOrPlu(traceabilityCode.getPluNo(), traceabilityCode.getItemNo());
                        if (temp == null) {
                            TraceabilityCodeHelper.insert(traceabilityCode);
                        } else {
                            traceabilityCode.set_id(temp.get_id());
                            TraceabilityCodeHelper.update(traceabilityCode);
                        }
                    }
                }
                outputStream.write("01".getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }
        }
    }

}
