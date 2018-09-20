package com.pointters.model.request;

import com.pointters.model.AddressModel;
import com.pointters.model.FulfillmentMethodForCustom;
import com.pointters.model.ParcelModel;

/**
 * Created by prashantkumar on 12/10/17.
 */

public class SendCustomOfferRequest1 {

    private String sellerId;
    private String buyerId;
    private String serviceId;
    private String currencyCode;
    private String currencySymbol;
    private String description;
    private FulfillmentMethodForCustom fulfillmentMethod;
    private int price;
    private int workDuration;
    private String workDurationUom;
    private AddressModel address;
    private ParcelModel parcel;



    public SendCustomOfferRequest1(String sellerId, String buyerId, String currencyCode, String currencySymbol, String description, FulfillmentMethodForCustom fulfillmentMethod, int price, int workDuration, String workDurationUom, AddressModel address, ParcelModel parcel) {
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.description = description;
        this.fulfillmentMethod = fulfillmentMethod;
        this.price = price;
        this.workDuration = workDuration;
        this.workDurationUom = workDurationUom;
        this.address = address;
        this.parcel = parcel;
    }

     public String getServiceId() {
        return serviceId;
     }

     public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
