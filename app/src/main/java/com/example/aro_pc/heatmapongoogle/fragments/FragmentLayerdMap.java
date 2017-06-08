package com.example.aro_pc.heatmapongoogle.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.aro_pc.heatmapongoogle.Consts;
import com.example.aro_pc.heatmapongoogle.R;
import com.example.aro_pc.heatmapongoogle.helper.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class FragmentLayerdMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMapLongClickListener, View.OnTouchListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private List<LatLng> latlng;
    private HeatmapTileProvider mProvider;
    private TileOverlay tileOverlay;
    private ArrayList<LatLng> markerPoints;
    private CameraPosition cameraPosition;


    private static FragmentLayerdMap instance = null;

    private FragmentLayerdMap() {
    }

    public static synchronized FragmentLayerdMap getInstance() {
        if (instance == null) {
            instance = new FragmentLayerdMap();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latlng = new ArrayList<>();
        markerPoints = new ArrayList<>();
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

        mapView.setOnTouchListener(this);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Consts.LOG_MAP_TOUCH,"map is clicked");
            }
        });


    }


    Marker marker;

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        MapHelper.getInstance().setMap(googleMap);

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        LatLng sydney = new LatLng(40.1792, 44.4991);

        tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity().getApplicationContext(), R.raw.map_white_stile));

        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(40.1792, 44.4991))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(1)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();

    }

    private void addLayerdHeatMap() {
        latlng.add(new LatLng(40.1835327, 44.5112699));
        latlng.add(new LatLng(40.1835327, 44.5112699));
        latlng.add(new LatLng(40.1835327, 44.5112699));
        mProvider = new HeatmapTileProvider.Builder().data(latlng).build();
    }


    @Override
    public void onMapClick(LatLng latLng) {


//        cameraPosition = new CameraPosition.Builder()
//                .target(latLng)
//                .zoom(10)
////                .bearing(90)
////                .tilt(30)
//                .build();

//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        MapHelper.getInstance().animateRoad(true);
//        MapHelper.getInstance().drawRoad(latLng);
//        MapHelper.getInstance().animateMarker(true);

        drawCanvas(latLng);

    }

    private void drawCanvas(LatLng latLng) {
//        Point point = googleMap.getProjection().toScreenLocation(latLng);

//        Canvas canvas = new Canvas(Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Bitmap.Config.ARGB_8888));
//        canvas.drawColor(Color.BLACK);
//        Paint innerCirclePaint;

//        innerCirclePaint = new Paint();
//        innerCirclePaint.setColor(Color.BLUE);
//        innerCirclePaint.setAlpha(25);
//        innerCirclePaint.setAntiAlias(true);

//        innerCirclePaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(point.x, point.y, 1000, innerCirclePaint);

//        mapView.draw(canvas);

//        Matrix matrix = new Matrix();
//        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
////Set to UnAutoScale
//        bfoOptions.inScaled = false;
////Fit to your size
//        matrix.setScale(50, 50);
//        Bitmap tempBitmap = BitmapFactory.decodeResource(R.drawable.ic_car_icon, bfoOptions);
//        Bitmap outputbitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);

    }


    @Override
    public void onMyLocationChange(Location location) {
//        float bearing = location.getBearing() + 30;
//        cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
//                .zoom(17)                   // Sets the zoom
//                .bearing(bearing)                // Sets the orientation of the camera to east
//                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                .build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MapHelper.getInstance().removeHandlers();
        MapHelper.getInstance().removePolyline();


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(Consts.LOG_MAP_TOUCH,"map is touched");
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        mapView.invalidate();
        return true;
    }

}

