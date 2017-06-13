package com.example.aro_pc.heatmapongoogle.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aro-PC on 6/13/2017.
 */

public class RoadAnimation {
    private LatLng startPosRoad, startPos, endPosRoad, endPos;
    private GoogleMap googleMap;
    private ArrayList<LatLng> road, startCurvedPoints, endCurvedPoints, deltaDotsToShow1, deltaDotsToShow2;
    private int t1 = 0;
    private int t2 = 0;
    ArrayList<LatLng> addAllDotsToRoad1, addAllDotsToRoad2;
    ArrayList<LatLng> dotArray1, dotArray2;

    final int PATTERN_DASH_LENGTH_PX = 5;
    final int PATTERN_GAP_LENGTH_PX = 5;
    final PatternItem DOT = new Dot();
    final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    final List<PatternItem> PATTERN_POLYGON_ALPHA1 = Arrays.asList( GAP, DOT);
    final List<PatternItem> PATTERN_POLYGON_ALPHA2 = Arrays.asList( GAP, DOT);

    AnimatorSet animToRoad, animFromRoad, animRoad;


    public RoadAnimation(GoogleMap googleMap, LatLng startPos, LatLng endPos, ArrayList<LatLng> road) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.googleMap = googleMap;
        this.road = road;
        if (road.size() == 0) return;
        this.startPosRoad = road.get(0);
        this.endPosRoad = road.get(road.size() - 1);

        double heading1 = Math.abs(SphericalUtil.computeHeading(startPos, startPosRoad));
        double k1 = takeK(heading1);


        if (startPos.longitude > startPosRoad.longitude) {
            startCurvedPoints = getCurvedPolyline(startPosRoad, startPos, k1);
            ArrayList<LatLng> backList = (ArrayList<LatLng>) startCurvedPoints.clone();
            startCurvedPoints.clear();
            for (int i = backList.size() - 1; i > 0; i--) {
                startCurvedPoints.add(backList.get(i));
            }


        } else {
            startCurvedPoints = getCurvedPolyline(startPos, startPosRoad, k1);

        }

        double heading2 = Math.abs(SphericalUtil.computeHeading(endPosRoad, endPos));
        double k2 = takeK(heading2);

        if (endPosRoad.longitude > endPos.longitude) {
            endCurvedPoints = getCurvedPolyline(endPos, endPosRoad, k2);
            ArrayList<LatLng> backList = (ArrayList<LatLng>) startCurvedPoints.clone();
            endCurvedPoints.clear();
            for (int i = backList.size() - 1; i > 0; i--) {
                endCurvedPoints.add(backList.get(i));
            }


        } else {
            endCurvedPoints = getCurvedPolyline(endPosRoad, endPos, k2);

        }

        if (startCurvedPoints.size() != 0)
            animToRoad = fromStartPosToRoad(startCurvedPoints);

        if (endCurvedPoints.size() != 0)
            fromRoadToEndPos(endCurvedPoints);

        animToRoad.start();

        animRoad = animateRoad(road);
        animRoad.start();

    }

    private double takeK(double heading) {
        double k = 0.5;
        if (heading < 5 || heading > 175) {
            k = 0.05;
        } else if (heading < 15 || heading > 165) {
            k = 0.1;
        } else if (heading < 45 || heading > 135) {
            k = 0.25;
        } else if (heading < 60 || heading > 120) {
            k = 0.35;
        } else if (heading < 85 || heading > 95) {
            k = 0.4;
        }

        return k;
    }

    public AnimatorSet animateRoad(final ArrayList<LatLng> road) {

        PolylineOptions options = new PolylineOptions().width(10).zIndex(10).color(Color.BLACK);
        final Polyline roadPolyline = googleMap.addPolyline(options);


        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(1500);
        valueAnimator.setInterpolator(new FastOutLinearInInterpolator());
        final ArrayList<LatLng> arr = new ArrayList<>();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> foregroundPoints = road;

                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount = foregroundPoints.size();
                int countTobeRemoved = (int) (pointcount * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
                for (LatLng la : subListTobeRemoved)
                    arr.add(la);
                roadPolyline.setPoints(arr);
                subListTobeRemoved.clear();
            }
        });

        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.GRAY, Color.BLACK);
        colorAnimation.setInterpolator(new AccelerateInterpolator());
        colorAnimation.setDuration(1500);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                roadPolyline.setColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);


        AnimatorSet roadAnimator = new AnimatorSet();
        roadAnimator.playTogether(valueAnimator,colorAnimation);
        return roadAnimator;
    }

    public AnimatorSet fromStartPosToRoad(ArrayList<LatLng> startCurvedPoints) {

        dotArray1 = new ArrayList<>();
        addAllDotsToRoad1 = new ArrayList<>();
        deltaDotsToShow1 = new ArrayList<>();


        PolylineOptions options = new PolylineOptions().width(10).zIndex(10).color(Color.BLUE).pattern(PATTERN_POLYGON_ALPHA1);
        final Polyline dotPolyline = googleMap.addPolyline(options);

        int removeFrom = 19;
        int dotCount = 10;
        do {
            List<LatLng> deltaArray = startCurvedPoints.subList(removeFrom, removeFrom + dotCount);
            removeFrom = removeFrom + 30;

            for (LatLng dot : deltaArray) {
                dotArray1.add(dot);
            }

        } while (removeFrom < startCurvedPoints.size());

        final ValueAnimator dotAnim = ValueAnimator.ofInt(0, 9);
        dotAnim.setInterpolator(new FastOutSlowInInterpolator());
        dotAnim.setDuration(1500);

        dotAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if (t1 < dotArray1.size()) {
                    List<LatLng> dot = dotArray1.subList(t1, t1 + 10);
                    t1 = t1 + 10;
                    for (LatLng l : dot) {
                        addAllDotsToRoad1.add(l);
                    }
                    dotPolyline.setPoints(addAllDotsToRoad1);
                }
            }
        });

        dotAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                addAllDotsToRoad1.clear();

                t1 = 0;


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {


            }
        });

