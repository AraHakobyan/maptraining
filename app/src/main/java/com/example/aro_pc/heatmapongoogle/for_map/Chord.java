package com.example.aro_pc.heatmapongoogle.for_map;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by Aro-PC on 6/5/2017.
 */

public class Chord {
    int r;
    int h = r / 4;
    int alfa;
    int arc;
    int a;
    GoogleMap mMap;


    public Chord(int a, GoogleMap mMap) {
        this.a = a;
        this.mMap = mMap;
    }

    public void calculateArc() {
       // 15 * r * r = 16 * 4 * a * a;
       // r * r = 16 * a * a;
        double x = 64 * a * a / 15;
        r = (int) sqrt(x);
        arc = (int) ( PI * r / 180);
        alfa = (int) sin(a/r);


    }

    private ArrayList<LatLng> makeCircle(LatLng centre, double radius)
    {
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        double EARTH_RADIUS = 6378100.0;
        // Convert to radians.
        double lat = centre.latitude * Math.PI / 180.0;
        double lon = centre.longitude * Math.PI / 180.0;

        for (double t = 0; t <= Math.PI * 2; t += 0.3)
        {
            // y
            double latPoint = lat + (radius / EARTH_RADIUS) * Math.sin(t);
            // x
            double lonPoint = lon + (radius / EARTH_RADIUS) * Math.cos(t) / Math.cos(lat);

            // saving the location on circle as a LatLng point
            LatLng point =new LatLng(latPoint * 180.0 / Math.PI, lonPoint * 180.0 / Math.PI);

            // now here note that same point(lat/lng) is used for marker as well as saved in the ArrayList
            points.add(point);

        }

        return points;
    }

    public void drawChort(){
        LatLng center = mMap.getCameraPosition().target;
        ArrayList<LatLng> circulePoints = new ArrayList<>();
        drawCircle(center);

    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }

}
