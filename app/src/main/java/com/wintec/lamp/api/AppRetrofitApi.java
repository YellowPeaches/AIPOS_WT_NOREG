package com.wintec.lamp.api;

import com.wintec.lamp.bean.DiscernData;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.bean.TagRules;
import com.wintec.lamp.bean.registerBean;
import com.wintec.lamp.bean.VersionBean;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.httpdownload.DownInfo;
import com.wintec.lamp.result.HttpResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * <p>
 * Time: 下午4:35
 */
public interface AppRetrofitApi {

    //批量更新商品数据
    @POST("sku/batchUpdatePrices")
    Observable<HttpResponse<Object>> updatePriceAll(@Body RequestBody requestBody);

    // 下载商品数据
    @GET("sku/commdoity/{branchId}")
    Observable<HttpResponse<List<PluDto>>> downloadCommdities(@Path("branchId") String branchId);

    //批量更新商品数据
    @FormUrlEncoded
    @POST("deal/insert")
    Observable<HttpResponse<String>> dealInsert(@FieldMap Map<String, Object> map);

    //批量更新商品数据-图片文件
    @Multipart
    @POST("deal/img")
    Observable<HttpResponse<String>> dealImg(@Part("trasactionId") String trasactionId, @Part MultipartBody.Part file);//trasactionId

    //注册pos
    @POST("posManage/insert")
    Observable<HttpResponse<registerBean>> posRegister(@Body RequestBody requestBody);

    @Multipart
    @POST("sku/updateImg")
    Observable<HttpResponse<String>> updateImg(@Part MultipartBody.Part file, @Part("branchId") Integer id, @Part("itemCode") String itemCode);

    @POST("appVersion/getVersion")
    Observable<HttpResponse<VersionBean>> checkVersion();

    @POST("appState/insert")
    Observable<HttpResponse<String>> appState(@Body RequestBody requestBody);

    @GET("appState/getState")
    Observable<HttpResponse<DownInfo>> getAppState();

    @Multipart
    @POST("/posLog/addLog")
    Observable<HttpResponse<String>> upLog(@Part MultipartBody.Part file, @Part("fileName") String fileName, @Part("posSn") String code);

    //更新条码信息
    @POST("tagRules/selectOne")
    Observable<HttpResponse<TagRules>> getBarCode(@Query("branchCode") String branchCode);

    //更新价签
    @POST("priceTags/selectList")
    Observable<HttpResponse<List<TagMiddle>>> getPriceTag(@Query("branchCode") String branchCode, @Query("posSn") String posSn);

    //多价签
    @POST("priceTags/getTags")
    Observable<HttpResponse<Map<Integer, List<TagMiddle>>>> getPriceTag2(@Query("posSn") String posSn);

    //备份到云端
    @Multipart
    @POST("discern/insertFile")
    Observable<HttpResponse<String>> exportData(@Part MultipartBody.Part file,
                                                @Part("branchCode") String branchCode,
                                                @Part("posSn") String code,
                                                @Part("version") int version);

    //更新价签
    @GET("discern/selectOne")
    Observable<HttpResponse<DiscernData>> importData(@Query("branchCode") String branchCode,
                                                     @Query("posSn") String posSn, @Query("version") int version);

    //
    @POST("pos/mapDepot/uploading")
    Observable<HttpResponse<String>> upplus(@Query("plus") String plus);
    //
    @FormUrlEncoded
    @POST("pos/mapDepot/getImgUrl")
    Observable<HttpResponse<Object>> getImgUrl(@FieldMap Map<String,String> map);
}
