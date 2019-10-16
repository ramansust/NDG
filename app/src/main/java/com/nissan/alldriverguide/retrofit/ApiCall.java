package com.nissan.alldriverguide.retrofit;

import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.FindADealerCompleteAPI;
import com.nissan.alldriverguide.interfaces.InterfaceGlobalMessageResponse;
import com.nissan.alldriverguide.interfaces.CompleteAssistanceTabContent;
import com.nissan.alldriverguide.interfaces.CompleteExploreTabContent;
import com.nissan.alldriverguide.interfaces.CompleteSettingTabContent;
import com.nissan.alldriverguide.interfaces.ParentCarListCompleteAPI;
import com.nissan.alldriverguide.model.DealerUrl;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.model.parentCarList.ParentCarListResponse;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.multiLang.model.SettingsTabModel;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.Values;

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

        Logger.error("car_id","_________" + car_id);
        Logger.error("lang_id","_________" + lang_id);

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postCarDownload(car_id, lang_id, epub_id, device_id, Values.APK_VERSION);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
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
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }

                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post Car Delete status
    public void postCarDelete(String car_id, String lang_id, String epub_id, String device_id, final CompleteAPI completeAPI) {

        Logger.error("car_id", "_________" + car_id);
        Logger.error("lang_id", "_________" + lang_id);
        Logger.error("epub_id", "_________" + epub_id);



        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postCarDeleteConfirmation(car_id, lang_id, epub_id, device_id);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }

                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
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
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
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
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
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
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                } else {
                    completeAPI.onFailed(Values.FAILED_STATUS);
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
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
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
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
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post add feedback
    public void postAddFeedback(String device_id, String title, String details, String os_version, final CompleteAPI completeAPI) {

        Logger.error("device_id", "___________" + device_id);
        Logger.error("title", "___________" + title);
        Logger.error("details", "___________" + details);
        Logger.error("os_version", "___________" + os_version);

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postAddFeedback(device_id, title, details, os_version);
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                } else {
                    //do your failure work
                }
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post Car wise Language List
//    public void postGlobalAlertMsg(String device_id, String language_id, final CompleteCarwiseLanguageListAPI completeAPI) {
//
//        //Creating an object of our api interface
//        ApiService api = RetrofitClient.getApiService();
//        Call<LanguageList> call = api.postCarwiseLanguageList(device_id, language_id);
//        call.enqueue(new Callback<LanguageList>() {
//            @Override
//            public void onResponse(Call<LanguageList> call, Response<LanguageList> response) {
//                completeAPI.onDownloaded(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<LanguageList> call, Throwable t) {
//                completeAPI.onFailed(Values.FAILED_STATUS);
//            }
//        });
//
//    }

    // post ExploreTab Content
    public void postExploreTabContent(String device_id, String language_id, String car_id, String epub_id, String tab_id, final CompleteExploreTabContent completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ExploreTabModel> call = api.postTabWiseContent(device_id, language_id,car_id,epub_id,tab_id);
        call.enqueue(new Callback<ExploreTabModel>() {
            @Override
            public void onResponse(Call<ExploreTabModel> call, Response<ExploreTabModel> response) {
                if(response.isSuccessful()){
                    completeAPI.onDownloaded(response.body());
                }
            }

            @Override
            public void onFailure(Call<ExploreTabModel> call, Throwable t) {
                completeAPI.onFailed(Values.FAILED_STATUS);

            }
        });

    }

    // post ExploreTab Content
    public void postSettingTabContent(String device_id, String language_id, String car_id, String epub_id, String tab_id, final CompleteSettingTabContent completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<SettingsTabModel> call = api.postSettingTabWiseContent(device_id, language_id,car_id,epub_id,tab_id);
        call.enqueue(new Callback<SettingsTabModel>() {
            @Override
            public void onResponse(Call<SettingsTabModel> call, Response<SettingsTabModel> response) {
                if(response.isSuccessful()){
                    completeAPI.onDownloaded(response.body());
                }
            }

            @Override
            public void onFailure(Call<SettingsTabModel> call, Throwable t) {
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
            public void onResponse(Call<AssistanceInfo> call, Response<AssistanceInfo> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        completeAPI.onDownloaded(response.body());
                    } else {
                        completeAPI.onFailed(Values.FAILED_STATUS);
                    }
                }
            }

            @Override
            public void onFailure(Call<AssistanceInfo> call, Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });
    }

    public void getCarList(final String device_id, final String language_id, final CarListACompleteAPI completeAPI) {

        ApiService api = RetrofitClient.getApiService();

        Call<CarListResponse> call = api.carList(device_id, language_id);

        call.enqueue(new Callback<CarListResponse>() {

            @Override
            public void onResponse(Call<CarListResponse> call, Response<CarListResponse> response) {
                Logger.error("response.code(): ",""+ response.code() );
                if (response.isSuccessful()) {
                    CarListResponse carListResponse = response.body();
                    completeAPI.onDownloaded(carListResponse);
                }
            }

            @Override
            public void onFailure(Call<CarListResponse> call, Throwable t) {
                Logger.error("Error___", "_______"+t.toString());
                completeAPI.onFailed(t.toString());
            }
        });
    }

    public void getParentCarList(final ParentCarListCompleteAPI parentCarListCompleteAPI)
    {
        ApiService api =RetrofitClient.getApiService();
        Call<ParentCarListResponse> call = api.parenCarList();
        call.enqueue(new Callback<ParentCarListResponse>() {
            @Override
            public void onResponse(Call<ParentCarListResponse> call, Response<ParentCarListResponse> response) {
                ParentCarListResponse parentCarListResponse=response.body();
                parentCarListCompleteAPI.onDownloaded(parentCarListResponse);
            }

            @Override
            public void onFailure(Call<ParentCarListResponse> call, Throwable t) {
                parentCarListCompleteAPI.onFailed(t.toString());
            }
        });
    }
    public void getChildCarList(String device_id, String language_id,String parentId,final CarListACompleteAPI carListACompleteAPI)
    {
        ApiService api =RetrofitClient.getApiService();
        Call<CarListResponse> call = api.getChildCarList(device_id,language_id,parentId);
        call.enqueue(new Callback<CarListResponse>() {
            @Override
            public void onResponse(Call<CarListResponse> call, Response<CarListResponse> response) {
                CarListResponse carListResponse=response.body();
                carListACompleteAPI.onDownloaded(carListResponse);
            }

            @Override
            public void onFailure(Call<CarListResponse> call, Throwable t) {
                carListACompleteAPI.onFailed(t.toString());
            }
        });
    }
    public void getFindADealerUrl(String language_id, final FindADealerCompleteAPI findADealerCompleteAPI)
    {
        ApiService api =RetrofitClient.getApiService();
        Call<DealerUrl> call = api.getFindADealer(language_id);
        call.enqueue(new Callback<DealerUrl>() {
            @Override
            public void onResponse(Call<DealerUrl> call, Response<DealerUrl> response) {
                DealerUrl dealerUrl=response.body();
                findADealerCompleteAPI.onDownloaded(dealerUrl);
            }

            @Override
            public void onFailure(Call<DealerUrl> call, Throwable t) {
                findADealerCompleteAPI.onFailed(t.toString());
            }
        });
    }

}
