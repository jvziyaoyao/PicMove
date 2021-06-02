package com.jvziyaoyao.picmove.extend.PhotoIndicator;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.viewpager.widget.ViewPager;

import com.hitomi.tilibrary.style.IIndexIndicator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-27 01:36
 **/
public class CusIndicator implements IIndexIndicator {

    private ViewIndicator indicator;

    public CusIndicator(ViewIndicator indicator) {
        this.indicator = indicator;
    }

    @Override
    public void attach(FrameLayout parent) {
        FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        indexLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        indicator.setLayoutParams(indexLp);

        parent.addView(indicator);
    }

    @Override
    public void onShow(ViewPager viewPager) {
        if(indicator == null) return;
        indicator.setVisibility(View.VISIBLE);
        indicator.setViewPager(viewPager);
    }

    @Override
    public void onHide() {
        if (indicator == null) return;
        indicator.setVisibility(View.GONE);
    }

    @Override
    public void onRemove() {
        if (indicator == null) return;
        ViewGroup vg = (ViewGroup) indicator.getParent();
        if (vg != null) {
            vg.removeView(indicator);
        }
    }

}
