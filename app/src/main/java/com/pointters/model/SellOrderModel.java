package com.pointters.model;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class SellOrderModel {
    private ReceivedOfferSellerModel buyer;

    public ReceivedOfferSellerModel getBuyer() {
        return buyer;
    }

    public void setBuyer(ReceivedOfferSellerModel buyer) {
        this.buyer = buyer;
    }
}
