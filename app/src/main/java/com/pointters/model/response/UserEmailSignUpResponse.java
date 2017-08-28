package com.pointters.model.response;

/**
 * Created by AnilJha on 8/8/17.
 */

public class UserEmailSignUpResponse {

    private boolean success;
    private String id;
    private String msg;
    private String token;

    public UserEmailSignUpResponse(boolean success, String id, String msg, String token) {
        this.success = success;
        this.id = id;
        this.msg = msg;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
