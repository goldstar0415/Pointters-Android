package com.pointters.model.response;

/**
 * Created by AnilJha on 8/8/17.
 */

public class UserFacebookLoginResponse {

    private String token;
    private String id;



    public UserFacebookLoginResponse(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
