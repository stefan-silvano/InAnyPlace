package com.example.inanyplace.Database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cart", primaryKeys = {"uid", "foodId", "foodAddon", "foodSize", "restaurantId"})
public class CartItem {

    @NonNull
    @ColumnInfo(name = "restaurantId")
    private String restaurantId;

    @NonNull
    @ColumnInfo(name = "foodId")
    private String foodId;

    @ColumnInfo(name = "foodName")
    private String foodName;

    @ColumnInfo(name = "foodImage")
    private String foodImage;

    @ColumnInfo(name = "foodPrice")
    private Double foodPrice;

    @ColumnInfo(name = "foodQuantity")
    private int foodQuantity;

    @ColumnInfo(name = "foodExtraPrice")
    private Double foodExtraPrice;

    @NonNull
    @ColumnInfo(name = "foodAddon")
    private String foodAddon;

    @NonNull
    @ColumnInfo(name = "foodSize")
    private String foodSize;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(@NonNull String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public Double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(Double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(int foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public Double getFoodExtraPrice() {
        return foodExtraPrice;
    }

    public void setFoodExtraPrice(Double foodExtraPrice) {
        this.foodExtraPrice = foodExtraPrice;
    }

    public String getFoodAddon() {
        return foodAddon;
    }

    public void setFoodAddon(String foodAddon) {
        this.foodAddon = foodAddon;
    }

    public String getFoodSize() {
        return foodSize;
    }

    public void setFoodSize(String foodSize) {
        this.foodSize = foodSize;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @NonNull
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(@NonNull String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof CartItem))
            return false;
        CartItem cartItem = (CartItem) obj;
        return cartItem.getFoodId().equals(this.foodId) &&
                cartItem.getFoodAddon().equals(this.foodAddon) &&
                cartItem.getFoodSize().equals(this.foodSize);
    }
}
