package com.wintec.lamp.server;

import com.hjq.http.config.IRequestServer;

public class TestServer implements IRequestServer {

    @Override
    public String getHost() {
        return "http://se-test.wmdigit.com/";
    }

    @Override
    public String getPath() {
        return "newretail/";
    }
}
