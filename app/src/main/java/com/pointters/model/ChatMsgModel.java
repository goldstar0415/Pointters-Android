package com.pointters.model;

import java.util.List;

/**
 * Created by mac on 1/5/18.
 */

public class ChatMsgModel {

    private String id;
    private String createdAt;
    private String updatedAt;
    private String messageText;
    private List<Media> media;
    private ChatServiceModel service;
    private ChatOfferModel offer;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public ChatServiceModel getService() {
        return service;
    }

    public void setService(ChatServiceModel service) {
        this.service = service;
    }

    public ChatOfferModel getOffer() {
        return offer;
    }

    public void setOffer(ChatOfferModel offer) {
        this.offer = offer;
    }
}
