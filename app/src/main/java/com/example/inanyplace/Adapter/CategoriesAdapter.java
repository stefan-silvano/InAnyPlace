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
import com.example.inanyplace.Callback.IRecyclerClickListener;
import com.example.inanyplace.Event.CatetgoryClickEvent;
import com.example.inanyplace.Model.Category;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    Context context;
    List<Category> categoryModelList;

    public CategoriesAdapter(Context context, List<Category> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(categoryModelList.get(position).getImage()).into(holder.imageCategory);
        holder.textCateogry.setText(new StringBuilder(categoryModelList.get(position).getName()));

        //Event
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                Utils.categorySelected = categoryModelList.get(pos);
                EventBus.getDefault().postSticky(new CatetgoryClickEvent(true,categoryModelList.get(pos)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (categoryModelList.size() == 1) {
            return 0;
        } else {
            if (categoryModelList.size() % 2 == 0) {
                return 0;
            } else {
                return (position > 1 && position == categoryModelList.size() - 1) ? 1 : 0;
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Unbinder unbinder;
        @BindView(R.id.image_category)
        ImageView imageCategory;

        @BindView(R.id.text_category)
        TextView textCateogry;

        IRecyclerClickListener listener;

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
