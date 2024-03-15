package com.example.inanyplace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inanyplace.Callback.IRecyclerClickListener;
import com.example.inanyplace.Database.CartDataSource;
import com.example.inanyplace.Database.CartDatabase;
import com.example.inanyplace.Database.CartItem;
import com.example.inanyplace.Database.LocalCartDataSource;
import com.example.inanyplace.Event.CounterCartEvent;
import com.example.inanyplace.Event.FoodItemClickEvent;
import com.example.inanyplace.Model.Food;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {

    private Context context;
    private List<Food> foodModelList;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    public FoodListAdapter(Context context, List<Food> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.imageFood);
        holder.textFoodPrice.setText(new StringBuilder().append(foodModelList.get(position).getPrice()).append(" Lei"));
        holder.textFoodName.setText(new StringBuilder().append(foodModelList.get(position).getName()));

        //Event

        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                Utils.selectedFood = foodModelList.get(pos);
                Utils.selectedFood.setKey(String.valueOf(pos));
                EventBus.getDefault().postSticky(new FoodItemClickEvent(true, foodModelList.get(pos)));
            }
        });

        holder.imageQuickCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem();
                cartItem.setRestaurantId(Utils.currentRestaurant.getUid());
                cartItem.setUid(FirebaseAuth.getInstance().getUid());

                cartItem.setFoodId(foodModelList.get(position).getId());
                cartItem.setFoodName(foodModelList.get(position).getName());
                cartItem.setFoodImage(foodModelList.get(position).getImage());
                cartItem.setFoodPrice(Double.valueOf(String.valueOf(foodModelList.get(position).getPrice())));
                cartItem.setFoodQuantity(1);
                cartItem.setFoodExtraPrice(0.0); //Default not choose size + addon
                cartItem.setFoodAddon("Default");
                cartItem.setFoodSize("Default");

                cartDataSource.getItemWithAllOptionsInCart(FirebaseAuth.getInstance().getUid(),
                        cartItem.getFoodId(),
                        cartItem.getFoodSize(),
                        cartItem.getFoodAddon(),
                        Utils.currentRestaurant.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<CartItem>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull CartItem cartItemFromDB) {
                                if (cartItemFromDB.equals(cartItem)) {
                                    //Already in DB just update
                                    cartItemFromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                                    cartItemFromDB.setFoodAddon(cartItem.getFoodAddon());
                                    cartItemFromDB.setFoodSize(cartItem.getFoodSize());
                                    cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                                    cartDataSource.updateCartItems(cartItemFromDB)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<Integer>() {
                                                @Override
                                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                                    //TODO
                                                }

                                                @Override
                                                public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                                    Toast.makeText(context, "Update cart successfully.", Toast.LENGTH_SHORT).show();
                                                    EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                                }

                                                @Override
                                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                    Toast.makeText(context, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    //Item not available in cart before, insert new
                                    compositeDisposable.add(cartDataSource.insertOrReplace(cartItem)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() -> {
                                                Toast.makeText(context, "Add to cart successfully.", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            }, throwable -> {
                                                Toast.makeText(context, "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }));
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                if (e.getMessage().contains("empty")) {
                                    //Default, if Cart is empty, this code will be fired
                                    compositeDisposable.add(cartDataSource.insertOrReplace(cartItem)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() -> {
                                                Toast.makeText(context, "Add to cart successfully.", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            }, throwable -> {
                                                Toast.makeText(context, "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }));
                                } else
                                    Toast.makeText(context, "[GET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                //Old cart sistem
                /*compositeDisposable.add(cartDataSource.insertOrReplace(cartItem)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Toast.makeText(context, "Add cart successfully.", Toast.LENGTH_SHORT).show();
                            //Here will send notify to OrderFoodActivity to update counter in cart
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                        }, throwable -> {
                            Toast.makeText(context, "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;

        @BindView(R.id.text_food_name)
        TextView textFoodName;

        @BindView(R.id.text_food_price)
        TextView textFoodPrice;

        @BindView(R.id.image_food_image)
        ImageView imageFood;

        @BindView(R.id.image_quick_cart)
        ImageView imageQuickCart;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v, getAdapterPosition());
        }
    }
}
