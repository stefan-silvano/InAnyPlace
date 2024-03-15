package com.example.inanyplace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.inanyplace.Database.CartItem;
import com.example.inanyplace.Event.UpdateItemInCartEvent;
import com.example.inanyplace.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodImage())
                .into(holder.imageCart);
        holder.textFoodNameCart.setText(new StringBuilder(cartItemList.get(position).getFoodName()));
        holder.textFoodPriceCart.setText(new StringBuilder("")
                .append(cartItemList.get(position).getFoodPrice() + cartItemList.get(position).getFoodExtraPrice()));
        holder.buttonNumberCart.setNumber(String.valueOf(cartItemList.get(position).getFoodQuantity()));

        //Event
        holder.buttonNumberCart.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                //When user click this button, we will update database
                cartItemList.get(position).setFoodQuantity(newValue);
                EventBus.getDefault().postSticky(new UpdateItemInCartEvent(cartItemList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Unbinder unbinder;

        @BindView(R.id.image_cart)
        ImageView imageCart;

        @BindView(R.id.text_food_price_cart)
        TextView textFoodPriceCart;

        @BindView(R.id.text_food_name_cart)
        TextView textFoodNameCart;

        @BindView(R.id.button_number_cart)
        ElegantNumberButton buttonNumberCart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }


    }
}
