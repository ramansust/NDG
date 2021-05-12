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
public class ARMicraNew implements GLSurfaceView.Renderer, SampleAppRendererControl, View.OnClickListener {

    private static final String LOGTAG = "ImageTargetRenderer";

    private final SampleApplicationSession vuforiaAppSession;
    private final ImageTargetActivity mActivity;

    @SuppressLint("StaticFieldLeak")
    public static ImageView iv;

    private LayoutInflater inflater;
    private final String drawables;

    private final SampleAppRenderer mSampleAppRenderer;

    public ARMicraNew(ImageTargetActivity activity, SampleApplicationSession session) {
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
                // auto ac
                case R.id.micra_new_auto_ac_main_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_auto_ac_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_ac_auto_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;


                case R.id.micra_new_auto_ac_main_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_auto_ac_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_ac_auto_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_auto_ac_main_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_auto_ac_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_ac_auto_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;
                //manual ac
                case R.id.micra_new_manual_ac_main_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_manual_ac_type_a_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_ac_manual_type_a_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_manual_ac_main_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_manual_ac_type_a_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_ac_manual_type_a_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_manual_ac_main_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_manual_ac_type_a_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_ac_manual_type_a_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                // radio without navi type A
                case R.id.micra_new_radio_wo_navi_type_a_main_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_wo_navi_type_a_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_wo_navi_type_a_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_wo_navi_type_a_main_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_wo_navi_type_a_middle_1, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_wo_navi_type_a_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_wo_navi_type_a_main_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_wo_navi_type_a_right_1, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_wo_navi_type_a_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                // radio without navi type B
                case R.id.micra_new_radio_wo_navi_type_b_main_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_wo_navi_type_b_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_wo_navi_type_b_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_wo_navi_type_b_main_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_wo_navi_type_b_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_wo_navi_type_b_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_wo_navi_type_b_main_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_wo_navi_type_b_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_wo_navi_type_b_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                // radio with navi type B
                case R.id.micra_new_radio_w_navi_type_b_main_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_w_navi_type_b_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_navi_type_b_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_w_navi_type_b_main_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_w_navi_type_b_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_navi_type_b_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_w_navi_type_b_main_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_new_radio_w_navi_type_b_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_navi_type_b_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                // radio with navi type c
                case R.id.micra_new_radio_with_navi_type_c_main_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micrak14_radio_navi_type_c_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_navi_type_c_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_with_navi_type_c_main_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micrak14_radio_navi_type_c_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_navi_type_c_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.micra_new_radio_with_navi_type_c_main_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micrak14_radio_navi_type_c_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micrak14_radio_navi_type_c_right.png");
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

                if (userDataToCompare.equalsIgnoreCase("start_stop_ignition_1")
                        || userDataToCompare.equalsIgnoreCase("start_stop_ignition_2")
                        || userDataToCompare.equalsIgnoreCase("start_stop_ignition_3")
                ) {
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.START_STOP_IGNITION;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_start_stop_ignition, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_start_stop_ignition.png");

                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_1")
                        || userDataToCompare.equalsIgnoreCase("ac_2")
                        || userDataToCompare.equalsIgnoreCase("ac_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_auto_ac_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_auto_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_auto_ac_main_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_auto_ac_main_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_auto_ac_main_right));

                } else if (userDataToCompare.equalsIgnoreCase("ac_left_1")
                        || userDataToCompare.equalsIgnoreCase("ac_left_2")
                        || userDataToCompare.equalsIgnoreCase("ac_left_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_auto_ac_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_auto_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_middle_1")
                        || userDataToCompare.equalsIgnoreCase("ac_middle_2")
                        || userDataToCompare.equalsIgnoreCase("ac_middle_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_auto_ac_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_auto_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_right_1")
                        || userDataToCompare.equalsIgnoreCase("ac_right_2")
                        || userDataToCompare.equalsIgnoreCase("ac_right_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_auto_ac_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_auto_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("combimeter_1")
                        || userDataToCompare.equalsIgnoreCase("combimeter_2")
                        || userDataToCompare.equalsIgnoreCase("combimeter_3")
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
                            R.layout.micra_new_combimeter_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_combimeter.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_manual_ac_main_type_a, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_manual_main_type_a.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_manual_ac_main_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_manual_ac_main_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_manual_ac_main_right));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_left_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_left_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_left_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_manual_ac_type_a_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_manual_type_a_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_middle_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_middle_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_middle_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_manual_ac_type_a_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_manual_type_a_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_right_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_right_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_right_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_manual_ac_type_a_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_ac_manual_type_a_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                }
                //  start radio panel
                // radio without navi type A
                else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_main_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_main_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_a_main_1, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_a_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_wo_navi_type_a_main_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_wo_navi_type_a_main_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_wo_navi_type_a_main_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_left_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_left_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_left_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_a_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_a_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_middle_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_middle_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_middle_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_a_middle_1, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_a_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_right_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_right_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_a_right_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_a_right_1, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_a_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                }
                // radio without navi type B
                else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_main_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_main_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_main_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_b_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_b_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_wo_navi_type_b_main_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_wo_navi_type_b_main_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_wo_navi_type_b_main_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_left_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_left_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_left_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_b_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_b_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_middle_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_middle_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_middle_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_b_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_b_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_right_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_right_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_type_b_right_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_wo_navi_type_b_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_wo_navi_type_b_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                }
                // radio with navi type B
                else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_main_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_main_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_main_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_w_navi_type_b_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_b_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_w_navi_type_b_main_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_w_navi_type_b_main_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_w_navi_type_b_main_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_left_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_left_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_left_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_w_navi_type_b_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_b_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_middle_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_middle_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_middle_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_w_navi_type_b_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_b_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_right_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_right_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_b_right_03")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_radio_w_navi_type_b_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_b_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                }
                // radio with navi type C
                else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_main_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_main_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_main_03")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_main_04")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micrak14_radio_navi_type_c_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_c_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_with_navi_type_c_main_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_with_navi_type_c_main_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.micra_new_radio_with_navi_type_c_main_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_left_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_left_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_left_03")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_left_04")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_left_05")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micrak14_radio_navi_type_c_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_c_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_03")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_04")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_05")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_06")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_07")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_08")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_middle_09")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micrak14_radio_navi_type_c_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_c_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_right_01")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_right_02")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_right_03")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_right_04")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_type_c_right_05")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micrak14_radio_navi_type_c_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_radio_navi_type_c_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                }
                //end radio panel
                else if (userDataToCompare.equalsIgnoreCase("steering_left_1")
                        || userDataToCompare.equalsIgnoreCase("steering_left_2")
                        || userDataToCompare.equalsIgnoreCase("steering_left_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_LEFT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_steering_view_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_steering_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("steering_right_1")
                        || userDataToCompare.equalsIgnoreCase("steering_right_2")
                        || userDataToCompare.equalsIgnoreCase("steering_right_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_RIGHT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_steering_view_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_steering_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("trip_reset_1")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_2")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_3")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_4")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.TRIP_RESET;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_trip_reset, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_tripreset_switch.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("mixed_panel_1")
                        || userDataToCompare.equalsIgnoreCase("mixed_panel_2")
                        || userDataToCompare.equalsIgnoreCase("mixed_panel_3")
                        || userDataToCompare.equalsIgnoreCase("mixed_panel_4")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // popup image not given thats why layout design is not completed
                    Values.ar_value = Analytics.MIXED_PANEL;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_new_mixed_panel, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micrak14_multi_switch.png");
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
