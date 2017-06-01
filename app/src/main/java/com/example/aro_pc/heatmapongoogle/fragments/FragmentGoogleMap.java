package com.example.aro_pc.heatmapongoogle.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aro_pc.heatmapongoogle.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentGoogleMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMapLongClickListener {

    public static final String GMAPTAG = "log";
    public static final int ANIMATEROAD = 1;
    public static final int NOANIMATEROAD = 0;
    MapView mapView;
    GoogleMap googleMap;
    Canvas canvas;
    private Handler handler;
    private Runnable runnable;

    private static FragmentGoogleMap instance = null;

    private FragmentGoogleMap() {
        latLngs = new ArrayList<>();
        latLngs.add(new LatLng(40.1965, 44.485));
        latLngs.add(new LatLng(40.1966, 44.487));
        latLngs.add(new LatLng(40.1967, 44.5));
        latLngs.add(new LatLng(40.1968, 44.51));
        latLngs.add(new LatLng(40.1969, 44.52));
        latLngs.add(new LatLng(40.1970, 44.53));
        latLngs.add(new LatLng(40.1972, 44.535));
        latLngs.add(new LatLng(40.1974, 44.540));
        latLngs.add(new LatLng(40.1976, 44.545));
        latLngs.add(new LatLng(40.1980, 44.550));
        latLngs.add(new LatLng(40.1984, 44.555));
        latLngs.add(new LatLng(40.1988, 44.560));
        latLngs.add(new LatLng(40.1992, 44.565));
        latLngs.add(new LatLng(40.1996, 44.570));
        latLngs.add(new LatLng(40.2, 44.575));
        handler = new Handler();
        Log.d(GMAPTAG,"message frome google map fragment's Constructor");




    }

    private boolean isFirstTime = true;
    private ArrayList<LatLng> latLngs;

    public static FragmentGoogleMap getInstance() {
        if (instance == null) {
            instance = new FragmentGoogleMap();
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
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);
        initUi(view);
        initMap(view);
        return view;
    }

    private void initMap(View view) {
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);

    }


    private void initUi(View view) {
        mapView = (MapView) view.findViewById(R.id.map_view_google_map);
    }



    private long animateTime = 10;
    private boolean isLastPosition = false;
    private Runnable runnableWithAnimation;
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Bitmap bitmap = null;

        this.googleMap = googleMap;
        final int[] i = {1};

        runnable = new Runnable() {
            @Override
            public void run() {


                    addPosition(latLngs.get(i[0]),NOANIMATEROAD);



                if(i[0] < latLngs.size()-1){
                    animateTime = animateTime > 1000 ? 1000 : animateTime;
                    handler.postDelayed(this, animateTime);
                    i[0]++;
                }



            }
        };

        runnableWithAnimation = new Runnable() {
            @Override
            public void run() {
                addPosition(latLngs.get(i[0]),ANIMATEROAD);
                if(i[0] < latLngs.size()-1){
                    handler.postDelayed(this, animateTime);
                    i[0]++;
                }
            }
        };



        handler.postDelayed(runnable,2000);


        canvas = new Canvas();
        canvas.setMatrix(mapView.getMatrix());
        canvas.drawColor(getResources().getColor(R.color.colorPrimaryDark));
        getView().draw(canvas);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setOnMapLongClickListener(this);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        showCanvas(bitmap);
                        getCurrentLocation();
                    }
                });
            }
        });

//        addPosition(latLngs.get(0));
//        addPosition(latLngs.get(6));
//        addPosition(new LatLng(40.2,44.5));



    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    private void showCanvas(Bitmap bitmap) {
        canvas.setBitmap(bitmap);
        getView().draw(canvas);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        latLngs.add(latLng);
        drawRoad(latLng);
    }

    @Override
    public void onMyLocationChange(Location location) {


    }


    private LatLng latLng1;
    private boolean isFirstTimeAnimate = true;
    private void addPosition(LatLng latLng,int animate) {
        if (isFirstTime){
            latLng1 = latLngs.get(0);
            isFirstTime = false;
        }

        switch (animate){
            case 0:
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(latLng1);
                polylineOptions.zIndex(10);
                polylineOptions.add(latLng);
                polylineOptions.color(R.color.colorPrimaryDark);
                googleMap.addPolyline(polylineOptions);
                latLng1 = latLng;
                break;
            case 1:

                if (isFirstTimeAnimate){
                    latLng1 = latLng;
                    isFirstTimeAnimate = false;
                    break;
                }
                PolylineOptions polylineOptions1 = new PolylineOptions();
                polylineOptions1.zIndex(10);
                polylineOptions1.add(latLng);
                polylineOptions1.add(latLng1);
                polylineOptions1.color(R.color.colorPrimaryDark);
                googleMap.addPolyline(polylineOptions1);
                latLng1 = latLng;


                break;
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        isFirstTimeAnimate = true;
        animateRoad();

    }

    private void animateRoad() {
        handler.post(runnableWithAnimation);
    }

    private void drawRoad(LatLng latLng){
        String url = getUrl(latLng1, latLng);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        latLng1 = latLng;

    }
    private String getUrl(LatLng origin,LatLng dest){


        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }


    private class FetchUrl extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        public ArrayList<LatLng> getPoints() {
            return points;
        }

        ArrayList<LatLng> points;
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            long positionCount = 0;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    latLngs.add(position);
                    positionCount++;
                }
                animateTime = 1000/positionCount;



                // Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(10);
//                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
//            if(lineOptions != null) {
//                mMap.addPolyline(lineOptions);
//            }
//            else {
//                Log.d("onPostExecute","without Polylines drawn");
//            }
        }
    }

    public class DataParser {

        /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
        public List<List<HashMap<String,String>>> parse(JSONObject jObject){

            List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for(int i=0;i<jRoutes.length();i++){
                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();

                    /** Traversing all legs */
                    for(int j=0;j<jLegs.length();j++){
                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for(int k=0;k<jSteps.length();k++){
                            String polyline = "";
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for(int l=0;l<list.size();l++){
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude) );
                                hm.put("lng", Double.toString((list.get(l)).longitude) );
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
            }


            return routes;
        }


        /**
         * Method to decode polyline points
         * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         * */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

}
