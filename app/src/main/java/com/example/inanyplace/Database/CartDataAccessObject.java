package com.example.inanyplace.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDataAccessObject {

    @Query("SELECT * FROM Cart WHERE uid=:uid AND restaurantId=:restaurantId")
    Flowable<List<CartItem>> getAllCart(String uid, String restaurantId);

    @Query("SELECT SUM(foodQuantity) FROM Cart WHERE uid=:uid AND restaurantId=:restaurantId")
    Single<Integer> countItemInCart(String uid, String restaurantId);

    @Query("SELECT SUM((foodPrice + foodExtraPrice) * foodQuantity) FROM Cart WHERE uid=:uid AND restaurantId=:restaurantId")
    Single<Double> sumPriceInCart(String uid, String restaurantId);

    @Query("SELECT * FROM Cart WHERE foodId=:foodId AND uid=:uid AND restaurantId=:restaurantId")
    Single<CartItem> getItemInCart(String foodId, String uid, String restaurantId);

    @Query("SELECT * FROM Cart WHERE uid=:uid AND foodId=:foodId AND foodSize=:foodSize AND foodAddon=:foodAddon AND restaurantId=:restaurantId")
    Single<CartItem> getItemWithAllOptionsInCart(String uid, String foodId, String foodSize, String foodAddon, String restaurantId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplace(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItems(CartItem cartItems);

    @Delete
    Single<Integer> deleteCartItem(CartItem cartItems);

    @Query("DELETE FROM Cart WHERE uid=:uid AND restaurantId=:restaurantId")
    Single<Integer> cleanCart(String uid, String restaurantId);
}
