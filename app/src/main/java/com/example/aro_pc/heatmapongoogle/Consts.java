package com.example.aro_pc.heatmapongoogle;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aro-PC on 5/2/2017.
 */

public  class Consts {
    public static final String LOG_GOOGLE_MAP_CLICKED = "GOOGLE MAP" ;
    public static final String LOG_MSG_GOOGLE_MAP_CLICKED = "Map menu item has been clicked frome navigation view" ;
    public static final String LOG_MAP_HELPER = "mapHelper";
    public static final String LOG_MAP_TOUCH = "mapTouch";


    public static final String TORIGHT = "toRight";
    public static final String TOLEFT = "toLeft";


    public static final int GREY_900 = Color.parseColor("#212121");
    public static final LatLng YEREVAN_KENTRON = new LatLng(40.1792461,44.5095374);
    public static final LatLng ABOVYAN_KINO = new LatLng(40.2715286,44.633383);
    public static final LatLng CHARENTSAVAN_KINO = new LatLng(40.4018115,44.6433958);

}
