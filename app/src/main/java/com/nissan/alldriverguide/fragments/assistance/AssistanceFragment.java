package com.nissan.alldriverguide.fragments.assistance;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.AssistanceAdapter;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.combimeter.CombimeterFragment;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAssistanceTabContent;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.Datum;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.SingleContentUpdating;
import com.nissan.alldriverguide.utils.Values;

import java.util.List;

public class AssistanceFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Context context;
    public Resources resources;
    private String[] assistanceArray;
    private int[] assistanceImage = {R.drawable.warning_light, R.drawable.quick_reference, R.drawable.tyre, R.drawable.engine_compartment, R.drawable.warranty, R.drawable.selled};
    private View view;
    private TextView txtViewCarName;
    private TextView txtViewDriverGuide;
    private ImageView imageView;
    private ListView lstView;
    private TextView txt_title;
    private AssistanceAdapter adapter;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil;
    private AssistanceInfo assistanceInfo;

    private CommonDao commonDao;
    private ProgressDialog progressDialog;

    private long mLastClickTime = 0;
    private long doubleClickPopup = 0;

    public static Fragment newInstance() {
        Fragment frag = new AssistanceFragment();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assistance, container, false);

        initViews(view);
        loadResource();
        setListener();
        postAssistanceData();
        return view;
    }

    // here load the initial data
    private void loadData() {
        txtViewDriverGuide.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "font/Nissan Brand Regular.otf"));
//        txtViewDriverGuide.setText(resources.getString(R.string.driver_guide));
        txtViewDriverGuide.setText(assistanceInfo.getAssistanceTitle());
//        txt_title.setText(resources.getString(R.string.assistance));
//        txt_title.setText(resources.getString(R.string.assistance));
        txt_title.setText(NissanApp.getInstance().getTabMenuArrayList().get(1).getTitle());
