package com.pointters.model;

/**
 * Created by mac on 1/6/18.
 */

public class ChatOfferModel {

    private String offerId;
    private String description;
    private Media media;
    private Integer price;
    private String currencySymbol;
    private String currencyCode;
    private Integer workDuration;
    private String workDurationUom;
    private ChatServiceModel service;


    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public Integer getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(Integer workDuration) {
        this.workDuration = workDuration;
    }

    public String getWorkDurationUom() {
        return workDurationUom;
    }

    public void setWorkDurationUom(String workDurationUom) {
        this.workDurationUom = workDurationUom;
    }

    public ChatServiceModel getService() {
        return service;
    }

    public void setService(ChatServiceModel service) {
        this.service = service;
    }
}
