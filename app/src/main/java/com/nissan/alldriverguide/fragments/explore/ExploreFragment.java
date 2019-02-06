package com.nissan.alldriverguide.fragments.explore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.ImageTargetActivity;
import com.nissan.alldriverguide.PDFOpenActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.VideoPlayerActivity;
import com.nissan.alldriverguide.adapter.GridViewAdapter;
import com.nissan.alldriverguide.controller.ExploreTabContentController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.assistance.DetailsFragment;
import com.nissan.alldriverguide.interfaces.CompleteExploreTabContent;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;
import com.nissan.alldriverguide.multiLang.model.ExploreTabVideoModel;
import com.nissan.alldriverguide.utils.CustomViewPager;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.view.ScrollableGridView;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class ExploreFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, CompleteExploreTabContent, ViewPager.OnPageChangeListener {

    private static final String TAG = "ExploreFragment";

    private Context mContext;
    private Button btnAR;
    private RelativeLayout btnBlindSpotAR, relativeAR, relativeBlindSpot, rlMapView;
    private View view;

    private ScrollableGridView gridView;
    private GridViewAdapter adapter;

    private Resources resources;
    private DisplayMetrics metrics;
    private TextView txtViewExplore, txt_ar, tvNoContent, tvPageTitle, textViewMap, textViewMap2;
    private ProgressBar progressBar;
    private String sharedpref_key;
    private ArrayList<ExploreTabVideoModel> videoList = null;
    private String header_text;
    private ExploreTabModel exploreModel;
    private String device_density, internetCheckMessage = "";
    public static ProgressBar progress_bar;
    private ExploreTabContentController controller;
    private LinearLayout layoutDataNotFound, llLeftArrow, llRightArrow;
    private LinearLayout mapView;
    private List<EpubInfo> list;
    private CustomViewPager viewPager;
    private ImageView ivRight, ivLeft;
    private PreferenceUtil preferenceUtil;
    private ProgressDialog progressDialog = null;

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

        videoList = new ArrayList<>();
        adapter = new GridViewAdapter(getActivity().getApplicationContext(), videoList, device_density);
        gridView.setAdapter(adapter);


        sharedpref_key = Values.carType + "_" + Values.EXPLORE_OBJ_STORE_KEY;
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
            Logger.error(TAG, "check_density: xxxhdpi");
        } else if (device_density.equalsIgnoreCase("xxhdpi")) {
            header_text = exploreModel.getHeaderXxhdpi();
            Logger.error(TAG, "check_density: xxhdpi");
        } else if (device_density.equalsIgnoreCase("xhdpi")) {
            header_text = exploreModel.getHeaderXhdpi();
            Logger.error(TAG, "check_density: xhdpi " + header_text);
        } else if (device_density.equalsIgnoreCase("hdpi")) {
            header_text = exploreModel.getHeaderHdpi();
            Logger.error(TAG, "check_density: hdpi");
        } else if (device_density.equalsIgnoreCase("ldpi")) {
            header_text = exploreModel.getHeaderLdpi();
            Logger.error(TAG, "check_density: ldpi");
        } else {
            header_text = exploreModel.getHeaderXhdpi();
            Logger.error(TAG, "check_density: else");

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
            videoList = new ArrayList<>();
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
        if (Values.carType == 11 || Values.carType == 12
                || Values.carType == 16 || Values.carType == 13
                || Values.carType == 14 || Values.carType == 15) {

            btnAR.setBackgroundResource(R.drawable.ar_selector_new_car);
        } else {
            btnAR.setBackgroundResource(R.drawable.ar_selector);
        }

        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 10) {
            btnBlindSpotAR.setBackgroundResource(R.drawable.explore_xtrail_eur);
        } else if (Values.carType == 11 || Values.carType == 12 || Values.carType == 16
                || Values.carType == 13 || Values.carType == 14 || Values.carType == 15) {
            btnBlindSpotAR.setBackgroundResource(R.drawable.explore_micra_new);
        }

        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4
                || Values.carType == 10 || Values.carType == 11 || Values.carType == 12
                || Values.carType == 16
                || Values.carType == 13 || Values.carType == 14 || Values.carType == 15) {
            relativeBlindSpot.setVisibility(View.VISIBLE);
            relativeAR.setVisibility(View.GONE);

            if (header_text != null) {

/*
                SpannableString str = new SpannableString(mContext.getResources().getString(R.string.combimeter_is_the_main));
                str.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, str.length(), 0);
                txtViewExplore.setText(str);
*/

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

                for (int i = 0; i < videoList.size(); i++) {

                    if (videoList.get(i).getTag() == 997) {
                        ExploreTabVideoModel model = videoList.get(i);
                        videoList.remove(i);
                        videoList.add(model);
                    }
                }

                adapter.setList(videoList);
                adapter.notifyDataSetChanged();
            }

        } else {

            check_density();

            Glide.with(this).load(header_text).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        txt_ar.setBackground(drawable);
//                            txtViewExplore.setBackgroundResource(R.color.black);
                    }
                }

            });

