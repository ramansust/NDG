package com.nissan.alldriverguide.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
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
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.CarInfo;
import com.nissan.alldriverguide.model.LanguageInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.interfaces.InterfaceLanguageListResponse;
import com.nissan.alldriverguide.multiLang.model.CarList;
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
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.STARTING_DOWNLOAD;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

/**
 * Created by raman on 1/19/17.
 */
public class CarDownloadSettingsAdapter extends BaseAdapter implements View.OnClickListener, InterfaceGlobalMessageResponse, CarListACompleteAPI, InterfaceLanguageListResponse {

    private static final String TAG = "CarDownloadSettingsAdapter";
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
    private String[] languageName;/*= {"English", "Deutsch", "Français", "Italiano", "Español", "Nederlands", "Русский", "Svenska", "Norsk", "Polski", "Suomi", "Português"};*/
    // declare the language sort name string array
    private String[] languageShortName; /*= {"en", "de", "fr", "it", "es", "nl", "ru", "sv", "no", "pl", "fi", "pt"};*/
    // declare the language flag int array
    private int[] languageImage; /*= {R.drawable.united_kingdom, R.drawable.germany, R.drawable.france, R.drawable.italy, R.drawable.spain, R.drawable.netherlands, R.drawable.russia, R.drawable.sweden, R.drawable.norway, R.drawable.poland, R.drawable.finland, R.drawable.portugal};*/
    public String lang = "";
    private Resources resources;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil; // declare the native internal storage
    private String[] carNames;
    public ProgressDialog progressDialog;
    private String internetCheckMessage = "";
    private String deviceDensity;
    List<LanguageList> languageLists = new ArrayList<>();
    private GlobalMessageController controller;
    private CarListContentController carListContentController;
    private LanguageSelectionController controllerLanguageSelection;
    private int selectedCarType = -1;
    private long mLastClickTime = 0;
    private List<CarList> carListArrayList = new ArrayList<>();
    private Typeface typeFaceBold;

    public CarDownloadSettingsAdapter(AddCarFragment frag, Activity activity, Context context, ArrayList<CarInfo> list) {
        this.activity = activity;
        this.context = context;
        this.list = list;
        this.frag = frag;
        inflater = LayoutInflater.from(this.context);
        commonDao = CommonDao.getInstance();
        preferenceUtil = new PreferenceUtil(context);
        loadResource();
        deviceDensity = NissanApp.getInstance().getDensityName(this.activity);
        getDataFromSP();
        typeFaceBold = Typeface.createFromAsset(context.getAssets(), "font/Nissan Brand Bold.otf");
    }

