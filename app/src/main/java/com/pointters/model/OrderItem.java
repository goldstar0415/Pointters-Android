package com.pointters.model;

public class OrderItem {
    private String description;
    private Integer price;
    private Integer priceWithoutDiscount;
    private Integer quantity;
    private Integer time;
    private String timeUnitOfMeasure;
    private String _id;


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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
