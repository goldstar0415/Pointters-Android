package com.pointters.model.response;

import com.pointters.model.Service;

public class SearchSearviceResponse {
    private String type;
    private Service service;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
