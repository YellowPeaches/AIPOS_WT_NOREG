package com.wintec.lamp.api;

import com.wintec.lamp.base.BaseMvpModel;
import com.wintec.lamp.bean.DiscernData;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.bean.TagRules;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.httpdownload.DownInfo;
import com.wintec.lamp.mvp.ModelImpl;
import com.wintec.lamp.network.ModelRequestCallBack;
import com.wintec.lamp.network.RetrofitClient;
import com.wintec.lamp.network.RetrofitSubscriber;
import com.wintec.lamp.result.HttpResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Query;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午3:38
 */
public class ComModel extends BaseMvpModel {

    private AppRetrofitApi retrofitApi;

    public ComModel(ModelImpl modelImpl) {
        super(modelImpl);
        retrofitApi = RetrofitClient.getInstance().createService(AppRetrofitApi.class);
    }


    public void updatePriceAll(RequestBody body, ModelRequestCallBack<Object> modelRequestCallBack) {
        requestNormal(retrofitApi.updatePriceAll(body), modelRequestCallBack);
    }

    public void downloadCommdities(String id, ModelRequestCallBack<List<PluDto>> modelRequestCallBack) {
        requestNormal(retrofitApi.downloadCommdities(id), modelRequestCallBack);
    }

    public void dealInsert(Map<String, Object> map, ModelRequestCallBack<String> modelRequestCallBack) {
        requestNormal(retrofitApi.dealInsert(map), modelRequestCallBack);
    }

    public void posRegister(RequestBody requestBody, ModelRequestCallBack<registerBean> modelRequestCallBack) {
        requestNormal(retrofitApi.posRegister(requestBody), modelRequestCallBack);
    }

    public void checkVersion(ModelRequestCallBack<VersionBean> modelRequestCallBack) {
        requestNormal(retrofitApi.checkVersion(), modelRequestCallBack);
    }

    public void appState(RequestBody body, ModelRequestCallBack<String> modelRequestCallBack) {
        requestNormal(retrofitApi.appState(body), modelRequestCallBack);
    }

    public void getAppState(ModelRequestCallBack<DownInfo> modelRequestCallBack) {
        requestNormal(retrofitApi.getAppState(), modelRequestCallBack);
    }

    public void getBarCode(String branchCode, ModelRequestCallBack<TagRules> modelRequestCallBack) {
        requestNormal(retrofitApi.getBarCode(branchCode), modelRequestCallBack);
    }

    public void getPriceTag(String branchCode, String posSn, ModelRequestCallBack<List<TagMiddle>> modelRequestCallBack) {
        requestNormal(retrofitApi.getPriceTag(branchCode, posSn), modelRequestCallBack);
    }

    public void upplus(String plus, ModelRequestCallBack<String> modelRequestCallBack) {
        requestNormal(retrofitApi.upplus(plus), modelRequestCallBack);
    }

    public void getImgUrl(Map<String,String> map, ModelRequestCallBack<Object> modelRequestCallBack) {
        requestNormal(retrofitApi.getImgUrl(map), modelRequestCallBack);
    }

    public void getPriceTag2(String posSn, ModelRequestCallBack<Map<Integer, List<TagMiddle>>> modelRequestCallBack) {
        requestNormal(retrofitApi.getPriceTag2(posSn), modelRequestCallBack);
    }

    public void importData(String branchCode, String posSn, int version, ModelRequestCallBack<DiscernData> modelRequestCallBack) {
        requestNormal(retrofitApi.importData(branchCode, posSn, version), modelRequestCallBack);
    }

    public void updateImg(File file, Integer id, String itemCode, ModelRequestCallBack<String> modelRequestCallBack) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        String mediaType = "image/jpeg";
        if (file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("imgFile", file.getName(), requestFile);
            partList.add(part);
        }

        retrofitApi.updateImg(partList.get(0), id, itemCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitSubscriber<HttpResponse<String>>(modelImpl) {
                    @Override
                    protected void onSuccess(HttpResponse<String> response) {
                        modelRequestCallBack.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    public void upLog(File file, String fileName, String posSn, ModelRequestCallBack<String> modelRequestCallBack) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        String mediaType = "text/plain";
        if (file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("logFile", file.getName(), requestFile);
            partList.add(part);
        }

        retrofitApi.upLog(partList.get(0), fileName, posSn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitSubscriber<HttpResponse<String>>(modelImpl) {
                    @Override
                    protected void onSuccess(HttpResponse<String> response) {
                        modelRequestCallBack.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    public void exportData(File file, String branchCode, String posSn, int version, ModelRequestCallBack<String> modelRequestCallBack) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        String mediaType = "text/plain";
        if (file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            partList.add(part);
        }

        retrofitApi.exportData(partList.get(0), branchCode, posSn, version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitSubscriber<HttpResponse<String>>(modelImpl) {
                    @Override
                    protected void onSuccess(HttpResponse<String> response) {
                        modelRequestCallBack.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }


    public void dealImg(File file, String id, String mediaType, ModelRequestCallBack<String> modelRequestCallBack) {
        List<MultipartBody.Part> partList = new ArrayList<>();
//        RequestBody fBody = RequestBody.create(MediaType.parse("trasactionId"), id);

        if (file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("imgFile", file.getName(), requestFile);
            partList.add(part);
        }

        retrofitApi.dealImg(id, partList.get(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitSubscriber<HttpResponse<String>>(modelImpl) {
                    @Override
                    protected void onSuccess(HttpResponse<String> response) {
                        modelRequestCallBack.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

}
