package com.example.aro_pc.heatmapongoogle.for_map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.aro_pc.heatmapongoogle.Consts;
import com.example.aro_pc.heatmapongoogle.R;
import com.example.aro_pc.heatmapongoogle.helper.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
        // context = MapHelper.getInstance().getActivity();
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
        c = 5;
        // ArrayList<ArrayList<LatLng>> matric = divArray(points);

        LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
        builder1.include(points.get(0));
        builder1.include(points.get(points.size() - 1));
        LatLngBounds bounds1 = builder1.build();

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
                    theCenterILookingFor = newCenters.get(newCenters.size() / 4);
                } else {
                    theCenterILookingFor = newCenters.get(newCenters.size() - newCenters.size() / 4);
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

                LatLng start = theLine.get(theLine.size() / 4);
                LatLng end = theLine.get(theLine.size() - theLine.size() / 4);

                int startPos = 0;
                int endPos = 0;
                startPos = theardArrayList.indexOf(start);
                endPos = theardArrayList.indexOf(end);

                List<LatLng> subString1 = new ArrayList<>();

                if (endPos > startPos) {
                }

                subString1 = theardArrayList.subList(startPos, endPos);
//                subString1.add(0,this.endPos);
//                subString1.add(this.startPos);

                googleMap.addPolyline(new PolylineOptions().addAll(subString1).color(Consts.GREY_900).width(14));

                //evrikaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa


                startAnim(subString1);

                break;
            case 4:
                LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                builder2.include(points.get(0));
                builder2.include(points.get(points.size() - 1));
                LatLngBounds bounds2 = builder2.build();

                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds2, 200));


                final LatLng finalStartPos = this.startPos;
                final LatLng finalEndPos = this.endPos;

                final Point startPoint = googleMap.getProjection().toScreenLocation(finalStartPos);
                final Point endPoint = googleMap.getProjection().toScreenLocation(finalEndPos);
                getAllPixels(startPoint, endPoint);
                animL();
//                        animPixels(startPoint.x,endPoint.x,startPoint.y, endPoint.y);

                break;

            case 5:
                BitmapDescriptor bitmapDescriptor = vectorToBitmap(R.drawable.ic_brightness_3_black_24dp, Color.RED);
