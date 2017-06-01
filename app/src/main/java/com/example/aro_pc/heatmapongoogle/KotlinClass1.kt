package com.example.aro_pc.heatmapongoogle

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.MapView

/**
 * Created by Aro-PC on 5/19/2017.
 */

class KotlinClass1 : AppCompatActivity() {

    var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.kotlin_activity1_layout)

        mapView = findViewById(R.id.kotlin_map_id) as MapView

    }
}
