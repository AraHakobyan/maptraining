package com.example.aro_pc.heatmapongoogle.for_map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.gms.maps.MapView;

/**
 * Created by Aro-PC on 6/7/2017.
 */

public class DrawCirculeCanvas {

    MapView mapView;

    public DrawCirculeCanvas(Point point) {
        this.mapView = mapView;
//        Point point = googleMap.getProjection().toScreenLocation(new LatLng(40.4,40.4));
        drawCircule(point.x,point.y,15);
    }
     private void drawCircule(float cX, float cY, float radius){
         Canvas canvas = new Canvas(Bitmap.createBitmap(mapView.getWidth(),mapView.getHeight(), Bitmap.Config.ARGB_8888));
         canvas.drawColor(Color.WHITE);
         Paint paint = new Paint();
         paint.setStyle(Paint.Style.FILL);
         canvas.drawCircle(cX,cY,radius,paint);
     }
}
