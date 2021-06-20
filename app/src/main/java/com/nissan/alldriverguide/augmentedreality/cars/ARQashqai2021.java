/*
 * Copyright (c) 1/2/2021
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.augmentedreality.cars;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.os.Handler;
import android.view.View;

import com.nissan.alldriverguide.ImageTargetActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.augmentedreality.ARCommon;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.vuforia.SampleApplicationException;
import com.nissan.alldriverguide.vuforia.SampleApplicationSession;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;

public class ARQashqai2021 extends ARCommon {


    public ARQashqai2021(ImageTargetActivity activity, SampleApplicationSession session) {
        super(activity, session);
    }

    @SuppressLint({"NonConstantResourceId", "InflateParams"})
    @Override
    public void buttonEventInitial(View img_view) {

        img_view.setOnClickListener(v -> {

            switch (v.getId()) {

                case R.id.btn_manual_ac_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_2021_manual_ac_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_manual_ac_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_manual_ac_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_2021_manual_ac_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_manual_ac_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_manual_ac_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_2021_manual_ac_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_manual_ac_center.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                /*case R.id.btn_radio_wo_navi_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_radio_wo_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_radio_wo_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_wo_navi_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_radio_wo_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_radio_wo_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_wo_navi_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_radio_wo_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_radio_wo_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;*/

                case R.id.btn_navi_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_2021_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_navi_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_2021_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_navi_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.qashqai_2021_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_auto_ac_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_auto_ac_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_auto_ac_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_auto_ac_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_auto_ac_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "j12qashqai_auto_ac_right.png");
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

        enableRenderer();

        // did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            Trackable trackable = result.getTrackable();

            final String userDataToCompare = (String) trackable.getUserData();

            Handler handler = new Handler(mActivity.getMainLooper());
            handler.post(() -> {

                if (userDataToCompare.equalsIgnoreCase("steering_left_1")
                        || userDataToCompare.equalsIgnoreCase("steering_left_2")
                        || userDataToCompare.equalsIgnoreCase("steering_left_3")
                ) {
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_LEFT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_steering_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_steering_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("steering_right_1")
                        || userDataToCompare.equalsIgnoreCase("steering_right_2")
                        || userDataToCompare.equalsIgnoreCase("steering_right_3")
                ) {
//raman
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.STEERING_RIGHT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_steering_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_steering_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("sos")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_2")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//raman
                    Values.ar_value = Analytics.SOS;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_sos, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_tripreset_switch.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("combi_1")
                        || userDataToCompare.equalsIgnoreCase("combi_2")
                        || userDataToCompare.equalsIgnoreCase("combi_3")
                        || userDataToCompare.equalsIgnoreCase("combi_4")
                        || userDataToCompare.equalsIgnoreCase("combi_5")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.COMBINATION_METER;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_combimeter_view, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_combi.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("start_stop_1")
                        || userDataToCompare.equalsIgnoreCase("start_stop_2")
                        || userDataToCompare.equalsIgnoreCase("start_stop_3")
                        || userDataToCompare.equalsIgnoreCase("start_stop_4")
                        || userDataToCompare.equalsIgnoreCase("start_stop_5")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.START_STOP_IGNITION;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_start_stop_ignition, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_start_stop.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("multiswitch")
                        || userDataToCompare.equalsIgnoreCase("switch_2")
                        || userDataToCompare.equalsIgnoreCase("switch_3")
                        || userDataToCompare.equalsIgnoreCase("switch_4")
                        || userDataToCompare.equalsIgnoreCase("switch_5")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//raman
                    Values.ar_value = Analytics.MIXED_PANEL;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_multi_switch, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_multi_switch.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_full_1")
                        || userDataToCompare.equalsIgnoreCase("ac_full_2")
                        || userDataToCompare.equalsIgnoreCase("ac_full_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_auto_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_auto_full.png");
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
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_auto_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2017_ac_auto_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_middle_1")
                        || userDataToCompare.equalsIgnoreCase("ac_middle_2")
                        || userDataToCompare.equalsIgnoreCase("ac_middle_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_auto_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_auto_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_right_1")
                        || userDataToCompare.equalsIgnoreCase("ac_right_2")
                        || userDataToCompare.equalsIgnoreCase("ac_right_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_auto_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_auto_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("manual_ac_middle_1")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_middle_2")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_middle_3")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_middle_4")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_middle_5")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_manual_ac_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_manual_ac_center.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("manual_ac_left_1")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_left_2")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_left_3")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_left_4")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_left_5")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_left_6")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_manual_ac_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_manual_ac_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("manual_ac_right_1")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_right_2")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_right_3")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_right_4")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_right_5")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_manual_ac_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_manual_ac_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("manual_ac_full_1")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_full_2")
                        || userDataToCompare.equalsIgnoreCase("manual_ac_full_3")) {
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_manual_ac_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_manual_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_manual_ac_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_manual_ac_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_manual_ac_full_right));


                } else if (userDataToCompare.equalsIgnoreCase("navi_full_1")
                        || userDataToCompare.equalsIgnoreCase("navi_full_2")
                        || userDataToCompare.equalsIgnoreCase("navi_full_3")
                        || userDataToCompare.equalsIgnoreCase("navi_full_4")
                        || userDataToCompare.equalsIgnoreCase("navi_full_5")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_navi_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_navi_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_navi_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_navi_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_navi_full_right));

                } else if (userDataToCompare.equalsIgnoreCase("navi_left_2")
                        || userDataToCompare.equalsIgnoreCase("navi_left_3")
                        || userDataToCompare.equalsIgnoreCase("navi_left_4")
                        || userDataToCompare.equalsIgnoreCase("navi_left_5")
                        || userDataToCompare.equalsIgnoreCase("navi_left_6")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }


                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("navi_right_1")
                        || userDataToCompare.equalsIgnoreCase("navi_right_2")
                        || userDataToCompare.equalsIgnoreCase("navi_right_3")
                        || userDataToCompare.equalsIgnoreCase("navi_right_4")
                        || userDataToCompare.equalsIgnoreCase("navi_right_5")
                        || userDataToCompare.equalsIgnoreCase("navi_right_6")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("navi_middle_1")
                        || userDataToCompare.equalsIgnoreCase("navi_middle_2")
                        || userDataToCompare.equalsIgnoreCase("navi_middle_3")
                        || userDataToCompare.equalsIgnoreCase("navi_middle_4")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("auto_ac_full_1")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_auto_ac_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_full_right));

                } else if (userDataToCompare.equalsIgnoreCase("auto_ac_left_1")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_left_2")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_left_3")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_left_4")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_auto_ac_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("auto_ac_right_1")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_right_2")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_right_3")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_right_4")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_auto_ac_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("auto_ac_middle_1")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_middle_2")
                        || userDataToCompare.equalsIgnoreCase("auto_ac_middle_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.AUTO_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_auto_ac_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("parking_brake_1")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_2")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_3")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_4")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_5")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_6")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_parking_brake, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_parking_brake.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("qi_charger_1")
                        || userDataToCompare.equalsIgnoreCase("qi_charger_2")
                        || userDataToCompare.equalsIgnoreCase("qi_charger_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.QI_CHARGER;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_qi_charger, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_qi_charger.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("seat_heating_1")
                        || userDataToCompare.equalsIgnoreCase("seat_heating_2")
                        || userDataToCompare.equalsIgnoreCase("seat_heating_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.SEAT_HEAT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_seat_heat, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_seat_heating.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("trunk_1")
                        || userDataToCompare.equalsIgnoreCase("trunk_2")
                        || userDataToCompare.equalsIgnoreCase("trunk_3")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.TRUNK;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_trunk, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_trunk.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("usb_1")
                        || userDataToCompare.equalsIgnoreCase("usb_2")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.USB;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_usb, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_usb.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("d_mode_1")
                        || userDataToCompare.equalsIgnoreCase("d_mode_2")
                        || userDataToCompare.equalsIgnoreCase("d_mode_3")
                        || userDataToCompare.equalsIgnoreCase("d_mode_4")
                        || userDataToCompare.equalsIgnoreCase("d_mode_5")
                        || userDataToCompare.equalsIgnoreCase("d_mode_6")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.D_MODE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_d_mode, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_d_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("hazard_1")
                        || userDataToCompare.equalsIgnoreCase("hazard_2")
                        || userDataToCompare.equalsIgnoreCase("hazard_3")
                        || userDataToCompare.equalsIgnoreCase("hazard_4")
                        || userDataToCompare.equalsIgnoreCase("hazard_5")
                        || userDataToCompare.equalsIgnoreCase("hazard_6")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.HAZARD_LIGHT;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_hazard_light, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_hazard.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("power_window")
                        || userDataToCompare.equalsIgnoreCase("hazard_2")
                        || userDataToCompare.equalsIgnoreCase("hazard_3")
                        || userDataToCompare.equalsIgnoreCase("hazard_4")
                        || userDataToCompare.equalsIgnoreCase("hazard_5")
                        || userDataToCompare.equalsIgnoreCase("hazard_6")) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//raman
                    Values.ar_value = Analytics.POWER_WINDOW;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_power_window, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_hazard.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("gear_1")
                        || userDataToCompare.equalsIgnoreCase("gear_2")
                        || userDataToCompare.equalsIgnoreCase("gear_3")
                        || userDataToCompare.equalsIgnoreCase("gear_4")
                        || userDataToCompare.equalsIgnoreCase("gear_5")
                        || userDataToCompare.equalsIgnoreCase("gear_6")
                        || userDataToCompare.equalsIgnoreCase("gear_7")
                        || userDataToCompare.equalsIgnoreCase("gear_8")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.GEAR_SELECTOR;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_gear_paddle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_gear.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("gear_mode_1")
                        || userDataToCompare.equalsIgnoreCase("gear_mode_2")
                        || userDataToCompare.equalsIgnoreCase("gear_mode_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.GEAR_MODE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_gear_mode, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_gear_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("adp_1")
                        || userDataToCompare.equalsIgnoreCase("adp_2")
                        || userDataToCompare.equalsIgnoreCase("adp_3")
                        || userDataToCompare.equalsIgnoreCase("adp_4")
                        || userDataToCompare.equalsIgnoreCase("adp_5")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.ADP;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_driver_position, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "j12qashqai_adp.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else {
                    ImageTargetActivity.isDetected = false;
                }

            });

        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }
}
