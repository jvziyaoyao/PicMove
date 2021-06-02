package com.jvziyaoyao.picmove.extend.PhotoIndicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.jvziyaoyao.picmove.R;

import java.util.Locale;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-10 13:53
 **/
public class LeftNumberIndicator extends ViewIndicator {

    private static final String STR_NUM_FORMAT = "%s/%s";

    private TextView mNumberText;

    private ViewPager mViewPager;

    public LeftNumberIndicator(Context context) {
        this(context, null);
    }
    public LeftNumberIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LeftNumberIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_left_number_indicator, this);
        mNumberText = findViewById(R.id.fni_text);
    }

    private final ViewPager.OnPageChangeListener mInternalPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            if (mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() <= 0)
                return;
            if (mNumberText != null) {
                mNumberText.setText(String.format(Locale.getDefault(),
                        STR_NUM_FORMAT,
                        position + 1,
                        mViewPager.getAdapter().getCount()));
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public void setViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            mViewPager = viewPager;
            mViewPager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewPager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
        }
    }

}
