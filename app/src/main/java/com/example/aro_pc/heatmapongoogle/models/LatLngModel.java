package com.example.aro_pc.heatmapongoogle.models;

/**
 * Created by Aro-PC on 5/22/2017.
 */

public class LatLngModel {
    double lat;
    double lng;

    public LatLngModel(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }


    public double getLng() {
        return lng;
    }

}
