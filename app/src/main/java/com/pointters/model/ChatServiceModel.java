package com.pointters.model;

/**
 * Created by mac on 1/6/18.
 */

public class ChatServiceModel {

    private String serviceId;
    private String description;
    private Media media;
    private Prices price;
    private UserChatModel seller;


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public Prices getPrice() {
        return price;
    }

    public void setPrice(Prices price) {
        this.price = price;
    }

    public UserChatModel getSeller() {
        return seller;
    }

    public void setSeller(UserChatModel seller) {
        this.seller = seller;
    }
}
