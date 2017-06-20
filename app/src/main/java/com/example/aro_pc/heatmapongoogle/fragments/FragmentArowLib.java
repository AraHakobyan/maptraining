package com.example.aro_pc.heatmapongoogle.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aro_pc.heatmapongoogle.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class FragmentArowLib extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap googleMap;
    private MapView mapView;
    private static FragmentArowLib instance = null;
    private Polyline polyline;

    public FragmentArowLib() {
    }

    public static FragmentArowLib getInstance() {
        if (instance == null) {
            instance = new FragmentArowLib();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__mind__map, container, false);
        initMapView(view);
        return view;
    }

    private void createPolyline() {
        polyline = googleMap.addPolyline(new PolylineOptions().width(8).color(Color.GRAY));
    }

    private void initMapView(View view) {
        mapView = (MapView) view.findViewById(R.id.map_view_mind_map);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        createPolyline();




    }
    @Override
    public void onMapClick(LatLng latLng) {

    }



}
