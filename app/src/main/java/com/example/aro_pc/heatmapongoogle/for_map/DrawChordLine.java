package com.example.aro_pc.heatmapongoogle.for_map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aro-PC on 6/8/2017.
 */

public class DrawChordLine {
    double sugitta ;
    float radius;
    float distance;
    public void drawLine(LatLng startPos, LatLng endPos, float radiusR){

//      distance =   getDistance();
      sugitta = computeSagitta();



    }

    public double computeSagitta(){
        return (radius - (Math.sqrt((radius * radius) - (distance/2 * distance/2))));
    }
}
