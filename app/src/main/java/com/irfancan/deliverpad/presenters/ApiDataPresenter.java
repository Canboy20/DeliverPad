package com.irfancan.deliverpad.presenters;

import android.util.Log;
import android.view.View;

import com.irfancan.deliverpad.models.database.Item;
import com.irfancan.deliverpad.models.model.DeliveredItem;
import com.irfancan.deliverpad.network.DeliveredItemsFetcherService;
import com.irfancan.deliverpad.network.RetrofitService;
import com.irfancan.deliverpad.views.ViewUpdater;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ApiDataPresenter {


    //This contains methods which will update UI with data
    private ViewUpdater mViewUpdater;


    //If activity goes into background, this will be used to dispose any ongoing rxJava/Retrofit calls as there wont be any visible UI to display retrieved data
    private CompositeDisposable mRequestsDisposables = new CompositeDisposable();


    //These will be added to the BASE URL to tell API how much of the delivered items we would like to retrieve from the Server and from where the offset should start
    private int OFFSET=0;
    private final int LIMIT = 20;


    //When user reaches the end of the list, we will request API for the next delivered items. If we receive at least 1 deliverd item, this count will be increased
    private static final int PAGE_START = 0;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;



    public ApiDataPresenter(ViewUpdater viewUpdater){

        mViewUpdater = viewUpdater;

    }


    public void getDeliveredItemsFromAPI(){

        //Service that will fetch Delivered items
        DeliveredItemsFetcherService apiService = RetrofitService.getClient().create(DeliveredItemsFetcherService.class);


        mRequestsDisposables.add(apiService.getDeliveredItems(OFFSET+"",LIMIT+"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DeliveredItem>>() {
                    @Override
                    public void onSuccess(List<DeliveredItem> deliveredItemsResponse) {

                        Log.d("REQUEST SUCCESS","SUCCESS");

                        //Will update UI with new data
                        mViewUpdater.displayFirstDeliveredItemsFromApi(deliveredItemsResponse);

                    }


                    @Override
                    public void onError(Throwable e) {

                        Log.d("REQUEST FAILED","FAILED");


                        mViewUpdater.displayErrorLoadingData();


                    }
                }));

    }





    public void getNextDeliveredItemsFromAPI(){


        //Service that will fetch Delivered items
        DeliveredItemsFetcherService apiService = RetrofitService.getClient().create(DeliveredItemsFetcherService.class);

        OFFSET = OFFSET + LIMIT;
        //LIMIT = LIMIT + 20;

        mRequestsDisposables.add(apiService.getDeliveredItems(OFFSET+"",LIMIT+"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DeliveredItem>>() {
                    @Override
                    public void onSuccess(List<DeliveredItem> rootResponse) {


                        Log.d("REQUEST SUCCESS","SUCCESS");
                        mViewUpdater.displayNextDeliveredItemsFromApi(rootResponse);


                    }

                    @Override
                    public void onError(Throwable e) {

                        // Network error
                        Log.d("REQUEST FAILED","FAILED");

                        mViewUpdater.displayErrorLoadingData();


                    }
                }));



    }



}
