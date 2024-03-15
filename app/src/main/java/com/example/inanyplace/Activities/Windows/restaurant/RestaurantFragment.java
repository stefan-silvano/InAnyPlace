package com.example.inanyplace.Activities.Windows.restaurant;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inanyplace.Adapter.RestaurantAdapter;
import com.example.inanyplace.Event.HideFABCartEvent;
import com.example.inanyplace.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RestaurantFragment extends Fragment {
    private RestaurantViewModel restaurantViewModel;
    private Unbinder unbinder;
    private AlertDialog dialog;
    private LayoutAnimationController layoutAnimationController;
    private RestaurantAdapter adapter;

    @BindView(R.id.recyler_restaurant)
    RecyclerView recyclerRestaurant;

    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        restaurantViewModel =
                new ViewModelProvider(this).get(RestaurantViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurant, container, false);
        unbinder = ButterKnife.bind(this, root);
        initView();
        restaurantViewModel.getMessageError().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        restaurantViewModel.getRestaurantListMutable().observe(getViewLifecycleOwner(),restaurantModels -> {
            dialog.dismiss();
            adapter = new RestaurantAdapter(restaurantModels, getContext());
            recyclerRestaurant.setAdapter(adapter);
            recyclerRestaurant.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initView() {
        setHasOptionsMenu(true);
        dialog = new AlertDialog.Builder(getContext()).setCancelable(false)
                .setMessage("Please wait...").create();

        dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_from_left);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerRestaurant.setLayoutManager(linearLayoutManager);
        recyclerRestaurant.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));
        hideFab();
    }

    private void hideFab(){
        new CountDownTimer(1, 1) {
            public void onTick(long millisUntilFinished) {
                //TODO
            }

            public void onFinish() {
                EventBus.getDefault().postSticky(new HideFABCartEvent(true));
            }
        }.start();
    }
}
