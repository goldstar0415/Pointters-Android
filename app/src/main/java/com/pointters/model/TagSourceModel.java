package com.pointters.model;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by mac on 12/8/17.
 */

public class TagSourceModel {

    private String firstName;
    private String lastName;
    private String description;
    private String profilePic;
    private Object location;


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

    public void setLocation(Object location) { this.location = location; }
}
