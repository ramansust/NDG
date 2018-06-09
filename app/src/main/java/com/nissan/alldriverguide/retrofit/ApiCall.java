package com.nissan.alldriverguide.retrofit;

import android.util.Log;
import android.widget.Toast;

import com.nissan.alldriverguide.MyApplication;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.interfaces.InterfaceLanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
import com.nissan.alldriverguide.multiLang.model.LanguageListResponse;
import com.nissan.alldriverguide.utils.Values;

import java.util.List;

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

    /*************************
     * MultiLangual
     **************************/
    public void getLanguageList(final String device_id, final String car_id,/* final ProgressDialog progressDialog,*/ final InterfaceLanguageListResponse interfaceLanguageListResponse) {

        ApiService api = RetrofitClient.getApiService();

        Call<LanguageListResponse> call = api.languageList(device_id, car_id);

            call.enqueue(new Callback<LanguageListResponse>() {

                @Override
                public void onResponse(Call<LanguageListResponse> call, Response<LanguageListResponse> response) {
                    Log.e("response.code(): ",""+ response.code() );
                    if (response.isSuccessful()) {
                        LanguageListResponse languageListResponse = response.body();
                        List<LanguageList> languageLists = response.body().getLanguageList();
                        Log.e("--", "onResponse: "+ languageLists.size());
                        interfaceLanguageListResponse.languageListResponse(languageListResponse);

//                        progressDialog.dismiss();
//                        List<LanguageList> languageLists = response.body();
//                        interfaceLanguageListResponse.languageListResponse(languageLists);
//                        interfaceLanguageListResponse.languageListResponse(languageLists);
//                        Toast.makeText(context, "Response Success", Toast.LENGTH_LONG).show();

                    } else {
//                        progressDialog.dismiss();
                        Toast.makeText(MyApplication.getAppContext(), "Response Failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LanguageListResponse> call, Throwable t) {
                    Log.e("Error___", "_______"+t.toString());
                    Toast.makeText(MyApplication.getAppContext(), "onFailure",Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();

                }
            });
    }
}
