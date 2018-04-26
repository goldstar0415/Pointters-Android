package com.pointters.model.response;

/**
 * Created by jkc on 3/22/18.
 */

public class GetSellerEligibilityResponse {

    private String paymentSetupStatus;
    private Integer numServices;
    private String backgroundCheckStatus;

    public String getBackgroundCheckStatus() {
        return backgroundCheckStatus;
    }

    public void setBackgroundCheckStatus(String backgroundCheckStatus) {
        this.backgroundCheckStatus = backgroundCheckStatus;
    }

    public String getPaymentSetupStatus() {
        return paymentSetupStatus;
    }

    public void setPaymentSetupStatus(String paymentSetupStatus) {
        this.paymentSetupStatus = paymentSetupStatus;
    }

    public Integer getNumServices() {
        return numServices;
    }

    public void setNumServices(Integer numServices) {
        this.numServices = numServices;
    }
}
