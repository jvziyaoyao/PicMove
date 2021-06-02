package com.jvziyaoyao.picmove.utils;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.jvziyaoyao.picmove.R;
import com.vansz.glideimageloader.GlideImageLoader;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-10 10:38
 **/
public class TransfereeUtils {

    public static final String TAG = TransfereeUtils.class.getSimpleName();

    public static Transferee getTransferee(Activity activity, @Nullable Runnable onShow, @Nullable Runnable onDismiss) {
        Transferee trans = Transferee.getDefault(activity);
        trans.setOnTransfereeStateChangeListener(new Transferee.OnTransfereeStateChangeListener() {
            @Override
            public void onShow() {
                if (onShow != null) onShow.run();
            }
            @Override
            public void onDismiss() {
                MUtils.setAndroidNativeLightStatusBar(activity,true);
                if (onDismiss != null) onDismiss.run();
            }
        });
        return trans;
    }

    public static TransferConfig.Builder getDefaultBuilder(Activity activity) {
        TransferConfig.Builder builder = TransferConfig.build()
                .setImageLoader(GlideImageLoader.with(activity.getApplicationContext())) // 图片加载器，可以实现 ImageLoader 扩展
                .setBackgroundColor(activity.getColor(R.color.black)) // 背景色
                .setDuration(300) // 开启、关闭、手势拖拽关闭、显示、扩散消失等动画时长
                .setOffscreenPageLimit(2) // 第一次初始化或者切换页面时预加载资源的数量，与 justLoadHitImage 属性冲突，默认为 1
                .enableDragClose(true) // 是否开启下拉手势关闭，默认开启
                .enableDragHide(false) // 下拉拖拽关闭时，是否先隐藏页面上除主视图以外的其他视图，默认开启
                .enableDragPause(false) // 下拉拖拽关闭时，如果当前是视频，是否暂停播放，默认关闭
                .enableHideThumb(false) // 是否开启当 transferee 打开时，隐藏缩略图, 默认关闭
                .enableScrollingWithPageChange(false)
                .setIndexIndicator(new NumberIndexIndicator());
        return builder;
    }


}
