package com.example.inanyplace.Event;

import com.example.inanyplace.Model.Restaurant;

public class MenuItemClickEvent {
    private boolean success;
    private Restaurant restaurantModel;

    public MenuItemClickEvent(boolean success, Restaurant restaurantModel) {
        this.success = success;
        this.restaurantModel = restaurantModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Restaurant getRestaurantModel() {
        return restaurantModel;
    }

    public void setRestaurantModel(Restaurant restaurantModel) {
        this.restaurantModel = restaurantModel;
    }
}
