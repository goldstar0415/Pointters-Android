package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;
import com.pointters.model.request.LocationRequestModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prashantkumar on 17/11/17.
 */

public class ServicesWoArray {
    private String id;
    private String description;
    private Object location;
    private Media media;
    private Prices prices;
    private Boolean promoted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationModel getLocation() {
        LocationModel result = new LocationModel();

        if (location instanceof LinkedTreeMap) {

            result.setCity((String) ((LinkedTreeMap) location).get("city"));
            result.setCountry((String) ((LinkedTreeMap) location).get("country"));
            result.setPostalCode((String) ((LinkedTreeMap) location).get("postalCode"));
            result.setProvince((String) ((LinkedTreeMap) location).get("province"));
            result.setState((String) ((LinkedTreeMap) location).get("state"));

            if (((LinkedTreeMap) location).get("geoJson") != null) {
                result.setGeoJson(((LinkedTreeMap) location).get("geoJson"));
            }
        }
        else if (location instanceof ArrayList) {

            if (((ArrayList) location).size() > 0) {

                result.setCity((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("city"));
                result.setCountry((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("country"));
                result.setPostalCode((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("postalCode"));
                result.setProvince((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("province"));
                result.setState((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("state"));

                if (((LinkedTreeMap) ((ArrayList) location).get(0)).get("geoJson") != null) {
                    result.setGeoJson(((LinkedTreeMap) ((ArrayList) location).get(0)).get("geoJson"));
                }
            }
        }

        return result;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public Boolean getPromoted() {
        return promoted;
    }

    public void setPromoted(Boolean promoted) {
        this.promoted = promoted;
    }
}
