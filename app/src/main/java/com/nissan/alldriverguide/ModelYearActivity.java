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
import com.nissan.alldriverguide.pushnotification.Config;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;

public class ModelYearActivity extends AppCompatActivity implements CarListACompleteAPI, ModelYearItemClcikListener {

    private static final String TAG = "ModelYearActivity";

    private Activity activity;
    private Context context;
    private Resources resources;
    private PreferenceUtil preferenceUtil;
    private ProgressDialog progressDialog;
    private long mLastClickTime = 0;
    private long doubleClickPopup = 0;
    private CommonDao commonDao;
    private LinearLayout container;
    List<CarList> carLists;
    private int selectedCarIndex = 0;
    private String selectedLang = "";
    int parentCarId = -1;
    private final List<CarList> carListArrayList = new ArrayList<>();
    private DisplayMetrics metrics;
    private ModelYearAdapter adapter;
    private final ArrayList<Object> childCars = new ArrayList<>();
    private List<CarList> allCarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_year);

        Intent intent = getIntent();
        parentCarId = intent.getIntExtra("parent_car_id", -1);

        initViews();
        loadResource(preferenceUtil.getSelectedLang());
        allCarList = NissanApp.getInstance().getCarListWAP();

        loadChildCarList(allCarList, parentCarId);
        loadData(childCars);
    }

    private void loadChildCarList(List<CarList> allCarList, int parentCarId) {
        int downloadedChildCar = 0;
        childCars.clear();
        for (CarList carList : allCarList) {
            if (Integer.parseInt(carList.getParent_car_id()) == parentCarId
                    && carList.getCar_model_version().equals("new")
                    && !carList.getId().equals("15") //skip Xtrail 2017 Rus Version
            ) {
             /*carList.getId(),carList.getCarName(),
                        commonDao.getStatus(this,Integer.valueOf(carList.getId())),
                        NissanApp.getInstance().getDateTime(), */
                CarInfo carInfo = new CarInfo();
                carInfo.setId(Integer.parseInt(carList.getId()));
                carInfo.setName(carList.getCarName());
                carInfo.setStatus(String.valueOf(commonDao.getStatus(this, Integer.parseInt(carList.getId()))));
                carInfo.setDateTime(NissanApp.getInstance().getDateTime());
                carInfo.setCarModelVersion(carList.getCar_model_version());
                carInfo.setCarImg(carList.getCarImg());
                carInfo.setParentCarId(Integer.parseInt(carList.getParent_car_id()));
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
            CarInfo carInfo = new CarInfo(Integer.parseInt(carList.getId()), carList.getCarName(),
                    Values.AVAILABLE_FOR_DOWNLOAD, NissanApp.getInstance().getDateTime(),
                    "EUR", "en", Values.CAR_NOT_SELECTED,
                    NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), i);
            modifiedCarList.add(carInfo);
        }
        loadData(modifiedCarList);
    }

    @SuppressLint("InflateParams")
    private void loadData(ArrayList<Object> carLists) {
        View[] childViews = new View[1]; //we can have multiple child views
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Adding multiple views
        for (int v = 0; v < childViews.length; v++) {
            childViews[v] = inflater.inflate(R.layout.model_year_list, null);
            TextView title = childViews[v].findViewById(R.id.tv_model_year_list_title);
            title.setText("NEW NISSAN LEAF");
            RecyclerView recyclerView = childViews[v].findViewById(R.id.rv_model_year_list);

            adapter = new ModelYearAdapter(carLists, this);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mLayoutManager.getOrientation());
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

        // TODO: 2019-04-25 Copy the code from CarDownloadActivity to handle the car download  procedure
        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Logger.error("push_sp_status", "___________" + new PreferenceUtil(context).getPushRegistrationStatus());

        if (!new PreferenceUtil(context).getPushRegistrationStatus()) {

            if (DetectConnection.checkInternetConnection(context)) {

                final Dialog dialog = new DialogController(activity).pushRegistrationDialog();

                TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
                String pushTitle = NissanApp.getInstance().getAlertMessage(this, preferenceUtil.getSelectedLang(), Values.REGISTER_PUSH_MESSAGE);
                txtViewTitle.setText(pushTitle.isEmpty() ? getResources().getString(R.string.register_push) : pushTitle);

                //TODO
                String okText = NissanApp.getInstance().getGlobalMessage(this).getOk();
                String cancelText = NissanApp.getInstance().getGlobalMessage(this).getCancel();

                Button btnCancel = dialog.findViewById(R.id.btn_cancel);
                Button btnOk = dialog.findViewById(R.id.btn_ok);

                btnOk.setText(okText.isEmpty() ? resources.getString(R.string.button_OK) : okText);
                btnCancel.setText(cancelText.isEmpty() ? resources.getString(R.string.button_CANCEL) : cancelText);

                btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                    Toast.makeText(context, "Without registration you cannot download car", Toast.LENGTH_LONG).show();
                });

                btnOk.setOnClickListener(v -> {
                    dialog.dismiss();
                    progressDialog = new ProgressDialogController(activity).showDialog(resources.getString(R.string.register_push_dialog));
                    registerForPush(position);
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
                    showNoInternetDialogue();
                    return;
                }

                //click for eur/rus by rohan
                if (info.getId() == 1 || info.getId() == 2
                        || info.getId() == 4 || info.getId() == 5
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

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(resources.getString(R.string.update_msg));

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText(resources.getString(R.string.button_YES));

        btnOk.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
                return;
            }
            doubleClickPopup = SystemClock.elapsedRealtime();

            dialog.dismiss();

            if (DetectConnection.checkInternetConnection(ModelYearActivity.this)) {
                runOnUiThread(() -> progressDialog = new ProgressDialogController(activity).showDialog(getResources().getString(R.string.start_download)));

                new ApiCall().postContentDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), stringBuilder.toString(), NissanApp.getInstance().getDeviceID(getApplicationContext()), new CompleteAPI() {
                    @Override
                    public void onDownloaded(ResponseInfo responseInfo) {

                        Logger.error("Status " + responseInfo.getStatusCode(), "URL " + responseInfo.getUrl());

                        if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getUrl())) {
                            Logger.error("content_url", "____________" + responseInfo.getUrl());
                            //TODO Implement download
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
            }
        });

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setText(resources.getString(R.string.button_NO));

        btnCancel.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
                return;
            }
            doubleClickPopup = SystemClock.elapsedRealtime();

            dialog.dismiss();
            goToNextPage(position);
        });

        dialog.show();
    }

    private void showNoInternetDialogue() {

        final Dialog dialog = new DialogController(activity).internetDialog();

        dialog.setCancelable(false);

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText("No Internet Connection. Please check your WIFI or cellular data network and try again.");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void startCarDownloadProcedure() {
        progressDialog = new ProgressDialogController(activity).showDialog(getResources().getString(R.string.start_download));

        new ApiCall().postCarDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(selectedLang), "0", NissanApp.getInstance().getDeviceID(this), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                if (Values.SUCCESS_STATUS.equals(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getAssetsUrl()) && !TextUtils.isEmpty(responseInfo.getLangUrl())) {
//                    startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? responseInfo.getAssetsUrl() : NissanApp.getInstance().getAssetsURL(Values.carType), AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL(Values.carType, selectedLang), NissanApp.getInstance().getCarPath(Values.carType));
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

        btnEUR.setOnClickListener(v -> {
            dialog.dismiss();//click for eur/rus by rohan

            if (carType == 1 || carType == 4 || carType == 13 || carType == 12) {
                carDownloadCheck(carType);
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

        ArrayList<Object> getList;

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

        String device_density, carImageURL;
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
                            info.setParentCarId(Integer.parseInt(carListModel.getParent_car_id()));//new field added for parent car : MB
                            info.setCarModelVersion(carListModel.getCar_model_version());//new field added for parent car : MB
                            mainList.set(i, info);
                        }
                    }
                }
            }

            NissanApp.getInstance().setCarList(mainList);
        }
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(getSupportFragmentManager(), "error_fragment");
    }

    private void loadResource(String lang) {
        resources = new Resources(getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(ModelYearActivity.this, lang));
    }
}
