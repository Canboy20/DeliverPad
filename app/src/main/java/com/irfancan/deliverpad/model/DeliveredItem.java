package com.irfancan.deliverpad.model;

import com.google.gson.annotations.SerializedName;

public class DeliveredItem {

    @SerializedName("id")
    private int id;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("location")
    private LocationInfo location;




    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocationInfo getLocation() {
        return location;
    }
}
