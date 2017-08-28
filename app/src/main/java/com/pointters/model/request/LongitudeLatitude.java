package com.pointters.model.request;

/**
 * Created by prashantkumar on 17/8/17.
 */

public class LongitudeLatitude {
    private Double longitude;
    private Double latitude;

    public LongitudeLatitude(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
