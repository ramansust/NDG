package com.nissan.alldriverguide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.adapter.TutorialViewPagerAdapter;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.multiLang.model.Tutorial;
import com.nissan.alldriverguide.utils.Values;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Type;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    private Tracker tracker;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private ImageButton btnClose, btnNext;
    private TextView txtNext;
    private int[] imageTutorials = {R.drawable.tutorial_1, R.drawable.tutorial_2, R.drawable.tutorial_3, R.drawable.tutorial_1, R.drawable.tutorial_2, R.drawable.tutorial_3};
    private String[] tutorialTitles = new String[10];
    private String[] tutorialDetails = new String[10];
    private CirclePageIndicator indicator;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        initViews();
        setListener();
        loadData();
    }

    /**
     * Initialized all view
     */
    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.tutorialViewPager);
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        txtNext = (TextView) findViewById(R.id.txtNext);
        indicator = (CirclePageIndicator) findViewById(R.id.viewpager_indicator);
    }

    /**
     * Here set the listener for click item
     */
    private void setListener() {
        btnClose.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        txtNext.setOnClickListener(this);
    }

    /**
     * Here loading data and set adapter
     */
    private void loadData() {

        ArrayList<Tutorial> list = getDataFromStorage();


        for (int i = 0; i < list.size(); i++) {

            tutorialTitles[i] = list.get(i).getTitle();
            tutorialDetails[i] = list.get(i).getDetails();

        }

//        tutorialTitles = getResources().getStringArray(R.array.tutorial_titles);
//        tutorialDetails = getResources().getStringArray(R.array.tutorial_details);
        adapter = new TutorialViewPagerAdapter(TutorialActivity.this, imageTutorials, tutorialTitles, tutorialDetails);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                if (getIntent().getExtras().get("from").equals("activity")) { // if car downloaded is first time
                    startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    finish();
                } else if (getIntent().getExtras().get("from").equals("fragment")) { // if start this activity form settingsFragment
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    finish();
                }
                break;

            case R.id.btnNext:
                try {
                    if (viewPager.getCurrentItem() == 2) {
                        gotoNextActivity();
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.txtNext:
                try {
                    if (viewPager.getCurrentItem() == 2) {
                        gotoNextActivity();
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    /**
     * indicate for the next movement action
     */
    private void gotoNextActivity() {
        if (getIntent().getExtras().get("from").equals("activity")) {
            startActivity(new Intent(TutorialActivity.this, MainActivity.class));
            finish();
        } else if (getIntent().getExtras().get("from").equals("fragment")) {
            finish();
        }
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void sendMsgToGoogleAnalytics(String msgName) {
        ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        tracker = ((MyApplication) getApplication())
                .getTracker(MyApplication.TrackerName.APP_TRACKER);

        tracker.setScreenName(msgName.toLowerCase());
        tracker.enableAdvertisingIdCollection(true);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private ArrayList<Tutorial> getDataFromStorage() {

        Type type = new TypeToken<ArrayList<Tutorial>>() {        }.getType();
        return new Gson().fromJson(new PreferenceUtil(this).retrieveMultiLangData(Values.TUTORIAL), type);

    }
}
