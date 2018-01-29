package com.pointters.model;

/**
 * Created by mac on 12/25/17.
 */

public class FulfillmentMethodForCustom {

    private String local;
    private String online;
    private String shipment;
    private String store;
    private int localServiceRadius;
    private String localServiceRadiusUom;


    public FulfillmentMethodForCustom(String online, String shipment, String local, String store, int localServiceRadius, String localServiceRadiusUom) {
        this.online = online;
        this.shipment = shipment;
        this.local = local;
        this.store = store;
        this.localServiceRadius = localServiceRadius;
        this.localServiceRadiusUom = localServiceRadiusUom;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
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
