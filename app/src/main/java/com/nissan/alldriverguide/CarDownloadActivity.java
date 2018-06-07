package com.nissan.alldriverguide;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.adapter.CarDownloadAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAlertAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.AlertMessage;
import com.nissan.alldriverguide.multiLang.model.GlobalMessage;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.pushnotification.Config;
import com.nissan.alldriverguide.pushnotification.NotificationUtils;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.AppConfig;
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.SearchDBAsync;
import com.nissan.alldriverguide.utils.SingleContentUpdating;
import com.nissan.alldriverguide.utils.Values;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CarDownloadActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView lstView;
    private TextView txtView_title;
    private CarDownloadAdapter adapter;
    private PreferenceUtil preferenceUtil;
    private Tracker t;
    private CommonDao commonDao;
    // variable to track event time
    private long mLastClickTime = 0;

    // Start------------ For permission related constants
    private static final int PERMISSION_REQUEST_CODE_ALL = 200;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 201;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 202;
    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 203;
    // End------------ For permission related constants

//    private String[] carNames = {"Qashqai EUR Specs", "Qashqai RUS Specs", "Juke", "X-Trail EUR Specs", "X-Trail RUS Specs",
//            "Pulsar", "Micra", "Note", "Leaf", "Navara", "All New Nissan Micra", "New Nissan Qashqai"};

    private String[] carNames = {"Qashqai EUR Specs", "Qashqai RUS Specs", "Juke", "X-Trail EUR Specs", "X-Trail RUS Specs",
            "Pulsar", "Micra", "Note", "Leaf", "Navara", "All New Nissan Micra", "New Nissan QASHQAI", "New Nissan X-TRAIL", "New Nissan LEAF"};

    private int[] previousCarArray = {1, 2, 4, 5, 7, 9};
//    private int[] previousCarArray = {1, 2, 7};

    private ProgressDialog progressDialog;
    private int selectedCarIndex = 0;

    public Resources resources;
    private DisplayMetrics metrics;
    private String selectedLang = "";

    private Activity activity;
    private Context context;
    private Object regIdForPush;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private long doubleClickPopup = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_download);
        Logger.error("device_id", NissanApp.getInstance().getDeviceID(this));

