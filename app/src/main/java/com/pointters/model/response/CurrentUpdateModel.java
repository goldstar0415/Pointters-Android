package com.pointters.model.response;

import com.pointters.model.CommentModel;
import com.pointters.model.PostData;
import com.pointters.model.Service;
import com.pointters.model.UserProfileModel;

import java.util.ArrayList;

public class CurrentUpdateModel {

    private UserProfileModel user;
    private boolean liked;
    private Service service;
    private PostData post;
    private ArrayList<CommentModel> comments;

    public UserProfileModel getUser() {
        return user;
    }

    public void setUser(UserProfileModel user) {
        this.user = user;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public PostData getPost() {
        return post;
    }

    public void setPost(PostData post) {
        this.post = post;
    }

    public ArrayList<CommentModel> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
    }
}
