package com.pointters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jkc on 3/20/18.
 */

public class AddressModel implements Serializable{
    @SerializedName("street1")
    @Expose
    private String street1;
    @SerializedName("street2")
    @Expose
    private String street2;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("apt")
    @Expose
    private String apt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("fullAddress")
    @Expose
    private String fullAddress;
    @SerializedName("isDefault")
    @Expose
    private boolean isDefault;

    public AddressModel(){
        this.isDefault = false;
        this.fullAddress = "";
    }
    public AddressModel(String tt, String st, String ct, String state, String zip, String apt, boolean b, String fullAddress) {
        this.street1 = st;
        this.name = tt;
        this.city = ct;
        this.state = state;
        this.zip = zip;
        this.apt = apt;
        this.isDefault = b;
        this.fullAddress = fullAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean mdefault) {
        isDefault = mdefault;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getApt() {
        return apt;
    }

    public void setApt(String apt) {
        this.apt = apt;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }
}
