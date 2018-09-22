package com.irfancan.deliverpad.views.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.irfancan.deliverpad.R;
import com.irfancan.deliverpad.views.RecyclerViewHelpers;

public class LoadingViewHolder extends RecyclerView.ViewHolder {


    public LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;
    private LinearLayout mTryAgainLayout;
    private TextView mTryAgainTextView;
    private RecyclerViewHelpers mRecyclerViewHelpers;



    public LoadingViewHolder(LinearLayout v, RecyclerViewHelpers recyclerViewHelpers) {
        super(v);

        mLinearLayout = v;
        mRecyclerViewHelpers = recyclerViewHelpers;

        mTryAgainLayout =  v.findViewById(R.id.try_again_viewholder_layout);
        mProgressBar = v. findViewById(R.id.loading_viewholder_progressBar);
        mTryAgainTextView = v.findViewById(R.id.try_again_viewholder_textView);

        //If user decides to try reloading the data again, then we will send a new request to API
        mTryAgainTextView.setOnClickListener(v1 -> {

            displayLoading();
            mRecyclerViewHelpers.retryReceivingNextItemsFromAPI();

        });

    }




    public void displayTryAgain(){

        mProgressBar.setVisibility(View.INVISIBLE);
        mTryAgainLayout.setVisibility(View.VISIBLE);


    }


    public void displayLoading(){

        mProgressBar.setVisibility(View.VISIBLE);
        mTryAgainLayout.setVisibility(View.GONE);


    }






}
