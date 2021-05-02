package com.nissan.alldriverguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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

import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.nissan.alldriverguide.interfaces.ParentCarListCompleteAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.model.parentCarList.ParentCarListResponse;
import com.nissan.alldriverguide.model.parentCarList.Parent_car_list;
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
import com.nissan.alldriverguide.utils.SingleContentUpdating;
import com.nissan.alldriverguide.utils.Values;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class CarDownloadActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        InterfaceGlobalMessageResponse, CarListACompleteAPI {

    private static final String TAG = "CarDownloadActivity";
    // Start------------ For permission related constants
    private static final int PERMISSION_REQUEST_CODE_ALL = 200;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 201;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 202;
    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 203;
    public Resources resources;
    private ListView lstView;
    private TextView txtView_title;
    private CarDownloadAdapter adapter;
    private PreferenceUtil preferenceUtil;
    private Tracker t;
    private CommonDao commonDao;
    // variable to track event time
    private long mLastClickTime = 0;
    // End------------ For permission related constants


    private ArrayList<Object> getList = new ArrayList<>();
    private String[] carNames = {"Qashqai EUR Specs", "Qashqai RUS Specs", "Juke", "X-Trail EUR Specs", "X-Trail RUS Specs",
            "Pulsar", "Micra", "Note", "Leaf", "Navara", "All New Nissan Micra", "New Nissan QASHQAI", "Nissan X-TRAIL",
            "New Nissan LEAF", "New Nissan X-TRAIL RUS", "New Nissan QASHQAI RUS", "Leaf 2019", "New Nissan Juke", "X-TRAIL EUR 2020"};
    private int[] indices = {1, 0, 2, 4, 3, 5, 6, 7, 8, 9, 10, 12, 14, 15, 13, 11, 16, 17, 18};
    private int[] previousCarArray = {1, 2, 4, 5, 7, 9, 3};

    private List<Parent_car_list> parent_car_lists = null;
    private ProgressDialog progressDialog;
    private ProgressBar pbCarDownload;
    private int selectedCarIndex = 0;
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
    private ParentCarListResponse parentCarListResponse;

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
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    /*FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();*/

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
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
        lstView = findViewById(R.id.lst_view);
        txtView_title = findViewById(R.id.txtView_title);
        preferenceUtil = new PreferenceUtil(getApplicationContext());
        pbCarDownload = findViewById(R.id.pbCarDownload);
        globalMessageController = new GlobalMessageController(this);
        carListContentController = new CarListContentController(this);
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
        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Logger.error("push_sp_status", "___________" + new PreferenceUtil(context).getPushRegistrationStatus());
        Logger.error("device_id", "___________" + NissanApp.getInstance().getDeviceID(getApplicationContext()));

        if (!new PreferenceUtil(context).getPushRegistrationStatus()) {

            if (DetectConnection.checkInternetConnection(context)) {

                final Dialog dialog = new DialogController(activity).pushRegistrationDialog();

                TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
                String pushTitle = NissanApp.getInstance().getAlertMessage(this, preferenceUtil.getSelectedLang(), Values.REGISTER_PUSH_MESSAGE);
                txtViewTitle.setText(pushTitle.isEmpty() ? getResources().getString(R.string.register_push) : pushTitle);

                String okText = NissanApp.getInstance().getGlobalMessage(this).getOk();
                String cancelText = NissanApp.getInstance().getGlobalMessage(this).getCancel();

                Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);

                btnOk.setText(okText.isEmpty() ? resources.getString(R.string.button_OK) : okText);
                btnCancel.setText(cancelText.isEmpty() ? resources.getString(R.string.button_CANCEL) : cancelText);

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
                    Logger.error("CarDownloadActivity: Device registration Successful", "________________________________" + "refresh token");
                    new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(true);
                }
            }

            @Override
            public void onFailed(String failedReason) {
                Logger.error("CarDownloadActivity: Device registration failed_refresh_token", "_____________________" + failedReason);
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

            if (Values.ALREADY_DOWNLOADED.equalsIgnoreCase(info.getStatus())) { // status 1 means downloaded car, 2 means previous car and 0 means available cars
                carDownloadCheck(info.getId());
            } else {

                if (!DetectConnection.checkInternetConnection(context)) {
                    showNoInternetDialogue("No Internet Connection. Please check your WIFI or cellular data network and try again.");
                    return;
                }

                //click for eur/rus by rohan
                if (info.getId() == 1 || info.getId() == 2
                        || info.getId() == 4 || info.getId() == 5
                        || info.getId() == 13 || info.getId() == 15
                        || info.getId() == 12 || info.getId() == 16) {
                    showCarDownloadDialog(info.getId());
                } else {
                    carDownloadCheck(info.getId());
                }
            }
        } else if (parent.getAdapter().getItem(position).getClass() == Parent_car_list.class) {
            Intent intent = new Intent(this, ModelYearActivity.class);
            Parent_car_list parent_car_list = (Parent_car_list) parent.getAdapter().getItem(position);
            intent.putExtra("parent_car_id", parent_car_list.getId());
            if (parent_car_list.getId() == 10) {
                showCarDownloadDialog(13);
            } else {
                startActivity(intent);
            }
        }
    }

    private void showCarDownloadDialog(final int carType) {
        final Dialog dialog = new DialogController(CarDownloadActivity.this).carDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(getResources().getString(R.string.download_msg));

        ImageButton btnEUR = dialog.findViewById(R.id.btn_eur);
        ImageButton btnRUS = dialog.findViewById(R.id.btn_rus);

        ImageButton imgBtnEur = dialog.findViewById(R.id.img_btn_eur_delete_donwload);
        ImageButton imgBtnRus = dialog.findViewById(R.id.img_btn_rus_delete_donwload);

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
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType))
                    && commonDao.getStatus(getBaseContext(), carType) == 1 && commonDao.getStatus(getBaseContext(), 19) == 1) {
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
        } else if (carType == 12) {//click for eur/rus by rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(getBaseContext(), carType) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 4)) && commonDao.getStatus(getBaseContext(), carType + 4) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
            }
        } else if (carType == 16) {//click for eur/rus by rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 4)) && commonDao.getStatus(getBaseContext(), carType - 4) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(getBaseContext(), carType) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
            }
        }

        btnEUR.setOnClickListener(v -> {
            dialog.dismiss();//click for eur/rus by rohan

            if (carType == 1 || carType == 4 || carType == 12) {
                carDownloadCheck(carType);
            } else if (carType == 13) {
                Intent intent = new Intent(this, ModelYearActivity.class);
                intent.putExtra("parent_car_id", 10);
                startActivity(intent);
            }
        });

        btnRUS.setOnClickListener(v -> {
            dialog.dismiss();//click for eur/rus by rohan

            if (carType == 2 || carType == 5) {
                carDownloadCheck(carType);
            } else if (carType == 13) {
                carDownloadCheck(carType + 2);
            } else if (carType == 15) {
                carDownloadCheck(carType);
            } else if (carType == 16) {
                carDownloadCheck(carType);
            } else if (carType == 12) {
                carDownloadCheck(carType + 4);
            } else {
                carDownloadCheck(carType + 1);
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

        }
    }

    private void loadCarData() {

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

        String sharedpref_key = "en_" + Values.CAR_LIST_KEY + "_1";
        Type type = new TypeToken<ArrayList<CarList>>() {
        }.getType();
        carListArrayList = new Gson().fromJson(preferenceUtil.retrieveMultiLangData(sharedpref_key), type);

        loadCarData();
        pbCarDownload.setVisibility(View.VISIBLE);

        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
            carListContentController.callApi(NissanApp.getInstance().getDeviceID(this), "1");
        } else {
            if (pbCarDownload.getVisibility() == View.VISIBLE)
                pbCarDownload.setVisibility(View.GONE);
        }
        new ApiCall().getParentCarList(new ParentCarListCompleteAPI() {
            @Override
            public void onDownloaded(ParentCarListResponse parentCarListResponse) {
                parent_car_lists = parentCarListResponse.getParent_car_list();
            }

            @Override
            public void onFailed(String failedReason) {
                Log.d(TAG, "onFailed: ParentCar Faild " + failedReason);
            }
        });

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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

    private void carDownloadCheck(final int carId) {
        Values.carType = carId; // set the car type/id

        if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(Values.carType))) { // here check the file existence on mmc
            if (commonDao.getStatus(getBaseContext(), Values.carType) == Integer.parseInt(Values.ALREADY_DOWNLOADED)) { // if car is downloaded (status 1 is used for downloaded car)
                CarInfo info = commonDao.getCarInfo(getApplicationContext(), Values.carType);
                if (info != null) {
                    // this condition for force download and when update build version
                    if (!NissanApp.getInstance().getVersionName().equalsIgnoreCase(info.getVersionName()) || info.getVersionCode() != NissanApp.getInstance().getVersionCode()) {
                        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
                            try {

                                FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carId)));
                                if (carId == 2 || carId == 5) {
                                    if (commonDao.getStatus(getApplicationContext(), carId - 1) == Integer.parseInt(Values.PREVIOUS_CAR)) {
                                        commonDao.updateDateAndStatus(getApplicationContext(), carId, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        adapter.list.remove(selectedCarIndex);
                                    } else {
                                        commonDao.updateDateAndStatus(getApplicationContext(), carId, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    }
                                } else {
                                    if (carId == 1 || carId == 4) {
                                        commonDao.updateDateAndStatus(getApplicationContext(), carId, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        if (commonDao.getStatus(getApplicationContext(), carId + 1) == 2) {
                                            commonDao.updateDateAndStatus(getApplicationContext(), carId + 1, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            adapter.list.remove(selectedCarIndex);
                                        } else {
                                            commonDao.updateDateAndStatus(getApplicationContext(), carId + 1, Values.ALREADY_DOWNLOADED, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        }
                                    } else {
                                        if (carId == 7 || carId == 9) {
                                            commonDao.updateDateAndStatus(getApplicationContext(), carId, Values.PREVIOUS_CAR, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        } else {
                                            commonDao.updateDateAndStatus(getApplicationContext(), carId, Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
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
                            goToNextPage(carId);
                        }

                    } else { // this block for when version code is same that means no update available
                        //check and get all the content update from database
                        final ArrayList<PushContentInfo> list = commonDao.getNotificationList(getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(getApplicationContext(), carId)));

                        if (list != null && list.size() > 0) {
                            if (DetectConnection.checkInternetConnection(getApplicationContext())) {
                                downloadContentUpdate(list, carId);
                            } else {
                                goToNextPage(carId);
                            }
                        } else {
                            goToNextPage(carId);
                        }
                    }
                }

            } else { // if car is not downloaded
                try {

                    Logger.error("path", "__________" + NissanApp.getInstance().getCarPath(Values.carType));

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

    private void downloadCar(String langUrl, String assetUrl,
                             final ArrayList<PushContentInfo> list,
                             final int position,
                             final StringBuilder stringBuilder) {
        CarDownloadHelper carDownloadHelper = new CarDownloadHelper(CarDownloadActivity.this, "" + Values.carType,
                langUrl, assetUrl,
                NissanApp.getInstance().getCarPath(Values.carType)
        );
        carDownloadHelper.downloadAssetAndLang();
        carDownloadHelper.getDownloadProgress().observe(CarDownloadActivity.this, carDownloadProgress -> {

            if (carDownloadProgress == null) return;

            if (carDownloadProgress == CarDownloadProgress.COMPLETE.INSTANCE) {
                downloadCompleted(list, position, stringBuilder);
            } else BaseActivity.checkCarDownloadProgress(CarDownloadActivity.this,
                    carDownloadProgress,
                    progressDialog);

        });
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

                                downloadCar(responseInfo.getLangUrl(), responseInfo.getAssetsUrl(),
                                        list, position, stringBuilder
                                );

//                                new MADownloadManager(
//                                        activity, getApplicationContext()).downloadLanguage(false, "Language", responseInfo.getUrl(), NissanApp.getInstance().getCarPath(Values.carType), new DownloaderStatus() {
//                                    @Override
//                                    public boolean onComplete(boolean b) {
//                                        if (b) {
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {

//                                        return false;
//                                    }
//
//                                    @Override
//                                    public int onError(int i) {
//                                        showErrorDialog("Error ! Unable to update content, Please try again.");
//                                        dismissDialog();
//                                        return 0;
//                                    }
//
//                                    @Override
//                                    public boolean internetConnection(boolean b) {

//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean urlReachable(boolean b) {
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean destinationExists(boolean b) {
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean sourcePath(boolean b) {
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean destinationPath(boolean b) {
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public void downloadCompletion(Float aFloat) {
//
//                                    }
//
//                                    @Override
//                                    public void init() {
//                                        if (progressDialog == null) {
//                                            progressDialog = new ProgressDialogController(CarDownloadActivity.this).downloadProgress(CarDownloadActivity.this.getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
//                                        }
//                                    }
//                                });


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

    @SuppressLint("StaticFieldLeak")
    private void downloadCompleted(final ArrayList<PushContentInfo> list, final int position, final StringBuilder stringBuilder) {
        if (progressDialog != null) {
            progressDialog.setMessage(getResources().getString(R.string.data_syncing));
        }

        new SingleContentUpdating(this, selectedLang, Values.carType) {
            @Override
            public void onComplete(boolean status) {

                if (status) {

                    new ApiCall().postContentDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), stringBuilder.toString(), NissanApp.getInstance().getDeviceID(getApplicationContext()), new CompleteAPI() {
                        @SuppressLint("StaticFieldLeak")
                        @Override
                        public void onDownloaded(ResponseInfo responseInfo) {

                            Logger.error("Status code _________", "__________________" + responseInfo.getStatusCode());

                            if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                                try {
                                    commonDao.updateLanguageStatus(getBaseContext(), Values.carType, selectedLang);
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
//        String regId ="";

        new ApiCall().postDeviceRegistrationForPush(NissanApp.getInstance().getDeviceID(getApplicationContext()), null, Values.DEVICE_TYPE, new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                    if (!new PreferenceUtil(getApplicationContext()).getPushRegistrationStatus()) {
                        new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(true);
                    }

                    Logger.error("CarDownloadActivity: Device registration Successful", "________________________________successful");
                }
            }

            @Override
            public void onFailed(String failedReason) {
                Logger.error("CarDownloadActivity: Device registration failed", "_____________________" + failedReason);
            }
        });
    }

    private void showCarDeleteDialog(final int carType, final int position) {

        final Dialog dialog = new DialogController(activity).langDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);

        TextView txt_header = dialog.findViewById(R.id.txt_header);
        txt_header.setText(activity.getResources().getString(R.string.delete));

        String deleteMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.DELETE_MESSAGE);
        txtViewTitle.setText(deleteMsg == null || deleteMsg.isEmpty() ? resources.getString(R.string.alert_msg23) : deleteMsg);


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

        final int carIdFromList = ((CarInfo) NissanApp.getInstance().getCarList().get(position)).getId();

        NissanApp.getInstance().setCarWiseLanguageList(getLanguageDataFromDBAccordingToCar(carIdFromList));

        new ApiCall().postCarDelete("" + carIdFromList, "" + NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(getApplicationContext(), carIdFromList)), "0", NissanApp.getInstance().getDeviceID(activity.getApplicationContext()), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    try {

                        FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carIdFromList)));
//                        ((CarDownloadActivity) activity).sendMsgToGoogleAnalytics(((MainActivity) activity).getAnalyticsForDelete(carNames[carId - 1], Analytics.CAR_SELECTION + Analytics.DELETE));

                        if (carIdFromList == 16) {
                            if (commonDao.getStatus(context, carIdFromList - 4) == 0) {
                                commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                getList.remove(position);
                            } else {
                                commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                getList.remove(position);
                                ((CarInfo) getList.get(position)).setStatus("0");
                            }
                        }

                        if (carIdFromList == 12) {
                            commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                            if (commonDao.getStatus(context, carIdFromList + 4) == 0) {
                                commonDao.updateDateAndStatus(context, carIdFromList + 4, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

                                getList.remove(position);

                            } else {
                                commonDao.updateDateAndStatus(context, carIdFromList + 4, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                ((CarInfo) getList.get(position)).setStatus("1");
                            }
                        }

                     /*   if (carIdFromList == 15) {
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
                        }*/


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
                                    if (carIdFromList != 12 && carIdFromList != 16) {
                                        commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        ((CarInfo) getList.get(position)).setStatus("0");
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                        getList = new ArrayList<>();
                        getList = getData();

                        boolean euroAvailable = false;

                       /* for (int i = 0; i < getList.size(); i++) {
                            if (getList.get(i).getClass() == CarInfo.class) {
                                CarInfo info = ((CarInfo) getList.get(i));
                                if (info.getId() == 13 && info.getStatus().equals("0")) {
                                    euroAvailable = true;
                                }
                            }
                        }

                        for (int i = 0; i < getList.size(); i++) {
                            if (getList.get(i).getClass() == CarInfo.class) {
                                CarInfo info = ((CarInfo) getList.get(i));
                                if (euroAvailable) {
                                    if (info.getName().contains("RUS") && info.getId() == 15 && info.getStatus().equals("0"))
                                        getList.remove(info);
                                }
                            }
                        }*/
//                        getList = swapXtrailEurRusIfBothDownloaded(getList);
                        adapter.setList(getList);
                        adapter.notifyDataSetChanged();
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

                if (progressDialog != null && progressDialog.isShowing())
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

        //For support Parent Car by Mostasim Billah
        if (parent_car_lists != null) {
            int firstCarindexForReplaceCarInfo = -1;
            int childCarSize = 0;
            for (int i = 0; i < parent_car_lists.size(); i++) {
                Parent_car_list parentCar = parent_car_lists.get(i);
                if (parentCar.getChild_car_support()) {

                    ArrayList<Object> carList = NissanApp.getInstance().getCarList();

                    Iterator<Object> iterator = carList.iterator();
                    int firstChildCarPosition = -1;
                    while (iterator.hasNext()) {
                        Object obj = iterator.next(); // must be called before you can call i.remove()
                        // Do something
                        //Object obj = iterator;
                        if (obj.getClass() == CarInfo.class) {
                            CarInfo carInfo = (CarInfo) obj;
                            if (carInfo.getParentCarId() == parentCar.getId()
                                    && carInfo.getCarModelVersion().equalsIgnoreCase("new")
                                    && !carInfo.getStatus().equals(Values.ALREADY_DOWNLOADED)) {

                                if (firstChildCarPosition == -1)
                                    firstChildCarPosition = carList.indexOf(obj);
                                iterator.remove();
                                childCarSize++;

                            }
                        }
                    }
                    for (int m = 0; m < carList.size(); m++) {
                        if (carList.get(m).getClass() == String.class) {
                            String ob = (String) carList.get(m);

                            if (ob.contains(resources.getString(R.string.available_for_download_car))) {
                                firstCarindexForReplaceCarInfo = (firstChildCarPosition == -1) ? m + 1 : firstChildCarPosition;
                            }
                        }

                    }
                    int totalChildCarSize = NissanApp.getInstance().getChildCars(Integer.valueOf(parentCar.getId())) != null ? NissanApp.getInstance().getChildCars(Integer.valueOf(parentCar.getId())).size() : -1;
                    int totalChildCarDownloaded = NissanApp.getInstance()
                            .getCountTotalChildCarDownloaded(CarDownloadActivity.this, parentCar.getId());
                    if (totalChildCarSize != totalChildCarDownloaded && totalChildCarSize != -1) {
                        NissanApp.getInstance().getCarList().add(firstCarindexForReplaceCarInfo, parentCar);
                    }

                }
            }
        }

        /*boolean xtrailRus = false, xtrailEur = false;
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

        if (xtrailEur && xtrailRus) {
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
        }*/

        return getList;
    }

    private ArrayList<Object> swapXtrailEurRusIfBothDownloaded(ArrayList<Object> getList) {

        CarInfo xtrailEuroInfo = new CarInfo();
        CarInfo xtrailRusInfo = new CarInfo();
        CarInfo leaf2017Info = new CarInfo();
        int xtrailEuroIndex = -1, xtrailRusIndex = -1, leaf2017Index = -1;
        boolean xtrailRusDownloaded = false, xtrailEuroDownloaded = false, leaf2017Downloaded = false;

        for (int k = 0; k < getList.size(); k++) {
            if (getList.get(k).getClass() == CarInfo.class) {
                CarInfo info = (CarInfo) getList.get(k);
                if (info.getStatus().equals("1") && info.getId() == 13) {
                    xtrailEuroInfo = info;
                    xtrailEuroIndex = k;
                    xtrailEuroDownloaded = true;
                }

                if (info.getStatus().equals("1") && info.getId() == 15) {
                    xtrailRusInfo = info;
                    xtrailRusIndex = k;
                    xtrailRusDownloaded = true;
                }

                if (info.getStatus().equals("1") && info.getId() == 14) {
                    leaf2017Info = info;
                    leaf2017Index = k;
                    leaf2017Downloaded = true;
                }
            }
        }

        if (xtrailEuroDownloaded && xtrailRusDownloaded) {
            getList.set(xtrailEuroIndex, xtrailRusInfo);
            getList.set(xtrailRusIndex, xtrailEuroInfo);
        }

        if (xtrailRusDownloaded && leaf2017Downloaded && xtrailEuroDownloaded) {
            getList.set(xtrailRusIndex, leaf2017Info);
            getList.set(leaf2017Index, xtrailEuroInfo);
        }

        if (xtrailRusDownloaded && leaf2017Downloaded) {
            if (!xtrailEuroDownloaded) {
                getList.set(leaf2017Index, xtrailRusInfo);
                getList.set(xtrailRusIndex, leaf2017Info);
            }
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
                            if (carListModel.getParent_car_id() != null)
                                info.setParentCarId(Integer.valueOf(carListModel.getParent_car_id()));//new field added for parent car : MB
                            info.setCarModelVersion(carListModel.getCar_model_version());//new field added for parent car : MB
                            mainList.set(i, info);
                        }
                    }
                }
            }

            NissanApp.getInstance().setCarList(mainList);
        }
    }

    private void startCarAssetsDownload(String assetsSource, String assetsDestination, String langSource, String langDestination) {
        CarDownloadHelper carDownloadHelper = new CarDownloadHelper(CarDownloadActivity.this, "" + Values.carType,
                langSource, assetsSource,
                NissanApp.getInstance().getCarPath(Values.carType),
                langDestination, assetsDestination
        );
        carDownloadHelper.downloadAssetAndLang();
        carDownloadHelper.getDownloadProgress().observe(this, new Observer<CarDownloadProgress>() {
            @Override
            public void onChanged(CarDownloadProgress carDownloadProgress) {

                if (carDownloadProgress == null) return;

                if (carDownloadProgress == CarDownloadProgress.COMPLETE.INSTANCE) {
                    new ApiCall().postCarDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), "0", NissanApp.getInstance().getDeviceID(context), new CompleteAPI() {
                        @Override
                        public void onDownloaded(ResponseInfo responseInfo) {

                            if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                                sendMsgToGoogleAnalytics(NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOWNLOAD + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM);
                                // set the car path where car asset is downloaded
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

                                // if car is first time downloaded then show the Tutorial Activity
                                if (preferenceUtil.getIsFirstTime()) {
                                    startActivity(new Intent(CarDownloadActivity.this, TutorialActivity.class).putExtra("from", "activity"));
                                    finish();
                                } else {
                                    startActivity(new Intent(CarDownloadActivity.this, MainActivity.class));
                                }
                            }


                        }

                        @Override
                        public void onFailed(String failedReason) {
                            showErrorDialog(failedReason);
                            dismissDialog();
                        }
                    });
                } else BaseActivity.checkCarDownloadProgress(CarDownloadActivity.this,
                        carDownloadProgress,
                        progressDialog);

                // downloadCarAssets method download car asset and language both
//        new MADownloadManager(activity, context).downloadCarAssets(false, NissanApp.getInstance().getCarName(Values.carType), assetsSource, assetsDestination, langSource, langDestination, new DownloaderStatus() {
//            @SuppressLint("StaticFieldLeak")
//            @Override
//            public boolean onComplete(boolean b) {
//                if (b) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (progressDialog != null) {
//                                progressDialog.setMessage(getResources().getString(R.string.data_syncing));
//                            }
//                        }
//                    });
//
//                    new SearchDBAsync(activity, selectedLang, Values.carType) {
//                        @Override
//                        public void onComplete(boolean status) {
//
//                            if (status) {
//
//
//                                        } else {
//                                            showErrorDialog("Confirmation send error!");
//                                            dismissDialog();
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onFailed(String failedReason) {
//                                        showErrorDialog(failedReason);
//                                        dismissDialog();
//                                    }
//                                });
//
//                            } else {
//                                dismissDialog();
//                            }
//
//                        }
//                    }.execute();
//
//                } else {
//                    dismissDialog();
//                }
//                return false;
//            }
//
//            @Override
//            public int onError(int i) {
//                errorFileDelete(Values.carType);
//                return 0;
//            }
//
//            @Override
//            public boolean internetConnection(boolean b) {
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public boolean urlReachable(boolean b) {
//                errorFileDelete(Values.carType);
//                Logger.error("urlReachable", "_________" + b);
//                return false;
//            }
//
//            @Override
//            public boolean destinationExists(boolean b) {
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public boolean sourcePath(boolean b) {
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public boolean destinationPath(boolean b) {
//                errorFileDelete(Values.carType);
//                return false;
//            }
//
//            @Override
//            public void downloadCompletion(Float aFloat) {
//
//                String formattedString = String.format("%.02f", aFloat);
//                if (progressDialog != null) {
//                    progressDialog.setMessage(NissanApp.getInstance().getCarName(Values.carType) + "\n" + getResources().getString(R.string.alert_downloading) + formattedString + "%");
//                }
//            }
//
//            @Override
//            public void init() {
//                loadResource(selectedLang);
//                if (progressDialog == null) {
//                    progressDialog = new ProgressDialogController(CarDownloadActivity.this).downloadProgress(NissanApp.getInstance().getCarName(Values.carType) + "\n" + getResources().getString(R.string.alert_downloading));
//                }
//            }
//        });


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

    private List<LanguageList> getLanguageDataFromDBAccordingToCar(int carType) {

        Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        return new Gson().fromJson(preferenceUtil.retrieveMultiLangData(carType + "_" + Values.CAR_LANGUAGE_LIST), type);
    }

    public class LoadDataBase extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (preferenceUtil.getIsDatabaseEmpty()) {
                for (int i = 0; i < carNames.length; i++) {
                    CarInfo carInfo;
                    //click for eur/rus by rohan
                    if (i == 1 || i == 4 || i == 14 || i == 15 || i == 16) { // this logic actually work for car id (2 & 5) since i start from 0
                        carInfo = new CarInfo((i + 1), carNames[i], Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "RUS", "en", Values.CAR_NOT_SELECTED, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), indices[i]);
                    } else {
                        carInfo = new CarInfo((i + 1), carNames[i], Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "EUR", "en", Values.CAR_NOT_SELECTED, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), indices[i]);
                    }
                    commonDao.insertInCarInfoTable(getBaseContext(), carInfo); // insert into database as a CarInfo object
                }
                preferenceUtil.setIsDatabaseEmpty(false);
            } else {
                int lastID = commonDao.getLastID(getBaseContext());
                for (int i = lastID; i < carNames.length; i++) {
                    if (i == 16)
                        commonDao.insertInCarInfoTable(getBaseContext(), new CarInfo((lastID + 1), carNames[lastID], Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "RUS", "en", Values.CAR_NOT_SELECTED, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), indices[i]));
                    else
                        commonDao.insertInCarInfoTable(getBaseContext(), new CarInfo((lastID + 1), carNames[lastID], Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(), "EUR", "en", Values.CAR_NOT_SELECTED, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), indices[i]));
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

            ArrayList<Object> objectArrayList = commonDao.getAllOderCarList(getBaseContext());
            ArrayList<CarInfo> carInfoArrayList = commonDao.getAllCarList(getBaseContext());

            // here set the all ordered car list that getting from database as an object type
            NissanApp.getInstance().setCarList(objectArrayList);
            // here set the all unordered car list that getting from database as a CarInfo object
            NissanApp.getInstance().setCarAllList(carInfoArrayList);


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

            int xtrailRusDownloadStatus = commonDao.getStatus(getBaseContext(), 15);

            //For support Parent Car by Mostasim Billah
            if (parent_car_lists != null) {
                int firstCarindexForReplaceCarInfo = -1;
                int childCarSize = 0;
                for (int i = 0; i < parent_car_lists.size(); i++) {
                    Parent_car_list parentCar = parent_car_lists.get(i);
                    if (parentCar.getChild_car_support()) {

                        ArrayList<Object> carList = NissanApp.getInstance().getCarList();

                        Iterator<Object> iterator = carList.iterator();
                        int firstChildCarPosition = -1;
                        while (iterator.hasNext()) {
                            Object obj = iterator.next(); // must be called before you can call i.remove()
                            // Do something
                            //Object obj = iterator;
                            if (obj.getClass() == CarInfo.class) {
                                CarInfo carInfo = (CarInfo) obj;
                                if (carInfo.getParentCarId() == parentCar.getId()
                                        && carInfo.getCarModelVersion().equalsIgnoreCase("new")
                                        && !carInfo.getStatus().equals(Values.ALREADY_DOWNLOADED)) {

                                    if (firstChildCarPosition == -1)
                                        firstChildCarPosition = carList.indexOf(obj);
                                    iterator.remove();
                                    childCarSize++;

                                }
                            }
                        }
                        for (int m = 0; m < carList.size(); m++) {
                            if (carList.get(m).getClass() == String.class) {
                                String ob = (String) carList.get(m);

                                if (ob.contains(resources.getString(R.string.available_for_download_car))) {
                                    firstCarindexForReplaceCarInfo = (firstChildCarPosition == -1) ? m + 1 : firstChildCarPosition;
                                }
                            }

                        }
                        int totalChildCarSize = NissanApp.getInstance().getChildCars(Integer.valueOf(parentCar.getId())) != null ? NissanApp.getInstance().getChildCars(Integer.valueOf(parentCar.getId())).size() : -1;
                        int totalChildCarDownloaded = NissanApp.getInstance()
                                .getCountTotalChildCarDownloaded(CarDownloadActivity.this, parentCar.getId());
                        if (parentCar.getId() == 10) {
                            if (totalChildCarSize == totalChildCarDownloaded && xtrailRusDownloadStatus == 0
                                    || totalChildCarSize != totalChildCarDownloaded && totalChildCarSize != -1 && firstCarindexForReplaceCarInfo != -1) {
                                NissanApp.getInstance().getCarList().add(firstCarindexForReplaceCarInfo, parentCar);
                            }
                        } else if (totalChildCarSize != totalChildCarDownloaded && totalChildCarSize != -1 && firstCarindexForReplaceCarInfo != -1) {
                            NissanApp.getInstance().getCarList().add(firstCarindexForReplaceCarInfo, parentCar);
                        }

                    }
                }
            }

            if (NissanApp.getInstance().getCarList() != null && adapter == null) {

                adapter = new CarDownloadAdapter(getApplicationContext(), getList, (carId, position) -> {

                    String internetCheckMessage = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);

                    if (DetectConnection.checkInternetConnection(activity.getApplicationContext())) {
                        showCarDeleteDialog(carId, position);
                    } else {
                        NissanApp.getInstance().showInternetAlert(activity, internetCheckMessage.isEmpty() ? context.getResources().getString(R.string.internet_connect) : internetCheckMessage);
                    }

                });
                lstView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                lstView.setDivider(null);
            } else {
                adapter.setList(NissanApp.getInstance().getCarList());
            }
        }
    }
}
