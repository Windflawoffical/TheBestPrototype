package com.example.thebestprototype.API;


import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit retrofit;
    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    public RetrofitService(){
        initializeRetrofit();
    }
    private void initializeRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl("https://backend-webserver-tbp.onrender.com")
                .addConverterFactory(GsonConverterFactory.create(new Gson())).client(okHttpClient).build();

    }
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
