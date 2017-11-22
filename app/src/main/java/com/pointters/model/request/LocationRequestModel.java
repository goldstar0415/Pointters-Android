package com.pointters.model.request;

/**
 * Created by prashantkumar on 17/8/17.
 */

public class LocationRequestModel {
    private String city;
    private String country;
    private LongitudeLatitude geoJson;
    private String postalCode;
    private String province;
    private String state;

    public LocationRequestModel(String city, String country, LongitudeLatitude geoJson, String postalCode, String province, String state) {
        this.city = city;
        this.country = country;
        this.geoJson = geoJson;
        this.postalCode = postalCode;
        this.province = province;
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LongitudeLatitude getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(LongitudeLatitude geoJson) {
        this.geoJson = geoJson;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
