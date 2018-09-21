package com.irfancan.deliverpad;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.irfancan.deliverpad.InternetState.InternetStateChecker;
import com.irfancan.deliverpad.Room.AppDatabase;
import com.irfancan.deliverpad.Room.Item;
import com.irfancan.deliverpad.model.DeliveredItem;
import com.irfancan.deliverpad.model.LocationInfo;
import com.irfancan.deliverpad.network.service.DeliveredItemsFetcherService;
import com.irfancan.deliverpad.network.service.RetrofitService;
import com.irfancan.deliverpad.recyclerview.adapter.DeliveriesAdapter;
import com.irfancan.deliverpad.recyclerview.listeners.PagingScrollListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mDeliveriesRecyclerView;
    private DeliveriesAdapter mDeliveriesAdapter;
    private RecyclerView.LayoutManager mDeliveriesLayoutManager;
    private LinearLayoutManager mDeliveriesLayoutManager_NON_RECYCLER;

    //Room Database instance
    AppDatabase db;


    //ProgressBar ref
    private LinearLayout mProgressBarLayout;

    //Just for testing purpose
    List<DeliveredItem> myDeliveries=new ArrayList<>();
    List<DeliveredItem> myCachedDeliveries=new LinkedList<>();


    private int OFFSET=0;
    private int LIMIT = 20;


    private CompositeDisposable mRequestsDisposables = new CompositeDisposable();


    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    //When user reaches the end of the list, we will request API for the next delivered items. If we receive at least 1 deliverd item, this count will be increased
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        //Progressbar Ref
        mProgressBarLayout=findViewById(R.id.progressbar_linear_layout);

        //RecyclerView Ref
        mDeliveriesRecyclerView = findViewById(R.id.deliveries_recycler_view);

        //Linear Layout
        /*mDeliveriesLayoutManager = new LinearLayoutManager(this);
        mDeliveriesRecyclerView.setLayoutManager(mDeliveriesLayoutManager);*/


        mDeliveriesLayoutManager_NON_RECYCLER = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mDeliveriesRecyclerView.setLayoutManager(mDeliveriesLayoutManager_NON_RECYCLER);


        //Adapter
        mDeliveriesAdapter = new DeliveriesAdapter(this);
        mDeliveriesRecyclerView.setAdapter(mDeliveriesAdapter);


        //Custom scrolllistener which implements paging
        mDeliveriesRecyclerView.addOnScrollListener(new PagingScrollListener(mDeliveriesLayoutManager_NON_RECYCLER) {

            @Override
            protected void loadNextDeliveredItems() {

                mProgressBarLayout.setVisibility(View.VISIBLE);

                isLoading = true;
                currentPage += 1;

                getNextDeliveredItemsFromAPI();


            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });







        //Check if Internet connection is available
        //We will only try to retrieve delivered items from API only if Internet connection is available
        //If it isnt, we try to display cached data instead
        if (InternetStateChecker.isNetwork(this)) {

            getDeliveredItemsFromAPI();

        }else{

            displayLoadingFromCacheToast();
            getDeliveredItemsFromCache();

        }


    }










    public void getDeliveredItemsFromAPI(){

        //Service that will fetch Delivered items
        DeliveredItemsFetcherService apiService = RetrofitService.getClient().create(DeliveredItemsFetcherService.class);


        mRequestsDisposables.add(apiService.getDeliveredItems(OFFSET+"",LIMIT+"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DeliveredItem>>() {
                    @Override
                    public void onSuccess(List<DeliveredItem> rootResponse) {


                        mProgressBarLayout.setVisibility(View.GONE);
                        mDeliveriesAdapter. addAll(rootResponse);


                        //Store retrieved valies to local DB
                        List<Item> dbVersionOfDeliverItems = convertToLocalObject(rootResponse);
                        Completable.fromAction(() -> db.itemDao().insertAllItems(dbVersionOfDeliverItems))
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        () -> {
                                            Log.v("TEST","STAT: Success :)");
                                            //listener.onSuccess();

                                        },
                                        throwable -> Log.e("ERROR", "DB - Could not add items!"));



                        if (currentPage <= TOTAL_PAGES){

                            mDeliveriesAdapter.addLoadingFooter();

                        }else{

                            isLastPage = true;

                        }

                        //Log.d("REQUEST SUCCESS","SUCCESS");
                        //mDeliveriesAdapter = new DeliveriesAdapter(rootResponse);
                        //mDeliveriesRecyclerView.setAdapter(mDeliveriesAdapter);

                        //mDeliveriesAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(Throwable e) {

                        mProgressBarLayout.setVisibility(View.GONE);




                        // Network error
                        Log.d("REQUEST FAILED","FAILED");


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


                        mProgressBarLayout.setVisibility(View.GONE);


                        mDeliveriesAdapter.removeLoadingFooter();
                        isLoading = false;

                        mDeliveriesAdapter.addAll(rootResponse);


                        //Important!
                        // CASE 1: If the amount of items we have just received from the API is less than LIMIT (20 in htis caase), it means that we have fetched all the available delivered items from the API(We asked for 20 more new items but received less than 20)
                        //In this case we dont want to send a new request to the API again since we have fetched all available items. We do this by not incrementing TOTAL_PAGES anymore
                        //
                        // CASE 2: However, if the amount of new deliverd items we have just received from the API is LIMIT(20 in this case) amount, it means there is a chance that there can be more items that are yet to be fetched.
                        // Therefore in this case we increment page count by 1 so that the API call will be made again when the user scrolls to the bottom of the list
                        //
                        if(rootResponse.size() == LIMIT){

                            TOTAL_PAGES++;

                        }

                        if (currentPage != TOTAL_PAGES){

                            mDeliveriesAdapter.addLoadingFooter();

                        }
                        else{

                            isLastPage = true;

                        }


                        Log.d("REQUEST SUCCESS","SUCCESS");
                        //mDeliveriesAdapter = new DeliveriesAdapter(rootResponse);
                        //mDeliveriesRecyclerView.setAdapter(mDeliveriesAdapter);

                        //mDeliveriesAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(Throwable e) {

                        mProgressBarLayout.setVisibility(View.GONE);

                        // Network error
                        Log.d("REQUEST FAILED","FAILED");


                    }
                }));

    }




    private List<Item> convertToLocalObject(List<DeliveredItem> deliveredItemsRef){


        //I choose linkedlist to preserve insertion order
        List<Item> localObjects=new LinkedList<>();

        for(int i=0;i<deliveredItemsRef.size();i++){

            Item myItem=new Item();
            myItem.setUid(i);
            myItem.setId(deliveredItemsRef.get(i).getId());
            myItem.setDescription(deliveredItemsRef.get(i).getDescription());
            myItem.setImageUrl(deliveredItemsRef.get(i).getImageUrl());

            //Just making sure that location is not null
            if( deliveredItemsRef.get(i).getLocation() != null  ){

                myItem.setLat(deliveredItemsRef.get(i).getLocation().getLat());
                myItem.setLng(deliveredItemsRef.get(i).getLocation().getLng());
                myItem.setAddress(deliveredItemsRef.get(i).getLocation().getAddress());


            }

            localObjects.add(myItem);

        }

        return localObjects;

    }



    private List<DeliveredItem> convertDbItemToDeliveredItems(List<Item> itemsRef){

        //I choose linkedlist to preserve insertion order
        List<DeliveredItem> deliveredItemsObject=new LinkedList<>();

        for(int i=0;i<itemsRef.size();i++){

            DeliveredItem myDeliveredItem=new DeliveredItem();
            myDeliveredItem.setId(itemsRef.get(i).getId());
            myDeliveredItem.setDescription(itemsRef.get(i).getDescription());
            myDeliveredItem.setImageUrl(itemsRef.get(i).getImageUrl());


            LocationInfo myDeliveredItemLocationInfo=new LocationInfo();

            myDeliveredItemLocationInfo.setLat(itemsRef.get(i).getLat());
            myDeliveredItemLocationInfo.setLng(itemsRef.get(i).getLng());
            myDeliveredItemLocationInfo.setAddress(itemsRef.get(i).getAddress());
            myDeliveredItem.setLocation(myDeliveredItemLocationInfo);

            deliveredItemsObject.add(myDeliveredItem);

        }


        return deliveredItemsObject;

    }






    private void displayLoadingFromCacheToast(){

        Context context = getApplicationContext();
        CharSequence text = "No internet connection available. Loading data from cache!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }



    private void getDeliveredItemsFromCache(){

        db.itemDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        deliveredItemsFromDB -> {

                            myCachedDeliveries = convertDbItemToDeliveredItems(deliveredItemsFromDB);

                            mProgressBarLayout.setVisibility(View.GONE);
                            mDeliveriesAdapter. addAll(myCachedDeliveries);

                            //}
                        },
                        throwable -> Log.e("ERROR","ERROR WHILE GETTING DATA FROM DB"));


    }



/*
    public void getDeliveredItemsFromAPI(){

        //Service that will fetch Delivered items
        DeliveredItemsFetcherService apiService = RetrofitService.getClient().create(DeliveredItemsFetcherService.class);


        mRequestsDisposables.add(apiService.getDeliveredItems("0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<DeliveredItem>>() {
                    @Override
                    public void onSuccess(List<DeliveredItem> rootResponse) {


                        mProgressBar.setVisibility(View.GONE);

                        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                        else isLastPage = true;

                        Log.d("REQUEST SUCCESS","SUCCESS");
                        mDeliveriesAdapter = new DeliveriesAdapter(rootResponse);
                        mDeliveriesRecyclerView.setAdapter(mDeliveriesAdapter);

                        mDeliveriesAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(Throwable e) {

                        mProgressBar.setVisibility(View.GONE);

                        // Network error
                        Log.d("REQUEST FAILED","FAILED");


                    }
                }));



    }*/

}

