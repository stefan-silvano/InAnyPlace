package com.example.inanyplace.Activities.Windows.comments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inanyplace.Adapter.CommentAdapter;
import com.example.inanyplace.Callback.ICommentCallbackListener;
import com.example.inanyplace.Model.Comment;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class CommentFragment extends BottomSheetDialogFragment implements ICommentCallbackListener {

    private CommentViewModel commentViewModel;
    private Unbinder unbinder;
    private AlertDialog dialog;
    private ICommentCallbackListener listener;
    private static CommentFragment instance;

    @BindView(R.id.recyler_comment)
    RecyclerView recyclerComment;

    public CommentFragment() {
        listener = this;
    }

    public static CommentFragment getInstance() {
        if (instance == null)
            instance = new CommentFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.layout_bottom_sheet_comment, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        initView();
        loadCommentsFromFireBase();
        commentViewModel.getCommentMutableLiveData().observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> commentModels) {
                CommentAdapter adapter = new CommentAdapter(getContext(), commentModels);
                recyclerComment.setAdapter(adapter);
            }
        });
        return itemView;
    }

    private void initView() {
        commentViewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        recyclerComment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        recyclerComment.setLayoutManager(layoutManager);
        recyclerComment.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
    }

    private void loadCommentsFromFireBase() {
        dialog.show();
        List<Comment> commentModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference(Utils.RESTAURANT_REF)
                .child(Utils.currentRestaurant.getUid())
                .child(Utils.COMMNET_REF)
                .child(Utils.selectedFood.getId())
                .orderByChild("commentTimeStamp")
                .limitToFirst(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot commentSnapShot : snapshot.getChildren()) {
                            Comment commentModel = commentSnapShot.getValue(Comment.class);
                            commentModels.add(commentModel);
                        }
                        listener.onCommentLoadSuccess(commentModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onCommentLoadFailed(error.getMessage());
                    }
                });
    }

    @Override
    public void onCommentLoadSuccess(List<Comment> commentModels) {
        dialog.dismiss();
        commentViewModel.setCommentList(commentModels);
    }

    @Override
    public void onCommentLoadFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
