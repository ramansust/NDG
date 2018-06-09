package com.nissan.alldriverguide.retrofit;

import android.app.ProgressDialog;

import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAlertAPI;
import com.nissan.alldriverguide.interfaces.CompleteCarwiseLanguageListAPI;
import com.nissan.alldriverguide.interfaces.CompleteExploreTabContent;
import com.nissan.alldriverguide.model.LanguageList;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
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

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ResponseInfo> call = api.postCarDownload(car_id, lang_id, epub_id, device_id);
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
        Call<ResponseInfo> call = api.postLanguageDownload(car_id, lang_id, epub_id, device_id);
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
        Call<ResponseInfo> call = api.postDeviceRegistrationForPush(device_id, "" + reg_id, device_type);
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

    // post Alert Msg
    public void postGlobalAlertMsg(String device_id, String language_id, final CompleteAlertAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<GlobalMsgResponse> call = api.postAlertMsg(device_id, language_id);
        call.enqueue(new Callback<GlobalMsgResponse>() {
            @Override
            public void onResponse(Call<GlobalMsgResponse> call, Response<GlobalMsgResponse> response) {
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
            public void onFailure(Call<GlobalMsgResponse> call, Throwable t) {
                completeAPI.onFailed(t.getMessage());
            }
        });

    }

    // post Car wise Language List
    public void postGlobalAlertMsg(String device_id, String language_id, final CompleteCarwiseLanguageListAPI completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<LanguageList> call = api.postCarwiseLanguageList(device_id, language_id);
        call.enqueue(new Callback<LanguageList>() {
            @Override
            public void onResponse(Call<LanguageList> call, Response<LanguageList> response) {
                completeAPI.onDownloaded(response.body());
            }

            @Override
            public void onFailure(Call<LanguageList> call, Throwable t) {
                completeAPI.onFailed(Values.FAILED_STATUS);
            }
        });

    }

    // post ExploreTab Content
    public void postExploreTabContent(final ProgressDialog progressDialog,String device_id, String language_id, String car_id, String epub_id, String tab_id, final CompleteExploreTabContent completeAPI) {

        //Creating an object of our api interface
        ApiService api = RetrofitClient.getApiService();
        Call<ExploreTabModel> call = api.postTabWiseContent(device_id, language_id,car_id,epub_id,tab_id);
        call.enqueue(new Callback<ExploreTabModel>() {
            @Override
            public void onResponse(Call<ExploreTabModel> call, Response<ExploreTabModel> response) {
                if(response.isSuccessful()){
                    progressDialog.dismiss();
                    completeAPI.onDownloaded(response.body());
                }
            }

            @Override
            public void onFailure(Call<ExploreTabModel> call, Throwable t) {
                completeAPI.onFailed(Values.FAILED_STATUS);
                progressDialog.dismiss();
            }
        });

    }

    /*************************
     * MultiLangual
     **************************/
    // by auve
//    public static void Alertdetails(String department_id, final String employee_id, final
//    ProgressDialog progressDialog, final InterfaceAlertDetailsResponse
//                                                 interfaceAlertDetailsResponse) {
//
//        //Creating an object of our api interface
//        final ApiService api = RetrofitClient.getApiService();
//        Call<AlertDetails> call = api.getAlertdetails(department_id, employee_id);
//        call.enqueue(new Callback<AlertDetails>() {
//
//            @Override
//            public void onResponse(Call<AlertDetails> call, Response<AlertDetails> response) {
//                if (response.isSuccessful()) {
//                    progressDialog.dismiss();
//                    AlertDetails alertDetails = response.body();
//                    interfaceAlertDetailsResponse.listOfAlertDetailsResponse(alertDetails);
//                } else {
//                    Toast.makeText(MyApplication.getAppContext(), "Response Failed", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AlertDetails> call, Throwable t) {
//                Toast.makeText(MyApplication.getAppContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//            }
//        });
//    }


}
