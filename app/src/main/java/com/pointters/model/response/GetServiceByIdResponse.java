package com.pointters.model.response;

import com.pointters.model.LinkServiceModel;

/**
 * Created by mac on 12/26/17.
 */

public class GetServiceByIdResponse {

    private LinkServiceModel service;

    public LinkServiceModel getService() {
        return service;
    }

    public void setService(LinkServiceModel service) {
        this.service = service;
    }
}
