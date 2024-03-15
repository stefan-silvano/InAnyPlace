package com.example.inanyplace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.example.inanyplace.Event.BestDealItemClickEvent;
import com.example.inanyplace.Model.BestDeal;
import com.example.inanyplace.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BestDealsAdapter extends LoopingPagerAdapter<BestDeal> {

    @BindView(R.id.image_best_deal)
    ImageView imageBestDeal;
    @BindView(R.id.text_best_deal)
    TextView textBestDeal;

    Unbinder unbinder;

    public BestDealsAdapter(@NotNull Context context, @NotNull List<? extends BestDeal> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected void bindView(@NotNull View view, int i, int i1) {
        unbinder = ButterKnife.bind(this, view);
        //Set data
        Glide.with(view).load(getItemList().get(i).getImage()).into(imageBestDeal);
        textBestDeal.setText(getItemList().get(i).getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new BestDealItemClickEvent(getItemList().get(i)));
            }
        });
    }

    @NotNull
    @Override
    protected View inflateView(int i, @NotNull ViewGroup viewGroup, int i1) {
        return LayoutInflater.from(getContext()).inflate(R.layout.layout_best_deal_item, viewGroup, false);
    }
}
