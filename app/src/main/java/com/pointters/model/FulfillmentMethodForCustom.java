package com.pointters.model;

/**
 * Created by mac on 12/25/17.
 */

public class FulfillmentMethodForCustom {

    private boolean local;
    private boolean online;
    private boolean shipment;
    private boolean store;
    private int localServiceRadius;
    private String localServiceRadiusUom;

    public FulfillmentMethodForCustom(boolean online, boolean shipment, boolean local, boolean store, int localServiceRadius, String localServiceRadiusUom) {
        this.online = online;
        this.shipment = shipment;
        this.local = local;
        this.store = store;
        this.localServiceRadius = localServiceRadius;
        this.localServiceRadiusUom = localServiceRadiusUom;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isShipment() {
        return shipment;
    }

    public void setShipment(boolean shipment) {
        this.shipment = shipment;
    }

    public boolean isStore() {
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
