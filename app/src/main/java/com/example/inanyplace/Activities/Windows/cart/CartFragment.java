package com.example.inanyplace.Activities.Windows.cart;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inanyplace.Adapter.CartAdapter;
import com.example.inanyplace.Callback.ILoadTimeFromFirebaseListener;
import com.example.inanyplace.Database.CartDataSource;
import com.example.inanyplace.Database.CartDatabase;
import com.example.inanyplace.Database.CartItem;
import com.example.inanyplace.Database.LocalCartDataSource;
import com.example.inanyplace.Event.CounterCartEvent;
import com.example.inanyplace.Event.HideFABCartEvent;
import com.example.inanyplace.Event.UpdateItemInCartEvent;
import com.example.inanyplace.Model.Order;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;
import com.example.inanyplace.Utils.MySwiper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener {

    private CartViewModel cartViewModel;
    private Parcelable recyclerViewState;
    private CartDataSource cartDataSource;
    private CartAdapter adapter;
    private Unbinder unbinder;
    private String address;
    private double lat;
    private double lng;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private ILoadTimeFromFirebaseListener listener;

    @BindView(R.id.recyler_cart)
    RecyclerView recyclerCart;

    @BindView(R.id.text_total_price)
    TextView textTotalPrice;

    @BindView(R.id.groupe_place_holder)
    CardView groupePlaceHolder;

    @BindView(R.id.text_empty_cart)
    TextView textEmptyCart;

    @OnClick(R.id.button_place_order)
    void onPlaceOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Place order");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);

        EditText editTextAddress = (EditText) view.findViewById(R.id.edit_text_adress);
        TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.text_input_order);

        RadioButton radioButtonShipToThis = (RadioButton) view.findViewById(R.id.radio_button_ship_this_address);
        RadioButton radioButtonCurrentLocation = (RadioButton) view.findViewById(R.id.radio_button_ship_current_location);
        RadioButton radioButtonPayCash = (RadioButton) view.findViewById(R.id.radio_button_pay_cash);

        radioButtonShipToThis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextAddress.clearFocus();
                    editTextAddress.setText(null);
                }
            }
        });

        radioButtonCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull @NotNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull @NotNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null) {
                                    lat = task.getResult().getLatitude();
                                    lng = task.getResult().getLongitude();

                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(getContext(), Locale.getDefault());

                                    try {
                                        addresses = geocoder.getFromLocation(task.getResult().getLatitude(),
                                                task.getResult().getLongitude(), 1);
                                        address = addresses.get(0).getAddressLine(0);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    editTextAddress.setText(address);
                                }
                                else
                                    radioButtonCurrentLocation.performClick();
                            }
                        }
                    });

                }
            }
        });

        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(getContext(), "Implement place order.", Toast.LENGTH_SHORT).show();
                address = editTextAddress.getText().toString();

                if (radioButtonPayCash.isChecked())
                    paymentCash(editTextAddress.getText().toString());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().postSticky(new HideFABCartEvent(true));
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().postSticky(new HideFABCartEvent(false));
        cartViewModel.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        compositeDisposable.clear();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        super.onStop();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        listener = this;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItems().observe(getViewLifecycleOwner(), new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {
                    recyclerCart.setVisibility(View.GONE);
                    groupePlaceHolder.setVisibility(View.GONE);
                    textEmptyCart.setVisibility(View.VISIBLE);
                } else {
                    recyclerCart.setVisibility(View.VISIBLE);
                    groupePlaceHolder.setVisibility(View.VISIBLE);
                    textEmptyCart.setVisibility(View.GONE);

                    adapter = new CartAdapter(getContext(), cartItems);
                    recyclerCart.setAdapter(adapter);
                }
            }
        });
        unbinder = ButterKnife.bind(this, root);
        initView();
        initLocation();
        //generateOrders();
        return root;
    }

    private void initView() {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        EventBus.getDefault().postSticky(new HideFABCartEvent(true));

        recyclerCart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwiper mySwiperHelper = new MySwiper(getContext(), recyclerCart, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#FF3C30"),
                        pos -> {
                            CartItem cartItem = adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItem(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                            //TODO
                                        }

                                        @Override
                                        public void onSuccess(@NonNull Integer integer) {
                                            adapter.notifyItemChanged(pos);
                                            calculateTotalPrice(); //Update total price at delete item
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            Toast.makeText(getContext(), "Delete item from cart successfully.", Toast.LENGTH_SHORT).show();
                                            calculateTotalPrice();
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }));
            }
        };

        calculateTotalPrice();
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    private void paymentCash(String toString) {
        compositeDisposable.add(cartDataSource.getAllCart(FirebaseAuth.getInstance().getUid(), Utils.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CartItem>>() {
                    @Override
                    public void accept(List<CartItem> cartItems) throws Exception {
                        //Total price
                        cartDataSource.sumPriceInCart(FirebaseAuth.getInstance().getUid(), Utils.currentRestaurant.getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Double>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(@NonNull Double aDouble) {
                                        double totalOrderPrice = aDouble;
                                        Order order = new Order();
                                        order.setUserId(FirebaseAuth.getInstance().getUid());
                                        order.setUserName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                        order.setPhone("Not now!");
                                        order.setShippingAddress(address);

                                        order.setLat(lat);
                                        order.setLng(lng);

                                        order.setCartItemList(cartItems);
                                        order.setTotalOrderPrice(totalOrderPrice);
                                        order.setTransactionId("Cash");

                                        order.setRestaurantName(Utils.currentRestaurant.getName());

                                        //Simulate delivery time
                                        Random rand = new Random();
                                        int randomNum = rand.nextInt((Utils.currentRestaurant.getDeliveryTimeMax() - Utils.currentRestaurant.getDeliveryTimeMin()) + 1) + Utils.currentRestaurant.getDeliveryTimeMin();
                                        order.setDeliveryTime(randomNum);

                                        //Order object to firbase
                                        //writeOrderToFirebase(order);
                                        syncLocalTimeWithGlobalTime(order);
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        if (!e.getMessage().contains("Query returned empty"))
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }));
    }

    private void syncLocalTimeWithGlobalTime(Order order) {
        final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimateServerTimeMs = System.currentTimeMillis() + offset; //missing time between localtime and server
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyy HH:mm");
                //Date resultDate = new Date(estimateServerTimeMs);

                listener.onLoadTimeSuccess(order, estimateServerTimeMs);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                listener.onLoadTimeFailed(error.getMessage());
            }
        });
    }

    private void writeOrderToDatabase(Order order) {
        String orderNumberCreated = Utils.createOrderNumber();
        FirebaseDatabase.getInstance().getReference(Utils.ORDER_REF)
                .child(orderNumberCreated)
                .setValue(order)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                cartDataSource.cleanCart(FirebaseAuth.getInstance().getUid(), Utils.currentRestaurant.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                //TODO
                            }

                            @Override
                            public void onSuccess(@NonNull Integer integer) {
                                //Clean success.
                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                Toast.makeText(getContext(), "Order placed successfully.", Toast.LENGTH_SHORT).show();

                                //Simulate order status
                                new CountDownTimer(10000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        //TODO
                                    }

                                    public void onFinish() {
                                        Toast.makeText(getContext(), "Order shipping.", Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(Utils.ORDER_REF)
                                                .child(orderNumberCreated).child("orderStatus")
                                                .setValue(1);
                                    }
                                }.start();

                                new CountDownTimer(20000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        //TODO
                                    }

                                    public void onFinish() {
                                        Toast.makeText(getContext(), "Order delivered.", Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(Utils.ORDER_REF)
                                                .child(orderNumberCreated).child("orderStatus")
                                                .setValue(2);
                                    }
                                }.start();

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@androidx.annotation.NonNull Menu menu, @androidx.annotation.NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.more_option_cart, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_cart) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Clear Cart")
                    .setMessage("Do you really want to clear cart?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cartDataSource.cleanCart(FirebaseAuth.getInstance().getUid(), Utils.currentRestaurant.getUid())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                            //TODO
                                        }

                                        @Override
                                        public void onSuccess(@NonNull Integer integer) {
                                            Toast.makeText(getContext(), "Clear cart successfully.", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCartEvent event) {
        if (event.getCartItem() != null) {

            //First, save state of Recycler View
            recyclerViewState = recyclerCart.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                            //TODO
                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                            calculateTotalPrice();
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                            recyclerCart.getLayoutManager().onRestoreInstanceState(recyclerViewState); // Fix error refresh

                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(FirebaseAuth.getInstance().getUid(), Utils.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //TODO
                    }

                    @Override
                    public void onSuccess(@NonNull Double aDouble) {
                        textTotalPrice.setText(new StringBuilder("Total price: ")
                                .append(Utils.formatPrice(aDouble)).append(" Lei"));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (!e.getMessage().contains("Query returned empty"))
                            Toast.makeText(getContext(), "[SUM CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeInMs) {
        order.setCreateDate(estimateTimeInMs);
        order.setOrderStatus(0);
        writeOrderToDatabase(order);
    }


    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void generateOrders() {
        for (int i = 0; i < 250; i++) {
            Order order = new Order();
            Random rand = new Random();

            order.setUserId("Not necessary.");
            order.setUserName("Not necessary.");
            order.setPhone("Not necessary.");
            order.setShippingAddress("Not necessary.");

            order.setLat(-1f);
            order.setLng(-1f);

            order.setCartItemList(null);

            //Simulate order amount
            int randomOrderAmount = rand.nextInt((300 - 5) + 1) + 5;
            order.setTotalOrderPrice(randomOrderAmount);

            order.setTransactionId("Cash");
            order.setOrderStatus(2);
            order.setRestaurantName("Not necessary.");

            //Simulate delivery time
            int randomDeliveryTime = rand.nextInt((60 - 10) + 1) + 10;
            order.setDeliveryTime(randomDeliveryTime);

            long offset = Timestamp.valueOf("2021-01-01 00:00:00").getTime();
            long end = Timestamp.valueOf("2021-07-5 12:00:00").getTime();
            long diff = end - offset + 1;
            Timestamp timestamp = new Timestamp(offset + (long) (Math.random() * diff));

            order.setCreateDate(timestamp.getTime());

            String test = Utils.createOrderNumber();
            FirebaseDatabase.getInstance().getReference(Utils.ORDER_REF)
                    .child(test) // Create order number with only digit
                    .setValue(order)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull @NotNull Task<Void> task) {

                }
            });
        }
        Toast.makeText(getContext(), "250 orders generated!", Toast.LENGTH_SHORT).show();
    }
}
