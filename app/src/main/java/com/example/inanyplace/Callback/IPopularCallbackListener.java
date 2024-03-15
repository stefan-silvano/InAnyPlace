package com.example.inanyplace.Callback;

import com.example.inanyplace.Model.PopularCategory;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategory> popularCategoryModels);
    void onPopularLoadFailed(String message);
}
