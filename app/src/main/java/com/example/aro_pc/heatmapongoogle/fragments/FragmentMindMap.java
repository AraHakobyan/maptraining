package com.example.aro_pc.heatmapongoogle.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.aro_pc.heatmapongoogle.MultiDrawable;
import com.example.aro_pc.heatmapongoogle.R;
import com.example.aro_pc.heatmapongoogle.models.MyClasterItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FragmentMindMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private static FragmentMindMap instance = null;
    private ClusterManager mClusterManager;

    public FragmentMindMap() {
    }

    public static FragmentMindMap getInstance() {
        if (instance == null) {
            instance = new FragmentMindMap();
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

    private void initMapView(View view) {
        mapView = (MapView) view.findViewById(R.id.map_view_mind_map);
        mapView.onCreate(null);
        mapView.onResume();

        mapView.getMapAsync(this);
    }

    private void setUpClusterManager() {
        mClusterManager = new ClusterManager(getActivity(), googleMap);
        mClusterManager.setAlgorithm(new GridBasedAlgorithm<MyClasterItem>());
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.1835327,44.5112699),10));
        googleMap.setOnCameraIdleListener(mClusterManager);
//        mClusterManager.setAlgorithm();
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mClusterManager.clearItems();
//                mClusterManager.getClusterMarkerCollection().getMarkers().clear();
                InputStream inputStream = getResources().openRawResource(R.raw.radar_search_half);
                items.clear();
//                for (ClusterItem item : items)
//                    mClusterManager.removeItem(item);
                try {
                    items = new MyItemReader().read(inputStream);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mClusterManager.addItems(items);
                mClusterManager.setRenderer(new GGCarRenderer());

//                mClusterManager.cluster();

            }
        });
//        googleMap.setOnMarkerClickListener(mClusterManager);
//        addItems();
        fromeDemo();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        setUpClusterManager();
    }

    private void addItems() {
//        List<LatLng> latlng = new ArrayList<>();
//        latlng.add(new LatLng(40.1835327,44.5112699 ));
//        latlng.add(new LatLng(40.1835327,44.5112699));
//        latlng.add(new LatLng(40.1835327,44.5112699));
//        for (LatLng position : latlng){
//            mClusterManager.addItem(new MyClasterItem(position));
//        }
//
//        mClusterManager.setAnimation(true);
//        double lat = 51.5009;
//        double lng = -0.122;
//
//
//        String title = "This is the title" ;
//        String snippet ="add this is a snippet";
//
//// Create a cluster item for the marker and set the title and snippet using the constructor.
//        MyClasterItem infoWindowItem = new MyClasterItem(latlng.get(0), title, snippet);
//
//// Add the cluster item (marker) to the cluster manager.
//        mClusterManager.addItem(infoWindowItem);

    }

    private void fromeDemo() {
        try {
            readItems();
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Problem reading list of markers.", Toast.LENGTH_LONG).show();
        }
    }

    private List<MyClasterItem> items;

    private void readItems() throws JSONException {
        InputStream inputStream = getResources().openRawResource(R.raw.radar_search);
        items = new MyItemReader().read(inputStream);
        mClusterManager.addItems(items);
        mClusterManager.setRenderer(new GGCarRenderer());

    }

    public class MyItemReader {

        /*
         * This matches only once in whole input,
         * so Scanner.next returns whole InputStream as a String.
         * http://stackoverflow.com/a/5445161/2183804
         */
        private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";

        public List<MyClasterItem> read(InputStream inputStream) throws JSONException {
            List<MyClasterItem> items = new ArrayList<MyClasterItem>();
            String json = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
//                String title = null;
//                String snippet = null;
                JSONObject object = array.getJSONObject(i);
                double lat = object.getDouble("lat");
                double lng = object.getDouble("lng");
//                if (!object.isNull("title")) {
//                    title = object.getString("title");
//                }
//                if (!object.isNull("snippet")) {
//                    snippet = object.getString("snippet");
//                }
                items.add(new MyClasterItem(new LatLng(lat, lng)));
            }
            return items;
        }

    }

    private class GGCarRenderer extends DefaultClusterRenderer<MyClasterItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public GGCarRenderer() {
            super(getActivity(), googleMap, mClusterManager);

            View multiProfile = getInstance().getLayoutInflater(null).inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.multi_image);

            mImageView = new ImageView(getActivity());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyClasterItem person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_car_icon));
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MyClasterItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (MyClasterItem p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(R.drawable.ic_car_icon);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
