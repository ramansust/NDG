package com.nissan.alldriverguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.adapter.ModelYearAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.ModelYearItemClcikListener;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.model.parentCarList.Parent_car_list;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
import com.nissan.alldriverguide.pushnotification.Config;
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
import java.util.List;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;

public class ModelYearActivity extends AppCompatActivity implements CarListACompleteAPI, ModelYearItemClcikListener {
    private static final String TAG = "ModelYearActivity";


    private Activity activity;
    private Context context;
    private Resources resources;
    private PreferenceUtil preferenceUtil;
    private ProgressDialog progressDialog;
    private ProgressBar pbCarDownload;
    private long mLastClickTime = 0;
    private long doubleClickPopup = 0;
    private CommonDao commonDao;
    private LinearLayout container;
    List<CarList> carLists;
    private int selectedCarIndex = 0;
    private String selectedLang = "";
    int parentCarId = -1;
    private ArrayList<Object> getList = new ArrayList<>();
    private List<CarList> carListArrayList = new ArrayList<>();
    private DisplayMetrics metrics;
    private Tracker t;
    private ModelYearAdapter adapter;
    ArrayList<Object> childCars = new ArrayList<>();
    List<CarList> allCarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_year);

        Intent intent = getIntent();
        parentCarId = intent.getIntExtra("parent_car_id", -1);

        initViews();
        loadResource(preferenceUtil.getSelectedLang());

        if (parentCarId != -1) {
            //new ApiCall().getChildCarList(NissanApp.getInstance().getDeviceID(this),"1",String.valueOf(parentCarId),this);
        }
        allCarList = NissanApp.getInstance().getCarListWAP();

        loadChildCarList(allCarList, parentCarId);

        loadData(childCars);

    }

    private void loadChildCarList(List<CarList> allCarList, int parentCarId) {
        int downloadedChildCar = 0;
        childCars.clear();
        for (CarList carList : allCarList) {
            if (Integer.valueOf(carList.getParent_car_id()) == parentCarId && carList.getCar_model_version().equals("new")) {
             /*carList.getId(),carList.getCarName(),
                        commonDao.getStatus(this,Integer.valueOf(carList.getId())),
                        NissanApp.getInstance().getDateTime(), */
                CarInfo carInfo = new CarInfo();
                carInfo.setId(Integer.valueOf(carList.getId()));
                carInfo.setName(carList.getCarName());
                carInfo.setStatus(String.valueOf(commonDao.getStatus(this, Integer.valueOf(carList.getId()))));
                carInfo.setDateTime(NissanApp.getInstance().getDateTime());
                carInfo.setCarModelVersion(carList.getCar_model_version());
                carInfo.setCarImg(carList.getCarImg());
                carInfo.setParentCarId(Integer.valueOf(carList.getParent_car_id()));
                if (Values.ALREADY_DOWNLOADED.equals(carInfo.getStatus()))
                    downloadedChildCar++;
                childCars.add(carInfo);
            }
        }
        if (childCars.size() == downloadedChildCar)
            finish();
    }

    private void initViews() {
        context = getApplicationContext();
        container = findViewById(R.id.model_container);
        commonDao = CommonDao.getInstance();
        activity = ModelYearActivity.this;
        preferenceUtil = new PreferenceUtil(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildCarList(allCarList, parentCarId);
        adapter.setList(childCars);

    }

    @Override
    public void onDownloaded(CarListResponse responseInfo) {
        carLists = responseInfo.getCarList();
        loadCarList(carLists);
    }

    private void loadCarList(List<CarList> carLists) {
        ArrayList<Object> modifiedCarList = new ArrayList<>();

        for (int i = 0; i < carLists.size(); i++) {
            CarList carList = carLists.get(i);
            CarInfo carInfo = new CarInfo(Integer.valueOf(carList.getId()), carList.getCarName(),
                    Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(),
                    "EUR", "en", Values.CAR_NOT_SELECTED,
                    NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), i);
            modifiedCarList.add(carInfo);
        }
        loadData(modifiedCarList);
    }

    private void loadData(ArrayList<Object> carLists) {
        View[] childViews = new View[1]; //we can have multiple child views
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Adding multiple views
        for (int v = 0; v < childViews.length; v++) {
            childViews[v] = inflater.inflate(R.layout.model_year_list, null);
            TextView title = childViews[v].findViewById(R.id.tv_model_year_list_title);
            title.setText("NEW NISSAN LEAF");
            RecyclerView recyclerView = childViews[v].findViewById(R.id.rv_model_year_list);


            adapter = new ModelYearAdapter(ModelYearActivity.this, carLists, this);


            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    ((LinearLayoutManager) mLayoutManager).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            //carAdapter.notifyDataSetChanged();

            container.addView(childViews[v]);

        }
    }

    @Override
    public void onFailed(String failedReason) {
        Log.d(TAG, "onFailed: " + failedReason);
    }

    @Override
    public void onItemClicked(RecyclerView.ViewHolder vh, Object item, final int position) {

        CarInfo carInfo = null;
        if (item.getClass() == CarInfo.class) {
            carInfo = (CarInfo) item;
        }
        // TODO: 2019-04-25 Copy the code from CarDownloadActivity to handle the car download  procedure
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
                        registerForPush(position);
                    }
                });
                dialog.show();


            } else {
                goForNormalOperation(position, item);
            }

        } else {
            goForNormalOperation(position, item);
        }

        //Toast.makeText(ModelYearActivity.this, ""+carInfo.getName(), Toast.LENGTH_SHORT).show();
    }

    private void registerForPush(final int position) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);

        new ApiCall().postDeviceRegistrationForPush(NissanApp.getInstance().getDeviceID(getApplicationContext()), pref.getString("regId", ""), Values.DEVICE_TYPE, new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Logger.error("Device registration Successful", "________________________________" + "refresh token");
                    new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(true);
//                    goForNormalOperation(position, parent);

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

    private void goForNormalOperation(final int position, Object item) {
        selectedCarIndex = position;

        // class cast for CarInfo class
        if (item.getClass() == CarInfo.class) {

            CarInfo info = (CarInfo) item;

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
        } else if (item.getClass() == Parent_car_list.class) {
            Intent intent = new Intent(this, ModelYearActivity.class);
            Parent_car_list parent_car_list = (Parent_car_list) item;
            intent.putExtra("parent_car_id", parent_car_list.getId());
            startActivity(intent);
        }
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
                    startActivity(new Intent(ModelYearActivity.this, LanguageSelectionActivity.class));
                }
            }
        } else {
            startActivity(new Intent(ModelYearActivity.this, LanguageSelectionActivity.class));
        }

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
        startActivity(new Intent(ModelYearActivity.this, MainActivity.class)); // here redirect the landing page to Main activity if car is downloaded.
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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

                if (DetectConnection.checkInternetConnection(ModelYearActivity.this)) {
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

                                                    new SingleContentUpdating(ModelYearActivity.this, selectedLang, Values.carType) {
                                                        @Override
                                                        public void onComplete(boolean status) {

                                                            if (status) {

                                                                new ApiCall().postContentDownloadConfirmation("" + Values.carType,
                                                                        "" + NissanApp.getInstance().getLanguageID(selectedLang),
                                                                        stringBuilder.toString(),
                                                                        NissanApp.getInstance().getDeviceID(getApplicationContext()),
                                                                        new CompleteAPI() {
                                                                            @Override
                                                                            public void onDownloaded(ResponseInfo responseInfo) {

                                                                                Logger.error("Status code _________", "__________________" + responseInfo.getStatusCode());

                                                                                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                                                                                    try {
//                                                                                ((MainActivity) getApplicationContext()).sendMsgToGoogleAnalytics(((MainActivity) getApplicationContext()).getAnalyticsFromSettings(Analytics.CHANGE_LANGUAGE + Analytics.DOWNLOAD));
                                                                                        commonDao.updateLanguageStatus(ModelYearActivity.this.getBaseContext(), Values.carType, selectedLang);
                                                                                        commonDao.deleteSingleCarEpub(ModelYearActivity.this, Values.carType);

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
                                            progressDialog.setMessage(ModelYearActivity.this.getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete) + formattedString + "%");
                                        }
                                    }

                                    @Override
                                    public void init() {
                                        if (progressDialog == null) {
                                            progressDialog = new ProgressDialogController(ModelYearActivity.this).downloadProgress(ModelYearActivity.this.getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
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

    private void showCarDeleteDialog(final int carType, final int position) {

        final Dialog dialog = new DialogController(activity).langDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);

        TextView txt_header = dialog.findViewById(R.id.txt_header);
        txt_header.setText(activity.getResources().getString(R.string.delete));

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

                        for (int i = 0; i < getList.size(); i++) {
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
                        }
                        getList = swapXtrailEurRusIfBothDownloaded(getList);
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

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                showErrorDialog(resources.getString(R.string.failed_to_connect_server));
                Logger.error("Car deleting", "____________" + failedReason);
            }
        });

    }

    private List<LanguageList> getLanguageDataFromDBAccordingToCar(int carType) {

        Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        return new Gson().fromJson(preferenceUtil.retrieveMultiLangData(carType + "_" + Values.CAR_LANGUAGE_LIST), type);

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

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showCarDownloadDialog(final int carType) {
        final Dialog dialog = new DialogController(ModelYearActivity.this).carDialog();

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

        btnEUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//click for eur/rus by rohan

                if (carType == 1 || carType == 4 || carType == 13 || carType == 12) {
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
                } else if (carType == 16) {
                    carDownloadCheck(carType);
                } else if (carType == 12) {
                    carDownloadCheck(carType + 4);
                } else {
                    carDownloadCheck(carType + 1);
                }
            }
        });

        dialog.show();
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

        }

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
                                                startActivity(new Intent(ModelYearActivity.this, TutorialActivity.class).putExtra("from", "activity"));
                                                finish();
                                            } else {
                                                startActivity(new Intent(ModelYearActivity.this, MainActivity.class));
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
                    progressDialog = new ProgressDialogController(ModelYearActivity.this).downloadProgress(NissanApp.getInstance().getCarName(Values.carType) + "\n" + getResources().getString(R.string.alert_downloading));
                }
            }
        });
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(getSupportFragmentManager(), "error_fragment");
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

    private void loadResource(String lang) {
        resources = new Resources(getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(ModelYearActivity.this, lang));
    }

    private void errorFileDelete(int carType) {
        try {
            dismissDialog();
            FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carType)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
