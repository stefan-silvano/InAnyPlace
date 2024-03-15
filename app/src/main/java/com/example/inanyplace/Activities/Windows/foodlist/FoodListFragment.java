package com.example.inanyplace.Activities.Windows.foodlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inanyplace.Adapter.FoodListAdapter;
import com.example.inanyplace.Model.Food;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoodListFragment extends Fragment {

    private FoodListViewModel foodListViewModel;

    private Unbinder unbinder;
    private LayoutAnimationController layoutAnimationController;
    private FoodListAdapter adapter;

    public static FoodListFragment newInstance() {
        return new FoodListFragment();
    }

    @BindView(R.id.recyler_food_list)
    RecyclerView recyclerFoodList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInsanceState) {
        foodListViewModel =
                new ViewModelProvider(this).get(FoodListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        initView();
        foodListViewModel.getFoodListMutable().observe(getViewLifecycleOwner(), new Observer<List<Food>>() {
            @Override
            public void onChanged(List<Food> foodModels) {
                adapter = new FoodListAdapter(getContext(), foodModels);
                recyclerFoodList.setAdapter(adapter);
                recyclerFoodList.setLayoutAnimation(layoutAnimationController);
            }
        });
        return root;
    }

    private void initView() {

        if (Utils.categorySelected != null)
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setTitle(Utils.categorySelected.getName());

        recyclerFoodList.setHasFixedSize(true);
        recyclerFoodList.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_from_left);
    }

}
