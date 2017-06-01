package com.example.aro_pc.heatmapongoogle.stackoverflow;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import com.example.aro_pc.heatmapongoogle.R;


public class StackOverFlow extends Fragment implements View.OnTouchListener {

    private ImageSwitcher imageSwitcher;
    private TextView textView1, textView2;
    private String string;
    private Display display;
    private int tv1Width, tv2Width;
    ViewTreeObserver vto ;
    final int[] height = new int[1];
    final int[] width = new int[1];

    private static StackOverFlow instance = null;

    Context context;

    @Override
    public void onResume() {
        super.onResume();
        textView1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                height[0] = textView1.getMeasuredHeight();
                width[0] = textView1.getMeasuredWidth();
            }
        });

    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static StackOverFlow getInstance() {
        if (instance == null) {
            instance = new StackOverFlow();
        }
        return instance;
    }


    public StackOverFlow() {
        string = "let my go to other view ";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View[] view = {inflater.inflate(R.layout.fragment_stack_over_flow, container, false)};
        textView1 = (TextView) view[0].findViewById(R.id.tv1);

        return view[0];
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageSwitcher = (ImageSwitcher) view.findViewById(R.id.image_switcher);
        imageSwitcher.setOnTouchListener(this);

        textView2 = (TextView) view.findViewById(R.id.tv2);
        int textSize = 16;

        textView2.setTextSize(textSize);
        textView1.setTextSize(textSize);

        final float scale = getResources().getDisplayMetrics().density;
        int dpWidthInPx  = (int) (width[0] * scale);

        int countTv1Chars = dpWidthInPx / textSize;
        String tv1String = string.substring(0, countTv1Chars);
        String tv2String = string.substring(countTv1Chars, string.length() - 1);
        textView1.setText(tv1String);
        textView2.setText(tv2String);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return false;
    }
}
