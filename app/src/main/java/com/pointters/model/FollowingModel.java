package com.pointters.model;

import java.util.List;

/**
 * Created by prashantkumar on 11/11/17.
 */

public class FollowingModel {

    private Followers followTo;
    private List<String> categories;

    public Followers getFollowTo() {
        return followTo;
    }

    public void setFollowTo(Followers followTo) {
        this.followTo = followTo;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
