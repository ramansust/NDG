package com.nissan.alldriverguide.fragments.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nissan.alldriverguide.CarDownloadHelper;
import com.nissan.alldriverguide.CarDownloadProgress;
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
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        final Fragment frag = new LanguageFragment();
        return frag;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_language, container, false);
        this.deviceDensity = NissanApp.getInstance().getDensityName(this.getActivity());

        this.initViews(this.view);
        this.loadResource();
        this.getDataCarWise();
        this.setListener();

        return this.view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        new PreferenceUtil(this.getActivity()).setOpenCountForRateApp();
    }

    /**
     * Load the resources and set initialized text
     */
    private void loadResource() {
        this.resources = new Resources(this.getActivity().getAssets(), this.metrics, NissanApp.getInstance().changeLocalLanguage(this.getActivity(), this.preferenceUtil.getSelectedLang()));

        final String changeLanguageTitle = NissanApp.getInstance().getAlertMessage(this.getActivity(), this.preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_CHANGE_LANGUAGE);

        this.txt_title.setText(changeLanguageTitle == null || changeLanguageTitle.isEmpty() ? this.resources.getString(R.string.change_language) : changeLanguageTitle);
        this.txtBackTitle.setText(this.resources.getString(R.string.back));
    }

    private void getDataCarWise() {

        this.adapter = new LanguageSelectionAdapter(Objects.requireNonNull(this.getActivity()).getApplicationContext(), new ArrayList<LanguageInfo>(), true);
        this.lstView.setAdapter(this.adapter);

        this._languageLists = this.getDataFromSP();
        if (this._languageLists != null && this._languageLists.size() > 0) {
            this.progressBar.setVisibility(View.GONE);
            this.tvNoContent.setVisibility(View.GONE);
            this.populateDataIntoList();
        } else {
            this.progressBar.setVisibility(View.VISIBLE);
            if (!DetectConnection.checkInternetConnection(this.activity)) {
                this.progressBar.setVisibility(View.GONE);
                final String internetCheckMessage = NissanApp.getInstance().getAlertMessage(this.activity, this.preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
                this.showNoInternetDialogue(internetCheckMessage.isEmpty() ? this.resources.getString(R.string.internet_connect) : internetCheckMessage);
                return;
            }
        }

        this.controller.callApi(NissanApp.getInstance().getDeviceID(this.getActivity())/*"246E5A50-B79F-4019-82ED-877BF53FD617"*/, Values.carType + "");
    }

    @Override
    public void languageListDownloaded(final List<LanguageList> languageLists) {
        if (languageLists != null && languageLists.size() > 0) {
            if (this.progressBar.getVisibility() == View.VISIBLE) {
                this.progressBar.setVisibility(View.GONE);
                this.tvNoContent.setVisibility(View.GONE);
            }
            new PreferenceUtil(this.getActivity()).storeMultiLangData(languageLists, Values.carType + "_" + Values.CAR_LANGUAGE_LIST);
            _languageLists = languageLists;
            this.list = new ArrayList<>();
            this.populateDataIntoList();
        }
    }

    @Override
    public void languageListFailed(final String failedResponse) {
        if (this.progressBar.getVisibility() == View.VISIBLE) {
            this.progressBar.setVisibility(View.GONE);
            this.tvNoContent.setVisibility(View.VISIBLE);
        }
        Logger.error(LanguageFragment.TAG, ":callApiForLanguage______" + failedResponse);
    }


    private List<LanguageList> getDataFromSP() {

        final Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        return new Gson().fromJson(new PreferenceUtil(getActivity()).retrieveMultiLangData(Values.carType + "_" + Values.CAR_LANGUAGE_LIST), type);
    }

    private void showNoInternetDialogue(final String msg) {

        Dialog dialog = new DialogController(this.activity).internetDialog();

        final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        final Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
                LanguageFragment.this.getActivity().onBackPressed();
            }
        });

        dialog.show();
    }

    private void populateDataIntoList() {

        this.languageName = new String[this._languageLists.size()];
        this.languageShortName = new String[this._languageLists.size()];
        this.langFlagUri = new String[this._languageLists.size()];

        for (int i = 0; i < this._languageLists.size(); i++) {
            this.languageName[i] = (this._languageLists.get(i).getLanguageName());
            this.languageShortName[i] = (this._languageLists.get(i).getLanguageShortcode());

            if ("xxxhdpi".contains(this.deviceDensity)) {
                this.langFlagUri[i] = this._languageLists.get(i).getLanguageFlag().getXxxhdpi();
            } else if ("xxhdpi".contains(this.deviceDensity)) {
                this.langFlagUri[i] = this._languageLists.get(i).getLanguageFlag().getXxhdpi();
            } else if ("xhdpi".contains(this.deviceDensity)) {
                this.langFlagUri[i] = this._languageLists.get(i).getLanguageFlag().getXhdpi();
            } else if ("hdpi".contains(this.deviceDensity)) {
                this.langFlagUri[i] = this._languageLists.get(i).getLanguageFlag().getHdpi();
            } else if ("mdpi".contains(this.deviceDensity)) {
                this.langFlagUri[i] = this._languageLists.get(i).getLanguageFlag().getHdpi();
            } else if ("ldpi".contains(this.deviceDensity)) {
                this.langFlagUri[i] = this._languageLists.get(i).getLanguageFlag().getLdpi();
            }
        }
        this.loadData(this.langFlagUri);
    }

    /**
     * Here added the language to list according to car type;
     */
    private void loadData(final String[] FlagUrl) {
        this.list = new ArrayList<>();
        for (int i = 0; i < this.languageName.length; i++) {

            final boolean isDownloaded;
//             here check the epub existence on sdCard location
//             if available on sdCard isDownloaded true else false;
            isDownloaded = NissanApp.getInstance().isEpubExists(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + this.languageShortName[i], this.languageShortName[i]);
            final LanguageInfo info = new LanguageInfo(i, this.languageName[i], isDownloaded, FlagUrl[i]);
            this.list.add(info);

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


        this.adapter.setList(this.list);
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Set listener for click item
     */
    private void setListener() {
        this.lstView.setOnItemClickListener(this);
        this.btnBack.setOnClickListener(this);
        this.linearBack.setOnClickListener(this);
    }

    /**
     * Initialized all variable
     *
     * @param view need to find id
     */
    private void initViews(final View view) {
        this.activity = this.getActivity();
        this.context = this.getActivity().getApplicationContext();
        this.btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        this.lstView = (ListView) view.findViewById(R.id.lst_view);
        this.txt_title = (TextView) view.findViewById(R.id.txt_title);
        this.txtBackTitle = (TextView) view.findViewById(R.id.txt_back_title);
        this.tvNoContent = (TextView) view.findViewById(R.id.tvNoContent);
        this.progressBar = (ProgressBar) view.findViewById(R.id.pbLanguageFragment);
        this.preferenceUtil = new PreferenceUtil(this.getActivity().getApplicationContext());
        this.commonDao = CommonDao.getInstance();
        this.linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        this.dbHelper = new NissanDbHelper(this.getActivity());
        this.sqliteDB = this.dbHelper.getWritableDatabase();
        this.controller = new LanguageSelectionController(this);
        this.controllerGlobalMsg = new GlobalMessageController(this);
        this.controllerCarList = new CarListContentController(this);
        this.loadResource();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

        if (SystemClock.elapsedRealtime() - this.mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
            return;
        }
        this.mLastClickTime = SystemClock.elapsedRealtime();


        if (this.list.get(position).isSelected()) {
            this.preferenceUtil.setSelectedLang(this.languageShortName[this.list.get(position).getId()]);
            this.lang_sort_name = this.languageShortName[this.list.get(position).getId()];
            this.loadResource();

            ((MainActivity) this.getActivity()).setTabResources();
        } else {
            if (DetectConnection.checkInternetConnection(this.getActivity().getApplicationContext())) {
                this.lang_sort_name = this.languageShortName[this.list.get(position).getId()];
                this.showDownloadAlert(this.languageShortName[this.list.get(position).getId()], position);
            } else {
//                NissanApp.getInstance().showInternetAlert(getActivity(), getResources().getString(R.string.internet_connect));
                final String internetCheckMessage = NissanApp.getInstance().getAlertMessage(this.getActivity(), this.languageShortName[this.list.get(position).getId()], Values.ALERT_MSG_TYPE_INTERNET);
                NissanApp.getInstance().showInternetAlert(this.getActivity(), internetCheckMessage.isEmpty() ? this.getResources().getString(R.string.internet_connect) : internetCheckMessage);
            }
        }

    }

    @Override
    public void onDownloaded(final GlobalMsgResponse responseInfo) {

        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

            this.preferenceUtil.setPreviousLanguage(Values.carType + "_" + NissanApp.getInstance().getLanguageID(this.preferenceUtil.getSelectedLang()));

            final String key_global_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(this.lang_sort_name) + "_" + Values.GLOBAL_MSG_KEY;
            final String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(this.lang_sort_name) + "_" + Values.GLOBAL_ALERT_MSG_KEY;
            this.preferenceUtil.storeMultiLangData(responseInfo.getAlertMessage(), key_global_alert_message);
            this.preferenceUtil.storeMultiLangData(responseInfo.getGlobalMessage(), key_global_message);

            NissanApp.getInstance().setGlobalMessageArrayList(responseInfo.getGlobalMessage());
            NissanApp.getInstance().setAlertMessageGlobalArrayList(responseInfo.getAlertMessage());

            // start language download procedure
            this.dismissDialog();
        }
    }

    @Override
    public void onDownloaded(final CarListResponse responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
            final String car_list_key = this.lang_sort_name + "_" + Values.CAR_LIST_KEY + "_" + NissanApp.getInstance().getLanguageID(this.lang_sort_name);
            Logger.error(LanguageFragment.TAG, "car_list_key__________" + car_list_key);
            NissanApp.getInstance().setCarListWAP(responseInfo.getCarList());
            this.preferenceUtil.storeMultiLangData(responseInfo.getCarList(), car_list_key);
        }
    }

    @Override
    public void onFailed(final String failedReason) {
        Logger.error(LanguageFragment.TAG, "GlobalMsg__________" + failedReason);
        this.dismissDialog();
    }

    /**
     * Display download alert for language
     *
     * @param lang     language id need for save language sort name
     * @param position adapter position
     */
    private void showDownloadAlert(String lang, int position) {
        Dialog dialog = new DialogController(this.getActivity()).carDownloadDialog();

        final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        final String downloadSureMsg = NissanApp.getInstance().getAlertMessage(this.getActivity(), this.preferenceUtil.getSelectedLang(), Values.DOWNLOAD_SURE_MSG);
        txtViewTitle.setText(downloadSureMsg.isEmpty() ? this.getResources().getString(R.string.download_lang) : downloadSureMsg);

        ((TextView) dialog.findViewById(R.id.txt_header)).setText(this.activity.getResources().getString(R.string.change_language_popup_title));

        final String okText = NissanApp.getInstance().getGlobalMessage(this.getActivity()).getOk();
        final String cancelText = NissanApp.getInstance().getGlobalMessage(this.getActivity()).getCancel();

        final Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);

        btnOk.setText(okText == null || okText.isEmpty() ? this.resources.getString(R.string.button_OK) : okText);
        btnCancel.setText(cancelText == null || cancelText.isEmpty() ? this.resources.getString(R.string.button_CANCEL) : cancelText);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.dismiss();
