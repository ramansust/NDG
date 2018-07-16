package com.nissan.alldriverguide.fragments.explore;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.ImageTargetActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.VideoPlayerActivity;
import com.nissan.alldriverguide.adapter.GridViewAdapter;
import com.nissan.alldriverguide.controller.ExploreTabContentController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.assistance.DetailsFragment;
import com.nissan.alldriverguide.interfaces.CompleteExploreTabContent;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.multiLang.model.ExploreTabVideoModel;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.view.ScrollableGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ExploreFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, CompleteExploreTabContent {

    private static final String TAG = "ExploreFragment";
    // declare video thumb in an array
//    private int[] qashqai_eur_thumbnil_array_en = {R.drawable.qashqai_eur_en_video_01, R.drawable.qashqai_eur_en_video_02};
//    private int[] qashqai_eur_thumbnil_array_fr = {R.drawable.qashqai_eur_fr_video_01, R.drawable.qashqai_eur_fr_video_02};
//    private int[] qashqai_eur_thumbnil_array_it = {R.drawable.qashqai_eur_it_video_01, R.drawable.qashqai_eur_it_video_02};
//
//    private int[] juke_thumbnil_array_en = {R.drawable.juke_en_video_01, R.drawable.juke_en_video_02};
//    private int[] juke_thumbnil_array_de = {R.drawable.juke_de_video_01, R.drawable.juke_de_video_02};
//    private int[] juke_thumbnil_array_fr = {R.drawable.juke_fr_video_01, R.drawable.juke_fr_video_02};
//    private int[] juke_thumbnil_array_it = {R.drawable.juke_it_video_01, R.drawable.juke_it_video_02};
//
//    private int[] xtrail_eur_thumbnil_array_en = {R.drawable.xtrail_eur_en_video_01, R.drawable.xtrail_eur_en_video_02};
//    private int[] xtrail_eur_thumbnil_array_fr = {R.drawable.xtrail_eur_fr_video_01, R.drawable.xtrail_eur_fr_video_02};
//    private int[] xtrail_eur_thumbnil_array_it = {R.drawable.xtrail_eur_it_video_01, R.drawable.xtrail_eur_it_video_02};
//
//    private int[] navara_thumbnil_array_en = {R.drawable.navara_en_video_01, R.drawable.navara_en_video_02};
//    private int[] navara_thumbnil_array_de = {R.drawable.navara_de_video_01, R.drawable.navara_de_video_02};
//    private int[] navara_thumbnil_array_it = {R.drawable.navara_it_video_01, R.drawable.navara_it_video_02};
//
//    private int[] micrak14_thumbnil_array_en = {R.drawable.micrak14_en_video_01, R.drawable.micrak14_en_video_02, R.drawable.micrak14_en_video_03, R.drawable.micrak14_en_video_04};
//    private int[] micrak14_thumbnil_array_de = {R.drawable.micrak14_de_video_01, R.drawable.micrak14_de_video_02, R.drawable.micrak14_de_video_03, R.drawable.micrak14_de_video_04};
//    private int[] micrak14_thumbnil_array_nl = {R.drawable.micrak14_nl_video_01, R.drawable.micrak14_nl_video_02, R.drawable.micrak14_nl_video_03, R.drawable.micrak14_nl_video_04};
//    private int[] micrak14_thumbnil_array_es = {R.drawable.micrak14_es_video_01, R.drawable.micrak14_es_video_02, R.drawable.micrak14_es_video_03, R.drawable.micrak14_es_video_04};
//    private int[] micrak14_thumbnil_array_fr = {R.drawable.micrak14_fr_video_01, R.drawable.micrak14_fr_video_02, R.drawable.micrak14_fr_video_03, R.drawable.micrak14_fr_video_04};
//    private int[] micrak14_thumbnil_array_it = {R.drawable.micrak14_it_video_01, R.drawable.micrak14_it_video_02, R.drawable.micrak14_it_video_03, R.drawable.micrak14_it_video_04};
//
//    private int[] qashqai_2017_thumbnil_array_en = {R.drawable.qashqai_2017_en_video_01, R.drawable.qashqai_2017_en_video_02, R.drawable.qashqai_2017_en_video_03, R.drawable.qashqai_2017_en_video_04, R.drawable.qashqai_2017_en_video_05, R.drawable.qashqai_2017_en_video_06};
//    private int[] qashqai_2017_thumbnil_array_de = {R.drawable.qashqai_2017_de_video_01, R.drawable.qashqai_2017_de_video_02, R.drawable.qashqai_2017_de_video_03, R.drawable.qashqai_2017_de_video_04, R.drawable.qashqai_2017_de_video_05, R.drawable.qashqai_2017_de_video_06};
//    private int[] qashqai_2017_thumbnil_array_es = {R.drawable.qashqai_2017_es_video_01, R.drawable.qashqai_2017_es_video_02, R.drawable.qashqai_2017_es_video_03, R.drawable.qashqai_2017_es_video_04, R.drawable.qashqai_2017_es_video_05, R.drawable.qashqai_2017_es_video_06};
//    private int[] qashqai_2017_thumbnil_array_fr = {R.drawable.qashqai_2017_fr_video_01, R.drawable.qashqai_2017_fr_video_02, R.drawable.qashqai_2017_fr_video_03, R.drawable.qashqai_2017_fr_video_04, R.drawable.qashqai_2017_fr_video_05, R.drawable.qashqai_2017_fr_video_06};
//    private int[] qashqai_2017_thumbnil_array_nl = {R.drawable.qashqai_2017_nl_video_01, R.drawable.qashqai_2017_nl_video_02, R.drawable.qashqai_2017_nl_video_03, R.drawable.qashqai_2017_nl_video_04, R.drawable.qashqai_2017_nl_video_05, R.drawable.qashqai_2017_nl_video_06};
//    private int[] qashqai_2017_thumbnil_array_sv = {R.drawable.qashqai_2017_sv_video_01, R.drawable.qashqai_2017_sv_video_02, R.drawable.qashqai_2017_sv_video_03, R.drawable.qashqai_2017_sv_video_04, R.drawable.qashqai_2017_sv_video_05, R.drawable.qashqai_2017_sv_video_06};
//
//    private int[] xtrail_2017_thumbnil_array_en = {R.drawable.xtrial_2017_en_video_01, R.drawable.xtrial_2017_en_video_02, R.drawable.xtrial_2017_en_video_03, R.drawable.xtrial_2017_en_video_04, R.drawable.xtrial_2017_en_video_05, R.drawable.xtrial_2017_en_video_06};
//
//    private int[] leaf_2017_thumbnil_array_en = {R.drawable.leaf2017_en_video_01, R.drawable.leaf2017_en_video_02, R.drawable.leaf2017_en_video_03};
//    private int[] leaf_2017_thumbnil_array_nl = {R.drawable.leaf2017_nl_video_01, R.drawable.leaf2017_nl_video_02, R.drawable.leaf2017_nl_video_03};
//    private int[] leaf_2017_thumbnil_array_de = {R.drawable.leaf2017_de_video_01, R.drawable.leaf2017_de_video_02, R.drawable.leaf2017_de_video_03};
//    private int[] leaf_2017_thumbnil_array_it = {R.drawable.leaf2017_it_video_01, R.drawable.leaf2017_it_video_02, R.drawable.leaf2017_it_video_03};
//    private int[] leaf_2017_thumbnil_array_no = {R.drawable.leaf2017_no_video_01, R.drawable.leaf2017_no_video_02, R.drawable.leaf2017_no_video_03};
//    private int[] leaf_2017_thumbnil_array_es = {R.drawable.leaf2017_es_video_01, R.drawable.leaf2017_es_video_02, R.drawable.leaf2017_es_video_03};

    private Button btnAR;
    private RelativeLayout btnBlindSpotAR;
    private View view;

    private RelativeLayout relativeAR;
    private RelativeLayout relativeBlindSpot;

    private ScrollableGridView gridView;
    private GridViewAdapter adapter;

    private Resources resources;
    private DisplayMetrics metrics;
    private TextView txtViewExplore, tvNoContent, tvPageTitle, textViewMap;
    private ProgressBar progressBar;
    private String sharedpref_key;
    private String preSharedpref_key;
    private ArrayList<ExploreTabVideoModel> videoList = new ArrayList<>();
    private String header_text;
    private String header_Background;
    private ExploreTabModel exploreModel;
    private ProgressDialog progressDialog;
    private String device_density, internetCheckMessage = "";
    public static ProgressBar progress_bar;
    private ExploreTabContentController controller;
    private LinearLayout layoutDataNotFound;
    private LinearLayout mapView;
    private List<EpubInfo> list;

    /**
     * Creating instance for this fragment
     *
     * @return Explore Fragment
     */
    public static Fragment newInstance() {
        Fragment frag = new ExploreFragment();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_explore, container, false);

        initViews(view);
        device_density = NissanApp.getInstance().getDensityName(getActivity());
        loadResource();
        setListener();
        getExploreTabContent();
        showTitle();

        return view;
    }

    private void showTitle() {

        String title = NissanApp.getInstance().getTabTitle(getActivity(), "1");

        tvPageTitle.setText(title.isEmpty() ? resources.getString(R.string.explore) : title);

    }

    private void getExploreTabContent() {

        adapter = new GridViewAdapter(getActivity().getApplicationContext(), new ArrayList<ExploreTabVideoModel>(), device_density);
        gridView.setAdapter(adapter);


        sharedpref_key = Values.carType + "_" + Values.EXPLOREDATA;
        exploreModel = new PreferenceUtil(getActivity()).retrieveExploreDataList(sharedpref_key);
        if (exploreModel != null && exploreModel.getVideoList() != null && exploreModel.getVideoList().size() > 0) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.GONE);
            check_density();
            videoList = exploreModel.getVideoList();
            NissanApp.getInstance().setExploreVideoList(videoList);

            loadData();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if (!DetectConnection.checkInternetConnection(getActivity())) {
                progressBar.setVisibility(View.GONE);
                internetCheckMessage = NissanApp.getInstance().getAlertMessage(this.getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
                showNoInternetDialogue(internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage);
                return;
            }
        }

            int language_ID = NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang());
            controller.callApi(NissanApp.getInstance().getDeviceID(getActivity()), "" + language_ID, "" + Values.carType, Values.EPUBID, "1");

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

    public void check_density() {

        if (device_density.equalsIgnoreCase("xxxhdpi")) {
            header_text = exploreModel.getHeaderXxxhdpi();
        } else if (device_density.equalsIgnoreCase("xxhdpi")) {
            header_text = exploreModel.getHeaderXxhdpi();
        } else if (device_density.equalsIgnoreCase("xhdpi")) {
            header_text = exploreModel.getHeaderXhdpi();
        } else if (device_density.equalsIgnoreCase("hdpi")) {
            header_text = exploreModel.getHeaderHdpi();
        } else if (device_density.equalsIgnoreCase("ldpi")) {
            header_text = exploreModel.getHeaderLdpi();
        } else {
            header_text = exploreModel.getHeaderXhdpi();

        }
    }

    public void check_Data() {
        if (new PreferenceUtil(getActivity()).retrieveExploreDataList(sharedpref_key) != null) {
            videoList.clear();
            exploreModel = new PreferenceUtil(getActivity()).retrieveExploreDataList(sharedpref_key);
            check_density();

            videoList = exploreModel.getVideoList();
            NissanApp.getInstance().setExploreVideoList(videoList);

            loadData();
            apiCall();


        } else {
            apiCall();
        }
    }

    @Override
    public void onDownloaded(ExploreTabModel responseInfo) {
        if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
                layoutDataNotFound.setVisibility(View.GONE);
                tvNoContent.setVisibility(View.GONE);
            }
            new PreferenceUtil(getActivity().getApplicationContext()).storeExploreDataList(responseInfo, sharedpref_key);
            videoList.clear();
            exploreModel = responseInfo;
            check_density();
            videoList = exploreModel.getVideoList();
            NissanApp.getInstance().setExploreVideoList(videoList);
            loadData();
                    /*if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();*/

                    /*if(progress_bar !=null){
                        progress_bar.setVisibility(View.INVISIBLE);
                    }*/
        }

    }

    @Override
    public void onFailed(String failedReason) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            layoutDataNotFound.setVisibility(View.VISIBLE);
            tvNoContent.setVisibility(View.VISIBLE);
        }
    }

    private void apiCall() {
        int language_ID = NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang());
        String language_name = new PreferenceUtil(getActivity()).getSelectedLang();

        new ApiCall().postExploreTabContent(NissanApp.getInstance().getDeviceID(getActivity()), "" + language_ID, "" + Values.carType, Values.EPUBID, "1", new CompleteExploreTabContent() {
            @Override
            public void onDownloaded(ExploreTabModel responseInfo) {
                if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {
                    new PreferenceUtil(getActivity().getApplicationContext()).storeExploreDataList(responseInfo, sharedpref_key);
                    videoList.clear();
                    exploreModel = responseInfo;
                    check_density();
                    videoList = exploreModel.getVideoList();
                    NissanApp.getInstance().setExploreVideoList(videoList);
                    loadData();
                    /*if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();*/

                    /*if(progress_bar !=null){
                        progress_bar.setVisibility(View.INVISIBLE);
                    }*/
                }
            }

            @Override
            public void onFailed(String failedReason) {
            }
        });
    }

    /**
     * here load the explore fragment initialized data
     */
    private void loadData() {

        //old btn_ar visibility logic
        /*if (header_text != null) {
            relativeBlindSpot.setVisibility(View.VISIBLE);
            relativeAR.setVisibility(View.GONE);
            Glide.with(this).load(header_text).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        txtViewExplore.setBackground(drawable);
                        //txtViewExplore.setBackgroundResource(R.drawable.micra_eng);
                    }
                }
            });

            //txtViewExplore.setImageURI(Uri.parse(header_text));
           *//* Glide.with(this).load(header_Background).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnBlindSpotAR.setBackground(drawable);
                    }
                }
            });*//*

            Collections.sort(videoList, new Comparator<ExploreTabVideoModel>() {
                @Override
                public int compare(ExploreTabVideoModel lhs, ExploreTabVideoModel rhs) {
                    return lhs.getIndex().compareTo(rhs.getIndex());
                }
            });
            adapter.setList(videoList);
            adapter.notifyDataSetChanged();

        } else {
            relativeAR.setVisibility(View.VISIBLE);
            relativeBlindSpot.setVisibility(View.GONE);

            *//*Glide.with(this).load(header_Background).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnAR.setBackground(drawable);
                    }
                }
            });*//*
        }*/

        //old static Rohan
        if (Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {

            btnAR.setBackgroundResource(R.drawable.ar_selector_new_car);
        } else {
            btnAR.setBackgroundResource(R.drawable.ar_selector);
        }

        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 10) {
            btnBlindSpotAR.setBackgroundResource(R.drawable.explore_xtrail_eur);
        } else if (Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
            btnBlindSpotAR.setBackgroundResource(R.drawable.explore_micra_new);
        }

//        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 10 || Values.carType == 11) {
        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 10 || Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
            relativeBlindSpot.setVisibility(View.VISIBLE);
            relativeAR.setVisibility(View.GONE);

            if (header_text != null) {
                /*relativeBlindSpot.setVisibility(View.VISIBLE);
                relativeAR.setVisibility(View.GONE);*/

                Glide.with(this).load(header_text).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            txtViewExplore.setBackground(drawable);
                            //txtViewExplore.setBackgroundResource(R.drawable.micra_eng);
                        }
                    }
                });

                Collections.sort(videoList, new Comparator<ExploreTabVideoModel>() {
                    @Override
                    public int compare(ExploreTabVideoModel lhs, ExploreTabVideoModel rhs) {
                        return lhs.getIndex().compareTo(rhs.getIndex());
                    }
                });

                adapter.setList(videoList);
                adapter.notifyDataSetChanged();
            }

            //here set the adapter for grid view
