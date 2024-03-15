package com.example.inanyplace.Activities.Windows.restaurant;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inanyplace.Callback.IRestaurantCallbackListener;
import com.example.inanyplace.Model.Restaurant;
import com.example.inanyplace.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel implements IRestaurantCallbackListener {
    private MutableLiveData<List<Restaurant>> restaurantListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IRestaurantCallbackListener listener;

    public RestaurantViewModel() {
        listener = this;
    }

    public MutableLiveData<List<Restaurant>> getRestaurantListMutable() {
        if (restaurantListMutable == null) {
            restaurantListMutable = new MutableLiveData<>();
            loadRestaurantFromFirebase();
        }
        return restaurantListMutable;
    }

    private void loadRestaurantFromFirebase() {
        List<Restaurant> restaurantModels = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(Utils.RESTAURANT_REF);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot restuarantSnapShot : snapshot.getChildren()) {
                        Restaurant restaurantModel = restuarantSnapShot.getValue(Restaurant.class);
                        restaurantModel.setUid(restuarantSnapShot.getKey());
                        restaurantModels.add(restaurantModel);
                    }
                    if (restaurantModels.size() > 0) {
                        listener.onRestaurantLoadSuccess(restaurantModels);
                    } else {
                        listener.onRestaurantLoadFailed("Resturant list empty.");
                    }
                } else
                    listener.onRestaurantLoadFailed("Restaurant list doesen't exists.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onRestaurantLoadSuccess(List<Restaurant> restaurantModelList) {
        restaurantListMutable.setValue(restaurantModelList);
    }

    @Override
    public void onRestaurantLoadFailed(String message) {
        messageError.setValue(message);
    }
}
