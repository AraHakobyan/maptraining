package com.example.aro_pc.heatmapongoogle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.aro_pc.heatmapongoogle.background.BackgroundMainActivity;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentGoogleMap;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentLayerdMap;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentMindMap;
import com.example.aro_pc.heatmapongoogle.stackoverflow.StackOverFlow;
import com.google.android.gms.maps.model.PolygonOptions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FragmentGoogleMap fragmentGoogleMap;
    private FragmentLayerdMap fragmentLayerdMap;
    private FragmentMindMap fragmentMindMap;
    private FragmentManager fragmentManager;
    private StackOverFlow fragmentStackOverFlow;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentGoogleMap = FragmentGoogleMap.getInstance();
        fragmentLayerdMap = FragmentLayerdMap.getInstance();
        fragmentMindMap = FragmentMindMap.getInstance();
        fragmentStackOverFlow = StackOverFlow.getInstance();
        fragmentStackOverFlow.setContext(getApplicationContext());

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.navigation_item_google_maps:
                fragmentManager.beginTransaction().replace(R.id.fragment_map_container, fragmentGoogleMap).commit();

                break;

            case R.id.navigation_item_heat_map_layers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_container, fragmentLayerdMap).commit();

                break;
            case R.id.navigation_item_heat_mind_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_container, fragmentMindMap).commit();
                break;
            case R.id.stackoverflow:
//               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_container,fragmentStackOverFlow).commit();
//               startActivity(new Intent(this,OpenGlActivity.class));
                startActivity(new Intent(this, BackgroundMainActivity.class));
                break;
        }
        drawer.closeDrawer(Gravity.LEFT);

        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawers();
        return true;
    }

    PolygonOptions polygonOptions;


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_container, fragmentLayerdMap).commit();
    }

}



