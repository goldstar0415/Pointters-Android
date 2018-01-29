package com.pointters.model;

import com.pointters.model.LocationModel;
import com.pointters.model.Media;
import com.pointters.model.OrderMilestoneStatus;

/**
 * Created by mac on 12/20/17.
 */

public class OrdersModel {

     private String currencyCode;
     private String currencySymbol;
     private Integer totalAmount;
     private Integer totalAmountBeforeDiscount;
     private String paymentDate;
     private String status;
     private OrderMilestoneStatus orderMilestoneStatuses;
     private LocationModel serviceLocation;
     private Media media;
     private String description;
     private String priceDescription;
     private Integer notificationCount;


    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalAmountBeforeDiscount() {
        return totalAmountBeforeDiscount;
    }

    public void setTotalAmountBeforeDiscount(Integer totalAmountBeforeDiscount) {
        this.totalAmountBeforeDiscount = totalAmountBeforeDiscount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderMilestoneStatus getOrderMilestoneStatuses() {
        return orderMilestoneStatuses;
    }

    public void setOrderMilestoneStatuses(OrderMilestoneStatus orderMilestoneStatuses) {
        this.orderMilestoneStatuses = orderMilestoneStatuses;
    }

    public LocationModel getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(LocationModel serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceDescription() {
        return priceDescription;
    }

    public void setPriceDescription(String priceDescription) {
        this.priceDescription = priceDescription;
    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }
}
