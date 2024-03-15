package com.example.inanyplace.Event;

import com.example.inanyplace.Model.Category;

public class CatetgoryClickEvent {
    private boolean success;
    private Category categoryModel;

    public CatetgoryClickEvent(boolean success, Category categoryModel) {
        this.success = success;
        this.categoryModel = categoryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Category getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(Category categoryModel) {
        this.categoryModel = categoryModel;
    }
}
