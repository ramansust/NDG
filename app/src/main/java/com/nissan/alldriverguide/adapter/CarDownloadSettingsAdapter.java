package com.nissan.alldriverguide.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.settings.AddCarFragment;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAlertAPI;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.LanguageInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.AlertMessage;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;
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
import java.util.Comparator;
import java.util.List;

/**
 * Created by raman on 1/19/17.
 */
public class CarDownloadSettingsAdapter extends BaseAdapter implements View.OnClickListener {

    private CommonDao commonDao;
    private Context context;
    private LayoutInflater inflater;
    public ArrayList<CarInfo> list;
    private Activity activity;
    public int position = 0;
    public int carType = 0;
    private int selectedCarPosition = 0;
    private AddCarFragment frag;
    private Dialog dialog;
    private ArrayList<LanguageInfo> languageList;
    // declare the languages full name string array
    private String[] languageName = {"English", "Deutsch", "Français", "Italiano", "Español", "Nederlands", "Русский", "Svenska", "Norsk", "Polski", "Suomi", "Português"};
    // declare the language sort name string array
    private String[] languageShortName = {"en", "de", "fr", "it", "es", "nl", "ru", "sv", "no", "pl", "fi", "pt"};
    // declare the language flag int array
    private int[] languageImage = {R.drawable.united_kingdom, R.drawable.germany, R.drawable.france, R.drawable.italy, R.drawable.spain, R.drawable.netherlands, R.drawable.russia, R.drawable.sweden, R.drawable.norway, R.drawable.poland, R.drawable.finland, R.drawable.portugal};
    public String lang = "";
    private Resources resources;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil; // declare the native internal storage
    private String[] carNames;
    public ProgressDialog progressDialog;
    private String internetCheckMessage = "";