//                changeGlobalAlertMsg(position);
                LanguageFragment.this.startDownloadProcedure(lang, position);
                LanguageFragment.this.controllerCarList.callApi(NissanApp.getInstance().getDeviceID(LanguageFragment.this.getActivity()), NissanApp.getInstance().getLanguageID(lang) + "");
            }
        });
        dialog.show();
    }

    private void startDownloadProcedure(String lang, int position) {

        String downloadingMsg = NissanApp.getInstance().getAlertMessage(this.context, this.preferenceUtil.getSelectedLang(), Values.DOWNLOADING);
        String startingToDownloadMessage = NissanApp.getInstance().getAlertMessage(this.context, this.preferenceUtil.getSelectedLang(), Values.STARTING_DOWNLOAD);
        String dataSyncingMsg = NissanApp.getInstance().getAlertMessage(this.context, this.preferenceUtil.getSelectedLang(), DATA_SYNCING);
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LanguageFragment.this.progressDialog = new ProgressDialogController(LanguageFragment.this.activity).showDialog(startingToDownloadMessage == null || startingToDownloadMessage.isEmpty() ? LanguageFragment.this.resources.getString(R.string.start_download) : startingToDownloadMessage);
            }
        });

        new ApiCall().postLanguageDownload(Values.carType + "", "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(this.getActivity()), new CompleteAPI() {
            @Override
            public void onDownloaded(final ResponseInfo responseInfo) {

                if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getLangUrl())) {

                    final String old_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(LanguageFragment.this.preferenceUtil.getSelectedLang()) + "_" + Values.TUTORIAL_KEY;
                    final String old_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(LanguageFragment.this.preferenceUtil.getSelectedLang()) + "_" + Values.TAB_MENU_KEY;
                    final String new_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TUTORIAL_KEY;
                    final String new_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TAB_MENU_KEY;

                    Logger.error("Old key", " " + old_key_tutorial);
                    Logger.error("New key", " " + new_key_tutorial);

                    LanguageFragment.this.preferenceUtil.deleteMultiLangData(old_key_tab);
                    LanguageFragment.this.preferenceUtil.deleteMultiLangData(old_key_tutorial);
                    LanguageFragment.this.preferenceUtil.storeMultiLangData(responseInfo.getTutorials(), new_key_tutorial);
                    LanguageFragment.this.preferenceUtil.storeMultiLangData(responseInfo.getTabMenu(), new_key_tab);

//                    new MADownloadManager(activity, context).downloadLanguage(false, "Language", AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL(Values.carType, lang), NissanApp.getInstance().getCarPath(Values.carType), new DownloaderStatus() {
//                        @Override
//                        public boolean onComplete(boolean b) {
//                            if (b) {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @SuppressLint("StaticFieldLeak")
//                                    @Override
//                                    public void run() {
//                                        if (progressDialog != null) {
//                                            progressDialog.setMessage(dataSyncingMsg == null || dataSyncingMsg.isEmpty() ? activity.getResources().getString(R.string.data_syncing) : dataSyncingMsg);
//                                        }
//
//                                        new SearchDBAsync(getActivity(), lang, Values.carType) {
//                                            @Override
//                                            public void onComplete(boolean status) {
//
//                                                if (status) {
//                                                    new ApiCall().postLanguageDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
//                                                        @Override
//                                                        public void onDownloaded(ResponseInfo responseInfo) {
//
//                                                            if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
//                                                                try {
//
//                                                                    if (!preferenceUtil.getSelectedLang().equals("") && !lang.equals("")) {
//                                                                        if (!preferenceUtil.getSelectedLang().equals(lang)) {
//                                                                            // delete previous language directory
//                                                                            // eg. /storage/emulated/0/.AllDriverGuide/leaf2017/leaf2017_en
//                                                                            FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType)));
//                                                                            Logger.error("File Delete", "" + NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType));
//                                                                        }
//                                                                    }
//
////************************(Here store the language short name after complete downloading language)*******************************************************************************************************************
//                                                                    preferenceUtil.setSelectedLang(lang); // here store the language sort name
//                                                                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromSettings(Analytics.CHANGE_LANGUAGE + Analytics.DOWNLOAD));
//                                                                    commonDao.updateLanguageStatus(getActivity().getBaseContext(), Values.carType, lang);
//                                                                    commonDao.makeAllPushEntryStatusChangeLangauge(getActivity().getBaseContext(), Values.carType, NissanApp.getInstance().getLanguageID(lang));
//                                                                    commonDao.deleteSingleCarEpub(getActivity(), Values.carType);
//                                                                } catch (Exception e) {
//                                                                    e.printStackTrace();
//                                                                }
//
//                                                                // set selection for selected language
//                                                                for (int i = 0; i < list.size(); i++) {
//                                                                    if (i == position) {
//                                                                        list.get(i).setIsSelected(true);
//                                                                    } else {
//                                                                        list.get(i).setIsSelected(false);
//                                                                    }
//                                                                }
//
//                                                                adapter.notifyDataSetChanged();
//                                                                loadResource(); // load resource for change language
//                                                                ((MainActivity) getActivity()).loadResource();
//                                                                ((MainActivity) getActivity()).setTabResources();
//
//                                                                controllerGlobalMsg.callApi(NissanApp.getInstance().getDeviceID(getActivity()), NissanApp.getInstance().getLanguageID(lang_sort_name) + "");
//                                                                preferenceUtil.deleteMultiLangData(Values.carType + "_" + Values.ASSISTANCE_OBJ_STORE_KEY);
//                                                                preferenceUtil.deleteMultiLangData(Values.carType + "_" + Values.EXPLORE_OBJ_STORE_KEY);
//                                                                preferenceUtil.deleteMultiLangData(Values.carType + "_" + Values.SETTING_OBJ_STORE_KEY);
//                                                            } else {
//                                                                showErrorDialog("Confirmation send error!");
//                                                                dismissDialog();
//                                                                Logger.error("problem", "______sending confirmation-LanguageFragment");
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onFailed(String failedReason) {
//                                                            showErrorDialog("Confirmation send error");
//                                                            dismissDialog();
//                                                            Logger.error("failed_to_send", "______confirmation-LanguageFragment" + failedReason);
//                                                        }
//                                                    });
//
//                                                } else {
//                                                    showErrorDialog("Database Error Occurred, Please try again later.");
//                                                    errorFileDelete(Values.carType);
//                                                }
//                                            }
//                                        }.execute();
//
//                                    }
//                                });
//
//                            } else {
//                                dismissDialog();
//                                showErrorDialog("Parsing Error Occurred, Please try again later.");
//                                Logger.error("problem", "______assetDownload-LanguageFragment");
//                            }
//                            return false;
//                        }
//
//                        @Override
//                        public int onError(int i) {
//                            showErrorDialog(context.getResources().getString(R.string.internet_connection_interruption)); //"Hi Unknown Error Occurred, Please try again later.");
//                            errorFileDelete(Values.carType);
//                            return 0;
//                        }
//
//                        @Override
//                        public boolean internetConnection(boolean b) {
//                            errorFileDelete(Values.carType);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean urlReachable(boolean b) {
//                            errorFileDelete(Values.carType);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean destinationExists(boolean b) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean sourcePath(boolean b) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean destinationPath(boolean b) {
//                            return false;
//                        }
//
//                        @Override
//                        public void downloadCompletion(Float aFloat) {
//                            String formattedString = String.format("%.02f", aFloat);
//                            if (progressDialog != null) {
//                                progressDialog.setMessage((downloadingMsg == null || downloadingMsg.isEmpty() ? resources.getString(R.string.alert_download_complete) : downloadingMsg) + formattedString + "%");
//                                //resources.getStringArray(R.array.car_names)[Values.carType - 1] + "\n" +
//                            }
//                        }
//
//                        @Override
//                        public void init() {
//                            if (progressDialog == null) {
//                                progressDialog = new ProgressDialogController(activity).downloadProgress(getActivity().getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
//                            }
//                        }
//                    });

                    //TODO Implement Download
                    final CarDownloadHelper carDownloadHelper = new CarDownloadHelper(LanguageFragment.this.getContext(), "" + Values.carType,
                            responseInfo.getLangUrl(), responseInfo.getAssetsUrl(),
                            NissanApp.getInstance().getCarPath(Values.carType)
                    );
                    carDownloadHelper.getDownloadProgress().observe(LanguageFragment.this.getViewLifecycleOwner(), new Observer<CarDownloadProgress>() {
                        @Override
                        public void onChanged(final CarDownloadProgress carDownloadProgress) {
                            Log.d("CarDownload", carDownloadProgress.toString());
                        }
                    });
                    carDownloadHelper.downloadAssetAndLang();
                } else {
                    LanguageFragment.this.showErrorDialog("No content available!");
                    LanguageFragment.this.dismissDialog();
                }
            }

            @Override
            public void onFailed(final String failedReason) {
                LanguageFragment.this.dismissDialog();
                LanguageFragment.this.showErrorDialog(LanguageFragment.this.resources.getString(R.string.failed_to_connect_server));
                Logger.error("Language downloading failed", "____________" + failedReason);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (SystemClock.elapsedRealtime() - this.mLastClickTime < 2000) {
                    return;
                }
                this.mLastClickTime = SystemClock.elapsedRealtime();
                ((MainActivity) this.getActivity()).onBackPressed();

            case R.id.linear_back:
                if (SystemClock.elapsedRealtime() - this.mLastClickTime < 2000) {
                    return;
                }
                this.mLastClickTime = SystemClock.elapsedRealtime();
                ((MainActivity) this.getActivity()).onBackPressed();
                break;

            default:
                break;
        }
    }

    private void errorFileDelete(final int carType) {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    private void dismissDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    private void showErrorDialog(final String msg) {
        final DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(this.context, msg);
        dialogFragment.show(this.getActivity().getSupportFragmentManager(), "error_fragment");
    }
}
