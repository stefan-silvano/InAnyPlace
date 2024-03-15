package com.example.inanyplace.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.inanyplace.Database.CartDataSource;
import com.example.inanyplace.Database.CartDatabase;
import com.example.inanyplace.Database.LocalCartDataSource;
import com.example.inanyplace.Event.BestDealItemClickEvent;
import com.example.inanyplace.Event.CatetgoryClickEvent;
import com.example.inanyplace.Event.CounterCartEvent;
import com.example.inanyplace.Event.FoodItemClickEvent;
import com.example.inanyplace.Event.HideFABCartEvent;
import com.example.inanyplace.Event.MenuItemClickEvent;
import com.example.inanyplace.Event.PopularCategoryClickEvent;
import com.example.inanyplace.Model.Category;
import com.example.inanyplace.Model.Food;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavController navController;
    private TextView userName;
    private View headerView;
    private FirebaseAuth firebaseAuth;
    private CartDataSource cartDataSource;
    private AlertDialog dialog;
    private NavigationView navigationView;

    @BindView(R.id.fab)
    CounterFab fab;

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.currentRestaurant != null)
            counterCartItem();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        navController.navigate(R.id.nav_restaurant);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        ButterKnife.bind(this);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_cart);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_restaurant, R.id.nav_home, R.id.nav_category, R.id.nav_food_list,
                R.id.nav_detail_food, R.id.nav_cart, R.id.nav_orders, R.id.nav_statistics)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        updateNavigationHeaderUser();
        EventBus.getDefault().postSticky(new HideFABCartEvent(true));
        navigateToRestaurantsScreen();
    }


    private void navigateToRestaurantsScreen() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.main_drawer);
        navigationView.getMenu().getItem(0).setChecked(true);
        navController.navigate(R.id.nav_restaurant);
        EventBus.getDefault().postSticky(new HideFABCartEvent(true));
    }

    private void updateNavigationHeaderUser() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        userName = headerView.findViewById(R.id.menu_user_name_textview);
        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setCheckable(true);
        drawer.closeDrawers();
        switch (item.getItemId()) {

            case (R.id.nav_restaurant):
                navigateToRestaurantsScreen();
                break;

            case (R.id.nav_home):
                Bundle bundle = new Bundle();
                bundle.putString("restaurant", Utils.currentRestaurant.getUid());
                navController.navigate(R.id.nav_home, bundle);
                break;

            case (R.id.nav_category):
                navController.navigate(R.id.nav_category);
                break;

            case (R.id.nav_cart):
                navController.navigate(R.id.nav_cart);
                break;

            case (R.id.nav_orders):
                navController.navigate(R.id.nav_orders);
                break;

            case (R.id.nav_statistics):
                navController.navigate(R.id.nav_statistics);
                break;

            case (R.id.nav_sing_out):
                singOut();
                break;
        }
        return false;
    }

    //Event bus
    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CatetgoryClickEvent event) {
        if (event.isSuccess()) {
            //Toast.makeText(this, "Click to " + event.getCategoryModel().getName(), Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_food_list);
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onFoodItemClick(FoodItemClickEvent event) {
        if (event.isSuccess()) {
            //Toast.makeText(this, "Click to " + event.getFoodModel().getName(), Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_detail_food);
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onCartCounter(CounterCartEvent event) {
        if (event.isSuccess()) {
            counterCartItem();
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onHideFABEvent(HideFABCartEvent event) {
        if (event.isHidden())
            fab.hide();
        else
            fab.show();
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onRestaurantClick(MenuItemClickEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString("restaurant", event.getRestaurantModel().getUid());
        navController.navigate(R.id.nav_home, bundle);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.restaurant_drawer);
        EventBus.getDefault().postSticky(new HideFABCartEvent(false));
        counterCartItem();
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onBestDealItemClick(BestDealItemClickEvent event) {
        if (event.getBestDealModel() != null) {

            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(Utils.RESTAURANT_REF)
                    .child(Utils.currentRestaurant.getUid())
                    .child(Utils.CATEGORY_REF)
                    .child(event.getBestDealModel().getMenuId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Utils.categorySelected = snapshot.getValue(Category.class);
                                Utils.categorySelected.setMenuId(snapshot.getKey());

                                //Load food
                                FirebaseDatabase.getInstance()
                                        .getReference(Utils.RESTAURANT_REF)
                                        .child(Utils.currentRestaurant.getUid())
                                        .child(Utils.CATEGORY_REF)
                                        .child(event.getBestDealModel().getMenuId())
                                        .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.getBestDealModel().getFoodId())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot itemDataSnapShot : snapshot.getChildren()) {
                                                        Utils.selectedFood = itemDataSnapShot.getValue(Food.class);
                                                        Utils.selectedFood.setKey(itemDataSnapShot.getKey());

                                                    }
                                                    navController.navigate(R.id.nav_detail_food);
                                                } else {

                                                    Toast.makeText(MainActivity.this, "Item not found.", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Item not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onPopularItemClick(PopularCategoryClickEvent event) {
        if (event.getPopularCategoryModel() != null) {

            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(Utils.RESTAURANT_REF)
                    .child(Utils.currentRestaurant.getUid())
                    .child(Utils.CATEGORY_REF)
                    .child(event.getPopularCategoryModel().getMenuId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Utils.categorySelected = snapshot.getValue(Category.class);
                                Utils.categorySelected.setMenuId(snapshot.getKey());

                                //Load food
                                FirebaseDatabase.getInstance()
                                        .getReference(Utils.RESTAURANT_REF)
                                        .child(Utils.currentRestaurant.getUid())
                                        .child(Utils.CATEGORY_REF)
                                        .child(event.getPopularCategoryModel().getMenuId())
                                        .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.getPopularCategoryModel().getFoodId())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot itemDataSnapShot : snapshot.getChildren()) {
                                                        Utils.selectedFood = itemDataSnapShot.getValue(Food.class);
                                                        Utils.selectedFood.setKey(itemDataSnapShot.getKey());
                                                    }
                                                    navController.navigate(R.id.nav_detail_food);

                                                } else {

                                                    Toast.makeText(MainActivity.this, "Item not found.", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Item not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void counterCartItem() {
        cartDataSource.countItemInCart(FirebaseAuth.getInstance().getUid(), Utils.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        //TODO
                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                        fab.setCount(integer);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (e.getMessage().contains("Query returned empty"))
                            fab.setCount(0);
                        else
                            Toast.makeText(MainActivity.this, "[COUNTER CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void singOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sing Out")
                .setMessage("Do you really want to sing out?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.selectedFood = null;
                        Utils.categorySelected = null;
                        firebaseAuth.signOut();
                        LoginManager.getInstance().logOut();
                        GoogleSignIn.getClient(getApplicationContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();
                        Toast.makeText(getApplicationContext(), "Logout from account.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}