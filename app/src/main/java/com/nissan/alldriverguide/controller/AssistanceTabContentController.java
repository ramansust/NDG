package com.nissan.alldriverguide.controller;

import com.nissan.alldriverguide.interfaces.CompleteAssistanceTabContent;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shubha on 6/19/18.
 */

public class AssistanceTabContentController  implements Callback<AssistanceInfo> {

    private static CompleteAssistanceTabContent listener = null;

    public AssistanceTabContentController(CompleteAssistanceTabContent interfaceAssistance) {

        this.listener = interfaceAssistance;
    }

    public void callApi(String device_id, String language_id, String car_id, String epub_id, String tab_id) {
        ApiService api = RetrofitClient.getApiService();

        Call<AssistanceInfo> call = api.postAssistanceContent(device_id, language_id, car_id, epub_id, tab_id);
        call.enqueue(this);
    }


    @Override
    public void onResponse(Call<AssistanceInfo> call, Response<AssistanceInfo> response) {
        if (response.isSuccessful()) {
            AssistanceInfo assistanceInfo = response.body();

            if (assistanceInfo != null) {
                if (this.listener != null) this.listener.onDownloaded(assistanceInfo);
            } else {
                if (this.listener != null) this.listener.onFailed("No content available.");
            }
        } else {
            if (this.listener != null) this.listener.onFailed(response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<AssistanceInfo> call, Throwable t) {
        if (this.listener != null && t != null && t.getMessage() != null) {
            this.listener.onFailed(t.getMessage());
        }
    }
}
