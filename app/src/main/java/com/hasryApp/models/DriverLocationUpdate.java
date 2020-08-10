package com.hasryApp.models;

import java.io.Serializable;

public class DriverLocationUpdate implements Serializable {
    private double latitude;
    private double longitude;

    public DriverLocationUpdate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
