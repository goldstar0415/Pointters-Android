package com.pointters.model.request;

/**
 * Created by Anil Jha on 8/8/17.
 */

public class UserEmailSignUpRequest {

    private String email;
    private String password;

    public UserEmailSignUpRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
