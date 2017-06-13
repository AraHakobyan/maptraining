package com.example.aro_pc.heatmapongoogle.animations;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.design.widget.FloatingActionButton;
import android.view.animation.AccelerateInterpolator;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Aro-PC on 6/7/2017.
 */

public class CustomAnimations {


    public CustomAnimations() {
    }

    public void chordAnimation(ArrayList<LatLng> arrayList){

        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,100);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int valuAnimated = (int) animation.getAnimatedValue();
            }
        });

    }

    public void fabAnimate(final FloatingActionButton fab,boolean isShow, float toX, float toY){
        float valX = toX;
        float valY  = toY;
        if(isShow){
            valX = 180.0f;
            valY = 180.0f;
        }
        PropertyValuesHolder myView_X = PropertyValuesHolder.ofFloat(fab.TRANSLATION_X,  valX);
//        PropertyValuesHolder myView_Y = PropertyValuesHolder.ofFloat(fab.TRANSLATION_Y, valY);
        ObjectAnimator waveOneAnimator = ObjectAnimator.ofPropertyValuesHolder(fab, myView_X);
        waveOneAnimator.setDuration(300);
//        waveOneAnimator.setRepeatCount(5);
//        waveOneAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveOneAnimator.start();


//        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, Color.RED);
//        colorAnimator.setDuration(300);
//        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        colorAnimator.setRepeatCount(5);
//        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                fab.setColorFilter((int) animation.getAnimatedValue());
//            }
//        });

//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(waveOneAnimator, colorAnimator);
//        animatorSet.start();

    }

    public void setColor(){

    }

    public void windAnimate(ArrayList<LatLng> arrayList){

    }

}
