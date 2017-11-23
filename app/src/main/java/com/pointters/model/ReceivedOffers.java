package com.pointters.model;

/**
 * Created by prashantkumar on 9/11/17.
 */

public class ReceivedOffers {
    private SellerModel seller;
    private String serviceId;
    private int price;
    private int workDuration;
    private String workDurationUom;
    private String createdAt;
    private String serviceDescription;

    public SellerModel getSeller() {
        return seller;
    }

    public void setSeller(SellerModel seller) {
        this.seller = seller;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(int workDuration) {
        this.workDuration = workDuration;
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

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
}
