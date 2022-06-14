package com.wintec.lamp.network.yunnetwork.request;


import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;


public interface ApiService {


    @Multipart
    @POST("classifyImage/insert")
    Observable<Response<Integer>> uploadImage(
            @Part List<MultipartBody.Part> files,
            @Part MultipartBody.Part file,
            @Header("autherToken") String token
    );

    @GET("posSn/getSn")
    Observable<Response<String>> getCertificate();
}
