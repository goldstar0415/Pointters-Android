package com.pointters.model;

import java.util.ArrayList;

public class InviteSuggestedUserModel {
    private String userId;
    private String firstName;
    private String lastName;
    private String profilePic;
    private Integer numServices;
    private Integer pointValue;
    private Integer numOrders;
    private Integer numFollowers;
    private Integer avgRating;
    private boolean hasFollowed = false;
    private ArrayList<String> categories;
    private ArrayList<InviteUserServiceModel> services;

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

    public Integer getNumServices() {
        return numServices;
    }

    public void setNumServices(Integer numServices) {
        this.numServices = numServices;
    }

    public Integer getPointValue() {
        return pointValue;
    }

    public void setPointValue(Integer pointValue) {
        this.pointValue = pointValue;
    }

    public Integer getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(Integer numOrders) {
        this.numOrders = numOrders;
    }

    public Integer getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Integer avgRating) {
        this.avgRating = avgRating;
    }

    public boolean isHasFollowed() {
        return hasFollowed;
    }

    public void setHasFollowed(boolean hasFollowed) {
        this.hasFollowed = hasFollowed;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<InviteUserServiceModel> getServices() {
        return services;
    }

    public void setServices(ArrayList<InviteUserServiceModel> services) {
        this.services = services;
    }

    public Integer getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Integer numFollowers) {
        this.numFollowers = numFollowers;
    }
}
