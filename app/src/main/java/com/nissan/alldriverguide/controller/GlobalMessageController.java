package com.nissan.alldriverguide.controller;

import com.nissan.alldriverguide.interfaces.InterfaceGlobalMessageResponse;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shubha on 6/19/18.
 */

public class GlobalMessageController implements Callback<GlobalMsgResponse> {

    private static InterfaceGlobalMessageResponse listener = null;

    public GlobalMessageController(InterfaceGlobalMessageResponse interfaceGlobalMessageResponse) {

        listener = interfaceGlobalMessageResponse;
    }

    public void callApi(String device_id, String language_id) {
        ApiService api = RetrofitClient.getApiService();

        Call<GlobalMsgResponse> call = api.postAlertMsg(device_id, language_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<GlobalMsgResponse> call, Response<GlobalMsgResponse> response) {
        if (response.isSuccessful()) {
            GlobalMsgResponse languageListResponse = response.body();

            if (languageListResponse != null) {
                if (listener != null) listener.onDownloaded(response.body());
            } else {
                if (listener != null) listener.onFailed("No content available.");
            }
        } else {
            if (listener != null) listener.onFailed(response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<GlobalMsgResponse> call, Throwable t) {
        if (listener != null && t != null && t.getMessage() != null) {
            listener.onFailed(t.getMessage());
        }
    }
}
