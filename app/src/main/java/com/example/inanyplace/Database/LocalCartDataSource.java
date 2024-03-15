package com.example.inanyplace.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDataAccessObject cartDAO;

    public LocalCartDataSource(CartDataAccessObject cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String uid, String restaurantId) {
        return cartDAO.getAllCart(uid, restaurantId);
    }

    @Override
    public Single<Integer> countItemInCart(String uid, String restaurantId) {
        return cartDAO.countItemInCart(uid, restaurantId);
    }

    @Override
    public Single<Double> sumPriceInCart(String uid, String restaurantId) {
        return cartDAO.sumPriceInCart(uid, restaurantId);
    }

    @Override
    public Single<CartItem> getItemInCart(String foodId, String uid, String restaurantId) {
        return cartDAO.getItemInCart(foodId, uid, restaurantId);
    }

    @Override
    public Single<CartItem> getItemWithAllOptionsInCart(String uid, String foodId, String foodSize, String foodAddon, String restaurantId) {
        return cartDAO.getItemWithAllOptionsInCart(uid, foodId, foodSize, foodAddon, restaurantId);
    }

    @Override
    public Completable insertOrReplace(CartItem... cartItems) {
        return cartDAO.insertOrReplace(cartItems);
    }

    @Override
    public Single<Integer> updateCartItems(CartItem cartItems) {
        return cartDAO.updateCartItems(cartItems);
    }

    @Override
    public Single<Integer> deleteCartItem(CartItem cartItems) {
        return cartDAO.deleteCartItem(cartItems);
    }

    @Override
    public Single<Integer> cleanCart(String uid, String restaurantId) {
        return cartDAO.cleanCart(uid, restaurantId);
    }
}