//            gridView.setAdapter(new GridViewAdapter(getActivity().getApplicationContext(), getThumbnilArray(new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang())));
        } else {
            mapView.setVisibility(View.GONE);
            relativeAR.setVisibility(View.VISIBLE);
            relativeBlindSpot.setVisibility(View.GONE);
        }

        if (Values.carType == 11) {
            mapView.setVisibility(View.VISIBLE);
            mapTextImage(new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang());
        } else {
            mapView.setVisibility(View.GONE);
        }
    }

    /**
     * here set the click listener
     */
    private void setListener() {
        btnAR.setOnClickListener(this);
        btnBlindSpotAR.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
        mapView.setOnClickListener(this);
    }

    /**
     * initialized all global variable
     *
     * @param view for find fragment layout id
     */
    private void initViews(View view) {

        txtViewExplore = (TextView) view.findViewById(R.id.txt_blind_spot_ar);
        textViewMap = (TextView) view.findViewById(R.id.txt_map);
        tvPageTitle = (TextView ) view.findViewById(R.id.txt_title_explore);
//        txtViewExplore = (TextView) view.findViewById(R.id.txt_blind_spot_ar);
        relativeAR = (RelativeLayout) view.findViewById(R.id.relative_ar);
        relativeBlindSpot = (RelativeLayout) view.findViewById(R.id.relative_blind_spot);
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);

        btnAR = (Button) view.findViewById(R.id.btn_ar);
        btnBlindSpotAR = (RelativeLayout) view.findViewById(R.id.btn_blind_spot_ar);
        mapView = (LinearLayout) view.findViewById(R.id.map_view);
        gridView = (ScrollableGridView) view.findViewById(R.id.grid_view);

        // this code for prevent scrollview bottom display
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.smoothScrollTo(0, 0);

        progressBar = (ProgressBar) view.findViewById(R.id.prog_explore);
        layoutDataNotFound = (LinearLayout) view.findViewById(R.id.layout_explore_data_not_found);
        tvNoContent = (TextView) view.findViewById(R.id.txt_explore_data_not_found);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        controller = new ExploreTabContentController(this);
    }

    // load resources for language localized
    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ar:
                // here start the ImageTargetActivity class for AR
            case R.id.btn_blind_spot_ar:

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    requestCameraPermission();
                } else {
                    startActivity(new Intent(getActivity(), ImageTargetActivity.class));
                }
                break;

            case R.id.map_view:
                if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                    list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);
                }
                Values.ePubType = Values.HOMEPAGE_TYPE;

                Fragment frag = DetailsFragment.newInstance(list.get(52).getIndex(), resources.getString(R.string.updating_map_data));
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabExplore);
                ft.commit();
                break;


            default:
                break;
        }
    }



    /**
     * Requesting camera permission
     * This uses single permission model from dexter
     * Once the permission granted, opens the camera
     * On permanent denial opens settings dialog
     */
    private void requestCameraPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        if (ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(new Intent(getActivity(), ImageTargetActivity.class));
                        } else {
                            Toast.makeText(getActivity(), "Turn on storage and camera permissions both", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Values.videoIndex = position;
        // here start the playing video for grid view item click

        if (DetectConnection.checkInternetConnection(getActivity())) {

            if (NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getVideoUrl() != null) {
                startActivity(new Intent(getActivity(), VideoPlayerActivity.class));
            }

        } else {
            Toast.makeText(getActivity(), internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage, Toast.LENGTH_SHORT).show();
        }

    }

/*    *//**
     * @param lang for comparing language sort name
     * @return int array for specific language
     *//*
    private int[] getThumbnilArray(String lang) {
        int[] array = new int[4];
        if (Values.carType == 1) {
            if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("sv") || lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("nl") || lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("pt")) {
                array = qashqai_eur_thumbnil_array_en;
            } else if (lang.equalsIgnoreCase("fr")) {
                array = qashqai_eur_thumbnil_array_fr;
            } else if (lang.equalsIgnoreCase("it")) {
                array = qashqai_eur_thumbnil_array_it;
            }
        } else if (Values.carType == 3) {
            if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("nl") || lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("sv") || lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("pt")) {
                array = juke_thumbnil_array_en;
            } else if (lang.equalsIgnoreCase("de")) {
                array = juke_thumbnil_array_de;
            } else if (lang.equalsIgnoreCase("fr")) {
                array = juke_thumbnil_array_fr;
            } else if (lang.equalsIgnoreCase("it")) {
                array = juke_thumbnil_array_it;
            }
        } else if (Values.carType == 4) {
            if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("sv") || lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("nl") || lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("pt")) {
                array = xtrail_eur_thumbnil_array_en;
            } else if (lang.equalsIgnoreCase("fr")) {
                array = xtrail_eur_thumbnil_array_fr;
            } else if (lang.equalsIgnoreCase("it")) {
                array = xtrail_eur_thumbnil_array_it;
            }
        } else if (Values.carType == 10) {
            if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("fr") || lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("sv") || lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("nl") || lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("pt")) {
                array = navara_thumbnil_array_en;
            } else if (lang.equalsIgnoreCase("de")) {
                array = navara_thumbnil_array_de;
            } else if (lang.equalsIgnoreCase("it")) {
                array = navara_thumbnil_array_it;
            }
        } else if (Values.carType == 11) {
            if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("sv") || lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("pt")) {
                array = micrak14_thumbnil_array_en;
            } else if (lang.equalsIgnoreCase("de")) {
                array = micrak14_thumbnil_array_de;
            } else if (lang.equalsIgnoreCase("nl")) {
                array = micrak14_thumbnil_array_nl;
            } else if (lang.equalsIgnoreCase("es")) {
                array = micrak14_thumbnil_array_es;
            } else if (lang.equalsIgnoreCase("fr")) {
                array = micrak14_thumbnil_array_fr;
            } else if (lang.equalsIgnoreCase("it")) {
                array = micrak14_thumbnil_array_it;
            }
        } else if (Values.carType == 12) {
            if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("it") || lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("pt")) {
                array = qashqai_2017_thumbnil_array_en;
            } else if (lang.equalsIgnoreCase("de")) {
                array = qashqai_2017_thumbnil_array_de;
            } else if (lang.equalsIgnoreCase("nl")) {
                array = qashqai_2017_thumbnil_array_nl;
            } else if (lang.equalsIgnoreCase("es")) {
                array = qashqai_2017_thumbnil_array_es;
            } else if (lang.equalsIgnoreCase("fr")) {
                array = qashqai_2017_thumbnil_array_fr;
            } else if (lang.equalsIgnoreCase("sv")) {
                array = qashqai_2017_thumbnil_array_sv;
            }
        } else if (Values.carType == 13) {
            array = xtrail_2017_thumbnil_array_en;
        } else if (Values.carType == 14) {
            if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("ru") || lang.equalsIgnoreCase("fr") || lang.equalsIgnoreCase("sv") || lang.equalsIgnoreCase("pl") || lang.equalsIgnoreCase("fi") || lang.equalsIgnoreCase("pt")) {
                array = leaf_2017_thumbnil_array_en;
            } else if (lang.equalsIgnoreCase("de")) {
                array = leaf_2017_thumbnil_array_de;
            } else if (lang.equalsIgnoreCase("nl")) {
                array = leaf_2017_thumbnil_array_nl;
            } else if (lang.equalsIgnoreCase("es")) {
                array = leaf_2017_thumbnil_array_es;
            } else if (lang.equalsIgnoreCase("it")) {
                array = leaf_2017_thumbnil_array_it;
            } else if (lang.equalsIgnoreCase("no")) {
                array = leaf_2017_thumbnil_array_no;
            }
        } else {

        }

        // here set the localized language image in top left
        if (lang.equalsIgnoreCase("en")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_eng);
        } else if (lang.equalsIgnoreCase("de")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_german);
        } else if (lang.equalsIgnoreCase("ru")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_rus);
        } else if (lang.equalsIgnoreCase("sv")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_swedish);
        } else if (lang.equalsIgnoreCase("es")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_spanish);
        } else if (lang.equalsIgnoreCase("nl")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_dutch);
        } else if (lang.equalsIgnoreCase("no")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_no);
        } else if (lang.equalsIgnoreCase("fr")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_french);
        } else if (lang.equalsIgnoreCase("it")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_italian);
        } else if (lang.equalsIgnoreCase("pl")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_polish);
        } else if (lang.equalsIgnoreCase("fi")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_finish);
        } else if (lang.equalsIgnoreCase("pt")) {
            txtViewExplore.setBackgroundResource(R.drawable.micra_portuguese);
        } else {

        }
        return array;
    }*/

    private void mapTextImage(String lang) {
        if (Values.carType == 11) {
            if(lang.equalsIgnoreCase("en")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_en);
            } else if(lang.equalsIgnoreCase("de")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_de);
            } else if(lang.equalsIgnoreCase("ru")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_ru);
            } else if(lang.equalsIgnoreCase("sv")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_sv);
            } else if(lang.equalsIgnoreCase("es")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_es);
            } else if(lang.equalsIgnoreCase("nl")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_nl);
            } else if(lang.equalsIgnoreCase("no")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_no);
            } else if(lang.equalsIgnoreCase("fr")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_fr);
            } else if(lang.equalsIgnoreCase("it")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_it);
            } else if(lang.equalsIgnoreCase("pl")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_pl);
            } else if(lang.equalsIgnoreCase("fi")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_fi);
            } else if(lang.equalsIgnoreCase("pt")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_pt);
            } else {

            }
        }
    }


}
