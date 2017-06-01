package com.example.aro_pc.heatmapongoogle.background;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.aro_pc.heatmapongoogle.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Aro-PC on 5/22/2017.
 */

public class BackgroundService extends Service {

    public static final String TAG = "tag";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    NotificationManager notificationManager;
    Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                showNotification();
//                handler.postDelayed(this, 5000);
//            }
//        });
        return START_STICKY;
    }

    @Override
    public ComponentName startService(Intent service) {


        return super.startService(service);
    }

    private void showNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);

            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);

        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, BackgroundMainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("test")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Title")
                .setContentText(getLocation())
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .build();

     //   Log.e("screen on.................................", ""+isScreenOn);



        notificationManager.notify(15, notification);

    }

    String lastLocation = "";

    private String getLocation() {
        String location = "";

        Criteria criteria = new Criteria();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {

            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setSpeedRequired(true);

        }

        String provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        Location loc = mLocationManager.getLastKnownLocation(provider);


        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();

        location = "pos" + longitude + "pos" + latitude + "***" ;
        if(!lastLocation.equals(location)){
            try {
                saveInFile(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastLocation = location;
        }


        return location;
    }

    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    File mFile;

    private void saveInFile(String location) throws IOException {


        File dir = Environment.getExternalStorageDirectory();
        mFile = new File(dir, "mFile.html");


        if (mFile.exists()) {
            writeINtoAllFile(location);
        } else {
            try {
                mFile.createNewFile();
                writeINtoAllFile(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        FileOutputStream fOut = new FileOutputStream(mFile);
//        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//        myOutWriter.append(location + " Time: " + new Date());
//
//        myOutWriter.close();
//
//        fOut.flush();
//
//        fOut.close();

    }



    private void writeINtoAllFile(String location) throws IOException {

        fileWriter = new FileWriter(mFile, true);

        bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(location);
        bufferedWriter.newLine();
        bufferedWriter.close();

    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            showNotification();


        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


}
