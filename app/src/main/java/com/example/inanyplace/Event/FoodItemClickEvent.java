package com.example.inanyplace.Event;

import com.example.inanyplace.Model.Food;

public class FoodItemClickEvent {
    private boolean success;
    private Food foodModel;

    public FoodItemClickEvent(boolean success, Food foodModel) {
        this.success = success;
        this.foodModel = foodModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Food getFoodModel() {
        return foodModel;
    }

    public void setFoodModel(Food foodModel) {
        this.foodModel = foodModel;
    }
}
