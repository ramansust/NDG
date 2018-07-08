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
public class ARLeaf2017 implements GLSurfaceView.Renderer, SampleAppRendererControl, View.OnClickListener {

    private static final String LOGTAG = "ImageTargetRenderer";

    private SampleApplicationSession vuforiaAppSession;
    private ImageTargetActivity mActivity;

    private Renderer mRenderer;
    public static ImageView iv;

    private LayoutInflater inflater;
    private String drawables;

    private SampleAppRenderer mSampleAppRenderer;

    public ARLeaf2017(ImageTargetActivity activity, SampleApplicationSession session) {
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
                    case R.id.btn_ev_navi_left:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_ev_navi_system_left, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_ev_navi_system_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_ev_navi_middle:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_ev_navi_system_middle, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_ev_navi_system_middle.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_ev_navi_right:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_ev_navi_system_right, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_ev_navi_system_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_left:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_radio_w_navi_left, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_navi_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_middle:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_radio_w_navi_middle, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_navi_middle.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_right:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_radio_w_navi_right, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_navi_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_left:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_radio_wo_navi_left, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_radio_wo_navi_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_middle:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_radio_wo_navi_middle, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_radio_wo_navi_middle.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_right:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_radio_wo_navi_right, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_radio_wo_navi_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_left:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_nissan_connect_left, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_nissan_connect_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_middle:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_nissan_connect_middle, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_nissan_connect_middle.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_right:

                        mActivity.layoutCameraView.removeAllViews ();
                        mActivity.inflatedLayout_second = inflater.inflate (R.layout.leaf_2017_nissan_connect_right, null, false);
                        setBackground (mActivity.inflatedLayout_second, drawables + "leaf_2017_nissan_connect_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout_second);
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

                    if (userDataToCompare.equalsIgnoreCase ("power_switch_1")
                            || userDataToCompare.equalsIgnoreCase ("power_switch_2")
                            || userDataToCompare.equalsIgnoreCase ("power_switch_3")
                            || userDataToCompare.equalsIgnoreCase ("power_switch_4")
                            ) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.START_STOP_IGNITION;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_power_switch, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_power_switch.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("auto_ac_full_type_a_1")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_full_type_a_2")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_full_type_a_3")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_left_type_a_1")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_left_type_a_2")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_left_type_a_3")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_left_type_a_4")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_middle_type_a_1")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_middle_type_a_2")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_middle_type_a_3")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_right_type_a_1")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_right_type_a_2")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_right_type_a_3")
                            ) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.AUTO_AC_TYPE_A;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_auto_ac_type_a, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_auto_ac_full_type_a.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("auto_ac_full_type_b_1")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_full_type_b_2")
                            || userDataToCompare.equalsIgnoreCase ("auto_ac_full_type_b_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.AUTO_AC_TYPE_B;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_auto_ac_type_b, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_auto_ac_full_type_b.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("heated_seats_front_1")
                            || userDataToCompare.equalsIgnoreCase ("heated_seats_front_2")
                            || userDataToCompare.equalsIgnoreCase ("heated_seats_front_3")
                            || userDataToCompare.equalsIgnoreCase ("heated_seats_front_4")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.HEATED_FRONT;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_heated_seats_front, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_heated_seats_front.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("heated_seats_rear_1")
                            || userDataToCompare.equalsIgnoreCase ("heated_seats_rear_2")
                            || userDataToCompare.equalsIgnoreCase ("heated_seats_rear_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.HEATED_REAR;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_heated_seats_rear, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_heated_seats_rear.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("multiswitch_1")
                            || userDataToCompare.equalsIgnoreCase ("multiswitch_2")
                            || userDataToCompare.equalsIgnoreCase ("multiswitch_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.SWITCH;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_multiswitch, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_multiswitch.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("navi_unit_full_1")
                            || userDataToCompare.equalsIgnoreCase ("navi_unit_full_2")
                            || userDataToCompare.equalsIgnoreCase ("navi_unit_full_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_w_navi_main, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_navi_full.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_radio_left));
                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_radio_middle));
                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_radio_right));

                    } else if (userDataToCompare.equalsIgnoreCase ("navi_unit_left_1")
                            || userDataToCompare.equalsIgnoreCase ("navi_unit_left_2")
                            || userDataToCompare.equalsIgnoreCase ("navi_unit_left_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_w_navi_left, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_navi_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("navi_unit_right_1")
                            || userDataToCompare.equalsIgnoreCase ("navi_unit_right_2")
                            || userDataToCompare.equalsIgnoreCase ("navi_unit_right_3")) {
                        

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_w_navi_right, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_navi_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("navi_unit_middle_1")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_w_navi_middle, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_navi_middle.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("radio_wo_navi_full_1")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_full_2")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_full_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        // iv.setImageResource(R.drawable.ferrari);
                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_wo_navi_main, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_radio_wo_navi_full.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_radio_wo_navi_left));
                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_radio_wo_navi_middle));
                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_radio_wo_navi_right));

                    } else if (userDataToCompare.equalsIgnoreCase ("radio_wo_navi_left_1")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_left_2")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_left_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_wo_navi_left, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_radio_wo_navi_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("radio_wo_navi_middle_1")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_middle_2")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_middle_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_wo_navi_middle, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_radio_wo_navi_middle.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("radio_wo_navi_right_1")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_right_2")
                            || userDataToCompare.equalsIgnoreCase ("radio_wo_navi_right_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_radio_wo_navi_right, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_radio_wo_navi_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("nissan_connect_full_1")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_full_2")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_full_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        // iv.setImageResource(R.drawable.ferrari);
                        Values.ar_value = Analytics.CONNECT;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_nissan_connect_main, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_nissan_connect_full.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_connect_left));
                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_connect_middle));
                        buttonEventInitial (mActivity.inflatedLayout.findViewById (R.id.btn_connect_right));

                    } else if (userDataToCompare.equalsIgnoreCase ("nissan_connect_left_1")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_left_2")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_left_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.CONNECT;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_nissan_connect_left, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_nissan_connect_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("nissan_connect_middle_1")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_middle_2")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_middle_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.CONNECT;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_nissan_connect_middle, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_nissan_connect_middle.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("nissan_connect_right_1")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_right_2")
                            || userDataToCompare.equalsIgnoreCase ("nissan_connect_right_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.CONNECT;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_nissan_connect_right, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_nissan_connect_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("steering_wheel_left_1")
                            || userDataToCompare.equalsIgnoreCase ("steering_wheel_left_2")
                            || userDataToCompare.equalsIgnoreCase ("steering_wheel_left_3")
                            || userDataToCompare.equalsIgnoreCase ("steering_wheel_left_4")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.STEERING_LEFT;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_steering_wheel_left, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_teering_wheel_left.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("steering_wheel_right_1")
                            || userDataToCompare.equalsIgnoreCase ("steering_wheel_right_2")
                            || userDataToCompare.equalsIgnoreCase ("steering_wheel_right_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.STEERING_RIGHT;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_steering_wheel_right, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_teering_wheel_right.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("trip_reset_1")
                            || userDataToCompare.equalsIgnoreCase ("trip_reset_2")
                            || userDataToCompare.equalsIgnoreCase ("trip_reset_3")
                            || userDataToCompare.equalsIgnoreCase ("trip_reset_4")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.TRIP_RESET;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_tripreset, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_trip_reset.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("park_assist_1")
                            || userDataToCompare.equalsIgnoreCase ("park_assist_2")
                            || userDataToCompare.equalsIgnoreCase ("park_assist_6")
                            || userDataToCompare.equalsIgnoreCase ("park_assist_7")
                            || userDataToCompare.equalsIgnoreCase ("park_assist_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.PARK_ASSIST;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_park_assist, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_park_assist.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("parking_brake_1")
                            || userDataToCompare.equalsIgnoreCase ("parking_brake_2")
                            || userDataToCompare.equalsIgnoreCase ("parking_brake_3")
                            || userDataToCompare.equalsIgnoreCase ("parking_brake_4")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.PARKING_BRAKE;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_parking_brake, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_parking_brake.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("intelligent_park_assist_1")
                            || userDataToCompare.equalsIgnoreCase ("intelligent_park_assist_2")
                            || userDataToCompare.equalsIgnoreCase ("intelligent_park_assist_3")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.INTELLIGENT_PARK_ASSIST;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_intelligent_park_assist, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_intelligent_park_assist.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (userDataToCompare.equalsIgnoreCase ("combimeter_1")
                            || userDataToCompare.equalsIgnoreCase ("combimeter_2")
                            || userDataToCompare.equalsIgnoreCase ("combimeter_3")
                            || userDataToCompare.equalsIgnoreCase ("combimeter_4")) {

                        try {
                            mActivity.isDetected = true;
                            vuforiaAppSession.pauseAR ();
                        } catch (SampleApplicationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }

                        Values.ar_value = Analytics.COMBINATION_METER;
                        mActivity.inflatedLayout = inflater.inflate (
                                R.layout.leaf_2017_combimeter, null, false);
                        setBackground (mActivity.inflatedLayout, drawables + "leaf_2017_combimeter.png");
                        mActivity.layoutCameraView.addView (mActivity.inflatedLayout);
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
