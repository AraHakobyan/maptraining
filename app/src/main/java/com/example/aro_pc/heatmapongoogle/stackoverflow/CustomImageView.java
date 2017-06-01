package com.example.aro_pc.heatmapongoogle.stackoverflow;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Aro-PC on 4/21/2017.
 */

public class CustomImageView extends FrameLayout implements TintableBackgroundView {
    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {

    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return null;
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {

    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return null;
    }
}
