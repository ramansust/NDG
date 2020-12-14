package com.nissan.alldriverguide;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;


public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "VideoPlayerActivity";
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

    private boolean executeOnResume = true;

    private String from_where = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for full screen video Activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }

        setContentView(R.layout.activity_video_player);

        if (getIntent() != null && getIntent().getExtras() != null) {
            from_where = getIntent().getExtras().getString("from_where");
        }

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

        relativeVideo = findViewById(R.id.relative_video);
        relativePopup = findViewById(R.id.relative_video_popup);

        videoView = findViewById(R.id.video_view);
        btnClose = findViewById(R.id.btn_close);

        progressBar = findViewById(R.id.progress_bar);

        txtViewWatchAgain = findViewById(R.id.btn_watch_again);
        txtViewLearnMore = findViewById(R.id.btn_learn_more);

        txtViewLearnMoreAlways = findViewById(R.id.txt_view_learn_more);

        txtViewTitle = findViewById(R.id.txt_title);

        metrics = new DisplayMetrics();
        VideoPlayerActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
        String learnMoreTitle = NissanApp.getInstance().getAlertMessage(this, new PreferenceUtil(this).getSelectedLang(), Values.LEARN_MORE_TITLE);
        String watchAgainMsg = NissanApp.getInstance().getAlertMessage(this, new PreferenceUtil(this).getSelectedLang(), Values.WATCH_AGAIN_MSG);
        String learnMoreMsg = NissanApp.getInstance().getAlertMessage(this, new PreferenceUtil(this).getSelectedLang(), Values.LEARN_MORE_MSG);
        txtViewLearnMore.setText(learnMoreMsg.isEmpty() ? resources.getString(R.string.video_learn_more) : learnMoreMsg);
        txtViewWatchAgain.setText(watchAgainMsg.isEmpty() ? resources.getString(R.string.video_watch_again) : watchAgainMsg);
        txtViewTitle.setText(learnMoreTitle.isEmpty() ? resources.getString(R.string.video_popup_msg) : learnMoreTitle);
        txtViewLearnMoreAlways.setText(learnMoreMsg.isEmpty() ? resources.getString(R.string.video_learn_more) : learnMoreMsg);

        if (Values.carType == 12 || Values.carType == 13 || Values.carType == 15 || Values.carType == 16) { //for New Nissan QASHQAI and New Nissan X-TRAIL cars
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

        if (from_where.equals("map")) {
            txtViewLearnMore.setVisibility(View.GONE);
            txtViewLearnMoreAlways.setVisibility(View.GONE);
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

        if (from_where.equals("map")) {
            if (NissanApp.getInstance().getMapVideoUrl() != null && !NissanApp.getInstance().getMapVideoUrl().isEmpty()) {
                Uri video = Uri.parse(NissanApp.getInstance().getMapVideoUrl());
                videoView.setVideoURI(video);
            }
        } else {
            if (NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getVideoUrl() != null) {
                Uri video = Uri.parse(NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getVideoUrl());
                videoView.setVideoURI(video);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        relativeVideo.setVisibility(View.GONE);
        txtViewLearnMoreAlways.setVisibility(View.GONE);
        relativePopup.setVisibility(View.VISIBLE);
    }

    private void loadVideoTag() {

        homePageIndex = NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getTag();
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
        if (executeOnResume) {
            progressBar.setVisibility(View.GONE);
            if (videoView != null) {
                videoView.start();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            if (videoView != null) {
                videoView.pause();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (executeOnResume) { // first time true when activity call on create
            if (!videoView.isPlaying() && relativePopup.getVisibility() == View.INVISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
            }
            try {
                if (videoView != null) {
                    videoView.seekTo(position);
                    videoView.start();
                }
            } catch (Exception e) {
            }
        } else { // else working when back from another activity
            if (!videoView.isPlaying() && relativePopup.getVisibility() == View.INVISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
            }
            try {
                if (videoView != null) {
                    videoView.seekTo(position);
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //do not execute onResume code when back form another activity
        executeOnResume = false;
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
        if (videoView != null) {
            position = videoView.getCurrentPosition();
            videoView.pause();
            outState.putInt("position", position);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
    }
}
