package com.example.aro_pc.heatmapongoogle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.aro_pc.heatmapongoogle.background.BackgroundMainActivity;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentGoogleMap;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentLayerdMap;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentMindMap;
import com.example.aro_pc.heatmapongoogle.stackoverflow.StackOverFlow;
import com.google.android.gms.maps.model.PolygonOptions;
import com.konifar.fab_transformation.FabTransformation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FragmentGoogleMap fragmentGoogleMap;
    private FragmentLayerdMap fragmentLayerdMap;
    private FragmentMindMap fragmentMindMap;
    private FragmentManager fragmentManager;
    private StackOverFlow fragmentStackOverFlow;
    private DrawerLayout drawer;
    FrameLayout frameLayout;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = (FrameLayout) findViewById(R.id.frame);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                FabTransformation.with(fab).duration(100).transformTo(frameLayout);
                isFabShow = false;
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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_container, fragmentLayerdMap).commit();


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.navigation_item_google_maps:
                if (!isFabShow) hideFab();
                fragmentManager.beginTransaction().replace(R.id.fragment_map_container, fragmentGoogleMap).commit();
                break;
            case R.id.navigation_item_heat_map_layers:
                if (!isFabShow) hideFab();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_container, fragmentLayerdMap).commit();
                break;
            case R.id.navigation_item_heat_mind_map:
                if (!isFabShow) hideFab();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_container, fragmentMindMap).commit();
                break;
            case R.id.stackoverflow:
                if (!isFabShow) hideFab();
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

    private boolean isFabShow = true;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isFabShow)
            super.onBackPressed();
        }
        if(!isFabShow){
            hideFab();
            isFabShow = true;

        }

    }

    private void hideFab(){
        FabTransformation.with(fab).duration(100).transformFrom(frameLayout);
    }

}



