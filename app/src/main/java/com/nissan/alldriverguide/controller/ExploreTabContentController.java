package com.nissan.alldriverguide.controller;

import com.nissan.alldriverguide.interfaces.CompleteExploreTabContent;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shubha on 6/19/18.
 */

public class ExploreTabContentController implements Callback<ExploreTabModel> {

    private static CompleteExploreTabContent listener = null;

    public ExploreTabContentController(CompleteExploreTabContent interfaceExplore) {

        listener = interfaceExplore;
    }

    public void callApi(String device_id, String language_id, String car_id, String epub_id, String tab_id) {
        ApiService api = RetrofitClient.getApiService();

        Call<ExploreTabModel> call = api.postTabWiseContent(device_id, language_id, car_id, epub_id, tab_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<ExploreTabModel> call, Response<ExploreTabModel> response) {
        if (response.isSuccessful()) {
            ExploreTabModel exploreTabModel = response.body();
            if (exploreTabModel != null) {
                if (listener != null) listener.onDownloaded(exploreTabModel);
            } else {
                if (listener != null) listener.onFailed("No content available.");
            }
        } else {
            if (listener != null) listener.onFailed(response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<ExploreTabModel> call, Throwable t) {
        if (listener != null && t != null && t.getMessage() != null) {
            listener.onFailed(t.getMessage());
        }
    }
}
