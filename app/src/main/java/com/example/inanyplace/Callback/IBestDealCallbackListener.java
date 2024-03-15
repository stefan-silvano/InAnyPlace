package com.example.inanyplace.Callback;

import com.example.inanyplace.Model.BestDeal;

import java.util.List;

public interface IBestDealCallbackListener {
    void onBeastDealLoadSuccess(List<BestDeal> bestDealModels);
    void onBeastDealLoadFailed(String message);
}
