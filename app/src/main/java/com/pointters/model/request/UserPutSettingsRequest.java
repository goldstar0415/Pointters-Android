package com.pointters.model.request;

/**
 * Created by prashantkumar on 21/9/17.
 */

public class UserPutSettingsRequest {
    private String phoneViewPermission;
    private String locationViewPermission;
    private String generalNotifications;
    private String orderNotifications;
    private String offerNotifications;
    private String summaryEmail;

    public UserPutSettingsRequest(String phoneViewPermission, String locationViewPermission, String generalNotifications, String orderNotifications, String offerNotifications, String summaryEmail) {
        this.phoneViewPermission = phoneViewPermission;
        this.locationViewPermission = locationViewPermission;
        this.generalNotifications = generalNotifications;
        this.orderNotifications = orderNotifications;
        this.offerNotifications = offerNotifications;
        this.summaryEmail = summaryEmail;
    }


    public String getPhoneViewPermission() {
        return phoneViewPermission;
    }

    public void setPhoneViewPermission(String phoneViewPermission) {
        this.phoneViewPermission = phoneViewPermission;
    }

    public String getLocationViewPermission() {
        return locationViewPermission;
    }

    public void setLocationViewPermission(String locationViewPermission) {
        this.locationViewPermission = locationViewPermission;
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
