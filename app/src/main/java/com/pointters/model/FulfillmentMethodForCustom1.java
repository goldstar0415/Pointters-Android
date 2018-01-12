package com.pointters.model;

/**
 * Created by mac on 12/26/17.
 */

public class FulfillmentMethodForCustom1 {

    private boolean local;
    private boolean online;
    private boolean shipment;
    private boolean store;
    private int localServiceRadius;
    private String localServiceRadiusUom;


    public boolean getLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean getShipment() {
        return shipment;
    }

    public void setShipment(boolean shipment) {
        this.shipment = shipment;
    }

    public boolean getStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public int getLocalServiceRadius() {
        return localServiceRadius;
    }

    public void setLocalServiceRadius(int localServiceRadius) {
        this.localServiceRadius = localServiceRadius;
    }

    public String getLocalServiceRadiusUom() {
        return localServiceRadiusUom;
    }

    public void setLocalServiceRadiusUom(String localServiceRadiusUom) {
        this.localServiceRadiusUom = localServiceRadiusUom;
    }
}
