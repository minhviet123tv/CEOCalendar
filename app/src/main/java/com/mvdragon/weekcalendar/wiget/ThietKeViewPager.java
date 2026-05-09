package com.mvdragon.weekcalendar.wiget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class ThietKeViewPager extends ViewPager {
    private boolean enable;

    public ThietKeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(this.enable) {
            return super.onTouchEvent(ev);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(this.enable) {
            return super.onInterceptTouchEvent(ev);
        }

        return false;
    }

    public void setEnableSwipe(boolean enable){
        this.enable = enable;
    }

    @Override
    public void transformMatrixToGlobal(@NonNull Matrix matrix) {
        super.transformMatrixToGlobal(matrix);

    }
}
