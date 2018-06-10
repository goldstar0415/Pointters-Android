package com.pointters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PaymentMethodNonceModel implements Serializable {
    @SerializedName("billingAgreementId")
    @Expose
    private String billingAgreementId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("customerId")
    @Expose
    private String customerId;
    @SerializedName("default")
    @Expose
    private boolean isDefault;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("subscriptions")
    @Expose
    private ArrayList<Object> subscriptions;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("payerId")
    @Expose
    private String payerId;
    @SerializedName("isChannelInitiated")
    @Expose
    private boolean isChannelInitiated;
    @SerializedName("payerInfo")
    @Expose
    private Object payerInfo;
    @SerializedName("limitedUseOrderId")
    @Expose
    private String limitedUseOrderId;


    public String getBillingAgreementId() {
        return billingAgreementId;
    }

    public void setBillingAgreementId(String billingAgreementId) {
        this.billingAgreementId = billingAgreementId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<Object> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<Object> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public boolean isChannelInitiated() {
        return isChannelInitiated;
    }

    public void setChannelInitiated(boolean channelInitiated) {
        isChannelInitiated = channelInitiated;
    }

    public Object getPayerInfo() {
        return payerInfo;
    }

    public void setPayerInfo(Object payerInfo) {
        this.payerInfo = payerInfo;
    }

    public String getLimitedUseOrderId() {
        return limitedUseOrderId;
    }

    public void setLimitedUseOrderId(String limitedUseOrderId) {
        this.limitedUseOrderId = limitedUseOrderId;
    }
}
