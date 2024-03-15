package com.example.inanyplace.Activities.Windows.category;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inanyplace.Callback.ICategoryCallbackListener;
import com.example.inanyplace.Model.Category;
import com.example.inanyplace.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel implements ICategoryCallbackListener {

    private MutableLiveData<List<Category>> categoryListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private ICategoryCallbackListener categoryCallbackListener;

    public MutableLiveData<List<Category>> getCategoryListMutable() {
        if(categoryListMutable == null){
            categoryListMutable = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadCategories();
        }
        return categoryListMutable;
    }

    private void loadCategories() {
        List<Category> tempList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance()
                .getReference(Utils.RESTAURANT_REF)
                .child(Utils.currentRestaurant.getUid())
                .child(Utils.CATEGORY_REF);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapShot : snapshot.getChildren()){
                    Category categoryModel = itemSnapShot.getValue(Category.class);
                    categoryModel.setMenuId(itemSnapShot.getKey());
                    tempList.add(categoryModel);
                }
                categoryCallbackListener.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                categoryCallbackListener.onPopularLoadFailed(error.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public ICategoryCallbackListener getCategoryCallbackListener() {
        return categoryCallbackListener;
    }

    public CategoryViewModel() {
        categoryCallbackListener = this;
    }

    @Override
    public void onPopularLoadSuccess(List<Category> categoryModelList) {
        categoryListMutable.setValue(categoryModelList);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        messageError.setValue(message);
    }
}