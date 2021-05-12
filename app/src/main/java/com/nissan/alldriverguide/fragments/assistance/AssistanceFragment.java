package com.nissan.alldriverguide.fragments.assistance;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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
import com.nissan.alldriverguide.model.PushContentInfo;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;
import com.nissan.alldriverguide.multiLang.model.Datum;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.DialogErrorFragment;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class AssistanceFragment extends Fragment implements AdapterView.OnItemClickListener, CompleteAssistanceTabContent {
    private static final String TAG = "AssistanceFragment";
    private Context context;
    public Resources resources;
    private String[] assistanceArray;
    private final int[] assistanceImage = {R.drawable.warning_light, R.drawable.quick_reference, R.drawable.tyre, R.drawable.engine_compartment, R.drawable.warranty, R.drawable.selled, R.drawable.calendar};
    private TextView txtViewCarName;
    private TextView txtViewDriverGuide;
    private SimpleDraweeView imageView;
    private ListView lstView;
    private TextView txt_title;

    private ProgressBar progressBar;
    private TextView tvNoContent, txtView_loadTxt;

    private AssistanceAdapter adapter;
    private PreferenceUtil preferenceUtil;
    private AssistanceInfo assistanceInfo;

    private CommonDao commonDao;
    private ProgressDialog progressDialog;

    private long mLastClickTime = 0;
    private long doubleClickPopup = 0;

    private String sharedpref_key;
    private AssistanceTabContentController controller;

    private String url;
    private String bookingUrl = "";

    public AssistanceFragment() {
    }

    public static Fragment newInstance() {
        return new AssistanceFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assistance, container, false);
        initViews(view);
        loadResource();
        setListener();
        check_Data();
        return view;
    }

    public void check_Data() {

        adapter = new AssistanceAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), assistanceArray, assistanceImage);
        lstView.setAdapter(adapter);

        sharedpref_key = Values.carType + "_" + Values.ASSISTANCE_OBJ_STORE_KEY;
        assistanceInfo = preferenceUtil.retrieveAssistanceData(sharedpref_key);

        Logger.error("Alert msg", "---- " + NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.LOAD_TEXT_TITLE));

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
        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            Objects.requireNonNull(getActivity()).finish();
        });

        dialog.show();

    }

    // here load the initial data
    @SuppressLint("SetTextI18n")
    private void loadData() {
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


        String loadtext = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.LOAD_TEXT_TITLE);
        txtView_loadTxt.setText("" + (loadtext == null || loadtext.isEmpty() ? resources.getString(R.string.loading) : loadtext));
        setAssistanceCarBackgroundImage();

        //get current car model object
        NissanApp.getInstance().setCurrentCarModel(Values.carType);
    }

    private void loadResource() {
        resources = new Resources(Objects.requireNonNull(getActivity()).getAssets(), new DisplayMetrics(), NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    // here set assistance car background according to car type
    private void setAssistanceCarBackgroundImage() {

        url = getURLAccordingToDensity(NissanApp.getInstance().getDensityName(Objects.requireNonNull(getActivity())));


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
        Logger.error(TAG, url);
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
        } else if (device_density.equalsIgnoreCase("mdpi"))
            return assistanceInfo.getAssistanceImages().getAssistanceImgHdpi();

        return "";
    }

    // here initialized all variable
    private void initViews(View view) {
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
        commonDao = CommonDao.getInstance();
        txtViewCarName = view.findViewById(R.id.txt_view_car_name);
        txtView_loadTxt = view.findViewById(R.id.txtView_loading);
        txtViewDriverGuide = view.findViewById(R.id.txt_view_driver_guide);
        imageView = view.findViewById(R.id.img_car_bg);
        lstView = view.findViewById(R.id.lst_view);
        txt_title = view.findViewById(R.id.txt_title);

        progressBar = view.findViewById(R.id.prog_assistance);
        tvNoContent = view.findViewById(R.id.txt_assistance_data_not_found);

        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        controller = new AssistanceTabContentController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        new PreferenceUtil(Objects.requireNonNull(Objects.requireNonNull(getActivity())).getApplicationContext()).setOpenCountForRateApp();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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

        if (DetectConnection.checkInternetConnection(Objects.requireNonNull(getActivity()).getApplicationContext())) {
            PushContentInfo info = commonDao.getNotificationData(getActivity().getApplicationContext(), Values.carType, NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()), Values.ePubType);

            if (info != null && !TextUtils.isEmpty(info.getCarId()) && !TextUtils.isEmpty(info.getLangId()) && !TextUtils.isEmpty(info.getePubId())) {
                Logger.error("You have a update", "Do you want ?");

                final Dialog dialog = new DialogController(getActivity()).contentUpdateDialog();

                TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
                String updateMsg = NissanApp.getInstance().getAlertMessage(getActivity(), preferenceUtil.getSelectedLang(), Values.UPDATE_MSG);
                txtViewTitle.setText(updateMsg == null || updateMsg.isEmpty() ? getResources().getString(R.string.update_msg) : updateMsg);

                String okText = NissanApp.getInstance().getGlobalMessage(getActivity()).getYes();
                String cancelText = NissanApp.getInstance().getGlobalMessage(getActivity()).getNo();

                Button btnOk = dialog.findViewById(R.id.btn_ok);
//                btnOk.setText(resources.getString(R.string.button_YES));
                btnOk.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_YES) : okText);

                btnOk.setOnClickListener(v -> {

                    if (SystemClock.elapsedRealtime() - doubleClickPopup < DEFAULT_CLICK_TIMEOUT) {
                        return;
                    }
                    doubleClickPopup = SystemClock.elapsedRealtime();

                    dialog.dismiss();

                    if (!DetectConnection.checkInternetConnection(getActivity().getApplicationContext())) {
                        return;
                    }
                    getActivity().runOnUiThread(() -> progressDialog = new ProgressDialogController(getActivity()).showDialog(getResources().getString(R.string.start_download)));

                    new ApiCall().postContentDownload("" + Values.carType, "" + NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang()), "" + Values.ePubType, NissanApp.getInstance().getDeviceID(getActivity().getApplicationContext()), new CompleteAPI() {
                        @Override
                        public void onDownloaded(ResponseInfo responseInfo) {
//                                Logger.error("status", "__________" + responseInfo.getStatusCode() + "____" + responseInfo.getMessage() + "____" + responseInfo.getUrl());;

                            if (!SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode()) || TextUtils.isEmpty(responseInfo.getUrl())) {
                                showErrorDialog("No content found.");
                                dismissDialogue();
                            }  //
                            //TODO Integrate download manager
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
                });

                Button btnCancel = dialog.findViewById(R.id.btn_cancel);
                btnCancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_NO) : cancelText);

                btnCancel.setOnClickListener(v -> {
                    if (SystemClock.elapsedRealtime() - doubleClickPopup < 1000) {
                        return;
                    }
                    doubleClickPopup = SystemClock.elapsedRealtime();

                    dialog.dismiss();
                    loadDesireFragment(position);
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
                ((MainActivity) Objects.requireNonNull(getActivity())).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.WARNING_LIGHT));
                frag = CombimeterFragment.newInstance(pageTitle);
                break;

            case 1:
                if (NissanApp.getInstance().getCarListModel() != null) {
                    if (NissanApp.getInstance().getCarListModel().getQrgModule() == 1) {
                        frag = HomePageFragment.newInstance(pageTitle);
                    } else {
                        frag = ListFragment.newInstance(pageTitle);
                    }
                }
                break;

            case 2:
            case 3:
            case 4:
                frag = ListFragment.newInstance(pageTitle);
                break;

            case 5:
                if (getOnlineUrl() != null && !getOnlineUrl().isEmpty()) {
                    String onlineUrl = getOnlineUrl();
                    frag = NissanAssistanceFragment.newInstance(pageTitle, url, onlineUrl);
                } else frag = NissanAssistanceFragment.newInstance(pageTitle, url, "");

                break;

            default:
                break;
        }

        if (frag != null) {
            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            ft.replace(R.id.container, frag);
            ft.addToBackStack(Values.tabAssistance);
            ft.commit();
        }
    }

    private String getOnlineUrl() {
        if (assistanceInfo != null) {
            if (assistanceInfo.getData().size() == 7) {
                bookingUrl = assistanceInfo.getData().get(6).getBookingUrl();
                return bookingUrl;
            }
        }
        return bookingUrl;
    }

    private void dismissDialogue() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String msg) {
        DialogErrorFragment dialogFragment = DialogErrorFragment.getInstance(context, msg);
        dialogFragment.show(Objects.requireNonNull(Objects.requireNonNull(getActivity())).getSupportFragmentManager(), "error_fragment");
    }

}
