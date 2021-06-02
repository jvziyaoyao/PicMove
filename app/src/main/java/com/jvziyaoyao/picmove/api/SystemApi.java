package com.jvziyaoyao.picmove.api;

import com.jvziyaoyao.picmove.BuildConfig;
import com.jvziyaoyao.picmove.domain.model.UpgradeEntity;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @program: MicroFire
 * @description:
 * @author: 橘子妖妖
 * @create: 2021-01-25 16:05
 **/
public interface SystemApi {

    @GET(BuildConfig.UPGRADE_URL)
    Call<UpgradeEntity> checkForUpdate();

}
