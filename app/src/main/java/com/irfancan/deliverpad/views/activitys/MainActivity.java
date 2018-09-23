package com.irfancan.deliverpad.views.activitys;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.irfancan.deliverpad.R;
import com.irfancan.deliverpad.constants.ToastType;
import com.irfancan.deliverpad.network.internetState.InternetStateChecker;
import com.irfancan.deliverpad.models.database.AppDatabase;
import com.irfancan.deliverpad.models.model.DeliveredItem;
import com.irfancan.deliverpad.presenters.ApiDataPresenter;
import com.irfancan.deliverpad.presenters.CacheDataPresenter;
import com.irfancan.deliverpad.views.RecyclerViewHelpers;
import com.irfancan.deliverpad.views.ViewUpdater;
import com.irfancan.deliverpad.views.recyclerview.adapter.DeliveriesAdapter;
import com.irfancan.deliverpad.views.recyclerview.listeners.PagingScrollListener;
import com.irfancan.deliverpad.views.recyclerview.viewholder.LoadingViewHolder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewUpdater,RecyclerViewHelpers {


    private RecyclerView mDeliveriesRecyclerView;
    private DeliveriesAdapter mDeliveriesAdapter;
    private RecyclerView.LayoutManager mDeliveriesLayoutManager;
    private LinearLayoutManager mDeliveriesLayoutManager_NON_RECYCLER;

    //Room Database instance
    AppDatabase roomDatabase;


    //ProgressBar & Try Again layout ref
    private LinearLayout mProgressBarLayout;
    private LinearLayout mTryAgainLayout;

    private TextView tryAgainTextView;


    private int LIMIT = 20;



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

        //Layout Ref
        mProgressBarLayout=findViewById(R.id.progressbar_linear_layout);
        mTryAgainLayout=findViewById(R.id.try_again_layout);

        //RecyclerView Ref
        mDeliveriesRecyclerView = findViewById(R.id.deliveries_recycler_view);

        //Linear Layout
        /*mDeliveriesLayoutManager = new LinearLayoutManager(this);
        mDeliveriesRecyclerView.setLayoutManager(mDeliveriesLayoutManager);*/
        mDeliveriesLayoutManager_NON_RECYCLER = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mDeliveriesRecyclerView.setLayoutManager(mDeliveriesLayoutManager_NON_RECYCLER);

        //Try again text Ref. This will only be visible if at the very first attempt of retrieving data from API fails
        tryAgainTextView = findViewById(R.id.try_again_textView);
        tryAgainTextView.setOnClickListener(v -> {

            mTryAgainLayout.setVisibility(View.GONE);
            mProgressBarLayout.setVisibility(View.VISIBLE);
            retryReceivingItemsFromAPI();

        });

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

                //If user reaches end of list, then lets retrieve next items by making a request to API
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

            displayToast(ToastType.LOADING_DATA_FROM_CACHE_TOAST);
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

        //If cache is empty(size is 0), then there is no possible data that can be displayed. Too Bad :(
        if(receivedDeliveredItems.size()==0){
            displayToast(ToastType.CACHE_IS_EMPTY_TOAST);

        }else{

            mDeliveriesAdapter. addAll(receivedDeliveredItems);
            LIMIT = receivedDeliveredItems.size();
            isLastPage = true;

        }

    }

    @Override
    public void displayErrorLoadingData() {

        mProgressBarLayout.setVisibility(View.GONE);
        mTryAgainLayout.setVisibility(View.VISIBLE);

    }



    @Override
    public void displayErrorLoadingDataOnViewHolder() {

        RecyclerView.ViewHolder lastVisibleViewHolder = mDeliveriesRecyclerView.findViewHolderForAdapterPosition(mDeliveriesLayoutManager_NON_RECYCLER.getItemCount()-1);

        //Just being extra careful that the last viewholder is a Loading ViewHolder
        if(lastVisibleViewHolder instanceof LoadingViewHolder){

            ((LoadingViewHolder) lastVisibleViewHolder).displayTryAgain();

        }

    }

    /**This will be called from the Main Activity Layout(When user clicks at Try Again text)**/
    public void retryReceivingItemsFromAPI() {

        mApiDataPresenter.getDeliveredItemsFromAPI();
    }


    /**This will be called from the Loading ViewHolder(When user clicks at Try Again text)**/
    @Override
    public void retryReceivingNextItemsFromAPI() {
        mApiDataPresenter.getNextDeliveredItemsFromAPI();
    }


    @Override
    public Context getContext() {
        return this;
    }


    private void displayToast(ToastType toastType){

        int duration = Toast.LENGTH_LONG;

        if(toastType == ToastType.LOADING_DATA_FROM_CACHE_TOAST){

            CharSequence text = "No internet connection available. Loading data from cache!";
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();

        }else if(toastType == ToastType.CACHE_IS_EMPTY_TOAST){

            CharSequence text = "Cache is Empty. Sorry :( ";
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }

    }

//OnResume is responsible in restoring any RxJava calls that was disposed.
    @Override
    protected void onResume() {
        super.onResume();

        mApiDataPresenter.resumeDisposedCall();

    }

//OnStop is responsible in disposing any ongoing RxJava calls.
    @Override
    protected void onStop() {
        super.onStop();

        mApiDataPresenter.disposeCalls();
    }
}

