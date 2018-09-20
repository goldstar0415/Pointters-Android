package com.pointters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class Prices implements Serializable {

    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private Float price;
    @SerializedName("priceWithoutDiscount")
    @Expose
    private Float priceWithoutDiscount;
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


    public Prices(String description, Float price, Integer time, String timeUnitOfMeasure, String currencySymbol, String currencyCode) {
        this.description = description;
        this.price = price;
        this.time = time;
        this.timeUnitOfMeasure = timeUnitOfMeasure;
        this.currencySymbol = currencySymbol;
        this.currencyCode = currencyCode;
        this.priceWithoutDiscount = price;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPriceWithoutDiscount() {
        return priceWithoutDiscount;
    }

    public void setPriceWithoutDiscount(Float priceWithoutDiscount) {
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
