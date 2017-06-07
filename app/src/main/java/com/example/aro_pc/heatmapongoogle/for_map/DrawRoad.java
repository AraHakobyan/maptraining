package com.example.aro_pc.heatmapongoogle.for_map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

/**
 * Created by Aro-PC on 6/2/2017.
 */

public class DrawRoad {

    private boolean isAnimatedRoad = false;
    private boolean isAnimatedMarker = false;
    private GoogleMap googleMap;
    ArrayList<LatLng> markerPoints;
    LatLng startPos;
    LatLng endPos;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private Context context;

    public DrawRoad() {
        markerPoints = new ArrayList<>();
        // this.context = context;

    }


    public void draw(LatLng latLng) {

        if (markerPoints.size() > 1) {
            markerPoints.clear();
            googleMap.clear();
        }

        markerPoints.add(latLng);
        MarkerOptions options = new MarkerOptions();

        options.position(latLng);

        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        googleMap.addMarker(options);

        if (markerPoints.size() >= 2) {
            startPos = markerPoints.get(0);
            endPos = markerPoints.get(1);

            String url = getDirectionsUrl(startPos, endPos);

            DownloadTask downloadTask = new DownloadTask();

            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

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

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void removeHandlers() {
        handler.removeCallbacks(runnable);
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(2);
//                lineOptions.color(Color.RED);
            }

//            Log.d(Consts.LOG_MAP_HELPER, String.valueOf(points.size()));

            if (isAnimatedRoad && !isAnimatedMarker) {
                animatedRoad(points, 1);

            } else if (isAnimatedRoad && isAnimatedMarker) {

                animatedRoad(points, 2);
            } else if (!isAnimatedMarker && !isAnimatedRoad) {

                animatedRoad(points, 0);

            }
        }
    }

    public void setAnimatedRoad(boolean animated) {
        isAnimatedRoad = animated;
    }

    public void setAnimatedMarker(boolean isAnimatedMarker) {
        this.isAnimatedMarker = isAnimatedMarker;
    }

    private Handler handler = new Handler();
    private Runnable runnable;

    private int calculateAnimateTime(int size) {
        if (size / 400 > 1) {
            int x = size / 400;


            return size / 400;
        } else {
            return 1;
        }
    }

    private String strColor = "#95000000";
    boolean t = true;

    public void animatedRoad(final ArrayList<LatLng> points, int c) {
        c = 3;
        // ArrayList<ArrayList<LatLng>> matric = divArray(points);


        final int deltaLength = calculateAnimateTime(points.size());

        switch (c) {
            case 0:
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(points);
                lineOptions.width(6);
                lineOptions.color(Color.RED);
                googleMap.addPolyline(lineOptions);
                break;
            case 1:
                final int[] i = {0};
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        PolylineOptions lineOptions = new PolylineOptions();
                        lineOptions.add(points.get(i[0] + deltaLength - 1), points.get(i[0] + deltaLength));
                        lineOptions.width(14);
                        lineOptions.color(Color.RED);
                        googleMap.addPolyline(lineOptions);
                        i[0] = i[0] + deltaLength;
                        if (i[0] >= points.size() - 1 - deltaLength) {
                            handler.removeCallbacks(this);
                        } else
                            handler.postDelayed(this, (int) 400 / points.size());
                    }
                });
                break;
            case 2:

//                final Marker marker;
//                marker = googleMap.addMarker(new MarkerOptions().position(points.get(0))
//                        .title("Marker in Erevan"));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : points) {
                    builder.include(latLng);
                }
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

                final int[] k = {0};

                //  drawRoad(k, points, deltaLength);
                startAnim(points);
//                 int a = (int) distance(points.get(0).latitude,points.get(0).longitude,points.get(points.size()-1).latitude,points.get(points.size()-1).longitude);
                //  Chord chord = new Chord(a,googleMap);
                //  chord.drawChort();

//                drawRoute(k,points,deltaLength);

