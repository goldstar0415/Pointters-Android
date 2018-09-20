package com.pointters.model;

import com.pointters.model.request.LocationRequestModel;

import java.io.Serializable;

/**
 * Created by prashantkumar on 8/9/17.
 */

public class ContactModel implements Serializable{

    private String userId;
    private String firstName;
    private String lastName;
    private String profilePic;
    private boolean verified;

    public ContactModel(String userId, String firstName, String lastName, String profilePic, boolean verified) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic = profilePic;
        this.verified = verified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
