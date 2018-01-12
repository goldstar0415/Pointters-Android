package com.pointters.model;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class BuyOrderModel {

    private OrdersModel order;
    private UserServiceModel seller;


    public OrdersModel getOrder() { return order; }

    public void setOrder(OrdersModel order) {
        this.order = order;
    }

    public UserServiceModel getSeller() {
        return seller;
    }

    public void setSeller(UserServiceModel seller) {
        this.seller = seller;
    }
}
