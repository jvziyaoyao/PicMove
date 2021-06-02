package com.jvziyaoyao.picmove.extend;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @program: MicroFire
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-01-20 10:50
 **/
public abstract class BaseObserverAppCompatActivity extends AppCompatActivity {

    protected abstract void onEnter();

    protected abstract void onExit();

    @Override
    protected void onStart() {
        super.onStart();
        onEnter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onExit();
    }
}
