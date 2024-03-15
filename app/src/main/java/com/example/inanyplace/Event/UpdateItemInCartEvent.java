package com.example.inanyplace.Event;

import com.example.inanyplace.Database.CartItem;

public class UpdateItemInCartEvent {
    private CartItem cartItem;

    public UpdateItemInCartEvent(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}
