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

import java.util.Objects;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDowloadListController implements Callback<CarListResponse> {

    private final CarListACompleteAPI carListACompleteAPI;

    public CarDowloadListController(CarListACompleteAPI carListACompleteAPI) {
        this.carListACompleteAPI = carListACompleteAPI;
    }

    public void callApi(String device_id, String language_id) {
        ApiService api = RetrofitClient.getApiService();

        Call<CarListResponse> call = api.carList(device_id, language_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<CarListResponse> call, Response<CarListResponse> response) {
        if (response.isSuccessful()) {
            carListACompleteAPI.onDownloaded(response.body());
        } else {
            carListACompleteAPI.onFailed(Objects.requireNonNull(response.errorBody()).toString());
        }

    }

    @Override
    public void onFailure(@NonNull Call<CarListResponse> call, @NonNull Throwable t) {
        carListACompleteAPI.onFailed(t.getMessage());
    }
}