//            mapView.setVisibility(View.GONE);
            relativeAR.setVisibility(View.VISIBLE);
            relativeBlindSpot.setVisibility(View.GONE);
        }


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

        if (Values.carType == 11 || Values.carType == 12) {
            rlMapView.setVisibility(View.VISIBLE);
//            mapTextImage(new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang());
        } else {
            rlMapView.setVisibility(View.GONE);
        }
    }

    /**
     * here set the click listener
     */
    private void setListener() {
        btnAR.setOnClickListener(this);
        btnBlindSpotAR.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
//        mapView.setOnClickListener(this);
    }

    /**
     * initialized all global variable
     *
     * @param view for find fragment layout id
     */
    private void initViews(View view) {

        mContext = getActivity();
        preferenceUtil = new PreferenceUtil(mContext);
        txtViewExplore = (TextView) view.findViewById(R.id.txt_blind_spot_ar);
        txt_ar = (TextView) view.findViewById(R.id.txt_ar);

        tvPageTitle = (TextView) view.findViewById(R.id.txt_title_explore);
        relativeAR = (RelativeLayout) view.findViewById(R.id.relative_ar);
        relativeBlindSpot = (RelativeLayout) view.findViewById(R.id.relative_blind_spot);
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);

        btnAR = (Button) view.findViewById(R.id.btn_ar);
        btnBlindSpotAR = (RelativeLayout) view.findViewById(R.id.btn_blind_spot_ar);
        rlMapView = (RelativeLayout) view.findViewById(R.id.rlMapView);
        gridView = (ScrollableGridView) view.findViewById(R.id.grid_view);

        gridView.setFocusable(false);

