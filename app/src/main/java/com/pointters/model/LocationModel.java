package com.pointters.model;

import java.io.Serializable;

/**
 * Created by mac on 12/8/17.
 */

public class LocationModel implements Serializable{

    private String _id;
    private String city;
    private String country;
    private String postalCode;
    private String province;
    private String state;
    private GeoJsonModel geoJson;

    public String FullAddress(){
        return (getCity() != null ? getCity() + " " : "") + "" + (getPostalCode() != null ? getPostalCode() : "") + ", " + (getState() != null ? getState() : "") + ", " + (getCountry() != null ? getCountry() : "");
    }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }

    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getProvince() { return province; }

    public void setProvince(String province) { this.province = province; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public GeoJsonModel getGeoJson() {

        return geoJson;
    }

    public void setGeoJson(GeoJsonModel geoJson) {
        this.geoJson = geoJson;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}