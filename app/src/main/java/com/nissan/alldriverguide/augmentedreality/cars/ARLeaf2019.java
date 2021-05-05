/*
 * Copyright (c) 2/5/2019
 * Mostasim Billah
 * MobioApp
 * Updated: 16/10/2019
 */

package com.nissan.alldriverguide.augmentedreality.cars;

import android.opengl.GLES20;
import android.os.Handler;
import android.util.Log;
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

public class ARLeaf2019 extends ARCommon {

    public ARLeaf2019(ImageTargetActivity activity, SampleApplicationSession session) {
        super(activity, session);
    }

    @Override
    public void buttonEventInitial(View img_view) {
        img_view.setOnClickListener(v -> {

            switch (v.getId()) {
                case R.id.btn_ev_navi_left:
                    inflateSecondImage(R.layout.leaf_2017_ev_navi_system_left, "leaf2019_ev_navi_system_left.png");
                    break;

                case R.id.btn_ev_navi_middle:
                    inflateSecondImage(R.layout.leaf_2017_ev_navi_system_middle, "leaf2019_ev_navi_system_middle.png");
                    break;

                case R.id.btn_ev_navi_right:
                    inflateSecondImage(R.layout.leaf_2017_ev_navi_system_right, "leaf2019_ev_navi_system_right.png");
                    break;

                case R.id.btn_radio_left:
                    inflateSecondImage(R.layout.leaf_2019_radio_w_navi_left, "leaf2019_navi_left.png");
                    break;

                case R.id.btn_radio_middle:
                    inflateSecondImage(R.layout.leaf_2017_radio_w_navi_middle, "leaf2019_navi_middle.png");
                    break;

                case R.id.btn_radio_right:
                    inflateSecondImage(R.layout.leaf_2017_radio_w_navi_right, "leaf2019_navi_right.png");
                    break;

                case R.id.btn_radio_wo_navi_left:
                    inflateSecondImage(R.layout.leaf_2019_radio_wo_navi_left, "leaf2019_radio_wo_navi_left.png");
                    break;

                case R.id.btn_radio_wo_navi_middle:
                    inflateSecondImage(R.layout.leaf_2019_radio_wo_navi_middle, "leaf2019_radio_wo_navi_middle.png");
                    break;

                case R.id.btn_radio_wo_navi_right:
                    inflateSecondImage(R.layout.leaf_2019_radio_wo_navi_right, "leaf2019_radio_wo_navi_right.png");
                    break;

                case R.id.btn_connect_left:
                    inflateSecondImage(R.layout.leaf_2017_nissan_connect_left, "leaf2019_nissan_connect_left.png");
                    break;

                case R.id.btn_connect_middle:
                    inflateSecondImage(R.layout.leaf_2017_nissan_connect_middle, "leaf2019_nissan_connect_middle.png");
                    break;

                case R.id.btn_connect_right:
                    inflateSecondImage(R.layout.leaf_2017_nissan_connect_right, "leaf2019_nissan_connect_right.png");
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
            printUserData(trackable);

            final String userDataToCompare = (String) trackable.getUserData();

            Log.e("USER_DATA_MB", userDataToCompare);

            Handler handler = new Handler(mActivity.getMainLooper());
            handler.post(() -> {

                if (DataCheck.Leaf2019.POWER_SWITCH.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_power_switch, "leaf2019_power_switch.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.START_STOP_IGNITION));

                } else if (DataCheck.Leaf2019.AUTO_AC_FULL_TYPE_A.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_auto_ac_type_a, "leaf2019_auto_ac_full_type_a.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTO_AC_TYPE_A));

                } else if (DataCheck.Leaf2019.AUTO_AC_FULL_TYPE_B.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_auto_ac_type_b, "leaf2019_auto_ac_full_type_b.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTO_AC_TYPE_B));

                } else if (DataCheck.Leaf2019.HEATED_SEATS_FRONT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_heated_seats_front, "leaf2019_heated_seats_front.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.HEATED_FRONT));

                } else if (DataCheck.Leaf2019.HEATED_SEATS_REAR.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_heated_seats_rear, "leaf2019_heated_seats_rear.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.HEATED_REAR));

                } else if (DataCheck.Leaf2019.MULTI_SWITCH.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_multiswitch, "leaf2019_multiswitch.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.SWITCH));

                } else if (DataCheck.Leaf2019.NAVI_UNIT_FULL.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_radio_w_navi_main, "leaf2019_navi_full.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_right));

                } else if (DataCheck.Leaf2019.NAVI_UNIT_LEFT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2019_radio_w_navi_left, "leaf2019_navi_left.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                } else if (DataCheck.Leaf2019.NAVI_UNIT_RIGHT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_radio_w_navi_right, "leaf2019_navi_right.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                } else if (DataCheck.Leaf2019.NAVI_UNIT_MIDDLE.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_radio_w_navi_middle, "leaf2019_navi_middle.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_FULL.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2019_radio_wo_navi_full, "leaf2019_radio_wo_navi_full.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_right));

                } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_LEFT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2019_radio_wo_navi_left, "leaf2019_radio_wo_navi_left.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_MIDDLE.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2019_radio_wo_navi_middle, "leaf2019_radio_wo_navi_middle.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_RIGHT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2019_radio_wo_navi_right, "leaf2019_radio_wo_navi_right.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                } else if (DataCheck.Leaf2019.NISSAN_CONNECT_FULL.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_nissan_connect_main, "leaf2019_nissan_connect_full.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_connect_left));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_connect_middle));
                    buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_connect_right));

                } else if (DataCheck.Leaf2019.NISSAN_CONNECT_LEFT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_nissan_connect_left, "leaf2019_nissan_connect_left.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                } else if (DataCheck.Leaf2019.NISSAN_CONNECT_MIDDLE.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_nissan_connect_middle, "leaf2019_nissan_connect_middle.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                } else if (DataCheck.Leaf2019.NISSAN_CONNECT_RIGHT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_nissan_connect_right, "leaf2019_nissan_connect_right.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                } else if (DataCheck.Leaf2019.STEERING_WHEEL_LEFT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_steering_wheel_left, "leaf2019_steering_wheel_left.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.STEERING_LEFT));

                } else if (DataCheck.Leaf2019.STEERING_WHEEL_RIGHT.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_steering_wheel_right, "leaf2019_steering_wheel_right.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.STEERING_RIGHT));

                } else if (DataCheck.Leaf2019.TRIP_RESET.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_tripreset, "leaf2019_trip_reset.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.TRIP_RESET));

                } else if (DataCheck.Leaf2019.PARK_ASSIST.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_park_assist, "leaf2019_park_assist.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.PARK_ASSIST));

                } else if (DataCheck.Leaf2019.PARKING_BRAKE.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_parking_brake, "leaf2019_parking_brake.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.PARKING_BRAKE));

                } else if (DataCheck.Leaf2019.INTELLIGENT_PARK_ASSIST.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_intelligent_park_assist, "leaf2019_intelligent_park_assist.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.INTELLIGENT_PARK_ASSIST));

                } else if (DataCheck.Leaf2019.COMBIMETER.contains(userDataToCompare)) {

                    detectImage(R.layout.leaf_2017_combimeter, "leaf2019_combimeter.png");

                    mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.COMBINATION_METER));

                } else {
                    ImageTargetActivity.isDetected = false;
                }
            });
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }
}
