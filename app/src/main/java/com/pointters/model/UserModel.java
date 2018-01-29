package com.pointters.model;

import com.pointters.model.request.LocationRequestModel;

/**
 * Created by prashantkumar on 8/9/17.
 */

public class UserModel {

    private String _id;
    private String email;
    private Integer __v;

    private String tempPassword;
    private String resetPasswordExpires;
    private String companyName;
    private String description;
    private String firstName;
    private String lastName;
    private LocationRequestModel location;
    private String phone;
    private String profilePic;
    private boolean isActive;
    private boolean completedRegistration;

    public UserModel(String _id, String email, Integer __v, String tempPassword, String resetPasswordExpires, String companyName, String description, String firstName, String lastName, LocationRequestModel location, String phone, String profilePic, boolean isActive, boolean completedRegistration) {
        this._id = _id;
        this.email = email;
        this.__v = __v;
        this.tempPassword = tempPassword;
        this.resetPasswordExpires = resetPasswordExpires;
        this.companyName = companyName;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.phone = phone;
        this.profilePic = profilePic;
        this.isActive = isActive;
        this.completedRegistration = completedRegistration;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer get__v() {
        return __v;
    }

    public void set__v(Integer __v) {
        this.__v = __v;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public String getResetPasswordExpires() {
        return resetPasswordExpires;
    }

    public void setResetPasswordExpires(String resetPasswordExpires) {
        this.resetPasswordExpires = resetPasswordExpires;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isCompletedRegistration() {
        return completedRegistration;
    }

    public void setCompletedRegistration(boolean completedRegistration) {
        this.completedRegistration = completedRegistration;
    }
}
