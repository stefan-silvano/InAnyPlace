package com.example.inanyplace.Activities.Windows.orders;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inanyplace.Adapter.OrdersAdapter;
import com.example.inanyplace.Callback.ILoadOrderCallbackListener;
import com.example.inanyplace.Model.Order;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class OrdersFragment extends Fragment implements ILoadOrderCallbackListener {

    private OrdersViewModel ordersViewModel;
    private Unbinder unbinder;
    private ILoadOrderCallbackListener listener;
    private Dialog dialog;

    @BindView(R.id.recyler_orders)
    RecyclerView recyclerOrders;

    @BindView(R.id.text_empty_orders)
    TextView textEmptyOrders;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInsanceState) {
        
        ordersViewModel =
                new ViewModelProvider(this).get(OrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        unbinder = ButterKnife.bind(this, root);
        initView(root);
        loadOrdersFromDatabase();

        ordersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(), orderList -> {
            if (orderList == null || orderList.isEmpty()) {
                textEmptyOrders.setVisibility(View.VISIBLE);
                recyclerOrders.setVisibility(View.GONE);
            } else {
                textEmptyOrders.setVisibility(View.GONE);
                recyclerOrders.setVisibility(View.VISIBLE);
                OrdersAdapter adapter = new OrdersAdapter(getContext(), orderList);
                recyclerOrders.setAdapter(adapter);
            }
        });
        return root;
    }

    private void initView(View root) {

        listener = this;
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

        recyclerOrders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerOrders.setLayoutManager(layoutManager);
        recyclerOrders.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
    }

    private void loadOrdersFromDatabase() {
        List<Order> ordersList = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference(Utils.ORDER_REF)
                .orderByChild("userId")
                .equalTo(FirebaseAuth.getInstance().getUid())
                .limitToLast(15)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot orderDataSnapShot : snapshot.getChildren()) {
                            Order orderModel = orderDataSnapShot.getValue(Order.class);
                            orderModel.setOrderNumber(orderDataSnapShot.getKey()); //Set it
                            ordersList.add(orderModel);
                        }
                        Collections.reverse(ordersList);
                        listener.onLoadOrderSuccess(ordersList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        dialog.dismiss();
        ordersViewModel.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}