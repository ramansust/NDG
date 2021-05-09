package com.nissan.alldriverguide.retrofit;

import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAssistanceTabContent;
import com.nissan.alldriverguide.interfaces.CompleteExploreTabContent;
import com.nissan.alldriverguide.interfaces.CompleteSettingTabContent;
import com.nissan.alldriverguide.interfaces.FindADealerCompleteAPI;
import com.nissan.alldriverguide.interfaces.ParentCarListCompleteAPI;
import com.nissan.alldriverguide.model.DealerUrl;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.model.parentCarList.ParentCarListResponse;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.multiLang.model.SettingsTabModel;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.Values;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nirob on 10/18/17.
 */

public class ApiCall {


    public ApiCall() {

    }

    // post Car Download or Delete status
    public void postCarDownload(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postCarDownload(car_id, lang_id, epub_id, device_id, Values.APK_VERSION);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                }  else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post Car Download Confirmation
    public void postCarDownloadConfirmation(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postCarDownloadConfirmation(car_id, lang_id, epub_id, device_id);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                }  else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post Car Delete status
    public void postCarDelete(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postCarDeleteConfirmation(car_id, lang_id, epub_id, device_id);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                }  else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }


    // post language Download
    public void postLanguageDownload(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postLanguageDownload(car_id, lang_id, epub_id, device_id, Values.APK_VERSION);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                }  else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post language Download confirmation
    public void postLanguageDownloadConfirmation(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postLanguageDownloadConfirmation(car_id, lang_id, epub_id, device_id);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                }  else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post device registration for push
    public void postDeviceRegistrationForPush(String device_id, String reg_id, String device_type, final CompleteAPI completeAPI) {
        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postDeviceRegistrationForPush(device_id, "" + reg_id, device_type, Values.APK_VERSION);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post language Download
    public void postContentDownload(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postDownloadContent(car_id, lang_id, epub_id, device_id);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post content Download
    public void postContentDownloadConfirmation(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postDownloadContentConfirmation(car_id, lang_id, epub_id, device_id);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post add feedback
    public void postAddFeedback(String device_id, String title, String details, String os_version, final CompleteAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postAddFeedback(device_id, title, details, os_version);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseInfo> call, @NonNull Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post ExploreTab Content
    public void postExploreTabContent(String device_id, String language_id, String car_id, String epub_id, String tab_id, final CompleteExploreTabContent completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ExploreTabModel> call = api.postTabWiseContent(device_id, language_id, car_id, epub_id, tab_id);
        call.enqueue(new Callback<ExploreTabModel>() {
            @Override
            public void onResponse(@NonNull Call<ExploreTabModel> call, @NonNull Response<ExploreTabModel> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExploreTabModel> call, @NonNull Throwable t) {
                completeAPI.onFailed(Values.FAILED_STATUS);
            }
        });

    }

    // post ExploreTab Content
    public void postSettingTabContent(String device_id, String language_id, String car_id, String epub_id, String tab_id, final CompleteSettingTabContent completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<SettingsTabModel> call = api.postSettingTabWiseContent(device_id, language_id, car_id, epub_id, tab_id);
        call.enqueue(new Callback<SettingsTabModel>() {
            @Override
            public void onResponse(@NonNull Call<SettingsTabModel> call, @NonNull Response<SettingsTabModel> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SettingsTabModel> call, @NonNull Throwable t) {
                completeAPI.onFailed(Values.FAILED_STATUS);
            }
        });

    }

    // post AssistanceTab Content
    public void postAssistanceTabContent(String device_id, String language_id, String car_id, String epub_id, String tab_id, final CompleteAssistanceTabContent completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<AssistanceInfo> call = api.postAssistanceContent(device_id, language_id, car_id, epub_id, tab_id);
        call.enqueue(new Callback<AssistanceInfo>() {
            @Override
            public void onResponse(@NonNull Call<AssistanceInfo> call, @NonNull Response<AssistanceInfo> response) {
                if (response.isSuccessful()) {
                    completeAPI.onDownloaded(response.body());
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AssistanceInfo> call, @NonNull Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });
    }

    public void getCarList(final String device_id, final String language_id, final CarListACompleteAPI completeAPI) {

        ApiService api = RetrofitClient.getApiService();

        Call<CarListResponse> call = api.carList(device_id, language_id);

        call.enqueue(new Callback<CarListResponse>() {

            @Override
            public void onResponse(@NonNull Call<CarListResponse> call, @NonNull Response<CarListResponse> response) {
                Logger.error("response.code(): ", "" + response.code());
                if (response.isSuccessful()) {
                    CarListResponse carListResponse = response.body();
                    completeAPI.onDownloaded(carListResponse);
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CarListResponse> call, @NonNull Throwable t) {
                Logger.error("Error___", "_______" + t.toString());
                completeAPI.onFailed(t.toString());
            }
        });
    }

    public void getParentCarList(final ParentCarListCompleteAPI parentCarListCompleteAPI) {
        ApiService api = RetrofitClient.getApiService();
        Call<ParentCarListResponse> call = api.parenCarList();
        call.enqueue(new Callback<ParentCarListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ParentCarListResponse> call, @NonNull Response<ParentCarListResponse> response) {
                ParentCarListResponse parentCarListResponse = response.body();
                parentCarListCompleteAPI.onDownloaded(parentCarListResponse);
            }

            @Override
            public void onFailure(@NonNull Call<ParentCarListResponse> call, @NonNull Throwable t) {
                parentCarListCompleteAPI.onFailed(t.toString());
            }
        });
    }

    public void getChildCarList(String device_id, String language_id, String parentId, final CarListACompleteAPI carListACompleteAPI) {
        ApiService api = RetrofitClient.getApiService();
        Call<CarListResponse> call = api.getChildCarList(device_id, language_id, parentId);
        call.enqueue(new Callback<CarListResponse>() {
            @Override
            public void onResponse(@NonNull Call<CarListResponse> call, @NonNull Response<CarListResponse> response) {
                CarListResponse carListResponse = response.body();
                carListACompleteAPI.onDownloaded(carListResponse);
            }

            @Override
            public void onFailure(@NonNull Call<CarListResponse> call, @NonNull Throwable t) {
                carListACompleteAPI.onFailed(t.toString());
            }
        });
    }

    public void getFindADealerUrl(String language_id, final FindADealerCompleteAPI findADealerCompleteAPI) {
        ApiService api = RetrofitClient.getApiService();
        Call<DealerUrl> call = api.getFindADealer(language_id);
        call.enqueue(new Callback<DealerUrl>() {
            @Override
            public void onResponse(@NonNull Call<DealerUrl> call, @NonNull Response<DealerUrl> response) {
                DealerUrl dealerUrl = response.body();
                findADealerCompleteAPI.onDownloaded(dealerUrl);
            }

            @Override
            public void onFailure(@NonNull Call<DealerUrl> call, @NonNull Throwable t) {
                findADealerCompleteAPI.onFailed(t.toString());
            }
        });
    }

}