//        dotAnim.start();

        ValueAnimator animRemoveDots = ValueAnimator.ofInt(0, 9);
        animRemoveDots.setDuration(1500);
        animRemoveDots.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> currentdots = dotPolyline.getPoints();

                for (int i = 0; i < 10; i++) {
                    if (currentdots.size() > 0) {
                        currentdots.remove(currentdots.size() - 1);

                    }
                }
                dotPolyline.setPoints(currentdots);
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                animatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.playSequentially(dotAnim, animRemoveDots);
        return animatorSet;

    }

    public void fromRoadToEndPos(ArrayList<LatLng> endCurvedPoints) {

        dotArray2 = new ArrayList<>();
        addAllDotsToRoad2 = new ArrayList<>();
        deltaDotsToShow2 = new ArrayList<>();


        PolylineOptions options = new PolylineOptions().width(10).zIndex(10).color(Color.BLUE).pattern(PATTERN_POLYGON_ALPHA2);
        final Polyline dotPolyline = googleMap.addPolyline(options);
        dotPolyline.setPoints(endCurvedPoints);
//
//        int removeFrom = 19;
//        int dotCount = 10;
//        do {
//            if(removeFrom + dotCount <= endCurvedPoints.size() -1) {
//                Log.d("mmmmmm", "fromIndex : " + removeFrom + " toIndex : " + (removeFrom + dotCount) + " size : " + endCurvedPoints.size());
//                List<LatLng> deltaArray = endCurvedPoints.subList(removeFrom, removeFrom + dotCount);
//                removeFrom = removeFrom + 30;
//
//                for (LatLng dot : deltaArray) {
//                    dotArray2.add(dot);
//                }
//            } else {
//                removeFrom = removeFrom + 30;
//
//            }
//
//        } while (removeFrom < endCurvedPoints.size());
//
//        final ValueAnimator dotAnim = ValueAnimator.ofInt(0, 9);
//        dotAnim.setInterpolator(new FastOutSlowInInterpolator());
//        dotAnim.setDuration(1500);
//
//        dotAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//
//                if (t2 < dotArray2.size()) {
//                    List<LatLng> dot = dotArray2.subList(t2, t2 + 10);
//                    t2 = t2 + 10;
//                    for (LatLng l : dot) {
//                        addAllDotsToRoad2.add(l);
//                    }
//                    dotPolyline.setPoints(addAllDotsToRoad2);
//                }
//            }
//        });
//
//        dotAnim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                addAllDotsToRoad2.clear();
//
//                t2 = 0;
//
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//
//            }
//        });
//
////        dotAnim.start();
//
//        ValueAnimator animRemoveDots = ValueAnimator.ofInt(0, 9);
//        animRemoveDots.setDuration(1500);
//        animRemoveDots.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                List<LatLng> currentdots = dotPolyline.getPoints();
//
//                for (int i = 0; i < 10; i++) {
//                    if (currentdots.size() > 0) {
//                        currentdots.remove(currentdots.size() - 1);
//
//                    }
//                }
//                dotPolyline.setPoints(currentdots);
//            }
//        });
//
//        final AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//
//                animatorSet.start();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        animatorSet.playSequentially(dotAnim, animRemoveDots);
//        return animatorSet;
    }

    private ArrayList<LatLng> getCurvedPolyline(LatLng p1, LatLng p2, double k) {


        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);
        Log.d("heading ", String.valueOf(h));

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1 - k * k) * d * 0.5 / (2 * k);
        double r = (1 + k * k) * d * 0.5 / (2 * k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 300;
        double step = (h2 - h1) / numpoints;
        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (int i = 0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
            latLngs.add(pi);
        }


        return latLngs;
    }

}
