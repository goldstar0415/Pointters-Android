package com.pointters.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mac on 12/10/17.
 */

public class GeoJsonModel implements Serializable{

    private String type;
    private ArrayList<Double> coordinates;


    public ArrayList<Double> getCoordinates() { return coordinates; }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
