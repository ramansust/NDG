package com.nissan.alldriverguide.augmentedreality;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nissan.alldriverguide.ImageTargetActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.vuforia.LoadingDialogHandler;
import com.nissan.alldriverguide.vuforia.SampleAppRenderer;
import com.nissan.alldriverguide.vuforia.SampleAppRendererControl;
import com.nissan.alldriverguide.vuforia.SampleApplicationException;
import com.nissan.alldriverguide.vuforia.SampleApplicationSession;
import com.vuforia.Device;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Rohan on 5/26/16.
 */
public class ARXtrail2017Rus implements GLSurfaceView.Renderer, SampleAppRendererControl, View.OnClickListener {

    private static final String LOGTAG = "ImageTargetRenderer";

    private final SampleApplicationSession vuforiaAppSession;
    private final ImageTargetActivity mActivity;

    @SuppressLint("StaticFieldLeak")
    public static ImageView iv;

    private LayoutInflater inflater;
    private final String drawables;

    private final SampleAppRenderer mSampleAppRenderer;

    public ARXtrail2017Rus(ImageTargetActivity activity, SampleApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;
        drawables = Values.car_path + "/" + "drawables" + "/";
        mSampleAppRenderer = new SampleAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();

        mSampleAppRenderer.onSurfaceCreated();
        vuforiaAppSession.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mSampleAppRenderer.onConfigurationChanged(mActivity.mIsActive);

        initRendering();
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        if (!mActivity.mIsActive)
            return;

        // Call our function to render content
        mSampleAppRenderer.render();
    }

    public void setActive(boolean active) {
        mActivity.mIsActive = active;

        if (mActivity.mIsActive)
            mSampleAppRenderer.configureVideoBackground();
    }

    @Override
    public void onClick(View v) {

    }

