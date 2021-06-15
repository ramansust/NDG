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
import com.nissan.alldriverguide.utils.Logger;
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
                case R.id.btn_ac_manual_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_ac_manual_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_ac_manual_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_manual_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_ac_manual_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_ac_manual_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_manual_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_ac_manual_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_ac_manual_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_wo_navi_full_left:
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
                    break;

                case R.id.btn_radio_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_radio_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_radio_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_radio_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_radio_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_radio_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.xtrail_2017_radio_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_radio_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_auto_full_left:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.xtrail_2017_ac_auto_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_ac_auto_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_auto_full_middle:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.xtrail_2017_ac_auto_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_ac_auto_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                    break;

                case R.id.btn_ac_auto_full_right:
                    mActivity.layoutCameraView.removeAllViews();
                    ImageTargetActivity.inflatedLayout_second = inflater.inflate(
                            R.layout.xtrail_2017_ac_auto_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "xtrail_2020_ac_auto_right.png");
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

                } else if (userDataToCompare.equalsIgnoreCase("trip_reset_1")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_2")
                        || userDataToCompare.equalsIgnoreCase("trip_reset_3")
                ) {

                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.TRIP_RESET;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_tripreset_switch, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_tripreset_switch.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("combimeter_1")
                        || userDataToCompare.equalsIgnoreCase("combimeter_2")
                        || userDataToCompare.equalsIgnoreCase("combimeter_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//done-----------------------------
                    Values.ar_value = Analytics.COMBINATION_METER;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_combimeter_view, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_combimeter_view.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("start_stop_ignition_1")
                        || userDataToCompare.equalsIgnoreCase("start_stop_ignition_2")
                        || userDataToCompare.equalsIgnoreCase("start_stop_ignition_3")
                        || userDataToCompare.equalsIgnoreCase("start_stop_ignition_4")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.START_STOP_IGNITION;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_start_stop_ignition, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_start_stop_ignition.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("switch_1")
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

                    Values.ar_value = Analytics.MIXED_PANEL;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_multi_switch, null, false);
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

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_middle_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_middle_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_middle_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }


                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_manual_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_manual_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_left_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_left_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_left_3")
                        || userDataToCompare.equalsIgnoreCase("ac_man_left_4")
                        || userDataToCompare.equalsIgnoreCase("ac_man_left_5")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_manual_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_manual_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_right_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_right_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_right_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_manual_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_manual_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("ac_man_1")
                        || userDataToCompare.equalsIgnoreCase("ac_man_2")
                        || userDataToCompare.equalsIgnoreCase("ac_man_3")) {
                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.MANUAL_AC;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_ac_manual_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_ac_manual_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_manual_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_manual_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_manual_full_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_03")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_radio_wo_navi_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_wo_navi_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_full_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_left_03")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }


                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_radio_wo_navi_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_wo_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_right_03")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_radio_wo_navi_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_wo_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_01")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_02")
                        || userDataToCompare.equalsIgnoreCase("radio_wo_navi_middle_03")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }


                    Values.ar_value = Analytics.RADIO_WO_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_radio_wo_navi_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_wo_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_full_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_full_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_full_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    //done-----------------------------

                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_full, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_navi_full.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_full_right));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_left_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_left_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_left_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

//done-----------------------------
                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_left, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_navi_left.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_right_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_right_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_right_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//done-----------------------------
                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_right, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_navi_right.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("radio_with_navi_middle_1")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_middle_2")
                        || userDataToCompare.equalsIgnoreCase("radio_with_navi_middle_3")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//done-----------------------------
                    Values.ar_value = Analytics.RADIO_W_NAVI;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_auto_ac_middle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_radio_navi_middle.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("parking_brake_1")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_2")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_3")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_4")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_5")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_6")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_7")
                        || userDataToCompare.equalsIgnoreCase("parking_brake_8")) {

                    Logger.error("Xtrail EUR parking", "------------------");


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_parking_brake, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_parking_brake.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("all_mode_1")
                        || userDataToCompare.equalsIgnoreCase("all_mode_2")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }

                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.xtrail_2017_all_mode, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_all_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("all_mode_1")
                        || userDataToCompare.equalsIgnoreCase("all_mode_2")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//Done---------------------------
                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_hazard_light, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_all_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                }else if (userDataToCompare.equalsIgnoreCase("all_mode_1")
                        || userDataToCompare.equalsIgnoreCase("all_mode_2")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//Done---------------------------
                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_gear_paddle, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_all_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("all_mode_1")
                        || userDataToCompare.equalsIgnoreCase("all_mode_2")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//Done---------------------------
                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_gear_mode, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_all_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                } else if (userDataToCompare.equalsIgnoreCase("all_mode_1")
                        || userDataToCompare.equalsIgnoreCase("all_mode_2")) {


                    try {
                        ImageTargetActivity.isDetected = true;
                        vuforiaAppSession.pauseAR();
                    } catch (SampleApplicationException e) {
                        e.printStackTrace();
                    }
//Done---------------------------
                    Values.ar_value = Analytics.PARKING_BRAKE;
                    ImageTargetActivity.inflatedLayout = inflater.inflate(
                            R.layout.qashqai_2021_driver_position, null, false);
                    setBackground(ImageTargetActivity.inflatedLayout, drawables + "xtrail_2020_all_mode.png");
                    mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout);
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                }else {
                    ImageTargetActivity.isDetected = false;
                }

            });

        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }
}
