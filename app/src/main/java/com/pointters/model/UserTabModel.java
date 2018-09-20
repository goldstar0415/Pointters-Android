package com.pointters.model;

import com.pointters.model.request.LocationRequestModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by prashantkumar on 8/9/17.
 */

public class UserTabModel implements Serializable{

    private String id;
    private String firstName;
    private String lastName;
    private LocationRequestModel location;
    private String profilePic;
    private Integer numOrders;
    private float avgRating;
    private Integer pointValue;
    private List<String> categories;

    public UserTabModel(String id, String firstName, String lastName, LocationRequestModel location, String profilePic, int numOrders, float avgRating, int pointValue, List<String> categories) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.profilePic = profilePic;
        this.numOrders = numOrders;
        this.avgRating = avgRating;
        this.pointValue = pointValue;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
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

    public Integer getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(Integer numOrders) {
        this.numOrders = numOrders;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getPointValue() {
        return pointValue;
    }

    public void setPointValue(Integer pointValue) {
        this.pointValue = pointValue;
    }
}
