package com.nissan.alldriverguide;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.adapter.LanguageSelectionAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAlertAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.LanguageInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.interfaces.InterfaceLanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.AlertMessage;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.multiLang.model.Tutorial;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.AppConfig;
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.SearchDBAsync;
import com.nissan.alldriverguide.utils.Values;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LanguageSelectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String[] languageName ;/*= {"English", "Deutsch", "Français", "Italiano", "Español", "Nederlands", "Русский", "Svenska", "Norsk", "Polski", "Suomi", "Português"};*/
    private String[] languageShortName; /*= {"en", "de", "fr", "it", "es", "nl", "ru", "sv", "no", "pl", "fi", "pt"};*/
    private int[] languageImage = {R.drawable.united_kingdom, R.drawable.germany, R.drawable.france, R.drawable.italy, R.drawable.spain, R.drawable.netherlands, R.drawable.russia, R.drawable.sweden, R.drawable.norway, R.drawable.poland, R.drawable.finland, R.drawable.portugal};

    private ListView lstView;
    private ArrayList<LanguageInfo> list;
    private LanguageSelectionAdapter adapter;
    private PreferenceUtil preferenceUtil;
    private DisplayMetrics metrics;
    public Resources resources;
    private Tracker tracker;
    private CommonDao commonDao;
    private ProgressDialog progressDialog;
    private String carName = "";
    private Activity activity;
    private Context context;
    private LanguageListResponse languageListResponses;
    private String[] languageDialog, cancelLangDownload, okLangDownload;
    private String deviceDensity;
    private String langFlagUri;
    public LanguageSelectionActivity() {
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        progressDialog = new ProgressDialogController(this).showDialog("adfkaljshfkj");
        deviceDensity = NissanApp.getInstance().getDensityName(context);
        getDataCarWise();
        initViews();
        setListener();
    }

