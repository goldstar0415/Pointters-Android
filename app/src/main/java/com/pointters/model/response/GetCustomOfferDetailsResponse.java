package com.pointters.model.response;

import com.pointters.model.CustomOfferModels;

/**
 * Created by mac on 12/26/17.
 */

public class GetCustomOfferDetailsResponse {

    private CustomOfferModels offer;

    public CustomOfferModels getOffer() {
        return offer;
    }

    public void setOffer(CustomOfferModels offer) {
        this.offer = offer;
    }
}