//        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), resources.getStringArray(R.array.assistance_array), assistanceImage);
        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), assistanceArray, assistanceImage);
        lstView.setAdapter(adapter);

        if (Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
//            txtViewCarName.setText(resources.getStringArray(R.array.car_names)[Values.carType - 1]);
            txtViewCarName.setText(assistanceInfo.getSelectedCar());
        } else {
//            txtViewCarName.setText("NISSAN " + resources.getStringArray(R.array.car_names)[Values.carType - 1]);
            txtViewCarName.setText("NISSAN " + assistanceInfo.getSelectedCar());
        }
        setCarBackground(Values.carType);
    }

    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    // here set assistance car background according to car type
    private void setCarBackground(int index) {
        NissanApp.getInstance().setCarImageAssistance(index, imageView);
    }

    // here initialized all variable
    private void initViews(View view) {
        context = getActivity().getApplicationContext();
        commonDao = CommonDao.getInstance();

        txtViewCarName = (TextView) view.findViewById(R.id.txt_view_car_name);
        txtViewDriverGuide = (TextView) view.findViewById(R.id.txt_view_driver_guide);
        imageView = (ImageView) view.findViewById(R.id.img_car_bg);
        lstView = (ListView) view.findViewById(R.id.lst_view);
        txt_title = (TextView) view.findViewById(R.id.txt_title);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity().getApplicationContext()).setOpenCountForRateApp();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void postAssistanceData() {
        new ApiCall().postAssistanceTabContent(NissanApp.getInstance().getDeviceID(getActivity()), "1", String.valueOf(Values.carType), "0", "2", new CompleteAssistanceTabContent() {
            @Override
            public void onDownloaded(AssistanceInfo responseInfo) {

                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

                    if (preferenceUtil.retrieveAssistanceData(Values.ASSISTANCE_OBJ_STORE_KEY) == null) {
                        preferenceUtil.storeAssistanceData(responseInfo, Values.ASSISTANCE_OBJ_STORE_KEY);
                        NissanApp.getInstance().setAssistanceInfo(preferenceUtil.retrieveAssistanceData(Values.ASSISTANCE_OBJ_STORE_KEY));
                    } else {
                        NissanApp.getInstance().setAssistanceInfo(preferenceUtil.retrieveAssistanceData(Values.ASSISTANCE_OBJ_STORE_KEY));
                    }

                    if (assistanceInfo == null) {
                        assistanceInfo = preferenceUtil.retrieveAssistanceData(Values.ASSISTANCE_OBJ_STORE_KEY);
                        List<Datum> list = assistanceInfo.getData();
                        assistanceArray = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            assistanceArray[i] = list.get(i).getTitle();
                        }
                    }

                    loadData();
                }
            }

            @Override
            public void onFailed(String failedReason) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // here set the epub type for all assistance list item (eg. Warning Light, QRG, Tyre Information and more...)
        Values.ePubType = position + 1;

        if (DetectConnection.checkInternetConnection(getActivity().getApplicationContext())) {
            PushContentInfo info = commonDao.getNotificationData(getActivity().getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()), Values.ePubType);

            if (info != null && !TextUtils.isEmpty(info.getCarId()) && !TextUtils.isEmpty(info.getLangId()) && !TextUtils.isEmpty(info.getePubId())) {
                Logger.error("You have a update", "Do you want ?");

                final Dialog dialog = new DialogController(getActivity()).contentUpdateDialog();

                TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
                txtViewTitle.setText(resources.getString(R.string.update_msg));

                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
                btnOk.setText(resources.getString(R.string.button_YES));

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
                                    progressDialog = new ProgressDialogController(getActivity()).showDialog(getResources().getString(R.string.start_download));
                                }
                            });

                            new ApiCall().postContentDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()), "" + Values.ePubType, NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
                                @Override
                                public void onDownloaded(ResponseInfo responseInfo) {
//                                Logger.error("status", "__________" + responseInfo.getStatusCode() + "____" + responseInfo.getMessage() + "____" + responseInfo.getUrl());;

                                    if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getUrl())) {
                                        new MADownloadManager(getActivity(), getActivity().getApplicationContext()).downloadLanguage(false, "Language", responseInfo.getUrl(), NissanApp.getInstance().getCarPath(Values.carType), new DownloaderStatus() {
                                            @Override
                                            public boolean onComplete(boolean b) {
                                                Logger.error("downloadManager", "__________" + b);
                                                if (b) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (progressDialog != null) {
                                                                progressDialog.setMessage(getResources().getString(R.string.data_syncing));
                                                            }

                                                            new SingleContentUpdating(getActivity(), preferenceUtil.getSelectedLang(), Values.carType) {
                                                                @Override
                                                                public void onComplete(boolean status) {
                                                                    Logger.error("status_here", "__________" + status);

                                                                    if (status) {
                                                                        new ApiCall().postContentDownloadConfirmation("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()), "" + Values.ePubType, NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
                                                                            @Override
                                                                            public void onDownloaded(ResponseInfo responseInfo) {
                                                                                Logger.error("confirmation", "__________" + responseInfo.getStatusCode() + "____" + responseInfo.getMessage() + "____" + responseInfo.getUrl());

                                                                                if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                                                                                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromSettings(Analytics.CHANGE_LANGUAGE + Analytics.DOWNLOAD));

                                                                                    commonDao.updateLanguageStatus(getActivity().getBaseContext(), Values.carType, preferenceUtil.getSelectedLang());
                                                                                    commonDao.deleteSingleCarEpub(getActivity(), Values.carType);
                                                                                    commonDao.updatePushContentStatus(getActivity().getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang()), Values.ePubType);

                                                                                    if (progressDialog != null)
                                                                                        progressDialog.dismiss();

                                                                                    loadDesireFragment(position);
                                                                                } else {
                                                                                    errorFileDelete(Values.carType);
                                                                                    showErrorDialog("Confirmation send failed error!");
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailed(String failedReason) {
                                                                                Logger.error("onFailed", "__________" + failedReason);
                                                                                errorFileDelete(Values.carType);
                                                                                showErrorDialog(failedReason);
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
                                                    Logger.error("problem", "______assetDownload-LanguageFragment");
                                                }
                                                return false;
                                            }

                                            @Override
                                            public int onError(int i) {
                                                showErrorDialog("Error ! Unable to update content, Please try again.");
                                                errorFileDelete(Values.carType);
                                                return 0;
                                            }

                                            @Override
                                            public boolean internetConnection(boolean b) {
                                                showErrorDialog("No internet connection, please try again");
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
                                                    progressDialog.setMessage(getActivity().getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete) + formattedString + "%");
                                                }
                                            }

                                            @Override
                                            public void init() {
                                                if (progressDialog == null) {
                                                    progressDialog = new ProgressDialogController(getActivity()).downloadProgress(getActivity().getResources().getStringArray(R.array.car_names)[Values.carType - 1] + "\n" + getResources().getString(R.string.alert_download_complete));
                                                }
                                            }
                                        });
                                    } else {
                                        showErrorDialog("No content found.");
                                        errorFileDelete(Values.carType);
                                    }
                                }

                                @Override
                                public void onFailed(String failedReason) {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
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
                        if (SystemClock.elapsedRealtime() - doubleClickPopup < 1000) {
                            return;
                        }
                        doubleClickPopup = SystemClock.elapsedRealtime();

                        dialog.dismiss();
                        loadDesireFragment(position);
                    }
                });

                dialog.show();
            } else {
                loadDesireFragment(position);
            }
        } else {
            loadDesireFragment(position);
        }
    }

    /**
     * This method for indicate fragment that contain in assistance tab
     *
     * @param position comparing for redirect fragment
     */
    private void loadDesireFragment(int position) {
        Fragment frag = null;
        switch (position) {
            case 0:
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.WARNING_LIGHT));
                frag = CombimeterFragment.newInstance();
                break;

            case 1:
                if (Values.carType == 14) {
                    frag = HomePageFragment.newInstance((resources.getStringArray(R.array.assistance_array))[position]);
                } else {
                    frag = ListFragment.newInstance((resources.getStringArray(R.array.assistance_array))[position]);
                }
                break;

            case 2:
                frag = ListFragment.newInstance((resources.getStringArray(R.array.assistance_array))[position]);
                break;

            case 3:
                frag = ListFragment.newInstance((resources.getStringArray(R.array.assistance_array))[position]);
                break;

            case 4:
                frag = ListFragment.newInstance((resources.getStringArray(R.array.assistance_array))[position]);
                break;

            case 5:
//                frag = NissanAssistanceFragment.newInstance(resources.getStringArray(R.array.assistance_array)[position]);
                frag = NissanAssistanceFragment.newInstance(assistanceArray[position]);
                break;

            default:
                break;
        }

        if (frag != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            ft.replace(R.id.container, frag);
            ft.addToBackStack(Values.tabAssistance);
            ft.commit();
        }
    }

    private void errorFileDelete(int carType) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "error_fragment");
    }
}
