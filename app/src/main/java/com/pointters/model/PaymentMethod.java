package com.pointters.model;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class PaymentMethod  {

    private String method;

    public PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
