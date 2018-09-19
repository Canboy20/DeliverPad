package com.irfancan.deliverpad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.irfancan.deliverpad.model.DeliveredItem;
import com.irfancan.deliverpad.network.service.DeliveredItemsFetcherService;
import com.irfancan.deliverpad.network.service.RetrofitService;
import com.irfancan.deliverpad.recyclerview.adapter.DeliveriesAdapter;
import com.irfancan.deliverpad.recyclerview.listeners.PagingScrollListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mDeliveriesRecyclerView;
    private DeliveriesAdapter mDeliveriesAdapter;
    private RecyclerView.LayoutManager mDeliveriesLayoutManager;
    private LinearLayoutManager mDeliveriesLayoutManager_NON_RECYCLER;


    //ProgressBar ref
    private LinearLayout mProgressBarLayout;

    //Just for testing purpose
    List<DeliveredItem> myDeliveries=new ArrayList<>();


    private int OFFSET=0;
    private int LIMIT = 20;


    private CompositeDisposable mRequestsDisposables = new CompositeDisposable();


    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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


        getDeliveredItemsFromAPI();

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

                        if (currentPage != TOTAL_PAGES) mDeliveriesAdapter.addLoadingFooter();
                        else isLastPage = true;

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

