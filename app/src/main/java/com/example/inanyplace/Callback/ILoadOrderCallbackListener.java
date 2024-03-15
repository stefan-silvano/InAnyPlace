package com.example.inanyplace.Callback;

import com.example.inanyplace.Model.Order;

import java.util.List;

public interface ILoadOrderCallbackListener {

    void onLoadOrderSuccess(List<Order> orderList);

    void onLoadOrderFailed(String message);
}
