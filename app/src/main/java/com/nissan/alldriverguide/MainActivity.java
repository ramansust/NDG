package com.nissan.alldriverguide;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.assistance.AssistanceFragment;
import com.nissan.alldriverguide.fragments.explore.ExploreFragment;
import com.nissan.alldriverguide.fragments.search.SearchFragment;
import com.nissan.alldriverguide.fragments.search.tab.BaseTabFragmentActivity;
import com.nissan.alldriverguide.fragments.settings.Feedback;
import com.nissan.alldriverguide.fragments.settings.SettingsFragment;
import com.nissan.alldriverguide.multiLang.model.LanguageList;
import com.nissan.alldriverguide.multiLang.model.TabMenu;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseTabFragmentActivity implements TabLayout.OnTabSelectedListener {

    public TabLayout tabLayout;
    private Tracker tracker;
    private String[] tabNames = new String[4];
    private int[] tabIconsSelected = {R.drawable.explore_selected, R.drawable.assistance_selected, R.drawable.search_pressed,
            R.drawable.settings_selected};
    private int[] tabIconsUnSelected = {R.drawable.explore_unselected, R.drawable.assistance_unselected, R.drawable.search,
            R.drawable.settings_unselected};
    // Start------------ For permission related constants
    private static final int PERMISSION_REQUEST_CODE_ALL = 200;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 201;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 202;
    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 203;
    // End------------ For permission related constants

    // Start------------ For tab indicator top
    private View tabIndicator1, tabIndicator2, tabIndicator3, tabIndicator4;
    private ImageView[] tabIcons;
    private TextView[] tabTextViews;
    // End------------ For tab indicator top
    private Typeface typeFaceBold, typeFaceNormal;

    public int currentTab;
    public int previousTab;
    private DisplayMetrics metrics;
    private Resources resources;

    private TextView tabTextView;
    private boolean isAnimation = false;

    private PreferenceUtil preferenceUtil;

    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            super.attachBaseContext(newBase);
        }
        //Or implement this for api 29 and above
        else {
            super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        loadResource();
        initViews();
        setTabResources();
        setListener();
        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Set the car path and select tab
     */
    private void loadData() {
        Values.car_path = NissanApp.getInstance().getCarPath(Values.carType);

        selectFragment(0);
        setTabIndicatorIconAndTextColor(0);
    }

    /**
     * Initialized All views
     */
    private void initViews() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        preferenceUtil = new PreferenceUtil(getApplicationContext());

        resources = new Resources(getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(MainActivity.this, new PreferenceUtil(getApplicationContext()).getSelectedLang()));

        tabIndicator1 = findViewById(R.id.tab_indicator_1);
        tabIndicator2 = findViewById(R.id.tab_indicator_2);
        tabIndicator3 = findViewById(R.id.tab_indicator_3);
        tabIndicator4 = findViewById(R.id.tab_indicator_4);
        tabIcons = new ImageView[tabIconsSelected.length];
        tabTextViews = new TextView[tabIconsSelected.length];

        typeFaceNormal = Typeface.createFromAsset(getAssets(), "font/Nissan Brand Regular.otf");
        typeFaceBold = Typeface.createFromAsset(getAssets(), "font/Nissan Brand Bold.otf");

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        setTabNames();
        setupTabLayout();
    }

    private void setTabNames() {

        ArrayList<TabMenu> tabMenuArrayList = getDataFromStorage();

        NissanApp.getInstance().setTabMenuArrayList(tabMenuArrayList);

        if (tabMenuArrayList != null && tabMenuArrayList.size() > 0) {
            for (int i = 0; i < tabMenuArrayList.size(); i++) {
                tabNames[i] = tabMenuArrayList.get(i).getTitle();
                Logger.error("Tab names", "---- " + tabNames[i]);
            }
        } else {
            tabNames = resources.getStringArray(R.array.tab_names);
        }

    }

    public void loadResource() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        resources = new Resources(getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(MainActivity.this, new PreferenceUtil(getApplicationContext()).getSelectedLang()));
    }

    /**
     * Setup tab resources
     */
    public void setTabResources() {
        loadResource();

        setTabNames();
//        tabNames = resources.getStringArray(R.array.tab_names);
        for (int i = 0; i < tabNames.length; i++) {
            tabTextViews[i].setText(tabNames[i]); // set the tab name in tab text
        }

        Type type = new TypeToken<ArrayList<LanguageList>>() {
        }.getType();
        List<LanguageList> carWiseLangList = new Gson().fromJson(preferenceUtil.retrieveMultiLangData(Values.carType + "_" + Values.CAR_LANGUAGE_LIST), type);

        NissanApp.getInstance().setCarWiseLanguageList(carWiseLangList);
        NissanApp.getInstance().setGlobalMessageArrayList(null);
        NissanApp.getInstance().setAlertMessageGlobalArrayList(null);
    }

    /**
     * Setup tab icon and text color color
     *
     * @param tabPosition compare and decoration
     */
    public void setTabIndicatorIconAndTextColor(int tabPosition) {
        switch (tabPosition) {
            case 0:
                tabIndicator1.setBackgroundColor(getResources().getColor(R.color.tab_indicator_selected));
                tabIndicator2.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator3.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator4.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));

                tabIcons[0].setBackgroundResource(tabIconsSelected[0]);
                tabIcons[1].setBackgroundResource(tabIconsUnSelected[1]);
                tabIcons[2].setBackgroundResource(tabIconsUnSelected[2]);
                tabIcons[3].setBackgroundResource(tabIconsUnSelected[3]);

                tabTextViews[0].setTextColor(getResources().getColor(R.color.tab_text_color_selected));
                tabTextViews[1].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[2].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[3].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));

                tabTextViews[0].setTypeface(typeFaceBold);
                tabTextViews[1].setTypeface(typeFaceNormal);
                tabTextViews[2].setTypeface(typeFaceNormal);
                tabTextViews[3].setTypeface(typeFaceNormal);
                break;

            case 1:
                tabIndicator1.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator2.setBackgroundColor(getResources().getColor(R.color.tab_indicator_selected));
                tabIndicator3.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator4.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));

                tabIcons[0].setBackgroundResource(tabIconsUnSelected[0]);
                tabIcons[1].setBackgroundResource(tabIconsSelected[1]);
                tabIcons[2].setBackgroundResource(tabIconsUnSelected[2]);
                tabIcons[3].setBackgroundResource(tabIconsUnSelected[3]);

                tabTextViews[0].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[1].setTextColor(getResources().getColor(R.color.tab_text_color_selected));
                tabTextViews[2].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[3].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));

                tabTextViews[0].setTypeface(typeFaceNormal);
                tabTextViews[1].setTypeface(typeFaceBold);
                tabTextViews[2].setTypeface(typeFaceNormal);
                tabTextViews[3].setTypeface(typeFaceNormal);
                break;

            case 2:
                tabIndicator1.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator2.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator3.setBackgroundColor(getResources().getColor(R.color.tab_indicator_selected));
                tabIndicator4.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));

                tabIcons[0].setBackgroundResource(tabIconsUnSelected[0]);
                tabIcons[1].setBackgroundResource(tabIconsUnSelected[1]);
                tabIcons[2].setBackgroundResource(tabIconsSelected[2]);
                tabIcons[3].setBackgroundResource(tabIconsUnSelected[3]);

                tabTextViews[0].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[1].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[2].setTextColor(getResources().getColor(R.color.tab_text_color_selected));
                tabTextViews[3].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));

                tabTextViews[0].setTypeface(typeFaceNormal);
                tabTextViews[1].setTypeface(typeFaceNormal);
                tabTextViews[2].setTypeface(typeFaceBold);
                tabTextViews[3].setTypeface(typeFaceNormal);
                break;

            case 3:
                tabIndicator1.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator2.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator3.setBackgroundColor(getResources().getColor(R.color.tab_indicator_unselected));
                tabIndicator4.setBackgroundColor(getResources().getColor(R.color.tab_indicator_selected));

                tabIcons[0].setBackgroundResource(tabIconsUnSelected[0]);
                tabIcons[1].setBackgroundResource(tabIconsUnSelected[1]);
                tabIcons[2].setBackgroundResource(tabIconsUnSelected[2]);
                tabIcons[3].setBackgroundResource(tabIconsSelected[3]);

                tabTextViews[0].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[1].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[2].setTextColor(getResources().getColor(R.color.tab_text_color_unselected));
                tabTextViews[3].setTextColor(getResources().getColor(R.color.tab_text_color_selected));

                tabTextViews[0].setTypeface(typeFaceNormal);
                tabTextViews[1].setTypeface(typeFaceNormal);
                tabTextViews[2].setTypeface(typeFaceNormal);
                tabTextViews[3].setTypeface(typeFaceBold);
                break;
        }
    }

    /**
     * Setup custom tab layout
     */
    private void setupTabLayout() {
        TabLayout.Tab tab;
        for (int i = 0; i < tabNames.length; i++) {
            tab = tabLayout.newTab();
            LinearLayout rootLayout = (LinearLayout)
                    LayoutInflater.from(this).inflate(R.layout.custom_tab, tabLayout, false);

            tabTextView = (TextView) rootLayout.findViewById(R.id.tab_title);
            tabTextViews[i] = tabTextView;
            tabTextView.setText(tabNames[i]);
            ImageView tabImageView = (ImageView) rootLayout.findViewById(R.id.iv_tab);
            tabIcons[i] = tabImageView;
            tab.setCustomView(rootLayout);
            tabLayout.addTab(tab);
        }
    }

    private void setListener() {
        tabLayout.addOnTabSelectedListener(this);
    }

    public void selectFragment(int tab) {
        Fragment frag = null;
        // init corresponding fragment
        switch (tab) {
            case 0:
                frag = ExploreFragment.newInstance();
                break;

            case 1:
                frag = AssistanceFragment.newInstance();
                break;

            case 2:
                frag = SearchFragment.newInstance();
                break;

            case 3:
                frag = SettingsFragment.newInstance();
                break;
        }

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (isAnimation) {
                if (currentTab > previousTab) {
                    ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                } else {
                    ft.setCustomAnimations(R.anim.left_in, R.anim.right_out, R.anim.right_in, R.anim.left_out);
                }
            }
            ft.replace(R.id.container, frag);
            ft.addToBackStack("");
            ft.commit();
            isAnimation = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        currentTab = tab.getPosition();
        selectFragment(tab.getPosition());
        setTabIndicatorIconAndTextColor(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        previousTab = tab.getPosition();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

            if (!tag.equalsIgnoreCase("")) {
                selectFragment(tab.getPosition());
                setTabIndicatorIconAndTextColor(tab.getPosition());
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

            if (Values.tabExplore.equalsIgnoreCase(tag)) {
                backStack();
            } else if (Values.tabAssistance.equalsIgnoreCase(tag)) {
                backStack();
            } else if (Values.tabSearch.equalsIgnoreCase(tag)) {
                backStack();
            } else if (Values.tabSettings.equalsIgnoreCase(tag)) {
                backStack();
            } else if (Values.tabSearchChildFragment.equalsIgnoreCase(tag)) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out, R.anim.right_in, R.anim.left_out);
                transaction.replace(R.id.container, SearchFragment.newInstance());
                transaction.addToBackStack("");
                transaction.commit();
            } else { // this block for great or not great popup

                if (preferenceUtil.getIsFirstTimeGreatNotGreat() && preferenceUtil.getOpenCountForRateApp() >= Values.RATE_APP_FIRST_SESSION) {

                    greatNotGreat();

                } else {

                    if (preferenceUtil.isGreat() && !preferenceUtil.getIsFirstTimeGreatNotGreat()) {

                        if (preferenceUtil.getSessionOne() && preferenceUtil.getOpenCountForRateApp() >= 15 ||
                                preferenceUtil.getSessionThree() && preferenceUtil.getOpenCountForRateApp() >= 45) {
                            rateOurApp();

                        } else {
                            backAlert();
                        }
                    } else if (!preferenceUtil.isGreat() && !preferenceUtil.getIsFirstTimeGreatNotGreat()) {

                        if (preferenceUtil.getSessionOne() && preferenceUtil.getOpenCountForRateApp() >= 15 ||
                                preferenceUtil.getSessionThree() && preferenceUtil.getOpenCountForRateApp() >= 75) {
                            feedBack();

                        } else {
                            backAlert();
                        }
                    } else {
                        backAlert();
                    }
                }
            }
        } else {
            if (new PreferenceUtil(getApplicationContext()).getOpenCountForRateApp() >= Values.RATE_APP_DIVISOR) {
//                rateOurApp();
            } else {
                backAlert();
            }
        }
    }

    private void backStack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (new PreferenceUtil(getApplicationContext()).getOpenCountForRateApp() % Values.RATE_APP_DIVISOR == 0) {
//                rateOurApp();
            } else {
                backAlert();
            }
        }
    }

    /**
     * Back button pressed popup
     */
    public void backAlert() {
        final Dialog dialog = new DialogController(MainActivity.this).langDialog();

        TextView txtViewTitle = (TextView) dialog.findViewById(R.id.txt_title);

        String okText = NissanApp.getInstance().getGlobalMessage(this).getOk();
        String cancelText = NissanApp.getInstance().getGlobalMessage(this).getCancel();

        Button btnYes = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        btnYes.setText(okText == null || okText.isEmpty() ? resources.getString(R.string.button_OK) : okText);
        btnCancel.setText(cancelText == null || cancelText.isEmpty() ? resources.getString(R.string.button_CANCEL) : cancelText);

        String exitDialogueText = NissanApp.getInstance().getAlertMessage(this, preferenceUtil.getSelectedLang(), Values.CONFIRM_EXIT_MESSAGE);

        txtViewTitle.setText(exitDialogueText == null || exitDialogueText.isEmpty() ? resources.getString(R.string.exit_alert) : exitDialogueText);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Values.ePubType = 0; // reset the epub type
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Values.ePubType = 0;
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Rate our app popup display
     */
    public void rateOurApp() {
        preferenceUtil.setOpenCountForRateApp();

        final Dialog dialog = new DialogController(MainActivity.this).rateOurAppDialog();

        TextView txtViewSubTitle = (TextView) dialog.findViewById(R.id.txt_sub_title);
        txtViewSubTitle.setText(getResources().getString(R.string.rate_our_app_sub_title));

        TextView txtViewNoThanks = (TextView) dialog.findViewById(R.id.txt_view_no_thanks);
        TextView txtViewAskMeLater = (TextView) dialog.findViewById(R.id.txt_view_ask_me_later);
        TextView txtViewRateThisApp = (TextView) dialog.findViewById(R.id.txt_view_rate_this_app);

        txtViewNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                preferenceUtil.setSessionThree(true);
                preferenceUtil.setSessionOne(false);
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
            }
        });

        txtViewAskMeLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                preferenceUtil.setSessionOne(true);
                preferenceUtil.setSessionThree(false);
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
            }
        });

        txtViewRateThisApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                preferenceUtil.setSessionOne(false);
                preferenceUtil.setSessionThree(false);
                preferenceUtil.setIsFirstTimeGreatNotGreat(false);
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
            }
        });

        dialog.show();
    }

    /**
     * Great or Not Great popup display when user click 30 times in application
     */
    public void greatNotGreat() {
        preferenceUtil.setOpenCountForRateApp();

        final Dialog dialog = new DialogController(MainActivity.this).greatNotGreatDialog();

        TextView txtGreat = (TextView) dialog.findViewById(R.id.txt_great);
        TextView txtNotGreat = (TextView) dialog.findViewById(R.id.txt_not_great);

        txtGreat.setText(getResources().getString(R.string.great));
        txtNotGreat.setText(getResources().getString(R.string.not_great));

        txtGreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                preferenceUtil.setIsGreat(true);
                preferenceUtil.setIsFirstTimeGreatNotGreat(false);
                rateOurApp();
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
            }
        });

        txtNotGreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                preferenceUtil.setIsGreat(false);
                preferenceUtil.setIsFirstTimeGreatNotGreat(false);
                feedBack();
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
            }
        });

        dialog.show();

    }

    /**
     * Feedback dialog
     */
    public void feedBack() {
        preferenceUtil.setOpenCountForRateApp();

        final Dialog dialog = new DialogController(MainActivity.this).feedBackDialog();

        TextView feedBackNoThanks = (TextView) dialog.findViewById(R.id.feedback_no_thanks);
        TextView feedBackAskMeLater = (TextView) dialog.findViewById(R.id.feedback_ask_me_later);
        TextView feedBackYes = (TextView) dialog.findViewById(R.id.feedback_yes);

        feedBackNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                preferenceUtil.setSessionThree(true);
                preferenceUtil.setSessionOne(false);
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
            }
        });

        feedBackAskMeLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                preferenceUtil.setSessionOne(true);
                preferenceUtil.setSessionThree(false);
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
            }
        });

        feedBackYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                preferenceUtil.setSessionOne(true);
                preferenceUtil.setSessionThree(false);
                new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();

                Fragment frag = Feedback.newInstance();

                if (frag != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                    ft.replace(R.id.container, frag);
                    ft.addToBackStack(Values.tabSettings);
                    ft.commit();
                }
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

        if (preferenceUtil.getIsFirstTimeGreatNotGreat()) {

        } else {
            preferenceUtil.setSessionOne(true);
            preferenceUtil.setSessionThree(false);
            new PreferenceUtil(getApplicationContext()).resetUserNavigationCount();
        }
    }

    public void sendMsgToGoogleAnalytics(String msgName) {
        // Get a Tracker (should auto-report)
        ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Get tracker.
        tracker = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        tracker.setScreenName(msgName.toLowerCase());
        tracker.enableAdvertisingIdCollection(true);
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private void requestPermission(final String PERMISSION_NAME, final int PERMISSION_CODE) {

        ActivityCompat.requestPermissions(this, new String[]{PERMISSION_NAME}, PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_ALL:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && storageAccepted) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)
                                    || shouldShowRequestPermissionRationale(CAMERA)
                                    || shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow all of the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE_ALL);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }

                break;

            case PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {

                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to CAMERA",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermission(CAMERA, PERMISSION_REQUEST_CODE_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }

                break;

            case PERMISSION_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to SD card.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermission(WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_STORAGE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }

                break;

            case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to location data",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermission(ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }

                break;

            default:
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        Values.ePubType = 0;
        Values.sortedAlready = false;
        super.onDestroy();
    }

    public String getAnalyticsFromAssistance(String assistance) {
        return NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOT + Values.tabAssistance + assistance + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM + "";
    }

    public String getAnalyticsFromSettings(String assistance) {
        return NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOT + Values.tabSettings + assistance + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM + "";
    }

    public String getAnalyticsForDelete(String carname, String assistance) {
        return carname + Analytics.DOT + Values.tabSettings + assistance + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM + "";
    }

    public String getAnalyticsForDownloadSection(String assistance) {
        return NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOT + Values.tabSettings + assistance + Analytics.DOT + Analytics.PLATFORM + "";
    }

    public String getAnalyticsForCarSection(String assistance) {
        return NissanApp.getInstance().getCarName(Values.carType) + Analytics.DOT + Values.tabSettings + assistance + Analytics.DOT + NissanApp.getInstance().getLanguageName(new PreferenceUtil(getApplicationContext()).getSelectedLang()) + Analytics.DOT + Analytics.PLATFORM + "";
    }

    //this is for hide soft keyboard when touch outside when editText was focused
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private ArrayList<TabMenu> getDataFromStorage() {

        String key = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(this).getSelectedLang()) + "_" + Values.TAB_MENU_KEY;
        Logger.error("lan Key", "----- " + key);

        Type type = new TypeToken<ArrayList<TabMenu>>() {
        }.getType();
        return new Gson().fromJson(new PreferenceUtil(this).retrieveMultiLangData(key), type);

    }
}
