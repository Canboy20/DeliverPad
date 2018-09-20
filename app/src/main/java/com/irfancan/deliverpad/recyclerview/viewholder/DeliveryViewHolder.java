package com.irfancan.deliverpad.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.irfancan.deliverpad.R;

public class DeliveryViewHolder extends RecyclerView.ViewHolder {


    public LinearLayout mLinearLayout;
    private TextView mDescriptionTextView;
    private ImageView mImageOfItemImageView;

    public DeliveryViewHolder(LinearLayout v) {
        super(v);
        mLinearLayout = v;
        mDescriptionTextView = v.findViewById(R.id.descriptionTextView);
        mImageOfItemImageView=v.findViewById(R.id.itemImageView);
    }



    public void updateDescriptionTextView(String description){
        mDescriptionTextView.setText(description);
    }

    public void updateImageView(String urlOfImage){

        Glide.with(mImageOfItemImageView.getContext()).load(urlOfImage).into(mImageOfItemImageView);

    }


}
