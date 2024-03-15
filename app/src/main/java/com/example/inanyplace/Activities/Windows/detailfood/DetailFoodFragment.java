package com.example.inanyplace.Activities.Windows.detailfood;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.inanyplace.Activities.Windows.comments.CommentFragment;
import com.example.inanyplace.Database.CartDataSource;
import com.example.inanyplace.Database.CartDatabase;
import com.example.inanyplace.Database.CartItem;
import com.example.inanyplace.Database.LocalCartDataSource;
import com.example.inanyplace.Event.CounterCartEvent;
import com.example.inanyplace.Model.Addon;
import com.example.inanyplace.Model.Comment;
import com.example.inanyplace.Model.Food;
import com.example.inanyplace.Model.Size;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailFoodFragment extends Fragment implements TextWatcher {

    private DetailFoodViewModel detailFoodViewModel;
    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Unbinder unbinder;
    private android.app.AlertDialog waitingDialog;
    private BottomSheetDialog addonBottomSheetDialog;

    public static DetailFoodFragment newInstance() {
        return new DetailFoodFragment();
    }

    //View need inflate
    ChipGroup chipGroupAddon;
    EditText editTextSearch;

    @BindView(R.id.image_food)
    ImageView imageFood;

    @BindView(R.id.button_cart)
    CounterFab buttonCart;

    @BindView(R.id.button_rating)
    FloatingActionButton buttonRating;

    @BindView(R.id.food_name)
    TextView foodName;

    @BindView(R.id.food_description)
    TextView foodDesctription;

    @BindView(R.id.food_price)
    TextView foodPrice;

    @BindView(R.id.button_number)
    ElegantNumberButton buttonNumber;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.button_show_comment)
    Button buttonShowComent;

    @BindView(R.id.radio_group_size)
    RadioGroup radioGroupSize;

    @BindView(R.id.image_add_addon)
    ImageView imageAddon;

    @BindView(R.id.chip_group_user_selected_addon)
    ChipGroup chipGroupUserSelectedAddon;

    @OnClick(R.id.button_cart)
    void onCartItemAdd() {
        CartItem cartItem = new CartItem();
        cartItem.setUid(FirebaseAuth.getInstance().getUid());
        cartItem.setRestaurantId(Utils.currentRestaurant.getUid());

        cartItem.setFoodId(Utils.selectedFood.getId());
        cartItem.setFoodName(Utils.selectedFood.getName());
        cartItem.setFoodImage(Utils.selectedFood.getImage());
        cartItem.setFoodPrice(Double.valueOf(String.valueOf(Utils.selectedFood.getPrice())));
        cartItem.setFoodQuantity(Integer.valueOf(buttonNumber.getNumber()));
        cartItem.setFoodExtraPrice(Utils.calculatedExtraPrice(Utils.selectedFood.getUserSelectedSize(), Utils.selectedFood.getUserSelectedAddon()));
        if (Utils.selectedFood.getUserSelectedAddon() != null)
            cartItem.setFoodAddon(new Gson().toJson(Utils.selectedFood.getUserSelectedAddon()));
        else
            cartItem.setFoodAddon("Default");
        if (Utils.selectedFood.getUserSelectedSize() != null)
            cartItem.setFoodSize(new Gson().toJson(Utils.selectedFood.getUserSelectedSize()));
        else
            cartItem.setFoodSize("Default");

        cartDataSource.getItemWithAllOptionsInCart(FirebaseAuth.getInstance().getUid(),
                cartItem.getFoodId(),
                cartItem.getFoodSize(),
                cartItem.getFoodAddon(), Utils.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CartItem>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        //TODO
                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull CartItem cartItemFromDB) {
                        if (cartItemFromDB.equals(cartItem)) {
                            //Already in DB just update
                            cartItemFromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                            cartItemFromDB.setFoodAddon(cartItem.getFoodAddon());
                            cartItemFromDB.setFoodSize(cartItem.getFoodSize());
                            cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                            cartDataSource.updateCartItems(cartItemFromDB)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                                            //TODO
                                        }

                                        @Override
                                        public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                            Toast.makeText(getContext(), "Update cart successfully.", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            calculateTotalPrice();
                                        }

                                        @Override
                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                            Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            //Item not available in cart before, insert new
                            compositeDisposable.add(cartDataSource.insertOrReplace(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to cart successfully.", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        calculateTotalPrice();
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (e.getMessage().contains("empty")) {
                            //Default, if Cart is empty, this code will be fired
                            compositeDisposable.add(cartDataSource.insertOrReplace(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to cart successfully.", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        } else
                            Toast.makeText(getContext(), "[GET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.button_rating)
    void onRatingButtonClick() {
        showDialogRating();
    }

    @OnClick(R.id.button_show_comment)
    void onShowCommentClick() {

        CommentFragment commentFragment = CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(), "CommentFragment");
    }

    @OnClick(R.id.image_add_addon)
    void onAddonClick() {
        if (Utils.selectedFood.getAddon() != null) {
            displayAddonList(); //Show all addon options
            addonBottomSheetDialog.show();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        detailFoodViewModel =
                new ViewModelProvider(this).get(DetailFoodViewModel.class);
        View root = inflater.inflate(R.layout.fragment_detail_food, container, false);
        unbinder = ButterKnife.bind(this, root);
        initView();
        detailFoodViewModel.getFoodModelMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Food>() {
            @Override
            public void onChanged(Food foodModel) {
                if (foodModel != null)
                    displayInfo(foodModel);
            }
        });
        detailFoodViewModel.getCommentModelMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Comment>() {
            @Override
            public void onChanged(Comment commentModel) {
                submitRatingToDatabase(commentModel);
            }
        });

        buttonNumber.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                calculateTotalPrice();
            }
        });
        return root;
    }

    private void initView() {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        waitingDialog = new SpotsDialog.Builder().
                setCancelable(false)
                .setContext(getContext())
                .build();

        addonBottomSheetDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);
        View layoutAddonDisplay = getLayoutInflater().inflate(R.layout.layout_addon_display, null);
        chipGroupAddon = (ChipGroup) layoutAddonDisplay.findViewById(R.id.chip_group_addon);
        editTextSearch = (EditText) layoutAddonDisplay.findViewById(R.id.edit_text_search);
        addonBottomSheetDialog.setContentView(layoutAddonDisplay);

        addonBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                displayUserSelectedAddon();
                calculateTotalPrice();
            }
        });
    }

    private void displayAddonList() {
        if (Utils.selectedFood.getAddon().size() > 0) {
            chipGroupAddon.clearCheck(); //clear check all
            chipGroupAddon.removeAllViews();

            editTextSearch.addTextChangedListener(this);

            //Add all view
            for (Addon addonModel : Utils.selectedFood.getAddon()) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_addon_item, null);
                chip.setText(new StringBuilder(addonModel.getName())
                        .append("(+")
                        .append(addonModel.getPrice())
                        .append("Lei)"));
                chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (Utils.selectedFood.getUserSelectedAddon() == null)
                                Utils.selectedFood.setUserSelectedAddon(new ArrayList<>());
                            Utils.selectedFood.getUserSelectedAddon().add(addonModel);
                        }
                    }
                });
                chipGroupAddon.addView(chip);
            }
        }
    }

    private void showDialogRating() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rating Food");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating_food, null);

        RatingBar ratingBar = itemView.findViewById(R.id.rating_bar_select);
        EditText editComment = itemView.findViewById(R.id.edit_text_comment);

        builder.setView(itemView);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Comment commentModel = new Comment();
                commentModel.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                commentModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                commentModel.setComment(editComment.getText().toString());
                commentModel.setRatingValue(ratingBar.getRating());
                Map<String, Object> serverTimeStamp = new HashMap<>();
                serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
                commentModel.setCommentTimeStamp(serverTimeStamp);

                detailFoodViewModel.setCommentModel(commentModel);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayUserSelectedAddon() {
        if (Utils.selectedFood.getUserSelectedAddon() != null && Utils.selectedFood.getUserSelectedAddon().size() > 0) {
            chipGroupUserSelectedAddon.removeAllViews(); //Clear all view already add
            for (Addon addonModel : Utils.selectedFood.getUserSelectedAddon()) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon, null);
                chip.setText(new StringBuilder(addonModel.getName())
                        .append("(+")
                        .append(addonModel.getPrice())
                        .append("Lei)"));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Remove when user select delete
                        chipGroupUserSelectedAddon.removeView(v);
                        Utils.selectedFood.getUserSelectedAddon().remove(addonModel);
                        calculateTotalPrice();
                    }
                });
                chipGroupUserSelectedAddon.addView(chip);
            }
        } else if (Utils.selectedFood.getUserSelectedAddon() != null && Utils.selectedFood.getUserSelectedAddon().size() == 0) {
            chipGroupUserSelectedAddon.removeAllViews();
        }
    }

    private void submitRatingToDatabase(Comment commentModel) {
        waitingDialog.show();
        FirebaseDatabase.getInstance().getReference(Utils.RESTAURANT_REF)
                .child(Utils.currentRestaurant.getUid())
                .child(Utils.COMMNET_REF)
                .child(Utils.selectedFood.getId())
                .push()
                .setValue(commentModel).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Update value avg in Food
                            addRaitingToFood(commentModel.getRatingValue());
                        }
                        waitingDialog.dismiss();
                    }
                });
    }

    private void addRaitingToFood(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference(Utils.RESTAURANT_REF)
                .child(Utils.currentRestaurant.getUid())
                .child(Utils.CATEGORY_REF)
                .child(Utils.categorySelected.getMenuId())
                .child("foods")
                .child(Utils.selectedFood.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Food foodModel = snapshot.getValue(Food.class);
                            foodModel.setKey(Utils.selectedFood.getKey());

                            //Apply rating
                            double sumRating = foodModel.getRatingValue() + ratingValue;
                            long ratingCount = foodModel.getRatingCount() + 1;

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("ratingValue", sumRating);
                            updateData.put("ratingCount", ratingCount);

                            //Update rating data
                            foodModel.setRatingValue(sumRating);
                            foodModel.setRatingCount(ratingCount);

                            snapshot.getRef()
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                waitingDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Thank you for response!", Toast.LENGTH_SHORT).show();
                                                    Utils.selectedFood = foodModel;
                                                    detailFoodViewModel.setFoodModel(foodModel);

                                                }
                                            }
                                        }
                                    });

                        } else
                            waitingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        waitingDialog.dismiss();
                        Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayInfo(Food foodModel) {

        Glide.with(getContext()).load(foodModel.getImage()).into(imageFood);
        foodName.setText(new StringBuilder(foodModel.getName()));
        foodDesctription.setText(new StringBuilder(foodModel.getDescription()));
        foodPrice.setText(new StringBuilder(foodModel.getPrice().toString()).append(" Lei"));

        ratingBar.setRating((float) foodModel.getRatingValue() / foodModel.getRatingCount());

        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setTitle(Utils.selectedFood.getName());

        //Size
        if (radioGroupSize.getChildCount() == 0)
            for (Size sizeModel : Utils.selectedFood.getSize()) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Utils.selectedFood.setUserSelectedSize(sizeModel);

                        }
                        calculateTotalPrice(); //Update price
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                radioButton.setLayoutParams(params);
                radioButton.setText(sizeModel.getName());
                radioButton.setTag(sizeModel.getPrice());
                radioGroupSize.addView(radioButton);
            }

        if (radioGroupSize.getChildCount() > 0) {
            RadioButton radioButton = (RadioButton) radioGroupSize.getChildAt(0);
            radioButton.setChecked(true); //Default first select
        }

        calculateTotalPrice();
    }


    private void calculateTotalPrice() {
        double totalPrice = Double.parseDouble(Utils.selectedFood.getPrice().toString());
        double displayPrice = 0.0;

        //Addon
        if (Utils.selectedFood.getUserSelectedAddon() != null && Utils.selectedFood.getUserSelectedAddon().size() > 0) {
            for (Addon addonModel : Utils.selectedFood.getUserSelectedAddon()) {
                totalPrice += Double.parseDouble(addonModel.getPrice().toString());
            }
        }

        //Size
        if (Utils.selectedFood.getUserSelectedSize() != null)
            totalPrice += Double.parseDouble(Utils.selectedFood.getUserSelectedSize().getPrice().toString());

        displayPrice = totalPrice * (Integer.parseInt(buttonNumber.getNumber()));
        displayPrice = Math.round(displayPrice * 100 / 100);

        foodPrice.setText(new StringBuilder("").append(Utils.formatPrice(displayPrice)).toString() + " Lei");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //TODO
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        chipGroupAddon.clearCheck();
        chipGroupAddon.removeAllViews();

        for (Addon addonModel : Utils.selectedFood.getAddon()) {
            if (addonModel.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_addon_item, null);
                chip.setText(new StringBuilder(addonModel.getName())
                        .append("(+")
                        .append(addonModel.getPrice())
                        .append("Lei)"));
                chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (Utils.selectedFood.getUserSelectedAddon() == null)
                                Utils.selectedFood.setUserSelectedAddon(new ArrayList<>());
                            Utils.selectedFood.getUserSelectedAddon().add(addonModel);
                        }
                    }
                });
                chipGroupAddon.addView(chip);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //TODO
    }


    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
