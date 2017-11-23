package com.pointters.model;

import java.util.List;

/**
 * Created by prashantkumar on 11/11/17.
 */

public class FollowersModel {
    private Followers followFrom;
    private List<String> categories;

    public Followers getFollowFrom() {
        return followFrom;
    }

    public void setFollowFrom(Followers followFrom) {
        this.followFrom = followFrom;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
