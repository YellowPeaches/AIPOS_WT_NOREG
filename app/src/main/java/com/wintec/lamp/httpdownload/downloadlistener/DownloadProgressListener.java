package com.wintec.lamp.httpdownload.downloadlistener;

public interface DownloadProgressListener {
    /**
     * 下载进度
     *
     * @param read  进度
     * @param count 总长度
     */
    void update(long read, long count, boolean done);
}