//                handler.post(runnable);
                break;
            case 3:
                LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                builder1.include(points.get(0));
                builder1.include(points.get(points.size() - 1));
                LatLngBounds bounds1 = builder1.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds1, 200));

                PolylineOptions lineOptions1 = new PolylineOptions();
                lineOptions1.add(startPos, endPos);
                lineOptions1.width(10);
                lineOptions1.color(Color.parseColor("#20000000"));
                googleMap.addPolyline(lineOptions1);

                LatLng center = new LatLng((startPos.latitude + endPos.latitude) / 2, (startPos.longitude + endPos.longitude) / 2);
                double a1 = distance(startPos.latitude, startPos.longitude, endPos.latitude, endPos.longitude);
                Chord chord = new Chord((int) a1, googleMap, center);
                chord.getIntersaction(startPos, endPos);

                //draw circules and all you want with map ;)

                LatLng centerPos1 = chord.getCenterPoint(startPos, a1);

                googleMap.addMarker(new MarkerOptions().position(centerPos1));

                ArrayList<LatLng> firstArrayList = chord.makeCircle(startPos, a1);

                LatLng centerPos2 = chord.getCenterPoint(endPos, a1);
                googleMap.addMarker(new MarkerOptions().position(centerPos2));
                ArrayList<LatLng> secondArrayList = chord.makeCircle(endPos, a1);
                googleMap.addPolyline(new PolylineOptions().addAll(firstArrayList));
                googleMap.addPolyline(new PolylineOptions().addAll(secondArrayList).color(Color.RED));

                ArrayList<LatLng> newCenters = new ArrayList<>();

                for (LatLng firstLatLng : firstArrayList) {
                    for (LatLng secondLatLng : secondArrayList) {
//                        if (secondLatLng == firstLatLng){
//                            newCenters.add(secondLatLng);
//                        }
                        if (Math.abs(firstLatLng.latitude - secondLatLng.latitude) < 0.001 && Math.abs(firstLatLng.longitude - secondLatLng.longitude) < 0.01) {
                            newCenters.add(firstLatLng);
                        }
                    }
                }

                googleMap.addPolyline(new PolylineOptions().addAll(newCenters).color(Color.GREEN));
                ArrayList<LatLng> partArray = new ArrayList<>();
                LatLng theCenterILookingFor = null;

                if (newCenters.get(0).latitude - newCenters.get(newCenters.size() - 1).latitude < 0) {
                    theCenterILookingFor = newCenters.get(newCenters.size()/4);
                } else {
                    theCenterILookingFor = newCenters.get(newCenters.size() - newCenters.size()/4);
                }
                ArrayList<LatLng> theardArrayList = chord.makeCircleBest(theCenterILookingFor, a1);
                googleMap.addPolyline(new PolylineOptions().addAll(theardArrayList).color(Color.BLUE));

                ArrayList<LatLng> theLine = new ArrayList<>();


                    for (LatLng therdLatLng : theardArrayList) {
//                        if (secondLatLng == firstLatLng){
//                            newCenters.add(secondLatLng);
//                        }
                        if (Math.abs(startPos.latitude - therdLatLng.latitude) < 0.001 && Math.abs(startPos.longitude - therdLatLng.longitude) < 0.01) {
                            theLine.add(therdLatLng);
                        }

                        if (Math.abs(endPos.latitude - therdLatLng.latitude) < 0.001 && Math.abs(endPos.longitude - therdLatLng.longitude) < 0.01) {
                            theLine.add(therdLatLng);
                        }
                    }

                    googleMap.addPolyline(new PolylineOptions().addAll(theLine).color(Color.YELLOW));

                    LatLng start = theLine.get(theLine.size()/4);
                    LatLng end = theLine.get(theLine.size() - theLine.size()/4);

                    int startPos = 0;
                    int endPos = 0;
                    startPos = theardArrayList.indexOf(start);
                    endPos = theardArrayList.indexOf(end);

                    List<LatLng> subString1 = new ArrayList<>();

                    if(endPos > startPos){
                    }

                    subString1 = theardArrayList.subList(startPos,endPos);

                    googleMap.addPolyline(new PolylineOptions().addAll(subString1).color(Color.CYAN));

                    //evrikaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa




                break;
        }


    }

    public float distance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    private void startAnim(final ArrayList<LatLng> points) {
        if (googleMap != null) {
            MapAnimator.getInstance().setDuration(2000);
            MapAnimator.getInstance().animateRoute(googleMap, points);
        } else {
            //Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    PolylineOptions lineOptions;
    Polyline polyline;

//    private void drawRoad(final int[] k, final ArrayList<LatLng> points, final int deltaLength) {
//
//
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                lineOptions = new PolylineOptions();
//                lineOptions.width(14);
//                lineOptions.color(Color.parseColor(strColor)).add(points.get(0));
//                polyline = googleMap.addPolyline(lineOptions);
//
//
////                if (deltaLength <= 1) {
////                    lineOptions.add(points.get(k[0] + deltaLength - 1), points.get(k[0] + deltaLength));
////                } else {
////                    for (int j = 0; j < deltaLength; j++) {
////                        lineOptions.add(points.get(k[0] + j), points.get(k[0] + j + 1));
////                    }
//////                            handler.removeCallbacks(this);
//////                            marker.setPosition(points.get(points.size() - 1));
//////                            animatedRoad(points,0);
//////                            lineOptions.addAll(points.subList(k[0] , k[0] + time-1));
////                }
//
////                        marker.setPosition(points.get(k[0] + 1));
//
//                for (int i = 0; i<points.size() ; i++){
//                    polyline.setPoints(points);
//                }
//
//                k[0] = k[0] + deltaLength;
//                if (k[0] >= points.size() - 1 - deltaLength) {
////                            marker.setVisible(false);
//                    handler.removeCallbacks(this);
//                    if (strColor.equals("#95000000")) {
//                        t = false;
//                        strColor = "#BDBDBD";
//                    } else {
//                        t = true;
//                        strColor = "#95000000";
//                    }
//                    k[0] = 0;
//                    handler.postDelayed(runnable, (int) 400 / points.size());
//
//
//                } else
//                    handler.postDelayed(this, (int) 400 / points.size());
//            }
//        };
//    }

    public void removePolyline() {
        polyline.setColor(Color.parseColor("#d5d5d5"));


    }

    AnimatorSet firstAnimationSet;
    AnimatorSet secondAnimator;

    public void drawRoute(final int[] k, final ArrayList<LatLng> points, final int deltaLength) {
        firstAnimationSet = new AnimatorSet();
//        runnable = new Runnable() {
//            @Override
//            public void run() {

        lineOptions = new PolylineOptions();
        lineOptions.width(14);
        lineOptions.color(Color.parseColor(strColor)).add(points.get(0));
        polyline = googleMap.addPolyline(lineOptions);
//                polyline.setPoints(points);


        ValueAnimator roudAnimator = ValueAnimator.ofInt(0, 100);
        roudAnimator.setDuration(10000);
        roudAnimator.setInterpolator(new FastOutLinearInInterpolator());
        roudAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                polyline.setColor(Color.RED);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        roudAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                List<LatLng> foregroundPoints = points;

                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount = foregroundPoints.size();
                int countTobeRemoved = (int) (pointcount * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
                subListTobeRemoved.clear();

                polyline.setPoints(foregroundPoints);
            }
        });


        ObjectAnimator foregroundRouteAnimator = ObjectAnimator.ofObject(this, "routeIncreaseForward", new RouteEvaluator(), points.toArray());
        foregroundRouteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        foregroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                polyline.setPoints(points);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        foregroundRouteAnimator.setDuration(2000);


        firstAnimationSet.playSequentially(foregroundRouteAnimator, roudAnimator);

        firstAnimationSet.start();
        firstAnimationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                firstAnimationSet.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
