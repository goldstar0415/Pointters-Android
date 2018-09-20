package com.pointters.model;

import android.location.LocationManager;

import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.utils.L;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by prashantkumar on 8/11/17.
 */

public class Service {

    private String id;
    private String description;
    private Object media;
    private Object location;
    private List<Prices> prices;
    private FulfillmentMethodForCustom1 fulfillmentMethod;
    private boolean promoted;
    private Integer pointValue;
    private Integer numOrders;
    private float avgRating;
    private UserModel seller;
    private CategoryModel category;
    private String tagline;

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

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
            if ( ((ArrayList<LinkedTreeMap>) location).size() > 0) {
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
            }else{
                return null;
            }
        }
        return null;
    }

    public void setLocation(ArrayList<LocationModel> location) {
        this.location = location;
    }

    public List<Prices> getPrices() {
        return prices;
    }

    public void setPrices(List<Prices> prices) {
        this.prices = prices;
    }

    public FulfillmentMethodForCustom1 getFulfillmentMethod() {
        return fulfillmentMethod;
    }

    public void setFulfillmentMethod(FulfillmentMethodForCustom1 fulfillmentMethod) {
        this.fulfillmentMethod = fulfillmentMethod;
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

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public UserModel getSeller() {
        return seller;
    }

    public void setSeller(UserModel seller) {
        this.seller = seller;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
}
