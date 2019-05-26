/*
 * Copyright (c) 14/5/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.augmentedreality;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nissan.alldriverguide.ImageTargetActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.vuforia.LoadingDialogHandler;
import com.nissan.alldriverguide.vuforia.SampleAppRenderer;
import com.nissan.alldriverguide.vuforia.SampleAppRendererControl;
import com.nissan.alldriverguide.vuforia.SampleApplicationException;
import com.nissan.alldriverguide.vuforia.SampleApplicationSession;
import com.nissan.alldriverguide.vuforia.Texture;
import com.vuforia.Device;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Rohan on 5/26/16.
 */
public class ARNavara2019 implements GLSurfaceView.Renderer, SampleAppRendererControl, View.OnClickListener {

    private static final String LOGTAG = "ImageTargetRenderer";

    private SampleApplicationSession vuforiaAppSession;
    private ImageTargetActivity mActivity;

    private Renderer mRenderer;
    public static ImageView iv;

    private LayoutInflater inflater;
    private String drawables;

    private SampleAppRenderer mSampleAppRenderer;

    public ARNavara2019(ImageTargetActivity activity, SampleApplicationSession session) {
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

    @SuppressWarnings("deprecation")
    public void setBackground(View v, String image) {
        try {

            Drawable d = Drawable.createFromPath(image);
            v.setBackgroundDrawable(d);
            System.out.println("POP:::" + image);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void printUserData(Trackable trackable) {
        String userData = (String) trackable.getUserData();
    }

    public void setTextures(Vector<Texture> textures) {
//        mTextures = textures;
    }

    // Function for initializing the renderer.
    private void initRendering() {

        mRenderer = Renderer.getInstance();

        /*GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);*/
        // Hide the Loading Dialog
        mActivity.loadingDialogHandler
                .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

        mActivity.layoutCameraView = (RelativeLayout) mActivity
                .findViewById(R.id.camera_overlay_layout_2);
        mActivity.layoutBackRefreshView = (ViewGroup) mActivity
                .findViewById(R.id.camera_overlay_layout);

        ImageButton ibRefresh = (ImageButton) mActivity.layoutBackRefreshView
                .findViewById(R.id.refresh);

        ImageButton ibBack = (ImageButton) mActivity.layoutBackRefreshView
                .findViewById(R.id.back);

        ImageButton ibInfo = (ImageButton) mActivity.layoutBackRefreshView
                .findViewById(R.id.info);

        ibRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                mActivity.isDetected = false;

                mActivity.layoutCameraView.removeAllViews();
                vuforiaAppSession.onResume();

            }
        });

        ibInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                mActivity.isDetected = true;
                if (!mActivity.isDetected) {
                    try {
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
                }
                mActivity.showInfo();
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (mActivity.inflatedLayout_second != null && mActivity.inflatedLayout_second.isAttachedToWindow()) {

                    mActivity.layoutCameraView.removeView(mActivity.inflatedLayout_second);
                    mActivity.inflatedLayout_second = null;
                    mActivity.layoutCameraView.addView(mActivity.inflatedLayout);

                } else if (mActivity.inflatedLayout != null && mActivity.inflatedLayout.isAttachedToWindow()) {

                    mActivity.layoutCameraView.removeAllViews();
                    mActivity.isDetected = false;
                    vuforiaAppSession.onResume();
                } else {
                    mActivity.backButtonAlert();
                }

            }
        });
        inflater = LayoutInflater.from(mActivity);

    }

    //Method for sub category image click
    public void buttonEventInitial(View img_view) {

        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_radio_wo_navi_middle:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_radio_wo_navi_a_center, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_radio_wo_navi_a_middle.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_left:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_radio_wo_navi_a_left, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_radio_wo_navi_a_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_right:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_radio_wo_navi_a_right, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_radio_wo_navi_a_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_main_left:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_radio_w_navi_left, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_radio_navi_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_main_middle:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_radio_w_navi_middle, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_radio_navi_middle.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_main_right:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_radio_w_navi_right, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_radio_navi_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_ac_left:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_auto_ac_left, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_ac_auto_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_ac_middle:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_auto_ac_center, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_ac_auto_middle.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_ac_right:

                        mActivity.layoutCameraView.removeAllViews();
                        mActivity.inflatedLayout_second = inflater.inflate(R.layout.navara_auto_ac_right, null, false);
                        setBackground(mActivity.inflatedLayout_second, drawables + "navara_ac_auto_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout_second);
                        break;

                    default:
                        break;
                }
            }
        });

    }

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
            printUserData(trackable);

            final String userDataToCompare = (String) trackable.getUserData();

            Handler handler = new Handler(mActivity.getMainLooper());
            handler.post(new Runnable() {
                public void run() {

                    if (userDataToCompare.equalsIgnoreCase("start_stop_ignition_1")
                            || userDataToCompare.equalsIgnoreCase("start_stop_ignition_3")
                            || userDataToCompare.equalsIgnoreCase("start_stop_ignition_4")
                            ) {
                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.START_STOP_IGNITION;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_power_switch, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_start_stop_ignition.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));
                    } else if (userDataToCompare.equalsIgnoreCase("steering_left_1")
                            || userDataToCompare.equalsIgnoreCase("steering_left_2")
                            || userDataToCompare.equalsIgnoreCase("steering_left_3")
                            || userDataToCompare.equalsIgnoreCase("steering_left_4")
                            || userDataToCompare.equalsIgnoreCase("steering_left_5")
                            ) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.STEERING_LEFT;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_st_wheel_left, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_steering_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));
                    } else if (userDataToCompare.equalsIgnoreCase("steering_right_1")
                            || userDataToCompare.equalsIgnoreCase("steering_right_2")
                            || userDataToCompare.equalsIgnoreCase("steering_right_3")
                            || userDataToCompare.equalsIgnoreCase("steering_right_4")
                            || userDataToCompare.equalsIgnoreCase("steering_right_5")
                            || userDataToCompare.equalsIgnoreCase("steering_right_6")
                            ) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.STEERING_RIGHT;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_st_wheel_right, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_steering_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));
                    } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_1")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_2")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_3")
                            ) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_wo_navi_a_main, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_wo_navi_a_main.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_middle));
                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_left));
                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_right));

                    } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_1")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_2")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_wo_navi_a_left, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_wo_navi_a_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_1")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_2")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_3")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_4")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_wo_navi_a_center, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_wo_navi_a_middle.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_1")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_2")
                            || userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_wo_navi_a_right, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_wo_navi_a_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("radio_navi_1")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_2")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_w_navi_main, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_navi_main.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_connect_main_middle));
                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_connect_main_left));
                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_connect_main_right));

                    } else if (userDataToCompare.equalsIgnoreCase("radio_navi_left_1")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_left_2")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_left_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_w_navi_left, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_navi_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("radio_navi_middle_1")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_middle_2")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_middle_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_w_navi_middle, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_navi_middle.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("radio_navi_right_1")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_right_2")
                            || userDataToCompare.equalsIgnoreCase("radio_navi_right_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_radio_w_navi_right, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_radio_navi_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("ac_1")
                            || userDataToCompare.equalsIgnoreCase("ac_2")
                            || userDataToCompare.equalsIgnoreCase("ac_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.AUTO_AC;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_auto_ac_type, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_ac_auto_main.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_ac_middle));
                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_ac_left));
                        buttonEventInitial(mActivity.inflatedLayout.findViewById(R.id.btn_ac_right));

                    } else if (userDataToCompare.equalsIgnoreCase("ac_left_1")
                            || userDataToCompare.equalsIgnoreCase("ac_left_2")
                            || userDataToCompare.equalsIgnoreCase("ac_left_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.AUTO_AC;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_auto_ac_left, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_ac_auto_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("ac_middle_1")
                            || userDataToCompare.equalsIgnoreCase("ac_middle_2")
                            || userDataToCompare.equalsIgnoreCase("ac_middle_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.AUTO_AC;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_auto_ac_center, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_ac_auto_middle.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("ac_right_1")
                            || userDataToCompare.equalsIgnoreCase("ac_right_2")
                            || userDataToCompare.equalsIgnoreCase("ac_right_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.AUTO_AC;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_auto_ac_right, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_ac_auto_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("ac_man_b_1")
                            || userDataToCompare.equalsIgnoreCase("ac_man_b_2")
                            || userDataToCompare.equalsIgnoreCase("ac_man_b_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.MANUAL_AC;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_manual_ac_type_b, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_ac_manual_type_b.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    }
                    //new detection, added by rabiun
                    else if (userDataToCompare.equalsIgnoreCase("ac_man_1")
                            || userDataToCompare.equalsIgnoreCase("ac_man_2")
                            || userDataToCompare.equalsIgnoreCase("ac_man_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.MANUAL_AC;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_manual_ac_type_a, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_ac_manual_type_a.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("combimeter_1")
                            || userDataToCompare.equalsIgnoreCase("combimeter_2")
                            || userDataToCompare.equalsIgnoreCase("combimeter_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.COMBINATION_METER;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_combimeter, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_combimeter.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("switch_1")
                            || userDataToCompare.equalsIgnoreCase("switch_2")
                            || userDataToCompare.equalsIgnoreCase("switch_3")
                            || userDataToCompare.equalsIgnoreCase("switch_4")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.SWITCH;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_multiswitch, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_multi_switch.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("heated_front_seats_1")
                            || userDataToCompare.equalsIgnoreCase("heated_front_seats_2")
                            || userDataToCompare.equalsIgnoreCase("heated_front_seats_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.HEATED_FRONT;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_heated_front_seats_button, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_heated_front.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("console_left_1")
                            || userDataToCompare.equalsIgnoreCase("console_left_2")
                            || userDataToCompare.equalsIgnoreCase("console_left_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.CONSOLE;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_console_left, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_console_left.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("console_1")
                            || userDataToCompare.equalsIgnoreCase("console_2")
                            || userDataToCompare.equalsIgnoreCase("console_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.CONSOLE;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_console_main, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_console.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("console_right_1")
                            || userDataToCompare.equalsIgnoreCase("console_right_2")
                            || userDataToCompare.equalsIgnoreCase("console_right_3")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        Values.ar_value = Analytics.CONSOLE;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_console_right, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_console_right.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase("trip_reset_1")
                            || userDataToCompare.equalsIgnoreCase("trip_reset_2")
                            || userDataToCompare.equalsIgnoreCase("trip_reset_3")
                            || userDataToCompare.equalsIgnoreCase("trip_reset_4")) {


                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Values.ar_value = Analytics.TRIP_RESET;
                        mActivity.inflatedLayout = inflater.inflate(
                                R.layout.navara_trip, null, false);
                        setBackground(mActivity.inflatedLayout, drawables + "navara_tripreset_switch.png");
                        mActivity.layoutCameraView.addView(mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else {
                        mActivity.isDetected = false;
                    }

                }
            });

        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void updateConfiguration() {
        mSampleAppRenderer.onConfigurationChanged(mActivity.mIsActive);
    }
}