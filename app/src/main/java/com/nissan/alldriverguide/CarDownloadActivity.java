package com.nissan.alldriverguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.adapter.CarDownloadAdapter;
import com.nissan.alldriverguide.controller.CarListContentController;
import com.nissan.alldriverguide.controller.GlobalMessageController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.InterfaceGlobalMessageResponse;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class CarDownloadActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, InterfaceGlobalMessageResponse, CarListACompleteAPI {

    private static final String TAG = "CarDownloadActivity";
    private ListView lstView;
    private TextView txtView_title;
    private CarDownloadAdapter adapter;
    private PreferenceUtil preferenceUtil;
    private Tracker t;
    private CommonDao commonDao;
    // variable to track event time
    private long mLastClickTime = 0;
    private ArrayList<Object> getList = new ArrayList<>();

    // Start------------ For permission related constants
    private static final int PERMISSION_REQUEST_CODE_ALL = 200;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 201;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 202;
    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 203;
    // End------------ For permission related constants

//    private String[] carNames = {"Qashqai EUR Specs", "Qashqai RUS Specs", "Juke", "X-Trail EUR Specs", "X-Trail RUS Specs",
//            "Pulsar", "Micra", "Note", "Leaf", "Navara", "All New Nissan Micra", "New Nissan Qashqai"};

    private String[] carNames = {"Qashqai EUR Specs", "Qashqai RUS Specs", "Juke", "X-Trail EUR Specs", "X-Trail RUS Specs",
            "Pulsar", "Micra", "Note", "Leaf", "Navara", "All New Nissan Micra", "New Nissan QASHQAI", "Nissan X-TRAIL", "New Nissan LEAF", "New Nissan X-TRAIL RUS"};
    private int[] previousCarArray = {1, 2, 4, 5, 7, 9};
//    private int[] previousCarArray = {1, 2, 7};

    private ProgressDialog progressDialog;
    private ProgressBar pbCarDownload;
    private int selectedCarIndex = 0;

    public Resources resources;
    private DisplayMetrics metrics;
    private String selectedLang = "";

    private Activity activity;
    private Context context;
    private Object regIdForPush;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private long doubleClickPopup = 0;
    private List<LanguageList> languageLists;
    private GlobalMessageController globalMessageController;
    private CarListContentController carListContentController;
    private List<CarList> carListArrayList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_download);
        Logger.error("device_id", NissanApp.getInstance().getDeviceID(this));

        initViews();
        setListener();
        loadData();
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

    /**
     * Initialized all variable
     */
    private void initViews() {
        activity = CarDownloadActivity.this;
        context = getApplicationContext();
        commonDao = CommonDao.getInstance();
        lstView = (ListView) findViewById(R.id.lst_view);
        txtView_title = (TextView) findViewById(R.id.txtView_title);
        preferenceUtil = new PreferenceUtil(getApplicationContext());
        pbCarDownload = (ProgressBar) findViewById(R.id.pbCarDownload);
        globalMessageController = new GlobalMessageController(this);
        carListContentController = new CarListContentController(this);
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    private void loadData() {
        txtView_title.setText(getResources().getString(R.string.select_your_car));
        Logger.error("List msg","------load-----");
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();





        Logger.error("push_sp_status", "___________" + new PreferenceUtil(context).getPushRegistrationStatus());

        if (!new PreferenceUtil(context).getPushRegistrationStatus()) {

            if (DetectConnection.checkInternetConnection(context)) {

                final Dialog dialog = new DialogController(activity).pushRegistrationDialog();

                TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
                String pushTitle = NissanApp.getInstance().getAlertMessage(this, preferenceUtil.getSelectedLang(), Values.REGISTER_PUSH_MESSAGE);
                txtViewTitle.setText(pushTitle.isEmpty() ? getResources().getString(R.string.register_push) : pushTitle);

                //TODO
                String okText = NissanApp.getInstance().getGlobalMessage(this).getOk();
                String cancelText = NissanApp.getInstance().getGlobalMessage(this).getCancel();

                Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);

                btnOk.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_OK) : okText);
                btnCancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_CANCEL) : cancelText);

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

