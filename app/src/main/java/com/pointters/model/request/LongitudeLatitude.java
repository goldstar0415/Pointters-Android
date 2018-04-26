package com.pointters.model.request;

import java.io.Serializable;
import java.util.List;

/**
 * Created by prashantkumar on 17/8/17.
 */

public class LongitudeLatitude implements Serializable{

    private List<Double> coordinates;
    private String type;


    public LongitudeLatitude(List<Double> coordinates, String type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
