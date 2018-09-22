package com.irfancan.deliverpad.views;

import com.irfancan.deliverpad.models.model.DeliveredItem;

import java.util.List;

public interface ViewUpdater {

    void displayFirstDeliveredItemsFromApi(List<DeliveredItem> receivedDeliveredItems);

    void displayNextDeliveredItemsFromApi(List<DeliveredItem> receivedDeliveredItems);

    void dispayDeliveredItemsFromCache(List<DeliveredItem> receivedDeliveredItems);

    void displayErrorLoadingData();

    void displayErrorLoadingDataOnViewHolder();

    void displayCacheIsEmpty();

}
