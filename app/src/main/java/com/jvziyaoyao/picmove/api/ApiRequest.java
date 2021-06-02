package com.jvziyaoyao.picmove.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jvziyaoyao.picmove.BuildConfig;
import com.jvziyaoyao.picmove.domain.vo.ApiResult;
import com.jvziyaoyao.picmove.extend.MyConverterFactory;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class ApiRequest {

    public static final String TAG = ApiRequest.class.getSimpleName();

    public static String BASE_URI = BuildConfig.BASE_URL;

    private final static int Connect_Timeout = 30;
    private final static int Read_Timeout = 30;
    private final static int Write_Timeout = 30;

    private static ApiRequest apiRequest;

    private MyConverterFactory gsonConverterFactory;

    @Getter
    private Gson gson;

    private ApiRequest() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        // 由于响应后的结果如果与事先提供的泛型不符，会导致Gson无法继续解析，
        // 如：ApiResult<ClxxEntity>,返回结果中如果是403，则data:12233333,
        // Gson无法正常解析，Integer转Object会报错，然后直接提示网络故障，这不是我想要的，所以要加上拦截判断
        gsonConverterFactory = MyConverterFactory.create(gson, new MyConverterFactory.OnResultParseListener() {
            @Override
            public void onAction(ApiResult apiResult, ResponseBody value) {
                Log.i(TAG, "onAction: ---> " + apiResult.toString());
                // 全局拦截判断
                if (apiResult.getStatusCode() == 403) {
                    Log.i(TAG, "intercept: ---> 登录信息过期 ");
                }
            }
        });
        // gsonConverterFactory = GsonConverterFactory.create(gson);
    }

    public static ApiRequest getInstance() {
        if (apiRequest == null) {
            apiRequest = new ApiRequest();
        }
        return apiRequest;
    }

    public static OkHttpClient createOkClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Connect_Timeout, TimeUnit.SECONDS)
                .readTimeout(Read_Timeout, TimeUnit.SECONDS)
                .writeTimeout(Write_Timeout, TimeUnit.SECONDS)
                .addInterceptor(new ApiInterceptor())
                .build();
        return client;
    }

    public <T> T create(@NonNull Class<T> modelClass) {
        return create(BASE_URI, modelClass);
    }

    public <T> T create(@NotNull String baseUri, @NonNull Class<T> modelClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUri)
                .client(createOkClient())
                .addConverterFactory(gsonConverterFactory)
                .build();

        return retrofit.create(modelClass);
    }

    /**
     * 统一拦截设置（请求头）
     */
    public static class ApiInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            String token = "";
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("token", token)
                    .build();
            String s = request.url().toString();
            Log.i(TAG, "intercept: s -> " + s);
            return chain.proceed(request);
        }
    }

}
