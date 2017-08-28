package com.pointters.model.response;

/**
 * Created by AnilJha on 8/8/17.
 */

public class UserEmailLoginResponse {

    private boolean success;
    private String token;

    public UserEmailLoginResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
