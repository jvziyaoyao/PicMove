package com.jvziyaoyao.picmove.extend.PhotoIndicator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public abstract class ViewIndicator extends LinearLayout {

    public ViewIndicator(Context context) {
        super(context);
    }

    public ViewIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    abstract void setViewPager(ViewPager viewPager);

}
