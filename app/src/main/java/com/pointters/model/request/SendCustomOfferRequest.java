package com.pointters.model.request;

import com.pointters.model.FulfillmentMethod;
import com.pointters.model.Media;
import com.pointters.model.Prices;

/**
 * Created by prashantkumar on 12/10/17.
 */

public class SendCustomOfferRequest {
    private FulfillmentMethod fulfillmentMethod;
    private LocationRequestModel location;
  /*  private Media media;*/
    private Prices price;
    private String serviceId;

    public SendCustomOfferRequest(FulfillmentMethod fulfillmentMethod, LocationRequestModel location, Prices price, String serviceId) {
        this.fulfillmentMethod = fulfillmentMethod;
        this.location = location;
        this.price = price;
        this.serviceId = serviceId;
    }
}
