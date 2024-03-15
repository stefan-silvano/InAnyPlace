package com.example.inanyplace.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inanyplace.Model.Comment;
import com.example.inanyplace.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context context;
    private List<Comment> commentModelList;

    public CommentAdapter(Context context, List<Comment> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Long timeStamp = Long.valueOf(commentModelList.get(position).getCommentTimeStamp().get("timeStamp").toString());
        holder.commentDate.setText(DateUtils.getRelativeTimeSpanString(timeStamp));
        holder.commentReview.setText(commentModelList.get(position).getComment());
        holder.commentName.setText(commentModelList.get(position).getName());
        holder.ratingBarComment.setRating(commentModelList.get(position).getRatingValue());
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;

        @BindView(R.id.text_comment_name)
        TextView commentName;

        @BindView(R.id.text_comment_date)
        TextView commentDate;

        @BindView(R.id.text_comment_review)
        TextView commentReview;

        @BindView(R.id.rating_bar_comment)
        RatingBar ratingBarComment;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
