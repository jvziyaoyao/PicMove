package com.jvziyaoyao.picmove.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jvziyaoyao.picmove.PicMoveApplication;
import com.jvziyaoyao.picmove.R;
import com.jvziyaoyao.picmove.api.ApiRequest;
import com.jvziyaoyao.picmove.api.SystemApi;
import com.jvziyaoyao.picmove.domain.model.UpgradeEntity;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @program: PicMove
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-03-10 11:09
 **/
public class PicMoveUtils {

    public static final String TAG = PicMoveUtils.class.getSimpleName();

    // 格子间隔大小
    public static final int gap = PicMoveApplication.getApplication().getResources().getDimensionPixelSize(R.dimen.block_size);

    /**
     * 获取方块大小
     * @return
     */
    public static int getBlockSize() {
        int size = (int) ((PicMoveApplication.getApplication().getResources().getDisplayMetrics().widthPixels - gap * 3) / 4);
        return size;
    }

    /**
     * 检查更新
     */
    public static void checkVersion(Consumer<UpgradeEntity> onSuccess, Consumer<String> onError) {
        SystemApi systemApi = ApiRequest.getInstance().create(SystemApi.class);
        retrofit2.Call<UpgradeEntity> request = systemApi.checkForUpdate();
        request.enqueue(new Callback<UpgradeEntity>() {
            @Override
            public void onResponse(Call<UpgradeEntity> call, Response<UpgradeEntity> response) {
                UpgradeEntity result = response.body();
                Log.i(TAG, "onResponse: " + result);
                if (result != null) {
                    if (onSuccess != null) onSuccess.accept(result);
                } else {
                    if (onError != null) onError.accept("result is null");
                }
            }
            @Override
            public void onFailure(Call<UpgradeEntity> call, Throwable t) {
                Log.i(TAG, "onResponse: error " + t.getMessage());
                onError.accept(t.getMessage());
            }
        });
    }

    /**
     * 跳转浏览器下载文件
     * @param activity
     * @param url
     */
    public static void getDown(Activity activity,String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }

}
