package com.pointters.model.response;

import com.pointters.model.FollowersAndFollowing;

import java.util.List;

/**
 * Created by prashantkumar on 9/11/17.
 */

public class BaseResponse {
    private boolean success = false;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
