package com.pointters.model;

import java.io.Serializable;

/**
 * Created by mac on 12/26/17.
 */

public class FulfillmentMethodForCustom1 implements Serializable{

    private String _id;
    private boolean local;
    private boolean online;
    private boolean shipment;
    private boolean store;
    private int localServiceRadius;
    private String localServiceRadiusUom;
    private AddressModel address;
    private ParcelModel parcel;

    public FulfillmentMethodForCustom1(){
        this.online = false;
        this.shipment = false;
        this.local = false;
        this.store = false;
        this.localServiceRadius = 25;
        this.localServiceRadiusUom = "mile";
        this.address = new AddressModel();
        this.parcel = new ParcelModel();
    }

    public FulfillmentMethodForCustom1(boolean online, boolean shipment, boolean local, boolean store, int localServiceRadius, String localServiceRadiusUom) {
        this.online = online;
        this.shipment = shipment;
        this.local = local;
        this.store = store;
        this.localServiceRadius = localServiceRadius;
        this.localServiceRadiusUom = localServiceRadiusUom;
    }

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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public ParcelModel getParcel() {
        return parcel;
    }

    public void setParcel(ParcelModel parcel) {
        this.parcel = parcel;
    }
}
