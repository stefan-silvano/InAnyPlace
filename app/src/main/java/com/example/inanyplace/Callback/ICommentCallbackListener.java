package com.example.inanyplace.Callback;

import com.example.inanyplace.Model.Comment;

import java.util.List;

public interface ICommentCallbackListener {
    void onCommentLoadSuccess(List<Comment> commentModels);
    void onCommentLoadFailed(String message);

}
