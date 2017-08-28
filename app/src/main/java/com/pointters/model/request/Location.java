package com.pointters.model.request;

/**
 * Created by prashantkumar on 17/8/17.
 */

public class Location {
    private String city;
    private String country;
    private LongitudeLatitude location;
    private int postalCode;
    private String province;
    private String state;

    public Location(String city, String country, LongitudeLatitude location, int postalCode, String province, String state) {
        this.city = city;
        this.country = country;
        this.location = location;
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

    public LongitudeLatitude getLocation() {
        return location;
    }

    public void setLocation(LongitudeLatitude location) {
        this.location = location;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
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
