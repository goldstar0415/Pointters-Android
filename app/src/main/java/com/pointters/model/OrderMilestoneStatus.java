package com.pointters.model;

/**
 * Created by mac on 12/20/17.
 */

public class OrderMilestoneStatus {

    private String _id;
    private Boolean paid;
    private String paidDate;
    private String statusCode;
    private String statusDescription;


    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(String paidDate) {
        this.paidDate = paidDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
