package com.example.aro_pc.heatmapongoogle;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
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
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import com.example.aro_pc.heatmapongoogle.animations.CustomAnimations;
import com.example.aro_pc.heatmapongoogle.background.BackgroundMainActivity;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentGoogleMap;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentLayerdMap;
import com.example.aro_pc.heatmapongoogle.fragments.FragmentMindMap;
import com.example.aro_pc.heatmapongoogle.stackoverflow.StackOverFlow;
import com.google.android.gms.maps.model.PolygonOptions;
import com.konifar.fab_transformation.FabTransformation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener{


    private FragmentGoogleMap fragmentGoogleMap;
    private FragmentLayerdMap fragmentLayerdMap;
    private FragmentMindMap fragmentMindMap;
    private FragmentManager fragmentManager;
    private StackOverFlow fragmentStackOverFlow;
    private DrawerLayout drawer;
    FrameLayout frameLayout;
    FrameLayout container;

    public FloatingActionButton getFab() {
        return fab;
    }

    FloatingActionButton fab;
    View overLayView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        container = (FrameLayout) findViewById(R.id.fragment_map_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = (FrameLayout) findViewById(R.id.frame);
        overLayView = findViewById(R.id.overlay);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


                FabTransformation.with(fab).duration(100).setOverlay(overLayView).transformTo(frameLayout);
                isFabShow = false;

//           animateHideFab();



            }
        });

        fab.setOnTouchListener(this);

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
        getSupportFragmentManager().beginTransaction().replace(container.getId(), fragmentLayerdMap).commit();

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;



    }
    int width = 0;
    int height = 0;

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
        FabTransformation.with(fab).duration(100).setOverlay(overLayView).transformFrom(frameLayout);
    }

    public void animateHideFab(){
        CustomAnimations animations = new CustomAnimations();
//        animations.fabAnimate(fab,true);
    }

    public void animateShowFab(){
//        CustomAnimations animations = new CustomAnimations();
//        animations.fabAnimate(fab,false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int X = (int) event.getRawX() ;
        int Y = (int) event.getRawY() ;

//        if (X > width + fab.getWidth()/2) X = width + 2 *fab.getWidth();
//        if (Y > height + fab.getHeight()/2) Y = height + 2 * fab.getHeight() ;
        if (X < 0) X = 0;
        if(Y < 0) Y = 0;
        if( X + fab.getWidth() > width) X = width - fab.getWidth();
        if(Y + fab.getHeight() > container.getHeight()) Y = container.getHeight() - fab.getHeight();
        Log.d("XY",  "X : " + String.valueOf(X) + " Y : " + String.valueOf(Y) + " " + width + " " + height );
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("fab","fab ACTION_DOWN") ;

                break;
            case MotionEvent.ACTION_UP:
                Log.d("fab","fab is ACTION_UP") ;
                int a = (int) (16 * Resources.getSystem().getDisplayMetrics().density);
                CustomAnimations animations = new CustomAnimations();


                if(X > width / 2) {
//                    animations.fabAnimate(fab,false,fab.getWidth() ,Y);
                    animatePixels(X,width - fab.getWidth() - a,Y,container.getHeight()  - a);
                } else {
//                    animations.fabAnimate(fab,false, a,Y);
                    animatePixels(X,a,Y,container.getHeight()  - a);
                }


                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("fab","fab is ACTION_POINTER_DOWN") ;

                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("fab","fab is ACTION_POINTER_UP") ;


                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("fab","fab is ACTION_MOVE") ;
//                CustomAnimations animations = new CustomAnimations();
//                animations.fabAnimate(fab,false,X,Y);

                fab.setX(X);
                fab.setY(Y);

                break;
        }
        return false;
    }

    public void animatePixels(final int fromX, final int toX, int fromY, final int toY){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(fromY,toY);
//        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int percentageValue = (int) animation.getAnimatedValue();
                fab.setY(percentageValue);
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

        ValueAnimator valueAnimator1 = ValueAnimator.ofInt(fromX,toX);
        valueAnimator1.setDuration(2000);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fab.setX((int) animation.getAnimatedValue());
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator1,valueAnimator);
        animatorSet.start();

    }

    AnimatorSet animatorSet;



}



