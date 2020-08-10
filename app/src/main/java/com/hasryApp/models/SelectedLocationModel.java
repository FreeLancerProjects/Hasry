package com.hasryApp.models;

import java.io.Serializable;

public class SelectedLocationModel implements Serializable {
    private double lat;
    private double lng;
    private String address;

    public SelectedLocationModel(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }
}
