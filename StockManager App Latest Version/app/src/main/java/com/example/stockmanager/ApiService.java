package com.example.stockmanager;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("wp-json/variation-stock/v1/outofstock/{product_id}")
    Call<ResponseBody> markOutOfStock(@Path("product_id") int productId);


    @POST("wp-json/variation-stock/v1/instock/{product_id}")
    Call<ResponseBody> markInStock(@Path("product_id") int productId);


    // 🆕 Add this one here


    @GET("wp-json/variation-stock/v1/status/{id}")
    Call<JsonObject> getStockStatus(@Path("id") int productId);



}
