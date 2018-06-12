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
import android.util.Log;
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
import com.nissan.alldriverguide.utils.Logger;
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

        if(NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getVideoUrl()!=null){
            Log.e("Video Url",""+NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getVideoUrl());
            Log.e("Video Tag",""+NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getTag());
            Uri video = Uri.parse(NissanApp.getInstance().getExploreVideoList().get(Values.videoIndex).getVideoUrl());
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
