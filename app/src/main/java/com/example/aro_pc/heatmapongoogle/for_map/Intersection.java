package com.example.aro_pc.heatmapongoogle.for_map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Aro-PC on 6/5/2017.
 */

public class Intersection {
    private LatLng startPos;
    private LatLng endPos;
    public Intersection(LatLng startPos, LatLng endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    private void drawFirstLine(GoogleMap map){
        Polyline polyline = map.addPolyline(new PolylineOptions().add(startPos));

    }
}
