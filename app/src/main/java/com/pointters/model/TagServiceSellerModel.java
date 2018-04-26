package com.pointters.model;

import java.io.Serializable;

/**
 * Created by prashantkumar on 9/10/17.
 */

public class TagServiceSellerModel implements Serializable {

    private String type;
    private Service service;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
