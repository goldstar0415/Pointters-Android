package com.pointters.model;

import java.util.List;

/**
 * Created by mac on 2/2/18.
 */

public class ServiceInfoModel {

    private String _id;
    private String createdAt;
    private String description;
    private CategoryModel category;
    private FulfillmentMethodForCustom fulfillmentMethod;
    private List<LocationModel> location;
    private List<Media> media;
    private List<Prices> prices;
    private String updatedAt;
    private String userId;


    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    public FulfillmentMethodForCustom getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(FulfillmentMethodForCustom fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public List<LocationModel> getLocation() {
        return location;
    }

    public void setLocation(List<LocationModel> location) {
        this.location = location;
    }

    public List<Media> getMedia() {
       return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public List<Prices> getPrices() {
        return prices;
    }

    public void setPrices(List<Prices> prices) {
        this.prices = prices;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
