package com.wintec.lamp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.wintec.aiposui.view.AiPosListView;
import com.wintec.detection.utils.StringUtils;
import com.wintec.lamp.BuildConfig;
import com.wintec.lamp.api.ComModel;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.bean.MapDepot;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.contract.WelcomeContract;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.dao.helper.PluDtoDaoHelper;
import com.wintec.lamp.httpdownload.DownInfo;
import com.wintec.lamp.network.ModelRequestCallBack;
import com.wintec.lamp.result.HttpResponse;
import com.wintec.lamp.utils.HttpUtil;
import com.wintec.lamp.utils.NetWorkUtil;
import com.wintec.lamp.utils.ThreadPoolManagerUtils;
import com.wintec.lamp.utils.updateapp.DownloadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午5:40
 */
public class WelcomePresenter<JavaScriptSerializer> extends WelcomeContract.Presenter {

    private static final String TAG = "WelcomePresenter";
    private ComModel comModel;

    @Override
    public void getCheckUpDate(Context context, String url) {

        DownloadUtil.get(context).download(url, Environment.getExternalStorageDirectory() + File.separator + BuildConfig.APPLICATION_ID + File.separator, "AIPOS20.APK", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                getView().showCheckUpDate(file);
            }

            @Override
            public void onDownloading(int progress) {
                getView().showDownloading(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                getView().showDownloadFailed();
            }
        });
    }

    @Override
    public void posRegister(RequestBody body) {
        comModel.posRegister(body, new ModelRequestCallBack<registerBean>() {
            @Override
            public void onSuccess(HttpResponse<registerBean> response) {
                getView().showRegister(response.getData());
                XLog.i(response.getData().toString());
            }

            @Override
            public void onFail() {
                XLog.i("注册失败");
                getView().showCheckVersion(null);
            }
        });

    }

    @Override
    public void checkVersion() {
        comModel.checkVersion(new ModelRequestCallBack<VersionBean>() {
            @Override
            public void onSuccess(HttpResponse<VersionBean> response) {
                getView().showCheckVersion(response.getData());
            }

            @Override
            public void onFail() {
                getView().showCheckVersion(null);
            }

        });
    }

    @Override
    public void getAppState(RequestBody body) {
        comModel.appState(body, new ModelRequestCallBack<String>() {
            @Override
            public void onSuccess(HttpResponse<String> response) {
                getView().showAppState();
            }

            @Override
            public void onFail() {

            }
        });

    }

    @Override
    public void getAppDownInfo() {
        comModel.getAppState(new ModelRequestCallBack<DownInfo>() {
            @Override
            public void onSuccess(HttpResponse<DownInfo> response) {
                getView().showAppDownInfo(response.getData());
            }

            @Override
            public void onFail() {
            }
        });
    }

    @Override
    public void upplus() {
        List<MapDepot> depots = new ArrayList<>();
        List<PluDto> pluAll = PluDtoDaoHelper.getCommdityByItemCode();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        if (pluAll.size() > 0) {
            for (PluDto p : pluAll) {
                if (StringUtils.isNotEmpty(p.getPreviewImage())) {
                    continue;
                } else {
                    MapDepot mapDepot = new MapDepot();
                    mapDepot.setOrgan(Const.getSettingValue(Const.KEY_ACCOUNT)); //租户号
                    mapDepot.setSn(Const.SN);
                    mapDepot.setBranchCode(Const.getSettingValue(Const.KEY_BRANCH_ID));
                    mapDepot.setProductName(p.getNameTextA());
                    mapDepot.setProductCode(p.getPluNo());
                    depots.add(mapDepot);
                    if (depots.size() > 1000) {
                        break;
                    }
                }
            }
            String tempAns = JSON.toJSONString(depots);
            comModel.upplus(tempAns, new ModelRequestCallBack<String>() {
                @Override
                public void onSuccess(HttpResponse<String> response) {
                    Log.w("TAG", "上传plu成功");
                }

                @Override
                public void onFail() {
                    Log.w("TAG", "onFail: 上传plu失败");
                }
            });
        }

    }

    @Override
    public void getImgUrl() {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("PosSn", Const.SN);
        requestMap.put("organ", Const.getSettingValue(Const.KEY_ACCOUNT)); //租户号
        requestMap.put("branchCode", Const.getSettingValue(Const.KEY_BRANCH_ID));
        comModel.getImgUrl(requestMap, new ModelRequestCallBack<Object>() {
            @Override
            public void onSuccess(HttpResponse<Object> response) {
                Object data = response.getData();
                Map data1 = (Map) data;
                int mapSize =data1.size();
                ThreadPoolManagerUtils.getInstance().execute(() -> {
                    long s1 = System.currentTimeMillis();
                    int i=0;
                    Bitmap currentBitmap=null;
                    for (Object o : data1.entrySet()) {
                        String[] split = o.toString().split("=");
                        if (split != null && split.length == 2) {
                            PluDto currentPlu = null;
                            try {
                                currentPlu = PluDtoDaoHelper.getCommdityByScalesCodeLocal(split[0]);
                                if (currentPlu == null) {
                                    continue;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (currentPlu != null && !split[1].equals(currentPlu.getPreviewImage()) && StringUtils.isNotEmpty(split[1])) {
                                currentPlu.setPreviewImage(split[1]);
                                PluDtoDaoHelper.updateCommdity(currentPlu);
                                currentBitmap = NetWorkUtil.GetImageInputStream(split[1]);
                                if (currentBitmap != null) {
                                    savaImageToPath(currentBitmap, AiPosListView.ROOT_PATH, (split[0] + ".jpg"));
                                    XLog.d((i++)+"/"+mapSize+"下载图片成功："+split[0]);
                                } else {
                                    XLog.d("下载图片失败："+split[0]);
                                }
                            }
                        }
                    }
                    long s2 = System.currentTimeMillis();
                    XLog.i("下载图片花费："+ (s2 - s1) );
                });

            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void upLogTxt(File file, String fileName, String code) {
        comModel.upLog(file, fileName, code, new ModelRequestCallBack<String>() {
            @Override
            public void onSuccess(HttpResponse<String> response) {
                Const.setSettingValue(Const.ERROR_LOG_FLAG, "0");
                Log.i(TAG, "日志上传成功");
            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    protected void createModel() {
        comModel = new ComModel(this);
    }

    //上传商品信息
    public void upPLUDto() {
        List<MapDepot> depots = new ArrayList<>();
        List<PluDto> pluAll = PluDtoDaoHelper.getCommdityByItemCode();
        if (pluAll.size() > 0) {
            for (PluDto p : pluAll) {
                if (StringUtils.isEmpty(p.getPreviewImage())) {
                    MapDepot mapDepot = new MapDepot();
                    mapDepot.setOrgan(Const.getSettingValue(Const.KEY_ACCOUNT)); //租户号
                    mapDepot.setSn(Const.SN);
                    mapDepot.setBranchCode(Const.getSettingValue(Const.KEY_BRANCH_ID));
                    mapDepot.setProductName(p.getNameTextA());
                    mapDepot.setProductCode(p.getPluNo());
                    depots.add(mapDepot);
                    if (depots.size() > 2000) {
                        String tempAns = JSON.toJSONString(depots);
                        HttpUtil.postJson(Const.BASE_URL + "pos/mapDepot/uploading", tempAns);
                        depots.clear();
                    }
                }
            }
            if (depots.size() >= 1) {
                String tempAns = JSON.toJSONString(depots);
                HttpUtil.postJson(Const.BASE_URL + "pos/mapDepot/uploading", tempAns);
            }
        }

    }

    /**
     * 保存图片
     *
     * @param bitmap   图片bitmap
     * @param path     保存路径
     * @param fileName 保存的文件名
     */
    public static void savaImageToPath(Bitmap bitmap, String path, String fileName) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(path + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
