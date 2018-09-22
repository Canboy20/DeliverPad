package com.irfancan.deliverpad.models.model;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLocation(LocationInfo location) {
        this.location = location;
    }
}
