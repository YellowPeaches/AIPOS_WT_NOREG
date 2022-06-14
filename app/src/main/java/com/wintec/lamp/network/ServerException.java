package com.wintec.lamp.network;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:21
 */
public class ServerException extends RuntimeException {
    public int code;

    public ServerException(int code, String message) {
        super(message);
        this.code = code;
    }
}
