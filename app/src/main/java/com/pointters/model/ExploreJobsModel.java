package com.pointters.model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 12/5/17.
 */

public class ExploreJobsModel {

    private Object media;
    private String description;
    private String createdAt;
    private String id;
    private String updateAt;
    private Object location;
    private String scheduleDate;
    private String currencyCode;
    private String currencySymbol;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer numOffers;
    private UserModel user;
    private Object offerSent;
    private String userId;
    private Integer __v;
    private String _id;
    private CategoryModel category;



    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public ArrayList<Media> getMedia() {
        if (media instanceof LinkedTreeMap) {
            ArrayList<Media> m = new ArrayList<>();
            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) media;
            Media me = new Media();
            if (linkedTreeMap.containsKey("_id")) {
                me.set_id(linkedTreeMap.get("_id").toString());
            }
            me.setFileName(linkedTreeMap.get("fileName").toString());
            me.setMediaType(linkedTreeMap.get("mediaType").toString());
            m.add(me);
            return m;
        }else if (media instanceof ArrayList) {
            ArrayList<LinkedTreeMap> m = (ArrayList<LinkedTreeMap>) media;
            ArrayList<Media> ml = new ArrayList<>();
            for (LinkedTreeMap linkedTreeMap : m) {
                Media me = new Media();
                if (linkedTreeMap.containsKey("_id")) {
                    me.set_id(linkedTreeMap.get("_id").toString());
                }
                me.setFileName(linkedTreeMap.get("fileName").toString());
                me.setMediaType(linkedTreeMap.get("mediaType").toString());
                ml.add(me);
            }
            return ml;
        }
        return new ArrayList<Media>();
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
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

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getNumOffers() {
        return numOffers;
    }

    public void setNumOffers(Integer numOffers) {
        this.numOffers = numOffers;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Object getOfferSent() {
        return offerSent;
    }

    public void setOfferSent(Object offerSent) {
        this.offerSent = offerSent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer get__v() {
        return __v;
    }

    public void set__v(Integer __v) {
        this.__v = __v;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }


//    public Media getMedia() {
//
//        String strFileName = "";
//        String strMediaType = "";
//
//        if (media instanceof LinkedTreeMap) {
//            strFileName = (String)((LinkedTreeMap) media).get("fileName");
//            strMediaType = (String)((LinkedTreeMap) media).get("mediaType");
//        }
//        else if (media instanceof ArrayList) {
//            if (((ArrayList) media).size() > 0) {
//                strFileName = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("fileName");
//                strMediaType = (String)((LinkedTreeMap) ((ArrayList) media).get(0)).get("mediaType");
//            }
//        }
//
//        return new Media(strFileName, strMediaType);
//    }

//    public void setMedia(Object media) { this.media = media; }
}
