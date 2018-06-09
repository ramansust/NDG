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

import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.LanguageSelectionAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.NissanDbHelper;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.LanguageInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
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
import java.util.ArrayList;

public class LanguageFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    NissanDbHelper dbHelper;
    SQLiteDatabase sqliteDB;
    private String[] languageName = {"English", "Deutsch", "Français", "Italiano", "Español", "Nederlands", "Русский", "Svenska", "Norsk", "Polski", "Suomi", "Português"};
    private String[] languageShortName = {"en", "de", "fr", "it", "es", "nl", "ru", "sv", "no", "pl", "fi", "pt"};
    private int[] languageImage = {R.drawable.united_kingdom, R.drawable.germany, R.drawable.france, R.drawable.italy, R.drawable.spain, R.drawable.netherlands, R.drawable.russia, R.drawable.sweden, R.drawable.norway, R.drawable.poland, R.drawable.finland, R.drawable.portugal};
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

    public static Fragment newInstance() {
        Fragment frag = new LanguageFragment();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_language, container, false);

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

    /**
     * Load the resources and set initialized text
     */
    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
        txt_title.setText(resources.getString(R.string.change_language));
        txtBackTitle.setText(resources.getString(R.string.back));
        txtBackTitle.setTypeface(tf);
    }

    /**
     * Here added the language to list according to car type;
     */
    private void loadData() {
        list = new ArrayList<>();

        for (int i = 0; i < languageName.length; i++) {
            boolean isDownloaded;
            // here check the epub existence on sdCard location
            // if available on sdCard isDownloaded true else false;
            if (NissanApp.getInstance().isEpubExists(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + languageShortName[i], languageShortName[i])) {
                isDownloaded = true;
            } else {
                isDownloaded = false;
            }
            LanguageInfo info = new LanguageInfo(i, languageName[i], isDownloaded, languageImage[i]);
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
                    if (i != 8) { // added except 8 index language
                        list.add(info);
                    }
                } else {
                    list.add(info);
                }
            }
        }

        // set the adapter
        adapter = new LanguageSelectionAdapter(getActivity().getApplicationContext(), list, true);
        lstView.setAdapter(adapter);
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
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        commonDao = CommonDao.getInstance();
        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/Nissan Brand Regular.otf");
        dbHelper = new NissanDbHelper(getActivity());
        sqliteDB = dbHelper.getWritableDatabase();
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

        if (list.get(position).isSelected()) {
            preferenceUtil.setSelectedLang(languageShortName[list.get(position).getId()]);
            loadResource();

            ((MainActivity) getActivity()).setTabResources();
        } else {
            if (DetectConnection.checkInternetConnection(getActivity().getApplicationContext())) {
                showDownloadAlert(languageShortName[list.get(position).getId()], position);
            } else {
                NissanApp.getInstance().showInternetAlert(getActivity(), getResources().getString(R.string.internet_connect));
            }
        }

    }

    /**
     * Display download alert for language
     * @param lang language id need for save language sort name
     * @param position adapter position
     */
    private void showDownloadAlert(final String lang, final int position) {
        final Dialog dialog = new DialogController(getActivity()).langDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(getResources().getString(R.string.download_lang));

        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);

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
                startDownloadProcedure(lang, position); // start language download procedure
            }
        });
        dialog.show();
    }

    private void startDownloadProcedure(final String lang, final int position) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialogController(activity).showDialog(resources.getString(R.string.start_download));
            }
        });

        new ApiCall().postLanguageDownload(Values.carType + "", "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(getActivity()), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getLangUrl())) {

                    String old_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TUTORIAL;
                    String old_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TAB_MENU;
                    String new_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TUTORIAL;
                    String new_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TAB_MENU;

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
                                            progressDialog.setMessage(getResources().getString(R.string.data_syncing));
                                        }

                                        new SearchDBAsync(getActivity(), lang, Values.carType) {
                                            @Override
                                            public void onComplete(boolean status) {

                                                if (status) {
                                                    new ApiCall().postLanguageDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
                                                        @Override
                                                        public void onDownloaded(ResponseInfo responseInfo) {

                                                            if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                                                                try {
                                                                    // delete previous language directory
                                                                    FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + commonDao.getLanguageStatus(getActivity().getBaseContext(), Values.carType)));

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

                                                                dismissDialog();
                                                            } else {
                                                                showErrorDialog("Confirmation send error!");
                                                                dismissDialog();
                                                                Logger.error("problem", "______sending confirmation-LanguageFragment");
                                                            }

                                                        }

                                                        @Override
                                                        public void onFailed(String failedReason) {
                                                            showErrorDialog("Confirmation send error, " + failedReason);
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
                            showErrorDialog("Unknown Error Occurred, Please try again later.");
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
                                progressDialog.setMessage(activity.getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete) + formattedString + "%");
                            }
                        }

                        @Override
                        public void init() {
                            if (progressDialog == null) {
                                progressDialog = new ProgressDialogController(activity).downloadProgress(getActivity().getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
                            }
                        }
                    });
                } else {
                    showErrorDialog("No content available!");
                }
            }

            @Override
            public void onFailed(String failedReason) {
                dismissDialog();
                showErrorDialog(failedReason);
                Logger.error("Language downloading failed", "____________" + failedReason);
            }
        });


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
