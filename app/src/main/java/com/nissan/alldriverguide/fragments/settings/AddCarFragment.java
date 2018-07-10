package com.nissan.alldriverguide.fragments.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.CarDownloadSettingsAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddCarFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private View view;
    public ListView lstView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private TextView txt_title;
    private TextView txt_back_title;

    private CarDownloadSettingsAdapter adapter;

    private PreferenceUtil preferenceUtil;
    private Configuration conf;
    private Resources resources;
    private DisplayMetrics metrics;
    private CommonDao commonDao;
    private Typeface tf;
    private int selectedCarIndex = 0;

    private boolean isDownloaded = true;
    private boolean isAvailable = true;
    private boolean isPrevious = true;

    private long doubleClickPopup = 0;
    private List<CarList> carListArrayList = new ArrayList<>();

    public static Fragment newInstance() {
        Fragment frag = new AddCarFragment();
        return frag;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_car, container, false);

        initViews(view);
        loadResource();
        setListener();
        loadData();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    private void loadData() {

        getDataFromSP();

        final String ORDER = "102";
        Collections.sort(NissanApp.getInstance().getCarAllList(), new Comparator<CarInfo>() {
            @Override
            public int compare(CarInfo o1, CarInfo o2) {
                return ORDER.indexOf(o1.getStatus()) - ORDER.indexOf(o2.getStatus());
            }
        });

        for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
            if (NissanApp.getInstance().getCarAllList().get(i).getStatus().equalsIgnoreCase("1")) {
                if (isDownloaded) {
                    isDownloaded = false;
                    NissanApp.getInstance().getCarAllList().get(i).setSection(true);
                } else {
                    NissanApp.getInstance().getCarAllList().get(i).setSection(false);
                }
            } else if (NissanApp.getInstance().getCarAllList().get(i).getStatus().equalsIgnoreCase("0")) {
                if (isAvailable) {
                    isAvailable = false;
                    NissanApp.getInstance().getCarAllList().get(i).setSection(true);
                } else {
                    NissanApp.getInstance().getCarAllList().get(i).setSection(false);
                }

            } else if (NissanApp.getInstance().getCarAllList().get(i).getStatus().equalsIgnoreCase("2")) {
                if (isPrevious) {
                    isPrevious = false;
                    NissanApp.getInstance().getCarAllList().get(i).setSection(true);
                } else {
                    NissanApp.getInstance().getCarAllList().get(i).setSection(false);
                }

            } else {

            }
        }

        replaceTheCarNamesAndImages();

        adapter = new CarDownloadSettingsAdapter(AddCarFragment.this, getActivity(), getActivity().getApplicationContext(), NissanApp.getInstance().getCarAllList());
        lstView.setAdapter(adapter);
        lstView.setDivider(null);
    }

    private void replaceTheCarNamesAndImages() {

        ArrayList<CarInfo> carInfoArrayList = NissanApp.getInstance().getCarAllList();

        if (carInfoArrayList == null || carInfoArrayList.size() == 0)
            return;
        if (carListArrayList == null || carListArrayList.size() == 0)
            return;


/*
        for (CarInfo car : carInfoArrayList) {
            Logger.error("id_name", "__________" + car.getId() +"_____" + car.getName());
        }
*/

        for (int i = 0; i < carInfoArrayList.size(); i++) {

            for (CarList carListModel : carListArrayList) {
                if (carListModel.getId() == null || carListModel.getId().isEmpty())
                    continue;
                if (carInfoArrayList.get(i).getId() == Integer.parseInt(carListModel.getId())) {
                    carInfoArrayList.get(i).setName(carListModel.getCarDisplayName());
                    carInfoArrayList.get(i).setCarImg(NissanApp.getInstance().getURLAccordingToDensity(getActivity(), NissanApp.getInstance().getDensityName(getActivity()), carListModel));
                }
            }

        }


/*
        for (CarList carListModel : carListArrayList) {
            for (int i = 0; i < carInfoArrayList.size(); i++) {
                if (carListModel.getId() == null || carListModel.getId().isEmpty())
                    continue;
                if (carInfoArrayList.get(i).getId() == Integer.parseInt(carListModel.getId())) {
                    carInfoArrayList.get(i).setName(carListModel.getCarDisplayName());
                    carInfoArrayList.get(i).setCarImg(NissanApp.getInstance().getURLAccordingToDensity(NissanApp.getInstance().getDensityName(getActivity()), carListModel));
                }
            }
        }
*/

        NissanApp.getInstance().setCarAllList(carInfoArrayList);

    }


    private void getDataFromSP() {

        String car_list_key = preferenceUtil.getSelectedLang() + "_" + Values.CAR_LIST_KEY + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang());

        Logger.error("AddCarFragment", "car_list_key__________" + car_list_key);

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

    private void setListener() {
        lstView.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    public void loadResource() {
        ((MainActivity) getActivity()).setTabResources();
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
        String car_selection_title = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.CAR_SELECTION_TITLE);
        txt_title.setText(car_selection_title.isEmpty() ? resources.getString(R.string.add_extra_car) : car_selection_title);
        txt_back_title.setText(resources.getString(R.string.back));
        txt_back_title.setTypeface(tf);
        getDataFromSP();
        replaceTheCarNamesAndImages();
    }

    private void initViews(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        lstView = (ListView) view.findViewById(R.id.lst_view);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_back_title = (TextView) view.findViewById(R.id.txt_back_title);
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        commonDao = CommonDao.getInstance();
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Nissan Brand Regular.otf"); //initialize typeface here.
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedCarIndex = position;
        CarInfo info = (CarInfo) parent.getAdapter().getItem(position);
        if ("1".equalsIgnoreCase(info.getStatus())) {
            carDownloadCheck(info.getId());
            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsForCarSection(Analytics.CAR_SELECTION));
        } else {
            carDownloadCheck(info.getId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                ((MainActivity) getActivity()).onBackPressed();
                break;
            default:
                break;
        }
    }

    private void carDownloadCheck(final int position) {
        Logger.error("Path is " + commonDao.getStatus(getActivity().getBaseContext(), position), "_____________" + NissanApp.getInstance().getCarPath(position));
        if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(position))) {
            if (commonDao.getStatus(getActivity().getBaseContext(), position) == 1) {
                CarInfo info = commonDao.getCarInfo(getActivity().getApplicationContext(), position);
                if (info != null) {
                    if (!NissanApp.getInstance().getVersionName().equalsIgnoreCase(info.getVersionName()) || info.getVersionCode() != NissanApp.getInstance().getVersionCode()) {
                        if (DetectConnection.checkInternetConnection(getActivity().getApplicationContext())) {
                            try {
                                FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(position)));
                                if (position == 2 || position == 5) {
                                    if (commonDao.getStatus(getActivity().getApplicationContext(), position - 1) == 2) {
                                        commonDao.updateDateAndStatus(getActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
//                                        adapter.list.remove(selectedCarIndex);
                                        adapter.list.get(selectedCarIndex).setStatus("2");
                                    } else {
                                        commonDao.updateDateAndStatus(getActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        adapter.list.get(selectedCarIndex).setStatus("2");
                                    }

                                } else {
                                    if (position == 1 || position == 4) {
                                        CarInfo carInfo = commonDao.getCarInfo(getActivity().getApplicationContext(), position + 1);

                                        commonDao.updateDateAndStatus(getActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        if (commonDao.getStatus(getActivity().getApplicationContext(), position + 1) == 2) {
                                            commonDao.updateDateAndStatus(getActivity().getApplicationContext(), position + 1, "2", NissanApp.getInstance().getDateTime(), "RUS", carInfo.getVersionName(), carInfo.getVersionCode());
//                                            adapter.list.remove(selectedCarIndex);
                                            adapter.list.get(selectedCarIndex).setStatus("2");
                                        } else {
                                            commonDao.updateDateAndStatus(getActivity().getApplicationContext(), position + 1, "1", NissanApp.getInstance().getDateTime(), "EUR", carInfo.getVersionName(), carInfo.getVersionCode());
                                            adapter.list.get(selectedCarIndex).setStatus("2");
                                        }
                                    } else {
                                        if (position == 7 || position == 9) {
                                            commonDao.updateDateAndStatus(getActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            adapter.list.get(selectedCarIndex).setStatus("2");
                                        } else {
                                            commonDao.updateDateAndStatus(getActivity().getApplicationContext(), position, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            adapter.list.get(selectedCarIndex).setStatus("0");
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                adapter.carType = position;
                                adapter.position = selectedCarIndex;

                                adapter.lang = commonDao.getLanguageStatus(getActivity().getApplicationContext(), position);
                                preferenceUtil.setSelectedLang(adapter.lang);
                                resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), adapter.lang));
                                startCarDownloadProcedure(position);
                            }
                        } else {
                            Values.carType = position;

                            for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
                                if (NissanApp.getInstance().getCarAllList().get(i).getId() == position) {
                                    NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(1);
                                } else {
                                    NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(0);
                                }
                            }
                            Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);
                            preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType));
                            loadResource();
                            ((MainActivity) getActivity()).setTabResources();
                            adapter.loadResource();
                            getDataFromSP();
                            replaceTheCarNamesAndImages();
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        final ArrayList<PushContentInfo> list = commonDao.getNotificationList(getActivity().getApplicationContext(), position, NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(getActivity().getApplicationContext(), position)));

                        if(DetectConnection.checkInternetConnection(getActivity().getApplicationContext())) {
                            if (list != null && list.size() > 0) {
                                Logger.error("You have a update ", "Do you want ???");

                                final StringBuilder stringBuilder = new StringBuilder();

                                for (PushContentInfo pushContentInfo : list) {
                                    stringBuilder.append(pushContentInfo.getePubId() + ",");
                                }

                                stringBuilder.setLength(stringBuilder.length() - 1);

                                final Dialog dialog = new DialogController(getActivity()).contentUpdateDialog();

                                TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
                                txtViewTitle.setText(resources.getString(R.string.update_msg));
//TODO
                                String okText = NissanApp.getInstance().getGlobalMessage(getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.YES);
                                String cancelText = NissanApp.getInstance().getGlobalMessage(getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.NO);

                                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
//                                btnOk.setText(resources.getString(R.string.button_YES));
                                btnOk.setText(okText.isEmpty() ? resources.getString(R.string.button_YES) : okText);

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (SystemClock.elapsedRealtime() - doubleClickPopup < 1000) {
                                            return;
                                        }
                                        doubleClickPopup = SystemClock.elapsedRealtime();

                                        dialog.dismiss();

                                        if (DetectConnection.checkInternetConnection(getActivity().getApplicationContext())) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.progressDialog = new ProgressDialogController(getActivity()).showDialog(getResources().getString(R.string.start_download));
                                                }
                                            });

                                            new ApiCall().postContentDownload("" + position, "" + NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(getActivity().getApplicationContext(), position)), stringBuilder.toString(), NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
                                                @Override
                                                public void onDownloaded(ResponseInfo responseInfo) {
                                                    if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getUrl())) {
                                                        new MADownloadManager(getActivity(), getActivity().getApplicationContext()).downloadLanguage(false, "Language", responseInfo.getUrl(), NissanApp.getInstance().getCarPath(position), new DownloaderStatus() {
                                                            @Override
                                                            public boolean onComplete(boolean b) {
                                                                if (b) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            if (adapter.progressDialog != null) {
                                                                                adapter.progressDialog.setMessage(getResources().getString(R.string.data_syncing));
                                                                            }

                                                                            new SingleContentUpdating(getActivity(), commonDao.getLanguageStatus(getActivity().getApplicationContext(), position), position) {
                                                                                @Override
                                                                                public void onComplete(boolean status) {
                                                                                    Logger.error("Status code Data Syncing", "_________________"+status);
                                                                                    if (status) {

                                                                                        new ApiCall().postContentDownloadConfirmation("" + position, "" + NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(getActivity().getApplicationContext(), position)), "" + stringBuilder.toString(), NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
                                                                                            @Override
                                                                                            public void onDownloaded(ResponseInfo responseInfo) {
                                                                                                Logger.error("Status code ", "_________________"+responseInfo.getStatusCode());
                                                                                                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                                                                                                    try {
//                                                                                                    FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + commonDao.getLanguageStatus(getActivity().getApplicationContext(), Values.carType)));

//                                                                                                    ((MainActivity) getApplicationContext()).sendMsgToGoogleAnalytics(((MainActivity) getApplicationContext()).getAnalyticsFromSettings(Analytics.CHANGE_LANGUAGE + Analytics.DOWNLOAD));
                                                                                                        commonDao.updateLanguageStatus(getActivity().getApplicationContext(), position, commonDao.getLanguageStatus(getActivity().getApplicationContext(), position));
                                                                                                        commonDao.deleteSingleCarEpub(getActivity().getApplicationContext(), position);

                                                                                                        for (PushContentInfo pushContentInfo : list) {
                                                                                                            commonDao.updatePushContentStatus(getActivity().getApplicationContext(), Integer.parseInt(pushContentInfo.getCarId()), Integer.parseInt(pushContentInfo.getLangId()), Integer.parseInt(pushContentInfo.getePubId()));
                                                                                                        }

                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                    } finally {
                                                                                                        dismissDialog();

                                                                                                        for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
                                                                                                            if (NissanApp.getInstance().getCarAllList().get(i).getId() == position) {
                                                                                                                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(1);
                                                                                                            } else {
                                                                                                                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(0);
                                                                                                            }
                                                                                                        }
                                                                                                        Values.carType = position;
                                                                                                        Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);
                                                                                                        preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType));
                                                                                                        loadResource();
                                                                                                        ((MainActivity) getActivity()).setTabResources();
                                                                                                        adapter.loadResource();
                                                                                                        getDataFromSP();
                                                                                                        replaceTheCarNamesAndImages();
                                                                                                        adapter.notifyDataSetChanged();
                                                                                                    }

                                                                                                } else {
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

                                                                        }
                                                                    });

                                                                } else {
                                                                    Logger.error("problem", "______assetDownload-LanguageFragment");
                                                                }
                                                                return false;
                                                            }

                                                            @Override
                                                            public int onError(int i) {
                                                                dismissDialog();
                                                                showErrorDialog("Error ! Unable to update content, Please try again.");
                                                                return 0;
                                                            }

                                                            @Override
                                                            public boolean internetConnection(boolean b) {
                                                                dismissDialog();
                                                                showErrorDialog("No internet connection, please try again");
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
                                                                if (adapter.progressDialog != null) {
                                                                    adapter.progressDialog.setMessage(getActivity().getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete) + formattedString + "%");
                                                                }
                                                            }

                                                            @Override
                                                            public void init() {
                                                                if (adapter.progressDialog == null) {
                                                                    adapter.progressDialog = new ProgressDialogController(getActivity()).downloadProgress(getActivity().getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        dismissDialog();
                                                        showErrorDialog("Status code or URL not reachable");
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
//                                btnCancel.setText(resources.getString(R.string.button_NO));
                                btnCancel.setText(cancelText.isEmpty() ? resources.getString(R.string.button_NO) : cancelText);

                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (SystemClock.elapsedRealtime() - doubleClickPopup < 1000) {
                                            return;
                                        }
                                        doubleClickPopup = SystemClock.elapsedRealtime();

                                        dialog.dismiss();

                                        for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
                                            if (NissanApp.getInstance().getCarAllList().get(i).getId() == position) {
                                                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(1);
                                            } else {
                                                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(0);
                                            }
                                        }
                                        Values.carType = position;
                                        Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);
                                        preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType));
                                        loadResource();
                                        ((MainActivity) getActivity()).setTabResources();
                                        adapter.loadResource();
                                        getDataFromSP();
                                        replaceTheCarNamesAndImages();
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                dialog.show();
                            } else {
                                for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
                                    if (NissanApp.getInstance().getCarAllList().get(i).getId() == position) {
                                        NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(1);
                                    } else {
                                        NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(0);
                                    }
                                }
                                Values.carType = position;
                                Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);
                                preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType));
                                loadResource();
                                ((MainActivity) getActivity()).setTabResources();
                                adapter.loadResource();
                                getDataFromSP();
                                replaceTheCarNamesAndImages();
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
                                if (NissanApp.getInstance().getCarAllList().get(i).getId() == position) {
                                    NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(1);
                                } else {
                                    NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(0);
                                }
                            }
                            Values.carType = position;
                            Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);
                            preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType));
                            loadResource();
                            ((MainActivity) getActivity()).setTabResources();
                            adapter.loadResource();
                            getDataFromSP();
                            replaceTheCarNamesAndImages();
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {

                }

            } else {
                try {
                    FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(Values.carType)));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                    NissanApp.getInstance().showInternetAlert(getActivity(), getResources().getString(R.string.car_msg_download));
                    String carDownloadGuideMsg = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_1);
                    NissanApp.getInstance().showInternetAlert(getActivity(), carDownloadGuideMsg.isEmpty() ? getResources().getString(R.string.car_msg_download) : carDownloadGuideMsg);
                }
            }
        } else {
//            NissanApp.getInstance().showInternetAlert(getActivity(), getResources().getString(R.string.car_msg_download));
            String carDownloadGuideMsg = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_1);
            NissanApp.getInstance().showInternetAlert(getActivity(), carDownloadGuideMsg.isEmpty() ? getResources().getString(R.string.car_msg_download) : carDownloadGuideMsg);
        }
    }

    private void startCarDownloadProcedure(final int position) {
        adapter.progressDialog = new ProgressDialogController(getActivity()).showDialog(getResources().getString(R.string.start_download));

        Logger.error("cardownloadactivity_code", "__________" + "daat");

        new ApiCall().postCarDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(adapter.lang), "0", NissanApp.getInstance().getDeviceID(getActivity()), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {
                Logger.error("cardownloadactivity_code", "__________" + responseInfo.getStatusCode());

                if (AppConfig.IS_APP_ONLINE ? Values.SUCCESS_STATUS.equals(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getAssetsUrl()) && !TextUtils.isEmpty(responseInfo.getLangUrl()) : Values.SUCCESS_STATUS.equals(responseInfo.getStatusCode())) {
                    adapter.startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? responseInfo.getAssetsUrl() : NissanApp.getInstance().getAssetsURL(position), Values.PATH, AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL((position), adapter.lang), NissanApp.getInstance().getCarPath(Values.carType), true);
                } else {
                    dismissDialog();
                    showErrorDialog("Status OR URL not reachable");
                    Logger.error("cardownloadactivity_problem", "__________" + "postcardownload");
                }
            }

            @Override
            public void onFailed(String failedReason) {
                dismissDialog();
                showErrorDialog(failedReason);
                Logger.error("cardownloadactivity_failed", "__________" + "postcardownload->" + failedReason);
            }
        });

    }

    private void dismissDialog() {
        if (adapter.progressDialog != null && adapter.progressDialog.isShowing()) {
            adapter.progressDialog.dismiss();
        }
    }

    public void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(getActivity().getApplicationContext(), msg);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "error_fragment");
    }

}
