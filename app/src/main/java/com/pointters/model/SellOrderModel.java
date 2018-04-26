package com.pointters.model;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class SellOrderModel {

    private OrdersModel order;
    private UserServiceModel buyer;


    public OrdersModel getOrder() { return order; }

    public void setOrder(OrdersModel order) {
        this.order = order;
    }

    public UserServiceModel getBuyer() {
        return buyer;
    }

    public void setBuyer(UserServiceModel buyer) {
        this.buyer = buyer;
    }
}
