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
public class ARMicra implements GLSurfaceView.Renderer, SampleAppRendererControl, View.OnClickListener {

    private static final String LOGTAG = "ImageTargetRenderer";

    private final SampleApplicationSession vuforiaAppSession;
    private final ImageTargetActivity mActivity;

    public static ImageView iv;

    private LayoutInflater inflater;
    private final String drawables;

    private final SampleAppRenderer mSampleAppRenderer;

    public ARMicra(ImageTargetActivity activity, SampleApplicationSession session) {
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

        Renderer mRenderer = Renderer.getInstance();

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
    public void buttonEventInitial(View img_view) {

        img_view.setOnClickListener(v -> {

            switch (v.getId()) {
                case R.id.btn_radio_wo_navi_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_radio_wo_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micra_radio_wo_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_wo_navi_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_radio_wo_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micra_radio_wo_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_wo_navi_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_radio_wo_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micra_radio_wo_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_navi_left:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_radio_with_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micra_radio_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_navi_middle:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_radio_with_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micra_radio_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_navi_right:

                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.micra_radio_with_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "micra_radio_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                default:
                    break;
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
            handler.post(() -> {

                if (userDataToCompare.equalsIgnoreCase("start_stop_1")
                        || userDataToCompare.equalsIgnoreCase("start_stop_2")
                        || userDataToCompare.equalsIgnoreCase("start_stop_3")
                ) {
                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.START_STOP_IGNITION;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_start_stop_ignition, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_start_stop_ignition.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_1")
                        || userDataToCompare.equalsIgnoreCase("ac_2")
                        || userDataToCompare.equalsIgnoreCase("ac_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_auto_ac_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_ac_auto_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("combimeter_1")
                        || userDataToCompare.equalsIgnoreCase("combimeter_2")
                        || userDataToCompare.equalsIgnoreCase("combimeter_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.COMBINATION_METER;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_combimeter_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_combimeter.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_manual_ac_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_ac_manual_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_1")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_2")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_wo_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_wo_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_1")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_2")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_wo_navi_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_wo_navi_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_1")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_2")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_wo_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_wo_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_1")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_2")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_wo_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_wo_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("steering_left_1")
                        || userDataToCompare.equalsIgnoreCase("steering_left_2")
                        || userDataToCompare.equalsIgnoreCase("steering_left_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_LEFT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_steering_view_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_steering_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("steering_right_1")
                        || userDataToCompare.equalsIgnoreCase("steering_right_2")
                        || userDataToCompare.equalsIgnoreCase("steering_right_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_RIGHT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_steering_view_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_steering_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("switch_1")
                        || userDataToCompare.equalsIgnoreCase("switch_2")
                        || userDataToCompare.equalsIgnoreCase("switch_3")
                        || userDataToCompare.equalsIgnoreCase("switch_4")
                        || userDataToCompare.equalsIgnoreCase("switch_5")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.SWITCH;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_switch_panel_main, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_multi_switch.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_navi_left_1")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_left_2")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_left_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_with_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_navi_right_1")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_right_2")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_right_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_with_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_navi_1")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_2")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_with_navi, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_navi_main.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_navi_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_navi_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_navi_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_navi_middle_1")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_middle_2")
                        || userDataToCompare.equalsIgnoreCase("radio_navi_middle_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
//                            vuforiaAppSession.pauseAR();
                        mActivity.onPause();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.micra_radio_with_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "micra_radio_navi_middle.png");
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
