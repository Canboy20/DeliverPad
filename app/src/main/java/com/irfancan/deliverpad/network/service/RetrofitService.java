package com.irfancan.deliverpad.network.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {


    //BASE URL FOR NOW
    private static final String BASE_URL="https://mock-api-mobile.dev.lalamove.com";

    //https://mock-api-mobile.dev.lalamove.com/deliveries?offset=10&limit=20


    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
