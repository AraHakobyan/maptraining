package com.example.aro_pc.heatmapongoogle.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aro_pc.heatmapongoogle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class FragmentLayerdMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private List<LatLng> latlng;
    private HeatmapTileProvider mProvider;
    private TileOverlay tileOverlay;

    private static FragmentLayerdMap instance = null;

    private FragmentLayerdMap() {
    }

    public static FragmentLayerdMap getInstance() {
        if (instance == null) {
            instance = new FragmentLayerdMap();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latlng = new ArrayList<>();
        addLayerdHeatMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layerd_map, container, false);
        initUi(view);
        initMap();
        return view;
    }

    private void initMap() {
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    private void initUi(View view) {
        mapView = (MapView) view.findViewById(R.id.map_view_layerd_map);
    }

    CameraPosition cameraPosition;

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        googleMap.setOnMapClickListener(this);
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        LatLng sydney = new LatLng(40.1792, 44.4991);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity().getApplicationContext(),R.raw.map_white_stile));

        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(40.1792, 44.4991))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();


    }

    private void addLayerdHeatMap(){
        latlng.add(new LatLng(40.1835327,44.5112699 ));
        latlng.add(new LatLng(40.1835327,44.5112699));
        latlng.add(new LatLng(40.1835327,44.5112699));
        mProvider = new HeatmapTileProvider.Builder().data(latlng).build();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if(cameraPosition == null){
            cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17)
                    .bearing(90)
                    .tilt(30)
                    .build();
        }
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onMyLocationChange(Location location) {
        float bearing = location.getBearing() + 30;
           cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(bearing)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}
