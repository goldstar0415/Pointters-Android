package com.pointters.model.response;

/**
 * Created by AnilJha on 8/8/17.
 */

public class GetUserSettingsResponse {

    private String generalNotifications;
    private String orderNotifications;
    private String offerNotifications;
    private String summaryEmail;

    public GetUserSettingsResponse(String generalNotifications, String orderNotifications, String offerNotifications, String summaryEmail) {
        this.generalNotifications = generalNotifications;
        this.orderNotifications = orderNotifications;
        this.offerNotifications = offerNotifications;
        this.summaryEmail = summaryEmail;
    }

    public String getGeneralNotifications() {
        return generalNotifications;
    }

    public void setGeneralNotifications(String generalNotifications) {
        this.generalNotifications = generalNotifications;
    }

    public String getOrderNotifications() {
        return orderNotifications;
    }

    public void setOrderNotifications(String orderNotifications) {
        this.orderNotifications = orderNotifications;
    }

    public String getOfferNotifications() {
        return offerNotifications;
    }

    public void setOfferNotifications(String offerNotifications) {
        this.offerNotifications = offerNotifications;
    }

    public String getSummaryEmail() {
        return summaryEmail;
    }

    public void setSummaryEmail(String summaryEmail) {
        this.summaryEmail = summaryEmail;
    }
}
