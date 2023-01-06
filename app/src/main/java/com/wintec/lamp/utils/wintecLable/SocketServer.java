package com.wintec.lamp.utils.wintecLable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @description:
 * @projectName:winteclabel
 * @see:com.wintec
 * @author:赵冲
 * @createTime:2022/10/25 13:15
 * @version:1.0
 */
public class SocketServer {

    private ObjectHander scalesApiHandler = null;
    private Integer port = 3001;
    private ServerSocket server = null;
    private String ip = null;
    private static SocketServer instance;

    public static SocketServer getInstance(ObjectHander scalesApiHandler, Integer port) {
        if (instance == null) {
            instance = new SocketServer(scalesApiHandler, port);
        }
        return instance;
    }

    private SocketServer(ObjectHander scalesApiHandler, Integer port) {
        this.scalesApiHandler = scalesApiHandler;
        this.port = port;
    }


    public void init() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new ScaleApiException(ErrCode.PORT_OCCUPIED_EXCEPTION);
        }
        while (true) {
            Socket socket = null;
            try {
                socket = server.accept();
                ip = socket.getRemoteSocketAddress().toString();
                scalesApiHandler.setSocket(socket);
                Thread thread = new Thread(scalesApiHandler);
                thread.start();
            } catch (SocketTimeoutException e) {
                throw new ScaleApiException(ErrCode.OUT_TIME_EXCEPTION);
            } catch (IOException e) {
                throw new ScaleApiException(ErrCode.RECEIVE_DATA_EXCEPTION);
            }
        }
    }
    public void destroy()
    {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
