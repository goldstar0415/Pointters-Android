package com.pointters.model;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class ReceivedOfferModel {
    private ReceivedOfferSellerModel seller;
    private Media media;
    private LocationModel location;
    private String serviceId;
    private String workDurationUom;
    private Integer price;
    private Integer workDuration;
    private String createdAt;
    private String description;
    private String offerId;


    public ReceivedOfferSellerModel getSeller() {
        return seller;
    }

    public void setSeller(ReceivedOfferSellerModel seller) {
        this.seller = seller;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getWorkDurationUom() {
        return workDurationUom;
    }

    public void setWorkDurationUom(String workDurationUom) {
        this.workDurationUom = workDurationUom;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(Integer workDuration) {
        this.workDuration = workDuration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getServiceDescription() {
        return description;
    }

    public void setServiceDescription(String serviceDescription) {
        this.description = serviceDescription;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
}
