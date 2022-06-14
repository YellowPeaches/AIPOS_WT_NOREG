package com.wintec.lamp.server;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wintec.ThreadCacheManager;
import com.wintec.aiposui.utils.BitmapUtils;
import com.wintec.aiposui.utils.ImageUtils;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.AccDto;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.helper.AccDtoHelper;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.entity.DuoDianPlu;
import com.wintec.lamp.utils.BmpUtil;
import com.wintec.lamp.utils.pinyin.PinyinUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class DuoDianSocketHandler implements Runnable {

    private Socket socket;

    // 插入锁
    public static ReentrantLock lock = new ReentrantLock();

    public DuoDianSocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            // InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            OutputStream outputStream = socket.getOutputStream();
//            StringBuilder result = new StringBuilder();
//            int length = 0;
//
//            char[] outPut = new char[1024];
            StringBuilder stringBuilder = new StringBuilder();
//            String str = "";
//            while((str = br.readLine()) != null) {
//                str = str.trim();
//              //  System.out.println("收到客户端消息：" + str);
//                stringBuilder.append(str);
//            }
            char[] arr = new char[512];
            int read;

            while (true) {

                read = br.read(arr, 0, arr.length);

                if (read < 0) {
                    break;
                }

                stringBuilder.append(new String(arr, 0, read));
            }

//            while ((length = inputStreamReader.read(outPut)) > 0) {
//                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
//                result.append(new String(outPut, 0, length,"GBK"));
//            }
            br.close();
            inputStreamReader.close();
            final String ROOT_PATH = Environment.getExternalStoragePublicDirectory("skuimg_d").getPath() + "//";
            File file = new File(ROOT_PATH);
            if (!file.exists()) {
                file.mkdir();
            }
            String json = stringBuilder.toString();
            if (!"".equals(json) && json != null) {

                JSONObject jsonObject;
                JSONArray jsonArray;
                List<DuoDianPlu> plus = new ArrayList<>();
                jsonObject = JSONObject.parseObject(json);
                jsonArray = jsonObject.getJSONArray("data");
                plus = jsonArray.toJavaList(DuoDianPlu.class);
                //  数据保存数据到数据库

                for (DuoDianPlu item : plus) {
                    lock.lock();
                    try {
                        Log.i("DUODIAN", "run: 接收商品数据");
                        PluDto commdity = item.toPlu();
                        commdity.setEtNo(Integer.valueOf(commdity.getPluNo()));
                        commdity.setInitials(PinyinUtil.getFirstSpell(commdity.getNameTextA()));
                        commdity.setBranchId(Const.getSettingValue(Const.KEY_BRANCH_ID));
                        PluDto commdityByItemCode = PluDtoDaoHelper.getCommdityByScalesCode(commdity.getPluNo());
                        try {
                            if (commdityByItemCode == null) {
                                PluDtoDaoHelper.insertCommdity(commdity);
                            } else {
                                // CommdityHelper.deleteCommdityByKey(commdityByItemCode.get_id());
                                commdity.set_id(commdityByItemCode.get_id());
                                commdity.setPreviewImage(commdityByItemCode.getPreviewImage());
                                PluDtoDaoHelper.updateCommdity(commdity);
                            }
                        } catch (Exception e) {
                            Log.i("DuodianSocketHandler", "run: 插入失败 分析可能和传输重复数据在两次socket中导致锁对象为两个 " +
                                    "没有成功加锁 " +
                                    "发生线程安全问题");
                        }

                        // ThreadCacheManager.getExecutorService().execute(() -> {
                        String previewImage = item.getImgUrl();
                        if (!"".equals(previewImage) && previewImage != null) {
                            Log.i(" ", "run: 下载图片");
                            String fileName = ROOT_PATH + item.getPlu_No() + ".jpg";
                            if (!new File(fileName).exists()) {
                                try {
                                    Bitmap bitMBitmap = ImageUtils.getBitMBitmap("https://img.dmallcdn.com/" + previewImage);
                                    Bitmap bitmap = BitmapUtils.drawBitmapBg(Color.parseColor("#ffffff"), bitMBitmap);
                                    BmpUtil.savebitmap(bitmap, fileName);
                                    bitmap.recycle();
                                } catch (Exception e) {
                                    Log.i("DuodianSocketHandler", "run: 下载图片失败 是网络问题");
                                }

                            }
                        }
                        //  });
                        // 插入附加信息号  多点的附加信息号为plu号
                        AccDto accDto = new AccDto();
                        accDto.setAccNo(Integer.valueOf(commdity.getPluNo()));
                        accDto.setContent1(item.getLine4());
                        try {
                            AccDto accDto1 = AccDtoHelper.selectByAccNo(Integer.valueOf(commdity.getPluNo()));
                            if (accDto1 == null) {
                                AccDtoHelper.insert(accDto);
                            } else {
                                accDto.set_id(accDto1.get_id());
                                AccDtoHelper.uptate(accDto);
                            }
                        } catch (Exception e) {
                            Log.i("DuodianSocketHandler", "run: 插入失败 分析可能和传输重复数据在两次socket中导致锁对象为两个 " +
                                    "没有成功加锁 " +
                                    "发生线程安全问题");
                        }


                    } finally {
                        lock.unlock();
                    }

                }
                List<DuoDianPlu> finalPlus = plus;


            }
            outputStream.write("01".getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }
        }
    }

}
