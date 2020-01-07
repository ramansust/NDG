package com.nissan.alldriverguide.fragments.assistance;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mobioapp.infinitipacket.callback.DownloaderStatus;
import com.mobioapp.infinitipacket.downloader.MADownloadManager;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.AssistanceAdapter;
import com.nissan.alldriverguide.controller.AssistanceTabContentController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.combimeter.CombimeterFragment;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.interfaces.CompleteAssistanceTabContent;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.model.DealerUrl;
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.Datum;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.retrofit.ApiService;
import com.nissan.alldriverguide.retrofit.RetrofitClient;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.SingleContentUpdating;
import com.nissan.alldriverguide.utils.Values;
import com.vuforia.VIEW;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class AssistanceFragment extends Fragment implements AdapterView.OnItemClickListener, CompleteAssistanceTabContent {
    private static final String TAG = "AssistanceFragment";
    private Context context;
    public Resources resources;
    private String[] assistanceArray;
    private int[] assistanceImage = {R.drawable.warning_light, R.drawable.quick_reference, R.drawable.tyre, R.drawable.engine_compartment, R.drawable.warranty, R.drawable.selled};
    private View view;
    private TextView txtViewCarName;
    private TextView txtViewDriverGuide;
    private SimpleDraweeView imageView;
    private ListView lstView;
    private TextView txt_title;

    private ProgressBar progressBar;
    private TextView tvNoContent,txtView_loadTxt;

    private AssistanceAdapter adapter;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil;
    private AssistanceInfo assistanceInfo;

    private CommonDao commonDao;
    private ProgressDialog progressDialog;

    private long mLastClickTime = 0;
    private long doubleClickPopup = 0;

    private String sharedpref_key;
    private AssistanceTabContentController controller;

    private String url;
    private DealerUrl dealerUrl;

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
        check_Data();
        return view;
    }

    public void check_Data() {

        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), assistanceArray, assistanceImage);
        lstView.setAdapter(adapter);

        sharedpref_key = Values.carType + "_" + Values.ASSISTANCE_OBJ_STORE_KEY;
        assistanceInfo = preferenceUtil.retrieveAssistanceData(sharedpref_key);

        Logger.error("Alert msg" , "---- "  + NissanApp.getInstance().getAlertMessage(getActivity(),preferenceUtil.getSelectedLang(),Values.LOAD_TEXT_TITLE));

        if (assistanceInfo != null && assistanceInfo.getData() != null && assistanceInfo.getData().size() > 0) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.GONE);
            NissanApp.getInstance().setAssistanceInfo(assistanceInfo); //added by nirob
            List<Datum> list = assistanceInfo.getData();
            assistanceArray = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                assistanceArray[i] = list.get(i).getTitle();
            }
            loadData();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            if (!DetectConnection.checkInternetConnection(getActivity())) {
                progressBar.setVisibility(View.GONE);
                String internetCheckMessage = NissanApp.getInstance().getAlertMessage(this.getActivity(), preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
                showNoInternetDialogue(internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage);
                return;
            }

        }


        NissanApp.getInstance().setCurrentCarModel(Values.carType);

        int language_ID = NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang());
        controller.callApi(NissanApp.getInstance().getDeviceID(getActivity()), "" + language_ID, "" + Values.carType, Values.EPUBID, "2");

   /*     try{
            Call<DealerUrl> dealerUrlCall=RetrofitClient.getApiService().getFindADealer(""+language_ID);
            dealerUrlCall.enqueue(new Callback<DealerUrl>() {
                @Override
                public void onResponse(Call<DealerUrl> call, Response<DealerUrl> response) {
                    dealerUrl = response.body();
                }

                @Override
                public void onFailure(Call<DealerUrl> call, Throwable t) {
                    Logger.error("ASSISTANCE TAB ERROR",t.getMessage());
                }
            });
        }catch (Exception e){
            Logger.error(TAG,e.toString());
        }
*/

    }


    @Override
    public void onDownloaded(AssistanceInfo responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {

            preferenceUtil.storeAssistanceData(responseInfo, sharedpref_key);
            NissanApp.getInstance().setAssistanceInfo(responseInfo);

            assistanceInfo = responseInfo;
            List<Datum> list = assistanceInfo.getData();
            assistanceArray = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                assistanceArray[i] = list.get(i).getTitle();
            }

            loadData();
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onFailed(String failedReason) {
        Logger.error("AssistanceFragment", "_________" + failedReason);
    }

    private void showNoInternetDialogue(String msg) {

        final Dialog dialog = new DialogController(getActivity()).internetDialog();
        dialog.setCancelable(false);
        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().finish();
            }
        });

        dialog.show();

    }

    // here load the initial data
    private void loadData() {
        txtViewDriverGuide.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "font/Nissan Brand Regular.otf"));
        if (assistanceInfo != null && assistanceInfo.getAssistanceTitle() != null) {
            txtViewDriverGuide.setText(assistanceInfo.getAssistanceTitle().isEmpty() ? resources.getString(R.string.driver_guide) : assistanceInfo.getAssistanceTitle());
        }

        String title = NissanApp.getInstance().getTabTitle(getActivity(), "2");

        txt_title.setText(title == null || title.isEmpty() ? resources.getString(R.string.assistance) : title);

        adapter.setList(assistanceArray, assistanceImage);
        adapter.notifyDataSetChanged();

        String car_name = assistanceInfo.getSelectedCar();

        if (Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
            txtViewCarName.setText(car_name == null || car_name.isEmpty() ? resources.getStringArray(R.array.car_names)[Values.carType - 1] : car_name);
        } else {
            txtViewCarName.setText("" + (car_name == null || car_name.isEmpty() ? resources.getStringArray(R.array.car_names)[Values.carType - 1] : car_name));
        }
        txtViewCarName.setBackgroundResource(R.color.black);


        String loadtext = NissanApp.getInstance().getAlertMessage(getActivity(),preferenceUtil.getSelectedLang(),Values.LOAD_TEXT_TITLE);
        txtView_loadTxt.setText("" + (loadtext == null || loadtext.isEmpty() ? resources.getString(R.string.loading): loadtext));
        setAssistanceCarBackgroundImage();
    }

    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    // here set assistance car background according to car type
    private void setAssistanceCarBackgroundImage() {

        url = getURLAccordingToDensity(NissanApp.getInstance().getDensityName(getActivity()));


        DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .setPostprocessor(new BasePostprocessor() {
                            @Override
                            public void process(Bitmap bitmap) {
                                Logger.error(TAG, "Postprocessor.process");
                            }
                        })
                        .build())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        Logger.error(TAG, "BaseControllerListener.onFinalImageSet");
                        txtView_loadTxt.setVisibility(View.INVISIBLE);
                    }
                })
                .build();
        imageView.setController(controller);
        //imageView.setImageURI(url);
        Logger.error(TAG,url);
    }

    private String getURLAccordingToDensity(String device_density) {

        if (device_density.equalsIgnoreCase("xxxhdpi")) {
            return assistanceInfo.getAssistanceImages().getAssistanceImgXxxhdpi();
        } else if (device_density.equalsIgnoreCase("xxhdpi")) {
            return assistanceInfo.getAssistanceImages().getAssistanceImgXxhdpi();
        } else if (device_density.equalsIgnoreCase("xhdpi")) {
            return assistanceInfo.getAssistanceImages().getAssistanceImgXhdpi();
        } else if (device_density.equalsIgnoreCase("hdpi")) {
            return assistanceInfo.getAssistanceImages().getAssistanceImgHdpi();
        } else if (device_density.equalsIgnoreCase("ldpi")) {
            return assistanceInfo.getAssistanceImages().getAssistanceImgLdpi();
        } else if(device_density.equalsIgnoreCase("mdpi"))
            return assistanceInfo.getAssistanceImages().getAssistanceImgHdpi();

        return "";
    }

    // here initialized all variable
    private void initViews(View view) {
        context = getActivity().getApplicationContext();
        commonDao = CommonDao.getInstance();
        txtViewCarName = (TextView) view.findViewById(R.id.txt_view_car_name);
        txtView_loadTxt = (TextView) view.findViewById(R.id.txtView_loading);
        txtViewDriverGuide = (TextView) view.findViewById(R.id.txt_view_driver_guide);
        imageView = (SimpleDraweeView) view.findViewById(R.id.img_car_bg);
        lstView = (ListView) view.findViewById(R.id.lst_view);
        txt_title = (TextView) view.findViewById(R.id.txt_title);

        progressBar = (ProgressBar) view.findViewById(R.id.prog_assistance);
        tvNoContent = (TextView) view.findViewById(R.id.txt_assistance_data_not_found);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        controller = new AssistanceTabContentController(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
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
                String updateMsg = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.UPDATE_MSG);
                txtViewTitle.setText(updateMsg == null || updateMsg.isEmpty() ? getResources().getString(R.string.update_msg) : updateMsg);

                String okText = NissanApp.getInstance().getGlobalMessage(getActivity()).getYes();
                String cancelText = NissanApp.getInstance().getGlobalMessage(getActivity()).getNo();

                Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
//                btnOk.setText(resources.getString(R.string.button_YES));
                btnOk.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_YES) : okText);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
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

                                    if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) && !TextUtils.isEmpty(responseInfo.getUrl())) {
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

                                                                                if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                                                                                    ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromSettings(Analytics.CHANGE_LANGUAGE + Analytics.DOWNLOAD));

                                                                                    commonDao.updateLanguageStatus(getActivity().getBaseContext(), Values.carType, preferenceUtil.getSelectedLang());
                                                                                    commonDao.deleteSingleCarEpub(getActivity(), Values.carType);
                                                                                    commonDao.updatePushContentStatus(getActivity().getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang()), Values.ePubType);

                                                                                    if (progressDialog != null)
                                                                                        progressDialog.dismiss();

                                                                                    loadDesireFragment(position);
                                                                                } else {
                                                                                    dismissDialogue();
                                                                                    showErrorDialog("Confirmation send failed error!");
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onFailed(String failedReason) {
                                                                                Logger.error("onFailed", "__________" + failedReason);
                                                                                dismissDialogue();
                                                                                showErrorDialog(failedReason);
                                                                            }
                                                                        });

                                                                    } else {
                                                                        dismissDialogue();
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
                                                dismissDialogue();
                                                return 0;
                                            }

                                            @Override
                                            public boolean internetConnection(boolean b) {
                                                showErrorDialog("No internet connection, please try again");
                                                dismissDialogue();
                                                return false;
                                            }

                                            @Override
                                            public boolean urlReachable(boolean b) {
                                                dismissDialogue();
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
                                        dismissDialogue();
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
                btnCancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_NO) : cancelText);

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

        String pageTitle = assistanceArray[position] == null || assistanceArray[position].isEmpty() ? ((resources.getStringArray(R.array.assistance_array))[position]) : assistanceArray[position];

        switch (position) {
            case 0:
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.WARNING_LIGHT));
                frag = CombimeterFragment.newInstance(pageTitle);
                break;

            case 1:
                if(NissanApp.getInstance().getCarListModel().getQrgModule()==1){
                    Log.e("Qrg selected " , " car type --- " + Values.carType + " ----module " + NissanApp.getInstance().getQRGModule(Values.carType));
                    frag = HomePageFragment.newInstance(pageTitle);
                }else {
                    Log.e("Qrg Not selected " , " car type --- " + Values.carType + " ----module2 " + NissanApp.getInstance().getQRGModule(Values.carType));
                    frag = ListFragment.newInstance(pageTitle);
                }
                /*if (Values.carType == 14 || Values.carType == 17|| Values.carType == 18) {
                    frag = HomePageFragment.newInstance(pageTitle);
                } else {
                    frag = ListFragment.newInstance(pageTitle);
                }*/
                break;

            case 2:
                frag = ListFragment.newInstance(pageTitle);
                break;

            case 3:
                frag = ListFragment.newInstance(pageTitle);
                break;

            case 4:
                frag = ListFragment.newInstance(pageTitle);
                break;

            case 5:
                frag = NissanAssistanceFragment.newInstance(pageTitle, url);
                break;
            case 6:
                if(NissanApp.getInstance().getCarListModel()!=null){
//                    NissanApp.getInstance().getCarListModel().getOnlineBookingStatus()
                }
                /*if (dealerUrl!=null && dealerUrl.getUrl()!=null)
                {
                    Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                    openURL.setData(Uri.parse(dealerUrl.getUrl()));
                    startActivity(openURL);
                }*/
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

    private void dismissDialogue() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "error_fragment");
    }

}
