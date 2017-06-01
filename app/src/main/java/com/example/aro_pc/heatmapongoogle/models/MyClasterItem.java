package com.example.aro_pc.heatmapongoogle.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Aro-PC on 4/18/2017.
 */

public class MyClasterItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle ;
    private String mSnippet ;

    public MyClasterItem(LatLng mPosition) {
        this.mPosition = mPosition;
        mTitle = null;
        mSnippet = null;
    }

    public MyClasterItem(LatLng mPosition, String mTitle, String mSnippet) {
        this.mPosition = mPosition;
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
