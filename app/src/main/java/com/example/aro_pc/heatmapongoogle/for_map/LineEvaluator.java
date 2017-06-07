package com.example.aro_pc.heatmapongoogle.for_map;

import android.animation.TypeEvaluator;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aro-PC on 6/6/2017.
 */

public class LineEvaluator implements TypeEvaluator<LatLng> {


    @Override
    public LatLng evaluate(float fraction, LatLng startPos, LatLng endPos) {
        double lon = 0;
        double lat = 0;
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
             lon = oneMinusT * oneMinusT * startPos.longitude
                    + 2 * oneMinusT * t * cLon
                    + t2 * endPos.longitude;
             lat = oneMinusT * oneMinusT * startPos.latitude
                    + 2 * oneMinusT * t * cLat
                    + t2 * endPos.latitude;
        }
        return new LatLng(lat,lon);
    }
}
