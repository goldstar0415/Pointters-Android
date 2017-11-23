package com.pointters.model;

import com.pointters.model.request.LocationRequestModel;

/**
 * Created by prashantkumar on 9/9/17.
 */

public class UserPutRequestPhoneNoEmpty {

    private String companyName;
    private String description;
    private String firstName;
    private String lastName;
    private LocationRequestModel location;
    private String profilePic;

    public UserPutRequestPhoneNoEmpty(String companyName, String description, String firstName, String lastName, LocationRequestModel location, String profilePic) {
        this.companyName = companyName;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.profilePic = profilePic;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocationRequestModel getLocation() {
        return location;
    }

    public void setLocation(LocationRequestModel location) {
        this.location = location;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
