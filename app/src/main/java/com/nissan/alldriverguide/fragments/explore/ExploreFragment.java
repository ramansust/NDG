package com.nissan.alldriverguide.fragments.explore;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.datasoft.downloadManager.epubUtils.EpubInfo;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nissan.alldriverguide.DetailsActivity;
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
import com.nissan.alldriverguide.multiLang.model.ExploretabSliderModel;
import com.nissan.alldriverguide.multiLang.model.FrontImg;
import com.nissan.alldriverguide.utils.CustomViewPager;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.view.ScrollableGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, CompleteExploreTabContent, ViewPager.OnPageChangeListener {

    private Context mContext;
    private Button btnAR;
    private RelativeLayout btnBlindSpotAR, relativeAR, relativeBlindSpot, rlMapView;
    private LinearLayout llTitleVideo;

    private ScrollableGridView gridView;
    private GridViewAdapter adapter;

    private Resources resources;
    private TextView tvNoContent, tvPageTitle, simple_drawee_view_explore, simple_drawee_view_ar;
    private RelativeLayout infoImageButton;
    private ImageButton infoImageBtn;
    private RelativeLayout infoImageOldButton;
    private ImageButton infoImageOldBtn;
    private TextView txtViewVideolistTitle;
    private ProgressBar progressBar;
    private String sharedpref_key;
    private ArrayList<ExploreTabVideoModel> videoList = null;
    private ArrayList<ExploretabSliderModel> sliderModelArrayList;
    private String header_text;
    private String front_image_url;
    private String back_image_url;
    private ExploreTabModel exploreModel;
    private String device_density, internetCheckMessage = "";
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progress_bar;
    private ExploreTabContentController controller;
    private LinearLayout layoutDataNotFound, llLeftArrow, llRightArrow;
    private List<EpubInfo> list;
    private CustomViewPager viewPager;
    private ImageView ivRight, ivLeft;
    private int exploreSlidedItems = 0;
    ViewGroup layout;
    private int languageid;

    public ExploreFragment() {
    }

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
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        initViews(view);
        device_density = NissanApp.getInstance().getDensityName(requireActivity());
        loadResource();
        setListener();
        exploreSlidedItems = NissanApp.getInstance().getSlideItems(Values.carType);
        getExploreTabContent();
        showTitle();

        return view;
    }

    private void showTitle() {
        String title = NissanApp.getInstance().getTabTitle(getActivity(), "1");
        tvPageTitle.setText(title.isEmpty() ? resources.getString(R.string.explore) : title);

        /*String discoverYourTechText = NissanApp.getInstance().getAlertMessage(mContext,new PreferenceUtil(getActivity()).getSelectedLang(), Values.DISCOVER_YOUR_VEHICLE_TECH);
        tvDiscoverYourVehicle.setText(discoverYourTechText == null || discoverYourTechText.isEmpty() ? resources.getString(R.string.discover_your_vehicle) : discoverYourTechText);*/
    }

    private void getExploreTabContent() {
        videoList = new ArrayList<>();
        sliderModelArrayList = new ArrayList<>();
        adapter = new GridViewAdapter(requireActivity().getApplicationContext(), videoList, device_density);
        gridView.setAdapter(adapter);

        sharedpref_key = Values.carType + "_" + Values.EXPLORE_OBJ_STORE_KEY;
        exploreModel = new PreferenceUtil(getActivity()).retrieveExploreDataList(sharedpref_key);
        if (exploreModel != null && exploreModel.getVideoList() != null && exploreModel.getVideoList().size() > 0) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.GONE);
            check_density();
            videoList = exploreModel.getVideoList();
            sliderModelArrayList = exploreModel.getMapImageList();
            NissanApp.getInstance().setExploreVideoList(videoList);
            loadData();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if (!DetectConnection.checkInternetConnection(requireActivity())) {
                progressBar.setVisibility(View.GONE);
                internetCheckMessage = NissanApp.getInstance().getAlertMessage(this.getActivity(), new PreferenceUtil(getActivity()).getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
                showNoInternetDialogue(internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage);
                return;
            }
        }
        int language_ID = NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang());
        controller.callApi(NissanApp.getInstance().getDeviceID(requireActivity()), "" + language_ID, "" + Values.carType, Values.EPUBID, "1");
        languageid = language_ID;
    }

    private void showNoInternetDialogue(String msg) {
        final Dialog dialog = new DialogController(getActivity()).internetDialog();
        dialog.setCancelable(false);
        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            requireActivity().finish();
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

    public void setFrontImageExploreMiddleTab(ExploretabSliderModel exploreTabSliderModel, FrontImg frontImg) {
        if (device_density.equalsIgnoreCase("xxxhdpi")) {
            Logger.error("density ", " xxxhdpi ");
            front_image_url = frontImg.getFrontThumbXxxhdpi();
            back_image_url = exploreTabSliderModel.getBackThumbXxxhdpi();
        } else if (device_density.equalsIgnoreCase("xxhdpi")) {
            front_image_url = frontImg.getFrontThumbXxhdpi();
            Logger.error("density ", " xxhdpi ");
            back_image_url = exploreTabSliderModel.getBackThumbXxhdpi();
        } else if (device_density.equalsIgnoreCase("xhdpi")) {
            front_image_url = frontImg.getFrontThumbXhdpi();
            Logger.error("density ", " xhdpi ");
            back_image_url = exploreTabSliderModel.getBackThumbXhdpi();
        } else if (device_density.equalsIgnoreCase("hdpi")) {
            front_image_url = frontImg.getFrontThumbHdpi();
            back_image_url = exploreTabSliderModel.getBackThumbHdpi();
        } else if (device_density.equalsIgnoreCase("ldpi")) {
            front_image_url = frontImg.getFrontThumbLdpi();
            back_image_url = exploreTabSliderModel.getBackThumbLdpi();
        } else {
            front_image_url = frontImg.getFrontThumbXhdpi();
            back_image_url = exploreTabSliderModel.getBackThumbXhdpi();
            Logger.error("density ", " elsehdpi ");
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
            new PreferenceUtil(requireActivity().getApplicationContext()).storeExploreDataList(responseInfo, sharedpref_key);
            videoList = new ArrayList<>();
            exploreModel = responseInfo;
            check_density();
            videoList = exploreModel.getVideoList();
            sliderModelArrayList = exploreModel.getMapImageList();
            NissanApp.getInstance().setExploreVideoList(videoList);

            if (sliderModelArrayList != null) exploreSlidedItems = sliderModelArrayList.size();

            //get current car model object
            NissanApp.getInstance().setCurrentCarModel(Values.carType);
            loadData();

            //set pager data
            viewPager.setAdapter(new MyPagerAdapter());
            viewPager.disableScroll(false);
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

        //video title load from backend
        if (exploreModel != null) {
            if (exploreModel.getVideoHeaderTitle() != null) {
                llTitleVideo.setVisibility(View.VISIBLE);
                txtViewVideolistTitle.setText(exploreModel.getVideoHeaderTitle());
            } else llTitleVideo.setVisibility(View.GONE);
        }

        if (Values.carType == 11 || Values.carType == 12
                || Values.carType == 16 || Values.carType == 13
                || Values.carType == 14 || Values.carType == 15 || Values.carType == 17 || Values.carType == 18) {
            btnAR.setBackgroundResource(R.drawable.ar_selector_new_car);
        } else if (Values.carType == 20) {
            //for j12 qashqai 2021 car drawable is different
            btnAR.setBackgroundResource(R.drawable.ar_selector_j12);
        } else {
            btnAR.setBackgroundResource(R.drawable.ar_selector);
        }

        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4 || Values.carType == 10) {
            btnBlindSpotAR.setBackgroundResource(R.drawable.explore_xtrail_eur);
        } else if (Values.carType == 11 || Values.carType == 12 || Values.carType == 16
                || Values.carType == 13 || Values.carType == 14 || Values.carType == 15 || Values.carType == 17 || Values.carType == 18 || Values.carType == 19) {
            btnBlindSpotAR.setBackgroundResource(R.drawable.explore_micra_new);
        }

        if (Values.carType == 1 || Values.carType == 3 || Values.carType == 4
                || Values.carType == 10 || Values.carType == 11 || Values.carType == 12
                || Values.carType == 16
                || Values.carType == 13 || Values.carType == 14 || Values.carType == 15 || Values.carType == 17
                || Values.carType == 18 || Values.carType == 19) {

            relativeBlindSpot.setVisibility(View.VISIBLE);
            relativeAR.setVisibility(View.GONE);

            if (header_text != null) {

                check_density();

               /* String augmentedRealityText = NissanApp.getInstance().getAlertMessage(mContext,new PreferenceUtil(getActivity()).getSelectedLang(), Values.AUGMENTED_REALITY);
                String exploreYourCarText = NissanApp.getInstance().getAlertMessage(mContext,new PreferenceUtil(getActivity()).getSelectedLang(), Values.EXPLORE_YOUR_CAR);

                setTextToViews(tvAugmentedReality, augmentedRealityText == null || augmentedRealityText.isEmpty() ? resources.getString(R.string.augmented_reality) : augmentedRealityText, R.color.white);
                setTextToViews(tvExploreYourCar, exploreYourCarText == null || exploreYourCarText.isEmpty() ? resources.getString(R.string.explore_your_car) : exploreYourCarText, R.color.black);*/


                Glide.with(this).asBitmap().load(header_text).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(requireActivity().getResources(), resource);
                        simple_drawee_view_explore.setBackground(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                });

/*
                Glide.with(this).load(header_text).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            tvAugmentedReality.setBackground(drawable);
                        }
                    }

                });
*/

                if (videoList != null) {
                    if (videoList.size() > 0) {

                        Collections.sort(videoList, (lhs, rhs) -> lhs.getIndex().compareTo(rhs.getIndex()));

                        for (int i = 0; i < videoList.size(); i++) {

                            if (videoList.get(i).getTag() == 997) {
                                ExploreTabVideoModel model = videoList.get(i);
                                videoList.remove(i);
                                videoList.add(model);
                            }
                        }

                        adapter.setList(videoList);
                        adapter.notifyDataSetChanged();
                    } else {
                        gridView.setAdapter(null);
                        gridView.setVisibility(View.GONE);
                    }
                } else {
                    gridView.setAdapter(null);
                    gridView.setVisibility(View.GONE);
                }
            }

        } else {

            check_density();

           /* setTextToViews(tvAugmentedRealityOldCar, resources.getString(R.string.augmented_reality), R.color.white);
            setTextToViews(tvExploreYourCarOldCar, resources.getString(R.string.explore_your_car), R.color.black);*/

            Glide.with(this).asBitmap().load(header_text).into(new CustomTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Drawable drawable = new BitmapDrawable(requireActivity().getResources(), resource);
                    simple_drawee_view_ar.setBackground(drawable);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }

            });
            /*
            Glide.with(this).load(header_text).asBitmap().into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getActivity().getResources(), resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            simple_drawee_view_ar.setBackground(drawable);
                        }
                    }
            });
*/


            relativeAR.setVisibility(View.VISIBLE);
            relativeBlindSpot.setVisibility(View.GONE);
        }

        check_density();
        if (header_text != null) {
            Glide.with(this).asBitmap().load(header_text).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Drawable drawable = new BitmapDrawable(requireActivity().getResources(), resource);
                    simple_drawee_view_explore.setBackground(drawable);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }

            });
        }

        //Dynamic load Map & Epub View
        if (exploreSlidedItems > 0) {
            rlMapView.setVisibility(View.VISIBLE);
        } else {
            rlMapView.setVisibility(View.GONE);
        }

        check_density();
        if (header_text != null) {
            Glide.with(this).asBitmap().load(header_text).into(new CustomTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Drawable drawable = new BitmapDrawable(requireActivity().getResources(), resource);
                    simple_drawee_view_ar.setBackground(drawable);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }

            });
        }

        if (Values.carType == 18) {
            if (languageid == 1) {
                Logger.error("Explore juke ", " " + Values.carType + "  LAN " + languageid);
            } else {
                relativeAR.setVisibility(View.VISIBLE);
                relativeBlindSpot.setVisibility(View.GONE);
                Logger.error("Explore juke ", " other " + Values.carType + "  LAN " + languageid);
            }
        }
    }

   /* private void setTextToViews(TextView textView, String text, int backgroundColor) {
        int padding = 20; // in pixels
        textView.setShadowLayer(padding *//* radius *//*, 0, 0, 0 *//* transparent *//*);
        textView.setPadding(padding, 0, padding, 0);

        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new PaddingBackgroundColorSpan(
                getResources().getColor(backgroundColor),
                padding
        ), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }*/

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
        infoImageButton.setOnClickListener(this);
        infoImageOldButton.setOnClickListener(this);
        infoImageOldBtn.setOnClickListener(this);
        infoImageBtn.setOnClickListener(this);
