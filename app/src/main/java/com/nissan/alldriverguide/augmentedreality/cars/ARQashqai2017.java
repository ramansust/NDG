/*
 * Copyright (c) 17/10/2019
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
import com.nissan.alldriverguide.augmentedreality.data.DataCheck;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.vuforia.SampleApplicationSession;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;

public class ARQashqai2017 extends ARCommon {

    public ARQashqai2017(ImageTargetActivity activity, SampleApplicationSession session) {
        super(activity, session);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void buttonEventInitial(View img_view) {
        img_view.setOnClickListener(v -> {

            switch (v.getId()) {
                case R.id.btn_ac_manual_full_left:
                    inflateSecondImage(R.layout.qashqai_2017_ac_manual_left, "qashqai_2017_ac_manual_left.png");
                    break;

                case R.id.btn_ac_manual_full_middle:
                    inflateSecondImage(R.layout.qashqai_2017_ac_manual_middle, "qashqai_2017_ac_manual_middle.png");
                    break;

                case R.id.btn_ac_manual_full_right:
                    inflateSecondImage(R.layout.qashqai_2017_ac_manual_right, "qashqai_2017_ac_manual_right.png");
                    break;

                case R.id.btn_radio_wo_navi_full_left:
                    inflateSecondImage(R.layout.qashqai_2017_radio_wo_navi_left, "qashqai_2017_radio_wo_navi_left.png");
                    break;

                case R.id.btn_radio_wo_navi_full_middle:
                    inflateSecondImage(R.layout.qashqai_2017_radio_wo_navi_middle, "qashqai_2017_radio_wo_navi_middle.png");
                    break;

                case R.id.btn_radio_wo_navi_full_right:
                    inflateSecondImage(R.layout.qashqai_2017_radio_wo_navi_right, "qashqai_2017_radio_wo_navi_right.png");
                    break;

                case R.id.btn_radio_full_left_top:
                    inflateSecondImage(R.layout.qashqai_2017_radio_navi_left_top, "qashqai_2017_radio_navi_left_top.png");
                    break;

                case R.id.btn_radio_full_middle_top:
                    inflateSecondImage(R.layout.qashqai_2017_radio_navi_middle_top, "qashqai_2017_radio_navi_middle_top.png");
                    break;

                case R.id.btn_radio_full_right_top:
                    inflateSecondImage(R.layout.qashqai_2017_radio_navi_right_top, "qashqai_2017_radio_navi_right_top.png");
                    break;


                case R.id.btn_radio_full_left_bottom:
                    inflateSecondImage(R.layout.qashqai_2017_radio_navi_left_bottom, "qashqai_2017_radio_navi_left_bottom.png");
                    break;

                case R.id.btn_radio_full_middle_bottom:
                    inflateSecondImage(R.layout.qashqai_2017_radio_navi_middle_bottom, "qashqai_2017_radio_navi_middle_bottom.png");
                    break;

                case R.id.btn_radio_full_right_bottom:
                    inflateSecondImage(R.layout.qashqai_2017_radio_navi_right_bottom, "qashqai_2017_radio_navi_right_bottom.png");
                    break;

                case R.id.btn_ac_auto_full_left:
                    inflateSecondImage(R.layout.qashqai_2017_ac_auto_left, "qashqai_2017_ac_auto_left.png");
                    break;

                case R.id.btn_ac_auto_full_middle:
                    inflateSecondImage(R.layout.qashqai_2017_ac_auto_middle, "qashqai_2017_ac_auto_middle.png");
                    break;

                case R.id.btn_ac_auto_full_right:
                    inflateSecondImage(R.layout.qashqai_2017_ac_auto_right, "qashqai_2017_ac_auto_right.png");
                    break;

                case R.id.btn_av_system_full_left:
                    inflateSecondImage(R.layout.qashqai_2017_av_system_left, "qashqai_2017_av_system_left.png");
                    break;

                case R.id.btn_av_system_full_middle:
                    inflateSecondImage(R.layout.qashqai_2017_av_system_middle, "qashqai_2017_av_system_middle.png");
                    break;

                case R.id.btn_av_system_full_right:
                    inflateSecondImage(R.layout.qashqai_2017_av_system_right, "qashqai_2017_av_system_right.png");
                    break;

                default:
                    break;
            }
        });
    }

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

                // compare trackable.getUserData() with vuforia xml image target name
                if (DataCheck.Qashqai2017.STEERING_WHEEL_LEFT.contains(userDataToCompare)
                ) {

                    detectImage(R.layout.qashqai_2017_steering_left, "qashqai_2017_steering_left.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.STEERING_LEFT));

                } else if (DataCheck.Qashqai2017.STEERING_WHEEL_RIGHT_NEW.contains(userDataToCompare)) {

                    detectImage(R.layout.qashqai_2017_steering_wheel_right_new, "qashqai_2017_steering_right_new.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.STEERING_RIGHT));

                } else if (DataCheck.Qashqai2017.STEERING_WHEEL_RIGHT.contains(userDataToCompare)) {

                    detectImage(R.layout.qashqai_2017_steering_right, "qashqai_2017_steering_right.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.STEERING_RIGHT));

                } else if (DataCheck.Qashqai2017.AUTOMATIC_DRIVE_POSITIONER.contains(userDataToCompare)) {

                    detectImage(R.layout.qashqai_2017_automatic_drive_positioner, "qashqai_2017_automatic_drive_positioner.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTOMATIC_DRIVE_POSITION));

                } else if (DataCheck.Qashqai2017.PARKING_BUTTON.contains(userDataToCompare)) {

                    detectImage(R.layout.qashqai_2017_parking_button, "qashqai_2017_parking_button.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTOMATIC_DRIVE_POSITION));

                } else if (DataCheck.Qashqai2017.TRIP_RESET.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_tripreset_switch, "qashqai_2017_tripreset_switch.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.TRIP_RESET));

                } else if (DataCheck.Qashqai2017.COMBIMETER.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_combimeter_view, "qashqai_2017_combimeter_view.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.COMBINATION_METER));

                } else if (DataCheck.Qashqai2017.START_STOP_IGNITION.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_start_stop_ignition, "qashqai_2017_start_stop_ignition.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.START_STOP_IGNITION));

                } else if (DataCheck.Qashqai2017.MULTISWITCH_NEW.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_multiswitch_new, "qashqai_2017_multiswitch_new.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.MIXED_PANEL));

                } /*else if ( "mixed_panel_1",
                    "mixed_panel_2",
                    "mixed_panel_3",
                    "mixed_panel_4",
                    "mixed_panel_5")) {
                        detectImage( R.layout.qashqai_2017_multi_switch,"qashqai_2017_multi_switch.png");
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.MIXED_PANEL));

                }*/ else if (DataCheck.Qashqai2017.AC.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_auto_full, "qashqai_2017_ac_auto_full.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTO_AC));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_auto_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_auto_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_auto_full_right));

                } else if (DataCheck.Qashqai2017.AC_LEFT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_auto_left, "qashqai_2017_ac_auto_left.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTO_AC));

                } else if (DataCheck.Qashqai2017.AC_MIDDLE.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_auto_middle, "qashqai_2017_ac_auto_middle.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTO_AC));

                } else if (DataCheck.Qashqai2017.AC_RIGHT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_auto_right, "qashqai_2017_ac_auto_right.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTO_AC));

                } else if (DataCheck.Qashqai2017.AC_MAN_MIDDLE.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_manual_middle, "qashqai_2017_ac_manual_middle.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.MANUAL_AC));

                } else if (DataCheck.Qashqai2017.AC_MAN_LEFT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_manual_left, "qashqai_2017_ac_manual_left.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.MANUAL_AC));

                } else if (DataCheck.Qashqai2017.AC_MAN_RIGHT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_manual_right, "qashqai_2017_ac_manual_right.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.MANUAL_AC));

                } else if (DataCheck.Qashqai2017.AC_MAN.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_ac_manual_full, "qashqai_2017_ac_manual_full.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.MANUAL_AC));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_manual_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_manual_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_ac_manual_full_right));

                } else if (DataCheck.Qashqai2017.RADIO_WO_NAVI.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_wo_navi_full, "qashqai_2017_radio_wo_navi_full.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_full_right));

                } else if (DataCheck.Qashqai2017.RADIO_WO_NAVI_LEFT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_wo_navi_left, "qashqai_2017_radio_wo_navi_left.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                } else if (DataCheck.Qashqai2017.RADIO_WO_NAVI_RIGHT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_wo_navi_right, "qashqai_2017_radio_wo_navi_right.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                } else if (DataCheck.Qashqai2017.RADIO_WO_NAVI_MIDDLE.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_wo_navi_middle, "qashqai_2017_radio_wo_navi_middle.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                } else if (DataCheck.Qashqai2017.RADIO_NAVI.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_navi_full, "qashqai_2017_radio_navi_full.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_full_left_top));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_full_middle_top));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_full_right_top));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_full_left_bottom));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_full_middle_bottom));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_full_right_bottom));

                } else if (DataCheck.Qashqai2017.RADIO_NAVI_LEFT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_navi_left_top, "qashqai_2017_radio_navi_left_top.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                } else if (DataCheck.Qashqai2017.RADIO_NAVI_RIGHT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_navi_right_top, "qashqai_2017_radio_navi_right_top.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                } else if (DataCheck.Qashqai2017.RADIO_NAVI_MIDDLE.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_radio_navi_middle_top, "qashqai_2017_radio_navi_middle_top.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                } else if (DataCheck.Qashqai2017.PARKING_BRAKE.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_parking_brake, "qashqai_2017_parking_brake.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.PARKING_BRAKE));

                } else if (DataCheck.Qashqai2017.HEATED_FRONT_SEATS.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_heated_seat_switches, "qashqai_2017_heated_seat_switches.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.PARKING_BRAKE));

                } else if (DataCheck.Qashqai2017.AV_SYSTEM_LEFT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_av_system_left, "qashqai_2017_av_system_left.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AV_SYSTEM_LEFT));

                } else if (DataCheck.Qashqai2017.AV_SYSTEM_MIDDLE.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_av_system_middle, "qashqai_2017_av_system_middle.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AV_SYSTEM_MIDDLE));

                } else if (DataCheck.Qashqai2017.AV_SYSTEM_RIGHT.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_av_system_right, "qashqai_2017_av_system_right.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AV_SYSTEM_RIGHT));

                } else if (DataCheck.Qashqai2017.AV_SYSTEM_FULL.contains(userDataToCompare)) {
                    detectImage(R.layout.qashqai_2017_av_system_full, "qashqai_2017_av_system_full.png");
                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AV_SYSTEM));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_av_system_full_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_av_system_full_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_av_system_full_right));

                } else {
                    ImageTargetActivity.isDetected = false;
                }
            });

        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }
}
