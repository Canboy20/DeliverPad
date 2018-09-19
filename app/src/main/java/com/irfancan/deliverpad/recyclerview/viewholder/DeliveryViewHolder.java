package com.irfancan.deliverpad.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irfancan.deliverpad.R;

public class DeliveryViewHolder extends RecyclerView.ViewHolder {


    public LinearLayout mLinearLayout;
    private TextView mDescriptionTextView;

    public DeliveryViewHolder(LinearLayout v) {
        super(v);
        mLinearLayout = v;
        mDescriptionTextView = v.findViewById(R.id.descriptionTextView);
    }



    public TextView getDescriptionTextView(){
        return mDescriptionTextView;
    }

}
