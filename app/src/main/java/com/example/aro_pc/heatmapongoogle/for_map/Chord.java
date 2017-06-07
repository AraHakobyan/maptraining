package com.example.aro_pc.heatmapongoogle.for_map;

import android.animation.AnimatorSet;
import android.graphics.Color;
import android.util.Log;

import com.example.aro_pc.heatmapongoogle.Consts;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

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
    LatLng center;


    public Chord(int a, GoogleMap mMap,LatLng center) {
        this.center = center;
        this.a = a;
        this.mMap = mMap;
       // calculateArc();
    }

    public void calculateArc() {
       // 15 * r * r = 16 * 4 * a * a;
       // r * r = 16 * a * a;
        double x = 64 * a * a / 15;
        r = (int) sqrt(x);
        arc = (int) ( PI * r / 180);
        alfa = (int) sin(a/r);


    }

    public ArrayList<LatLng> makeCircle(LatLng centre, double radius)
    {
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        double EARTH_RADIUS = 6378100.0;
        // Convert to radians.
        double lat = centre.latitude * Math.PI / 180.0;
        double lon = centre.longitude * Math.PI / 180.0;

        for (double t = 0; t <= Math.PI * 2; t += 0.01)
        {
            // y
            double latPoint = lat + (radius / EARTH_RADIUS) * Math.sin(t);
            // x
            double lonPoint = lon + (radius / EARTH_RADIUS) * Math.cos(t) / Math.cos(lat);

            // saving the location on circle as a LatLng point
            LatLng point =new LatLng(latPoint * 180.0 / Math.PI, lonPoint * 180.0 / Math.PI);

           // mMap.addMarker(new MarkerOptions().position(point));

            // now here note that same point(lat/lng) is used for marker as well as saved in the ArrayList
            points.add(point);

        }

        return points;
    }

    public ArrayList<LatLng> makeCircleBest(LatLng centre, double radius)
    {
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        double EARTH_RADIUS = 6378100.0;
        // Convert to radians.
        double lat = centre.latitude * Math.PI / 180.0;
        double lon = centre.longitude * Math.PI / 180.0;

        for (double t = 0; t <= Math.PI * 2; t += 0.0001)
        {
            // y
            double latPoint = lat + (radius / EARTH_RADIUS) * Math.sin(t);
            // x
            double lonPoint = lon + (radius / EARTH_RADIUS) * Math.cos(t) / Math.cos(lat);

            // saving the location on circle as a LatLng point
            LatLng point =new LatLng(latPoint * 180.0 / Math.PI, lonPoint * 180.0 / Math.PI);

            // mMap.addMarker(new MarkerOptions().position(point));

            // now here note that same point(lat/lng) is used for marker as well as saved in the ArrayList
            points.add(point);

        }

        return points;
    }

    public LatLng getCenterPoint(LatLng point, double radius){
        double EARTH_RADIUS = 6378100.0;
        double t = 0.9 ;
        LatLng center = null;
        double lat = Math.PI * point.latitude/180 - (radius / EARTH_RADIUS) * sin(t) ;
        double lon = Math.PI * point.longitude/180 - (radius / EARTH_RADIUS) * cos(t) / cos(lat) ;

        double centerLatitude = 180.0 * lat / Math.PI;
        double centerLongitude = 180.0 * lon / Math.PI;

        center = new LatLng(centerLatitude,centerLongitude);

        return center;

    }

    public void drawChort(){
        LatLng center = mMap.getCameraPosition().target;
        ArrayList<LatLng> circulePoints = new ArrayList<>();
        //drawCircle(center);

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

//    public LatLng calculateCenter(LatLng start,int R,int beta){
//        double dx = R*cos(beta);
//
//        double dy = R*sin(beta);
//
//        double delta_longitude = dx/(111320*cos(latitude));
//
//        double delta_latitude = dy/110540;
//
//        double longitude = start.longitude + delta_longitude;
//
//        double latitude = start.latitude + delta_latitude;
//    }

    public LatLng movePoint(double latitude, double longitude, double distanceInMetres, double bearing) {
      //  calculateArc();
        bearing = 90-alfa;
        double brngRad = toRadians(30);
        double latRad = toRadians(latitude);
        double lonRad = toRadians(longitude);
        int earthRadiusInMetres = 6371000;
        double distFrac = distanceInMetres / earthRadiusInMetres;

        double latitudeResult = asin(sin(latRad) * cos(distFrac) + cos(latRad) * sin(distFrac) * cos(brngRad));
        double a = atan2(sin(brngRad) * sin(distFrac) * cos(latRad), cos(distFrac) - sin(latRad) * sin(latitudeResult));
        double longitudeResult = (lonRad + a + 3 * PI) % (2 * PI) - PI;

        Log.d(Consts.LOG_MAP_HELPER,"latitude: " + toDegrees(latitudeResult) + ", longitude: " + toDegrees(longitudeResult));
        return new LatLng(toDegrees(latitudeResult),toDegrees(longitudeResult));

    }
    ArrayList<LatLng> alLatLng;

    public void getIntersaction(LatLng startPos, LatLng endPos){
        alLatLng = new ArrayList<>();
        double cLat = ((startPos.latitude + endPos.latitude) / 2);
        double cLon = ((startPos.longitude + endPos.longitude) / 2);

        double d = Math.abs(startPos.longitude - endPos.longitude);

        if( d < 0.0001){
            cLon -= d/4;
        } else {
            cLat += d/4;
        }

        double tDelta = 1.0/5000;
        for (double t = 0;  t <= 1.0; t+=tDelta) {
            double oneMinusT = (1.0-t);
            double t2 = Math.pow(t, 2);
            double lon = oneMinusT * oneMinusT * startPos.longitude
                    + 2 * oneMinusT * t * cLon
                    + t2 * endPos.longitude;
            double lat = oneMinusT * oneMinusT * startPos.latitude
                    + 2 * oneMinusT * t * cLat
                    + t2 * endPos.latitude;
            alLatLng.add(new LatLng(lat, lon));
        }

        // draw polyline
        PolylineOptions line = new PolylineOptions();
        line.width(14);
        line.color(Color.RED);
        line.addAll(alLatLng);
        Log.d(Consts.LOG_MAP_HELPER, String.valueOf(alLatLng.size()));
        animateLine();
//        mMap.addPolyline(line);
    }

    AnimatorSet animatorSet;

    private void animateLine() {
        MapAnimator.getInstance().setDuration(2000);
      //  MapAnimator.getInstance().animateLine(mMap, alLatLng);
//        getCenterPoint()
    }

}
