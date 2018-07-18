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
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class ExploreFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, CompleteExploreTabContent {

    private static final String TAG = "ExploreFragment";

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
    private ArrayList<ExploreTabVideoModel> videoList = new ArrayList<>();
    private String header_text;
    private ExploreTabModel exploreModel;
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
        return new ExploreFragment();
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

    @Override
    public void onDownloaded(ExploreTabModel responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
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

    /**
     * here load the explore fragment initialized data
     */
    private void loadData() {

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

        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 10 || Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
            relativeBlindSpot.setVisibility(View.VISIBLE);
            relativeAR.setVisibility(View.GONE);

            if (header_text != null) {

                Glide.with(this).load(header_text).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            txtViewExplore.setBackground(drawable);
//                            txtViewExplore.setBackgroundResource(R.color.black);
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
