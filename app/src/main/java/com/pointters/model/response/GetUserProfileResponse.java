package com.pointters.model.response;

import com.pointters.model.UserProfileModel;

/**
 * Created by mac on 1/26/18.
 */

public class GetUserProfileResponse {

    private UserProfileModel result;

    public UserProfileModel getResult() {
        return result;
    }

    public void setResult(UserProfileModel result) {
        this.result = result;
    }
}
