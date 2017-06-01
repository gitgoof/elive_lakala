package com.lakala.elive.map.api;

//import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
/*

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
*/

/**
 * Created by xg on 2016/6/24.
 */
public class Api {
    public static final String BASE_TRC_URL = "http://routes.lakala.dituwuyou.com:5678";

    private static Retrofit retrofit = null;

    public static ApiService createApiService() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.addInterceptor(interceptor);
            builder.addNetworkInterceptor(new StethoInterceptor());//facebook抓包
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_TRC_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }


    static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws java.io.IOException {
            Request request = chain.request().newBuilder()
                    .addHeader("Source", "android")
                    .addHeader("Accept", "application/json,text/javascript,*/*")
                    .build();
            Response response = chain.proceed(request);
            return response;
        }
    };

    /**
     * 得到解析库
     *
     * @return
     */
    public static Gson getGson() {//能够解析RealmObject实体类
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)//支持hashmap
                .serializeNulls()
                .create();
    }
}
