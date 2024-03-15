package com.example.inanyplace.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inanyplace.Callback.IRecyclerClickListener;
import com.example.inanyplace.Event.MenuItemClickEvent;
import com.example.inanyplace.Model.Restaurant;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {

    private List<Restaurant> restaurantModelList;
    private Context context;

    public RestaurantAdapter(List<Restaurant> restaurantModelList, Context context) {
        this.restaurantModelList = restaurantModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(restaurantModelList.get(position).getImageUrl())
                .into(holder.imageRestaurant);
        holder.restaurantName.setText(new StringBuilder(restaurantModelList.get(position).getName()));
        holder.restaurantAddress.setText(new StringBuilder(restaurantModelList.get(position).getAddress()));
        holder.imageRestaurantInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(restaurantModelList.get(position).getName())
                        .setMessage("Phone: " + restaurantModelList.get(position).getPhone() + "\n"
                                + "Delivery time: " + restaurantModelList.get(position).getDeliveryTimeMin()
                                + " - " + restaurantModelList.get(position).getDeliveryTimeMax() + " min.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Event
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                Utils.currentRestaurant = restaurantModelList.get(pos);
                EventBus.getDefault().postSticky(new MenuItemClickEvent(true, restaurantModelList.get(pos)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_restaurant_name)
        TextView restaurantName;

        @BindView(R.id.text_restaurant_address)
        TextView restaurantAddress;

        @BindView(R.id.image_restaurant)
        ImageView imageRestaurant;

        @BindView(R.id.image_restaurant_info)
        ImageView imageRestaurantInfo;

        private IRecyclerClickListener listener;
        private Unbinder unbinder;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v, getAdapterPosition());
        }
    }
}
