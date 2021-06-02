package com.jvziyaoyao.picmove.extend;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jvziyaoyao.picmove.domain.vo.ApiResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static kotlin.text.Charsets.UTF_8;

/**
 * @program: FireMobileCommand
 * @description:
 * @author: 橘子妖妖
 * @create: 2020-12-02 15:18
 **/
public class MyConverterFactory extends Converter.Factory {

    public static final String TAG = MyConverterFactory.class.getSimpleName();

    public static MyConverterFactory create(OnResultParseListener onResultParseListener) {
        return create(new Gson(),onResultParseListener);
    }

    @Nullable
    public OnResultParseListener onResultParseListener;

    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static MyConverterFactory create(Gson gson, OnResultParseListener onResultParseListener) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new MyConverterFactory(gson, onResultParseListener);
    }

    private final Gson gson;

    private MyConverterFactory(Gson gson, OnResultParseListener onResultParseListener) {
        this.gson = gson;
        this.onResultParseListener = onResultParseListener;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    /**
     * 请求转换
     * @param <T>
     */
    final class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    /**
     * 响应转换
     * @param <T>
     */
    final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override public T convert(ResponseBody value) throws IOException {
            String response = value.string();
            Log.i(TAG, "convert: ---> " + response);
            ApiResult apiResult = gson.fromJson(response, ApiResult.class);
            //核心代码:  判断 status 是否是后台定义的正常值
            if (onResultParseListener != null) {
                onResultParseListener.onAction(apiResult, value);
            }
            MediaType contentType = value.contentType();
            Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
            Reader reader = new InputStreamReader(inputStream, charset);
            JsonReader jsonReader = gson.newJsonReader(reader);

            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }

        }
    }

    /**
     * 用于观察响应解析出来的内容
     */
    public interface OnResultParseListener {
        void onAction(ApiResult apiResult, ResponseBody value);
    }

}
