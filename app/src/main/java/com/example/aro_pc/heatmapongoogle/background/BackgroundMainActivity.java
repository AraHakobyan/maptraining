package com.example.aro_pc.heatmapongoogle.background;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aro_pc.heatmapongoogle.R;
import com.example.aro_pc.heatmapongoogle.fragments.MapFragment;
import com.example.aro_pc.heatmapongoogle.listeners.ReadLocationsFromeFile;
import com.example.aro_pc.heatmapongoogle.models.LatLngModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Aro-PC on 5/22/2017.
 */

public class BackgroundMainActivity extends AppCompatActivity implements View.OnClickListener , ReadLocationsFromeFile{

    private Button mReadFromeFileButton, mBackgroundServiceButton, mMapButton;
    private TextView textView;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backend_activity);

        latLngModels = new ArrayList<>();

        textView = (TextView) findViewById(R.id.file_tv);
        mReadFromeFileButton = (Button) findViewById(R.id.read_frome_file);
        mBackgroundServiceButton = (Button) findViewById(R.id.start_background_service);
        mMapButton = (Button) findViewById(R.id.to_map);
        mMapButton.setOnClickListener(this);
        mBackgroundServiceButton.setOnClickListener(this);
        mReadFromeFileButton.setOnClickListener(this);

        findViewById(R.id.delete_file).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.read_frome_file:

                    textView.setText(readFromeFile());

                break;
            case R.id.start_background_service:

                startService(new Intent(this, BackgroundService.class));
                break;
            case R.id.to_map:


                findViewById(R.id.scroll_view_id).setVisibility(View.GONE);
                findViewById(R.id.container_id).setVisibility(View.VISIBLE);

                readFromeFile();
                Gson gson = new Gson();
                String json = gson.toJson(latLngModels);
                mapFragment = new com.example.aro_pc.heatmapongoogle.fragments.MapFragment(json, this);


                getSupportFragmentManager().beginTransaction().replace(R.id.container_id,mapFragment).commit();

                break;
            case R.id.delete_file:
                mapFragment.clearMap();
                File dir = Environment.getExternalStorageDirectory();
                File mFile = new File(dir, "mFile.html");
                mFile.delete();


        }
    }
    private String myData = "";

    private String readFromeFile() {

        File dir = Environment.getExternalStorageDirectory();
        File mFile = new File(dir, "mFile.html");


        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                //text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        String mString = text.toString();
        getArrayFromeString(mString);


        return text.toString();


    }

    ArrayList<LatLngModel> latLngModels;

    private void getArrayFromeString(String mString) {

        String[] strings = mString.split("\\*\\*\\*") ;

        if(!mString.equals(""))

        for (String a : strings){
            String[] pos = a.split("pos");
            double lat = Double.parseDouble(pos[1]);
            double lng = Double.parseDouble(pos[2]);
            latLngModels.add(new LatLngModel(lat, lng));

        }


    }

    int fileLength = 0;

    @Override
    public void readLocationsFromeFile(boolean read) {
        if(read) {
            latLngModels.clear();
            readFromeFile();
            Gson gson = new Gson();
            String json = gson.toJson(latLngModels);
            if(fileLength != json.length()){
                mapFragment.setLatLngs(json);
            }

        }
    }
}
