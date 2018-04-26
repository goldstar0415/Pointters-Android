package com.pointters.model;

import java.util.List;

/**
 * Created by prashantkumar on 14/11/17.
 */

public class ServiceDetailModel {

    private SellerModel seller;
    private Service service;
    private MetricsModel serviceMetrics;
    private List<ServiceReviewModel> reviews;


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public SellerModel getSeller() {
        return seller;
    }

    public void setSeller(SellerModel seller) {
        this.seller = seller;
    }

    public MetricsModel getServiceMetrics() {
        return serviceMetrics;
    }

    public void setServiceMetrics(MetricsModel serviceMetrics) {
        this.serviceMetrics = serviceMetrics;
    }

    public List<ServiceReviewModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ServiceReviewModel> reviews) {
        this.reviews = reviews;
    }
}
