package com.jvziyaoyao.picmove.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jvziyaoyao.picmove.BuildConfig;
import com.jvziyaoyao.picmove.PicMoveApplication;

import static android.content.Context.MODE_PRIVATE;

/**
 * @program: MicroFire
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-01-22 15:56
 **/
public class ShareUtils {

    public static final String TAG = ShareUtils.class.getSimpleName();

    public static final String SHARE_SYSTEM_CONFIG_KEY = "SHARE_SYSTEM_CONFIG_KEY";

    public static void set(String key, String value) {
        SharedPreferences sharedPreferences = PicMoveApplication.getApplication().getSharedPreferences(BuildConfig.SHARE_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).commit();
    }

    public static String get(String key) {
        SharedPreferences sp = PicMoveApplication.getApplication().getSharedPreferences(BuildConfig.SHARE_TAG, Context.MODE_PRIVATE);
        //通过key值获取到相应的data，如果没取到，则返回后面的默认值
        return sp.getString(key, BuildConfig.SHARE_NO_VALUE);
    }

    public static void delete(String key) {
        SharedPreferences sharedPreferences = PicMoveApplication.getApplication().getSharedPreferences(BuildConfig.SHARE_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key).commit();
    }

}
