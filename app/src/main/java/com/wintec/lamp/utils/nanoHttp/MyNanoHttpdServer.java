package com.wintec.lamp.utils.nanoHttp;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.wintec.detection.utils.StringUtils;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.utils.pinyin.PinyinUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MyNanoHttpdServer extends NanoHTTPD {

    private static final String TAG = "NanoHTTPD";
    private static Boolean busy = false;

    //实现父类的构造方法
    public MyNanoHttpdServer(int port) {
        super(port);
    }

    //正式开启服务
    public void start() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);

            //向日志文件中写入接收消息的接口已经打开
//            WriteLogToFile.info("The face recognition callback interface has been opened, the port number is 9999.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        int i = 0;
        //打印请求数据
        XLog.i("serve uri: " + session.getUri());
        XLog.i("serve getQueryParameterString: " + session.getQueryParameterString());
        XLog.i("serve getRemoteHostName: " + session.getRemoteHostName());
        XLog.i("serve getRemoteIpAddress: " + session.getRemoteIpAddress());


        session.getHeaders().forEach((key, val) -> {
            XLog.i(key+ " : " + val);
        });

        Map<String, List<String>> parameters = session.getParameters();
        parameters.forEach((key,val)->{
            XLog.i(key+ " : " + val.toString());
        });
        CookieHandler cookies = session.getCookies();


//        if (uri.contains("table/goods") && !busy) {
            busy = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    InputStream sessionInputStream = session.getInputStream();
                    Log.i("time0", (System.currentTimeMillis() - start) + "");
                    String input2String = getStringByInputStream(sessionInputStream);
                    try {
                        sessionInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handlePLUToLocal(input2String);


//                    Log.i("time", (System.currentTimeMillis() - start) + "");
                    XLog.i("转化信息serve: " + input2String);
                }
            }).start();
//        }

        try {
            String msg = "<html><body><h1>Hello server,HaHHH</h1></body></html>";
            return newFixedLengthResponse("200");
        } catch (Exception exception) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal Server Error!!!");
        }

    }

    public String getStringByInputStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[10240];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
        } catch (Exception e) {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e1) {
            }
        }
        return outputStream.toString();
    }

    public void handlePLUToLocal(String pluInfo) {
        /**
         * @description: plu插入到本地
         * @param pluInfo   plu信息字符串
         * @return: void
         * @author: dean
         * @time: 2022/12/9 13:57
         */

        if (StringUtils.isNotEmpty(pluInfo)) {

            final String[] pluLine = pluInfo.split("\r\n");
            PluDto pluDto = new PluDto();
            for (int i = 1; i < pluLine.length; i = i + 2) {
                try {
                    String[] onePlu = pluLine[i].split(",");

                    pluDto.setPluNo(onePlu[0]);
                    pluDto.setNameTextA(onePlu[1]);
                    if ("kg".equals(onePlu[2].toLowerCase())) {
                        pluDto.setPriceUnitA(0);
                    } else {
                        pluDto.setPriceUnitA(1);
                    }
                    pluDto.setUnitPriceA(Float.parseFloat(onePlu[5]));
                    pluDto.setItemNo(onePlu[3]);
                    pluDto.set_id(Long.parseLong(onePlu[3]));
                    pluDto.setDeptNo(Integer.parseInt(onePlu[4]));

                    pluDto.setLabelNoA(Integer.parseInt(onePlu[6]));
                    pluDto.setInitials(PinyinUtil.getFirstSpell(pluDto.getNameTextA()));

                    final long l = PluDtoDaoHelper.insertCommdity(pluDto);
                } catch (Exception e) {
                    XLog.e(e);
                }
            }
        }
        busy = false;
    }
    public void handlePLUToLocalJson(String pluInfo) {
        /**
         * @description: plu插入到本地
         * @param pluInfo   plu信息字符串
         * @return: void
         * @author: dean
         * @time: 2022/12/9 13:57
         */

        if (StringUtils.isNotEmpty(pluInfo)) {

            final String[] pluLine = pluInfo.split("\r\n");
            PluDto pluDto = new PluDto();
            for (int i = 1; i < pluLine.length; i = i + 2) {
                try {
                    String[] onePlu = pluLine[i].split(",");

                    pluDto.setPluNo(onePlu[0]);
                    pluDto.setNameTextA(onePlu[1]);
                    if ("kg".equals(onePlu[2].toLowerCase())) {
                        pluDto.setPriceUnitA(0);
                    } else {
                        pluDto.setPriceUnitA(1);
                    }
                    pluDto.setUnitPriceA(Float.parseFloat(onePlu[5]));
                    pluDto.setItemNo(onePlu[3]);
                    pluDto.set_id(Long.parseLong(onePlu[3]));
                    pluDto.setDeptNo(Integer.parseInt(onePlu[4]));

                    pluDto.setLabelNoA(Integer.parseInt(onePlu[6]));
                    pluDto.setInitials(PinyinUtil.getFirstSpell(pluDto.getNameTextA()));

                    final long l = PluDtoDaoHelper.insertCommdity(pluDto);
                } catch (Exception e) {
                    XLog.e(e);
                }
            }
        }
        busy = false;
    }


    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        //这就是上边的serve方法最后一行调用的那个过时的方法，这里简单的做个判断就好了
        if (!method.equals(Method.POST)) {//判断请求方式是否争取
            return newFixedLengthResponse("the request method is incoorect");
        }
//        if (!StringUtils.equalsIgnoreCase(uri, "callBackUrl")) {//判断uri是否正确
//            return newFixedLengthResponse("the request uri is incoorect");
//        }
//        String personData = files.get("postData");
//        if (StringUtils.isEmpty(personData)) {//判断post过来的数据是否正确
//            return newFixedLengthResponse("postData is null");
//        }
        //判断完了开始解析数据，如果是你想要的数据，那么你就给返回一个正确的格式就好了
        //举个栗子：return newFixedLengthResponse("{\"result\":0,\"success\":true}");
        return super.serve(uri, method, headers, parms, files);
    }
}

