package com.nissan.alldriverguide.fragments.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.LanguageSelectionAdapter;
import com.nissan.alldriverguide.controller.CarListContentController;
import com.nissan.alldriverguide.controller.GlobalMessageController;
import com.nissan.alldriverguide.controller.LanguageSelectionController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.NissanDbHelper;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.InterfaceGlobalMessageResponse;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.LanguageInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.interfaces.InterfaceLanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
import com.nissan.alldriverguide.multiLang.model.LanguageListResponse;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.nissan.alldriverguide.utils.Values.DATA_SYNCING;
import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class LanguageFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, InterfaceLanguageListResponse, InterfaceGlobalMessageResponse, CarListACompleteAPI {

    private static final String TAG = "LanguageFragment";
    NissanDbHelper dbHelper;
    SQLiteDatabase sqliteDB;
    private String[] languageName; /*= {"English", "Deutsch", "Français", "Italiano", "Español", "Nederlands", "Русский", "Svenska", "Norsk", "Polski", "Suomi", "Português"};*/
    private String[] languageShortName; /*= {"en", "de", "fr", "it", "es", "nl", "ru", "sv", "no", "pl", "fi", "pt"};*/
    private int[] languageImage; /*= {R.drawable.united_kingdom, R.drawable.germany, R.drawable.france, R.drawable.italy, R.drawable.spain, R.drawable.netherlands, R.drawable.russia, R.drawable.sweden, R.drawable.norway, R.drawable.poland, R.drawable.finland, R.drawable.portugal};*/
    private View view;
    private ListView lstView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private LanguageSelectionAdapter adapter;
    private ArrayList<LanguageInfo> list;
    private PreferenceUtil preferenceUtil;
    private Configuration conf;
    private Resources resources;
    private DisplayMetrics metrics;
    private TextView txt_title;
    private TextView txtBackTitle;
    private CommonDao commonDao;
    private Typeface tf;
    private ProgressDialog progressDialog;
    private Activity activity;
    private Context context;
    private LanguageListResponse languageListResponses;

    private String deviceDensity, lang_sort_name = "";
    private String[] langFlagUri;
    private List<LanguageList> _languageLists = new ArrayList<>();
    private long mLastClickTime;
    private LanguageSelectionController controller;
    private GlobalMessageController controllerGlobalMsg;
    private CarListContentController controllerCarList;
    private ProgressBar progressBar;
    private TextView tvNoContent;

    public static Fragment newInstance() {
        Fragment frag = new LanguageFragment();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_language, container, false);
        deviceDensity = NissanApp.getInstance().getDensityName(getActivity());

        initViews(view);
        loadResource();
        getDataCarWise();
        setListener();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    /**
     * Load the resources and set initialized text
     */
    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));

        String changeLanguageTitle = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_CHANGE_LANGUAGE);

        txt_title.setText(changeLanguageTitle == null || changeLanguageTitle.isEmpty() ? resources.getString(R.string.change_language) : changeLanguageTitle);
        txtBackTitle.setText(resources.getString(R.string.back));
        txtBackTitle.setTypeface(tf);
    }

    private void getDataCarWise() {

        adapter = new LanguageSelectionAdapter(getActivity().getApplicationContext(), new ArrayList<LanguageInfo>(), true);
        lstView.setAdapter(adapter);

        _languageLists = getDataFromSP();
        if (_languageLists != null && _languageLists.size() > 0) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.GONE);
            populateDataIntoList();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if (!DetectConnection.checkInternetConnection(activity)) {
                progressBar.setVisibility(View.GONE);
                String internetCheckMessage = NissanApp.getInstance().getAlertMessage(activity, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
                showNoInternetDialogue(internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage);
                return;
            }
        }

            controller.callApi(NissanApp.getInstance().getDeviceID(getActivity())/*"246E5A50-B79F-4019-82ED-877BF53FD617"*/, Values.carType + "");

    }

    @Override
    public void languageListDownloaded(List<LanguageList> languageLists) {
        if (languageLists != null && languageLists.size() > 0) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
                tvNoContent.setVisibility(View.GONE);
            }
            new PreferenceUtil(getActivity()).storeMultiLangData(languageLists, Values.carType + "_" + Values.CAR_LANGUAGE_LIST);
            this._languageLists = languageLists;
            list = new ArrayList<>();
            populateDataIntoList();
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


    private List<LanguageList> getDataFromSP() {

        Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        return new Gson().fromJson(new PreferenceUtil(this.getActivity()).retrieveMultiLangData(Values.carType + "_" + Values.CAR_LANGUAGE_LIST), type);

    }
    private void showNoInternetDialogue(String msg) {

        final Dialog dialog = new DialogController(activity).internetDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().onBackPressed();
            }
        });

        dialog.show();

    }
    private void populateDataIntoList() {

        languageName = new String[_languageLists.size()];
        languageShortName = new String[_languageLists.size()];
        langFlagUri =  new String[_languageLists.size()];

        for(int i = 0; i < _languageLists.size(); i++){
            languageName[i] = (_languageLists.get(i).getLanguageName());
            languageShortName[i] = (_languageLists.get(i).getLanguageShortcode());

            if("xxxhdpi".contains(deviceDensity)){
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getXxxhdpi();
            } else if("xxhdpi".contains(deviceDensity)){
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getXxhdpi();
            }else if("xhdpi".contains(deviceDensity)){
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getXhdpi();
            }else if("hdpi".contains(deviceDensity)){
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getHdpi();
            }else if("mdpi".contains(deviceDensity)){
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getHdpi();
            }else if("ldpi".contains(deviceDensity)){
                langFlagUri[i] = _languageLists.get(i).getLanguageFlag().getLdpi();
            }
        }
        loadData(langFlagUri);
    }

    /**
     * Here added the language to list according to car type;
     */
    private void loadData(String FlagUrl[]) {
        list = new ArrayList<>();
        for (int i = 0; i < languageName.length; i++) {

            boolean isDownloaded;
//             here check the epub existence on sdCard location
//             if available on sdCard isDownloaded true else false;
            if (NissanApp.getInstance().isEpubExists(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + languageShortName[i], languageShortName[i])) {
                isDownloaded = true;
            } else {
                isDownloaded = false;
            }
            LanguageInfo info = new LanguageInfo(i, languageName[i], isDownloaded, FlagUrl[i]);
            list.add(info);

/*
            if (Values.carType == 2 || Values.carType == 5) { // car type Qashqai Rus space and X-Trail Rus space added only two language
                if (i == 0 || i == 6) {
                    list.add(info);
                }
            } else {
                if (Values.carType == 7 || Values.carType == 8 || Values.carType == 9) {
                    if (i != 6 && i != 8) { // 7,8 & 9 car added all language except 6 & 8 two language
                        list.add(info);
                    }
                } else if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 6 || Values.carType == 10 || Values.carType == 11 || Values.carType == 12 || Values.carType == 13) {

                    Logger.error("lang_name", "____________" + languageName[i] + "_____" + i);

                    if (i != 8) { // added except 8 index language
                        list.add(info);
                    }
                } else {
                    list.add(info);
                }
            }
*/

        }


        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * Set listener for click item
     */
    private void setListener() {
        lstView.setOnItemClickListener(this);
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);
    }

    /**
     * Initialized all variable
     * @param view need to find id
     */
    private void initViews(View view) {
        activity = getActivity();
        context = getActivity().getApplicationContext();
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        lstView = (ListView) view.findViewById(R.id.lst_view);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txtBackTitle = (TextView) view.findViewById(R.id.txt_back_title);
        tvNoContent = (TextView) view.findViewById(R.id.tvNoContent);
        progressBar = (ProgressBar) view.findViewById(R.id.pbLanguageFragment);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        commonDao = CommonDao.getInstance();
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Nissan Brand Regular.otf");
        dbHelper = new NissanDbHelper(getActivity());
        sqliteDB = dbHelper.getWritableDatabase();
        controller = new LanguageSelectionController(this);
        controllerGlobalMsg = new GlobalMessageController(this);
        controllerCarList = new CarListContentController(this);
        loadResource();
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

        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();


        if (list.get(position).isSelected()) {
            preferenceUtil.setSelectedLang(languageShortName[list.get(position).getId()]);
            lang_sort_name = languageShortName[list.get(position).getId()];
            loadResource();

            ((MainActivity) getActivity()).setTabResources();
        } else {
            if (DetectConnection.checkInternetConnection(getActivity().getApplicationContext())) {
                lang_sort_name = languageShortName[list.get(position).getId()];
                showDownloadAlert(languageShortName[list.get(position).getId()], position);
            } else {
//                NissanApp.getInstance().showInternetAlert(getActivity(), getResources().getString(R.string.internet_connect));
                String internetCheckMessage = NissanApp.getInstance().getAlertMessage(getActivity(), languageShortName[list.get(position).getId()], Values.ALERT_MSG_TYPE_INTERNET);
                NissanApp.getInstance().showInternetAlert(getActivity(), internetCheckMessage.isEmpty() ? getResources().getString(R.string.internet_connect) : internetCheckMessage);
            }
        }

    }

    @Override
    public void onDownloaded(GlobalMsgResponse responseInfo) {

        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

            preferenceUtil.setPreviousLanguage(Values.carType+"_"+NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()));

            String key_global_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang_sort_name) + "_" + Values.GLOBAL_MSG_KEY;
            String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang_sort_name) + "_" + Values.GLOBAL_ALERT_MSG_KEY;
            preferenceUtil.storeMultiLangData(responseInfo.getAlertMessage(), key_global_alert_message);
            preferenceUtil.storeMultiLangData(responseInfo.getGlobalMessage(), key_global_message);

            NissanApp.getInstance().setGlobalMessageArrayList(responseInfo.getGlobalMessage());
            NissanApp.getInstance().setAlertMessageGlobalArrayList(responseInfo.getAlertMessage());

            // start language download procedure
            dismissDialog();

        }
    }

    @Override
    public void onDownloaded(CarListResponse responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
            String car_list_key = lang_sort_name + "_" + Values.CAR_LIST_KEY + "_" + NissanApp.getInstance().getLanguageID(lang_sort_name);
            Logger.error(TAG, "car_list_key__________" + car_list_key);
            NissanApp.getInstance().setCarListWAP(responseInfo.getCarList());
            preferenceUtil.storeMultiLangData(responseInfo.getCarList(), car_list_key);
        }
    }

    @Override
    public void onFailed(String failedReason) {
        Logger.error(TAG, "GlobalMsg__________" + failedReason);
        dismissDialog();
    }

    /**
     * Display download alert for language
     * @param lang language id need for save language sort name
     * @param position adapter position
     */
    private void showDownloadAlert(final String lang, final int position) {
        final Dialog dialog = new DialogController(getActivity()).carDownloadDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        String downloadSureMsg = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.DOWNLOAD_SURE_MSG);
        txtViewTitle.setText(downloadSureMsg.isEmpty() ? getResources().getString(R.string.download_lang) : downloadSureMsg);

        ((TextView) dialog.findViewById(R.id.txt_header)).setText(activity.getResources().getString(R.string.change_language_popup_title));

        String okText = NissanApp.getInstance().getGlobalMessage(getActivity()).getOk();
        String cancelText = NissanApp.getInstance().getGlobalMessage(getActivity()).getCancel();

        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);

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
//                changeGlobalAlertMsg(position);
                startDownloadProcedure(lang, position);
                controllerCarList.callApi(NissanApp.getInstance().getDeviceID(getActivity()), NissanApp.getInstance().getLanguageID(lang)+"");
            }
        });
        dialog.show();
    }

    private void startDownloadProcedure(final String lang, final int position) {

        final String downloadingMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.DOWNLOADING);
        final String startingToDownloadMessage = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.STARTING_DOWNLOAD);
        final String dataSyncingMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), DATA_SYNCING);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressDialog = new ProgressDialogController(activity).showDialog(startingToDownloadMessage == null || startingToDownloadMessage.isEmpty() ? resources.getString(R.string.start_download) : startingToDownloadMessage);
            }
        });

        new ApiCall().postLanguageDownload(Values.carType + "", "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(getActivity()), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getLangUrl())) {

                    String old_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TUTORIAL_KEY;
                    String old_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TAB_MENU_KEY;
                    String new_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TUTORIAL_KEY;
                    String new_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TAB_MENU_KEY;

                    Logger.error("Old key" , " " + old_key_tutorial);
                    Logger.error("New key" , " " + new_key_tutorial);

                    preferenceUtil.deleteMultiLangData(old_key_tab);
                    preferenceUtil.deleteMultiLangData(old_key_tutorial);
                    preferenceUtil.storeMultiLangData(responseInfo.getTutorials(), new_key_tutorial);
                    preferenceUtil.storeMultiLangData(responseInfo.getTabMenu(), new_key_tab);

                    new MADownloadManager(activity, context).downloadLanguage(false, "Language", AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL(Values.carType, lang), NissanApp.getInstance().getCarPath(Values.carType), new DownloaderStatus() {
                        @Override
                        public boolean onComplete(boolean b) {
                            if (b) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @SuppressLint("StaticFieldLeak")
                                    @Override
                                    public void run() {
                                        if (progressDialog != null) {
                                            progressDialog.setMessage(dataSyncingMsg == null || dataSyncingMsg.isEmpty() ? activity.getResources().getString(R.string.data_syncing) : dataSyncingMsg);
                                        }

                                        new SearchDBAsync(getActivity(), lang, Values.carType) {
                                            @Override
                                            public void onComplete(boolean status) {

                                                if (status) {
                                                    new ApiCall().postLanguageDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
                                                        @Override
                                                        public void onDownloaded(ResponseInfo responseInfo) {

                                                            if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                                                                try {

                                                                    if (!preferenceUtil.getSelectedLang().equals("") && !lang.equals("")) {
                                                                        if (!preferenceUtil.getSelectedLang().equals(lang)) {
                                                                            // delete previous language directory
                                                                            // eg. /storage/emulated/0/.AllDriverGuide/leaf2017/leaf2017_en
                                                                            FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType)));
                                                                            Logger.error("File Delete" , "" + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType));
                                                                        }
                                                                    }

//************************(Here store the language short name after complete downloading language)*******************************************************************************************************************
                                                                    preferenceUtil.setSelectedLang(lang); // here store the language sort name
                                                                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromSettings(Analytics.CHANGE_LANGUAGE + Analytics.DOWNLOAD));
                                                                    commonDao.updateLanguageStatus(getActivity().getBaseContext(), Values.carType, lang);
                                                                    commonDao.makeAllPushEntryStatusChangeLangauge(getActivity().getBaseContext(), Values.carType, NissanApp.getInstance().getLanguageID(lang));
                                                                    commonDao.deleteSingleCarEpub(getActivity(), Values.carType);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                                // set selection for selected language
                                                                for (int i = 0; i < list.size(); i++) {
                                                                    if (i == position) {
                                                                        list.get(i).setIsSelected(true);
                                                                    } else {
                                                                        list.get(i).setIsSelected(false);
                                                                    }
                                                                }

                                                                adapter.notifyDataSetChanged();
                                                                loadResource(); // load resource for change language
                                                                ((MainActivity) getActivity()).loadResource();
                                                                ((MainActivity) getActivity()).setTabResources();

                                                                controllerGlobalMsg.callApi(NissanApp.getInstance().getDeviceID(getActivity()), NissanApp.getInstance().getLanguageID(lang_sort_name)+"");
                                                                preferenceUtil.deleteMultiLangData(Values.carType + "_" + Values.ASSISTANCE_OBJ_STORE_KEY);
                                                                preferenceUtil.deleteMultiLangData(Values.carType + "_" + Values.EXPLORE_OBJ_STORE_KEY);
                                                                preferenceUtil.deleteMultiLangData(Values.carType + "_" + Values.SETTING_OBJ_STORE_KEY);

                                                            } else {
                                                                showErrorDialog("Confirmation send error!");
                                                                dismissDialog();
                                                                Logger.error("problem", "______sending confirmation-LanguageFragment");
                                                            }

                                                        }

                                                        @Override
                                                        public void onFailed(String failedReason) {
                                                            showErrorDialog("Confirmation send error");
                                                            dismissDialog();
                                                            Logger.error("failed_to_send", "______confirmation-LanguageFragment" + failedReason);
                                                        }
                                                    });

                                                } else {
                                                    showErrorDialog("Database Error Occurred, Please try again later.");
                                                    errorFileDelete(Values.carType);
                                                }
                                            }
                                        }.execute();

                                    }
                                });

                            } else {
                                dismissDialog();
                                showErrorDialog("Parsing Error Occurred, Please try again later.");
                                Logger.error("problem", "______assetDownload-LanguageFragment");
                            }
                            return false;
                        }

                        @Override
                        public int onError(int i) {
                            showErrorDialog(context.getResources().getString(R.string.internet_connection_interruption)); //"Hi Unknown Error Occurred, Please try again later.");
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
                                progressDialog.setMessage((downloadingMsg == null || downloadingMsg.isEmpty() ? resources.getString(R.string.alert_download_complete) : downloadingMsg) + formattedString + "%");
                                //resources.getStringArray(R.array.car_names)[Values.carType - 1] + "\n" +
                            }
                        }

                        @Override
                        public void init() {
                            if (progressDialog == null ) {
                                progressDialog = new ProgressDialogController(activity).downloadProgress(getActivity().getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
                            }
                        }
                    });
                } else {
                    showErrorDialog("No content available!");
                    dismissDialog();
                }
            }

            @Override
            public void onFailed(String failedReason) {
                dismissDialog();
                showErrorDialog(resources.getString(R.string.failed_to_connect_server));
                Logger.error("Language downloading failed", "____________" + failedReason);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ((MainActivity) getActivity()).onBackPressed();

            case R.id.linear_back:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ((MainActivity) getActivity()).onBackPressed();
                break;

            default:
                break;
        }
    }

    private void errorFileDelete(int carType) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "error_fragment");
    }

}
