package com.example.aro_pc.heatmapongoogle.open_gl_pack;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Aro-PC on 5/19/2017.
 */

public class MyGlSurfaceView extends GLSurfaceView {

    private final MyGlRenderer myGlRenderer;

    public MyGlSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        myGlRenderer = new MyGlRenderer();
        setRenderer(myGlRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

}
