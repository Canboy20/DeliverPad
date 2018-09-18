package com.irfancan.deliverpad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irfancan.deliverpad.R;
import com.irfancan.deliverpad.viewholder.DeliveryViewHolder;

import java.util.List;

public class DeliveriesAdapter extends RecyclerView.Adapter<DeliveryViewHolder> {


    private List<String> mDeliveriesName;


    public DeliveriesAdapter(List<String> myDataset) {
        mDeliveriesName = myDataset;
    }

    @Override
    public DeliveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout deliveredItemRow= (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.delivered_item, parent, false);
        DeliveryViewHolder vh = new DeliveryViewHolder(deliveredItemRow);
        return vh;
    }

    @Override
    public void onBindViewHolder(DeliveryViewHolder holder, int position) {

        holder.getDescriptionTextView().setText(mDeliveriesName.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDeliveriesName.size();
    }
}
