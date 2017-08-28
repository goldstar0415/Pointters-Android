package com.pointters.model.response;

import java.util.ArrayList;

/**
 * Created by AnilJha on 8/8/17.
 */

public class GetUserResponse {

    private String _id;
    private String email;
    private String password;
    private boolean isActive;
    private boolean isAdmin;
    private String license;
    private String awards;
    private String education;
    private String description;
    private String birthday;
    private LocationObject location;
    private boolean isEmailValid;
    private String profilePic;
    private String gender;
    private String companyName;
    private String lastName;
    private String firstName;
    private String __v;
    private String phone;
    private String phoneNumber;
    private ArrayList<String> watching;
    private ArrayList<String> following;
    private ArrayList<String> likes;
    private GetUserSettingsResponse settings;

    public GetUserResponse(String _id, String email, String password, boolean isActive, boolean isAdmin, String license, String awards, String education, String description, String birthday, LocationObject location, boolean isEmailValid, String profilePic, String gender, String companyName, String lastName, String firstName, String __v, String phone, String phoneNumber, ArrayList<String> watching, ArrayList<String> following, ArrayList<String> likes, GetUserSettingsResponse settings) {
        this._id = _id;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.license = license;
        this.awards = awards;
        this.education = education;
        this.description = description;
        this.birthday = birthday;
        this.location = location;
        this.isEmailValid = isEmailValid;
        this.profilePic = profilePic;
        this.gender = gender;
        this.companyName = companyName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.__v = __v;
        this.phone = phone;
        this.phoneNumber = phoneNumber;
        this.watching = watching;
        this.following = following;
        this.likes = likes;
        this.settings = settings;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public LocationObject getLocation() {
        return location;
    }

    public void setLocation(LocationObject location) {
        this.location = location;
    }

    public boolean isEmailValid() {
        return isEmailValid;
    }

    public void setEmailValid(boolean emailValid) {
        isEmailValid = emailValid;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<String> getWatching() {
        return watching;
    }

    public void setWatching(ArrayList<String> watching) {
        this.watching = watching;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public GetUserSettingsResponse getSettings() {
        return settings;
    }

    public void setSettings(GetUserSettingsResponse settings) {
        this.settings = settings;
    }
}
