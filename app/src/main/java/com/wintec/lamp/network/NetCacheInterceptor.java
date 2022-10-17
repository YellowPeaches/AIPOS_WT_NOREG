package com.wintec.lamp.network;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.elvishew.xlog.XLog;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.utils.ContextUtils;
import com.wintec.lamp.utils.DateUtils;
import com.wintec.lamp.utils.NetworkUtils;
import com.wintec.lamp.utils.RSAUtils;
import com.wintec.lamp.utils.UserManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.interfaces.RSAPublicKey;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:40
 */
public class NetCacheInterceptor implements Interceptor {

    public static final String HEADER_MOBILE_ID = "mobileID";
    String SNTime = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (NetworkUtils.isConnected(ContextUtils.getApp())) {
            try {
                String str = Const.SN + "," + DateUtils.getTime(DateUtils.getCurrentTime());
                // Log.d("TAG", "获取打印之前的数据" + str);
                int keyLength = 1024;
//                用公钥加密
//                String publicKeyS = RSA.getPublicKey(keyPair);

                String publicKeyS = UserManager.getInstance().getPublicKey();
                // Log.d("TAG", "获取到后端返回的公钥" + publicKeyS);
                if (!TextUtils.isEmpty(publicKeyS)) {
                    //得到公钥
                    RSAPublicKey publicKeySR = RSAUtils.getPublicKey(publicKeyS);
//                    String pb = RSA.encryptByPublicKey(str, publicKeyS);
                    //进行加密
                    String pb = RSAUtils.publicEncrypt(str, publicKeySR);
                    SNTime = pb;
                    // Log.d("TAG", "加密后的数据：" + pb);

//                    用私钥解密
//                    String privateKeyP = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAklPdoVt2RrSzLVU4YqODc8HBEeZ4Xl7nt/1sBSY9N/9jBZVlw10EvI6dXxWoXDQHK+PYM+l5lD7CtaZ+LsGtiQIDAQABAkAIo3TZGsovwGk3iulqQVb4VX7tbJq8j4PEg+yFnU1N6em7a/tw58dU6cksozGp8O2Z7lnU4/bCF/eo/STmzvEBAiEA31HQo3UAC479cCGAq02X4yfoYE6M2tjiFMyBRsQ45WkCIQCnvbZo7AZHaGG6rXIqLItdU1HU+hXpKnl6b4se8ADjIQIhAMctXXysWcH1Yq2j8/LVXebILUXYSWDXl60dSSAo2uYhAiBlhaPOMvPF6j4kDu/m6Jjcvh3sCgSS33swiCwqaFYRIQIhAI9ntxzxXRKtXEHRFnLLOaJVORC60TrcPRZVLC129Fj0";
//                    String s2 = RSA.decryptByPrivateKey(pb.toString(), privateKeyP);
//                    Log.d("TAG", "解密后的数据：" + s2 + "\n\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            builder.addHeader("Content-Type", "application/json;charset=UTF-8");
            builder.addHeader("autherToken", SNTime);
            builder.addHeader("appName", "AIPOS-20");
            builder.addHeader("osType", "Android");
            builder.addHeader("mobileType", android.os.Build.MODEL == null ? "" : android.os.Build.MODEL);
            Response response = chain.proceed(builder.build());
            XLog.tag("Http").d(String.format("...\n请求链接：%s\n请求参数：%s\n请求响应%s", request.url(), getRequestInfo(request), getResponseInfo(response)));
            return response;
        } else {
            int offlineCacheTime = 30 * 24 * 60 * 60;//离线的时候的缓存的过期时间
            Request.Builder newRequest = chain.request().newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime);
            return chain.proceed(newRequest.build());
        }
    }

    /**
     * 打印请求消息
     *
     * @param request 请求的对象
     */
    private String getRequestInfo(Request request) {
        String str = "";
        if (request == null) {
            return str;
        }
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return str;
        }
        try {
            Buffer bufferedSink = new Buffer();
            requestBody.writeTo(bufferedSink);
            Charset charset = Charset.forName("utf-8");
            str = bufferedSink.readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 打印返回消息
     *
     * @param response 返回的对象
     */
    private String getResponseInfo(Response response) {
        String str = "";
        if (response == null || !response.isSuccessful()) {
            return str;
        }
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        Charset charset = Charset.forName("utf-8");
        if (contentLength != 0) {
            str = buffer.clone().readString(charset);
        }
        return str;
    }
}
