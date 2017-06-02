package com.example.aro_pc.heatmapongoogle.helper;

import com.example.aro_pc.heatmapongoogle.for_map.DrawRoad;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aro-PC on 6/2/2017.
 */

public class MapHelper {

    private static MapHelper instance;
    private GoogleMap map;
    private DrawRoad mDrawRoad;

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void drawRoad(LatLng latLng){
//        if(map != null)
        mDrawRoad.setGoogleMap(map);
        mDrawRoad.draw(latLng);
//        else Log.d(Consts.LOG_MAP_HELPER,"map is null");
    }



    public void animateRoad(boolean isAnimatedRoad){
        mDrawRoad.setAnimatedRoad(isAnimatedRoad);
    }

    public void animateMarker(boolean isAnimatedMarker){
        mDrawRoad.setAnimatedMarker(isAnimatedMarker);
    }

    private MapHelper() {
        mDrawRoad = new DrawRoad();
    }

    public static synchronized MapHelper getInstance(){
           if (instance == null){
               instance = new MapHelper();
           }
       return instance;
    }
}
