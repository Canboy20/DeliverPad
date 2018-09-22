package com.irfancan.deliverpad.views.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.irfancan.deliverpad.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {


    public LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;
    private LinearLayout mTryAgainLayout;
    private TextView mTryAgainTextView;



    public LoadingViewHolder(LinearLayout v) {
        super(v);
        mLinearLayout = v;
        mTryAgainLayout =  v.findViewById(R.id.try_again_viewholder_layout);
        mProgressBar = v. findViewById(R.id.loading_viewholder_progressBar);
        mTryAgainTextView = v.findViewById(R.id.try_again_viewholder_textView);
        mTryAgainTextView.setOnClickListener(v1 -> {

        });

    }




    public void displayTryAgain(){

        mProgressBar.setVisibility(View.INVISIBLE);
        mTryAgainLayout.setVisibility(View.VISIBLE);


    }






}
