package com.pointters.model.response;

/**
 * Created by AnilJha on 8/8/17.
 */

public class LongitudeLatitudeObject {

    private Double latitude;
    private Double longitude;

    public LongitudeLatitudeObject(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
