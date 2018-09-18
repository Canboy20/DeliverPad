package com.irfancan.deliverpad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.irfancan.deliverpad.adapter.DeliveriesAdapter;
import com.irfancan.deliverpad.model.DeliveredItem;
import com.irfancan.deliverpad.network.service.DeliveredItemsFetcherService;
import com.irfancan.deliverpad.network.service.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mDeliveriesRecyclerView;
    private RecyclerView.Adapter mDeliveriesAdapter;
    private RecyclerView.LayoutManager mDeliveriesLayoutManager;

    //Just for testing purpose
    List<String> myDeliveries=new ArrayList<>();


    private CompositeDisposable mRequestsDisposables = new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView Ref
        mDeliveriesRecyclerView = findViewById(R.id.deliveries_recycler_view);

        //Linear Layout
        mDeliveriesLayoutManager = new LinearLayoutManager(this);
        mDeliveriesRecyclerView.setLayoutManager(mDeliveriesLayoutManager);


        //Just for test purpose
        myDeliveries.add("HONG KONG");
        myDeliveries.add("HELSINKI");
        myDeliveries.add("REYKJAVIK");
        myDeliveries.add("PARIS");



        //Adapter
        mDeliveriesAdapter = new DeliveriesAdapter(myDeliveries);
        mDeliveriesRecyclerView.setAdapter(mDeliveriesAdapter);

        getDeliveredItemsFromAPI();

    }




    public void getDeliveredItemsFromAPI(){

        //Service that will fetch Delivered items
        DeliveredItemsFetcherService apiService = RetrofitService.getClient().create(DeliveredItemsFetcherService.class);


        mRequestsDisposables.add(apiService.getDeliveredItems("0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DeliveredItem>>() {
                    @Override
                    public void onSuccess(List<DeliveredItem> rootResponse) {

                        Log.d("REQUEST SUCCESS","SUCCESS");

                    }

                    @Override
                    public void onError(Throwable e) {

                        // Network error
                        Log.d("REQUEST FAILED","FAILED");


                    }
                }));



    }

}

