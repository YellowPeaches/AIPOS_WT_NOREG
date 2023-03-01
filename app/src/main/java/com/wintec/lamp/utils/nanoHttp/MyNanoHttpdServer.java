//package com.wintec.lamp.utils.nanoHttp;//package com.wintec.lamp.utils.nanoHttp;
//
//import android.util.Log;
//
//import com.elvishew.xlog.XLog;
//import com.wintec.detection.utils.StringUtils;
//import com.wintec.lamp.dao.entity.AccDto;
//import com.wintec.lamp.dao.entity.PluDto;
//import com.wintec.lamp.dao.helper.AccDtoHelper;
//import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
//import com.wintec.lamp.utils.pinyin.PinyinUtil;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//import fi.iki.elonen.NanoHTTPD;
//
//public class MyNanoHttpdServer extends NanoHTTPD {
//
//    private static final String TAG = "NanoHTTPD";
//    private static Boolean busy = false;
//    private static int count =0;
//    //实现父类的构造方法
//    public MyNanoHttpdServer(int port) {
//        super(port);
//    }
//
//    //正式开启服务
//    public void start() {
//        try {
//            start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
//
//            //向日志文件中写入接收消息的接口已经打开
////            WriteLogToFile.info("The face recognition callback interface has been opened, the port number is 9999.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public Response serve(IHTTPSession session) {
//        Map<String, String> files = new HashMap<String, String>();
//        /*获取header信息，NanoHttp的header不仅仅是HTTP的header，还包括其他信息。*/
//        Map<String, String> header = session.getHeaders();
////        return super.serve(session);
//        String param = null;
//        try {
//            session.parseBody(files);
//             param = files.get("postData");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ResponseException e) {
//            e.printStackTrace();
//        }
//
////        session.getHeaders().forEach((key, val) -> {
////            XLog.i(key + " : " + val);
////        });
////
////        Map<String, List<String>> parameters = session.getParameters();
////        parameters.forEach((key, val) -> {
////            XLog.i(key + " : " + val.toString());
////        });
////        CookieHandler cookies = session.getCookies();
//
//        String uri = session.getUri();
//
//        if (uri.contains("table/goods")) {
////            busy = true;
//            String finalParam = param;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    long start = System.currentTimeMillis();
//                    InputStream sessionInputStream = session.getInputStream();
//                    Log.i("商品信息：", (System.currentTimeMillis() - start) + "");
////                    String input2String = getStringByInputStream(sessionInputStream);
//                    String input2String = finalParam;
//                    try {
//                        sessionInputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    XLog.i("plu转化信息serve: " + input2String);
//                    handlePLUToLocal(input2String);
//
////                    busy = false;
//
////                    Log.i("time", (System.currentTimeMillis() - start) + "");
//                }
//            }).start();
//        } else if (uri.contains("extra_text")) {
////            busy = true;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    InputStream sessionInputStream = session.getInputStream();
//                    String input2String = getStringByInputStream(sessionInputStream);
//                    try {
//                        sessionInputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    XLog.i("附加信息转化信息serve: " + input2String);
//                    handleExtraTextToLocal(input2String);
//
//                    busy = false;
//
////                    Log.i("time", (System.currentTimeMillis() - start) + "");
//                }
//            }).start();
//
////            busy = false;    //删除
//        }
//
////        / 将读取到的文件内容返回给浏览器
////        return new NanoHTTPD.Response(new String(buffer,0,len));
//
//
//        try {
//            String msg = "<html><body><h1>Hello server,HaHHH</h1></body></html>";
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return newFixedLengthResponse("200");
//        } catch (Exception exception) {
//            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal Server Error!!!");
//        }
//
//    }
//
//    public String getStringByInputStream(InputStream inputStream) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            byte[] b = new byte[10240];
//            int n;
//            while ((n = inputStream.read(b)) != -1) {
//                outputStream.write(b, 0, n);
//            }
//        } catch (Exception e) {
//            try {
//                inputStream.close();
//                outputStream.close();
//            } catch (Exception e1) {
//            }
//        }
//        return outputStream.toString();
//    }
//
//    public void handlePLUToLocal(String pluInfo) {
//        /**
//         * @description: plu插入到本地
//         * @param pluInfo   plu信息字符串
//         * @return: void
//         * @author: dean
//         * @time: 2022/12/9 13:57
//         */
//
//        if (StringUtils.isNotEmpty(pluInfo)) {
//
//            final String[] pluLine = pluInfo.split("\n");
//            for (int i = 1; i < pluLine.length; i++) {
//                PluDto pluDto = new PluDto();
//                try {
//                    String[] onePlu = pluLine[i].split(",");
//
//                    pluDto.setPluNo(onePlu[0]);
//                    pluDto.setNameTextA(onePlu[1]);
//                    if ("0".equalsIgnoreCase(onePlu[6])) {
//                        pluDto.setPriceUnitA(0);
//                    } else {
//                        pluDto.setPriceUnitA(1);
//                    }
//                    pluDto.setUnitPriceA(Float.parseFloat(onePlu[5]));
//                    pluDto.setItemNo(onePlu[3]);
//                    pluDto.set_id(Long.parseLong(onePlu[3]));
//                    if (onePlu[4].matches("[0-9]+")) {
//                        pluDto.setDeptNo(Integer.parseInt(onePlu[4]));
//                    }
//                    if (onePlu[0].matches("[0-9]+")) {
//                        pluDto.setEtNo(Integer.parseInt(onePlu[0]));
//                    }
//                    if (onePlu[7].matches("[0-9]+")) {
//                        pluDto.setLabelNoA(Integer.parseInt(onePlu[7]));
//                    }
//                    pluDto.setSellByDate(onePlu[9]);
//                    pluDto.setInitials(PinyinUtil.getFirstSpell(pluDto.getNameTextA()));
//                    if (onePlu[8].matches("[0-9]+")) {
//                        pluDto.setBarcodeNo(Integer.parseInt(onePlu[8]));
//                    }
//
//
//                    PluDto commdityByItemCode = PluDtoDaoHelper.getCommdityByScalesCodeLocal(pluDto.getPluNo());
//                    if (commdityByItemCode == null) {
//                        PluDtoDaoHelper.insertCommdity(pluDto);
//                        XLog.i("新数据存入 " + pluDto.toString());
//                    } else {
//                        // CommdityHelper.deleteCommdityByKey(commdityByItemCode.get_id());
//                        pluDto.set_id(commdityByItemCode.get_id());
//                        pluDto.setPreviewImage(commdityByItemCode.getPreviewImage());
//                        PluDtoDaoHelper.updateCommdity(pluDto);
//                        XLog.i("数据修改 " + i + "/" + pluDto.toString());
//                    }
//                } catch (Exception e) {
//                    XLog.e(e.getMessage());
//                    XLog.e(e);
//                }
//            }
//            XLog.i("完成一次传输plu"+count++);
//        }
//    }
//
//
//    public void handleExtraTextToLocal(String extraTextInfo) {
//        /**
//         * @description: extra_text插入到本地
//         * @param extraTextInfo
//         * @return: void
//         * @author: dean
//         * @time: 2023/2/21 13:57
//         */
//
//        if (StringUtils.isNotEmpty(extraTextInfo)) {
//
//
//            int index = extraTextInfo.indexOf("\n") + 1;
//            String fullInfo = extraTextInfo.substring(index);
//            String[] oneText = fullInfo.split(",\n");
//
//            for (int i = 0; i < oneText.length; i++) {
//                try {
//                    String oneAcc = oneText[i] + ",";
//                    final String[] split = oneAcc.split(",");
//                    AccDto accDto = new AccDto();
//                    accDto.set_id(Long.parseLong(split[0]));
//                    accDto.setAccNo(Integer.parseInt(split[0]));
//                    if (StringUtils.isNotEmpty(split[1])) {
//                        accDto.setDepNo(Integer.parseInt(split[1]));
//                    }
//                    if (StringUtils.isNotEmpty(split[2])) {
//                        accDto.setGroupNo(Integer.parseInt(split[2]));
//                    }
//                    if (split.length >= 4) {
//                        accDto.setContent1(split[3].replaceAll("\"", ""));
//                    }
//                    if (split.length >= 5) {
//                        accDto.setContent2(split[4]);
//                    }
//                    if (split.length >= 6) {
//                        accDto.setContent3(split[5]);
//                    }
//                    if (split.length >= 7) {
//                        accDto.setContent4(split[6]);
//                    }
//
//
//                    AccDto accDtoByExtraNo = AccDtoHelper.selectByAccNo(Integer.parseInt(split[0]));
//
//                    if (accDtoByExtraNo == null) {
//                        AccDtoHelper.insert(accDto);
//                        XLog.i("新附加文本存入 " + accDto.toString());
//                    } else {
//                        AccDtoHelper.uptate(accDto);
//                        XLog.i("新附加文本修改 " + i + "/" + accDto.toString());
//                    }
//                } catch (Exception e) {
//                    XLog.e(e.getMessage());
//                    XLog.e(e);
//                }
//            }
//        }
//    }
//
//    public void handlePLUToLocalJson(String pluInfo) {
//        /**
//         * @description: plu插入到本地
//         * @param pluInfo   plu信息字符串
//         * @return: void
//         * @author: dean
//         * @time: 2022/12/9 13:57
//         */
//
//        if (StringUtils.isNotEmpty(pluInfo)) {
//
//            final String[] pluLine = pluInfo.split("\r\n");
//            PluDto pluDto = new PluDto();
//            for (int i = 1; i < pluLine.length; i = i + 2) {
//                try {
//                    String[] onePlu = pluLine[i].split(",");
//
//                    pluDto.setPluNo(onePlu[0]);
//                    pluDto.setNameTextA(onePlu[1]);
//                    if ("kg".equals(onePlu[2].toLowerCase())) {
//                        pluDto.setPriceUnitA(0);
//                    } else {
//                        pluDto.setPriceUnitA(1);
//                    }
//                    pluDto.setUnitPriceA(Float.parseFloat(onePlu[5]));
//                    pluDto.setItemNo(onePlu[3]);
//                    pluDto.set_id(Long.parseLong(onePlu[3]));
//                    pluDto.setDeptNo(Integer.parseInt(onePlu[4]));
//
//                    pluDto.setLabelNoA(Integer.parseInt(onePlu[6]));
//                    pluDto.setInitials(PinyinUtil.getFirstSpell(pluDto.getNameTextA()));
//
//                    final long l = PluDtoDaoHelper.insertCommdity(pluDto);
//                } catch (Exception e) {
//                    XLog.e(e);
//                }
//            }
//        }
//        busy = false;
//    }
//
//
//    @Override
//    public Response serve(String uri, Method
//            method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
//        //这就是上边的serve方法最后一行调用的那个过时的方法，这里简单的做个判断就好了
//        if (!method.equals(Method.POST)) {//判断请求方式是否争取
//            return newFixedLengthResponse("the request method is incoorect");
//        }
////        if (!StringUtils.equalsIgnoreCase(uri, "callBackUrl")) {//判断uri是否正确
////            return newFixedLengthResponse("the request uri is incoorect");
////        }
////        String personData = files.get("postData");
////        if (StringUtils.isEmpty(personData)) {//判断post过来的数据是否正确
////            return newFixedLengthResponse("postData is null");
////        }
//        //判断完了开始解析数据，如果是你想要的数据，那么你就给返回一个正确的格式就好了
//        //举个栗子：return newFixedLengthResponse("{\"result\":0,\"success\":true}");
//        return super.serve(uri, method, headers, parms, files);
//    }
//}
//
