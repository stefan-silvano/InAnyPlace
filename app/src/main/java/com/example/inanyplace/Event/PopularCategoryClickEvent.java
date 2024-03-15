package com.example.inanyplace.Event;

import com.example.inanyplace.Model.PopularCategory;

public class PopularCategoryClickEvent {
    private PopularCategory popularCategoryModel;

    public PopularCategoryClickEvent(PopularCategory popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }

    public PopularCategory getPopularCategoryModel() {
        return popularCategoryModel;
    }

    public void setPopularCategoryModel(PopularCategory popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }
}
