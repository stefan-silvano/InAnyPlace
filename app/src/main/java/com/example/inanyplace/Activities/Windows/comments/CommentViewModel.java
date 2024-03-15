package com.example.inanyplace.Activities.Windows.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inanyplace.Model.Comment;

import java.util.List;

public class CommentViewModel extends ViewModel {

    private MutableLiveData<List<Comment>> commentMutableLiveData;

    public CommentViewModel() {
        commentMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<Comment>> getCommentMutableLiveData() {
        return commentMutableLiveData;
    }

    public void setCommentList(List<Comment> commentList) {
        commentMutableLiveData.setValue(commentList);
    }
}
