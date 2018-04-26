package com.pointters.model;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

/**
 * Created by mac on 12/20/17.
 */

public class InviteUserServiceModel {

    private boolean isActive;
    private String createdAt;
    private String updatedAt;
    private ArrayList<GeoJsonModel> geofence;
    private ArrayList<Media> media;
    private ArrayList<LocationModel> location;
    private ArrayList<Prices> prices;
    private String _id;
    private String userId;
    private SubCategories category;
    private String description;
    private FulfillmentMethod fulfillmentMethod;
    private Integer __v;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public SubCategories getCategory() {
        return category;
    }

    public void setCategory(SubCategories category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FulfillmentMethod getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(FulfillmentMethod fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }


    public Integer get__v() {
        return __v;
    }

    public void set__v(Integer __v) {
        this.__v = __v;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }

    public ArrayList<LocationModel> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<LocationModel> location) {
        this.location = location;
    }

    public ArrayList<Prices> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<Prices> prices) {
        this.prices = prices;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
