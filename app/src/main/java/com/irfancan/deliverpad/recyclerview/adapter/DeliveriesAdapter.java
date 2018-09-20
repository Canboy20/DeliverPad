package com.irfancan.deliverpad.recyclerview.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.irfancan.deliverpad.Constants.Constants;
import com.irfancan.deliverpad.DeliveredItemDetailsActivity;
import com.irfancan.deliverpad.R;
import com.irfancan.deliverpad.model.DeliveredItem;
import com.irfancan.deliverpad.recyclerview.viewholder.DeliveryViewHolder;
import com.irfancan.deliverpad.recyclerview.viewholder.LoadingViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DeliveriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int ITEM = 0;
    private static final int LOADING = 1;


    private List<DeliveredItem> mDeliveredItems;

    private Context context;
    private boolean isLoadingAdded = false;



    public DeliveriesAdapter(Context context) {
        this.context = context;
        mDeliveredItems = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                LinearLayout deliveredItemRow= (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.delivered_item, parent, false);
                viewHolder = new DeliveryViewHolder(deliveredItemRow);
                break;

            case LOADING:
                View v2 = inflater.inflate(R.layout.loading_item, parent, false);
                viewHolder = new LoadingViewHolder(v2);
                break;
        }
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        switch (getItemViewType(position)) {
            case ITEM:

                DeliveryViewHolder deliveredItemHolder = (DeliveryViewHolder) holder;
                deliveredItemHolder.updateDescriptionTextView(mDeliveredItems.get(position).getDescription());
                deliveredItemHolder.updateLocationNameTextView(mDeliveredItems.get(position).getLocation() != null ? mDeliveredItems.get(position).getLocation().getAddress() : "");
                deliveredItemHolder.updateImageView(mDeliveredItems.get(position).getImageUrl());
                deliveredItemHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent deliveredItemIntent=new Intent(context, DeliveredItemDetailsActivity.class);
                        Bundle deliveredItemBundle = new Bundle();

                        deliveredItemBundle.putString(Constants.ITEM_NAME, mDeliveredItems.get(position).getDescription());
                        deliveredItemBundle.putString(Constants.ITEM_IMG_URL, mDeliveredItems.get(position).getImageUrl());

                        if(mDeliveredItems.get(position).getLocation() != null){

                            deliveredItemBundle.putString(Constants.ITEM_ADDRESS, mDeliveredItems.get(position).getLocation().getAddress());
                            deliveredItemBundle.putDouble(Constants.ITEM_LONG, mDeliveredItems.get(position).getLocation().getLng());
                            deliveredItemBundle.putDouble(Constants.ITEM_LATI, mDeliveredItems.get(position).getLocation().getLat());

                        }


                        deliveredItemIntent.putExtras(deliveredItemBundle);
                        context.startActivity(deliveredItemIntent);

                    }
                });

                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        //Just being extra careful by checking null condition
        return mDeliveredItems == null ? 0 : mDeliveredItems.size();

    }



    public List<DeliveredItem> getmDeliveredItems() {
        return mDeliveredItems;
    }

    public void setMovies(List<DeliveredItem> deliveredItems) {
        this.mDeliveredItems = deliveredItems;
    }










    public void add(DeliveredItem mc) {
        mDeliveredItems.add(mc);
        notifyItemInserted(mDeliveredItems.size() - 1);
    }

    public void addAll(List<DeliveredItem> mcList) {
        for (DeliveredItem mc : mcList) {
            add(mc);
        }
    }

    public void remove(DeliveredItem city) {
        int position = mDeliveredItems.indexOf(city);
        if (position > -1) {
            mDeliveredItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new DeliveredItem());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mDeliveredItems.size() - 1;
        DeliveredItem item = getItem(position);

        if (item != null) {
            mDeliveredItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public DeliveredItem getItem(int position) {
        return mDeliveredItems.get(position);
    }









    /*private List<DeliveredItem> mDeliveredItems;


    public DeliveriesAdapter(List<DeliveredItem> myDataset) {
        mDeliveredItems = myDataset;
    }

    @Override
    public DeliveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout deliveredItemRow= (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.delivered_item, parent, false);
        DeliveryViewHolder vh = new DeliveryViewHolder(deliveredItemRow);
        return vh;
    }

    @Override
    public void onBindViewHolder(DeliveryViewHolder holder, int position) {

        holder.getDescriptionTextView().setText(mDeliveredItems.get(position).getDescription());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        //Just being extra careful by checking null condition
        return mDeliveredItems == null ? 0 : mDeliveredItems.size();

    }*/
}
