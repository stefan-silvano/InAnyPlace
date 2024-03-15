package com.example.inanyplace.Activities.Windows.foodlist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inanyplace.Model.Food;
import com.example.inanyplace.Utils.Utils;

import java.util.List;

public class FoodListViewModel extends ViewModel {

    private MutableLiveData<List<Food>> foodListMutable;

    public FoodListViewModel() {
    }

    public MutableLiveData<List<Food>> getFoodListMutable() {
        if (foodListMutable == null)
            foodListMutable = new MutableLiveData<>();
        foodListMutable.setValue(Utils.categorySelected.getFoods());
        return foodListMutable;
    }

}
