package com.nissan.alldriverguide.fragments.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nissan.alldriverguide.CarDownloadHelper;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.CarDownloadSettingsAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.AppConfig;
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;

public class AddCarFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    public ListView lstView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private TextView txt_title;
    private TextView txt_back_title;

    private CarDownloadSettingsAdapter adapter;

    private PreferenceUtil preferenceUtil;
    private Resources resources;
    private CommonDao commonDao;
    private int selectedCarIndex = 0;

    private boolean isDownloaded = true;
    private boolean isAvailable = true;
    private boolean isPrevious = true;

    private long doubleClickPopup = 0;
    private List<CarList> carListArrayList = new ArrayList<>();
    private long mLastClickTime = 0;

    public static Fragment newInstance() {
        return new AddCarFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car, container, false);

        initViews(view);
        loadResource();
        setListener();
        loadData();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    private void loadData() {

        getDataFromSP();

        for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {

            if (NissanApp.getInstance().getCarAllList().get(i).getId() == 20) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(19);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 19) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(18);
            }else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 18) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(17);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 14) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(16);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 13) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(15);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 15) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(14);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 12) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(13);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 16) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(12);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 11) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(11);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 3) { //make old juke 1st index for bottom of the list
                NissanApp.getInstance().getCarAllList().get(i).setIndex(1);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 6) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(10);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 8) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(9);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 10) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(8);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 1) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(7);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 2) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(6);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 4) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(5);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 5) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(4);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 7) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(3);
            } else if (NissanApp.getInstance().getCarAllList().get(i).getId() == 9) {
                NissanApp.getInstance().getCarAllList().get(i).setIndex(2);
            }
        }


        Collections.sort(NissanApp.getInstance().getCarAllList(), (ideaVal1, ideaVal2) -> {
            // avoiding NullPointerException in case name is null
            Long idea1 = (long) ideaVal1.getIndex();
            Long idea2 = (long) ideaVal2.getIndex();
            return idea2.compareTo(idea1);
        });

        final String ORDER = "102";
        Collections.sort(NissanApp.getInstance().getCarAllList(), (o1, o2) -> ORDER.indexOf(o1.getStatus()) - ORDER.indexOf(o2.getStatus()));

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
            }
        }

        replaceTheCarNamesAndImages();

        for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {

            if (NissanApp.getInstance().getCarAllList().get(i).getId() == Values.carType) {
                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(1);
            } else {
                NissanApp.getInstance().getCarAllList().get(i).setSelectedCar(0);
            }
        }

        adapter = new CarDownloadSettingsAdapter(AddCarFragment.this, getActivity(), requireActivity().getApplicationContext(), NissanApp.getInstance().getCarAllList());
        lstView.setAdapter(adapter);
        lstView.setDivider(null);
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
                    carInfoArrayList.get(i).setCarImg(NissanApp.getInstance().getURLAccordingToDensity(requireActivity(), NissanApp.getInstance().getDensityName(requireActivity()), carListModel));
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

    private void setListener() {
        lstView.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    public void loadResource() {
        ((MainActivity) requireActivity()).setTabResources();
        resources = new Resources(requireActivity().getAssets(), new DisplayMetrics(), NissanApp.getInstance().changeLocalLanguage(requireActivity(), preferenceUtil.getSelectedLang()));
        String car_selection_title = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.CAR_SELECTION_TITLE);
        txt_title.setText(car_selection_title.isEmpty() ? resources.getString(R.string.add_extra_car) : car_selection_title);
        txt_back_title.setText(resources.getString(R.string.back));

        Logger.error("carType", "_________" + Values.carType);
        getDataFromSP();
        replaceTheCarNamesAndImages();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        lstView = view.findViewById(R.id.lst_view);
        txt_title = view.findViewById(R.id.txt_title);
        txt_back_title = view.findViewById(R.id.txt_back_title);
        linearBack = view.findViewById(R.id.linear_back);
        preferenceUtil = new PreferenceUtil(requireActivity().getApplicationContext());
        commonDao = CommonDao.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        selectedCarIndex = position;
        CarInfo info = (CarInfo) parent.getAdapter().getItem(position);
        if ("1".equalsIgnoreCase(info.getStatus())) {
            carDownloadCheck(info.getId());
            ((MainActivity) requireActivity())
                    .sendMsgToGoogleAnalytics(((MainActivity) requireActivity())
                            .getAnalyticsForCarSection(Analytics.CAR_SELECTION));
        } else {
            carDownloadCheck(info.getId());
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                requireActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    private void carDownloadCheck(final int position) {


        if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(position))) {
            if (commonDao.getStatus(requireActivity().getBaseContext(), position) == 1) {
                CarInfo info = commonDao.getCarInfo(requireActivity().getApplicationContext(), position);
                if (info != null) {
                    if (!NissanApp.getInstance().getVersionName().equalsIgnoreCase(info.getVersionName()) || info.getVersionCode() != NissanApp.getInstance().getVersionCode()) {
                        if (DetectConnection.checkInternetConnection(requireActivity().getApplicationContext())) {
                            try {
                                FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(position)));
                                if (position == 2 || position == 5) {
                                    if (commonDao.getStatus(requireActivity().getApplicationContext(), position - 1) == 2) {
                                        commonDao.updateDateAndStatus(requireActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
//                                        adapter.list.remove(selectedCarIndex);
                                        adapter.list.get(selectedCarIndex).setStatus("2");
                                    } else {
                                        commonDao.updateDateAndStatus(requireActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        adapter.list.get(selectedCarIndex).setStatus("2");
                                    }

                                } else {
                                    if (position == 1 || position == 4) {
                                        CarInfo carInfo = commonDao.getCarInfo(requireActivity().getApplicationContext(), position + 1);

                                        commonDao.updateDateAndStatus(requireActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                        if (commonDao.getStatus(requireActivity().getApplicationContext(), position + 1) == 2) {
                                            commonDao.updateDateAndStatus(requireActivity().getApplicationContext(), position + 1, "2", NissanApp.getInstance().getDateTime(), "RUS", carInfo.getVersionName(), carInfo.getVersionCode());
//                                            adapter.list.remove(selectedCarIndex);
                                            adapter.list.get(selectedCarIndex).setStatus("2");
                                        } else {
                                            commonDao.updateDateAndStatus(requireActivity().getApplicationContext(), position + 1, "1", NissanApp.getInstance().getDateTime(), "EUR", carInfo.getVersionName(), carInfo.getVersionCode());
                                            adapter.list.get(selectedCarIndex).setStatus("2");
                                        }
                                    } else {
                                        if (position == 7 || position == 9) {
                                            commonDao.updateDateAndStatus(requireActivity().getApplicationContext(), position, "2", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            adapter.list.get(selectedCarIndex).setStatus("2");
                                        } else {
                                            commonDao.updateDateAndStatus(requireActivity().getApplicationContext(), position, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                            adapter.list.get(selectedCarIndex).setStatus("0");
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                adapter.carType = position;
                                adapter.position = selectedCarIndex;
                                adapter.lang = commonDao.getLanguageStatus(requireActivity().getApplicationContext(), position);
                                preferenceUtil.setSelectedLang(adapter.lang);
                                resources = new Resources(requireActivity().getAssets(), new DisplayMetrics(), NissanApp.getInstance().changeLocalLanguage(requireActivity(), adapter.lang));
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
                            preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(requireActivity().getBaseContext(), Values.carType));
                            loadResource();
                            ((MainActivity) requireActivity()).setTabResources();
                            adapter.loadResource();
                            getDataFromSP();
                            replaceTheCarNamesAndImages();
                            adapter.notifyDataSetChanged();
                        }

                    } else {

                        final ArrayList<PushContentInfo> list = commonDao.getNotificationList(requireActivity().getApplicationContext(), position, NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(requireActivity().getApplicationContext(), position)));

                        if (DetectConnection.checkInternetConnection(requireActivity().getApplicationContext())) {
                            if (list != null && list.size() > 0) {
                                Logger.error("You have a update ", "Do you want ???");
                                final StringBuilder stringBuilder = new StringBuilder();

                                for (PushContentInfo pushContentInfo : list) {
                                    stringBuilder.append(pushContentInfo.getePubId()).append(",");
                                }

                                stringBuilder.setLength(stringBuilder.length() - 1);

                                final Dialog dialog = new DialogController(requireActivity()).contentUpdateDialog();

                                TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
                                txtViewTitle.setText(resources.getString(R.string.update_msg));
//TODO
                                String okText = NissanApp.getInstance().getGlobalMessage(requireActivity().getApplicationContext()).getYes();
                                String cancelText = NissanApp.getInstance().getGlobalMessage(requireActivity().getApplicationContext()).getNo();

                                Button btnOk = dialog.findViewById(R.id.btn_ok);
//                                btnOk.setText(resources.getString(R.string.button_YES));
                                btnOk.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_YES) : okText);

                                btnOk.setOnClickListener(v -> {
                                    if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
                                        return;
                                    }
                                    doubleClickPopup = SystemClock.elapsedRealtime();

                                    dialog.dismiss();

                                    if (DetectConnection.checkInternetConnection(requireActivity().getApplicationContext())) {
                                        requireActivity().runOnUiThread(() -> adapter.progressDialog = new ProgressDialogController(getActivity()).showDialog(getResources().getString(R.string.start_download)));

                                        new ApiCall().postContentDownload("" + position, "" + NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(requireActivity().getApplicationContext(), position)), stringBuilder.toString(), NissanApp.getInstance().getDeviceID(requireActivity().getApplicationContext()), new CompleteAPI() {
                                            @Override
                                            public void onDownloaded(ResponseInfo responseInfo) {
                                                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getUrl())) {
                                                    //Todo implement download
                                                    CarDownloadHelper carDownloadHelper = new CarDownloadHelper(requireContext(), "" + Values.carType,
                                                            responseInfo.getLangUrl(), responseInfo.getAssetsUrl(),
                                                            NissanApp.getInstance().getCarPath(Values.carType)
                                                    );
                                                    carDownloadHelper.getDownloadProgress().observe(getViewLifecycleOwner(), carDownloadProgress -> Log.d("CarDownload", carDownloadProgress.toString()));
                                                    carDownloadHelper.downloadAssetAndLang();

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

                                    }
                                });

                                Button btnCancel = dialog.findViewById(R.id.btn_cancel);
//                                btnCancel.setText(resources.getString(R.string.button_NO));
                                btnCancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_NO) : cancelText);

                                btnCancel.setOnClickListener(v -> {
                                    if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
                                        return;
                                    }
                                    doubleClickPopup = SystemClock.elapsedRealtime();

                                    dialog.dismiss();

                                    for (int i = 0; i < NissanApp.getInstance().getCarAllList().size(); i++) {
                                        if (NissanApp.getInstance()
                                                .getCarAllList()
                                                .get(i)
                                                .getId() == position) {
                                            NissanApp.getInstance()
                                                    .getCarAllList()
                                                    .get(i)
                                                    .setSelectedCar(1);
                                        } else {
                                            NissanApp.getInstance()
                                                    .getCarAllList()
                                                    .get(i)
                                                    .setSelectedCar(0);
                                        }
                                    }
                                    Values.carType = position;
                                    Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);
                                    preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(requireActivity().getBaseContext(), Values.carType));
                                    loadResource();
                                    ((MainActivity) requireActivity()).setTabResources();
                                    adapter.loadResource();
                                    getDataFromSP();
                                    replaceTheCarNamesAndImages();
                                    adapter.notifyDataSetChanged();
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
                                preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(requireActivity().getBaseContext(), Values.carType));
                                loadResource();
                                ((MainActivity) requireActivity()).setTabResources();
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
                            preferenceUtil.setSelectedLang(commonDao.getLanguageStatus(requireActivity().getBaseContext(), Values.carType));
                            loadResource();
                            ((MainActivity) requireActivity()).setTabResources();
                            adapter.loadResource();
                            getDataFromSP();
                            replaceTheCarNamesAndImages();
                            adapter.notifyDataSetChanged();
                        }
                    }
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

        new ApiCall().postCarDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(adapter.lang), "0", NissanApp.getInstance().getDeviceID(requireActivity()), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                if (AppConfig.IS_APP_ONLINE ? Values.SUCCESS_STATUS.equals(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getAssetsUrl()) && !TextUtils.isEmpty(responseInfo.getLangUrl()) : Values.SUCCESS_STATUS.equals(responseInfo.getStatusCode())) {
                    adapter.startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? responseInfo.getAssetsUrl() : NissanApp.getInstance().getAssetsURL(position), Values.PATH, AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL((position), adapter.lang), NissanApp.getInstance().getCarPath(Values.carType), true);
                } else {
                    dismissDialog();
                    showErrorDialog("Status OR URL not reachable");
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
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(requireActivity().getApplicationContext(), msg);
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "error_fragment");
    }

}
