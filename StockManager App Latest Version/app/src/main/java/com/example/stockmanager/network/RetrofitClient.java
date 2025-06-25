package com.example.stockmanager.network;

import com.example.stockmanager.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.Credentials;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        String authToken = Credentials.basic("ck_ff40981ff997580145f8954ad517ae9664a9cca1", "cs_3e744ecb6cb3e32ad89905f021829e2e3c30a729"); // 🔥 API Keys Replace Kar
                        return chain.proceed(chain.request().newBuilder()
                                .header("Authorization", authToken)
                                .build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://fillingscoulsdon.co.uk/") // 🔥 Tera Store URL
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://fillingscoulsdon.co.uk/") // 👉 change this
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}

