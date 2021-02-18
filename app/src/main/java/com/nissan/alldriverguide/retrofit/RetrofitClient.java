package com.nissan.alldriverguide.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by nirob on 10/18/17.
 */

public class RetrofitClient {

    //    new api online URL
    //private static final String ROOT_URL = "http://192.168.2.122:8000/api/"; // Remote url //live url
//    private static final String ROOT_URL = "http://192.168.1.194:8001/api/"; // Remote url //niloy pc url
//    private static final String ROOT_URL = "http://178.62.193.6:84/api/"; // dev server / test server
//    private static final String ROOT_URL = "http://cdnuat.maruboshi.nl/api/"; // dev server /maruboshi
//    private static final String ROOT_URL = "http://104.236.201.218:84/api/"; // Remote server second instance
//    private static final String ROOT_URL = "http://213.136.27.240:8001/api/"; // Remote server second instance
    private static final String ROOT_URL = "http://cdnuat.maruboshi.nl/m2/api/"; // Remote server second instance//dev url
//    private static final String ROOT_URL = "http://cdn.maruboshi.nl/api/"; // Remote server second instance//prod url


    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60 * 2, TimeUnit.SECONDS)
                .connectTimeout(60 * 2, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
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
