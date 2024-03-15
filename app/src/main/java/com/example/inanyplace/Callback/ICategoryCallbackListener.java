package com.example.inanyplace.Callback;


import com.example.inanyplace.Model.Category;

import java.util.List;

public interface ICategoryCallbackListener {
    void onPopularLoadSuccess(List<Category> categoryModelList);
    void onPopularLoadFailed(String message);
}
