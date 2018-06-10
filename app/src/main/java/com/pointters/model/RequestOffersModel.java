package com.pointters.model;

/**
 * Created by mac on 12/5/17.
 */

public class RequestOffersModel {

    private JobRequestModel request;
    private JobRequesterModel requester;
    private String createdAt;
    private Integer numOffers;
    private Integer expiresIn;
    private String requestOfferId;


    public JobRequestModel getRequest() { return request; }

    public void setRequest(JobRequestModel request) {
        this.request = request;
    }

    public JobRequesterModel getRequester() {
        return requester;
    }

    public void setRequester(JobRequesterModel requester) {
        this.requester = requester;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getNumOffers() {
        return numOffers;
    }

    public void setNumOffers(Integer numOffers) {
        this.numOffers = numOffers;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRequestOfferId() {
        return requestOfferId;
    }

    public void setRequestOfferId(String requestOfferId) {
        this.requestOfferId = requestOfferId;
    }
}
