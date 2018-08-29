package com.pointters.model.response;

import com.pointters.model.UserModel;

/**
 * Created by AnilJha on 8/8/17.
 */

public class GetUserResponse {

    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
