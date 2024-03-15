package com.example.inanyplace.Activities.Windows.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inanyplace.Callback.IBestDealCallbackListener;
import com.example.inanyplace.Callback.IPopularCallbackListener;
import com.example.inanyplace.Model.BestDeal;
import com.example.inanyplace.Model.PopularCategory;
import com.example.inanyplace.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel implements IPopularCallbackListener, IBestDealCallbackListener {

    private MutableLiveData<List<PopularCategory>> popularList;
    private MutableLiveData<List<BestDeal>> bestDealList;
    private MutableLiveData<String> messageError;
    private IPopularCallbackListener popularCallbackListener;
    private IBestDealCallbackListener bestDealCallbackListener;

    public MutableLiveData<List<BestDeal>> getBestDealList(String key) {
        if (bestDealList == null) {
            bestDealList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadBestDealList(key);
        }
        return bestDealList;
    }

    private void loadBestDealList(String key) {
        List<BestDeal> tempList = new ArrayList<>();
        DatabaseReference bestDealRef = FirebaseDatabase.getInstance()
                .getReference(Utils.RESTAURANT_REF)
                .child(key)
                .child(Utils.BEST_DEAL_REF);
        bestDealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapShot : snapshot.getChildren()) {
                    BestDeal model = itemSnapShot.getValue(BestDeal.class);
                    tempList.add(model);
                }
                bestDealCallbackListener.onBeastDealLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bestDealCallbackListener.onBeastDealLoadFailed(error.getMessage());
            }
        });
    }

    public MutableLiveData<List<PopularCategory>> getPopularList(String key) {
        if (popularList == null) {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopularList(key);
        }
        return popularList;
    }

    private void loadPopularList(String key) {
        List<PopularCategory> tempList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Utils.RESTAURANT_REF)
                .child(key)
                .child(Utils.POPULAR_CATEGORY_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot itemSnapShot : snapshot.getChildren()) {
                            PopularCategory model = itemSnapShot.getValue(PopularCategory.class);
                            tempList.add(model);
                        }
                        popularCallbackListener.onPopularLoadSuccess(tempList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        popularCallbackListener.onPopularLoadFailed(error.getMessage());
                    }
                });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public HomeViewModel() {
        popularCallbackListener = this;
        bestDealCallbackListener = this;
    }


    @Override
    public void onPopularLoadSuccess(List<PopularCategory> popularCategoryModels) {
        popularList.setValue(popularCategoryModels);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        messageError.setValue(message);
    }

    @Override
    public void onBeastDealLoadSuccess(List<BestDeal> bestDealModels) {
        bestDealList.setValue(bestDealModels);
    }

    @Override
    public void onBeastDealLoadFailed(String message) {
        messageError.setValue(message);
    }
}