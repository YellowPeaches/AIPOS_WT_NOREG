package com.wintec.lamp.network.yunnetwork.download;

import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends AsyncTask<String, Integer, Void> {
    private static final String TAG = "DownloadThread";

    private String path;                // 文件路径
    private long totalSize = 0;             // 总大小
    private long completeSize = 0;          // 已完成大小
    private String downloadUrl;         // 下载URL
    private DownloadCallback callback;  // 回调

    public DownloadThread(String downloadUrl, DownloadCallback callback, String path) {
        this.path = path;
        this.downloadUrl = downloadUrl;
        this.callback = callback;
    }

    public interface DownloadCallback {
        void onDownloading(int progress);

        void onComplete();
    }

    @Override
    protected Void doInBackground(String... params) {
        InputStream is = null;
        OutputStream os = null;
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            totalSize = conn.getContentLength();
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                os = new FileOutputStream(path);
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                    completeSize += len;
                    // 返回下载进度
                    int progress = (int) (completeSize * 100 / totalSize);
                    callback.onDownloading(progress);
                }
            }
        } catch (Exception e) {
            Log.i("DownloadThread", e.toString());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this.callback.onDownloading(values[0]);
        // 下载完成
        if (values[0] >= 100) {

            this.callback.onComplete();
        }
    }
}
