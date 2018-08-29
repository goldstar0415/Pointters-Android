package com.pointters.model.request;

/**
 * Created by prashantkumar on 17/8/17.
 */

public class UserRegisterPutRequest {

    private String firstName;
    private String lastName;
    private LocationRequestModel location;
    private String profilePic;
    private Boolean completedRegistration;
    private String completedRegistrationDate;


    public UserRegisterPutRequest() {}


    public UserRegisterPutRequest(String firstName, String lastName, LocationRequestModel location, String profilePic, Boolean completedRegistration, String completedRegistrationDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;

        this.profilePic = profilePic;
        this.completedRegistration = completedRegistration;
        this.completedRegistrationDate = completedRegistrationDate;
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


    public Boolean getCompletedRegistration() {
        return completedRegistration;
    }

    public void setCompletedRegistration(Boolean completedRegistration) {
        this.completedRegistration = completedRegistration;
    }

    public String getCompletedRegistrationDate() {
        return completedRegistrationDate;
    }

    public void setCompletedRegistrationDate(String completedRegistrationDate) {
        this.completedRegistrationDate = completedRegistrationDate;
    }
}
