package com.example.stockmanager.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WooCommerceApi {

    // ✅ Fetch all coupons
    @GET("wp-json/wc/v3/coupons")
    Call<List<CouponResponse>> getCoupons(
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );

    // ✅ Create a coupon
    @Headers("Content-Type: application/json")
    @POST("wp-json/wc/v3/coupons")
    Call<CouponResponse> createCoupon(
            @Body CouponRequest couponRequest,
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );

    // ✅ Delete a coupon
    @DELETE("wp-json/wc/v3/coupons/{id}")
    Call<Void> deleteCoupon(
            @Path("id") int couponId,
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );


    // ✅ Update Product Stock (Corrected)
    @Headers("Content-Type: application/json")
    @PUT("wp-json/wc/v3/products/{id}")
    Call<ProductResponse> updateProductStock(
            @Path("id") int productId,
            @Body ProductResponse updatedProduct,
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );

    // ✅ Update stock status of a product
    @PUT("products/{id}")
    Call<Product> updateProductStock(
            @Path("id") int productId,
            @Body StockStatus stockStatus,
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );

    // ✅ Fetch variations of a variable product
    @GET("wp-json/wc/v3/products/{id}/variations")
    Call<List<Product>> getProductVariations(
            @Path("id") int parentProductId,
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );

    @PUT("wp-json/wc/v3/products/{parent_id}/variations/{id}")
    Call<ProductResponse> updateVariationStock(
            @Path("parent_id") int parentProductId,
            @Path("id") int variationId,
            @Body ProductResponse updatedProduct,
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );

    @PUT("wp-json/wc/v3/coupons/{id}")
    Call<CouponResponse> updateCoupon(
            @Path("id") int couponId,
            @Body CouponRequest couponRequest,
            @Query("consumer_key") String consumerKey,
            @Query("consumer_secret") String consumerSecret
    );

}