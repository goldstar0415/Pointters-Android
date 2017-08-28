package com.pointters.model.request;

/**
 * Created by prashantkumar on 17/8/17.
 */

public class UserPutRequest {
    private String companyName;
    private String description;
    private String firstName;
    private String lastName;
    private Location location;
    private String phone;
    private String profilePic;

    public UserPutRequest(String companyName, String description, String firstName, String lastName, Location location, String phone, String profilePic) {
        this.companyName = companyName;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.phone = phone;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
