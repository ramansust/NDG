/*
 * Copyright (c) 16/10/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.augmentedreality;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.nissan.alldriverguide.ImageTargetActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.vuforia.LoadingDialogHandler;
import com.nissan.alldriverguide.vuforia.SampleAppRenderer;
import com.nissan.alldriverguide.vuforia.SampleAppRendererControl;
import com.nissan.alldriverguide.vuforia.SampleApplicationException;
import com.nissan.alldriverguide.vuforia.SampleApplicationSession;
import com.vuforia.Device;
import com.vuforia.Renderer;
import com.vuforia.Trackable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class ARCommon implements GLSurfaceView.Renderer, SampleAppRendererControl {

    private static final String LOG_TAG = "AR_COMMON";

    protected SampleApplicationSession vuforiaAppSession;
    protected SampleAppRenderer mSampleAppRenderer;
    protected ImageTargetActivity mActivity;
    protected Renderer mRenderer;
    protected LayoutInflater inflater;
    protected String drawables;

    protected ARCommon(ImageTargetActivity activity, SampleApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;
        drawables = Values.car_path + "/" + "drawables" + "/";
        mSampleAppRenderer = new SampleAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);
    }

    public abstract void buttonEventInitial(View img_view);

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        Log.d(LOG_TAG, "GLRenderer.onSurfaceCreated");
        vuforiaAppSession.onSurfaceCreated();

        mSampleAppRenderer.onSurfaceCreated();
        vuforiaAppSession.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOG_TAG, "GLRenderer.onSurfaceChanged");

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

    // Function for initializing the renderer.
    private void initRendering() {

        mRenderer = Renderer.getInstance();
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

        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageTargetActivity.isDetected = false;

                mActivity.layoutCameraView.removeAllViews();
                vuforiaAppSession.onResume();

            }
        });

        ibInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                mActivity.isDetected = true;
                if (!ImageTargetActivity.isDetected) {
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

            }
        });

        inflater = LayoutInflater.from(mActivity);

    }

    protected void enableRenderer() {
        mSampleAppRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
    }

    protected void detectImage(int layout_id, String imageName) {
        try {
            ImageTargetActivity.isDetected = true;
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e) {
            e.printStackTrace();
        }

        ImageTargetActivity.inflatedLayout = inflater.inflate(
                layout_id, null, false);
        setBackground(ImageTargetActivity.inflatedLayout, drawables + imageName);
        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
    }

    protected void inflateSecondImage(int layout, String imageSource) {
        mActivity.layoutCameraView.removeAllViews();
        ImageTargetActivity.inflatedLayout_second = inflater.inflate(layout, null, false);
        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + imageSource);
        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
    }

    @SuppressWarnings("deprecation")
    public void setBackground(View v, String image) {
        try {

            Drawable d = Drawable.createFromPath(image);
            v.setBackgroundDrawable(d);
            System.out.println("PopUpImage::" + image);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateConfiguration() {
        mSampleAppRenderer.onConfigurationChanged(mActivity.mIsActive);
    }

    protected void printUserData(Trackable trackable) {
        String userData = (String) trackable.getUserData();
    }
}
