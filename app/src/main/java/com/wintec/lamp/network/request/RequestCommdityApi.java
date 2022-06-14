package com.wintec.lamp.network.request;


import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.network.response.Response;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestCommdityApi {

    // 下载商品数据
    @GET("sku/commdoity/{branchId}")
    Observable<Response<List<Commdity>>> downloadCommdities(@Path("branchId") String branchId);

    // 更新商品数据
    @FormUrlEncoded
    @POST("sku/update")
    Observable<Response<String>> updatePrice(@Field("branchId") String branchId,
                                             @Field("itemCode") String itemCode,
                                             @Field("price") String price);

    //插入商品数据
    @POST("sku/insertSku")
    Observable<Response<Commdity>> insertSku(@Body RequestBody requestBody);

    //注册pos
    @POST("posManage/insert")
    Observable<Response<String>> posRegister(@Body RequestBody requestBody);


    //批量更新商品数据
    @POST("sku/batchUpdatePrices")
    Observable<Response<String>> updatePriceAll(@Body RequestBody requestBody);

}
