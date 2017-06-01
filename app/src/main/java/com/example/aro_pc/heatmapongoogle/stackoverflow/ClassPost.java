package com.example.aro_pc.heatmapongoogle.stackoverflow;

import android.os.AsyncTask;
import android.view.View;

/**
 * Created by Aro-PC on 4/21/2017.
 */

public class ClassPost extends AsyncTask<Void, Void, Void> {


    private View view;

    @Override
    protected Void doInBackground(Void... params) {
        return null;
//        View view = null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        
        view.setVisibility(View.GONE);
    }
}