    public CarDownloadSettingsAdapter(AddCarFragment frag, Activity activity, Context context, ArrayList<CarInfo> list) {
        this.activity = activity;
        this.context = context;
        this.list = list;
        this.frag = frag;
        inflater = LayoutInflater.from(this.context);
        commonDao = CommonDao.getInstance();
        preferenceUtil = new PreferenceUtil(context);

        loadResource();
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
            NissanApp.getInstance().setPreviousCarImage(list.get(position).getId(), viewHolder.imageView);
            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            if (list.get(position).getId() == 1 || list.get(position).getId() == 2 || list.get(position).getId() == 4 || list.get(position).getId() == 5) {
                String name[] = (carNames[list.get(position).getId() - 1]).split(" ");
                viewHolder.txtViewTitle.setText(name[0]);
            } else {
                viewHolder.txtViewTitle.setText(carNames[list.get(position).getId() - 1]);
            }
        } else if ("1".equalsIgnoreCase(list.get(position).getStatus())) {
            NissanApp.getInstance().setCarImage(list.get(position).getId(), viewHolder.imageView);
            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.orange));
            viewHolder.txtViewTitle.setText(carNames[list.get(position).getId() - 1]);
        } else if ("0".equalsIgnoreCase(list.get(position).getStatus())) {
            NissanApp.getInstance().setCarImage(list.get(position).getId(), viewHolder.imageView);
            viewHolder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
/*            if (list.get(position).getId() == 1 || list.get(position).getId() == 2 || list.get(position).getId() == 4 || list.get(position).getId() == 5) {
                String name[] = (carNames[list.get(position).getId() - 1]).split(" ");
                viewHolder.txtViewTitle.setText(name[0]);
            } else {
                viewHolder.txtViewTitle.setText(carNames[list.get(position).getId() - 1]);
            }*/
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

        /**
         * set selected car border and delete icon visible invisible
         */
        if (list.get(position).getSelectedCar() == 1 && "1".equalsIgnoreCase(list.get(position).getStatus())) {
            selectedCarPosition = position;
            viewHolder.imageViewBorder.setVisibility(View.VISIBLE);
            viewHolder.imgDeleteOrDownload.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.imageViewBorder.setVisibility(View.GONE);
            viewHolder.imgDeleteOrDownload.setVisibility(View.VISIBLE);
        }

        viewHolder.imgDeleteOrDownload.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_delete_or_download:
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
                            if (list.get(position).getId() == 1 || list.get(position).getId() == 2 || list.get(position).getId() == 4 || list.get(position).getId() == 5) {
                                showCarDownloadDialog(list.get(position).getId());
                            } else {
                                LanguageSelectionDialog(list.get(position).getId());
                            }
                        } else {
//                            NissanApp.getInstance().showInternetAlert(activity, context.getResources().getString(R.string.internet_connect));
                            NissanApp.getInstance().showInternetAlert(activity, internetCheckMessage.isEmpty() ? context.getResources().getString(R.string.internet_connect) : internetCheckMessage);
                        }
                    } else {

                    }
                }

                break;

            default:
                break;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ImageView imageViewBorder;
        TextView txtViewTitle;
        ImageButton imgDeleteOrDownload;
        RelativeLayout relativeLayoutSection;
        RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.img_view);
            imageViewBorder = (ImageView) view.findViewById(R.id.img_view_border);
            txtViewTitle = (TextView) view.findViewById(R.id.txt_title);
            imgDeleteOrDownload = (ImageButton) view.findViewById(R.id.img_btn_delete_or_download);
            relativeLayoutSection = (RelativeLayout) view.findViewById(R.id.relative_section);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_car_download);
        }
    }

    public void startCarAssetsDownload(String assetsSource, String assetsDestination, final String langSource, String langDestination, final boolean isForceDownload) {
        new MADownloadManager(activity, context).downloadCarAssets(false, NissanApp.getInstance().getCarName(carType), assetsSource, assetsDestination, langSource, langDestination, new DownloaderStatus() {
            @Override
            public boolean onComplete(boolean status) {
                if (status) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.setMessage(activity.getResources().getString(R.string.data_syncing));
                            }

                            new SearchDBAsync(activity, lang, carType) {

                                @Override
                                public void onComplete(boolean status) {

                                    if (status) {

                                        new ApiCall().postCarDownloadConfirmation("" + carType, "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(context), new CompleteAPI() {
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
                                                                    CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime().toString(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

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
                                                                    CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime().toString(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

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

                                                    Logger.error("Car type " + carType, "___Position___" + position);

                                                    commonDao.updateLanguageStatus(context, carType, lang);
                                                    commonDao.updateAllPushContentStatusForSingleCar(context, carType, NissanApp.getInstance().getLanguageID(lang));
                                                    new PreferenceUtil(context).setSelectedLang(commonDao.getLanguageStatus(context, Values.carType));
                                                    ((MainActivity) activity).sendMsgToGoogleAnalytics(((MainActivity) activity).getAnalyticsFromSettings(Analytics.CAR_SELECTION + Analytics.DOWNLOAD));

                                                    frag.loadResource();
                                                    loadResource();
                                                    adapterNotify(false);

                                                    if (progressDialog != null) {
                                                        progressDialog.dismiss();
                                                    }

                                                } else {

                                                    if (progressDialog != null)
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
                    });
                }
                return false;
            }

            @Override
            public int onError(int i) {
                errorFileDelete(carType);
                return 0;
            }

            @Override
            public boolean internetConnection(boolean b) {
                errorFileDelete(carType);
                return false;
            }

            @Override
            public boolean urlReachable(boolean b) {
                errorFileDelete(carType);
                return false;
            }

            @Override
            public boolean destinationExists(boolean b) {
                errorFileDelete(carType);
                return false;
            }

            @Override
            public boolean sourcePath(boolean b) {
                errorFileDelete(carType);
                return false;
            }

            @Override
            public boolean destinationPath(boolean b) {
                errorFileDelete(carType);
                return false;
            }

            @Override
            public void downloadCompletion(Float aFloat) {

                String formattedString = String.format("%.02f", aFloat);
                if (progressDialog != null) {
                    progressDialog.setMessage(carNames[carType - 1] + "\n" + activity.getResources().getString(R.string.alert_downloading) + formattedString + "%");
                }
            }

            @Override
            public void init() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialogController(activity).downloadProgress(carNames[carType - 1] + "\n" + activity.getResources().getString(R.string.alert_downloading));
                }
            }
        });
    }

    private void changeGlobalAlertMsg(){

        new ApiCall().postGlobalAlertMsg("e224fb09fb8daee4", NissanApp.getInstance().getLanguageID(lang)+"", new CompleteAlertAPI() {
            @Override
            public void onDownloaded(GlobalMsgResponse responseInfo) {
                if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {

                    String key_global_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.GLOBAL_MSG_KEY;
                    String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.GLOBAL_ALERT_MSG_KEY;

                    preferenceUtil.storeMultiLangData(responseInfo.getAlertMessage(), key_global_alert_message);
                    preferenceUtil.storeMultiLangData(responseInfo.getGlobalMessage(), key_global_message);

                    NissanApp.getInstance().setGlobalMessageArrayList(responseInfo.getGlobalMessage());
                    NissanApp.getInstance().setAlertMessageGlobalArrayList(responseInfo.getAlertMessage());

                    startCarDownloadProcedure();

                }
            }

            @Override
            public void onFailed(String failedReason) {
                Logger.error("changeGlobal", "********Fail******" + failedReason);
            }
        });
    }

    private void showCarDownloadDialogForSingleCar(final int carType, final boolean isCarDownload) {
        this.carType = carType;
        final Dialog dialog = new DialogController(activity).langDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        if (isCarDownload) {
            txtViewTitle.setText(context.getResources().getString(R.string.alert_msg22));
        } else {
            txtViewTitle.setText(getAlertMessage(preferenceUtil.getSelectedLang(), Values.DELETE_MESSAGE).isEmpty() ? context.getResources().getString(R.string.alert_msg23) : getAlertMessage(preferenceUtil.getSelectedLang(), Values.DELETE_MESSAGE));
        }

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

                // if isCarDownload = true then start car downloading process
                if (isCarDownload) {

                    changeGlobalAlertMsg();

                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog = new ProgressDialogController(activity).showDialog(resources.getString(R.string.start_car_delete));
                        }
                    });

                    new ApiCall().postCarDelete("" + carType, "" + NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(context, carType)), "0", NissanApp.getInstance().getDeviceID(activity.getApplicationContext()), new CompleteAPI() {
                        @Override
                        public void onDownloaded(ResponseInfo responseInfo) {
                            if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                                try {
                                    FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(list.get(position).getId())));
                                    ((MainActivity) activity).sendMsgToGoogleAnalytics(((MainActivity) activity).getAnalyticsForDelete(carNames[list.get(position).getId() - 1], Analytics.CAR_SELECTION + Analytics.DELETE));
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
                                                commonDao.updateDateAndStatus(context, list.get(position).getId(), "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                list.get(position).setStatus("0");
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
                            frag.showErrorDialog(failedReason);
                            Logger.error("Car deleting", "____________" + failedReason);
                        }
                    });
                }

            }
        });

        dialog.show();
    }

    private void startCarDownloadProcedure() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressDialog = new ProgressDialogController(activity).showDialog(resources.getString(R.string.start_download));

            }
        });

        new ApiCall().postCarDownload("" + carType, "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(context), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                String old_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TUTORIAL_KEY;
                String old_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TAB_MENU_KEY;
                String new_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TUTORIAL_KEY;
                String new_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TAB_MENU_KEY;

                preferenceUtil.deleteMultiLangData(old_key_tab);
                preferenceUtil.deleteMultiLangData(old_key_tutorial);
                preferenceUtil.storeMultiLangData(responseInfo.getTutorials(), new_key_tutorial);
                preferenceUtil.storeMultiLangData(responseInfo.getTabMenu(), new_key_tab);



                if (AppConfig.IS_APP_ONLINE ? Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getAssetsUrl()) && !TextUtils.isEmpty(responseInfo.getLangUrl()) : Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                    startCarAssetsDownload(AppConfig.IS_APP_ONLINE ? responseInfo.getAssetsUrl() : NissanApp.getInstance().getAssetsURL(carType), Values.PATH, AppConfig.IS_APP_ONLINE ? responseInfo.getLangUrl() : NissanApp.getInstance().getLanguageURL((carType), lang), NissanApp.getInstance().getCarPath(carType), false);
                } else {
                    frag.showErrorDialog("Status OR URL not reachable");
                }

            }

            @Override
            public void onFailed(String failedReason) {
                frag.showErrorDialog(failedReason);
            }
        });

    }

    private void showCarDownloadDialog(final int carType) {
        final Dialog dialog = new DialogController(activity).carDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);

        String textMsg = getAlertMessage(preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_2);

        if (TextUtils.isEmpty(textMsg))
            textMsg = activity.getResources().getString(R.string.download_msg);

        txtViewTitle.setText(textMsg);

        ImageButton btnEUR = (ImageButton) dialog.findViewById(R.id.btn_eur);
        ImageButton btnRUS = (ImageButton) dialog.findViewById(R.id.btn_rus);

        ImageButton imgBtnEur = (ImageButton) dialog.findViewById(R.id.img_btn_eur_delete_donwload);
        ImageButton imgBtnRus = (ImageButton) dialog.findViewById(R.id.img_btn_rus_delete_donwload);

        Logger.error("Car Type is ", "_____________" + carType);

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
        }

        btnEUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 1 || carType == 4) {
                    LanguageSelectionDialog(carType);
                } else {
                    LanguageSelectionDialog(carType - 1);
                }
            }
        });

        btnRUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 2 || carType == 5) {
                    LanguageSelectionDialog(carType);
                } else {
                    LanguageSelectionDialog(carType + 1);
                }
            }
        });


        imgBtnEur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 1 || carType == 4) {
                    if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                        position++;
                        showCarDownloadDialogForSingleCar(carType, false);
                    } else {
                        LanguageSelectionDialog(carType);
                    }
                } else {
                    if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType - 1)) && commonDao.getStatus(context, carType - 1) == 1) {
                        position--;
                        showCarDownloadDialogForSingleCar(carType - 1, false);
                    } else {
                        LanguageSelectionDialog(carType - 1);
                    }
                }
            }
        });

        imgBtnRus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 2 || carType == 5) {
                    if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType)) && commonDao.getStatus(context, carType) == 1) {
                        position--;
                        showCarDownloadDialogForSingleCar(carType, false);
                    } else {
                        LanguageSelectionDialog(carType);
                    }
                } else {
                    if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 1)) && commonDao.getStatus(context, carType + 1) == 1) {
                        position++;
                        showCarDownloadDialogForSingleCar(carType + 1, false);
                    } else {
                        LanguageSelectionDialog(carType + 1);
                    }
                }
            }
        });

        dialog.show();
    }

    private void LanguageSelectionDialog(final int carType) {
        dialog = new DialogController(activity).languageSelectionDialog();
        ListView lstView = (ListView) dialog.findViewById(R.id.lst_view);

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                LanguageInfo info = (LanguageInfo) parent.getAdapter().getItem(position);
                lang = languageShortName[info.getId()];
                showCarDownloadDialogForSingleCar(carType, true);
            }
        });

        languageList = new ArrayList<>();

        for (int i = 0; i < languageName.length; i++) {
            LanguageInfo info = new LanguageInfo(i, languageName[i], false, languageImage[i]);
            /*if (carType == 2 || carType == 5) {
                if (i == 0 || i == 6) {
                    languageList.add(info);
                }
            } else if (carType == 14) {
                if (i == 0) {
                    languageList.add(info);
                    break;
                }
            } else {
                if (carType == 7 || carType == 8 || carType == 9) {
                    if (i != 6) {
                        languageList.add(info);
                    }
                } else {
                    languageList.add(info);
                }
            }*/

            if (carType == 2 || carType == 5) {
                if (i == 0 || i == 6) {
                    languageList.add(info);
                }
            } else {
                if (carType == 7 || carType == 8 || carType == 9) {
                    if (i != 6 && i != 8) {
                        languageList.add(info);
                    }
                } else if (carType == 1 || carType == 3 || carType == 4 || carType == 6 || carType == 10 || carType == 11 || carType == 12 || carType == 13) {
                    if (i != 8) {
                        languageList.add(info);
                    }
                } else {
                    languageList.add(info);
                }
            }
        }
        lstView.setAdapter(new LanguageSelectionAdapter(context, languageList, false));
        lstView.setDivider(null);
        ColorDrawable sage = new ColorDrawable(context.getResources().getColor(R.color.line_color));
        lstView.setDivider(sage);
        lstView.setDividerHeight(4);

        dialog.show();
    }

    private void errorFileDelete(int carType) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(carType)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            CarDownloadSettingsAdapter.this.notifyDataSetChanged();
            adapterNotify(true);
        }
    }

    public void loadResource() {
        resources = new Resources(activity.getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(activity, preferenceUtil.getSelectedLang()));
        carNames = activity.getResources().getStringArray(R.array.car_names);
        internetCheckMessage = getAlertMessage(preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
    }

    public void adapterNotify(boolean isDelete) {

        boolean isDownloaded = true;
        boolean isAvailable = true;
        boolean isPrevious = true;

        Collections.sort(list, new Comparator<CarInfo>() {
            @Override
            public int compare(CarInfo o1, CarInfo o2) {
                return o1.getId() - o2.getId();
            }
        });


        Collections.sort(list, new Comparator<CarInfo>() {
            @Override
            public int compare(CarInfo o1, CarInfo o2) {
                if (o1.getId() >= 11) {
                    return o2.getId() - o1.getId();
                } else {
                    return 0;
                }
            }
        });

        final String ORDER = "102";
        Collections.sort(list, new Comparator<CarInfo>() {
            @Override
            public int compare(CarInfo o1, CarInfo o2) {
                return ORDER.indexOf(o1.getStatus()) - ORDER.indexOf(o2.getStatus());
            }
        });

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

            } else {

            }
        }

        CarDownloadSettingsAdapter.this.notifyDataSetChanged();
        if (!isDelete) {
            frag.lstView.smoothScrollToPosition(index);
        } else {
            frag.lstView.smoothScrollBy(2, 10);
        }
    }

    private String getAlertMessage(String lang_short_name, String msg_type) {

        String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang_short_name) + "_" + Values.GLOBAL_ALERT_MSG_KEY;

        List<AlertMessage> alertMessageArrayList = NissanApp.getInstance().getAlertMessageGlobalArrayList();
        if (alertMessageArrayList == null || alertMessageArrayList.size() == 0) {
            Type type = new TypeToken<ArrayList<AlertMessage>>() {
            }.getType();
            alertMessageArrayList = new Gson().fromJson(new PreferenceUtil(activity).retrieveMultiLangData(key_global_alert_message), type);
            NissanApp.getInstance().setAlertMessageGlobalArrayList(alertMessageArrayList);
        }


        for (int i = 0; i < alertMessageArrayList.size(); i++) {

            if (msg_type.equalsIgnoreCase(Values.ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_2) && alertMessageArrayList.get(i).getType().equalsIgnoreCase(Values.ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_2))
                return alertMessageArrayList.get(i).getMsg();
            if (msg_type.equalsIgnoreCase(Values.DELETE_MESSAGE) && alertMessageArrayList.get(i).getType().equalsIgnoreCase(Values.DELETE_MESSAGE))
                return alertMessageArrayList.get(i).getMsg();
            if (msg_type.equalsIgnoreCase(Values.ALERT_MSG_TYPE_INTERNET) && alertMessageArrayList.get(i).getType().equalsIgnoreCase(Values.ALERT_MSG_TYPE_INTERNET))
                return alertMessageArrayList.get(i).getMsg();
        }

        return "";
    }

}
