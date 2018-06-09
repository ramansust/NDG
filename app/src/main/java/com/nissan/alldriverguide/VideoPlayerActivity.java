package com.nissan.alldriverguide;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private VideoView videoView;
    private ImageButton btnClose;
    private ProgressBar progressBar;

    private int homePageIndex = 0;

    private TextView txtViewWatchAgain;
    private TextView txtViewLearnMore;
    private TextView txtViewTitle;

    private TextView txtViewLearnMoreAlways;

    private RelativeLayout relativeVideo;
    private RelativeLayout relativePopup;

    private int position = 0;

    private Resources resources;
    private DisplayMetrics metrics;

    private int indexDouble = 1;

    // here declare the base url for video
    private String qashqaiEURBaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/qashqai/videos/";
    private String jukeBaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/juke/videos/";
    private String xtrailEURBaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/xtrail/videos/";
    private String navaraBaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/navara/videos/";
    private String micrak14BaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/micrak14/videos/";
    private String qashqai2017BaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/qashqai2017/videos/";
    private String xtrail2017BaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/xtrail2017/videos/";
    private String leaf2017BaseURL = "http://213.136.27.240:8002/hasan/m2/nissan/a/eu3v0/leaf2017/videos/";

    // here declare the video header name
    private String[] qashqaiEURVideoNameArray = {"NISSAN_QASHQAI_PARK_ASSIST_", "NISSAN_QASHQAI_SAFETY_SHIELD_"};
    private String[] jukeVideoNameArray = {"NISSAN_JUKE_AVM_", "NISSAN_JUKE_SAFETY_SHIELD_"};
    private String[] xtrailEURVideoNameArray = {"NISSAN_XTRAIL_PARK_ASSIST_", "NISSAN_XTRAIL_SAFETY_SHIELD_"};
    private String[] navaraVideoNameArray = {"NISSAN_NAVARA_AVM_TECHNOLOGIES_", "NISSAN_NAVARA_SAFETY_SHIELD_"};
    private String[] micrak14VideoNameArray = {"NISSAN_MICRA_LANE_INTERVENTION_", "NISSAN_MICRA_EMERGENCY_BRAKING_", "NISSAN_MICRA_AROUND_VIEW_MONITOR_", "NISSAN_MICRA_APPLE_CARPLAY_"};
    private String[] qashqai2017VideoNameArray = {"NEW_QASHQAI_AROUND_VIEW_MONITOR_", "NEW_QASHQAI_CHASSIS_CONTROL_", "NEW_QASHQAI_INTELLIGENT_AUTO_HEADLIGHTS_", "NEW_QASHQAI_INTELLIGENT_EMERGENCY_BRAKING_", "NEW_QASHQAI_INTELLIGENT_PARKING_", "NEW_QASHQAI_INTERIOR_AND_EXTERIOR_DESIGN_"};
    private String[] xtrail2017VideoNameArray = {"NEW_XTRAIL_AROUND_VIEW_MONITOR_", "NEW_XTRAIL_CHASSIS_CONTROL_", "NEW_XTRAIL_INTELLIGENT_AUTO_HEADLIGHTS_", "NEW_XTRAIL_INTELLIGENT_EMERGENCY_BRAKING_", "NEW_XTRAIL_INTELLIGENT_PARKING_", "NEW_XTRAIL_INTERIOR_AND_EXTERIOR_DESIGN_"};
    private String[] leaf2017VideoNameArray = {"NISSAN_LEAF_PROPILOT_PARK_MASTER_", "NISSAN_LEAF_PROPILOT_ASSIST_MASTER_", "NISSAN_LEAF_PROPILOT_E_PEDAL_MASTER_"};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for full screen video Activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if( savedInstanceState != null ) {
            position = savedInstanceState.getInt("position");
        }

        setContentView(R.layout.activity_video_player);

        initViews();
        loadResource();
        setListener();
        loadVideoTag();
        loadData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Here initialized all variable
     */
    private void initViews() {
        new PreferenceUtil(getApplicationContext()).setOpenCountForRateApp();

        relativeVideo = (RelativeLayout) findViewById(R.id.relative_video);
        relativePopup = (RelativeLayout) findViewById(R.id.relative_video_popup);

        videoView = (VideoView) findViewById(R.id.video_view);
        btnClose = (ImageButton) findViewById(R.id.btn_close);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        txtViewWatchAgain = (TextView) findViewById(R.id.btn_watch_again);
        txtViewLearnMore = (TextView) findViewById(R.id.btn_learn_more);
        txtViewTitle = (TextView) findViewById(R.id.txt_title);

        txtViewLearnMoreAlways = (TextView) findViewById(R.id.txt_view_learn_more);

        metrics = new DisplayMetrics();
        VideoPlayerActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        PreferenceUtil preferenceUtil = new PreferenceUtil(getApplicationContext());

        if(preferenceUtil.getSelectedLang().equalsIgnoreCase("pl") || preferenceUtil.getSelectedLang().equalsIgnoreCase("fi") || preferenceUtil.getSelectedLang().equalsIgnoreCase("pt")) {
            indexDouble = 2;
        } else {
            indexDouble = 1;
        }
    }

    /**
     * Here set the click listener
     */
    private void setListener() {
        videoView.setOnCompletionListener(this);
        videoView.setMediaController(new MediaController(this));
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);

        btnClose.setOnClickListener(this);
        txtViewWatchAgain.setOnClickListener(this);
        txtViewLearnMore.setOnClickListener(this);
        txtViewLearnMoreAlways.setOnClickListener(this);
    }

    private void loadData() {
        txtViewLearnMore.setText(resources.getString(R.string.video_learn_more));
        txtViewWatchAgain.setText(resources.getString(R.string.video_watch_again));
        txtViewTitle.setText(resources.getString(R.string.video_popup_msg));
        txtViewLearnMoreAlways.setText(resources.getString(R.string.video_learn_more));

        if (Values.carType == 12 || Values.carType == 13) { //for New Nissan QASHQAI and New Nissan X-TRAIL cars
            if (Values.videoIndex == 5) { // for 6 number video index learn more button will hide
                txtViewLearnMore.setVisibility(View.GONE);
                txtViewLearnMoreAlways.setVisibility(View.GONE);
            } else {
                txtViewLearnMore.setVisibility(View.VISIBLE);
                txtViewLearnMoreAlways.setVisibility(View.VISIBLE);
            }
        } else {
            txtViewLearnMore.setVisibility(View.VISIBLE);
            txtViewLearnMoreAlways.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.VISIBLE);
        startVideo();
    }

    private void loadResource() {
        resources = new Resources(VideoPlayerActivity.this.getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(VideoPlayerActivity.this, new PreferenceUtil(getApplicationContext()).getSelectedLang()));
    }

    /**
     * Here set the video video full online link to video view
     */
    private void startVideo() {
        if (Values.carType == 1) {
            Uri video = Uri.parse(qashqaiEURBaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + qashqaiEURVideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        } else if (Values.carType == 3) {
            Uri video = Uri.parse(jukeBaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + jukeVideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        } else if (Values.carType == 4) {
            Uri video = Uri.parse(xtrailEURBaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + xtrailEURVideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        } else if (Values.carType == 10) {
            Uri video = Uri.parse(navaraBaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + navaraVideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        } else if (Values.carType == 11) {
            Uri video = Uri.parse(micrak14BaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + micrak14VideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        } else if (Values.carType == 12) {
            Uri video = Uri.parse(qashqai2017BaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + qashqai2017VideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        } else if (Values.carType == 13) {
            Uri video = Uri.parse(xtrail2017BaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + xtrail2017VideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        } else if (Values.carType == 14) {
            Uri video = Uri.parse(leaf2017BaseURL + new PreferenceUtil(getApplicationContext()).getSelectedLang() + "/" + leaf2017VideoNameArray[Values.videoIndex] + new PreferenceUtil(getApplicationContext()).getSelectedLang().toString().toUpperCase() + ".mp4");
            videoView.setVideoURI(video);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        relativeVideo.setVisibility(View.GONE);
        txtViewLearnMoreAlways.setVisibility(View.GONE);
        relativePopup.setVisibility(View.VISIBLE);
    }

    private void loadVideoTag() {
        if (Values.carType == 1) {
            switch (Values.videoIndex) {
                case 0:
//                    homePageIndex = 26;
                    homePageIndex = 26 * indexDouble;
                    break;

                case 1:
//                    homePageIndex = 24;
                    homePageIndex = 24 * indexDouble;
                    break;

                default:
                    break;
            }
        } else if (Values.carType == 3) {
            switch (Values.videoIndex) {
                case 0:
//                    homePageIndex = 11;
                    homePageIndex = 11 * indexDouble;
                    break;

                case 1:
//                    homePageIndex = 17;
                    homePageIndex = 17 * indexDouble;
                    break;

                default:
                    break;
            }
        } else if (Values.carType == 4) {
            switch (Values.videoIndex) {
                case 0:
//                    homePageIndex = 29;
                    homePageIndex = 29 * indexDouble;
                    break;

                case 1:
//                    homePageIndex = 27;
                    homePageIndex = 27 * indexDouble;
                    break;

                default:
                    break;
            }
        } else if (Values.carType == 10) {
            switch (Values.videoIndex) {
                case 0:
//                    homePageIndex = 34;
                    homePageIndex = 34 * indexDouble;
                    break;

                case 1:
//                    homePageIndex = 23;
                    homePageIndex = 23 * indexDouble;
                    break;

                default:
                    break;
            }
        } else if (Values.carType == 11) {
            switch (Values.videoIndex) {
                case 0:
                    homePageIndex = 25 * 2;
                    break;

                case 1:
                    homePageIndex = 26 * 2;
                    break;

                case 2:
                    homePageIndex = 15 * 2;
                    break;

                case 3:
                    homePageIndex = 21 * 2;
                    break;

                default:
                    break;
            }
        } else if (Values.carType == 12) {
            switch (Values.videoIndex) {
                case 0:
                    homePageIndex = 26 * 2;
                    break;

                case 1:
                    homePageIndex = 28 * 2;
                    break;

                case 2:
                    homePageIndex = 5 * 2;
                    break;

                case 3:
                    homePageIndex = 34 * 2;
                    break;

                case 4:
                    homePageIndex = 27 * 2;
                    break;

                case 5:
                    homePageIndex = 0;
                    break;

                default:
                    break;
            }
        } else if (Values.carType == 13) {
            switch (Values.videoIndex) {
                case 0:
                    homePageIndex = 26 * 2;
                    break;

                case 1:
                    homePageIndex = 33 * 2;
                    break;

                case 2:
                    homePageIndex = 5 * 2;
                    break;

                case 3:
                    homePageIndex = 34 * 2;
                    break;

                case 4:
                    homePageIndex = 27 * 2;
                    break;

                case 5:
                    homePageIndex = 0;
                    break;

                default:
                    break;
            }
        } else if (Values.carType == 14) {
            switch (Values.videoIndex) {
                case 0:
                    homePageIndex = 70;
                    break;

                case 1:
                    homePageIndex = 68;
                    break;

                case 2:
                    homePageIndex = 44;
                    break;

                default:
                    break;
            }
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                finish();
                break;

            case R.id.btn_watch_again:
                Intent intent = new Intent(VideoPlayerActivity.this, VideoPlayerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_learn_more:
            case R.id.txt_view_learn_more:
                position = videoView.getCurrentPosition();

                Values.ePubType = Values.HOMEPAGE_TYPE;
                // here load the learn more epub for video content
                Intent intentButton = new Intent(VideoPlayerActivity.this, DetailsActivity.class);
                intentButton.putExtra("epub_index", homePageIndex);
                intentButton.putExtra("epub_title", "DETAILS");
                startActivity(intentButton);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        relativePopup.setVisibility(View.INVISIBLE);
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        progressBar.setVisibility(View.GONE);
        if (videoView != null) {
            videoView.start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!videoView.isPlaying() && relativePopup.getVisibility() == View.INVISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }

        try {
            if (videoView != null) {
                videoView.seekTo(position);
                videoView.start();
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (videoView != null) {
                position = videoView.getCurrentPosition();
                videoView.pause();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        videoView.stopPlayback();
        onBackPressed();
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(videoView != null ) {
            position = videoView.getCurrentPosition();
            videoView.pause();
            outState.putInt("position", position);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if( savedInstanceState != null ) {
            position = savedInstanceState.getInt("position");
        }
    }
}