    public void setBackground(View v, String image) {
        try {
            Drawable d = Drawable.createFromPath(image);
            v.setBackground(d);
            System.out.println("POP:::" + image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function for initializing the renderer.
    private void initRendering() {

        /*GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);*/
        // Hide the Loading Dialog
        mActivity.loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

        mActivity.layoutCameraView = mActivity
                .findViewById(R.id.camera_overlay_layout_2);
        mActivity.layoutBackRefreshView = mActivity
                .findViewById(R.id.camera_overlay_layout);

        ImageButton ibRefresh = mActivity.layoutBackRefreshView
                .findViewById(R.id.refresh);

        ImageButton ibBack = mActivity.layoutBackRefreshView
                .findViewById(R.id.back);

        ImageButton ibInfo = mActivity.layoutBackRefreshView
                .findViewById(R.id.info);

        ibRefresh.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            ImageTargetActivity.isDetected = false;

            mActivity.layoutCameraView.removeAllViews();
            vuforiaAppSession.onResume();
        });

        ibInfo.setOnClickListener(v -> {
            // TODO Auto-generated method stub
//                mActivity.isDetected = true;
            if (!ImageTargetActivity.isDetected) {
                try {
                    vuforiaAppSession.pauseAR();
                } catch (SampleApplicationException e) {
                    e.printStackTrace();
                }
            }
            mActivity.showInfo();
        });
        ibBack.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            if (ImageTargetActivity.inflatedLayout_second != null && ImageTargetActivity.inflatedLayout_second.isAttachedToWindow()) {

                mActivity.layoutCameraView.removeView(ImageTargetActivity.inflatedLayout_second);
                ImageTargetActivity.inflatedLayout_second = null;
                mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);

            } else if (ImageTargetActivity.inflatedLayout != null && ImageTargetActivity.inflatedLayout.isAttachedToWindow()) {

                mActivity.layoutCameraView.removeAllViews();
                ImageTargetActivity.isDetected = false;
                vuforiaAppSession.onResume();
            } else {
                mActivity.backButtonAlert();
            }

        });
        inflater = LayoutInflater.from(mActivity);
    }

    //Method for sub category image click
    @SuppressLint({"NonConstantResourceId", "InflateParams"})
    public void buttonEventInitial(View img_view) {

        img_view.setOnClickListener(v -> {

            switch (v.getId()) {

                case R.id.btn_radio_with_navi_left_a:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail2017rus_radio_w_navi_left_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2017_rus_audio_with_navi_left_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_with_navi_right_a:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail2017rus_radio_w_navi_right_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2017_rus_audio_with_navi_right_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_with_navi_middle_a:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail2017rus_radio_w_navi_middle_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2017_rus_audio_with_navi_middle_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_with_navi_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail2017rus_radio_w_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_radio_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_with_navi_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail2017rus_radio_w_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_radio_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_with_navi_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail2017rus_radio_w_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_radio_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_without_navi_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_radio_wo_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_radio_wo_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_without_navi_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_radio_wo_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_radio_wo_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_without_navi_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_rus_radio_wo_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_radio_wo_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_auto_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.xtrailrus_2017_ac_auto_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_ac_auto_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_auto_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.xtrailrus_2017_ac_auto_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_ac_auto_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_auto_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.xtrailrus_2017_ac_auto_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail2017rus_ac_auto_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                default:
                    break;
            }
        });

    }

    @SuppressLint("InflateParams")
    @Override
    public void renderFrame(State state, float[] projectionMatrix) {
        // Renders video background replacing Renderer.DrawVideoBackground()
        mSampleAppRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        // did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            Trackable trackable = result.getTrackable();

            final String userDataToCompare = (String) trackable.getUserData();

            Handler handler = new Handler(mActivity.getMainLooper());
            handler.post(() -> {

                if (userDataToCompare.equalsIgnoreCase("steering_wheel_left_3")
                        || userDataToCompare.equalsIgnoreCase("steering_wheel_left_2")
                        || userDataToCompare.equalsIgnoreCase("steering_wheel_left_1")
                ) {
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_LEFT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_steering_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_steering_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("steering_wheel_right_1")
                        || userDataToCompare.equalsIgnoreCase("steering_wheel_right_2")
                        || userDataToCompare.equalsIgnoreCase("steering_wheel_right_3")
                        || userDataToCompare.equalsIgnoreCase("v2_steering_wheel_right_1")
                        || userDataToCompare.equalsIgnoreCase("v2_steering_wheel_right_2")
                        || userDataToCompare.equalsIgnoreCase("v2_steering_wheel_right_3")
                        || userDataToCompare.equalsIgnoreCase("v2_steering_wheel_right_4")
                        || userDataToCompare.equalsIgnoreCase("v2_steering_wheel_right_5")
                ) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_RIGHT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_steering_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_steering_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("sos_1")
                        || userDataToCompare.equalsIgnoreCase("sos_2")
                        || userDataToCompare.equalsIgnoreCase("sos_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.SUNROOF;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_sos, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_sos.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("trip_reset_1")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_2")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.TRIP_RESET;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_tripreset_switch, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_tripreset_switch.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("start_stop_2")
                        || userDataToCompare.equalsIgnoreCase("start_stop_3")
                        || userDataToCompare.equalsIgnoreCase("start_stop_1")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.START_STOP_IGNITION;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_start_stop_ignition, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_start_stop_ignition.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("multiswitch_1")
                        || userDataToCompare.equalsIgnoreCase("multiswitch_2")
                        || userDataToCompare.equalsIgnoreCase("multiswitch_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MIXED_PANEL;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_mixed_panel, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_multi_switch.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_auto_full_1")
                        || userDataToCompare.equalsIgnoreCase("ac_auto_full_2")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_ac_auto_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_ac_auto_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_auto_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_auto_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_auto_full_right));

                } else if (userDataToCompare.equalsIgnoreCase("ac_left_1")
                        || userDataToCompare.equalsIgnoreCase("ac_left_2")
                        || userDataToCompare.equalsIgnoreCase("ac_left_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_ac_auto_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_ac_auto_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_middle_1")
                        || userDataToCompare.equalsIgnoreCase("ac_middle_2")
                        || userDataToCompare.equalsIgnoreCase("ac_middle_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_ac_auto_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_ac_auto_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("combimeter_3")
                        || userDataToCompare.equalsIgnoreCase("combimeter_2")
                        || userDataToCompare.equalsIgnoreCase("combimeter_1")
                        || userDataToCompare.equalsIgnoreCase("combimeter_4")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.COMBINATION_METER;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_combimeter_view, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_combimeter.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_right_1")
                        || userDataToCompare.equalsIgnoreCase("ac_right_2")
                        || userDataToCompare.equalsIgnoreCase("ac_right_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_ac_auto_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_ac_auto_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_full_2")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_full_1")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_rus_radio_without_navi_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_radio_wo_navi_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_without_navi_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_without_navi_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_without_navi_full_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_left_4")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_left_5")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_left_6")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_rus_radio_wo_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_radio_wo_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_right_1")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_right_2")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_right_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_rus_radio_wo_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_radio_wo_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_middle_3")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_middle_2")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_middle_1")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_rus_radio_wo_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_radio_wo_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_full_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_full_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_full_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_rus_radio_with_navi, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_radio_navi_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_with_navi_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_with_navi_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_with_navi_right));

                } else if (userDataToCompare.equalsIgnoreCase("audio_with_navi_a_full_1")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_full_2")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_full_3")) {

                    Log.e("Xtrail radio navi full", "----------------------");
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail2017rus_radio_with_navi_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2017_rus_audio_with_navi_full_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_with_navi_left_a));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_with_navi_middle_a));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_with_navi_right_a));

                } else if (userDataToCompare.equalsIgnoreCase("audio_with_navi_a_middle_1")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_middle_2")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_middle_3")) {

                    Log.e("Xtrail radio navi midle", "----------------------");
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail2017rus_radio_w_navi_middle_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2017_rus_audio_with_navi_middle_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("audio_with_navi_a_left_1")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_left_2")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_left_3")) {

                    Log.e("Xtrail radio navi left", "----------------------");
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail2017rus_radio_w_navi_left_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2017_rus_audio_with_navi_left_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("audio_with_navi_a_right_3")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_right_2")
                        || userDataToCompare.equalsIgnoreCase("audio_with_navi_a_right_1")) {

                    Log.e("Xtrail radio navi right", "----------------------");
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail2017rus_radio_w_navi_right_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2017_rus_audio_with_navi_right_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_left_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_left_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_left_3")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_left_4")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail2017rus_radio_with_navi_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2017_rus_audio_with_navi_full_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_right_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_right_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_right_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail2017rus_radio_w_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_radio_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_middle_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_middle_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_middle_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail2017rus_radio_w_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_radio_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("parking_brake_1")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_2")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_parking_brake, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_parking_brake.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("all_mode_1")
                        || userDataToCompare.equalsIgnoreCase("all_mode_2")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.ALL_MODE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_all_mode, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_all_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("heated_seat_front_1")
                        || userDataToCompare.equalsIgnoreCase("heated_seat_front_2")
                        || userDataToCompare.equalsIgnoreCase("heated_seat_front_3")
                        || userDataToCompare.equalsIgnoreCase("heated_seat_front_4")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.HEATED_FRONT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_heated_seats_front, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_heated_seats_front.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("heated_seat_rear_1")
                        || userDataToCompare.equalsIgnoreCase("heated_seat_rear_2")
                        || userDataToCompare.equalsIgnoreCase("heated_seat_rear_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.HEATED_REAR;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrailrus_2017_heated_seats_rear, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail2017rus_heated_seats_rear.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else {
                    ImageTargetActivity.isDetected = false;
                }

            });

        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void updateConfiguration() {
        mSampleAppRenderer.onConfigurationChanged(mActivity.mIsActive);
    }
}
