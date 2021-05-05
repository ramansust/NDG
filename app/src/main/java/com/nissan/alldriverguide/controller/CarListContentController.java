package com.nissan.alldriverguide.controller;

import android.util.Log;

import com.google.gson.Gson;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shubha on 6/20/18.
 */

public class CarListContentController implements Callback<CarListResponse> {

    private static CarListACompleteAPI listener = null;

    public CarListContentController(CarListACompleteAPI interfaceCarList) {

        listener = interfaceCarList;
    }

    public void callApi(String device_id, String language_id) {

        ApiService api = RetrofitClient.getApiService();

        Call<CarListResponse> call = api.carList(device_id, language_id);
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<CarListResponse> call, Response<CarListResponse> response) {
        if (response.isSuccessful()) {
            CarListResponse carListResponse = response.body();

            Log.e("Carlist ", "new json 1 " + new Gson().toJson(response.body()));

            if (carListResponse != null) {
                if (listener != null) listener.onDownloaded(carListResponse);
            } else {
                if (listener != null) listener.onFailed("No content available.");
            }
        } else {
            if (listener != null) listener.onFailed(response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<CarListResponse> call, Throwable t) {
        if (listener != null && t != null && t.getMessage() != null) {
            listener.onFailed(t.getMessage());
        }
    }

    public void removeListener() {
        listener = null;
    }
}
