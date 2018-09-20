package com.pointters.model;

import java.util.ArrayList;

/**
 * Created by mac on 12/20/17.
 */

public class OrdersDetailModel {

    private String createdAt;
    private String updatedAt;
    private ArrayList<GeoJsonModel> geofence;
    private ArrayList<OrderItem> orderItems;
    private ArrayList<Media> sellerDeliveredMedia;
    private ArrayList<LocationModel> sellerServiceLocation;
    private Object buyerServiceLocation;
    private ArrayList<Prices> servicesPrices;
    private String _id;
    private String buyerId;
    private CategoryDetails category;
    private String currencyCode;
    private String currencySymbol;
    private String description;
    private FulfillmentMethod fulfillmentMethod;
    private OrderMilestoneStatus orderMilestoneStatuses;
    private String paymentDate;
    private PaymentMethod paymentMethod;
    private String sellerId;
    private String serviceId;
    private Float taxAmount;
    private Float shippingFee;
    private Float subtotalAmount;
    private Integer totalAmountBeforeDiscount;
    private boolean sellerAcceptedScheduleTime;
    private boolean sellerAcceptedBuyerServiceLocation;
//    private LocationModel buyerServiceLocation;
    private Integer __v;
    private ContactModel contact;
    private String serviceScheduleDate;

    public String getServiceScheduleDate() {
        return serviceScheduleDate;
    }

    public void setServiceScheduleDate(String serviceScheduleDate) {
        this.serviceScheduleDate = serviceScheduleDate;
    }

    public Float getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Float taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Float getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Float shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Float getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(Float subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public ContactModel getContact() {
        return contact;
    }

    public void setContact(ContactModel contact) {
        this.contact = contact;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<GeoJsonModel> getGeofence() {
        return geofence;
    }

    public void setGeofence(ArrayList<GeoJsonModel> geofence) {
        this.geofence = geofence;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public ArrayList<Media> getSellerDeliveredMedia() {
        return sellerDeliveredMedia;
    }

    public void setSellerDeliveredMedia(ArrayList<Media> sellerDeliveredMedia) {
        this.sellerDeliveredMedia = sellerDeliveredMedia;
    }

    public ArrayList<LocationModel> getSellerServiceLocation() {
        return sellerServiceLocation;
    }

    public void setSellerServiceLocation(ArrayList<LocationModel> sellerServiceLocation) {
        this.sellerServiceLocation = sellerServiceLocation;
    }

    public ArrayList<Prices> getServicesPrices() {
        return servicesPrices;
    }

    public void setServicesPrices(ArrayList<Prices> servicesPrices) {
        this.servicesPrices = servicesPrices;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public CategoryDetails getCategory() {
        return category;
    }

    public void setCategory(CategoryDetails category) {
        this.category = category;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FulfillmentMethod getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(FulfillmentMethod fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public OrderMilestoneStatus getOrderMilestoneStatuses() {
        return orderMilestoneStatuses;
    }

    public void setOrderMilestoneStatuses(OrderMilestoneStatus orderMilestoneStatuses) {
        this.orderMilestoneStatuses = orderMilestoneStatuses;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getTotalAmountBeforeDiscount() {
        return totalAmountBeforeDiscount;
    }

    public void setTotalAmountBeforeDiscount(Integer totalAmountBeforeDiscount) {
        this.totalAmountBeforeDiscount = totalAmountBeforeDiscount;
    }

    public boolean isSellerAcceptedScheduleTime() {
        return sellerAcceptedScheduleTime;
    }

    public void setSellerAcceptedScheduleTime(boolean sellerAcceptedScheduleTime) {
        this.sellerAcceptedScheduleTime = sellerAcceptedScheduleTime;
    }

    public boolean isSellerAcceptedBuyerServiceLocation() {
        return sellerAcceptedBuyerServiceLocation;
    }

    public void setSellerAcceptedBuyerServiceLocation(boolean sellerAcceptedBuyerServiceLocation) {
        this.sellerAcceptedBuyerServiceLocation = sellerAcceptedBuyerServiceLocation;
    }

    public Object getBuyerServiceLocation() {
        return buyerServiceLocation;
    }

    public void setBuyerServiceLocation(Object buyerServiceLocation) {
        this.buyerServiceLocation = buyerServiceLocation;
    }

    public Integer get__v() {
        return __v;
    }

    public void set__v(Integer __v) {
        this.__v = __v;
    }
}
