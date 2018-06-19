package com.nissan.alldriverguide.controller;

import com.nissan.alldriverguide.interfaces.CompleteSettingTabContent;
import com.nissan.alldriverguide.multiLang.model.SettingsTabModel;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shubha on 6/19/18.
 */

public class SettingsTabContentController implements Callback<SettingsTabModel> {

    private static CompleteSettingTabContent listener = null;

    public SettingsTabContentController(CompleteSettingTabContent interfaceSettings) {

        this.listener = interfaceSettings;
    }

    public void callApi(String device_id, String language_id, String car_id, String epub_id, String tab_id) {
        ApiService api = RetrofitClient.getApiService();

        Call<SettingsTabModel> call = api.postSettingTabWiseContent(device_id, language_id, car_id, epub_id, tab_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<SettingsTabModel> call, Response<SettingsTabModel> response) {
        if (response.isSuccessful()) {
            SettingsTabModel settingsTabModel = response.body();

            if (settingsTabModel != null) {
                if (this.listener != null) this.listener.onDownloaded(settingsTabModel);
            } else {
                if (this.listener != null) this.listener.onFailed("No content available.");
            }
        } else {
            if (this.listener != null) this.listener.onFailed(response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<SettingsTabModel> call, Throwable t) {
        if (this.listener != null && t != null && t.getMessage() != null) {
            this.listener.onFailed(t.getMessage());
        }
    }
}
