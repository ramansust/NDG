package com.nissan.alldriverguide.adapter;

import static com.nissan.alldriverguide.utils.Values.ALERT_MSG_TYPE_LOADING_TEXT;
import static com.nissan.alldriverguide.utils.Values.DATA_SYNCING;
import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.DOWNLOADING;
import static com.nissan.alldriverguide.utils.Values.STARTING_DOWNLOAD;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nissan.alldriverguide.BaseActivity;
import com.nissan.alldriverguide.CarDownloadHelper;
import com.nissan.alldriverguide.CarDownloadProgress;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.controller.CarListContentController;
import com.nissan.alldriverguide.controller.GlobalMessageController;
import com.nissan.alldriverguide.controller.LanguageSelectionController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.settings.AddCarFragment;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.InterfaceGlobalMessageResponse;
import com.nissan.alldriverguide.interfaces.ModelYearItemClcikListener;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.LanguageInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.interfaces.InterfaceLanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.AppConfig;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.SearchDBAsync;
import com.nissan.alldriverguide.utils.Values;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by raman on 1/19/17.
 */
public class CarDownloadSettingsAdapter extends BaseAdapter implements View.OnClickListener, InterfaceGlobalMessageResponse, CarListACompleteAPI, InterfaceLanguageListResponse, ModelYearItemClcikListener {

    private static final String TAG = "CarDownloadSettingsAdapter";
    public ArrayList<CarInfo> list;
    public int position = 0;
    public int carType = 0;
    public String lang = "";
    public ProgressDialog progressDialog;
    List<LanguageList> languageLists = new ArrayList<>();
    private final CommonDao commonDao;
    private final Context context;
    private LayoutInflater inflater;
    private final Activity activity;
    private int selectedCarPosition = 0;
    private final AddCarFragment frag;
    private Dialog dialog, dialogModelYearFeature;
    private int downloading_lang_id = 0;
    private Resources resources;
    private DisplayMetrics metrics;
    private final PreferenceUtil preferenceUtil; // declare the native internal storage
    private String internetCheckMessage = "";
    private GlobalMessageController controller;
    private int selectedCarType = -1;
    private long mLastClickTime = 0;
    private List<CarList> carListArrayList = new ArrayList<>();
    private ArrayList<Object> childCarsList = new ArrayList<>();
    private String dataSyncingMsg = "", downloadingMsg = "";

    public CarDownloadSettingsAdapter(AddCarFragment frag, Activity activity, Context context, ArrayList<CarInfo> list) {
        this.activity = activity;
        this.context = context;
        this.list = list;

        this.frag = frag;
        inflater = LayoutInflater.from(this.context);
        commonDao = CommonDao.getInstance();
        preferenceUtil = new PreferenceUtil(context);
        loadResource();
        getDataFromSP();
    }