//    private ArrayList<LanguageList> getDataFromStorage() {
//        String key = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.CAR_LANGUAGE_LIST;
//
//        Type type = new TypeToken<ArrayList<LanguageList>>() {        }.getType();
//        return new Gson().fromJson(new PreferenceUtil(this).retrieveMultiLangData(key), type);
//
//    }

    private void getDataCarWise() {
        new ApiCall().getLanguageList("e224fb09fb8daee4", "1", progressDialog , new InterfaceLanguageListResponse() {
            @Override
            public void languageListResponse(LanguageListResponse languageListResponse) {
                languageListResponses = languageListResponse;
                String key = Values.carType + "_" + Values.CAR_LANGUAGE_LIST;
//
                preferenceUtil.storeMultiLangData(languageListResponses.getLanguageList(), key);
//
                languageName = new String[languageListResponses.getLanguageList().size()];
                languageShortName = new String[languageListResponses.getLanguageList().size()];
                languageDialog = new String[languageListResponses.getLanguageList().size()];
                cancelLangDownload = new String[languageListResponses.getLanguageList().size()];
                okLangDownload = new String[languageListResponses.getLanguageList().size()];

                for(int i = 0; i <languageListResponses.getLanguageList().size(); i++){
                    Log.e("---", "languageListResponse: "+ languageListResponse.getLanguageList().get(i).getLanguageName() );
                    languageName[i] = (languageListResponse.getLanguageList().get(i).getLanguageName());
                    languageShortName[i] = (languageListResponse.getLanguageList().get(i).getLanguageShortcode());
                    languageDialog[i] = (languageListResponse.getLanguageList().get(i).getAlertMessage());
                    cancelLangDownload[i] = (languageListResponse.getLanguageList().get(i).getCancel());
                    okLangDownload[i] = (languageListResponse.getLanguageList().get(i).getOk());

                    if("xxxhdpi".contains(deviceDensity)){
                        langFlagUri = languageListResponse.getLanguageList().get(i).getLanguageFlag().getXxxhdpi();
                    }
                    if("xxhdpi".contains(deviceDensity)){
                        langFlagUri = languageListResponse.getLanguageList().get(i).getLanguageFlag().getXxxhdpi();
                    }
                    if("xhdpi".contains(deviceDensity)){
                        langFlagUri = languageListResponse.getLanguageList().get(i).getLanguageFlag().getXxxhdpi();
                    }
                    if("hdpi".contains(deviceDensity)){
                        langFlagUri = languageListResponse.getLanguageList().get(i).getLanguageFlag().getXxxhdpi();
                    }
                    if("mdpi".contains(deviceDensity)){
                        langFlagUri = languageListResponse.getLanguageList().get(i).getLanguageFlag().getXxxhdpi();
                    }
                    if("ldpi".contains(deviceDensity)){
                        langFlagUri = languageListResponse.getLanguageList().get(i).getLanguageFlag().getXxxhdpi();
                    }
                }
                loadData();
            }
        });
    }

    /**
     * Initialized all variable
     */
    private void initViews() {
        activity = LanguageSelectionActivity.this;
        context = getApplicationContext();
        lstView = (ListView) findViewById(R.id.lst_view);
        list = new ArrayList<>();
        preferenceUtil = new PreferenceUtil(getApplicationContext());
        commonDao = CommonDao.getInstance();
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    /**
     * Load the initial data into list
     */
    private void loadData() {
        Log.e("***", "loadData: "+ languageName.length);
        for (int i = 0; i < languageName.length; i++) {
            LanguageInfo info = new LanguageInfo(i, languageName[i], false, languageImage[i]);
            // display 2 languages only for car type 2 and 5
            if (Values.carType == 2 || Values.carType == 5) {
                if (i == 0 || i == 6) {
                    list.add(info);
                }
            } else {
                if (Values.carType == 7 || Values.carType == 8 || Values.carType == 9) {
                    if (i != 6 && i != 8) {
                        list.add(info);
                    }
                } else if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 6 || Values.carType == 10 || Values.carType == 11 || Values.carType == 12 || Values.carType == 13) {
                    if (i != 8) {
                        list.add(info);
                    }
                } else {
                    list.add(info);
                }
            }
        }

        adapter = new LanguageSelectionAdapter(getApplicationContext(), list, false);
        lstView.setAdapter(adapter);
        lstView.setDivider(null);
        ColorDrawable sage = new ColorDrawable(this.getResources().getColor(R.color.line_color));
        lstView.setDivider(sage);
        lstView.setDividerHeight(4);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LanguageInfo info = (LanguageInfo) parent.getAdapter().getItem(position);
        preferenceUtil.setSelectedLang(languageShortName[info.getId()]); // here save the selected language sort name into preference
        Logger.error("onItemClick: ", "" + info.getId());
        Logger.error("onItemClick: ", "" + languageShortName[info.getId()]);

        loadResource();

        if (NissanApp.getInstance().createPath(Values.PATH)) {
            if (DetectConnection.checkInternetConnection(getApplicationContext())) {
                showCarDownloadDialogForSingleCar(info);
                changeGlobalAlertMsg();
            } else {
//                NissanApp.getInstance().showInternetAlert(LanguageSelectionActivity.this, getResources().getString(R.string.internet_connect));
                String internetCheckMessage = getInternetCheckMessage();
                NissanApp.getInstance().showInternetAlert(LanguageSelectionActivity.this, internetCheckMessage);
            }
        } else {

        }
    }

    private void changeGlobalAlertMsg(){
        new ApiCall().postGlobalAlertMsg("e224fb09fb8daee4", NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang())+"", new CompleteAlertAPI() {
            @Override
            public void onDownloaded(GlobalMsgResponse responseInfo) {
                if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {

                    String key_global_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.GLOBAL_MSG_KEY;
                    String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.GLOBAL_ALERT_MSG_KEY;

                    preferenceUtil.storeMultiLangData(responseInfo.getAlertMessage(), key_global_alert_message);
                    preferenceUtil.storeMultiLangData(responseInfo.getGlobalMessage(), key_global_message);

                    NissanApp.getInstance().setGlobalMessageArrayList(responseInfo.getGlobalMessage());
                    NissanApp.getInstance().setAlertMessageGlobalArrayList(responseInfo.getAlertMessage());

                    showCarDownloadDialogForSingleCar();

                }
            }

            @Override
            public void onFailed(String failedReason) {
                Logger.error("changeGlobal", "********Fail******" + failedReason);
            }
        });
    }

    private void loadResource() {
        resources = new Resources(getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(LanguageSelectionActivity.this, preferenceUtil.getSelectedLang()));
    }

    /**
     * Start Car download procedure
     */
    private void startCarDownloadProcedure() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialogController(activity).showDialog(resources.getString(R.string.start_download));
            }
        });

        new ApiCall().postCarDownload(Values.carType + "", "" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()), "0", NissanApp.getInstance().getDeviceID(this), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo info) {
                if (AppConfig.IS_APP_ONLINE ? Values.SUCCESS_STATUS.equalsIgnoreCase(info.getStatusCode()) && !TextUtils.isEmpty(info.getAssetsUrl()) && !TextUtils.isEmpty(info.getLangUrl()) : Values.SUCCESS_STATUS.equalsIgnoreCase(info.getStatusCode())) {

                    preferenceUtil.storeMultiLangData(info.getTutorials(), Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.TUTORIAL_KEY);
                    preferenceUtil.storeMultiLangData(info.getTabMenu(), Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.TAB_MENU_KEY);


                    //call this after getting the asset link successfully
                    startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? info.getAssetsUrl() : NissanApp.getInstance().getAssetsURL(Values.carType), Values.PATH, AppConfig.IS_APP_ONLINE ? info.getLangUrl() : NissanApp.getInstance().getLanguageURL((Values.carType), preferenceUtil.getSelectedLang()), NissanApp.getInstance().getCarPath(Values.carType));
                } else {
                    showErrorDialog("No content found.");
                    dismissDialog();
                    Logger.error("Car downloading failed", "____________" + info.getMessage());
                }
            }

            @Override
            public void onFailed(String failedReason) {
                showErrorDialog(failedReason);
                dismissDialog();
                Logger.error("Car downloading failed", "____________" + failedReason);
            }
        });

    }

    private void startCarAssetsDownload(String assetsSource, String assetsDestination, final String langSource, String langDestination) {
        // downloadCarAssets method actually download car asset and language epub are both
        new MADownloadManager(activity, context).downloadCarAssets(false, NissanApp.getInstance().getCarName(Values.carType), assetsSource, assetsDestination, langSource, langDestination, new DownloaderStatus() {
            @Override
            public boolean onComplete(boolean b) {
                if (b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (progressDialog != null) {
                                progressDialog.setMessage(resources.getString(R.string.data_syncing));
                            }

                            new SearchDBAsync(LanguageSelectionActivity.this, preferenceUtil.getSelectedLang(), Values.carType) {

                                @Override
                                public void onComplete(boolean status) {
                                    if (status) {
                                        postCarDownloadSuccessfulStatus();
                                    } else {
                                        errorFileDelete(Values.carType);
                                    }

                                }
                            }.execute();
                        }
                    });

                }
                return false;
            }

            @Override
            public int onError(int i) {
                errorFileDelete(Values.carType);
                return 0;
            }

            @Override
            public boolean internetConnection(boolean b) {
                errorFileDelete(Values.carType);
                return false;
            }

            @Override
            public boolean urlReachable(boolean b) {
                errorFileDelete(Values.carType);
                return false;
            }

            @Override
            public boolean destinationExists(boolean b) {
                errorFileDelete(Values.carType);
                return false;
            }

            @Override
            public boolean sourcePath(boolean b) {
                errorFileDelete(Values.carType);
                return false;
            }

            @Override
            public boolean destinationPath(boolean b) {
                errorFileDelete(Values.carType);
                return false;
            }

            @Override
            public void downloadCompletion(Float aFloat) {
                String formattedString = String.format("%.02f", aFloat);
                if (progressDialog != null) {
                    progressDialog.setMessage(carName + "\n" + getResources().getString(R.string.alert_downloading) + formattedString + "%");
                }
            }

            @Override
            public void init() {
                if (Values.carType == 11) {
                    carName = "All New Nissan Micra";
                } else {
                    carName = NissanApp.getInstance().getCarName(Values.carType);
                }
                if (progressDialog == null) {
                    progressDialog = new ProgressDialogController(LanguageSelectionActivity.this).downloadProgress(carName + "\n" + getResources().getString(R.string.alert_downloading));
                }
            }
        });
    }

    private void postCarDownloadSuccessfulStatus() {
        new ApiCall().postCarDownloadConfirmation(Values.carType + "", "" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()), "0", NissanApp.getInstance().getDeviceID(this), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    sendMsgToGoogleAnalytics(NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOWNLOAD + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM);
                    // set the car path according to car type
                    Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);

                    commonDao.updateDateAndStatus(getBaseContext(), Values.carType, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                    if (Values.carType == 1 || Values.carType == 4) {
                        if (commonDao.getStatus(getBaseContext(), Values.carType + 1) == 2) {
                            commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 1, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                        }
                    }
                    commonDao.updateLanguageStatus(getBaseContext(), Values.carType, preferenceUtil.getSelectedLang());

                    // for downloaded car selection
                    NissanApp.getInstance().setCarAllList(commonDao.getAllCarList(getBaseContext()));
                    for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
                        if (NissanApp.getInstance().getCarAllList().get(i).getId() == Values.carType) {
                            NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(1);
                        } else {
                            NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(0);
                        }
                    }

                    commonDao.updateAllPushContentStatusForSingleCar(context, Values.carType, NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()));

                    dismissDialog();
                    goToNextPage();
                } else {
                    showErrorDialog("Confirmation send error.");
                    dismissDialog();
                }
            }

            @Override
            public void onFailed(String failedReason) {
                showErrorDialog(failedReason);
                dismissDialog();
                Logger.error("Car download confirmation failed", "_____________" + failedReason);
            }
        });
    }


    /**
     * Indicate MainActivity after downloading car
     */
    private void goToNextPage() {
        if (preferenceUtil.getIsFirstTime()) {
            preferenceUtil.setIsFirstTime(false);
            startActivity(new Intent(LanguageSelectionActivity.this, TutorialActivity.class).putExtra("from", "activity"));
        } else {
            startActivity(new Intent(LanguageSelectionActivity.this, MainActivity.class));
        }
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void sendMsgToGoogleAnalytics(String msgName) {
        // Get a Tracker (should auto-report)
        ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Get tracker.
        tracker = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        tracker.setScreenName(msgName.toLowerCase());
        tracker.enableAdvertisingIdCollection(true);

        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showCarDownloadDialogForSingleCar(LanguageInfo info) {
        final Dialog dialog = new DialogController(LanguageSelectionActivity.this).langDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(languageDialog[info.getId()]);

        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnCancel.setText(cancelLangDownload[info.getId()]);
        btnOk.setText(okLangDownload[info.getId()]);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startCarDownloadProcedure();
            }
        });

        dialog.show();
    }

    private void errorFileDelete(int carType) {
        try {
            dismissDialog();
            FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carType)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(getSupportFragmentManager(), "error_fragment");
    }

    private String getInternetCheckMessage() {

        String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.GLOBAL_ALERT_MSG_KEY;

        List<AlertMessage> alertMessageArrayList = NissanApp.getInstance().getAlertMessageGlobalArrayList();
        if (alertMessageArrayList == null || alertMessageArrayList.size() == 0) {
            Type type = new TypeToken<ArrayList<AlertMessage>>() {
            }.getType();
            alertMessageArrayList = new Gson().fromJson(new PreferenceUtil(this).retrieveMultiLangData(key_global_alert_message), type);
            NissanApp.getInstance().setAlertMessageGlobalArrayList(alertMessageArrayList);
        }


        for (int i = 0; i < alertMessageArrayList.size(); i++) {

            if (alertMessageArrayList.get(i).getType().equalsIgnoreCase(Values.ALERT_MSG_TYPE_INTERNET))
                return alertMessageArrayList.get(i).getMsg();

        }

        return getResources().getString(R.string.internet_connect);
    }
}
