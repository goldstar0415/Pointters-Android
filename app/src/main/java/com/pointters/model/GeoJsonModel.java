package com.pointters.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by mac on 12/10/17.
 */

public class GeoJsonModel {

    private ArrayList<Double> coordinates;


    public ArrayList<Double> getCoordinates() { return coordinates; }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }
}
