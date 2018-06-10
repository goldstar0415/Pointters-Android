package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

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

        if (location instanceof LinkedTreeMap) {
            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) location;
            LocationModel loc = new LocationModel();
            if (linkedTreeMap.containsKey("_id")) {
                loc.set_id(linkedTreeMap.get("_id").toString());
            }
            if (linkedTreeMap.containsKey("city")){
                loc.setCity(linkedTreeMap.get("city").toString());
            }
            if (linkedTreeMap.containsKey("country")){
                loc.setCountry(linkedTreeMap.get("country").toString());
            }
            if (linkedTreeMap.containsKey("geoJson")){
                LinkedTreeMap gj = (LinkedTreeMap) linkedTreeMap.get("geoJson");
                GeoJsonModel geoJsonModel = new GeoJsonModel();
                geoJsonModel.setType(gj.get("type").toString());
                ArrayList<Double> doubles = (ArrayList<Double>) gj.get("coordinates");
                geoJsonModel.setCoordinates(doubles);
                loc.setGeoJson(geoJsonModel);
            }
            if (linkedTreeMap.containsKey("postalCode")){
                loc.setPostalCode(linkedTreeMap.get("postalCode").toString());
            }
            if (linkedTreeMap.containsKey("province")){
                loc.setProvince(linkedTreeMap.get("province").toString());
            }
            if (linkedTreeMap.containsKey("state")){
                loc.setState(linkedTreeMap.get("state").toString());
            }
            return loc;
        }else if (location instanceof ArrayList) {
            if (((ArrayList<LinkedTreeMap>) location).size() > 0) {
                LinkedTreeMap linkedTreeMap = ((ArrayList<LinkedTreeMap>) location).get(0);
                LocationModel loc = new LocationModel();
                if (linkedTreeMap.containsKey("_id")) {
                    loc.set_id(linkedTreeMap.get("_id").toString());
                }
                if (linkedTreeMap.containsKey("city")){
                    loc.setCity(linkedTreeMap.get("city").toString());
                }
                if (linkedTreeMap.containsKey("country")){
                    loc.setCountry(linkedTreeMap.get("country").toString());
                }
                if (linkedTreeMap.containsKey("geoJson")){
                    LinkedTreeMap gj = (LinkedTreeMap) linkedTreeMap.get("geoJson");
                    GeoJsonModel geoJsonModel = new GeoJsonModel();
                    geoJsonModel.setType(gj.get("type").toString());
                    ArrayList<Double> doubles = (ArrayList<Double>) gj.get("coordinates");
                    geoJsonModel.setCoordinates(doubles);
                    loc.setGeoJson(geoJsonModel);
                }
                if (linkedTreeMap.containsKey("postalCode")){
                    loc.setPostalCode(linkedTreeMap.get("postalCode").toString());
                }
                if (linkedTreeMap.containsKey("province")){
                    loc.setProvince(linkedTreeMap.get("province").toString());
                }
                if (linkedTreeMap.containsKey("state")){
                    loc.setState(linkedTreeMap.get("state").toString());
                }
                return loc;
            }else {
                return null;
            }
        }
        return null;
    }

    public void setLocation(ArrayList<LocationModel> location) {
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
