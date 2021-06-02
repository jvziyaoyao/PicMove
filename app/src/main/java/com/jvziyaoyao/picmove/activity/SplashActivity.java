package com.jvziyaoyao.picmove.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.utils.MUtils;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-02-19 15:19
 **/
public class SplashActivity extends AppCompatActivity {

    // 闪屏延迟时间
    public static final long SPLASH_SHOW_DELAY = 1200;

    // 主视图
    public ConstraintLayout mMainView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 去除title
        MUtils.setTranslucentStatus(SplashActivity.this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mMainView = findViewById(R.id.splash_page);
        if (mMainView != null) MUtils.setAllTypeFace(mMainView, SplashActivity.this);
        MUtils.goTimerDelay(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }, SPLASH_SHOW_DELAY, this);
    }

}
