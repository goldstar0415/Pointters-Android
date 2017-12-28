package com.pointters.model.request;

/**
 * Created by AnilJha on 8/8/17.
 */

public class UserEmailLoginRequest {

    private String email;
    private String password;

    public UserEmailLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
