package com.pointters.model.response;

import com.pointters.model.ServiceInfoModel;

/**
 * Created by mac on 2/2/18.
 */

public class GetServiceInfoResponse {

    private ServiceInfoModel service;

    public ServiceInfoModel getService() {
        return service;
    }

    public void setService(ServiceInfoModel service) {
        this.service = service;
    }
}
