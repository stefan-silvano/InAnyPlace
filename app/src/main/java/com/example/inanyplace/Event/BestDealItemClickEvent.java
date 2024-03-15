package com.example.inanyplace.Event;

import com.example.inanyplace.Model.BestDeal;

public class BestDealItemClickEvent {
    private BestDeal bestDealModel;

    public BestDealItemClickEvent(BestDeal bestDealModel) {
        this.bestDealModel = bestDealModel;
    }

    public BestDeal getBestDealModel() {
        return bestDealModel;
    }

    public void setBestDealModel(BestDeal bestDealModel) {
        this.bestDealModel = bestDealModel;
    }
}
