package com.nissan.alldriverguide.controller;

import com.nissan.alldriverguide.multiLang.interfaces.InterfaceLanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageListResponse;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;

import java.util.Objects;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shubha on 6/19/18.
 */

public class LanguageSelectionController implements Callback<LanguageListResponse> {

    private static InterfaceLanguageListResponse listener = null;

    public LanguageSelectionController(InterfaceLanguageListResponse interfaceLanguageListResponse) {

        listener = interfaceLanguageListResponse;
    }

    public void callApi(String device_id, String car_id) {
        ApiService api = RetrofitClient.getApiService();

        Call<LanguageListResponse> call = api.languageList(device_id, car_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<LanguageListResponse> call, Response<LanguageListResponse> response) {
        if (response.isSuccessful()) {
            LanguageListResponse languageListResponse = response.body();

            if (languageListResponse != null) {
                if (listener != null)
                    listener.languageListDownloaded(languageListResponse.getLanguageList());
            } else {
                if (listener != null)
                    listener.languageListFailed("No content available.");
            }
        } else {
            if (listener != null)
                listener.languageListFailed(Objects.requireNonNull(response.errorBody()).toString());
        }
    }

    @Override
    public void onFailure(@NonNull Call<LanguageListResponse> call, @NonNull Throwable t) {
        if (listener != null && t.getMessage() != null) {
            listener.languageListFailed(t.getMessage());
        }
    }
}
