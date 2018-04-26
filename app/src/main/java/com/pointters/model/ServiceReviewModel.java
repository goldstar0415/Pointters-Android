package com.pointters.model;

/**
 * Created by mac on 1/31/18.
 */

public class ServiceReviewModel {

    private String comment;
    private String createdAt;
    private Float qualityOfService;
    private Float overallRating;
    private Integer willingToBuyServiceAgain;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Float getQualityOfService() {
        return qualityOfService;
    }

    public void setQualityOfService(Float qualityOfService) {
        this.qualityOfService = qualityOfService;
    }

    public Float getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Float overallRating) {
        this.overallRating = overallRating;
    }

    public Integer getWillingToBuyServiceAgain() {
        return willingToBuyServiceAgain;
    }

    public void setWillingToBuyServiceAgain(Integer willingToBuyServiceAgain) {
        this.willingToBuyServiceAgain = willingToBuyServiceAgain;
    }
}
