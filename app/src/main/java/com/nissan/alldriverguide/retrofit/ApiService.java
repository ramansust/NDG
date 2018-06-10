package com.nissan.alldriverguide.retrofit;

import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.SettingsTabModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by nirob on 10/18/17.
 */

public interface ApiService {

    // car downloading
    @FormUrlEncoded
    @POST("car_download/")
    Call<ResponseInfo> postCarDownload(@Field("car_id") String car_id, @Field("language_id") String language_id,
                                       @Field("epub_id") String ePub_id, @Field("device_id") String device_id);

    // car downloading confirmation post
    @FormUrlEncoded
    @POST("download_confirmation/")
    Call<ResponseInfo> postCarDownloadConfirmation(@Field("car_id") String car_id, @Field("language_id") String language_id,
                                                @Field("epub_id") String ePub_id, @Field("device_id") String device_id);

    // car deleting confirmation post
    @FormUrlEncoded
    @POST("car_delete/")
    Call<ResponseInfo> postCarDeleteConfirmation(@Field("car_id") String car_id, @Field("language_id") String language_id,
                                                @Field("epub_id") String ePub_id, @Field("device_id") String device_id);

    // language download post (Language Fragment)
    @FormUrlEncoded
    @POST("language_download/")
    Call<ResponseInfo> postLanguageDownload(@Field("car_id") String car_id, @Field("language_id") String language_id,
                                              @Field("epub_id") String ePub_id, @Field("device_id") String device_id);


    // keyword search post (Language Fragment)
    @FormUrlEncoded
    @POST("download_confirmation/")
    Call<ResponseInfo> postLanguageDownloadConfirmation(@Field("car_id") String car_id, @Field("language_id") String language_id,
                                                        @Field("epub_id") String epub_id, @Field("device_id") String device_id);

    // device registration for push notification
    @FormUrlEncoded
    @POST("device/registration/")
    Call<ResponseInfo> postDeviceRegistrationForPush(@Field("device_id") String device_id, @Field("fcm_reg_id") String reg_id, @Field("device_type") String device_type);

    // get content download
    @FormUrlEncoded
    @POST("update/epub_content/")
    Call<ResponseInfo> postDownloadContent(@Field("car_id") String car_id, @Field("language_id") String language_id,
                                           @Field("epub_id") String epub_id, @Field("device_id") String device_id);

    // post content download confirmation
    @FormUrlEncoded
    @POST("epub_download_confirmation/")
    Call<ResponseInfo> postDownloadContentConfirmation (@Field("car_id") String car_id, @Field("language_id") String language_id,
                                                        @Field("epub_id") String epub_id, @Field("device_id") String device_id);


    // post add feedback
    @FormUrlEncoded
    @POST("add_feedback/")
    Call<ResponseInfo> postAddFeedback(@Field("device_id") String device_id, @Field("title") String title,
                                       @Field("details") String details, @Field("os_version") String os_version);

    /** MultiLanguage ***************************/
    // post add feedback
    @FormUrlEncoded
    @POST("global_message/")
    Call<GlobalMsgResponse> postAlertMsg(@Field("device_id") String device_id, @Field("language_id") String language_id);

    @FormUrlEncoded
    @POST("car_wise_language_list/")
    Call<LanguageListResponse> languageList(@Field("device_id") String device_id, @Field("car_id") String car_id);

    // post Explore Tab Content
    @FormUrlEncoded
    @POST("get_tab_content/")
    Call<ExploreTabModel> postTabWiseContent(@Field("device_id") String device_id, @Field("language_id") String language_id, @Field("car_id") String car_id, @Field("epub_id") String epub_id, @Field("tab_id") String tab_id);

    // post Setting Tab Content
    @FormUrlEncoded
    @POST("get_tab_content/")
    Call<SettingsTabModel> postSettingTabWiseContent(@Field("device_id") String device_id, @Field("language_id") String language_id, @Field("car_id") String car_id, @Field("epub_id") String epub_id, @Field("tab_id") String tab_id);

    // post Assistance Tab Content
    @FormUrlEncoded
    @POST("get_tab_content/")
    Call<AssistanceInfo> postAssistanceContent(@Field("device_id") String device_id, @Field("language_id") String language_id, @Field("car_id") String car_id, @Field("epub_id") String epub_id, @Field("tab_id") String tab_id);

}
