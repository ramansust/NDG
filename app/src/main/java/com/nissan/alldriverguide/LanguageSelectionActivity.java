package com.nissan.alldriverguide;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nissan.alldriverguide.adapter.LanguageSelectionAdapter;
import com.nissan.alldriverguide.controller.CarListContentController;
import com.nissan.alldriverguide.controller.GlobalMessageController;
import com.nissan.alldriverguide.controller.LanguageSelectionController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.InterfaceGlobalMessageResponse;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.LanguageInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.interfaces.InterfaceLanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.AlertMessage;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.nissan.alldriverguide.utils.Values.DATA_SYNCING;
import static com.nissan.alldriverguide.utils.Values.STARTING_DOWNLOAD;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class LanguageSelectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, InterfaceLanguageListResponse, InterfaceGlobalMessageResponse, CarListACompleteAPI {

    private static final String TAG = "LanguageSelectionActivity";
    public Resources resources;

    //    private int[] languageImage = {R.drawable.united_kingdom, R.drawable.germany, R.drawable.france, R.drawable.italy, R.drawable.spain, R.drawable.netherlands, R.drawable.russia, R.drawable.sweden, R.drawable.norway, R.drawable.poland, R.drawable.finland, R.drawable.portugal};
    private String[] languageName;/*= {"English", "Deutsch", "Fran??ais", "Italiano", "Espa??ol", "Nederlands", "??????????????", "Svenska", "Norsk", "Polski", "Suomi", "Portugu??s"};*/
    private ListView lstView;
    private ArrayList<LanguageInfo> list;
    private LanguageSelectionAdapter adapter;
    private PreferenceUtil preferenceUtil;
    private CommonDao commonDao;
    private Activity activity;
    private Context context;
    private ProgressDialog progressDialog;
    private String deviceDensity;
    private LanguageList selectedLangModel = new LanguageList();
    private List<LanguageList> _languageLists = new ArrayList<>();
    private LanguageSelectionController controller;
    private GlobalMessageController controllerGlobalMsg;
    private CarListContentController carListContentController;
    private ProgressBar progressBar;
    private TextView tvNoContent;
    private boolean isActivityOnPaused = false;

    public LanguageSelectionActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        initViews();
        setListener();
        setAdapterFromDB();
    }

    private List<LanguageList> getLanguageDataFromSP(Context context) {

        Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        return new Gson().fromJson(new PreferenceUtil(context).retrieveMultiLangData(Values.carType + "_" + Values.CAR_LANGUAGE_LIST), type);
    }

    private void setAdapterFromDB() {

        adapter = new LanguageSelectionAdapter(this, new ArrayList<>(), false);
        lstView.setAdapter(adapter);
        lstView.setDivider(null);
        ColorDrawable sage = new ColorDrawable(this.getResources().getColor(R.color.line_color));
        lstView.setDivider(sage);
        lstView.setDividerHeight(4);

        _languageLists = getLanguageDataFromSP(context);
        if (_languageLists != null && _languageLists.size() > 0) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.GONE);
            populateDataIntoList();
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (DetectConnection.checkInternetConnection(activity)) {
            controller.callApi(NissanApp.getInstance().getDeviceID(this)/*"246E5A50-B79F-4019-82ED-877BF53FD617"*/, Values.carType + "");
        } else {
            progressBar.setVisibility(View.GONE);
            String internetCheckMessage = NissanApp.getInstance().getAlertMessage(activity, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
            showNoInternetDialogue(internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage);
        }
    }

    @Override
    public void languageListDownloaded(List<LanguageList> languageLists) {

        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }

        tvNoContent.setVisibility(View.GONE);

        if (languageLists != null && languageLists.size() > 0) {
            tvNoContent.setVisibility(View.GONE);
            NissanApp.getInstance().setCarWiseLanguageList(languageLists);
            new PreferenceUtil(MyApplication.getAppContext()).storeMultiLangData(languageLists, Values.carType + "_" + Values.CAR_LANGUAGE_LIST);
            _languageLists = languageLists;
            list = new ArrayList<>();
            populateDataIntoList();
        } else {
            tvNoContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void languageListFailed(String failedResponse) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.VISIBLE);
        }
        Logger.error(TAG, ":callApiForLanguage______" + failedResponse);
    }

    private void showNoInternetDialogue(String msg) {

        final Dialog dialog = new DialogController(activity).internetDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    private void populateDataIntoList() {

        languageName = new String[_languageLists.size()];
        String[] langFlagUri = new String[_languageLists.size()];

        for (int i = 0; i < _languageLists.size(); i++) {
            NissanApp.getInstance().setAlertMessageCarWiseLangDownloadList(_languageLists.get(i).getAlertMessage());
            languageName[i] = (_languageLists.get(i).getLanguageName());

            if ("xxxhdpi".contains(deviceDensity)) {
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getXxxhdpi();
            } else if ("xxhdpi".contains(deviceDensity)) {
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getXxhdpi();
            } else if ("xhdpi".contains(deviceDensity)) {
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getXhdpi();
            } else if ("hdpi".contains(deviceDensity)) {
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getHdpi();
            } else if ("mdpi".contains(deviceDensity)) {
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getHdpi();
            } else if ("ldpi".contains(deviceDensity)) {
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getLdpi();
            }
        }
        loadData(langFlagUri);
    }

    /**
     * Initialized all variable
     */
    private void initViews() {
        activity = LanguageSelectionActivity.this;
        context = getApplicationContext();
        tvNoContent = findViewById(R.id.tvNoContent);
        progressBar = findViewById(R.id.pbLanguageSelection);
        lstView = findViewById(R.id.lst_view);
        list = new ArrayList<>();
        preferenceUtil = new PreferenceUtil(getApplicationContext());
        commonDao = CommonDao.getInstance();
        deviceDensity = NissanApp.getInstance().getDensityName(activity);
        controller = new LanguageSelectionController(this);
        controllerGlobalMsg = new GlobalMessageController(this);
        carListContentController = new CarListContentController(this);
        loadResource();
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    /**
     * Load the initial data into list
     */
    private void loadData(String[] FlagUrl) {
        for (int i = 0; i < languageName.length; i++) {

            LanguageInfo info = new LanguageInfo(i, languageName[i], false, FlagUrl[i]);
            list.add(info);
        }

        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        LanguageInfo info = (LanguageInfo) parent.getAdapter().getItem(position);

        selectedLangModel = getDataFromMainList(languageName[info.getId()]);
        preferenceUtil.setSelectedLang(selectedLangModel.getLanguageShortcode());

//        preferenceUtil.setSelectedLang(languageShortName[info.getId()]); // here save the selected language sort name into preference


        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
            showCarDownloadDialogForSingleCar();
        } else {
            String internetCheckMessage = NissanApp.getInstance().getAlertMessage(this, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
            NissanApp.getInstance().showInternetAlert(LanguageSelectionActivity.this, internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage);
        }
    }

    private LanguageList getDataFromMainList(String selectedLanguageName) {

        if (_languageLists != null && _languageLists.size() > 0) {
            for (int i = 0; i < _languageLists.size(); i++) {
                Logger.error("list_lang_temp", "__________" + _languageLists.get(i).getLanguageName());
                if (selectedLanguageName.equalsIgnoreCase(_languageLists.get(i).getLanguageName())) {
                    return _languageLists.get(i);
                }
            }
        }
        return new LanguageList();
    }


    private void loadResource() {
        resources = new Resources(getAssets(), new DisplayMetrics(), NissanApp.getInstance().changeLocalLanguage(LanguageSelectionActivity.this, preferenceUtil.getSelectedLang()));
    }

    /**
     * Start Car download procedure
     */
    private void startCarDownloadProcedure() {

        carListContentController.callApi(NissanApp.getInstance().getDeviceID(activity), selectedLangModel.getLanguageId() + "");
        controllerGlobalMsg.callApi(NissanApp.getInstance().getDeviceID(activity)/*"246E5A50-B79F-4019-82ED-877BF53FD617"*/, selectedLangModel.getLanguageId() + "");

        final String startingDownloadMsg = getAlertMessage(STARTING_DOWNLOAD);

        activity.runOnUiThread(() -> progressDialog = new ProgressDialogController(activity).showDialog(startingDownloadMsg.isEmpty() ? resources.getString(R.string.start_download) : startingDownloadMsg));

        new ApiCall().postCarDownload(Values.carType + "", "" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()), "0", NissanApp.getInstance().getDeviceID(this), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo info) {
                if (AppConfig.IS_APP_ONLINE ? Values.SUCCESS_STATUS.equalsIgnoreCase(info.getStatusCode())
                        && !TextUtils.isEmpty(info.getAssetsUrl())
                        && !TextUtils.isEmpty(info.getLangUrl()) : Values.SUCCESS_STATUS.equalsIgnoreCase(info.getStatusCode())) {
                    preferenceUtil.storeMultiLangData(info.getTutorials(), Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.TUTORIAL_KEY);
                    preferenceUtil.storeMultiLangData(info.getTabMenu(), Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.TAB_MENU_KEY);

                    //call this after getting the asset link successfully
                    startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? info.getAssetsUrl() :
                                    NissanApp.getInstance().getAssetsURL(Values.carType),
                            AppConfig.IS_APP_ONLINE ? info.getLangUrl() : NissanApp.getInstance().getLanguageURL((Values.carType),
                                    preferenceUtil.getSelectedLang()),
                            NissanApp.getInstance().getCarPath(Values.carType));
                } else {
                    showErrorDialog("No content found.");
                    dismissDialog();
                    Logger.error("Car downloading failed", "____________" + info.getMessage());
                }
            }

            @Override
            public void onFailed(String failedReason) {
                showErrorDialog(resources.getString(R.string.failed_to_connect_server));
                dismissDialog();
                Logger.error("Car downloading failed", "____________" + failedReason);
            }
        });
    }

    private void startCarAssetsDownload(String assetsSource, final String langSource, String langDestination) {
        // downloadCarAssets method actually download car asset and language epub are both
//        new MADownloadManager(activity, context).downloadCarAssets(false, NissanApp.getInstance().getCarName(Values.carType), assetsSource, assetsDestination, langSource, langDestination, new DownloaderStatus() {
//            @Override
//            public boolean onComplete(boolean b) {
//                if (b) {
//                    runOnUiThread(new Runnable() {
//                        @SuppressLint("StaticFieldLeak")
//                        @Override
//                        public void run() {
//

//                        }
//                    });
//
//                }
//                return false;
//            }
//
//            @Override
//            public int onError(int i) {
//                errorFileDelete(Values.carType);
//                showErrorDialog(context.getResources().getString(R.string.internet_connection_interruption));
//                Logger.error("onError", "______" + i);
//                return 0;
//            }
//
//            @Override
//            public boolean internetConnection(boolean b) {
//                Logger.error("internetConnection", "______" + b);
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public boolean urlReachable(boolean b) {
//                Logger.error("urlReachable", "______" + b);
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public boolean destinationExists(boolean b) {
//                Logger.error("destinationExists", "______" + b);
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public boolean sourcePath(boolean b) {
//                Logger.error("sourcePath", "______" + b);
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public boolean destinationPath(boolean b) {
//                Logger.error("destinationPath", "______" + b);
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public void downloadCompletion(Float aFloat) {
//                String formattedString = String.format("%.02f", aFloat);
//                if (progressDialog != null) {
//                    String downloadingMsg = getAlertMessage(DOWNLOADING);
//                    downloadingMsg = downloadingMsg.isEmpty() ? getResources().getString(R.string.alert_downloading) : downloadingMsg;
//                    progressDialog.setMessage(carName + "\n" + downloadingMsg + formattedString + "%");
//                }
//            }
//
//            @Override
//            public void init() {
//                Logger.error("init", "______" + "start");
//                if (Values.carType == 11) {
////                    carName = "All New Nissan Micra";
//                    carName = "Nissan Micra";
//                } else {
//                    carName = NissanApp.getInstance().getCarName(Values.carType);
//                }
//                if (progressDialog == null) {
//
//                    String downloadingMsg = getAlertMessage(DOWNLOADING);
//                    downloadingMsg = downloadingMsg.isEmpty() ? getResources().getString(R.string.alert_downloading) : downloadingMsg;
//
//                    progressDialog = new ProgressDialogController(LanguageSelectionActivity.this).downloadProgress(carName + "\n" + downloadingMsg);
//                }
//            }
//        });


        CarDownloadHelper carDownloadHelper = new CarDownloadHelper(this,
                NissanApp.getInstance().getCarName(Values.carType),
                langSource,
                assetsSource,
                Values.PATH,
                langDestination,
                Values.PATH);
        carDownloadHelper.getDownloadProgress().observe(this, carDownloadProgress -> {
            if (carDownloadProgress == null) return;
            if (carDownloadProgress == CarDownloadProgress.COMPLETE.INSTANCE) {
                downloadCompleted();
            } else BaseActivity.checkCarDownloadProgress(LanguageSelectionActivity.this,
                    carDownloadProgress,
                    progressDialog, true);

        });
        carDownloadHelper.downloadAssetAndLang();
    }

    void downloadCompleted() {
        if (progressDialog != null) {
            String dataSyncingMsg = getAlertMessage(DATA_SYNCING);
            progressDialog.setMessage(dataSyncingMsg.isEmpty() ? resources.getString(R.string.data_syncing) : dataSyncingMsg);
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

    private void postCarDownloadSuccessfulStatus() {

        new ApiCall().postCarDownloadConfirmation(Values.carType + "", "" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()), "0", NissanApp.getInstance().getDeviceID(this), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    sendMsgToGoogleAnalytics(NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOWNLOAD + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM);
                    // set the car path according to car type
                    Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);

                    commonDao.updateDateAndStatus(getBaseContext(), Values.carType, Values.ALREADY_DOWNLOADED, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

                    if (Values.carType == 1 || Values.carType == 4) {
                        if (commonDao.getStatus(getBaseContext(), Values.carType + 1) == 2) {
                            commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 1, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                        }
                    } else if (Values.carType == 13) {//click eur/rus rohan
                        if (commonDao.getStatus(getBaseContext(), Values.carType + 2) == 0) {
                            commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 2, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                        }//click eur/rus rohan
                    } else if (Values.carType == 12) {//click eur/rus rohan
                        if (commonDao.getStatus(getBaseContext(), Values.carType + 4) == 0) {
                            commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 4, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                        }//click eur/rus rohan
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
                showErrorDialog(resources.getString(R.string.failed_to_connect_server));
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

        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {

        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void sendMsgToGoogleAnalytics(String msgName) {
        // Get a Tracker (should auto-report)
        ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Get tracker.
        Tracker tracker = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        tracker.setScreenName(msgName.toLowerCase());
        tracker.enableAdvertisingIdCollection(true);

        // Send a screen view.
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void showCarDownloadDialogForSingleCar() {

        carListContentController = new CarListContentController(this);
        controllerGlobalMsg = new GlobalMessageController(this);
        final Dialog dialog = new DialogController(LanguageSelectionActivity.this).carDownloadDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);

        String downloadConfirmationText = getAlertMessage(Values.DOWNLOAD_CONFIRMATION);
        txtViewTitle.setText(downloadConfirmationText.isEmpty() ? resources.getString(R.string.alert_msg22) : downloadConfirmationText);

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnCancel.setText(selectedLangModel.getCancel().isEmpty() ? resources.getString(R.string.button_CANCEL) : selectedLangModel.getCancel());
        btnOk.setText(selectedLangModel.getOk().isEmpty() ? resources.getString(R.string.button_OK) : selectedLangModel.getOk());

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestStoragePermission();
            } else {

                if (preferenceUtil.getIsFirstTime()) {
                    if (new File(Values.PATH).exists()) {
                        try {
                            Logger.error("Values.PATH", "_________" + Values.PATH);

                            FileUtils.deleteDirectory(new File(Values.PATH));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                Logger.error("lang_id", "__________" + selectedLangModel.getLanguageId());
                if (NissanApp.getInstance().createPath(Values.PATH)) {
                    startCarDownloadProcedure();
                } else {
                    Logger.error("error in", "________creating path");
                }
            }

        });

        dialog.show();
    }


    /**
     * Requesting camera permission
     * This uses single permission model from dexter
     * Once the permission granted, opens the camera
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission() {
        Dexter.withContext(this)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted

                        if (preferenceUtil.getIsFirstTime()) {
                            if (new File(Values.PATH).exists()) {
                                try {
                                    Logger.error("Values.PATH", "_________" + Values.PATH);
                                    FileUtils.deleteDirectory(new File(Values.PATH));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (NissanApp.getInstance().createPath(Values.PATH)) {
                            startCarDownloadProcedure();
                        } else {
                            Logger.error("error in", "________path");
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LanguageSelectionActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private String getAlertMessage(String msgType) {

        List<AlertMessage> alertMessageList = selectedLangModel.getAlertMessage();

        if (alertMessageList == null || alertMessageList.size() == 0)
            return "";

        for (AlertMessage alertMsg : alertMessageList) {
            if (alertMsg.getType().equalsIgnoreCase(msgType)) {
                return alertMsg.getMsg();
            }
        }
        return "";
    }

    private void errorFileDelete(int carType) {
        try {
            dismissDialog();
            // delete the directory eg. /storage/emulated/0/.AllDriverGuide/xtrail2017
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
        if (!isActivityOnPaused) {
            DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
            dialogFragment.show(getSupportFragmentManager(), "error_fragment");
        }

    }

    @Override
    public void onDownloaded(GlobalMsgResponse responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

            String key_global_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.GLOBAL_MSG_KEY;
            String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + "_" + Values.GLOBAL_ALERT_MSG_KEY;

            preferenceUtil.storeMultiLangData(responseInfo.getAlertMessage(), key_global_alert_message);
            preferenceUtil.storeMultiLangData(responseInfo.getGlobalMessage(), key_global_message);

            NissanApp.getInstance().setGlobalMessageArrayList(responseInfo.getGlobalMessage());
            NissanApp.getInstance().setAlertMessageGlobalArrayList(responseInfo.getAlertMessage());
        }
    }

    @Override
    public void onDownloaded(CarListResponse responseInfo) {

        if (responseInfo.getStatusCode().equals("200")) {
            String car_list_key = selectedLangModel.getLanguageShortcode() + "_" + Values.CAR_LIST_KEY + "_" + selectedLangModel.getLanguageId();
            Logger.error("Language_fragment", "car_list_key__________" + car_list_key);
            NissanApp.getInstance().setCarListWAP(responseInfo.getCarList());
            preferenceUtil.storeMultiLangData(responseInfo.getCarList(), car_list_key);
        }

    }

    @Override
    public void onFailed(String failedReason) {
        Logger.error(TAG, "globalMessage__________" + failedReason);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOnPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityOnPaused = false;
    }
}