/*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestStoragePermission(position, parent);
                } else {
                    goForNormalOperation(position, parent);
                }
*/


            }

        } else {
/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestStoragePermission(position, parent);
            } else {
                goForNormalOperation(position, parent);
            }
*/
            goForNormalOperation(position, parent);
        }

    }

    /**
     * Requesting camera permission
     * This uses single permission model from dexter
     * Once the permission granted, opens the camera
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(final int position, final AdapterView<?> parent) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        goForNormalOperation(position, parent);
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

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CarDownloadActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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
/*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestStoragePermission(position, parent);
                    } else {
                        goForNormalOperation(position, parent);
                    }
*/

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

    /**
     * For Normal app behaviour without update push notification
     *
     * @param position needed for list item
     * @param parent   derived from object
     */
    private void goForNormalOperation(int position, AdapterView<?> parent) {
        selectedCarIndex = position;
        // class cast for CarInfo class
        if (parent.getAdapter().getItem(position).getClass() == CarInfo.class) {

            CarInfo info = (CarInfo) parent.getAdapter().getItem(position);

            selectedLang = commonDao.getLanguageStatus(getApplicationContext(), info.getId());

            if ("1".equalsIgnoreCase(info.getStatus())) { // status 1 means downloaded car, 2 means previous car and 0 means available cars
                carDownloadCheck(info.getId());
            } else {

                if (!DetectConnection.checkInternetConnection(context)) {
                    showNoInternetDialogue("No Internet Connection. Please check your WIFI or cellular data network and try again.");
                    return;
                }

                //click for eur/rus by rohan
                if (info.getId() == 1 || info.getId() == 2 || info.getId() == 4 || info.getId() == 5 || info.getId() == 13 || info.getId() == 15) {
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
        } else if (carType == 13) {//click for eur/rus by rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(getBaseContext(), carType) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 2)) && commonDao.getStatus(getBaseContext(), carType + 2) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
            }
        } else if (carType == 15) {//click for eur/rus by rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 2)) && commonDao.getStatus(getBaseContext(), carType - 2) == 1) {
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
                dialog.dismiss();//click for eur/rus by rohan

                if (carType == 1 || carType == 4 || carType == 13) {
                    carDownloadCheck(carType);
                }
            }
        });

        btnRUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//click for eur/rus by rohan

                if (carType == 2 || carType == 5) {
                    carDownloadCheck(carType);
                } else if (carType == 13) {
                    carDownloadCheck(carType + 2);
                } else if (carType == 15) {
                    carDownloadCheck(carType);
                } else {
                    carDownloadCheck(carType + 1);
                }
            }
        });

        dialog.show();
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

            Logger.error("List msg","-----------");

        }
    }

    private void loadCarData() {


//        if (carListArrayList == null || carListArrayList.size() == 0)
//            return;

//        carNames = new String[carListArrayList.size()];

//        for (int i = 0; i < carListArrayList.size(); i++) {
//            carNames[i] = carListArrayList.get(i).getCarDisplayName();
//        }

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
    }

    @Override
    public void onDownloaded(CarListResponse responseInfo) {

        if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

            carListArrayList = new ArrayList<>();

            carListArrayList = responseInfo.getCarList();
            NissanApp.getInstance().setCarListWAP(carListArrayList);
            preferenceUtil.storeMultiLangData(carListArrayList, "en_" + Values.CAR_LIST_KEY + "_1");
            loadCarData();
            if (pbCarDownload != null && pbCarDownload.getVisibility() == View.VISIBLE) {
                pbCarDownload.setVisibility(View.GONE);
            }

        } else {
//            showErrorDialog(resources.getString(R.string.failed_to_connect_server));
            Logger.error(TAG, "CarListResponse______" + resources.getString(R.string.failed_to_connect_server));
        }

    }

    @Override
    public void onFailed(String failedReason) {
        Logger.error(TAG, "GlobalMsg_________" + failedReason);
        Logger.error(TAG, "OnFailed______" + resources.getString(R.string.failed_to_connect_server));
//        showErrorDialog(resources.getString(R.string.failed_to_connect_server));
        if (pbCarDownload != null && pbCarDownload.getVisibility() == View.VISIBLE) {
            pbCarDownload.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {

        carListContentController = new CarListContentController(this);

        globalMessageController.callApi(NissanApp.getInstance().getDeviceID(getApplicationContext()), NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "");

//        adapter = new CarDownloadAdapter(getApplicationContext(), NissanApp.getInstance().getCarList());
//        lstView.setAdapter(adapter);

        String sharedpref_key = "en_" + Values.CAR_LIST_KEY + "_1";
        Type type = new TypeToken<ArrayList<CarList>>() {
        }.getType();
        carListArrayList = new Gson().fromJson(preferenceUtil.retrieveMultiLangData(sharedpref_key), type);

//        if (carListArrayList != null && carListArrayList.size() > 0) {
        loadCarData();
//        } else {
        pbCarDownload.setVisibility(View.VISIBLE);
//        }

        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
            carListContentController.callApi(NissanApp.getInstance().getDeviceID(this), "1");
        } else {
            if (pbCarDownload.getVisibility() == View.VISIBLE)
                pbCarDownload.setVisibility(View.GONE);
//            Toast.makeText(activity, getString(R.string.internet_connect), Toast.LENGTH_SHORT).show();
//            showErrorDialog(getResources().getString(R.string.internet_connect));
        }

        Logger.error("List msg","------onresume-----");

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
        if (carListContentController != null) {
            carListContentController.removeListener();
        }
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
        Values.carType = position; // set the car type

        Log.e("Values.carType", "___________" + Values.carType);

        if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(Values.carType))) { // here check the file existence on mmc
            if (commonDao.getStatus(getBaseContext(), Values.carType) == 1) { // if car is downloaded (status 1 is used for downloaded car)
                CarInfo info = commonDao.getCarInfo(getApplicationContext(), Values.carType);
                if (info != null) {
                    // this condition for force download and when update build version
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

                    } else { // this block for when version code is same that means no update available

                        //check and get all the content update from database
                        final ArrayList<PushContentInfo> list = commonDao.getNotificationList(getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(getApplicationContext(), position)));

                        if (list != null && list.size() > 0) {
                            if (DetectConnection.checkInternetConnection(getApplicationContext())) {
                                downloadContentUpdate(list, position);
                            } else {
                                goToNextPage(position);
                            }
                        } else {
                            goToNextPage(position);
                        }

                    }
                }

            } else { // if car is not downloaded

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

    private void showNoInternetDialogue(String msg) {

        final Dialog dialog = new DialogController(activity).internetDialog();

        dialog.setCancelable(false);

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

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

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
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

                            Logger.error("Status " + responseInfo.getStatusCode(), "URL " + responseInfo.getUrl());

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

                                                                        Logger.error("Status code _________", "__________________" + responseInfo.getStatusCode());

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
                if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                doubleClickPopup = SystemClock.elapsedRealtime();

                dialog.dismiss();
                goToNextPage(position);
            }
        });

        dialog.show();
    }

    /**
     * Before next page downloaded car set selected by 1
     *
     * @param position compared downloaded car
     */
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

    /**
     * Start Car download procedure
     */
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

    private void getRegIdForPush() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        new ApiCall().postDeviceRegistrationForPush(NissanApp.getInstance().getDeviceID(getApplicationContext()), regId, Values.DEVICE_TYPE, new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                    if (!new PreferenceUtil(getApplicationContext()).getPushRegistrationStatus()) {
                        new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(true);
                    }

                    Logger.error("Device registration Successfull", "________________________________successful");
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
                    //click for eur/rus by rohan
                    if (i == 1 || i == 4 || i == 14) { // this logic actually work for car id (2 & 5) since i start from 0
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
            if (NissanApp.getInstance().getCarList() != null) {
                setCarImageAccordingToDeviceResolution();
            }

            getList = NissanApp.getInstance().getCarList();
            boolean xtrailRus = false, xtrailEur = false;
            CarInfo xtrailRusInfo = new CarInfo();
            getList = NissanApp.getInstance().getCarList();
            for (int k = 0; k < getList.size(); k++) {
                if (getList.get(k).getClass() == CarInfo.class) {
                    CarInfo info = (CarInfo) getList.get(k);

                    if (info.getStatus().equals("0") && info.getId() == 13) {
                        xtrailEur = true;
                    }

                    if (info.getStatus().equals("0") && info.getId() == 15) {
                        xtrailRusInfo = info;
                        xtrailRus = true;
                    }

                }
            }

            if (xtrailEur && xtrailRus){
                getList.remove(xtrailRusInfo);
            }

            CarInfo xtrailInfo = new CarInfo();
            CarInfo leafInfo = new CarInfo();
            int xtrailIndex = -1, leafIndex = -1;

            if (xtrailLeafAvailableForDownload(getList)) {
                for (int k = 0; k < getList.size(); k++) {
                    if (getList.get(k).getClass() == CarInfo.class) {
                        CarInfo info = (CarInfo) getList.get(k);

                        if (info.getId() == 15) {
                            xtrailInfo = info;
                            xtrailIndex = k;
                        }

                        if (info.getId() == 14) {
                            leafInfo = info;
                            leafIndex = k;
                        }

                    }
                }


                getList.set(xtrailIndex, leafInfo);
                getList.set(leafIndex, xtrailInfo);

            }



            if (NissanApp.getInstance().getCarList() != null && adapter == null) {

                adapter = new CarDownloadAdapter(getApplicationContext(), getList, new CarDownloadAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(final int carId, final int position) {

                        String internetCheckMessage = internetCheckMessage = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);

                        if (DetectConnection.checkInternetConnection(activity.getApplicationContext())) {
                            showCarDeleteDialog(carId, position);
                        } else {

                            NissanApp.getInstance().showInternetAlert(activity, internetCheckMessage.isEmpty() ? context.getResources().getString(R.string.internet_connect) : internetCheckMessage);
                        }









                    }
                });
                lstView.setAdapter(adapter);
                lstView.setDivider(null);
            } else {
                //adapter.setList(NissanApp.getInstance().getCarList());
                adapter.setList(getList);
            }

        }
    }

    private void showCarDeleteDialog(final int carType, final int position) {

        final Dialog dialog = new DialogController(activity).langDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);

            String deleteMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.DELETE_MESSAGE);
            txtViewTitle.setText(deleteMsg == null || deleteMsg.isEmpty() ? resources.getString(R.string.alert_msg23) : deleteMsg);


        //TODO
        String okText = NissanApp.getInstance().getGlobalMessage(context).getOk();
        String cancelText = NissanApp.getInstance().getGlobalMessage(context).getCancel();

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        btnOk.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_OK) : okText);
        btnCancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_CANCEL) : cancelText);

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

                deleteCar(carType, position);
            }
        });

        dialog.show();
    }

    private void deleteCar(final int carId, final int position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialogController(activity).showDialog("Car Deleting..."); //getResources().getString(R.string.start_car_delete));
            }
        });

        final int carIdFromList = ((CarInfo) getList.get(position)).getId();

        new ApiCall().postCarDelete("" + carId, "" + NissanApp.getInstance().getLanguageID("en"), "0", NissanApp.getInstance().getDeviceID(activity.getApplicationContext()), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                Logger.error("car_delete", "_________" + "onDownloaded");
                Logger.error("response_code", "_________" + responseInfo.getStatusCode());



                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    try {

                        FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carId)));
