package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by mac on 12/8/17.
 */

public class TagSourceModel {

    private String userId;
    private String type;
    private String firstName;
    private String lastName;
    private String description;
    private String profilePic;
    private Object location;
    private String serviceId;
    private Object media;
    private ArrayList<Prices> prices;
    private Integer pointValue;
    private Integer numOrders;
    private Integer avgRating;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

//    public LocationModel getLocation() {
//
//        LocationModel result = new LocationModel();
//
//        if (location instanceof LinkedTreeMap) {
//
//            result.setCity((String) ((LinkedTreeMap) location).get("city"));
//            result.setCountry((String) ((LinkedTreeMap) location).get("country"));
//            result.setPostalCode((String) ((LinkedTreeMap) location).get("postalCode"));
//            result.setProvince((String) ((LinkedTreeMap) location).get("province"));
//            result.setState((String) ((LinkedTreeMap) location).get("state"));
//
//            if (((LinkedTreeMap) location).get("geoJson") != null) {
//                result.setGeoJson(((LinkedTreeMap) location).get("geoJson"));
//            }
//        }
//        else if (location instanceof ArrayList) {
//
//            if (((ArrayList) location).size() > 0) {
//
//                result.setCity((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("city"));
//                result.setCountry((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("country"));
//                result.setPostalCode((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("postalCode"));
//                result.setProvince((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("province"));
//                result.setState((String) ((LinkedTreeMap) ((ArrayList) location).get(0)).get("state"));
//
//                if (((LinkedTreeMap) ((ArrayList) location).get(0)).get("geoJson") != null) {
//                    result.setGeoJson(((LinkedTreeMap) ((ArrayList) location).get(0)).get("geoJson"));
//                }
//            }
//        }
//
//        return result;
//    }

    public void setLocation(Object location) { this.location = location; }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
            if (((ArrayList<LinkedTreeMap>) location).size() == 0) {
                return null;
            }
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
        }
        return null;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Media getMedia() {

        String strFileName = "";
        String strMediaType = "";

        if (media instanceof LinkedTreeMap) {
            strFileName = (String)((LinkedTreeMap) media).get("fileName");
            strMediaType = (String)((LinkedTreeMap) media).get("mediaType");
        }
        else if (media instanceof ArrayList) {
            if (((ArrayList) media).size() > 0) {
                strFileName = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("fileName");
                strMediaType = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("mediaType");
            }
        }

        return new Media(strFileName, strMediaType);
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public ArrayList<Prices> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<Prices> prices) {
        this.prices = prices;
    }

    public Integer getPointValue() {
        return pointValue;
    }

    public void setPointValue(Integer pointValue) {
        this.pointValue = pointValue;
    }

    public Integer getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(Integer numOrders) {
        this.numOrders = numOrders;
    }

    public Integer getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Integer avgRating) {
        this.avgRating = avgRating;
    }
}
