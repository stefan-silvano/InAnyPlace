package com.example.inanyplace.Activities.Windows.detailfood;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inanyplace.Model.Comment;
import com.example.inanyplace.Model.Food;
import com.example.inanyplace.Utils.Utils;

public class DetailFoodViewModel extends ViewModel {

    private MutableLiveData<Food> foodModelMutableLiveData;
    private MutableLiveData<Comment> commentModelMutableLiveData;

    public void setCommentModel(Comment commentModel) {
        if (commentModelMutableLiveData != null)
            commentModelMutableLiveData.setValue(commentModel);

    }

    public MutableLiveData<Comment> getCommentModelMutableLiveData() {
        return commentModelMutableLiveData;
    }

    public DetailFoodViewModel() {
        commentModelMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Food> getFoodModelMutableLiveData() {
        if (foodModelMutableLiveData == null)
            foodModelMutableLiveData = new MutableLiveData<>();
        foodModelMutableLiveData.setValue(Utils.selectedFood);
        return foodModelMutableLiveData;
    }

    public void setFoodModel(Food foodModel) {
        if (foodModelMutableLiveData != null)
            foodModelMutableLiveData.setValue(foodModel);
    }
}
