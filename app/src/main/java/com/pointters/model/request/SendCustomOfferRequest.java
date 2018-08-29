package com.pointters.model.request;

import com.pointters.model.FulfillmentMethodForCustom1;

/**
 * Created by prashantkumar on 12/10/17.
 */

public class SendCustomOfferRequest {

    private String sellerId;
    private String buyerId;
    private String serviceId;
    private String currencyCode;
    private String currencySymbol;
    private String description;
    private FulfillmentMethodForCustom1 fulfillmentMethod;
    private int price;
    private int workDuration;
    private String workDurationUom;


    public SendCustomOfferRequest(String sellerId, String buyerId, String currencyCode, String currencySymbol, String description, FulfillmentMethodForCustom1 fulfillmentMethod, int price, int workDuration, String workDurationUom) {
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.description = description;
        this.fulfillmentMethod = fulfillmentMethod;
        this.price = price;
        this.workDuration = workDuration;
        this.workDurationUom = workDurationUom;
    }

     public String getServiceId() {
        return serviceId;
     }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
