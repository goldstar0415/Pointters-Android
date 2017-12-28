package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 12/8/17.
 */

public class LocationModel {

    private String city;
    private String country;
    private String postalCode;
    private String province;
    private String state;
    private Object geoJson;


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
        GeoJsonModel result = new GeoJsonModel();

        if (geoJson != null) {
            if (((LinkedTreeMap) geoJson).get("coordinates") != null) {
                result.setCoordinates((ArrayList<Double>) ((LinkedTreeMap) geoJson).get("coordinates"));
            }
        }

        return result;
    }

    public void setGeoJson(Object geoJson) {
        this.geoJson = geoJson;
    }
}