    private void getCarList(String carId) {

        Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        languageLists = new Gson().fromJson(new PreferenceUtil(context).retrieveMultiLangData(carId + "_" + Values.CAR_LANGUAGE_LIST), type);

//        showLanguageDialog(Integer.parseInt(carId));

        if (languageLists == null || languageLists.size() == 0) {
            if (DetectConnection.checkInternetConnection(context)) {
                controllerLanguageSelection = new LanguageSelectionController(this);
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

        viewHolder.txtViewTitle.setTypeface(typeFaceBold);


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
                String name[] = list.get(position).getName().split(" "); //(carNames[list.get(position).getId() - 1]).split(" ");
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
                String name[] = list.get(position).getName().split(" "); //(carNames[list.get(position).getId() - 1]).split(" ");
                viewHolder.txtViewTitle.setText(name[0]);
//                viewHolder.txtViewTitle.setText(list.get(position).getName());
            } else if(list.get(position).getId() == 13 || list.get(position).getId() == 15){
                viewHolder.txtViewTitle.setText("NISSAN X-TRAIL");
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
                            if (list.get(position).getId() == 1 || list.get(position).getId() == 2 || list.get(position).getId() == 4 || list.get(position).getId() == 5 || list.get(position).getId() == 13 || list.get(position).getId() == 15) {
                                showCarDownloadDialog(list.get(position).getId());
                            } else {
                                selectedCarType = list.get(position).getId();
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

    @Override
    public void onDownloaded(GlobalMsgResponse responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

            String key_global_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.GLOBAL_MSG_KEY;
            String key_global_alert_message = Values.carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.GLOBAL_ALERT_MSG_KEY;

            preferenceUtil.storeMultiLangData(responseInfo.getAlertMessage(), key_global_alert_message);
            preferenceUtil.storeMultiLangData(responseInfo.getGlobalMessage(), key_global_message);

            NissanApp.getInstance().setGlobalMessageArrayList(responseInfo.getGlobalMessage());
            NissanApp.getInstance().setAlertMessageGlobalArrayList(responseInfo.getAlertMessage());

        }
    }

    @Override
    public void onDownloaded(CarListResponse responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
            String car_list_key = lang + "_" + Values.CAR_LIST_KEY + "_" + NissanApp.getInstance().getLanguageID(lang);
            NissanApp.getInstance().setCarListWAP(responseInfo.getCarList());
            preferenceUtil.storeMultiLangData(responseInfo.getCarList(), car_list_key);
        } else
            Logger.error(TAG, "CarListResponse_status________" + responseInfo.getStatusCode());
    }

    @Override
    public void onFailed(String failedReason) {
        Logger.error(TAG, "globalMsg____________" + failedReason);
    }

    @Override
    public void languageListDownloaded(List<LanguageList> languageListsTemp) {

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

    @Override
    public void languageListFailed(String failedResponse) {
        Logger.error(TAG, "languageListFailed__________" + failedResponse);
    }

    static class ViewHolder {
        SimpleDraweeView imageView;
        ImageView imageViewBorder;
        TextView txtViewTitle;
        ImageButton imgDeleteOrDownload;
        RelativeLayout relativeLayoutSection;
        RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            imageView = (SimpleDraweeView) view.findViewById(R.id.ivMainCarImage);
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
                        @SuppressLint("StaticFieldLeak")
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
                                                    } else if(carType == 13) {//click eur/rus rohan
                                                        if (commonDao.getStatus(context, carType + 2) == 0) {
                                                            commonDao.updateDateAndStatus(context, carType + 2, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
                                                        }
                                                    } else if (carType == 15) {
//                                                        if (commonDao.getStatus(context, carType - 2) == 0) {
//                                                            commonDao.updateDateAndStatus(context, carType - 2, "0", NissanApp.getInstance().getDateTime(), "EUR", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
//                                                        }
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

                                                    } else if (carType == 13 || carType == 15){

                                                        if (carType == 13) {
                                                            if (commonDao.getStatus(context, carType + 2) == 1) {

                                                                list.get(selectedCarPosition).setSelectedCar(0);
                                                                list.get(position).setSelectedCar(1);
                                                                list.get(position).setName(commonDao.getCarName(context, carType));
                                                                list.get(position).setStatus("1");
                                                            } else {

                                                                if (!isForceDownload) {

                                                                    CarInfo info = new CarInfo(carType, commonDao.getCarName(context, carType), "1", NissanApp.getInstance().getDateTime().toString(), "EUR", new PreferenceUtil(context).getSelectedLang(), 1, NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());

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
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                return false;
            }

            @Override
            public int onError(int i) {
                errorFileDelete(carType);
                showErrorDialog(context.getResources().getString(R.string.internet_connection_interruption));
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

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "error_fragment");
    }

    private void showCarDownloadDialogForSingleCar(final int carType, final boolean isCarDownload) {

        controller = new GlobalMessageController(this);

        this.carType = carType;
        final Dialog dialog = new DialogController(activity).carDownloadDialog();

        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        TextView txtViewHeader = dialog.findViewById(R.id.txt_header);
        if (isCarDownload) {
            String downloadConfirmationMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.DOWNLOAD_CONFIRMATION);
            txtViewTitle.setText(downloadConfirmationMsg == null || downloadConfirmationMsg.isEmpty() ? resources.getString(R.string.alert_msg22) : downloadConfirmationMsg);
            txtViewHeader.setText(activity.getResources().getString(R.string.download));
        } else {
            String deleteMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.DELETE_MESSAGE);
            txtViewTitle.setText(deleteMsg == null || deleteMsg.isEmpty() ? resources.getString(R.string.alert_msg23) : deleteMsg);
            txtViewHeader.setText(activity.getResources().getString(R.string.delete));
        }

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

                // if isCarDownload = true then start car downloading process
                if (isCarDownload) {

                    controller.callApi(NissanApp.getInstance().getDeviceID(context), NissanApp.getInstance().getLanguageID(lang) + "");
                    startCarDownloadProcedure();

                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String car_delete_msg = NissanApp.getInstance().getAlertMessage(context, lang, Values.CAR_DELETE);
                            progressDialog = new ProgressDialogController(activity).showDialog(car_delete_msg == null || car_delete_msg.isEmpty() ? resources.getString(R.string.start_car_delete) : car_delete_msg);
                        }
                    });

                    new ApiCall().postCarDelete("" + carType, "" + NissanApp.getInstance().getLanguageID(commonDao.getLanguageStatus(context, carType)), "0", NissanApp.getInstance().getDeviceID(activity.getApplicationContext()), new CompleteAPI() {
                        @Override
                        public void onDownloaded(ResponseInfo responseInfo) {
                            if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                                try {

                                    FileUtils.deleteDirectory(new File(NissanApp.getInstance().getCarPath(list.get(position).getId())));
                                    ((MainActivity) activity).sendMsgToGoogleAnalytics(((MainActivity) activity).getAnalyticsForDelete(carNames[list.get(position).getId() - 1], Analytics.CAR_SELECTION + Analytics.DELETE));

                                    int carIdFromList = list.get(position).getId();

                                    if (carIdFromList == 15) {

                                        if (commonDao.getStatus(context, carIdFromList - 2) == 0) {
                                            commonDao.updateDateAndStatus(context, carIdFromList, "0", NissanApp.getInstance().getDateTime(), "RUS", NissanApp.getInstance().getVersionName(), NissanApp.getInstance().getVersionCode());
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
                                    }



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
                            frag.showErrorDialog(resources.getString(R.string.failed_to_connect_server));
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

                final String startingDownloadMsg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), STARTING_DOWNLOAD);

                progressDialog = new ProgressDialogController(activity).showDialog(startingDownloadMsg.isEmpty() ? resources.getString(R.string.start_download) : startingDownloadMsg);

            }
        });


        carListContentController = new CarListContentController(this);

        carListContentController.callApi(NissanApp.getInstance().getDeviceID(context), NissanApp.getInstance().getLanguageID(lang) + "");


        new ApiCall().postCarDownload("" + carType, "" + NissanApp.getInstance().getLanguageID(lang), "0", NissanApp.getInstance().getDeviceID(context), new CompleteAPI() {
            @Override
            public void onDownloaded(ResponseInfo responseInfo) {

                String old_key_tutorial = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TUTORIAL_KEY;
                String old_key_tab = Values.carType + "_" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()) + "_" + Values.TAB_MENU_KEY;
                String new_key_tutorial = carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TUTORIAL_KEY;
                String new_key_tab = carType + "_" + NissanApp.getInstance().getLanguageID(lang) + "_" + Values.TAB_MENU_KEY;

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

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);

        String downloadCarGuide2Msg = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_DOWNLOAD_CAR_GUIDE_2);

        txtViewTitle.setText(downloadCarGuide2Msg == null || downloadCarGuide2Msg.isEmpty() ? resources.getString(R.string.download_msg) : downloadCarGuide2Msg);

        ImageButton btnEUR = (ImageButton) dialog.findViewById(R.id.btn_eur);
        ImageButton btnRUS = (ImageButton) dialog.findViewById(R.id.btn_rus);

        ImageButton imgBtnEur = (ImageButton) dialog.findViewById(R.id.img_btn_eur_delete_donwload);
        ImageButton imgBtnRus = (ImageButton) dialog.findViewById(R.id.img_btn_rus_delete_donwload);

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
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 2)) && commonDao.getStatus(context, carType + 2) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType -1).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }//click rohan
        } else if (carType == 15) {//click rohan
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType -2 )) && commonDao.getStatus(context, carType-2) == 1) {
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
            if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType )) && commonDao.getStatus(context, carType ) == 1) {
                btnRUS.setAlpha(0.2f);
                btnRUS.setEnabled(false);
                imgBtnRus.setBackgroundResource(R.drawable.delete_selector);
                if (list.get(carType -2).getSelectedCar() == 1) {
                    imgBtnRus.setAlpha(0.2f);
                    imgBtnRus.setEnabled(false);
                } else {
                    imgBtnRus.setEnabled(true);
                }
            } else {
                imgBtnRus.setBackgroundResource(R.drawable.download_selector);
            }//click rohan
        }

        btnEUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 1 || carType == 4 || carType == 13) {//click rohan
                    selectedCarType = carType;
                    LanguageSelectionDialog(carType);
                } else {
                    selectedCarType = carType - 1;
                    LanguageSelectionDialog(carType - 1);
                }
            }
        });

        btnRUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (carType == 2 || carType == 5) {
                    selectedCarType = carType;
                    LanguageSelectionDialog(carType);
                } else if(carType == 13){//click rohan
                    selectedCarType = carType + 2;
                    LanguageSelectionDialog(carType + 2);//click rohan
                } else if(carType == 15){//click rohan
                    selectedCarType = carType;
                    LanguageSelectionDialog(carType);//click rohan
                }else {
                    selectedCarType = carType + 1;
                    LanguageSelectionDialog(carType + 1);
                }
            }
        });


        imgBtnEur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (carType == 1 || carType == 4 || carType == 13) {
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
                        selectedCarType = carType;
                        LanguageSelectionDialog(carType);
                    }
                } else if(carType == 13){//click rohan
                    if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 2)) && commonDao.getStatus(context, carType + 2) == 1) {
                        position--;
                        showCarDownloadDialogForSingleCar(carType + 2, false);//click rohan
                    } else {
                        selectedCarType = carType + 2;//click rohan
                        LanguageSelectionDialog(carType + 2);//click rohan
                    }
                }else {
                    if (NissanApp.getInstance().isFileExists(NissanApp.getInstance().getCarPath(carType + 1)) && commonDao.getStatus(context, carType + 1) == 1) {
                        position++;
                        showCarDownloadDialogForSingleCar(carType + 1, false);
                    } else {
                        selectedCarType = carType + 1;
                        LanguageSelectionDialog(carType + 1);
                    }
                }
            }
        });

        dialog.show();
    }

    private void LanguageSelectionDialog(final int carType) {

        getCarList(carType + "");

    }


    private void showLanguageDialog(final int carType) {

        if (languageLists == null || languageLists.size() == 0)
            languageLists = new ArrayList<>();
        languageList = new ArrayList<>();

        dialog = new DialogController(activity).languageSelectionDialog();
        ListView lstView = (ListView) dialog.findViewById(R.id.lst_view);

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                LanguageInfo info = (LanguageInfo) parent.getAdapter().getItem(position);
                lang = getLanguageShortName(info.getName()); //languageShortName[info.getId()];
                showCarDownloadDialogForSingleCar(carType, true);
            }
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

    private void errorFileDelete(int carType) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
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
        internetCheckMessage = NissanApp.getInstance().getAlertMessage(context, preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
    }

    private void adapterNotify(boolean isDelete) {

        NissanApp.getInstance().setCarAllList(list); //commonDao.getAllCarList(context)); //list); //commonDao.getAllCarList(context));
        replaceTheCarNamesAndImages();
        list = new ArrayList<>();
        list = NissanApp.getInstance().getCarAllList();

        boolean euroAvailable = false;

        for (int i = 0; i <list.size(); i++) {
            CarInfo info = list.get(i);
            if (info.getId() == 13 && info.getStatus().equals("0")) {
                euroAvailable = true;
            }

        }

        for (int i = 0; i <list.size(); i++) {
            CarInfo info = list.get(i);
            if (euroAvailable) {
                if (info.getName().contains("RUS") && info.getId() == 15 && info.getStatus().equals("0"))
                    list.remove(info);
            }

        }

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

//        list = swapXtrailEurRusIfBothDownloaded(list);

        list = swapXtrailRusleaf2017IfBothDownloaded(list);
        list = swapXtrailEurRusIfBothDownloaded(list);

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

    private ArrayList<CarInfo> swapXtrailEurRusIfBothDownloaded(ArrayList<CarInfo> getList) {


        CarInfo xtrailEuroInfo = new CarInfo();
        CarInfo xtrailRusInfo = new CarInfo();
        CarInfo leaf2017Info = new CarInfo();
        int xtrailEuroIndex = -1, xtrailRusIndex = -1, leaf2017Index = -1;
        boolean xtrailRusDownloaded = false, xtrailEuroDownloaded = false, leaf2017Downloaded = false;

        for (int k = 0; k < getList.size(); k++) {
            CarInfo info = getList.get(k);

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


        if (xtrailEuroDownloaded && xtrailRusDownloaded) {
            getList.set(xtrailEuroIndex, xtrailRusInfo);
            getList.set(xtrailRusIndex, xtrailEuroInfo);
        }

//        NissanApp.getInstance().setCarAllList(getList);
        return getList;
    }

    private ArrayList<CarInfo> swapXtrailRusleaf2017IfBothDownloaded(ArrayList<CarInfo> getList) {


        CarInfo xtrailEuroInfo = new CarInfo();
        CarInfo xtrailRusInfo = new CarInfo();
        CarInfo leaf2017Info = new CarInfo();
        int xtrailEuroIndex = -1, xtrailRusIndex = -1, leaf2017Index = -1;
        boolean xtrailRusDownloaded = false, xtrailEuroDownloaded = false, leaf2017Downloaded = false;

        for (int k = 0; k < getList.size(); k++) {
            CarInfo info = getList.get(k);

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


        if (leaf2017Downloaded && xtrailRusDownloaded) {
            getList.set(leaf2017Index, xtrailRusInfo);
            getList.set(xtrailRusIndex, leaf2017Info);
        }

//        NissanApp.getInstance().setCarAllList(getList);
        return getList;
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

    private boolean xtrailLeafAvailableForDownload(ArrayList<CarInfo> getList) {


        boolean isfifteenIdAvailable = false;

        for (int k = 0; k < getList.size(); k++) {
            CarInfo info = getList.get(k);
            if (info.getStatus().equals("0") && info.getId() == 14) {
                if (isfifteenIdAvailable)
                    return true;
            }

            if (info.getStatus().equals("0") && info.getId() == 15) {
                isfifteenIdAvailable = true;
            }

        }
        return false;
    }

}
