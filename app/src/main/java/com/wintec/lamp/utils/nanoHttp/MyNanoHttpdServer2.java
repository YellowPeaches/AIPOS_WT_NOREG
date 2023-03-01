package com.wintec.lamp.utils.nanoHttp;

import com.elvishew.xlog.XLog;
import com.wintec.detection.utils.StringUtils;
import com.wintec.lamp.dao.entity.AccDto;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.helper.AccDtoHelper;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.utils.pinyin.PinyinUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MyNanoHttpdServer2 extends NanoHTTPD {

    private static final String TAG = "NanoHTTPD";
    private static int countOfPlu = 0;
    private static int countOfEx = 0;

    //实现父类的构造方法
    public MyNanoHttpdServer2(int port) {
        super(port);
    }

    //正式开启服务
    public void start() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
//       /这个就是之前分析，重写父类的一个参数的方法，
        //这里边已经把所有的解析操作已经在这里执行了
        return super.serve(session);
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers,
                          Map<String, String> parms, Map<String, String> files) {
        //这就是上边的serve方法最后一行调用的那个过时的方法，这里简单的做个判断就好了
        if (!method.equals(Method.POST)) {//判断请求方式是否争取
            return newFixedLengthResponse("the request method is incoorect");
        }
        if (!uri.contains("table")) {//判断uri是否正确
            return newFixedLengthResponse("the request uri is incoorect");
        }
        String personData = files.get("postData");
        if (StringUtils.isEmpty(personData)) {//判断post过来的数据是否正确
            return newFixedLengthResponse("postData is null");
        }
        //判断完了开始解析数据，如果是你想要的数据，那么你就给返回一个正确的格式就好了
        if (uri.contains("goods")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String input2String = personData;
                    handlePLUToLocal(input2String);
                }
            }).start();

        } else if (uri.contains("extra_text"))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String input2String = personData;
                    handleExtraTextToLocal(input2String);
                }
            }).start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            return newFixedLengthResponse("{\"result\":0,\"success\":true}");
        } catch (Exception e) {
            return super.serve(uri, method, headers, parms, files);
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
            int currentPageNum = 0;
            Long startTime = System.currentTimeMillis();
            final String[] pluLine = pluInfo.split("\n");
            for (int i = 1; i < pluLine.length; i++) {
                PluDto pluDto = new PluDto();
                try {
                    String[] onePlu = pluLine[i].split(",");

                    pluDto.setPluNo(onePlu[0]);
                    pluDto.setNameTextA(onePlu[1]);
                    if ("0".equalsIgnoreCase(onePlu[6])) {
                        pluDto.setPriceUnitA(0);
                    } else {
                        pluDto.setPriceUnitA(1);
                    }
                    pluDto.setUnitPriceA(Float.parseFloat(onePlu[5]));
                    pluDto.setItemNo(onePlu[3]);
                    pluDto.set_id(Long.parseLong(onePlu[3]));
                    if (onePlu[4].matches("[0-9]+")) {
                        pluDto.setDeptNo(Integer.parseInt(onePlu[4]));
                    }
                    if (onePlu[0].matches("[0-9]+")) {
                        pluDto.setEtNo(Integer.parseInt(onePlu[0]));
                    }
                    if (onePlu[7].matches("[0-9]+")) {
                        pluDto.setLabelNoA(Integer.parseInt(onePlu[7]));
                    }
                    pluDto.setSellByDate(onePlu[9]);
                    pluDto.setInitials(PinyinUtil.getFirstSpell(pluDto.getNameTextA()));
                    if (onePlu[8].matches("[0-9]+")) {
                        pluDto.setBarcodeNo(Integer.parseInt(onePlu[8]));
                    }


                    PluDto commdityByItemCode = PluDtoDaoHelper.getCommdityByScalesCodeLocal(pluDto.getPluNo());
                    if (commdityByItemCode == null) {
                        PluDtoDaoHelper.insertCommdity(pluDto);
                        currentPageNum++;
//                        XLog.i("新数据存入 " + pluDto.toString());
                    } else {
                        pluDto.set_id(commdityByItemCode.get_id());
                        pluDto.setPreviewImage(commdityByItemCode.getPreviewImage());
                        PluDtoDaoHelper.updateCommdity(pluDto);
                        currentPageNum++;
//                        XLog.i("数据修改 " + i + "/" + pluDto.toString());
                    }
                } catch (Exception e) {
                    XLog.e(e.getMessage());
                    XLog.e(e);
                }
            }
            XLog.i("收到：" + (++countOfPlu) + "  存入 ： " + currentPageNum + " 花费 " + (System.currentTimeMillis() - startTime));
        }
    }


    public void handleExtraTextToLocal(String extraTextInfo) {
        /**
         * @description: extra_text插入到本地
         * @param extraTextInfo
         * @return: void
         * @author: dean
         * @time: 2023/2/21 13:57
         */

        if (StringUtils.isNotEmpty(extraTextInfo)) {
            int currentPageNum = 0;
            int index = extraTextInfo.indexOf("\n") + 1;
            String fullInfo = extraTextInfo.substring(index);
            String[] oneText = fullInfo.split(",\n");


            for (int i = 0; i < oneText.length; i++) {
                try {
                    String oneAcc = oneText[i] + ",";
                    final String[] split = oneAcc.split(",");
                    AccDto accDto = new AccDto();
                    accDto.set_id(Long.parseLong(split[0]));
                    accDto.setAccNo(Integer.parseInt(split[0]));
                    if (StringUtils.isNotEmpty(split[1])) {
                        accDto.setDepNo(Integer.parseInt(split[1]));
                    }
                    if (StringUtils.isNotEmpty(split[2])) {
                        accDto.setGroupNo(Integer.parseInt(split[2]));
                    }
                    if (split.length >= 4) {
                        accDto.setContent1(split[3].replaceAll("\"", ""));
                    }
                    if (split.length >= 5) {
                        accDto.setContent2(split[4]);
                    }
                    if (split.length >= 6) {
                        accDto.setContent3(split[5]);
                    }
                    if (split.length >= 7) {
                        accDto.setContent4(split[6]);
                    }


                    AccDto accDtoByExtraNo = AccDtoHelper.selectByAccNo(Integer.parseInt(split[0]));

                    if (accDtoByExtraNo == null) {
                        AccDtoHelper.insert(accDto);
                        currentPageNum++;
//                        XLog.i("新附加文本存入 " + accDto.toString());
                    } else {
                        AccDtoHelper.uptate(accDto);
                        currentPageNum++;
//                        XLog.i("新附加文本修改 " + i + "/" + accDto.toString());
                    }
                } catch (Exception e) {
                    XLog.e(e.getMessage());
                    XLog.e(e);
                }
            }
            XLog.i("收到EX：" + (++countOfEx) + "  存入 ： " + currentPageNum);

        }
    }

}

