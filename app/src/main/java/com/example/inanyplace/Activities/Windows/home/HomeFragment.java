package com.example.inanyplace.Activities.Windows.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.example.inanyplace.Adapter.BestDealsAdapter;
import com.example.inanyplace.Adapter.PopularCategoriesAdapter;
import com.example.inanyplace.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Unbinder unbinder;

    @BindView(R.id.recyler_pupular)
    RecyclerView recyclerPopular;
    @BindView(R.id.viewerpage)
    LoopingViewPager viewPager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);

        String key = getArguments().getString("restaurant");

        initView();
        homeViewModel.getPopularList(key).observe(getViewLifecycleOwner(), popularCategoryModels -> {
            PopularCategoriesAdapter adapter = new PopularCategoriesAdapter(getContext(), popularCategoryModels);
            recyclerPopular.setAdapter(adapter);
        });
        homeViewModel.getBestDealList(key).observe(getViewLifecycleOwner(), bestDealModels -> {
            BestDealsAdapter adapter = new BestDealsAdapter(getContext(), bestDealModels, true);
            viewPager.setAdapter(adapter);
        });

        return root;
    }

    private void initView() {
        recyclerPopular.setHasFixedSize(true);
        recyclerPopular.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        viewPager.pauseAutoScroll();
        super.onPause();
    }
}