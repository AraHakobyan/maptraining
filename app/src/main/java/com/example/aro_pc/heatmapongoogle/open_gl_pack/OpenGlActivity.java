package com.example.aro_pc.heatmapongoogle.open_gl_pack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Aro-PC on 5/19/2017.
 */

public class OpenGlActivity extends AppCompatActivity{

    private MyGlSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new MyGlSurfaceView(this);
        setContentView(glSurfaceView);

    }
}
