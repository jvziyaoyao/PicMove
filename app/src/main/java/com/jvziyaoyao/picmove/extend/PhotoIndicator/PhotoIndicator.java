package com.jvziyaoyao.picmove.extend.PhotoIndicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.domain.model.PhotoQueryEntity;
import com.jvziyaoyao.picmove.utils.MUtils;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-27 01:42
 **/
public class PhotoIndicator extends ViewIndicator {

    private static final String STR_NUM_FORMAT = "%s/%s";
    public static final String TXT_OK_SELECTED = PicMoveApplication.getApplication().getResources().getString(R.string.TXT_PI_OK_SELECTED);

    private TextView mNumberText;
    private LinearLayout mSelWrap;
    private TextView mSelAbleIcon;
    private TextView mSelIcon;
    private TextView mOK;
    private TextView mBack;
    private ConstraintLayout mMainView;

    private List<PhotoQueryEntity> list;

    private ViewPager mViewPager;

    private int mPosition;

    private final ViewPager.OnPageChangeListener mInternalPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            PhotoIndicator.this.mPosition = position;
            if (mViewPager.getAdapter() == null || mViewPager.getAdapter().getCount() <= 0)
                return;
            if (mNumberText != null) {
                mNumberText.setText(String.format(Locale.getDefault(),
                        STR_NUM_FORMAT,
                        position + 1,
                        mViewPager.getAdapter().getCount()));
            }
            onSelectedNotify();
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 刷新选中字样
     */
    public void onSelectedNotify() {
        if (list != null) {
            int selCount = list.stream().filter(a -> a.getSelected()).collect(Collectors.toList()).size();
            String okText = TXT_OK_SELECTED.replace("?", selCount+"");
            if (mOK != null) mOK.setText(okText);
            PhotoQueryEntity photo = list.get(mPosition);
            if (mSelAbleIcon != null) mSelAbleIcon.setVisibility(photo.getSelected() ? GONE : VISIBLE);
            if (mSelIcon != null) mSelIcon.setVisibility(photo.getSelected() ? VISIBLE : GONE);
        }
    }

    public PhotoIndicator(Context context, List<PhotoQueryEntity> list, Consumer<Integer> onSelectClick, Consumer<View> onOkBtnClick, Consumer<View> onBackClick) {
        this(context, null);
        this.list = list;
        this.onSelectedNotify();
        if (mSelWrap != null) {
            mSelWrap.setOnClickListener(v -> {
                if (list != null && mPosition != -1) {
                    if (onSelectClick != null) onSelectClick.accept(mPosition);
                    onSelectedNotify();
                }
            });
        }
        if (mOK != null) mOK.setOnClickListener(v -> {
            if (onOkBtnClick != null) onOkBtnClick.accept(v);
        });
        if (mBack != null) mBack.setOnClickListener(v -> {
            if (onBackClick != null) onBackClick.accept(v);
        });
    }
    public PhotoIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PhotoIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_photo_indicator, this);
        mNumberText = findViewById(R.id.pi_text);
        mSelWrap = findViewById(R.id.pi_sel_wrap);
        mSelAbleIcon = findViewById(R.id.pi_sel_able_icon);
        mSelIcon = findViewById(R.id.pi_sel_icon);
        mOK = findViewById(R.id.pi_send);
        mMainView = findViewById(R.id.pi_page);
        mBack = findViewById(R.id.pi_back);
        MUtils.setAllTypeFace(mMainView, context);
    }

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