//                                        ((CarD) activity).sendMsgToGoogleAnalytics(((MainActivity) activity).getAnalyticsForDelete(carNames[carId - 1], Analytics.CAR_SELECTION + Analytics.DELETE));


                        if (carIdFromList == 15) {
                            if (commonDao.getStatus(context, carIdFromList - 2) == 0) {
                                commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                getList.remove(position);
                            } else {
                                commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                getList.remove(position);
                                ((CarInfo) getList.get(position)).setStatus("0");
                            }
                        }

                        if (carIdFromList == 13) {
                            commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                            if (commonDao.getStatus(context, carIdFromList + 2) == 0) {
                                commonDao.updateDateAndStatus(context, carIdFromList + 2, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

                                getList.remove(position);



                            } else {
                                commonDao.updateDateAndStatus(context, carIdFromList + 2, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                ((CarInfo) getList.get(position)).setStatus("1");
                            }
                        }


                        if (carIdFromList == 2 || carIdFromList == 5) {
                            if (commonDao.getStatus(context, carIdFromList - 1) == 2) {
                                commonDao.updateDateAndStatus(context, carIdFromList, "2", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                getList.remove(position);
                            } else {
                                commonDao.updateDateAndStatus(context, carIdFromList, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                ((CarInfo) getList.get(position)).setStatus("2");
                            }
                        } else {
                            if (carIdFromList == 1 || carIdFromList == 4) {
                                commonDao.updateDateAndStatus(context, carIdFromList, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                if (commonDao.getStatus(context, carIdFromList + 1) == 2) {
                                    commonDao.updateDateAndStatus(context, carIdFromList + 1, "2", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    getList.remove(position);
                                } else {
                                    commonDao.updateDateAndStatus(context, carIdFromList + 1, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    ((CarInfo) getList.get(position)).setStatus("2");
                                }
                            } else {
                                if (carIdFromList == 7 || carIdFromList == 9) {
                                    commonDao.updateDateAndStatus(context, carIdFromList, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    ((CarInfo) getList.get(position)).setStatus("2");
                                } else {
                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    ((CarInfo) getList.get(position)).setStatus("0");
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {


                        getList = new ArrayList<>();
                        getList = getData();

                        boolean euroAvailable = false;

                        for (int i = 0; i <getList.size(); i++) {
                            if (getList.get(i).getClass() == CarInfo.class) {
                                CarInfo info = ((CarInfo) getList.get(i));
                                if (info.getId() == 13 && info.getStatus().equals("0")) {
                                    euroAvailable = true;
                                }
                            }
                        }

                        for (int i = 0; i <getList.size(); i++) {
                            if (getList.get(i).getClass() == CarInfo.class) {
                                CarInfo info = ((CarInfo) getList.get(i));
                                if (euroAvailable) {
                                    if (info.getName().contains("RUS") && info.getId() == 15 && info.getStatus().equals("0"))
                                        getList.remove(info);
                                }
                            }
                        }

                        adapter.setList(getList);
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                } else {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    showErrorDialog("Status or URL not reachable");
                }
            }

            @Override
            public void onFailed(String failedReason) {

                Logger.error("car_delete", "_________" + "onFailed");

                if (progressDialog != null)
                    progressDialog.dismiss();
                showErrorDialog(resources.getString(R.string.failed_to_connect_server));
                Logger.error("Car deleting", "____________" + failedReason);
            }
        });

    }

    private ArrayList<Object> getData() {


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
        if (NissanApp.getInstance().getCarList() != null) {
            setCarImageAccordingToDeviceResolution();
        }

        getList = NissanApp.getInstance().getCarList();

        boolean xtrailRus = false, xtrailEur = false;
        CarInfo xtrailRusInfo = new CarInfo();
        getList = NissanApp.getInstance().getCarList();
        for (int k = 0; k < getList.size(); k++) {
            if (getList.get(k).getClass() == CarInfo.class) {
                CarInfo info = (CarInfo) getList.get(k);

                if (info.getStatus().equals("0") && info.getId() == 13) {
                    xtrailEur = true;
                }

                if (info.getStatus().equals("0") && info.getId() == 15) {
                    xtrailRusInfo = info;
                    xtrailRus = true;
                }

            }
        }

        if (xtrailEur && xtrailRus){
            getList.remove(xtrailRusInfo);
        }

        CarInfo xtrailInfo = new CarInfo();
        CarInfo leafInfo = new CarInfo();
        int xtrailIndex = -1, leafIndex = -1;

        if (xtrailLeafAvailableForDownload(getList)) {
            for (int k = 0; k < getList.size(); k++) {
                if (getList.get(k).getClass() == CarInfo.class) {
                    CarInfo info = (CarInfo) getList.get(k);

                    if (info.getId() == 15) {
                        xtrailInfo = info;
                        xtrailIndex = k;
                    }

                    if (info.getId() == 14) {
                        leafInfo = info;
                        leafIndex = k;
                    }

                }
            }


            getList.set(xtrailIndex, leafInfo);
            getList.set(leafIndex, xtrailInfo);

        }

        return getList;


    }

    private boolean xtrailLeafAvailableForDownload(ArrayList<Object> getList) {


        boolean isfifteenIdAvailable = false;

        for (int k = 0; k < getList.size(); k++) {
            if (getList.get(k).getClass() == CarInfo.class) {
                CarInfo info = (CarInfo) getList.get(k);
                if (info.getStatus().equals("0") && info.getId() == 14) {
                    if (isfifteenIdAvailable)
                        return true;
                }

                if (info.getStatus().equals("0") && info.getId() == 15) {
                    isfifteenIdAvailable = true;
                }
            }
        }
        return false;
    }

    private void setCarImageAccordingToDeviceResolution() {

        String device_density = "", carImageURL = "";
        CarInfo info = new CarInfo();

        ArrayList<Object> mainList = NissanApp.getInstance().getCarList();

        if (mainList == null || mainList.size() == 0)
            return;

        if (carListArrayList != null && carListArrayList.size() > 0) {

            device_density = NissanApp.getInstance().getDensityName(this);

            for (int i = 0; i < mainList.size(); i++) {

                if (mainList.get(i).getClass() == CarInfo.class) {
                    info = (CarInfo) mainList.get(i);
                    for (CarList carListModel : carListArrayList) {

                        if (info.getId() == Integer.parseInt(carListModel.getId())) {
                            carImageURL = NissanApp.getInstance().getURLAccordingToDensity(this, device_density, carListModel);
                            info.setCarImg(carImageURL);
                            info.setName(carListModel.getCarDisplayName());
                            mainList.set(i, info);
                        }
                    }
                }
            }

            NissanApp.getInstance().setCarList(mainList);

        }


    }

    private void startCarAssetsDownload(String assetsSource, String assetsDestination, String langSource, String langDestination) {
        // downloadCarAssets method download car asset and language both
        new MADownloadManager(activity, context).downloadCarAssets(false, NissanApp.getInstance().getCarName(Values.carType), assetsSource, assetsDestination, langSource, langDestination, new DownloaderStatus() {
            @SuppressLint("StaticFieldLeak")
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
                                            // set the car paht where car asset is downloaded
                                            Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);

                                            commonDao.updateDateAndStatus(getBaseContext(), Values.carType, Values.ALREADY_DOWNLOADED, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            if (Values.carType == 1 || Values.carType == 4) {
                                                CarInfo carInfo = commonDao.getCarInfo(getApplicationContext(), Values.carType + 1);
                                                if (Values.carType == 1) {
                                                    // if car type 1 then update the 2 number car region = 'EUR' for display EUROPE and RUSSIA both car
                                                    if (commonDao.getStatus(getBaseContext(), Values.carType + 1) == 2) {
                                                        // here update the car when download car number 1
                                                        commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 1, "2", NissanApp.getInstance().getDateTime(), "EUR", carInfo.getVersionName(), carInfo.getVersionCode());
                                                    }
                                                } else if (Values.carType == 4) {
                                                    if (commonDao.getStatus(getBaseContext(), Values.carType + 1) == 0) {
                                                        // here update the car when download car number 4
                                                        commonDao.updateDateAndStatus(getBaseContext(), Values.carType + 1, "0", NissanApp.getInstance().getDateTime(), "EUR", carInfo.getVersionName(), carInfo.getVersionCode());
                                                    }
                                                } else {

                                                }
                                            }
                                            commonDao.updateLanguageStatus(getBaseContext(), Values.carType, preferenceUtil.getSelectedLang());

                                            // here set the car selection for car downloaded settings adapter
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

                                            for (int k = 0; k < NissanApp.getInstance().getCarList().size(); k++) {
                                                if (NissanApp.getInstance().getCarList().get(k).getClass() == CarInfo.class) {
                                                    Log.e("name_id_after_download", "_____" + ((CarInfo)NissanApp.getInstance().getCarList().get(k)).getName() + "_____" + ((CarInfo)NissanApp.getInstance().getCarList().get(k)).getId() + "____");
                                                }
                                            }

                                            // if car is first time downloaded then show the Tutorial Activity
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
                Logger.error("urlReachable", "_________" + b);
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