//                ImageView imageView = new ImageView(MapHelper.getInstance().getActivity());
//                imageView.setImageResource(R.drawable.ic_brightness_3_black_24dp);
//                imageView.setX(googleMap.getProjection().toScreenLocation(this.startPos).x);
//                imageView.setX(googleMap.getProjection().toScreenLocation(this.startPos).y);
//                imageView.setVisibility(View.VISIBLE);
//                GroundOverlay groundOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions().image())

                Drawable drawable = ResourcesCompat.getDrawable(MapHelper.getInstance().getActivity().getResources(), R.drawable.ic_brightness_3_black_24dp, null);
                VectorDrawable vectorDrawable = (VectorDrawable)drawable;

                int h = vectorDrawable.getIntrinsicHeight();
                int w = vectorDrawable.getIntrinsicWidth();
                vectorDrawable.setBounds(0,0,w,h);
                Bitmap bm = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);
                vectorDrawable.draw(canvas);
                BitmapDescriptor bmd =  BitmapDescriptorFactory.fromBitmap(bm);

                GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions().positionFromBounds(bounds1).image(bmd);

                GroundOverlay groundOverlay = googleMap.addGroundOverlay(groundOverlayOptions);


                break;
        }


    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(MapHelper.getInstance().getActivity().getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getAllPixels(Point startPoint, Point endPoint) {

    }

    ArrayList<LatLng> shadowArrayList;


    private void animPixels(final int fromX, final int toX, int fromY, final int toY) {


        ValueAnimator valueAnimator = ValueAnimator.ofInt(fromY, toY);
//        valueAnimator.setInterpolator(new AccelerateInterpolator());
//        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int percentageValue = (int) animation.getAnimatedValue();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

//              switch (dir){
//                  case Consts.TORIGHT:
//                      animateToRight(toY,fromX, toX);
//                      break;
//                  case Consts.TOLEFT:
//                      animateToRight(toY,fromX, toX);

//                      break;
//              }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(2000);
        valueAnimator.start();

        ValueAnimator valueAnimator1 = ValueAnimator.ofInt(fromX, toX);
        valueAnimator1.setDuration(2000);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = new Point();
                LatLng latLng = googleMap.getProjection().fromScreenLocation(point);
                googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator1, valueAnimator);
        animatorSet.start();

    }

    ArrayList<LatLng> lats = null;
    Marker marker = null;


    public void animL() {
        shadowArrayList = new ArrayList<>();

        double k = 0.1;

        double culc = Math.abs(startPos.longitude - endPos.longitude);

        if (culc > 0.05 && culc < 0.1) {
            k = 0.3;
        } else if (culc < 0.05 && culc > 0.01) {
            k = 0.1;
        } else if (culc > 0.1) {
            k = 0.4;
        } else if (culc < 0.01) {
            k = 0.05;
        }

        if (startPos.longitude > endPos.longitude) {
            lats = showCurvedPolyline(endPos, startPos, k);
            lats.add(0, endPos);
            lats.add(lats.size() - 1, startPos);
            ArrayList<LatLng> backList = (ArrayList<LatLng>) lats.clone();
            lats.clear();
            for (int i = backList.size() - 1; i > 0; i--) {
                lats.add(backList.get(i));
            }


        } else {
            lats = showCurvedPolyline(startPos, endPos, k);
            lats.add(0, startPos);
            lats.add(lats.size() - 1, endPos);
            lats.add(0, startPos);
        }

//        LatLng startPosForShadow = lats.get(0);
//        LatLng endPosForShadow = lats.get(lats.size() - 1);
        final LatLng[] startForLinePos = {startPos};


        for (int i = 0; i < 100; i++) {
            double deltaLat = (endPos.latitude - startPos.latitude) / 100;
            double deltaLng = (endPos.longitude - startPos.longitude) / 100;
            LatLng latLng = new LatLng(startForLinePos[0].latitude + deltaLat, startForLinePos[0].longitude + deltaLng);
//                googleMap.addPolyline(new PolylineOptions().add(startForLinePos[0],latLng));
            Log.d("ttt", String.valueOf(startForLinePos[0]));
            startForLinePos[0] = latLng;

            shadowArrayList.add(latLng);
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(14);
        polylineOptions.color(Color.BLACK);
        final Polyline polyline = googleMap.addPolyline(polylineOptions);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new FastOutLinearInInterpolator());
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        final ArrayList<LatLng> arr = new ArrayList<>();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> foregroundPoints = lats;

                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount = foregroundPoints.size();
                int countTobeRemoved = (int) (pointcount * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
                for (LatLng la : subListTobeRemoved)
                    arr.add(la);
                polyline.setPoints(arr);

                Point draw;
                if (arr.size() != 0) {

                    if (marker != null)
                        marker.remove();
                    marker = googleMap.addMarker(new MarkerOptions().position(arr.get(arr.size() - 1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_create_black_24dp)));

//                    draw = googleMap.getProjection().toScreenLocation(arr.get(arr.size() - 1));
//                    MapHelper.getInstance().getDraw().setY(draw.y);
//                    MapHelper.getInstance().getDraw().setX(draw.x);
                }


                subListTobeRemoved.clear();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

//                MapHelper.getInstance().getDraw().setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //googleMap.addPolyline(new PolylineOptions().addAll(shadowArrayList));
                marker.remove();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        valueAnimator.start();

        final Polyline pol = googleMap.addPolyline(new PolylineOptions().width(10).color(Color.GRAY));


        ValueAnimator shadowAnimator = ValueAnimator.ofInt(0, 100);
        shadowAnimator.setDuration(800);
        shadowAnimator.setInterpolator(new FastOutLinearInInterpolator());

        final ArrayList<LatLng> a = new ArrayList<>();

        shadowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {


                List<LatLng> foregroundPoints1 = shadowArrayList;

                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount1 = foregroundPoints1.size();
                int countTobeRemoved1 = (int) (pointcount1 * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved1 = foregroundPoints1.subList(0, countTobeRemoved1);
                for (LatLng la : subListTobeRemoved1)
                    a.add(la);
                pol.setPoints(a);
                subListTobeRemoved1.clear();


            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator, shadowAnimator);
        animatorSet.start();

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

    private void startAnim(final List<LatLng> points) {
        if (googleMap != null) {
            MapAnimator.getInstance().setDuration(2000);
            MapAnimator.getInstance().animateLine(googleMap, points);
        } else {
            //Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    PolylineOptions lineOptions;
    Polyline polyline;

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


    private ArrayList<LatLng> showCurvedPolyline(LatLng p1, LatLng p2, double k) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1 - k * k) * d * 0.5 / (2 * k);
        double r = (1 + k * k) * d * 0.5 / (2 * k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 1000;
        double step = (h2 - h1) / numpoints;
        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (int i = 0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
            latLngs.add(pi);
        }

        //Draw polyline
//        googleMap.addPolyline(options.width(10).color(Color.MAGENTA).geodesic(false).pattern(pattern));
        return latLngs;
    }

}