/*
        // this code for prevent scrollview bottom display
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.smoothScrollTo(0, 0);
*/

        progressBar = (ProgressBar) view.findViewById(R.id.prog_explore);
        layoutDataNotFound = (LinearLayout) view.findViewById(R.id.layout_explore_data_not_found);
        tvNoContent = (TextView) view.findViewById(R.id.txt_explore_data_not_found);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        controller = new ExploreTabContentController(this);
        viewPager = (CustomViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.disableScroll(false);
        llLeftArrow = view.findViewById(R.id.llLeftArrow);
        llRightArrow = view.findViewById(R.id.llRightArrow);
        ivRight = (ImageView) view.findViewById(R.id.ivRightArrow);
        ivLeft = (ImageView) view.findViewById(R.id.ivLeftArrow);

        if (Values.carType == 12) {
            viewPager.disableScroll(true);
            llLeftArrow.setVisibility(View.GONE);
            llRightArrow.setVisibility(View.GONE);
        }

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

            case R.id.ivLeftArrow:

                viewPager.setCurrentItem(getItem(-1), true);

/*
                if (viewPager.getCurrentItem() == 0) {
                    llLeftArrow.setVisibility(View.GONE);
                    llRightArrow.setVisibility(View.VISIBLE);
                } else if (viewPager.getCurrentItem() == 1) {
                    llLeftArrow.setVisibility(View.VISIBLE);
                    llRightArrow.setVisibility(View.VISIBLE);
                } else {
                    llLeftArrow.setVisibility(View.VISIBLE);
                    llRightArrow.setVisibility(View.GONE);
                }
*/

                if (viewPager.getCurrentItem() == 0) {
                    llLeftArrow.setVisibility(View.GONE);
                    llRightArrow.setVisibility(View.VISIBLE);
                } else {
                    llLeftArrow.setVisibility(View.VISIBLE);
                    llRightArrow.setVisibility(View.GONE);
                }


                break;

            case R.id.ivRightArrow:

                viewPager.setCurrentItem(getItem(+1), true);

/*
                if (viewPager.getCurrentItem() == 0) {
                    llLeftArrow.setVisibility(View.GONE);
                    llRightArrow.setVisibility(View.VISIBLE);
                } else if (viewPager.getCurrentItem() == 1) {
                    llLeftArrow.setVisibility(View.VISIBLE);
                    llRightArrow.setVisibility(View.VISIBLE);
                } else {
                    llLeftArrow.setVisibility(View.VISIBLE);
                    llRightArrow.setVisibility(View.GONE);
                }
*/

                if (viewPager.getCurrentItem() == 0) {
                    llLeftArrow.setVisibility(View.GONE);
                    llRightArrow.setVisibility(View.VISIBLE);
                } else {
                    llLeftArrow.setVisibility(View.VISIBLE);
                    llRightArrow.setVisibility(View.GONE);
                }

                break;

            case R.id.btn_ar:
                // here start the ImageTargetActivity class for AR
            case R.id.btn_blind_spot_ar:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestCameraPermission();
                } else {
                    startActivity(new Intent(getActivity(), ImageTargetActivity.class));
                }
                break;

