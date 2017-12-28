package com.pointters.model;

/**
 * Created by prashantkumar on 27/9/17.
 */

public class FulfillmentMethod {
    private Boolean local;
    private Boolean online;
    private Boolean shipment;
    private Boolean store;

    public FulfillmentMethod(Boolean local, Boolean online, Boolean shipment, Boolean store) {
        this.local = local;
        this.online = online;
        this.shipment = shipment;
        this.store = store;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getShipment() {
        return shipment;
    }

    public void setShipment(Boolean shipment) {
        this.shipment = shipment;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }
}
