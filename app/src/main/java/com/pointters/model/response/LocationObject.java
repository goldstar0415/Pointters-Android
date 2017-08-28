package com.pointters.model.response;

/**
 * Created by AnilJha on 8/8/17.
 */

public class LocationObject {

    private String state;
    private String province;
    private int postalCode;
    private LongitudeLatitudeObject longitudeLatitud;
    private String country;
    private String city;

    public LocationObject(String state, String province, int postalCode, LongitudeLatitudeObject longitudeLatitud, String country, String city) {
        this.state = state;
        this.province = province;
        this.postalCode = postalCode;
        this.longitudeLatitud = longitudeLatitud;
        this.country = country;
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public LongitudeLatitudeObject getLongitudeLatitud() {
        return longitudeLatitud;
    }

    public void setLongitudeLatitud(LongitudeLatitudeObject longitudeLatitud) {
        this.longitudeLatitud = longitudeLatitud;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