    private void getCarList(String carId) {

        Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        languageLists = new Gson().fromJson(new PreferenceUtil(context).retrieveMultiLangData(carId + "_" + Values.CAR_LANGUAGE_LIST), type);

        if (languageLists == null || languageLists.size() == 0) {
            if (DetectConnection.checkInternetConnection(context)) {

                String loadingMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), ALERT_MSG_TYPE_LOADING_TEXT);

                if (progressDialog == null)
                    progressDialog = new ProgressDialog(activity);

                progressDialog.setMessage(loadingMsg == null || loadingMsg.isEmpty() ? resources.getString(R.string.loading) : loadingMsg);
                progressDialog.setCancelable(false);
                progressDialog.show();

                LanguageSelectionController controllerLanguageSelection = new LanguageSelectionController(this);
                controllerLanguageSelection.callApi(NissanApp.getInstance().getDeviceID(context), carId);

            } else {
                Toast.makeText(context, "No Internet!", Toast.LENGTH_SHORT).show();
            }
        } else {
            showLanguageDialog(Integer.parseInt(carId));
        }

    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            // here inflate the list item view layout
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.car_download_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).isSection()) {
            viewHolder.relativeLayoutSection.setVisibility(View.VISIBLE);
            if (list.get(position).getStatus().equalsIgnoreCase("1") && list.get(position).isSection()) {
                viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
                viewHolder.relativeLayoutSection.setBackgroundResource(R.drawable.downloaded_car);
            } else if (list.get(position).getStatus().equalsIgnoreCase("0") && list.get(position).isSection()) {
                viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                viewHolder.relativeLayoutSection.setBackgroundResource(R.drawable.available_for_downlaod);
            } else if (list.get(position).getStatus().equalsIgnoreCase("2") && list.get(position).isSection()) {
                viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                viewHolder.relativeLayoutSection.setBackgroundResource(R.drawable.previous_model);
            } else {
                viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

        } else {
            viewHolder.relativeLayoutSection.setVisibility(View.GONE);
        }

        if ("2".equalsIgnoreCase(list.get(position).getStatus())) {
            viewHolder.imageView.setImageURI(list.get(position).getCarImg());

            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            if (list.get(position).getId() == 1 || list.get(position).getId() == 2 || list.get(position).getId() == 4 || list.get(position).getId() == 5) {
                String[] name = list.get(position).getName().split(" "); //(carNames[list.get(position).getId() - 1]).split(" ");
                viewHolder.txtViewTitle.setText(name[0]);
//                viewHolder.txtViewTitle.setText(list.get(position).getName());
            } else {
//                viewHolder.txtViewTitle.setText(carNames[list.get(position).getId() - 1]);
                viewHolder.txtViewTitle.setText(list.get(position).getName());
            }
        } else if ("1".equalsIgnoreCase(list.get(position).getStatus())) {
            viewHolder.imageView.setImageURI(list.get(position).getCarImg());
            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
//            viewHolder.txtViewTitle.setText(carNames[list.get(position).getId() - 1]);
            viewHolder.txtViewTitle.setText(list.get(position).getName());
        } else if ("0".equalsIgnoreCase(list.get(position).getStatus())) {
            viewHolder.imageView.setImageURI(list.get(position).getCarImg());
            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            if (list.get(position).getId() == 1 || list.get(position).getId() == 2 || list.get(position).getId() == 4 || list.get(position).getId() == 5) {
                String[] name = list.get(position).getName().split(" "); //(carNames[list.get(position).getId() - 1]).split(" ");
                viewHolder.txtViewTitle.setText(name[0]);
//                viewHolder.txtViewTitle.setText(list.get(position).getName());
            } else if (list.get(position).getId() == 13 || list.get(position).getId() == 15 || list.get(position).getId() == 19) {
                viewHolder.txtViewTitle.setText("NISSAN X-TRAIL");
            } else if (list.get(position).getId() == 18) {
                viewHolder.txtViewTitle.setText("NISSAN JUKE");
            } else if (list.get(position).getId() == 14 || list.get(position).getId() == 17) {
                viewHolder.txtViewTitle.setText("NISSAN LEAF");
            } else if (list.get(position).getId() == 12 || list.get(position).getId() == 16) {
                viewHolder.txtViewTitle.setText("NISSAN QASHQAI");
            } else {
//                viewHolder.txtViewTitle.setText(carNames[list.get(position).getId() - 1]);
                viewHolder.txtViewTitle.setText(list.get(position).getName());
            }
        } else {
            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }

        // here set the car delete or download icon
        viewHolder.imgDeleteOrDownload.setTag("" + position);
        if ("1".equalsIgnoreCase(list.get(position).getStatus())) {
            viewHolder.imgDeleteOrDownload.setBackgroundResource(R.drawable.delete_selector);
        } else {
            viewHolder.imgDeleteOrDownload.setBackgroundResource(R.drawable.download_selector);
        }

        /*
          set selected car border and delete icon visible invisible
         */

        if (list.get(position).getSelectedCar() == 1 && "1".equalsIgnoreCase(list.get(position).getStatus())) {
            selectedCarPosition = position;
            viewHolder.imageViewBorder.setVisibility(View.VISIBLE);
            viewHolder.imgDeleteOrDownload.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.imageViewBorder.setVisibility(View.INVISIBLE);
            viewHolder.imgDeleteOrDownload.setVisibility(View.VISIBLE);
        }

        viewHolder.imgDeleteOrDownload.setOnClickListener(this);

        return convertView;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_btn_delete_or_download) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            position = Integer.parseInt(v.getTag().toString());

            // for downloaded car deleted action
            if ("1".equalsIgnoreCase(list.get(position).getStatus())) {
                if (DetectConnection.checkInternetConnection(activity.getApplicationContext())) {
//                        startCarDeleteProcedure();
                    showCarDownloadDialogForSingleCar(list.get(position).getId(), false);
                } else {

                    NissanApp.getInstance().showInternetAlert(activity, internetCheckMessage.isEmpty() ? context.getResources().getString(R.string.internet_connect) : internetCheckMessage);
                }

            } else { // for downloading car action
                if (NissanApp.getInstance().createPath(Values.PATH)) {
                    if (DetectConnection.checkInternetConnection(activity.getApplicationContext())) {
                        selectedCarType = list.get(position).getId();
                        if (list.get(position).getId() == 1 || list.get(position).getId() == 2
                                || list.get(position).getId() == 4 || list.get(position).getId() == 5
                                || list.get(position).getId() == 12 || list.get(position).getId() == 16) {
                            showCarDownloadDialog(list.get(position).getId());
                        } else if (list.get(position).getId() == 14 || list.get(position).getId() == 17
                                || list.get(position).getId() == 13 || list.get(position).getId() == 15
                                || list.get(position).getId() == 19) {
                            Log.e(CarDownloadSettingsAdapter.class.getSimpleName(), "onClick: ParentCarID " + NissanApp.getInstance().getParentId(list.get(position).getId()));
                            if (list.get(position).getId() == 13 || list.get(position).getId() == 15
                                    || list.get(position).getId() == 19) {
                                showCarDownloadDialog(13);
                            } else
                                modelYearFeatureDialog(NissanApp.getInstance().getParentId(list.get(position).getId()));
                        } else {
                            LanguageSelectionDialog(list.get(position).getId());
                        }
                    } else {
//                            NissanApp.getInstance().showInternetAlert(activity, context.getResources().getString(R.string.internet_connect));
                        NissanApp.getInstance().showInternetAlert(activity, internetCheckMessage.isEmpty() ? context.getResources().getString(R.string.internet_connect) : internetCheckMessage);
                    }
                }
            }
        }
    }

    private void modelYearFeatureDialog(int parentCarId) {
        loadChildCars(parentCarId);
    }

    private void loadChildCars(int parentCarId) {

        childCarsList = new ArrayList<>();
        if (parentCarId == -1) {
            Logger.error("parentCarId", "________" + parentCarId + "____problem");
            return;
        }
        Logger.error("parentCarId", "________" + parentCarId);
        for (CarList carList : NissanApp.getInstance().getCarListWAP()) {
            if (Integer.parseInt(carList.getParent_car_id()) == parentCarId &&
                    carList.getCar_model_version().equals("new") &&
                    !carList.getId().equals("15")) //skip Xtrail 2017 Rus Version
            {
                CarInfo carInfo = new CarInfo();
                Logger.error("car_id_child", "________" + carList.getId() + "_____" + carList.getCarName());
                carInfo.setId(Integer.parseInt(carList.getId()));
                carInfo.setName(carList.getCarName());
                carInfo.setStatus(String.valueOf(commonDao.getStatus(context, Integer.parseInt(carList.getId()))));
                carInfo.setDateTime(NissanApp.getInstance().getDateTime());
                carInfo.setCarModelVersion(carList.getCar_model_version());
                carInfo.setCarImg(carList.getCarImg());
                carInfo.setParentCarId(Integer.parseInt(carList.getParent_car_id()));
//                if (Integer.valueOf(carInfo.getStatus()) != 1)
                childCarsList.add(carInfo);
            }
        }
        if (childCarsList.size() > 0) {
            loadData();
        }

    }

    @SuppressLint("InflateParams")
    private void loadData() {

        dialogModelYearFeature = new DialogController(activity).modelYearFeatureDialog();

        LinearLayout container = dialogModelYearFeature.findViewById(R.id.model_container);

        View[] childViews = new View[1]; //we can have multiple child views
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Adding multiple views
        for (int v = 0; v < childViews.length; v++) {
            childViews[v] = inflater.inflate(R.layout.model_year_list, null);
            TextView title = childViews[v].findViewById(R.id.tv_model_year_list_title);
            title.setText("NEW NISSAN LEAF");
            RecyclerView recyclerView = childViews[v].findViewById(R.id.rv_model_year_list);


            ModelYearAdapter adapter = new ModelYearAdapter(childCarsList, this);


            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            //carAdapter.notifyDataSetChanged();

            container.addView(childViews[v]);
        }

        dialogModelYearFeature.show();
    }

    @Override
    public void onItemClicked(RecyclerView.ViewHolder vh, Object item, int pos) {
        CarInfo carInfo = null;
        int carId = -1;
        if (item.getClass() == CarInfo.class) {
            carInfo = (CarInfo) item;
        }
        if (carInfo != null) {
            carId = carInfo.getId();
            selectedCarType = carId;
            Logger.error("selected_car_id_child_on_item_clicked", "________" + carInfo.getId() + "_____" + carInfo.getName());
        }
        if (DetectConnection.checkInternetConnection(context)) {
            Logger.error("carType_before_lang", "_________" + carId);
            LanguageSelectionDialog(carId);
        }
    }

    @Override
    public void onDownloaded(GlobalMsgResponse responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

            String key_global_message = Values.carType + "_" + downloading_lang_id + "_" + Values.GLOBAL_MSG_KEY;
            String key_global_alert_message = Values.carType + "_" + downloading_lang_id + "_" + Values.GLOBAL_ALERT_MSG_KEY;

            preferenceUtil.storeMultiLangData(responseInfo.getAlertMessage(), key_global_alert_message);
            preferenceUtil.storeMultiLangData(responseInfo.getGlobalMessage(), key_global_message);

            NissanApp.getInstance().setGlobalMessageArrayList(responseInfo.getGlobalMessage());
            NissanApp.getInstance().setAlertMessageGlobalArrayList(responseInfo.getAlertMessage());

        }
    }

    @Override
    public void onDownloaded(CarListResponse responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
            String car_list_key = lang + "_" + Values.CAR_LIST_KEY + "_" + downloading_lang_id;
            NissanApp.getInstance().setCarListWAP(responseInfo.getCarList());
            preferenceUtil.storeMultiLangData(responseInfo.getCarList(), car_list_key);
        } else
            Logger.error(TAG, "CarListResponse_status________" + responseInfo.getStatusCode());
    }

    @Override
    public void onFailed(String failedReason) {
        Logger.error(TAG, "carListResponse____________" + failedReason);
    }

    @Override
    public void languageListDownloaded(List<LanguageList> languageListsTemp) {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (languageListsTemp != null && languageListsTemp.size() > 0) {
            preferenceUtil.storeMultiLangData(languageListsTemp, selectedCarType + "_" + Values.CAR_LANGUAGE_LIST);
            this.languageLists = languageListsTemp;
            showLanguageDialog(selectedCarType);
        } else {
            String noContentMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.NO_CONTENT_FOUND);
            showNoContentDialogue(noContentMsg == null || noContentMsg.isEmpty() ? resources.getString(R.string.no_content_found_url) : noContentMsg);
        }
    }

    private void showNoContentDialogue(String msg) {

        final Dialog dialog = new DialogController(activity).internetDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    @Override
    public void languageListFailed(String failedResponse) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        Logger.error(TAG, "languageListFailed__________" + failedResponse);
    }

    public void startCarAssetsDownload(String assetsSource, String assetsDestination, final String langSource, String langDestination, final boolean isForceDownload) {

        Logger.error("downloadingMsg", "________" + downloadingMsg + "________" + preferenceUtil.getSelectedLang());


        CarDownloadHelper carDownloadHelper = new CarDownloadHelper(
                context, NissanApp.getInstance().getCarName(Values.carType),
                langSource,
                assetsSource,
                Values.PATH,
                langDestination,
                assetsDestination,
                new PreferenceUtil(context),
                lang
        );
        carDownloadHelper.getDownloadProgress().observe(this.frag, carDownloadProgress -> {
            if (carDownloadProgress == null) return;
            if (carDownloadProgress == CarDownloadProgress.COMPLETE.INSTANCE) {
                downloadComplete(isForceDownload);
            } else BaseActivity.checkCarDownloadProgress(context,
                    carDownloadProgress,
                    progressDialog,
                    true
            );
        });
        carDownloadHelper.downloadAssetAndLang();


    }

    @SuppressLint("StaticFieldLeak")
    void downloadComplete(final boolean isForceDownload) {

        if (progressDialog != null) {
            progressDialog.setMessage(dataSyncingMsg == null || dataSyncingMsg.isEmpty() ? activity.getResources().getString(R.string.data_syncing) : dataSyncingMsg);
        }

        new SearchDBAsync(activity, lang, carType) {

            @Override
            public void onComplete(boolean status) {

                if (status) {

                    new ApiCall().postCarDownloadConfirmation("" + carType, "" + downloading_lang_id, "0", NissanApp.getInstance().getDeviceID(context), new CompleteAPI() {

                        @Override
                        public void onDownloaded(ResponseInfo responseInfo) {
                            if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                                Values.carType = carType;
                                Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);
                                commonDao.updateDateAndStatus(context, carType, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

                                if (carType == 1 || carType == 4) {
                                    if (commonDao.getStatus(context, carType + 1) == 2) {
                                        commonDao.updateDateAndStatus(context, carType + 1, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    }
                                } else if (carType == 13) {//click eur/rus rohan
                                    if (commonDao.getStatus(context, carType + 2) == 0) {
                                        commonDao.updateDateAndStatus(context, carType + 2, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    }
                                } else if (carType == 12) {
                                    if (commonDao.getStatus(context, carType + 4) == 0) {
                                        commonDao.updateDateAndStatus(context, carType + 4, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    }
                                } else if (carType == 14) {
                                    if (commonDao.getStatus(context, carType + 3) == 0) {
                                        commonDao.updateDateAndStatus(context, carType + 3, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    }
                                }

                                if (carType == 1 || carType == 2 || carType == 4 || carType == 5) {
                                    if (carType == 1 || carType == 4) {
                                        if (commonDao.getStatus(context, carType + 1) == 1) {

                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 0);

                                                list.add(position, info);
                                                list.get(position + 1).setId(carType + 1);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    } else {
                                        if (commonDao.getStatus(context, carType - 1) == 1) {
                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 0);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    }

                                } else if (carType == 14 || carType == 17) {

                                    if (carType == 14) {
                                        if (commonDao.getStatus(context, carType + 3) == 1) {

                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {

                                            if (!isForceDownload) {

                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 15);

                                                list.add(position, info);
                                                list.get(position + 1).setId(carType + 3);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    } else {
                                        if (commonDao.getStatus(context, carType - 3) == 1) {
                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 16);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    }


                                }
                                /*else if (carType == 13 || carType == 15) {

                                    if (carType == 13) {
                                        if (commonDao.getStatus(context, carType + 2) == 1) {

                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {

                                            if (!isForceDownload) {

                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 14);

                                                list.add(position, info);
                                                list.get(position + 1).setId(carType + 2);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    } else {
                                        if (commonDao.getStatus(context, carType - 2) == 1) {
                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 13);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    }
                                }*/


                                // Added By Raman - 21.10.2021

                                else if (carType == 13 || carType == 15 || carType == 19) {

                                    if (carType == 13) {
                                        if (commonDao.getStatus(context, carType + 2) == 1 && commonDao.getStatus(context, carType + 6) == 1) {

                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else if (commonDao.getStatus(context, carType + 2) == 1 && commonDao.getStatus(context, carType + 6) == 0) {
                                            if (!isForceDownload) {

                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 18);

                                                list.add(position, info);
                                                list.get(position + 1).setId(carType + 6);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        } else if (commonDao.getStatus(context, carType + 2) == 0 && commonDao.getStatus(context, carType + 6) == 1) {
                                            if (!isForceDownload) {

                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 14);

                                                list.add(position, info);
                                                list.get(position + 1).setId(carType + 2);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        } else {
                                            if (!isForceDownload) {

                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 14);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    } else if (carType == 15) {
                                        if (commonDao.getStatus(context, carType - 2) == 1 && commonDao.getStatus(context, carType + 4) == 1) {
                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else if (commonDao.getStatus(context, carType - 2) == 1 && commonDao.getStatus(context, carType + 4) == 0) {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 18);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        } else if (commonDao.getStatus(context, carType - 2) == 0 && commonDao.getStatus(context, carType + 4) == 1) {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 13);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    } else {
                                        if (commonDao.getStatus(context, carType - 6) == 1 && commonDao.getStatus(context, carType - 4) == 1) {
                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else if (commonDao.getStatus(context, carType - 6) == 1 && commonDao.getStatus(context, carType - 4) == 0) {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 13);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        } else if (commonDao.getStatus(context, carType - 6) == 0 && commonDao.getStatus(context, carType - 4) == 1) {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 14);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    }
                                } // Ended - Raman

                                else if (carType == 12 || carType == 16) {

                                    if (carType == 12) {
                                        if (commonDao.getStatus(context, carType + 4) == 1) {

                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {

                                            if (!isForceDownload) {

                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 11);

                                                list.add(position, info);
                                                list.get(position + 1).setId(carType + 4);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    } else {
                                        if (commonDao.getStatus(context, carType - 4) == 1) {
                                            list.get(selectedCarPosition).setSelectedCar(0);
                                            list.get(position).setSelectedCar(1);
                                            list.get(position).setName(commonDao.getCarName(context, carType));
                                            list.get(position).setStatus("1");
                                        } else {
                                            if (!isForceDownload) {
                                                CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode(), 12);

                                                list.add(position + 1, info);
                                            } else {
                                                list.get(position).setStatus("1");
                                                list.get(position).setSelectedCar(1);
                                            }

                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getId() != carType) {
                                                    list.get(i).setSelectedCar(0);
                                                }
                                            }
                                        }
                                    }


                                } else {
                                    list.get(position).setStatus("1");
                                    list.get(position).setSelectedCar(1);
                                    list.get(selectedCarPosition).setSelectedCar(0);
                                }


                                commonDao.updateLanguageStatus(context, carType, lang);
                                commonDao.updateAllPushContentStatusForSingleCar(context, carType, downloading_lang_id);
                                new PreferenceUtil(context).setSelectedLang(commonDao.getLanguageStatus(context, Values.carType));
                                ((MainActivity) activity).sendMsgToGoogleAnalytics(((MainActivity) activity).getAnalyticsFromSettings(Analytics.CAR_SELECTION + Analytics.DOWNLOAD));

                                frag.loadResource();
                                loadResource();
                                adapterNotify(false);

                            }
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailed(String failedReason) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                    });

                } else {
                    errorFileDelete(Values.carType);
                }
            }
        }.execute();
    }

    private void showCarDownloadDialogForSingleCar(final int carType, final boolean isCarDownload) {
        controller = new GlobalMessageController(this);

        Logger.error("showCarDownloadDialogForSingleCar", "_________" + carType);

//        this.carType = carType;
//        Values.carType = carType;
        final Dialog dialog = new DialogController(activity).carDownloadDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        TextView txtViewHeader = dialog.findViewById(R.id.txt_header);
        if (isCarDownload) {
            String downloadConfirmationMsg = NissanApp.getInstance().getAlertMessage(context, lang, Values.DOWNLOAD_CONFIRMATION);
            txtViewTitle.setText(downloadConfirmationMsg == null || downloadConfirmationMsg.isEmpty() ? resources.getString(R.string.alert_msg22) : downloadConfirmationMsg);
            txtViewHeader.setText(activity.getResources().getString(R.string.download));
        } else {
            String deleteMsg = NissanApp.getInstance().getAlertMessage(context, lang, Values.DELETE_MESSAGE);
            txtViewTitle.setText(deleteMsg == null || deleteMsg.isEmpty() ? resources.getString(R.string.alert_msg23) : deleteMsg);
            txtViewHeader.setText(activity.getResources().getString(R.string.delete));
        }

        String okText = NissanApp.getInstance().getGlobalMessage(context).getOk();
        String cancelText = NissanApp.getInstance().getGlobalMessage(context).getCancel();

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        btnOk.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_OK) : okText);
        btnCancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_CANCEL) : cancelText);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();

            // if isCarDownload = true then start car downloading process
            if (isCarDownload) {
                this.carType = carType;
                Values.carType = carType;

                if (dialogModelYearFeature != null && dialogModelYearFeature.isShowing())
                    dialogModelYearFeature.dismiss();

                controller.callApi(NissanApp.getInstance().getDeviceID(context), downloading_lang_id + "");
                startCarDownloadProcedure();

            } else {
                activity.runOnUiThread(() -> {
                    String car_delete_msg = NissanApp.getInstance().getAlertMessage(context, lang, Values.CAR_DELETE);
                    progressDialog = new ProgressDialogController(activity).showDialog(car_delete_msg == null || car_delete_msg.isEmpty() ? resources.getString(R.string.start_car_delete) : car_delete_msg);
                });

                new ApiCall().postCarDelete("" + carType, "" + NissanApp.getInstance().getLanguageIDForSettingsPage(carType + "", commonDao.getLanguageStatus(context, carType)), "0", NissanApp.getInstance().getDeviceID(activity.getApplicationContext()), new CompleteAPI() {
                    @Override
                    public void onDownloaded(ResponseInfo responseInfo) {
                        if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                            try {

                                FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(list.get(position).getId())));
//                                    ((MainActivity) activity).sendMsgToGoogleAnalytics(((MainActivity) activity).getAnalyticsForDelete(carNames[list.get(position).getId() - 1], Analytics.CAR_SELECTION + Analytics.DELETE));

                                int carIdFromList = list.get(position).getId();

                                Logger.error("carIdFromList", "___________" + carIdFromList);

                                if (carIdFromList == 17) {

                                    if (commonDao.getStatus(context, carIdFromList - 3) == 0) {
                                        commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
//                                            list.remove(position);
                                    } else {
                                        commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        list.get(position).setStatus("0");
                                    }
                                }


                                if (carIdFromList == 14) {
                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    if (commonDao.getStatus(context, carIdFromList + 3) == 0) {
                                        commonDao.updateDateAndStatus(context, carIdFromList + 3, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
//                                            list.remove(position);


                                    } else {
                                        commonDao.updateDateAndStatus(context, carIdFromList + 3, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        list.get(position).setStatus("0");
                                    }
                                }

                                /*if (carIdFromList == 15) {

                                    if (commonDao.getStatus(context, carIdFromList - 2) == 0) {
                                        commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
//                                            list.remove(position);
                                    } else {
                                        commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        list.get(position).setStatus("0");
                                    }
                                }


                                if (carIdFromList == 13) {
                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    if (commonDao.getStatus(context, carIdFromList + 2) == 0) {
                                        commonDao.updateDateAndStatus(context, carIdFromList + 2, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
//                                            list.remove(position);


                                    } else {
                                        commonDao.updateDateAndStatus(context, carIdFromList + 2, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        list.get(position).setStatus("0");
                                    }
                                }*/


                                // Added by Raman - 19.10.2021 for 15, 13 and 19 ----Temporary solutions

                                if (carIdFromList == 15) {
                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

                                    if (commonDao.getStatus(context, carIdFromList - 2) == 1 && commonDao.getStatus(context, carIdFromList + 4) == 1) {
                                        list.get(position).setStatus("0");
                                    } else {
                                        list.remove(position);
                                    }
                                }


                                if (carIdFromList == 13) {
                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    if (commonDao.getStatus(context, carIdFromList + 2) == 0) {
                                        commonDao.updateDateAndStatus(context, carIdFromList + 2, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    }

                                    if (commonDao.getStatus(context, carIdFromList + 2) == 1 && commonDao.getStatus(context, carIdFromList + 6) == 1) {
                                        list.get(position).setStatus("0");
                                    } else {
                                        list.remove(position);
                                    }
                                }

                                if (carIdFromList == 19) {
                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                    if (commonDao.getStatus(context, 13) == 1 && commonDao.getStatus(context, 15) == 1)
                                        list.get(position).setStatus("0");
                                    else
                                        list.remove(position);
                                }

                                // Ended -- By Raman

                                if (list.get(position).getId() == 2 || list.get(position).getId() == 5) {
                                    if (commonDao.getStatus(context, list.get(position).getId() - 1) == 2) {
                                        commonDao.updateDateAndStatus(context, list.get(position).getId(), "2", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        list.remove(position);
                                    } else {
                                        commonDao.updateDateAndStatus(context, list.get(position).getId(), "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        list.get(position).setStatus("2");
                                    }
                                } else {

                                    if (list.get(position).getId() == 1 || list.get(position).getId() == 4) {
                                        commonDao.updateDateAndStatus(context, list.get(position).getId(), "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        if (commonDao.getStatus(context, list.get(position).getId() + 1) == 2) {
                                            commonDao.updateDateAndStatus(context, list.get(position).getId() + 1, "2", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            list.remove(position);
                                        } else {
                                            commonDao.updateDateAndStatus(context, list.get(position).getId() + 1, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            list.get(position).setStatus("2");
                                        }
                                    } else {
                                        if (list.get(position).getId() == 7 || list.get(position).getId() == 9) {
                                            commonDao.updateDateAndStatus(context, list.get(position).getId(), "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            list.get(position).setStatus("2");
                                        } else {


                                            if (carIdFromList == 16) {

                                                if (commonDao.getStatus(context, carIdFromList - 4) == 0) {
                                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                    list.remove(position);
                                                } else {
                                                    commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                    list.get(position).setStatus("0");
                                                }
                                            } else if (carIdFromList == 12) {
                                                commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                if (commonDao.getStatus(context, carIdFromList + 4) == 0) {
                                                    commonDao.updateDateAndStatus(context, carIdFromList + 4, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                    list.remove(position);

                                                } else {
                                                    commonDao.updateDateAndStatus(context, carIdFromList + 4, "1", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                    list.get(position).setStatus("0");
                                                }
                                            } else {

                                                commonDao.updateDateAndStatus(context, list.get(position).getId(), "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                list.get(position).setStatus("0");
                                            }

                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {


                                adapterNotify(true);
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                            }
                        } else {
                            if (progressDialog != null)
                                progressDialog.dismiss();
                            frag.showErrorDialog("Status or URL not reachable");
                        }
                    }

                    @Override
                    public void onFailed(String failedReason) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        frag.showErrorDialog(resources.getString(R.string.failed_to_connect_server));
                        Logger.error("Car deleting", "____________" + failedReason);
                    }
                });
            }

        });

        dialog.show();
    }

    private void startCarDownloadProcedure() {

        dataSyncingMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), DATA_SYNCING);
        downloadingMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), DOWNLOADING);

        activity.runOnUiThread(() -> {

            final String startingDownloadMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), STARTING_DOWNLOAD);

            progressDialog = new ProgressDialogController(activity).showDialog(startingDownloadMsg.isEmpty() ? resources.getString(R.string.start_download) : startingDownloadMsg);

        });


        CarListContentController carListContentController = new CarListContentController(this);

        carListContentController.callApi(NissanApp.getInstance().getDeviceID(context), downloading_lang_id + "");

        new ApiCall().postCarDownload("" + carType, "" + downloading_lang_id, "0", NissanApp.getInstance().getDeviceID(context), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                String old_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TUTORIAL_KEY;
                String old_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TAB_MENU_KEY;
                String new_key_tutorial = carType + "_" + downloading_lang_id + "_" + Values.TUTORIAL_KEY;
                String new_key_tab = carType + "_" + downloading_lang_id + "_" + Values.TAB_MENU_KEY;

                preferenceUtil.deleteMultiLangData(old_key_tab);
                preferenceUtil.deleteMultiLangData(old_key_tutorial);
                preferenceUtil.storeMultiLangData(responseInfo.getTutorials(), new_key_tutorial);
                preferenceUtil.storeMultiLangData(responseInfo.getTabMenu(), new_key_tab);

                if (AppConfig.IS_APP_ONLINE ? Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getAssetsUrl()) && !TextUtils.isEmpty(responseInfo.getLangUrl()) : Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? responseInfo.getAssetsUrl() : NissanApp.getInstance().getAssetsURL(carType), Values.PATH, AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL((carType), lang), NissanApp.getInstance().getCarPath(carType), false);
                } else {
                    frag.showErrorDialog("Status OR URL not reachable");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

            }

            @Override
            public void onFailed(String failedReason) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                frag.showErrorDialog(resources.getString(R.string.failed_to_connect_server));
            }
        });

    }

    private void showCarDownloadDialog(final int carType) {
        final Dialog dialog = new DialogController(activity).carDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);

        String downloadCarGuide2Msg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_2);

        txtViewTitle.setText(downloadCarGuide2Msg == null || downloadCarGuide2Msg.isEmpty() ? resources.getString(R.string.download_msg) : downloadCarGuide2Msg.replace("\\n", "\n"));

        ImageButton btnEUR = dialog.findViewById(R.id.btn_eur);
        ImageButton btnRUS = dialog.findViewById(R.id.btn_rus);

        ImageButton imgBtnEur = dialog.findViewById(R.id.img_btn_eur_delete_donwload);
        ImageButton imgBtnRus = dialog.findViewById(R.id.img_btn_rus_delete_donwload);

        if (carType == 1 || carType == 4) {
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
                imgBtnEur.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 1).getSelectedCar() == 1) {
                    imgBtnEur.setAlpha(0.2f);
                    imgBtnEur.setEnabled(false);
                } else {
                    imgBtnEur.setEnabled(true);
                }
            } else {
                imgBtnEur.setBackgroundResource(R.drawable.download_selector);
            }

            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 1)) && commonDao.getStatus(context, carType + 1) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }
        } else if (carType == 2 || carType == 5) {
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 1)) && commonDao.getStatus(context, carType - 1) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
                imgBtnEur.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 2).getSelectedCar() == 1) {
                    imgBtnEur.setAlpha(0.2f);
                    imgBtnEur.setEnabled(false);
                } else {
                    imgBtnEur.setEnabled(true);
                }
            } else {
                imgBtnEur.setBackgroundResource(R.drawable.download_selector);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 1).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }
        } else if (carType == 13) {//click rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1
                    && commonDao.getStatus(context, carType) == 1 && commonDao.getStatus(context, 19) == 1
            ) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
                imgBtnEur.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 1).getSelectedCar() == 1) {
                    imgBtnEur.setAlpha(0.2f);
                    imgBtnEur.setEnabled(false);
                } else {
                    imgBtnEur.setEnabled(true);
                }
            } else {
                imgBtnEur.setBackgroundResource(R.drawable.download_selector);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 2)) && commonDao.getStatus(context, carType + 2) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 1).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }//click rohan
        } else if (carType == 15) { //click rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 2)) && commonDao.getStatus(context, carType - 2) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
                imgBtnEur.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 3).getSelectedCar() == 1) {
                    imgBtnEur.setAlpha(0.2f);
                    imgBtnEur.setEnabled(false);
                } else {
                    imgBtnEur.setEnabled(true);
                }
            } else {
                imgBtnEur.setBackgroundResource(R.drawable.download_selector);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 2).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }//click rohan
        } else if (carType == 12) {//click rohan

            Logger.error("carType", "_____" + carType);

            for (CarInfo info : list) {
                Logger.error("car_name_id_index", "_____" + info.getName() + "_____" + info.getId() + "______" + info.getIndex());
            }

            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
                imgBtnEur.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 1).getSelectedCar() == 1) {
                    imgBtnEur.setAlpha(0.2f);
                    imgBtnEur.setEnabled(false);
                } else {
                    imgBtnEur.setEnabled(true);
                }
            } else {
                imgBtnEur.setBackgroundResource(R.drawable.download_selector);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 4)) && commonDao.getStatus(context, carType + 4) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 1).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }//click rohan
        } else if (carType == 16) { //click rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 4)) && commonDao.getStatus(context, carType - 4) == 1) {
                btnEUR.setAlpha(0.2f);
                btnEUR.setEnabled(false);
                imgBtnEur.setBackgroundResource(R.drawable.delete_selector);

                Logger.error("carType", "_____" + carType);

                for (CarInfo info : list) {
                    Logger.error("car_name_id_index", "_____" + info.getName() + "_____" + info.getId() + "______" + info.getIndex());
                }

                if (list.get(carType - 4).getSelectedCar() == 1) {
                    imgBtnEur.setAlpha(0.2f);
                    imgBtnEur.setEnabled(false);
                } else {
                    imgBtnEur.setEnabled(true);
                }
            } else {
                imgBtnEur.setBackgroundResource(R.drawable.download_selector);
            }
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType - 2).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }//click rohan
        }

        btnEUR.setOnClickListener(v -> {
            dialog.dismiss();
            if (carType == 1 || carType == 4 || carType == 13 || carType == 12) {//click rohan
                selectedCarType = carType;
                if (carType == 13) {
                    modelYearFeatureDialog(10);
                } else
                    LanguageSelectionDialog(carType);
            } else {
                selectedCarType = carType - 1;
                LanguageSelectionDialog(carType - 1);
            }
        });

        btnRUS.setOnClickListener(v -> {
            dialog.dismiss();

            if (carType == 2 || carType == 5) {
                selectedCarType = carType;
                LanguageSelectionDialog(carType);
            } else if (carType == 13) {//click rohan
                selectedCarType = carType + 2;
                LanguageSelectionDialog(carType + 2);//click rohan
            } else if (carType == 15) {//click rohan
                selectedCarType = carType;
                LanguageSelectionDialog(carType);//click rohan
            } else if (carType == 12) {//click rohan
                selectedCarType = carType + 4;
                LanguageSelectionDialog(carType + 4);//click rohan
            } else if (carType == 16) {//click rohan
                selectedCarType = carType;
                LanguageSelectionDialog(carType);//click rohan
            } else {
                selectedCarType = carType + 1;
                LanguageSelectionDialog(carType + 1);
            }
        });


        imgBtnEur.setOnClickListener(v -> {
            dialog.dismiss();
            if (carType == 1 || carType == 4 || carType == 13 || carType == 12) {
                if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                    position++;
                    showCarDownloadDialogForSingleCar(carType, false);
                } else {
                    selectedCarType = carType;
                    LanguageSelectionDialog(carType);
                }
            } else {
                if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 1)) && commonDao.getStatus(context, carType - 1) == 1) {
                    position--;
                    showCarDownloadDialogForSingleCar(carType - 1, false);
                } else {
                    selectedCarType = carType - 1;
                    LanguageSelectionDialog(carType - 1);
                }
            }
        });

        imgBtnRus.setOnClickListener(v -> {
            dialog.dismiss();
            if (carType == 2 || carType == 5) {
                if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                    position--;
                    showCarDownloadDialogForSingleCar(carType, false);
                } else {
                    selectedCarType = carType;
                    LanguageSelectionDialog(carType);
                }
            } else if (carType == 13) {//click rohan
                if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 2)) && commonDao.getStatus(context, carType + 2) == 1) {
                    position--;
                    showCarDownloadDialogForSingleCar(carType + 2, false);//click rohan
                } else {
                    selectedCarType = carType + 2;//click rohan
                    LanguageSelectionDialog(carType + 2);//click rohan
                }
            } else if (carType == 12) {//click rohan
                if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 4)) && commonDao.getStatus(context, carType + 4) == 1) {
                    position--;
                    showCarDownloadDialogForSingleCar(carType + 4, false);//click rohan
                } else {
                    selectedCarType = carType + 4;//click rohan
                    LanguageSelectionDialog(carType + 4);//click rohan
                }
            } else {
                if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 1)) && commonDao.getStatus(context, carType + 1) == 1) {
                    position++;
                    showCarDownloadDialogForSingleCar(carType + 1, false);
                } else {
                    selectedCarType = carType + 1;
                    LanguageSelectionDialog(carType + 1);
                }
            }
        });

        dialog.show();
    }

    private void LanguageSelectionDialog(final int carType) {

        Logger.error("carType", "_________" + carType);

        getCarList(carType + "");

    }

    private void showLanguageDialog(final int carTypeLocal) {

        if (languageLists == null || languageLists.size() == 0)
            languageLists = new ArrayList<>();
        ArrayList<LanguageInfo> languageList = new ArrayList<>();

        dialog = new DialogController(activity).languageSelectionDialog();
        ListView lstView = dialog.findViewById(R.id.lst_view);

        TextView tvSelectLanguageTitle = dialog.findViewById(R.id.tvSelectLanguageTitle);
        String selectLangTitle = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.SELECT_LANGUAGE);
        tvSelectLanguageTitle.setText(selectLangTitle == null || selectLangTitle.isEmpty() ? resources.getString(R.string.select_language) : selectLangTitle);

        lstView.setOnItemClickListener((parent, view, position, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }

            Logger.error("carType_lstView_click", "_________" + carTypeLocal);

            LanguageInfo info = (LanguageInfo) parent.getAdapter().getItem(position);
            lang = getLanguageShortName(info.getName()); //languageShortName[info.getId()];
            downloading_lang_id = getLanguageID(info.getName()); //languageShortName[info.getId()];
            Logger.error("on language selection", "_________" + lang + " id" + downloading_lang_id);
            showCarDownloadDialogForSingleCar(carTypeLocal, true);
        });

        String deviceDensity = NissanApp.getInstance().getDensityName(context);

        String[] langFlagUri = new String[languageLists.size()];

        for (int i = 0; i < languageLists.size(); i++) {

            if ("xxxhdpi".contains(deviceDensity)) {
                langFlagUri[i] = languageLists.get(i).getLanguageFlag().getXxxhdpi();
            } else if ("xxhdpi".contains(deviceDensity)) {
                langFlagUri[i] = languageLists.get(i).getLanguageFlag().getXxhdpi();
            } else if ("xhdpi".contains(deviceDensity)) {
                langFlagUri[i] = languageLists.get(i).getLanguageFlag().getXhdpi();
            } else if ("hdpi".contains(deviceDensity)) {
                langFlagUri[i] = languageLists.get(i).getLanguageFlag().getHdpi();
            } else if ("mdpi".contains(deviceDensity)) {
                langFlagUri[i] = languageLists.get(i).getLanguageFlag().getHdpi();
            } else if ("ldpi".contains(deviceDensity)) {
                langFlagUri[i] = languageLists.get(i).getLanguageFlag().getLdpi();
            }

            LanguageInfo info = new LanguageInfo(i, languageLists.get(i).getLanguageName(), false, langFlagUri[i]);
            languageList.add(info);
        }

        lstView.setAdapter(new LanguageSelectionAdapter(context, languageList, false));
        lstView.setDivider(null);
        ColorDrawable sage = new ColorDrawable(context.getResources().getColor(R.color.line_color));
        lstView.setDivider(sage);
        lstView.setDividerHeight(4);

        dialog.show();
    }

    private String getLanguageShortName(String name) {

        for (int i = 0; i < languageLists.size(); i++) {

            if (name.equalsIgnoreCase(languageLists.get(i).getLanguageName()))
                return languageLists.get(i).getLanguageShortcode();
        }
        return "";
    }

    private int getLanguageID(String name) {

        for (int i = 0; i < languageLists.size(); i++) {

            if (name.equalsIgnoreCase(languageLists.get(i).getLanguageName()))
                return languageLists.get(i).getLanguageId();

        }

        return 0;
    }

    private void errorFileDelete(int carType) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carType)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            adapterNotify(true);
        }
    }

    public void loadResource() {
        resources = new Resources(activity.getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(activity, preferenceUtil.getSelectedLang()));
        internetCheckMessage = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
       /* boolean leaf17IsDownloaded =false;
        for (int i = 0; i < list.size(); i++) {
            CarInfo info = list.get(i);
            if (info.getId() == 14 && info.getStatus().equals(Values.ALREADY_DOWNLOADED)){
                leaf17IsDownloaded =true;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            CarInfo info = list.get(i);
            if (info.getId() == 17 && info.getStatus().equals(Values.AVAILABLE_FOR_DOWNLOAD) && !leaf17IsDownloaded )
                list.remove(info);


        }*/
    }

    private void adapterNotify(boolean isDelete) {

//        NissanApp.getInstance().setCarAllList(list); //commonDao.getAllCarList(context)); //list); //commonDao.getAllCarList(context));
        replaceTheCarNamesAndImages();
/*
        list = new ArrayList<>();
        list = NissanApp.getInstance().getCarAllList();
*/

        boolean euroAvailable = false, leaf2017Available = false;

        for (int i = 0; i < list.size(); i++) {
            CarInfo info = list.get(i);
            if (info.getId() == 13 && info.getStatus().equals(Values.AVAILABLE_FOR_DOWNLOAD)) {
                euroAvailable = true;
            }

            if (info.getId() == 14 && info.getStatus().equals(Values.AVAILABLE_FOR_DOWNLOAD)) {
                leaf2017Available = true;
            }

        }

        for (int i = 0; i < list.size(); i++) {
            CarInfo info = list.get(i);
            Logger.error("car_name_id_status_id_region", "_____" + info.getName() + "____" + info.getId() + "____" + info.getStatus() + "___" + info.getRegion());
            if (euroAvailable) {
                if (info.getName().contains("RUS") && info.getId() == 15 && info.getStatus().equals(Values.AVAILABLE_FOR_DOWNLOAD))
                    list.remove(info);
            }

            if (leaf2017Available) {
                if (info.getName().contains("2019") && info.getId() == 17 && info.getStatus().equals(Values.AVAILABLE_FOR_DOWNLOAD))
                    list.remove(info);
            }
//            if (info.getId() == 19 && info.getStatus().equals(Values.AVAILABLE_FOR_DOWNLOAD))
//                list.remove(info);

        }

        boolean isDownloaded = true;
        boolean isAvailable = true;
        boolean isPrevious = true;

/*
        Collections.sort(list, new Comparator<CarInfo>() {
            @Override
            public int compare(CarInfo o1, CarInfo o2) {
                return o1.getId() - o2.getId();
            }
        });
*/


        Collections.sort(list, (o1, o2) -> {
            if (o1.getId() >= 11) {
                return o2.getId() - o1.getId();
            } else {
                return 0;
            }
        });

        Collections.sort(list, (ideaVal1, ideaVal2) -> {
            // avoiding NullPointerException in case name is null
            Long idea1 = (long) ideaVal1.getIndex();
            Long idea2 = (long) ideaVal2.getIndex();
            return idea2.compareTo(idea1);
        });

        final String ORDER = "102";
        Collections.sort(list, (o1, o2) -> ORDER.indexOf(o1.getStatus()) - ORDER.indexOf(o2.getStatus()));

/*
        CarInfo xtrailInfo = new CarInfo();
        CarInfo leafInfo = new CarInfo();
        int xtrailIndex = -1, leafIndex = -1;

        if (xtrailLeafAvailableForDownload(list)) {
            for (int k = 0; k < list.size(); k++) {
                CarInfo info = list.get(k);

                if (info.getId() == 15) {
                    xtrailInfo = info;
                    xtrailIndex = k;
                }

                if (info.getId() == 14) {
                    leafInfo = info;
                    leafIndex = k;
                }


            }


            list.set(xtrailIndex, leafInfo);
            list.set(leafIndex, xtrailInfo);

        }
*/


/*
        list = swapXtrailRusleaf2017IfBothDownloaded(list);
        list = swapXtrailEurRusIfBothDownloaded(list);
*/

        int index = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus().equalsIgnoreCase("1")) {
                if (isDownloaded) {
                    isDownloaded = false;
                    list.get(i).setSection(true);
                } else {
                    list.get(i).setSection(false);
                }
            } else if (list.get(i).getStatus().equalsIgnoreCase("0")) {
                if (isAvailable) {
                    isAvailable = false;
                    list.get(i).setSection(true);
                } else {
                    list.get(i).setSection(false);
                }

            } else if (list.get(i).getStatus().equalsIgnoreCase("2")) {
                if (isPrevious) {
                    isPrevious = false;
                    list.get(i).setSection(true);
                } else {
                    list.get(i).setSection(false);
                }

            }
        }

        CarDownloadSettingsAdapter.this.notifyDataSetChanged();
        if (!isDelete) {
            frag.lstView.smoothScrollToPosition(index);
        } else {
            frag.lstView.smoothScrollBy(2, 10);
        }
    }

    private void replaceTheCarNamesAndImages() {
        ArrayList<CarInfo> carInfoArrayList = NissanApp.getInstance().getCarAllList();

        if (carInfoArrayList == null || carInfoArrayList.size() == 0)
            return;
        if (carListArrayList == null || carListArrayList.size() == 0)
            return;


        for (int i = 0; i < carInfoArrayList.size(); i++) {

            for (CarList carListModel : carListArrayList) {
                if (carListModel.getId() == null || carListModel.getId().isEmpty())
                    continue;
                if (carInfoArrayList.get(i).getId() == Integer.parseInt(carListModel.getId())) {
                    carInfoArrayList.get(i).setName(carListModel.getCarDisplayName());
                    carInfoArrayList.get(i).setCarImg(NissanApp.getInstance().getURLAccordingToDensity(context, NissanApp.getInstance().getDensityName(context), carListModel));
                }
            }

        }

        NissanApp.getInstance().setCarAllList(carInfoArrayList);
    }

    private void getDataFromSP() {
        String car_list_key = preferenceUtil.getSelectedLang() + "_" + Values.CAR_LIST_KEY + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang());

        Type type = new TypeToken<ArrayList<CarList>>() {
        }.getType();

        carListArrayList = new Gson().fromJson(preferenceUtil.retrieveMultiLangData(car_list_key), type);

        car_list_key = "en_" + Values.CAR_LIST_KEY + "_1";

        ArrayList<CarList> carListArrayListEng = new Gson().fromJson(preferenceUtil.retrieveMultiLangData(car_list_key), type);

        if (carListArrayListEng != null && carListArrayListEng.size() > 0) {
            if (carListArrayList != null && carListArrayList.size() > 0) {
                for (int i = 0; i < carListArrayListEng.size(); i++) {

                    for (int j = 0; j < carListArrayList.size(); j++) {
                        if (carListArrayListEng.get(i).getCarUniqueName().equals(carListArrayList.get(j).getCarUniqueName())) {
                            carListArrayList.get(j).setImgHdpi(carListArrayListEng.get(i).getImgHdpi());
                            carListArrayList.get(j).setImgLdpi(carListArrayListEng.get(i).getImgLdpi());
                            carListArrayList.get(j).setImgXhdpi(carListArrayListEng.get(i).getImgXhdpi());
                            carListArrayList.get(j).setImgXXhdpi(carListArrayListEng.get(i).getImgXXhdpi());
                            carListArrayList.get(j).setImgXXXhdpi(carListArrayListEng.get(i).getImgXXXhdpi());
                        }

                    }

                }
            }
        }
    }

    public void setList(ArrayList<CarInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        final SimpleDraweeView imageView;
        final ImageView imageViewBorder;
        final TextView txtViewTitle;
        final ImageButton imgDeleteOrDownload;
        final RelativeLayout relativeLayoutSection;
        final RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            imageView = view.findViewById(R.id.ivMainCarImage);
            imageViewBorder = view.findViewById(R.id.img_view_border);
            txtViewTitle = view.findViewById(R.id.txt_title);
            imgDeleteOrDownload = view.findViewById(R.id.img_btn_delete_or_download);
            relativeLayoutSection = view.findViewById(R.id.relative_section);
            relativeLayout = view.findViewById(R.id.relative_car_download);
        }
    }
}
