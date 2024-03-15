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
import com.example.inanyplace.Model.Order;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private Context context;
    private List<Order> ordersList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public OrdersAdapter(Context context, List<Order> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(ordersList.get(position).getCartItemList().get(0).getFoodImage())
                .into(holder.imageOrder); //default image in cart
        calendar.setTimeInMillis(ordersList.get(position).getCreateDate());
        Date date = new Date(ordersList.get(position).getCreateDate());
        holder.textOrderDate.setText(new StringBuilder(Utils.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
                .append(", ")
                .append(simpleDateFormat.format(date)));
        holder.textOrderNumber.setText(new StringBuilder("Order number: ").append(ordersList.get(position).getOrderNumber()));
        holder.textOrderStatus.setText(new StringBuilder().append(Utils.convertStatusToText(ordersList.get(position).getOrderStatus())));
        holder.textOrderStatus.setTextColor(Utils.checkStatus(ordersList.get(position).getOrderStatus()));
        holder.textOrderRestaurant.setText(new StringBuilder().append(ordersList.get(position).getRestaurantName()));
        holder.textOrderPrice.setText(new StringBuilder("Order price: ").append(ordersList.get(position).getTotalOrderPrice()).append(" Lei"));

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_order_date)
        TextView textOrderDate;

        @BindView(R.id.text_order_number)
        TextView textOrderNumber;

        @BindView(R.id.text_order_status)
        TextView textOrderStatus;

        @BindView(R.id.text_order_restaurant)
        TextView textOrderRestaurant;

        @BindView(R.id.text_order_price)
        TextView textOrderPrice;

        @BindView(R.id.image_order)
        ImageView imageOrder;

        private Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