//        mapView.setOnClickListener(this);
    }

    /**
     * initialized all global variable
     *
     * @param view for find fragment layout id
     */
    private void initViews(View view) {
        mContext = getActivity();
        simple_drawee_view_explore = view.findViewById(R.id.simple_drawee_view_blind_spot_ar);
        simple_drawee_view_ar = view.findViewById(R.id.simple_drawee_view_ar);

        tvPageTitle = view.findViewById(R.id.txt_title_explore);
        relativeAR = view.findViewById(R.id.relative_ar);
        relativeBlindSpot = view.findViewById(R.id.relative_blind_spot);
        llTitleVideo = view.findViewById(R.id.llTitleVideo);
        progress_bar = view.findViewById(R.id.progress_bar);

        btnAR = view.findViewById(R.id.btn_ar);
        btnBlindSpotAR = view.findViewById(R.id.btn_blind_spot_ar);
        infoImageButton = view.findViewById(R.id.layout_info);
        infoImageOldButton = view.findViewById(R.id.layout_info_old);
        infoImageOldBtn = view.findViewById(R.id.info_explore_old);
        infoImageBtn = view.findViewById(R.id.info_explore);
        //discover title text
        txtViewVideolistTitle = view.findViewById(R.id.videolist_title);
        rlMapView = view.findViewById(R.id.rlMapView);
        gridView = view.findViewById(R.id.grid_view);
        gridView.setFocusable(false);

/*
        // this code for prevent scrollview bottom display
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.smoothScrollTo(0, 0);
*/

        progressBar = view.findViewById(R.id.prog_explore);
        layoutDataNotFound = view.findViewById(R.id.layout_explore_data_not_found);
        tvNoContent = view.findViewById(R.id.txt_explore_data_not_found);

        controller = new ExploreTabContentController(this);
        viewPager = view.findViewById(R.id.viewPager);
        /*viewPager.setAdapter(new MyPagerAdapter());
        viewPager.disableScroll(false);*/
        llLeftArrow = view.findViewById(R.id.llLeftArrow);
        llRightArrow = view.findViewById(R.id.llRightArrow);
        ivRight = view.findViewById(R.id.ivRightArrow);
        ivLeft = view.findViewById(R.id.ivLeftArrow);
    }

    // load resources for language localized
    private void loadResource() {
        resources = new Resources(requireActivity().getAssets(), new DisplayMetrics(), NissanApp.getInstance().changeLocalLanguage(requireActivity(), new PreferenceUtil(requireActivity().getApplicationContext()).getSelectedLang()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivLeftArrow:
                viewPager.setCurrentItem(getItem(-1), true);
                break;

            case R.id.ivRightArrow:
                viewPager.setCurrentItem(getItem(+1), true);
                break;

            /*case R.id.btn_ar:
                // here start the ImageTargetActivity class for AR
                break;*/

            case R.id.layout_info_old:
            case R.id.layout_info:
            case R.id.info_explore_old:
            case R.id.info_explore:
                Values.ePubType = Values.INFO_TYPE;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("epub_index", 0);
                intent.putExtra("epub_title", "INFO");
                startActivity(intent);
                break;

            case R.id.btn_ar:
            case R.id.btn_blind_spot_ar:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestCameraPermission();
                } else {
                    startActivity(new Intent(getActivity(), ImageTargetActivity.class));
                }
                break;

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
        Dexter.withContext(requireActivity().getApplicationContext())
                .withPermission(android.Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        if (ContextCompat.checkSelfPermission(requireActivity(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Values.videoIndex = position;
        // here start the playing video for grid view item click

        if (DetectConnection.checkInternetConnection(requireActivity())) {

            if (NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getVideoUrl() != null) {
                startActivity(new Intent(getActivity(), VideoPlayerActivity.class));
            }

        } else {
            Toast.makeText(getActivity(), internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage, Toast.LENGTH_SHORT).show();
        }

    }

    /*private void mapTextImage(String lang) {
        if (Values.carType == 11 || Values.carType == 12) {
            if (lang.equalsIgnoreCase("en")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_en);
            } else if (lang.equalsIgnoreCase("de")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_de);
            } else if (lang.equalsIgnoreCase("ru")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_ru);
            } else if (lang.equalsIgnoreCase("sv")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_sv);
            } else if (lang.equalsIgnoreCase("es")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_es);
            } else if (lang.equalsIgnoreCase("nl")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_nl);
            } else if (lang.equalsIgnoreCase("no")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_no);
            } else if (lang.equalsIgnoreCase("fr")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_fr);
            } else if (lang.equalsIgnoreCase("it")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_it);
            } else if (lang.equalsIgnoreCase("pl")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_pl);
            } else if (lang.equalsIgnoreCase("fi")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_fi);
            } else if (lang.equalsIgnoreCase("pt")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_pt);
            } else if (lang.equalsIgnoreCase("da")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_da);
            } else if (lang.equalsIgnoreCase("cs")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_cs);
            } else if (lang.equalsIgnoreCase("hu")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_hu);
            } else if (lang.equalsIgnoreCase("et")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_et);
            } else if (lang.equalsIgnoreCase("lv")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_lv);
            } else if (lang.equalsIgnoreCase("lt")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_lt);
            } else if (lang.equalsIgnoreCase("sk")) {
                imageViewMap.setBackgroundResource(R.drawable.micra_map_sk);
            } else {
                // default english
                imageViewMap.setBackgroundResource(R.drawable.micra_map_en);
            }
        }
    }*/

    /*private void mapTextImage2(String lang) {
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
                imageViewMap.setBackgroundResource(R.drawable.micra_map_en);
            }
        }
    }*/

    public void arrowVisibleEnable(int leftValue, int rightValue) {
        llLeftArrow.setVisibility(leftValue);
        llRightArrow.setVisibility(rightValue);
    }

    @Override
    public void onPageSelected(int position) {
        Objects.requireNonNull(viewPager.getAdapter()).getCount();//Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public ViewGroup epubView(ViewGroup collection, LayoutInflater inflater, final ExploretabSliderModel exploretabSliderModel) {

        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.mapview_page_1,
                collection, false);
                  /*  textViewNissanConnect = (TextView) layout.findViewById(R.id.tvNewNissanConnect);
                    textViewNissanConnect.setTypeface(typefaceRegular);
                    textViewUpdateYourMap = (TextView) layout.findViewById(R.id.tvUpdateYourMap);
                    textViewUpdateYourMap.setTypeface(typefaceBold);*/
        LinearLayout mapView = layout.findViewById(R.id.map_view);

        setFrontImageExploreMiddleTab(exploretabSliderModel, exploretabSliderModel.getFrontImg());

        @SuppressLint("CutPasteId") ImageView front_image_view = layout.findViewById(R.id.txt_map);
        if (front_image_url != null) {
            Glide.with(this).asBitmap().load(front_image_url).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Drawable drawable = new BitmapDrawable(requireActivity().getResources(), resource);
                    front_image_view.setBackground(drawable);
                    front_image_view.buildDrawingCache();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }
        @SuppressLint("CutPasteId") ImageView back_image_view = layout.findViewById(R.id.drawee_view_map_1);

        if (back_image_url != null) {
            Glide.with(this).asBitmap().load(back_image_url).fitCenter().into(back_image_view);
        }

        mapView.setOnClickListener(view -> {
            if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(requireActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
                list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(requireActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);
            }

//                Values.ePubType = Values.HOMEPAGE_TYPE;

            if (list == null || list.size() == 0)
                return;

            //String updatingMapText = NissanApp.getInstance().getAlertMessage(mContext,new PreferenceUtil(getActivity()).getSelectedLang(), Values.UPDATING_MAP_DATA);

            /*int epubIndex = 52;
            if (Values.carType == 12) {
                epubIndex = 58;
            } else if (Values.carType == 10) {
                epubIndex = 46;
            } else if (Values.carType == 11) {
                epubIndex = 54;
            } else {
                epubIndex = 52;
            }*/

            int epubIndex;
            Values.ePubType = exploretabSliderModel.getEpubType();
            epubIndex = exploretabSliderModel.getEpubTag();

            /*if (Values.carType == 12) {
                epubIndex = 58;
            } else if (Values.carType == 10) {
                epubIndex = 46;
            } else if (Values.carType == 11) {
                epubIndex = 54;
            } else if (Values.carType == 18) {
                epubIndex = 60;
            }*/

            /*Logger.error("Epub type " , " " + Values.ePubType);
            Logger.error("Epub Tag " , " " + epubIndex);*/

            Fragment frag = DetailsFragment.newInstance(list.get(epubIndex * 2).getIndex(), resources.getString(R.string.updating_map_data));
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            ft.replace(R.id.container, frag);
            ft.addToBackStack(Values.tabExplore);
            ft.commit();

            /*if(Values.carType == 18){
                Fragment frag = DetailsFragment.newInstance(60, resources.getString(R.string.updating_map_data));
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabExplore);
                ft.commit();
            }else{
                Fragment frag = DetailsFragment.newInstance(list.get(epubIndex).getIndex(), resources.getString(R.string.updating_map_data));
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabExplore);
                ft.commit();
            }*/
        });

        return layout;
    }

    //layout for map layout for explore slider part
    public ViewGroup mapView(ViewGroup collection, LayoutInflater inflater, final ExploretabSliderModel exploretabSliderModel) {

        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.mapview_page_2, collection, false);

        setFrontImageExploreMiddleTab(exploretabSliderModel, exploretabSliderModel.getFrontImg());

        ImageView front_image_view = layout.findViewById(R.id.drawee_view_map_2);

        if (front_image_url != null) {
            Glide.with(this).asBitmap().load(front_image_url).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Drawable drawable = new BitmapDrawable(requireActivity().getResources(), resource);
                    front_image_view.setBackground(drawable);
                    front_image_view.buildDrawingCache();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        }

        ImageView back_image_view = layout.findViewById(R.id.ivMap);

        if (back_image_url != null) {
            Glide.with(this).asBitmap().load(back_image_url).into(back_image_view);
        }

        //int drawable = mContext.getResources().getIdentifier("micra_map_2_" + preferenceUtil.getSelectedLang().toLowerCase(), "drawable", getActivity().getPackageName());
        //((ImageView) layout.findViewById(R.id.drawee_view_map_2)).setImageResource(drawable);

                    /*tvNissanDoorToDoor = layout.findViewById(R.id.tvNewNissanDoorToDoor);
                    tvSetUpGuide = layout.findViewById(R.id.tvSetupGuide);
                    tvNissanDoorToDoor.setTypeface(typefaceRegular);
                    tvSetUpGuide.setTypeface(typefaceBold);

                    String doorToDoorText = NissanApp.getInstance().getAlertMessage(mContext,new PreferenceUtil(getActivity()).getSelectedLang(), Values.MAP_DOOR_TO_DOOR);
                    String setUpGuideText = NissanApp.getInstance().getAlertMessage(mContext,new PreferenceUtil(getActivity()).getSelectedLang(), Values.MAP_SET_UP_GUIDE);


                    setTextToViews(tvNissanDoorToDoor, doorToDoorText == null || doorToDoorText.isEmpty() ? resources.getString(R.string.nissan_door_to_door_nav_text) : doorToDoorText, R.color.white);
                    setTextToViews(tvSetUpGuide, setUpGuideText == null || setUpGuideText.isEmpty() ? resources.getString(R.string.set_up_guide_text) : setUpGuideText, R.color.black);*/

        layout.findViewById(R.id.ivMap).setOnClickListener(view -> {
            // here start the playing video for grid view item click

            if (DetectConnection.checkInternetConnection(requireActivity())) {
                //just for rus

                /*int index = -1;
                if (videoList == null || videoList.size() == 0)
                    return;

                for (int i = 0; i < videoList.size(); i++) {

                    if (videoList.get(i).getTag() == 997 || videoList.get(i).getTag() == 46) { //video tag door to door MB
                        index = i;
                        break;
                    }

                }

                if (index == -1)
                    return;

                Values.videoIndex = index;

                if (NissanApp.getInstance().getExploreVideoList().get(index).getVideoUrl() != null) {

                    startActivity(new Intent(getActivity(), VideoPlayerActivity.class).putExtra("from_where", "map"));
                }*/

                if (exploretabSliderModel.getVideoUrl() != null)
                    NissanApp.getInstance().setMapVideoUrl(exploretabSliderModel.getVideoUrl());
                startActivity(new Intent(getActivity(), VideoPlayerActivity.class).putExtra("from_where", "map"));


            } else {
                Toast.makeText(getActivity(), internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage, Toast.LENGTH_SHORT).show();
            }
        });

        return layout;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Left Arrow Right arrow visibility functionality depending on viewpager items By Rohan
        int tempPosition = exploreSlidedItems - 1;
        if (position == 0) {
            if (position == tempPosition) arrowVisibleEnable(View.GONE, View.GONE);
            else arrowVisibleEnable(View.GONE, View.VISIBLE);
        } else if (position > 0) {
            if (position < tempPosition) {
                arrowVisibleEnable(View.VISIBLE, View.VISIBLE);
            } else if (position == tempPosition) {
                arrowVisibleEnable(View.VISIBLE, View.GONE);
            }
        }
        //Log.e("Slider Tab scroll " , " " + position + " temp pos " + tempPosition);
    }

    private class MyPagerAdapter extends PagerAdapter {
        //view inflating..
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = null;
            if (sliderModelArrayList != null) {
                if (sliderModelArrayList.size() > 0) {
                    if (sliderModelArrayList.get(position).getType() != null && !sliderModelArrayList.get(position).getType().isEmpty()) {
                        if (sliderModelArrayList.get(position).getType().equalsIgnoreCase(Values.epubTypeExploreVideo)) {
                            layout = mapView(collection, inflater, sliderModelArrayList.get(position));
                        } else
                            layout = epubView(collection, inflater, sliderModelArrayList.get(position));
                    }
                }
            }

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return exploreSlidedItems;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }

}
