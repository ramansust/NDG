/*
 * Copyright (c) 20/10/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.controller;

import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDowloadListController implements Callback<CarListResponse> {

    private CarListACompleteAPI carListACompleteAPI;

    public CarDowloadListController(CarListACompleteAPI carListACompleteAPI) {
        this.carListACompleteAPI = carListACompleteAPI;
    }

    public void callApi(String device_id, String language_id) {
        ApiService api = RetrofitClient.getApiService();

        Call<CarListResponse> call = api.carList(device_id, language_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<CarListResponse> call, Response<CarListResponse> response) {
        if (response.isSuccessful()) {
            carListACompleteAPI.onDownloaded(response.body());
        } else {
            carListACompleteAPI.onFailed(response.errorBody().toString());
        }

    }

    @Override
    public void onFailure(Call<CarListResponse> call, Throwable t) {
        carListACompleteAPI.onFailed(t.getMessage());
    }
}
