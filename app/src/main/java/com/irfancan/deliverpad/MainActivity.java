package com.irfancan.deliverpad;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.irfancan.deliverpad.internetState.InternetStateChecker;
import com.irfancan.deliverpad.models.database.AppDatabase;
import com.irfancan.deliverpad.models.database.Item;
import com.irfancan.deliverpad.models.model.DeliveredItem;
import com.irfancan.deliverpad.models.model.LocationInfo;
import com.irfancan.deliverpad.network.DeliveredItemsFetcherService;
import com.irfancan.deliverpad.network.RetrofitService;
import com.irfancan.deliverpad.presenters.ApiDataPresenter;
import com.irfancan.deliverpad.presenters.CacheDataPresenter;
import com.irfancan.deliverpad.views.ViewUpdater;
import com.irfancan.deliverpad.views.recyclerview.adapter.DeliveriesAdapter;
import com.irfancan.deliverpad.views.recyclerview.listeners.PagingScrollListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ViewUpdater {


    private RecyclerView mDeliveriesRecyclerView;
    private DeliveriesAdapter mDeliveriesAdapter;
    private RecyclerView.LayoutManager mDeliveriesLayoutManager;
    private LinearLayoutManager mDeliveriesLayoutManager_NON_RECYCLER;

    //Room Database instance
    AppDatabase roomDatabase;


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



    //Presenters
    private ApiDataPresenter mApiDataPresenter;              //This presenter is responsible in retrieving data from API and sending it to Views
    private CacheDataPresenter mCacheDataPresenter;          //This one is responsible in storing/retrieving data from Room Database. Will be used as cache (If user opens app when offline)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        roomDatabase = Room.databaseBuilder(getApplicationContext(),
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


        //Presenters
        mApiDataPresenter = new ApiDataPresenter(this);
        mCacheDataPresenter = new CacheDataPresenter(this,roomDatabase);

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

                mApiDataPresenter.getNextDeliveredItemsFromAPI();


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
        //We will only try to retrieve delivered items from API only if internet connection is available
        //If it isn't, we try to retrieve the data from cache instead
        if (InternetStateChecker.isNetwork(this)) {

            mApiDataPresenter.getDeliveredItemsFromAPI();

        }else{

            displayLoadingFromCacheToast();
            mCacheDataPresenter.getDeliveredItemsFromCache();

        }


    }





    @Override
    public void displayFirstDeliveredItemsFromApi(List<DeliveredItem> receivedDeliveredItems) {

        mProgressBarLayout.setVisibility(View.GONE);
        mDeliveriesAdapter.addAll(receivedDeliveredItems);


        //Will store retrieved items to Room Database. This will be used as cache later in case user opens app when he/she is offline.
        mCacheDataPresenter.addDeliveredItemsToDB(receivedDeliveredItems);


        if (currentPage <= TOTAL_PAGES){

            mDeliveriesAdapter.addLoadingFooter();

        }else{

            isLastPage = true;

        }

    }



    @Override
    public void displayNextDeliveredItemsFromApi(List<DeliveredItem> receivedDeliveredItems) {


        mProgressBarLayout.setVisibility(View.GONE);


        mDeliveriesAdapter.removeLoadingFooter();
        isLoading = false;

        mDeliveriesAdapter.addAll(receivedDeliveredItems);


        //Important!
        // CASE 1: If the amount of items we have just received from the API is less than LIMIT (20 in htis caase), it means that we have fetched all the available delivered items from the API(We asked for 20 more new items but received less than 20)
        //In this case we dont want to send a new request to the API again since we have fetched all available items. We do this by not incrementing TOTAL_PAGES anymore
        //
        // CASE 2: However, if the amount of new deliverd items we have just received from the API is LIMIT(20 in this case) amount, it means there is a chance that there can be more items that are yet to be fetched.
        // Therefore in this case we increment page count by 1 so that the API call will be made again when the user scrolls to the bottom of the list
        //
        if(receivedDeliveredItems.size() == LIMIT){

            TOTAL_PAGES++;

        }

        if (currentPage != TOTAL_PAGES){

            mDeliveriesAdapter.addLoadingFooter();

        }
        else{

            isLastPage = true;

        }


    }



    @Override
    public void dispayDeliveredItemsFromCache(List<DeliveredItem> receivedDeliveredItems) {

        mProgressBarLayout.setVisibility(View.GONE);


        mDeliveriesAdapter.removeLoadingFooter();
        isLoading = false;

        mDeliveriesAdapter.addAll(receivedDeliveredItems);

        currentPage = TOTAL_PAGES;
        isLastPage = true;



    }

    @Override
    public void displayErrorLoadingData() {


        //Place Error here


    }




    private void displayLoadingFromCacheToast(){

        Context context = getApplicationContext();
        CharSequence text = "No internet connection available. Loading data from cache!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }




}

