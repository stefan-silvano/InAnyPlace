package com.example.inanyplace.Activities.Windows.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inanyplace.Adapter.CategoriesAdapter;
import com.example.inanyplace.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;


public class CategoryFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    Unbinder unbinder;

    @BindView(R.id.recyler_menu)
    RecyclerView recyclerMenu;

    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    CategoriesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoryViewModel =
                new ViewModelProvider(this).get(CategoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        categoryViewModel.getMessageError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        categoryViewModel.getCategoryListMutable().observe(getViewLifecycleOwner(), categoryModelList -> {
            dialog.dismiss();
            adapter =new CategoriesAdapter(getContext(),categoryModelList);
            recyclerMenu.setAdapter(adapter);
            recyclerMenu.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initViews() {
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        dialog.show();

        recyclerMenu.setHasFixedSize(true);
        recyclerMenu.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_from_left);
    }

}