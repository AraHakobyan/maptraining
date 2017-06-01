package com.example.aro_pc.heatmapongoogle.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aro_pc.heatmapongoogle.R;
import com.example.aro_pc.heatmapongoogle.listeners.ReadLocationsFromeFile;
import com.example.aro_pc.heatmapongoogle.models.LatLngModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Aro-PC on 5/22/2017.
 */

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {


    private MapView mapView;
    private GoogleMap googleMap;
    ArrayList<MarkerOptions> markerOptions;
    LatLng lastLatLong;

    public void setLatLngs(String json) {
        if (googleMap != null) {
            getLatLngFromeJson(json);
            googleMap.addMarker(new MarkerOptions().position(lastLatLong));
            PolylineOptions polylineOptions1 = new PolylineOptions();
            polylineOptions1.zIndex(10);
            polylineOptions1.add(lastPos);
            polylineOptions1.add(lastLatLong);
            polylineOptions1.color(R.color.colorPrimaryDark);
            googleMap.addPolyline(polylineOptions1);
            lastPos = lastLatLong;
        }

    }

    ArrayList<LatLng> latLngs;
    ReadLocationsFromeFile readLocationsFromeFile;


    public MapFragment(String json, ReadLocationsFromeFile readLocationsFromeFile) {

        this.readLocationsFromeFile = readLocationsFromeFile;
        markerOptions = new ArrayList<>();

        getLatLngFromeJson(json);


    }

    private void getLatLngFromeJson(String json) {

        Gson gson = new Gson();
        latLngs = new ArrayList<>();
        markerOptions = new ArrayList<>();

        Type type = new TypeToken<ArrayList<LatLngModel>>() {
        }.getType();
        ArrayList<LatLngModel> latLngModels1 = new ArrayList<>();
        latLngModels1 = gson.fromJson(json, type);

        for (LatLngModel latLngModel : latLngModels1) {
            LatLng latLng = new LatLng(latLngModel.getLat(), latLngModel.getLng());
            latLngs.add(latLng);
            markerOptions.add(new MarkerOptions().position(latLng));
        }

        lastLatLong = latLngs.get(latLngModels1.size() - 1);


    }

    private void showOnMap(ArrayList<LatLng> latLngs) {

    }

    private ArrayList<LatLng> createLatLong(String mLocations) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        String[] parts = mLocations.split("\\*\\*\\*");


        Gson gson = new Gson();
        String json = gson.toJson(mLocations);


        return latLngs;
    }

    Handler handler;
    Runnable runnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                readLocationsFromeFile.readLocationsFromeFile(true);
                handler.postDelayed(this, 500);
            }
        };
        handler.post(runnable);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initUi(view);

        return view;
    }

    private void initUi(View view) {

        mapView = (MapView) view.findViewById(R.id.map_view_history);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setMarkers();
    }

    LatLng lastPos;

    private void setMarkers() {
        googleMap.clear();
        lastPos = latLngs.get(0);
        for (LatLng latLng : latLngs) {

            googleMap.addMarker(new MarkerOptions().position(latLng));
            PolylineOptions polylineOptions1 = new PolylineOptions();
            polylineOptions1.zIndex(10);
            polylineOptions1.add(lastPos);
            polylineOptions1.add(latLng);
            polylineOptions1.color(R.color.colorPrimaryDark);
            googleMap.addPolyline(polylineOptions1);
            lastPos = latLng;
        }

    }

    public void clearMap(){
        googleMap.clear();
        latLngs.clear();
        markerOptions.clear();
    }


}
