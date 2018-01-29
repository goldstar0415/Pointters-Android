package com.pointters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class Prices implements Serializable {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("priceWithoutDiscount")
    @Expose
    private Integer priceWithoutDiscount;
    @SerializedName("currencySymbol")
    @Expose
    private String currencySymbol;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("timeUnitOfMeasure")
    @Expose
    private String timeUnitOfMeasure;


    public Prices(String description, Integer price, Integer time, String timeUnitOfMeasure, String currencySymbol, String currencyCode) {
        this.description = description;
        this.price = price;
        this.time = time;
        this.timeUnitOfMeasure = timeUnitOfMeasure;
        this.currencySymbol = currencySymbol;
        this.currencyCode = currencyCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPriceWithoutDiscount() {
        return priceWithoutDiscount;
    }

    public void setPriceWithoutDiscount(Integer priceWithoutDiscount) {
        this.priceWithoutDiscount = priceWithoutDiscount;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getTimeUnitOfMeasure() {
        return timeUnitOfMeasure;
    }

    public void setTimeUnitOfMeasure(String timeUnitOfMeasure) {
        this.timeUnitOfMeasure = timeUnitOfMeasure;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
