package com.wintec.lamp.server;

import com.hjq.http.config.IRequestServer;

public class ReleaseServer implements IRequestServer {
    @Override
    public String getHost() {
        return "http://demo2.wmdigit.com/";
    }


    @Override
    public String getPath() {
        return "newretail/";
    }
}
