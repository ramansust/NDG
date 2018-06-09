package com.nissan.alldriverguide.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by nirob on 10/18/17.
 */

public class RetrofitClient {

    // new api online URL
//    private static final String ROOT_URL = "http://104.236.201.218:88/api/";
//    private static final String ROOT_URL = "http://192.168.1.194:8000/api/"; // Local url
//    private static final String ROOT_URL = "http://192.168.1.194:8001/api/"; // Local url
//    private static final String ROOT_URL = "http://104.236.201.218:84/api/"; // Remote url //live url
    private static final String ROOT_URL = "http://192.168.1.194:8001/api/"; // Remote url //niloy pc url


//    private static final String ROOT_URL = "http://178.62.193.6:84/api/"; // provided by rakib

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60 * 2, TimeUnit.SECONDS)
                .connectTimeout(60 * 2, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
