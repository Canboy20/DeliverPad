package com.irfancan.deliverpad.presenters;

import android.arch.persistence.room.Room;
import android.util.Log;
import android.view.View;

import com.irfancan.deliverpad.models.database.AppDatabase;
import com.irfancan.deliverpad.models.database.Item;
import com.irfancan.deliverpad.models.model.DeliveredItem;
import com.irfancan.deliverpad.models.model.LocationInfo;
import com.irfancan.deliverpad.views.ViewUpdater;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CacheDataPresenter {

    //This contains methods which will update UI with data
    private ViewUpdater mViewUpdater;


    //Room Database instance
    private AppDatabase roomDB;

    //If activity goes into background, this will be used to dispose any ongoing calls made to the database
    private CompositeDisposable mRequestsDisposables = new CompositeDisposable();


    public CacheDataPresenter(ViewUpdater viewUpdater, AppDatabase roomDatabaseRef){

        mViewUpdater = viewUpdater;
        roomDB = roomDatabaseRef;

    }



    public void getDeliveredItemsFromCache(){

        mRequestsDisposables.add(roomDB.itemDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        deliveredItemsFromDB -> {

                            List<DeliveredItem> myCachedDeliveries = convertDbItemToDeliveredItems(deliveredItemsFromDB);

                            mViewUpdater.dispayDeliveredItemsFromCache(myCachedDeliveries);


                            //}
                        },
                        throwable -> Log.e("ERROR","ERROR WHILE GETTING DATA FROM DB")));


    }



    public void addDeliveredItemsToDB(List<DeliveredItem> receivedDeliveredItems){

        List<Item> dbVersionOfDeliverItems = convertToLocalObject(receivedDeliveredItems);
        Completable.fromAction(() -> roomDB.itemDao().insertAllItems(dbVersionOfDeliverItems))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {
                            Log.v("TEST","STAT: Success :)");
                            //listener.onSuccess();

                        },
                        throwable -> Log.e("ERROR", "DB - Could not add items!"));
    }







    private List<Item> convertToLocalObject(List<DeliveredItem> deliveredItemsRef){


        //I choose linkedlist to preserve insertion order
        List<Item> localObjects=new LinkedList<>();

        for(int i=0;i<deliveredItemsRef.size();i++){

            Item myItem=new Item();
            myItem.setUid(i);
            myItem.setId(deliveredItemsRef.get(i).getId());
            myItem.setDescription(deliveredItemsRef.get(i).getDescription());
            myItem.setImageUrl(deliveredItemsRef.get(i).getImageUrl());

            //Just making sure that location is not null
            if( deliveredItemsRef.get(i).getLocation() != null  ){

                myItem.setLat(deliveredItemsRef.get(i).getLocation().getLat());
                myItem.setLng(deliveredItemsRef.get(i).getLocation().getLng());
                myItem.setAddress(deliveredItemsRef.get(i).getLocation().getAddress());


            }

            localObjects.add(myItem);

        }

        return localObjects;

    }





    private List<DeliveredItem> convertDbItemToDeliveredItems(List<Item> itemsRef){

        //I choose linkedlist to preserve insertion order
        List<DeliveredItem> deliveredItemsObject=new LinkedList<>();

        for(int i=0;i<itemsRef.size();i++){

            DeliveredItem myDeliveredItem=new DeliveredItem();
            myDeliveredItem.setId(itemsRef.get(i).getId());
            myDeliveredItem.setDescription(itemsRef.get(i).getDescription());
            myDeliveredItem.setImageUrl(itemsRef.get(i).getImageUrl());


            LocationInfo myDeliveredItemLocationInfo=new LocationInfo();

            myDeliveredItemLocationInfo.setLat(itemsRef.get(i).getLat());
            myDeliveredItemLocationInfo.setLng(itemsRef.get(i).getLng());
            myDeliveredItemLocationInfo.setAddress(itemsRef.get(i).getAddress());
            myDeliveredItem.setLocation(myDeliveredItemLocationInfo);

            deliveredItemsObject.add(myDeliveredItem);

        }


        return deliveredItemsObject;

    }



}
