package com.example.inanyplace.Activities.Windows.cart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inanyplace.Database.CartDataSource;
import com.example.inanyplace.Database.CartDatabase;
import com.example.inanyplace.Database.CartItem;
import com.example.inanyplace.Database.LocalCartDataSource;
import com.example.inanyplace.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItems;
    private CartDataSource cartDataSource;


    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initCartDataSource(Context context) {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItems() {
        if (mutableLiveDataCartItems == null)
            mutableLiveDataCartItems = new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataCartItems;
    }

    private void getAllCartItems() {
        compositeDisposable.add(cartDataSource.getAllCart(FirebaseAuth.getInstance().getUid(), Utils.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CartItem>>() {
                    @Override
                    public void accept(List<CartItem> cartItems) throws Exception {
                        mutableLiveDataCartItems.setValue(cartItems);
                    }
                }, throwable -> {
                    mutableLiveDataCartItems.setValue(null);
                }));
    }
}
