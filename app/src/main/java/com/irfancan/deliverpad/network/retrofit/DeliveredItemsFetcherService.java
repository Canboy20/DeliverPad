package com.irfancan.deliverpad.network.retrofit;

import com.irfancan.deliverpad.models.model.DeliveredItem;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeliveredItemsFetcherService {


    @GET("deliveries?")
    Single<List<DeliveredItem>> getDeliveredItems( @Query("offset") String offset , @Query("limit") String limit );

}
