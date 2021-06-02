package com.jvziyaoyao.picmove.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.jvziyaoyao.picmove.PicMoveApplication;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @program: StartDemo
 * @description: 工具类
 * @author: 橘子妖妖
 * @create: 2021-01-18 13:53
 **/
public class MUtils {

    public static final String ICONFONT_PATH = "get/iconfont.ttf";

    /**
     * 延后执行
     *
     * @param delay
     * @return
     */
    public static Timer goTimerDelay(final Runnable run, long delay, final Activity activity) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(run);
            }
        }, delay);
        return timer;
    }

    /**
     * 间隔执行
     *
     * @param activity
     * @param interval
     * @param run
     * @return
     */
    public static Timer goTimerInterval(final Activity activity, long interval, final Runnable run) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(run);
            }
        }, 0, interval);
        return timer;
    }

    /**
     * 设置view中的icon
     *
     * @param rootView
     * @param context
     */
    public static void setAllTypeFace(View rootView, Context context) {
        Typeface mIconTypeface = Typeface.createFromAsset(context.getAssets(), ICONFONT_PATH);
        if (rootView instanceof TextView) {
            if (mIconTypeface != null) {
                ((TextView) rootView).setTypeface(mIconTypeface);
            }
        }
        if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                setAllTypeFace(child, context);
            }
        }
    }

    /**
     * 设置状态栏透明
     *
     * @param act
     */
    public static void setTranslucentStatus(Activity act) {
        if (null == act) {
            return;
        }
        Window window = act.getWindow();
        if (null == window) {
            return;
        }
        // 5.0以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏为黑色或白色
     *
     * @param activity
     * @param dark
     */
    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public static Uri getUri(String url) {
        File tempFile = new File(url);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            try {
                return FileProvider.getUriForFile(PicMoveApplication.getApplication(), "com.jvziyaoyao.picmove.fileprovider", tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            return Uri.fromFile(tempFile);
        }
        return null;
    }

    public static String getMimeType(Context context, File file, @NonNull String authority) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        ContentResolver resolver = context.getContentResolver();
        return resolver.getType(uri);
    }

    public static String getMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(file.getName());
        return type;
    }

    public static String getUUID32(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 获取当前版本名
     * @return
     */
    public static String getVersionName() {
        String versionName = null;
        try {
            PackageManager pm = PicMoveApplication.getApplication().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PicMoveApplication.getApplication().getPackageName(), 0);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 手机系统版本
     */
    public static String getSdkVersion() {
        return android.os.Build.VERSION.RELEASE;
    }


}