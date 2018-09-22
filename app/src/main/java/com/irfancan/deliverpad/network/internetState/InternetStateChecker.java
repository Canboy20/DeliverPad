package com.irfancan.deliverpad.network.internetState;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetStateChecker {

    //This class is responsible in checking whether the device has an active internet connection
    //If it has, delivered items will be retrieved from API
    //If not, the items will be restored from the cache(Hope there was a time the user was able to retrieve the data from the API otherwise the cache will be empty! :/ )
    public static boolean isNetwork(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}