package com.irfancan.deliverpad.network.service;

import com.irfancan.deliverpad.model.DeliveredItem;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeliveredItemsFetcherService {


    @GET("deliveries?")
    Single<List<DeliveredItem>> getDeliveredItems(@Query("offset") String offset);

}
