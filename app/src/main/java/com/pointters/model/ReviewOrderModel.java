package com.pointters.model;

public class ReviewOrderModel {

    private String comment;
    private boolean isActive;
    private Integer qualityOfService;
    private Integer overallRating;
    private String serviceId;
    private String orderId;
    private Integer willingToBuyServiceAgain;

    public ReviewOrderModel(){

    }

    public ReviewOrderModel(String comment, boolean isActive, Integer qualityOfService, Integer overallRating, String serviceId, String orderId, Integer willingToBuyServiceAgain) {
        this.comment = comment;
        this.isActive = isActive;
        this.qualityOfService = qualityOfService;
        this.overallRating = overallRating;
        this.serviceId = serviceId;
        this.orderId = orderId;
        this.willingToBuyServiceAgain = willingToBuyServiceAgain;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getQualityOfService() {
        return qualityOfService;
    }

    public void setQualityOfService(Integer qualityOfService) {
        this.qualityOfService = qualityOfService;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getWillingToBuyServiceAgain() {
        return willingToBuyServiceAgain;
    }

    public void setWillingToBuyServiceAgain(Integer willingToBuyServiceAgain) {
        this.willingToBuyServiceAgain = willingToBuyServiceAgain;
    }
}