/*
            case R.id.map_view:
                if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                    list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);
                }
                Values.ePubType = Values.HOMEPAGE_TYPE;

                if(list == null || list.size() == 0)
                    return;

                Fragment frag = DetailsFragment.newInstance(list.get(52).getIndex(), resources.getString(R.string.updating_map_data));
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabExplore);
                ft.commit();
                break;
*/


            default:
                break;
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    /**
     * Requesting camera permission
     * This uses single permission model from dexter
     * Once the permission granted, opens the camera
     * On permanent denial opens settings dialog
     */
    private void requestCameraPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(android.Manifest.permission.CAMERA)
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
        if (Values.carType == 11 || Values.carType == 12) {
            if (lang.equalsIgnoreCase("en")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_en);
            } else if (lang.equalsIgnoreCase("de")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_de);
            } else if (lang.equalsIgnoreCase("ru")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_ru);
            } else if (lang.equalsIgnoreCase("sv")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_sv);
            } else if (lang.equalsIgnoreCase("es")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_es);
            } else if (lang.equalsIgnoreCase("nl")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_nl);
            } else if (lang.equalsIgnoreCase("no")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_no);
            } else if (lang.equalsIgnoreCase("fr")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_fr);
            } else if (lang.equalsIgnoreCase("it")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_it);
            } else if (lang.equalsIgnoreCase("pl")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_pl);
            } else if (lang.equalsIgnoreCase("fi")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_fi);
            } else if (lang.equalsIgnoreCase("pt")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_pt);
            } else if (lang.equalsIgnoreCase("da")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_da);
            } else if (lang.equalsIgnoreCase("cs")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_cs);
            } else if (lang.equalsIgnoreCase("hu")) {
                textViewMap.setBackgroundResource(R.drawable.micra_map_hu);
            } else {
                // default english
                textViewMap.setBackgroundResource(R.drawable.micra_map_en);
            }
        }
    }

    private void mapTextImage2(String lang) {
        if (Values.carType == 11 || Values.carType == 12) {
            if (lang.equalsIgnoreCase("en")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_en);
            } else if (lang.equalsIgnoreCase("de")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_de);
            } else if (lang.equalsIgnoreCase("ru")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_ru);
            } else if (lang.equalsIgnoreCase("sv")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_sv);
            } else if (lang.equalsIgnoreCase("es")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_es);
            } else if (lang.equalsIgnoreCase("nl")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_nl);
            } else if (lang.equalsIgnoreCase("no")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_no);
            } else if (lang.equalsIgnoreCase("fr")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_fr);
            } else if (lang.equalsIgnoreCase("it")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_it);
            } else if (lang.equalsIgnoreCase("pl")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_pl);
            } else if (lang.equalsIgnoreCase("fi")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_fi);
            } else if (lang.equalsIgnoreCase("pt")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_pt);
            } else if (lang.equalsIgnoreCase("da")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_da);
            } else if (lang.equalsIgnoreCase("cs")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_cs);
            } else if (lang.equalsIgnoreCase("hu")) {
                textViewMap2.setBackgroundResource(R.drawable.qashqaimc_map_hu);
            } else {
                // default english
                textViewMap.setBackgroundResource(R.drawable.micra_map_en);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        //while swipes between pages, imgArrow is visible or gone
/*
        if (viewPager.getCurrentItem() == 0) {
            llLeftArrow.setVisibility(View.GONE);
            llRightArrow.setVisibility(View.VISIBLE);
        } else if (viewPager.getCurrentItem() == 1) {
            llLeftArrow.setVisibility(View.VISIBLE);
            llRightArrow.setVisibility(View.VISIBLE);
        } else {
            llLeftArrow.setVisibility(View.VISIBLE);
            llRightArrow.setVisibility(View.GONE);
        }
*/

        if (viewPager.getCurrentItem() == 0) {
            llLeftArrow.setVisibility(View.GONE);
            llRightArrow.setVisibility(View.VISIBLE);
        } else {
            llLeftArrow.setVisibility(View.VISIBLE);
            llRightArrow.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyPagerAdapter extends PagerAdapter {


        //view inflating..
        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = null;

            switch (position) {
                case 0:
                    layout = (ViewGroup) inflater.inflate(R.layout.mapview_page_1,
                            collection, false);

                    textViewMap = (TextView) layout.findViewById(R.id.txt_map);
                    mapView = (LinearLayout) layout.findViewById(R.id.map_view);
                    if (Values.carType == 12) {
                        Uri uri = new Uri.Builder()
                                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                                .path(String.valueOf(R.drawable.map_new))
                                .build();
                        ((SimpleDraweeView) layout.findViewById(R.id.drawee_view_map_1)).setImageURI(uri);
                    }

                    mapTextImage(new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang());

                    mapView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                                list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);
                            }
                            Values.ePubType = Values.HOMEPAGE_TYPE;

                            if (list == null || list.size() == 0)
                                return;

                            Fragment frag = DetailsFragment.newInstance(list.get(Values.carType == 12 ? 58 : 52).getIndex(), resources.getString(R.string.updating_map_data));
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                            ft.replace(R.id.container, frag);
                            ft.addToBackStack(Values.tabExplore);
                            ft.commit();
                        }
                    });
                    break;
                case 1:
                    layout = (ViewGroup) inflater.inflate(R.layout.mapview_page_2,
                            collection, false);

                    textViewMap2 = (TextView) layout.findViewById(R.id.txt_map);

                    Logger.error("short_code", "______" + preferenceUtil.getSelectedLang());
                    int drawable = mContext.getResources().getIdentifier("micra_map_2_" + preferenceUtil.getSelectedLang().toLowerCase(), "drawable", getActivity().getPackageName());

                    ((ImageView) layout.findViewById(R.id.drawee_view_map_2)).setImageResource(drawable);

                    layout.findViewById(R.id.ivMap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // here start the playing video for grid view item click

                            if (DetectConnection.checkInternetConnection(getActivity())) {
                                //just for rus

                                int index = -1;
                                if (videoList == null || videoList.size() == 0)
                                    return;

                                for (int i = 0; i < videoList.size(); i++) {

                                    if (videoList.get(i).getTag() == 997) {
                                        index = i;
                                        break;
                                    }

                                }

                                if (index == -1)
                                    return;

                                Values.videoIndex = index;

                                if (NissanApp.getInstance().getExploreVideoList().get(index).getVideoUrl() != null) {

                                    startActivity(new Intent(getActivity(), VideoPlayerActivity.class).putExtra("from_where", "map"));
                                }


                            } else {

                                Toast.makeText(getActivity(), internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
                case 2:

                    layout = (ViewGroup) inflater.inflate(R.layout.mapview_page_3,
                            collection, false);

                    layout.findViewById(R.id.ivMap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // here start the playing video for grid view item click


                            String pdfPath = Values.car_path + File.separator + Values.MAP_PDF_FOLDER + File.separator;

                            final File file_pdf_folder = new File(pdfPath);
                            final File file_pdf = new File(pdfPath + Values.MAP_PDF_NAME);

                            if (file_pdf_folder.isDirectory() && file_pdf_folder.exists()) {
                                if (file_pdf.exists()) {
                                    openPDFFile(file_pdf);
                                    return;
                                }
                            } else {
                                file_pdf_folder.mkdirs();
                            }

                            if (DetectConnection.checkInternetConnection(getActivity())) {

                                progressDialog = new ProgressDialogController(getActivity()).showDialog(resources.getString(R.string.start_download));

                                // Setting timeout globally for the download network requests:
                                PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                                        .setReadTimeout(30_000)
                                        .setConnectTimeout(30_000)
                                        .build();
                                PRDownloader.initialize(getActivity(), config);


                                PRDownloader.download("http://www.sciencemag.org/site/special/data/ScienceData-hi.pdf",
                                        pdfPath, Values.MAP_PDF_NAME)
                                        .build()
                                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                            @Override
                                            public void onStartOrResume() {
                                                progressDialog.setMessage(resources.getString(R.string.alert_downloading));
                                            }
                                        })
                                        .setOnPauseListener(new OnPauseListener() {
                                            @Override
                                            public void onPause() {

                                            }
                                        })
                                        .setOnCancelListener(new OnCancelListener() {
                                            @Override
                                            public void onCancel() {

                                            }
                                        })
                                        .setOnProgressListener(new OnProgressListener() {
                                            @Override
                                            public void onProgress(Progress progress) {

                                                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                                progressDialog.setMessage(resources.getString(R.string.alert_downloading) + "  " + (int) progressPercent + "%");
                                            }
                                        })
                                        .start(new OnDownloadListener() {
                                            @Override
                                            public void onDownloadComplete() {
                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                openPDFFile(file_pdf);
                                            }

                                            @Override
                                            public void onError(Error error) {
                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                if (error.isConnectionError())
                                                    Toast.makeText(mContext, "Connection error occurred!", Toast.LENGTH_SHORT).show();
                                                if (error.isServerError())
                                                    Toast.makeText(mContext, "Server error occurred!", Toast.LENGTH_SHORT).show();
                                            }


                                        });

                            } else {

                                Toast.makeText(getActivity(), internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private void openPDFFile(File file_pdf) {

//        startActivity(new Intent(getActivity(), PDFOpenActivity.class));

                Intent target = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getPackageName() + ".provider",
                        file_pdf);
                target.setDataAndType(uri,"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // Instruct the user to install a PDF reader here, or something
                    e.printStackTrace();
                }

    }

}
