package com.example.inanyplace.Database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllCart(String uid, String restaurantId);

    Single<Integer> countItemInCart(String uid, String restaurantId);

    Single<Double> sumPriceInCart(String uid, String restaurantId);

    Single<CartItem> getItemInCart(String foodId, String uid, String restaurantId);

    Single<CartItem> getItemWithAllOptionsInCart(String uid, String foodId, String foodSize, String foodAddon, String restaurantId);

    Completable insertOrReplace(CartItem... cartItems);

    Single<Integer> updateCartItems(CartItem cartItems);

    Single<Integer> deleteCartItem(CartItem cartItems);

    Single<Integer> cleanCart(String uid, String restaurantId);
}
