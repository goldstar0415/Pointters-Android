package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 12/26/17.
 */

public class CustomOfferModels {

    private String _id;
    private String sellerId;
    private String buyerId;
    private String description;
    private FulfillmentMethodForCustom1 fulfillmentMethod;
    private LocationModel location;
    private Integer price;
    private String serviceId;
    private Integer workDuration;
    private String workDurationUom;
    private String updateAt;
    private Object media;
    private String createdAt;
    private String currencySymbol;
    private String currencyCode;


    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Media getMedia() {
        String strFileName = "";
        String strMediaType = "";

        if (media instanceof LinkedTreeMap) {
            strFileName = (String)((LinkedTreeMap) media).get("fileName");
            strMediaType = (String)((LinkedTreeMap) media).get("mediaType");
        }
        else if (media instanceof ArrayList) {
            if (((ArrayList) media).size() > 0) {
                strFileName = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("fileName");
                strMediaType = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("mediaType");
            }
        }

        return new Media(strFileName, strMediaType);
    }

    public void setMedia(Object media) {
        this.media = media;
    }

    public FulfillmentMethodForCustom1 getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(FulfillmentMethodForCustom1 fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(Integer workDuration) {
        this.workDuration = workDuration;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getWorkDurationUom() {
        return workDurationUom;
    }

    public void setWorkDurationUom(String workDurationUom) {
        this.workDurationUom = workDurationUom;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
