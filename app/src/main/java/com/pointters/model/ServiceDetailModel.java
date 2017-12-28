package com.pointters.model;

/**
 * Created by prashantkumar on 14/11/17.
 */

public class ServiceDetailModel {
    private SellerModel seller;
    private Service service;

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



}