//        getGlobalAlertMsg();
        initViews();
        setListener();
        loadData();
        assurePermissionForMarshmallowAndOver();
        getRegIdForPush();
        loadResource(preferenceUtil.getSelectedLang());

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//
//                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void getGlobalAlertMsg(){
        new ApiCall().postGlobalAlertMsg("e224fb09fb8daee4", "1", new CompleteAlertAPI() {
            @Override
            public void onDownloaded(GlobalMsgResponse responseInfo) {
                if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {

                    List<GlobalMessage> globalMsgList = responseInfo.getGlobalMessage();
                    List<AlertMessage>  alertMsgList = responseInfo.getAlertMessage();

                    List<Object> global_alert = new ArrayList<>();
                    global_alert.addAll(globalMsgList);
                    global_alert.addAll(alertMsgList);

                    preferenceUtil.storeGlobalMsgList(global_alert, Values.GLOBALMSGKEY);

                    List<Object> data = preferenceUtil.retrieveGlobalMsgList(Values.GLOBALMSGKEY);
                    if (data instanceof GlobalMessage) {

                        GlobalMessage gb = (GlobalMessage) data.get(0);
                        Log.e("xxxxxxxx", gb.getMsg());

                    } else if (data instanceof AlertMessage) {

                        AlertMessage gb = (AlertMessage) data.get(0);
                        Log.e("yyyyyyyy", "----"+gb.getMsg());

                    } else {
                        Log.e("zzzzzzzzzzz", "**************");
                    }

                }
            }

            @Override
            public void onFailed(String failedReason) {
                Log.e("onDownloaded", "********Fail******");
            }
        });
    }

    private void initViews() {
        activity = CarDownloadActivity.this;
        context = getApplicationContext();
        commonDao = CommonDao.getInstance();
        lstView = (ListView) findViewById(R.id.lst_view);
        txtView_title = (TextView) findViewById(R.id.txtView_title);
        preferenceUtil = new PreferenceUtil(getApplicationContext());
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    private void loadData() {
        txtView_title.setText(getResources().getString(R.string.select_your_car));
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Logger.error("push_sp_status","___________" + new PreferenceUtil(context).getPushRegistrationStatus());

        if (!new PreferenceUtil(context).getPushRegistrationStatus()) {

            if (DetectConnection.checkInternetConnection(context)) {

                final Dialog dialog = new DialogController(activity).pushRegistrationDialog();

                TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
                txtViewTitle.setText(getResources().getString(R.string.register_push));

                Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(context, "Without registration you cannot download car", Toast.LENGTH_LONG).show();
                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        progressDialog = new ProgressDialogController(activity).showDialog(resources.getString(R.string.register_push_dialog));
                        registerForPush(position, parent);
                    }
                });
                dialog.show();


            } else {
                goForNormalOperation(position, parent);
            }

        } else {
            goForNormalOperation(position, parent);
        }

    }

    private void registerForPush(final int position, final AdapterView<?> parent) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);

        new ApiCall().postDeviceRegistrationForPush(NissanApp.getInstance().getDeviceID(getApplicationContext()), pref.getString("regId", ""), Values.DEVICE_TYPE, new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Logger.error("Device registration Successful", "________________________________" + "refresh token");
                    new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(true);
                    goForNormalOperation(position, parent);
                }
            }

            @Override
            public void onFailed(String failedReason) {
                Logger.error("Device registration failed_refresh_token", "_____________________" + failedReason);
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(context, "Failed Registration!", Toast.LENGTH_SHORT).show();
                new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(false);
            }
        });

    }

    private void goForNormalOperation(int position, AdapterView<?> parent) {
        selectedCarIndex = position;

        if (parent.getAdapter().getItem(position).getClass() == CarInfo.class) {
            CarInfo info = (CarInfo) parent.getAdapter().getItem(position);
            selectedLang = commonDao.getLanguageStatus(getApplicationContext(), info.getId());

            if ("1".equalsIgnoreCase(info.getStatus())) {
                Logger.error("onItemClick:>>>>>>>>> ", "" + info.getSelectedLanguage() + "  == getSelectedCar = " + info.getSelectedCar());
                carDownloadCheck(info.getId());
            } else {
                if (info.getId() == 1 || info.getId() == 2 || info.getId() == 4 || info.getId() == 5) {
                    showCarDownloadDialog(info.getId());
                } else {
                    carDownloadCheck(info.getId());
                }
            }
        }
    }

    private void showCarDownloadDialog(final int carType) {
        final Dialog dialog = new DialogController(CarDownloadActivity.this).carDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(getResources().getString(R.string.download_msg));

        ImageButton btnEUR = (ImageButton) dialog.findViewById(R.id.btn_eur);
        ImageButton btnRUS = (ImageButton) dialog.findViewById(R.id.btn_rus);

        ImageButton imgBtnEur = (ImageButton) dialog.findViewById(R.id.img_btn_eur_delete_donwload);
        ImageButton imgBtnRus = (ImageButton) dialog.findViewById(R.id.img_btn_rus_delete_donwload);

        imgBtnEur.setVisibility(View.GONE);
        imgBtnRus.setVisibility(View.GONE);

        if (carType == 1 || carType == 4) {
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(getBaseContext(), carType) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
            }

            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 1)) && commonDao.getStatus(getBaseContext(), carType + 1) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
            }
        } else if (carType == 2 || carType == 5) {
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 1)) && commonDao.getStatus(getBaseContext(), carType - 1) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(getBaseContext(), carType) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
            }
        }

        btnEUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 1 || carType == 4) {
                    carDownloadCheck(carType);
                }
            }
        });

        btnRUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 2 || carType == 5) {
                    carDownloadCheck(carType);
                } else {
                    carDownloadCheck(carType + 1);
                }
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        Log.e("onResume: ", "8888888888888888888888888");
        getGlobalAlertMsg();
        if (preferenceUtil.getIsFirstTime()) {
            if (new File(Values.PATH).exists()) {
                try {
                    FileUtils.deleteDirectory(new File(Values.PATH));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        new LoadDataBase().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
        t = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(msgName.toLowerCase());
        t.enableAdvertisingIdCollection(true);

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void carDownloadCheck(final int position) {
        Logger.error("pos_carDownloadCheck","___________" + position);

        Values.carType = position;

        if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(Values.carType))) {
            if (commonDao.getStatus(getBaseContext(), Values.carType) == 1) { // if car is downloaded (status 1 is used for downloaded car)
                CarInfo info = commonDao.getCarInfo(getApplicationContext(), Values.carType);
                if (info != null) {
                    if (!NissanApp.getInstance().getVersionName().equalsIgnoreCase(info.getVersionName()) || info.getVersionCode() != NissanApp.getInstance().getVersionCode()) {
                        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
                            try {
                                FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(position)));
                                if (position == 2 || position == 5) {
                                    if (commonDao.getStatus(getApplicationContext(), position - 1) == 2) {
                                        commonDao.updateDateAndStatus(getApplicationContext(), position, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        adapter.list.remove(selectedCarIndex);
                                    } else {
                                        commonDao.updateDateAndStatus(getApplicationContext(), position, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    }
                                } else {
                                    if (position == 1 || position == 4) {
                                        commonDao.updateDateAndStatus(getApplicationContext(), position, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        if (commonDao.getStatus(getApplicationContext(), position + 1) == 2) {
                                            commonDao.updateDateAndStatus(getApplicationContext(), position + 1, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            adapter.list.remove(selectedCarIndex);
                                        } else {
                                            commonDao.updateDateAndStatus(getApplicationContext(), position + 1, Values.ALREADY_DOWNLOADED, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        }
                                    } else {
                                        if (position == 7 || position == 9) {
                                            commonDao.updateDateAndStatus(getApplicationContext(), position, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        } else {
                                            commonDao.updateDateAndStatus(getApplicationContext(), position, Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        }
                                    }
                                }

                                adapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                preferenceUtil.setSelectedLang(selectedLang);
                                startCarDownloadProcedure();
                            }
                        } else {
                            goToNextPage(position);
                        }

                    } else { // this block for when version code is same

                        //check and get all the content update from database
                        final ArrayList<PushContentInfo> list = commonDao.getNotificationList(getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(getApplicationContext(), position)));

                        if (list != null && list.size() > 0) {
                            if(DetectConnection.checkInternetConnection(getApplicationContext())) {
                                downloadContentUpdate(list, position);
                            } else {
                                goToNextPage(position);
                            }
                        } else {
                            goToNextPage(position);
                        }

                    }
                }

            } else {
                try {
                    FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(Values.carType)));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(CarDownloadActivity.this, LanguageSelectionActivity.class));
                }
            }
        } else {
            startActivity(new Intent(CarDownloadActivity.this, LanguageSelectionActivity.class));
        }
    }

    private void downloadContentUpdate(final ArrayList<PushContentInfo> list, final int position) {

        Logger.error("You have a update ", "Do you want ???");

        final StringBuilder stringBuilder = new StringBuilder();

        for (PushContentInfo pushContentInfo : list) {
            stringBuilder.append(pushContentInfo.getePubId()).append(",");
        }

        stringBuilder.setLength(stringBuilder.length() - 1);

        final Dialog dialog = new DialogController(this).contentUpdateDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(resources.getString(R.string.update_msg));

        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setText(resources.getString(R.string.button_YES));

        Logger.error("Epub_id____"+ stringBuilder, "LANG_ID _____" + selectedLang);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - doubleClickPopup < 1000) {
                    return;
                }
                doubleClickPopup = SystemClock.elapsedRealtime();

                dialog.dismiss();

                if (DetectConnection.checkInternetConnection(CarDownloadActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog = new ProgressDialogController(activity).showDialog(getResources().getString(R.string.start_download));
                        }
                    });

                    new ApiCall().postContentDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), stringBuilder.toString(), NissanApp.getInstance().getDeviceID(getApplicationContext()), new CompleteAPI() {
                        @Override
                        public void onDownloaded(ResponseInfo responseInfo) {

                            Logger.error("Status "+responseInfo.getStatusCode(), "URL "+responseInfo.getUrl());

                            if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getUrl())) {

                                Logger.error("content_url", "____________" + responseInfo.getUrl());

                                new MADownloadManager(activity, getApplicationContext()).downloadLanguage(false, "Language", responseInfo.getUrl(), NissanApp.getInstance().getCarPath(Values.carType), new DownloaderStatus() {
                                    @Override
                                    public boolean onComplete(boolean b) {
                                        if (b) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (progressDialog != null) {
                                                        progressDialog.setMessage(getResources().getString(R.string.data_syncing));
                                                    }

                                                    new SingleContentUpdating(CarDownloadActivity.this, selectedLang, Values.carType) {
                                                        @Override
                                                        public void onComplete(boolean status) {

                                                            if (status) {

                                                                new ApiCall().postContentDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), stringBuilder.toString(), NissanApp.getInstance().getDeviceID(getApplicationContext()), new CompleteAPI() {
                                                                    @Override
                                                                    public void onDownloaded(ResponseInfo responseInfo) {

                                                                        Logger.error("Status code _________", "__________________"+responseInfo.getStatusCode());

                                                                        if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                                                                            try {
//                                                                                ((MainActivity) getApplicationContext()).sendMsgToGoogleAnalytics(((MainActivity) getApplicationContext()).getAnalyticsFromSettings(Analytics.CHANGE_LANGUAGE + Analytics.DOWNLOAD));
                                                                                commonDao.updateLanguageStatus(CarDownloadActivity.this.getBaseContext(), Values.carType, selectedLang);
                                                                                commonDao.deleteSingleCarEpub(CarDownloadActivity.this, Values.carType);

                                                                                for (PushContentInfo pushContentInfo : list) {
                                                                                    commonDao.updatePushContentStatus(getApplicationContext(), Integer.parseInt(pushContentInfo.getCarId()), Integer.parseInt(pushContentInfo.getLangId()), Integer.parseInt(pushContentInfo.getePubId()));
                                                                                }

                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            } finally {
                                                                                if (progressDialog != null) {
                                                                                    progressDialog.dismiss();
                                                                                }

                                                                                goToNextPage(position);
                                                                            }

                                                                        } else {

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailed(String failedReason) {
                                                                        Logger.error("CarDownloadActivity", "_____download_confirmation_error->" + failedReason);
                                                                    }
                                                                });

                                                            } else {

                                                            }
                                                        }
                                                    }.execute();

                                                }
                                            });

                                        } else {
                                            Logger.error("problem", "______assetDownload-LanguageFragment");
                                        }
                                        return false;
                                    }

                                    @Override
                                    public int onError(int i) {
                                        showErrorDialog("Error ! Unable to update content, Please try again.");
                                        dismissDialog();
                                        return 0;
                                    }

                                    @Override
                                    public boolean internetConnection(boolean b) {
                                        showErrorDialog("No internet connection, Please try again");
                                        dismissDialog();
                                        return false;
                                    }

                                    @Override
                                    public boolean urlReachable(boolean b) {
                                        return false;
                                    }

                                    @Override
                                    public boolean destinationExists(boolean b) {
                                        return false;
                                    }

                                    @Override
                                    public boolean sourcePath(boolean b) {
                                        return false;
                                    }

                                    @Override
                                    public boolean destinationPath(boolean b) {
                                        return false;
                                    }

                                    @Override
                                    public void downloadCompletion(Float aFloat) {
                                        String formattedString = String.format("%.02f", aFloat);
                                        if (progressDialog != null) {
                                            progressDialog.setMessage(CarDownloadActivity.this.getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete) + formattedString + "%");
                                        }
                                    }

                                    @Override
                                    public void init() {
                                        if (progressDialog == null) {
                                            progressDialog = new ProgressDialogController(CarDownloadActivity.this).downloadProgress(CarDownloadActivity.this.getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
                                        }
                                    }
                                });
                            } else {
                                showErrorDialog("Status code OR URL not reachable");
                                dismissDialog();
                            }
                        }

                        @Override
                        public void onFailed(String failedReason) {
                            showErrorDialog(failedReason);
                            Logger.error("Single Content Downloading failed", "____________" + failedReason);
                        }
                    });
                } else {

                }
            }
        });

        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setText(resources.getString(R.string.button_NO));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - doubleClickPopup < 1000) {
                    return;
                }
                doubleClickPopup = SystemClock.elapsedRealtime();

                dialog.dismiss();
                goToNextPage(position);
            }
        });

        dialog.show();
    }

    private void goToNextPage(int position) {
        for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
            if (NissanApp.getInstance().getCarAllList().get(i).getId() == position) {
                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(Values.CAR_SELECTED);
            } else {
                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(Values.CAR_NOT_SELECTED);
            }
        }
        Values.car_path = NissanApp.getInstance().getCarPath(Values.carType); // set the car path
        preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(getBaseContext(), Values.carType)); // here save language sort name in preference that getting from db for further uses.
        startActivity(new Intent(CarDownloadActivity.this, MainActivity.class)); // here redirect the landing page to Main activity if car is downloaded.
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void startCarDownloadProcedure() {
        progressDialog = new ProgressDialogController(activity).showDialog(getResources().getString(R.string.start_download));

        new ApiCall().postCarDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), "0", NissanApp.getInstance().getDeviceID(this), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equals(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getAssetsUrl()) && !TextUtils.isEmpty(responseInfo.getLangUrl())) {
                    startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? responseInfo.getAssetsUrl() : NissanApp.getInstance().getAssetsURL(Values.carType), Values.PATH, AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL(Values.carType, selectedLang), NissanApp.getInstance().getCarPath(Values.carType));
                } else {
                    showErrorDialog("No content found.");
                    dismissDialog();
                    Logger.error("cardownloadactivity_problem", "__________" + "postcardownload");
                }
            }

            @Override
            public void onFailed(String failedReason) {
                showErrorDialog(failedReason);
                dismissDialog();
                Logger.error("cardownloadactivity_failed", "__________" + "postcardownload->" + failedReason);
            }
        });

    }

    private void assurePermissionForMarshmallowAndOver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissionAll()) {
                requestPermissionAll();
            }
        }
    }

    private boolean checkPermissionAll() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionAll() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_ALL);
    }

    private void requestPermission(final String PERMISSION_NAME, final int PERMISSION_CODE) {
        ActivityCompat.requestPermissions(this, new String[]{PERMISSION_NAME}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_ALL:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && storageAccepted) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)
                                    || shouldShowRequestPermissionRationale(CAMERA)
                                    || shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow all of the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE_ALL);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;

            case PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {

                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to CAMERA",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermission(CAMERA, PERMISSION_REQUEST_CODE_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;

            case PERMISSION_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to SD card.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermission(WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_STORAGE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;

            case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to location data",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermission(ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(CarDownloadActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void getRegIdForPush() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
//        String regId = pref.getString("regId", null);
        String regId = "e224fb09fb8daee4";//auve

        Logger.error("Firebase reg id: " + NissanApp.getInstance().getDeviceID(getApplicationContext()), "_________" + regId);

        new ApiCall().postDeviceRegistrationForPush(NissanApp.getInstance().getDeviceID(getApplicationContext()), regId, Values.DEVICE_TYPE, new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                    if (!new PreferenceUtil(getApplicationContext()).getPushRegistrationStatus()) {
                        new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(true);
                    }

                    Logger.error("Device registration Successfull", "________________________________succesfull");
                }
            }

            @Override
            public void onFailed(String failedReason) {
                Logger.error("Device registration failed", "_____________________" + failedReason);
            }
        });
    }

    public class LoadDataBase extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (preferenceUtil.getIsDatabaseEmpty()) {
                for (int i = 0; i < carNames.length; i++) {
                    CarInfo carInfo;
                    if (i == 1 || i == 4) { // this logic actually work for car id (2 & 5) since i start from 0
                        carInfo = new CarInfo((i + 1), carNames[i], Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "RUS", "en", Values.CAR_NOT_SELECTED, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                    } else {
                        carInfo = new CarInfo((i + 1), carNames[i], Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "EUR", "en", Values.CAR_NOT_SELECTED, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                    }
                    commonDao.insertInCarInfoTable(getBaseContext(), carInfo); // insert into database as a CarInfo object
                }
                preferenceUtil.setIsDatabaseEmpty(false);
            } else {
                int lastID = commonDao.getLastID(getBaseContext());
                for (int i = lastID; i < carNames.length; i++) {
                    commonDao.insertInCarInfoTable(getBaseContext(), new CarInfo((lastID + 1), carNames[lastID], Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "EUR", "en", Values.CAR_NOT_SELECTED, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode()));
                }
            }

            // this
            for (int i = 0; i < previousCarArray.length; i++) {
                if (commonDao.getStatus(getBaseContext(), previousCarArray[i]) == 0) {
                    commonDao.updateCarStatus(getBaseContext(), previousCarArray[i], 2);
                }
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            // here set the all ordered car list that getting from database as an object type
            NissanApp.getInstance().setCarList(commonDao.getAllOderCarList(getBaseContext()));
            // here set the all unordered car list that getting from database as a CarInfo object
            NissanApp.getInstance().setCarAllList(commonDao.getAllCarList(getBaseContext()));

            // actually this looping and logic determine for section header
            for (int i = 0; i < NissanApp.getInstance().getCarList().size(); i++) {
                CarInfo info = (CarInfo) NissanApp.getInstance().getCarList().get(i);
                if ((Values.ALREADY_DOWNLOADED.equalsIgnoreCase(info.getStatus()))) {
                    if (!NissanApp.getInstance().getCarList().contains(resources.getString(R.string.downloaded_car))) {
                        NissanApp.getInstance().getCarList().add(i, resources.getString(R.string.downloaded_car));
                    }
                } else if ((Values.PREVIOUS_CAR.equalsIgnoreCase(info.getStatus()))) {
                    if (!NissanApp.getInstance().getCarList().contains(resources.getString(R.string.previous_car))) {
                        NissanApp.getInstance().getCarList().add(i, resources.getString(R.string.previous_car));
                    }
                } else {
                    if (!NissanApp.getInstance().getCarList().contains(resources.getString(R.string.available_for_download_car))) {
                        NissanApp.getInstance().getCarList().add(i, resources.getString(R.string.available_for_download_car));
                    }
                }
            }

            // set the adapter
            if (NissanApp.getInstance().getCarList() != null) {
                adapter = new CarDownloadAdapter(getApplicationContext(), NissanApp.getInstance().getCarList());
                lstView.setAdapter(adapter);
                lstView.setDivider(null);
            }

        }
    }

    private void startCarAssetsDownload(String assetsSource, String assetsDestination, String langSource, String langDestination) {
        new MADownloadManager(activity, context).downloadCarAssets(false, NissanApp.getInstance().getCarName(Values.carType), assetsSource, assetsDestination, langSource, langDestination, new DownloaderStatus() {
            @Override
            public boolean onComplete(boolean b) {
                if (b) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.setMessage(getResources().getString(R.string.data_syncing));
                            }
                        }
                    });

                    new SearchDBAsync(activity, selectedLang, Values.carType) {
                        @Override
                        public void onComplete(boolean status) {

                            if (status) {
                                new ApiCall().postCarDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), "0", NissanApp.getInstance().getDeviceID(context), new CompleteAPI() {
                                    @Override
                                    public void onDownloaded(ResponseInfo responseInfo) {

                                        if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                                            sendMsgToGoogleAnalytics(NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOWNLOAD + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM);
                                            Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);

                                            commonDao.updateDateAndStatus(getBaseContext(), Values.carType, Values.ALREADY_DOWNLOADED, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            if (Values.carType == 1 || Values.carType == 4) {
                                                CarInfo carInfo = commonDao.getCarInfo(getApplicationContext(), Values.carType + 1);
                                                if (Values.carType == 1) {
                                                    if (commonDao.getStatus(getBaseContext(), Values.carType + 1) == 2) {
                                                        commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 1, "2", NissanApp.getInstance().getDateTime(), "EUR", carInfo.getVersionName(), carInfo.getVersionCode());
                                                    }
                                                } else if (Values.carType == 4) {
                                                    if (commonDao.getStatus(getBaseContext(), Values.carType + 1) == 0) {
                                                        commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 1, "0", NissanApp.getInstance().getDateTime(), "EUR", carInfo.getVersionName(), carInfo.getVersionCode());
                                                    }
                                                } else {

                                                }
                                            }
                                            commonDao.updateLanguageStatus(getBaseContext(), Values.carType, preferenceUtil.getSelectedLang());

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

                                            if (preferenceUtil.getIsFirstTime()) {
                                                startActivity(new Intent(CarDownloadActivity.this, TutorialActivity.class).putExtra("from", "activity"));
                                                finish();
                                            } else {
                                                startActivity(new Intent(CarDownloadActivity.this, MainActivity.class));
                                            }

                                        } else {
                                            showErrorDialog("Confirmation send error!");
                                            dismissDialog();
                                        }

                                    }

                                    @Override
                                    public void onFailed(String failedReason) {
                                        showErrorDialog(failedReason);
                                        dismissDialog();
                                    }
                                });

                            } else {
                                dismissDialog();
                            }

                        }
                    }.execute();

                } else {
                    dismissDialog();
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
                    progressDialog.setMessage(NissanApp.getInstance().getCarName(Values.carType) + "\n" + getResources().getString(R.string.alert_downloading) + formattedString + "%");
                }
            }

            @Override
            public void init() {
                loadResource(selectedLang);
                if (progressDialog == null) {
                    progressDialog = new ProgressDialogController(CarDownloadActivity.this).downloadProgress(NissanApp.getInstance().getCarName(Values.carType) + "\n" + getResources().getString(R.string.alert_downloading));
                }
            }
        });
    }

    private void errorFileDelete(int carType) {
        try {
            dismissDialog();
            FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carType)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadResource(String lang) {
        resources = new Resources(getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(CarDownloadActivity.this, lang));
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(getSupportFragmentManager(), "error_fragment");
    }
}
