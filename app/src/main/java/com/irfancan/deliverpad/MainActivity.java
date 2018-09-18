package com.irfancan.deliverpad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.irfancan.deliverpad.adapter.DeliveriesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mDeliveriesRecyclerView;
    private RecyclerView.Adapter mDeliveriesAdapter;
    private RecyclerView.LayoutManager mDeliveriesLayoutManager;

    //Just for testing purpose
    List<String> myDeliveries=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView Ref
        mDeliveriesRecyclerView = findViewById(R.id.deliveries_recycler_view);

        //Linear Layout
        mDeliveriesLayoutManager = new LinearLayoutManager(this);
        mDeliveriesRecyclerView.setLayoutManager(mDeliveriesLayoutManager);


        //Just for test purpose
        myDeliveries.add("HONG KONG");
        myDeliveries.add("HELSINKI");
        myDeliveries.add("REYKJAVIK");
        myDeliveries.add("PARIS");



        //Adapter
        mDeliveriesAdapter = new DeliveriesAdapter(myDeliveries);
        mDeliveriesRecyclerView.setAdapter(mDeliveriesAdapter);

    }

}

