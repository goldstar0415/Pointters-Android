package com.pointters.model.response;

/**
 * Created by AnilJha on 8/8/17.
 */

public class UserFacebookLoginResponse {

    private String token;

    public UserFacebookLoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
