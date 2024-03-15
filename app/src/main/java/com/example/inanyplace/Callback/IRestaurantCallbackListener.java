package com.example.inanyplace.Callback;

import com.example.inanyplace.Model.Restaurant;

import java.util.List;

public interface IRestaurantCallbackListener {
    void onRestaurantLoadSuccess(List<Restaurant> restaurantModelList);

    void onRestaurantLoadFailed(String message);
}
