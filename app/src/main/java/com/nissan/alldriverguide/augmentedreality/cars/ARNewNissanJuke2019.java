/*
 * Copyright (c) 21/10/2019
 * Mostasim Billah
 * MobioApp
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

public class ARNewNissanJuke2019 extends ARCommon {

    public ARNewNissanJuke2019(ImageTargetActivity activity, SampleApplicationSession session) {
        super(activity, session);
    }

    @Override
    public void buttonEventInitial(View img_view) {
        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {


                    case R.id.btn_auto_ac_left:
                        inflateSecondImage(R.layout.juke_2019_ac_left, "juke_ac_auto_left.png");
                        break;

                    case R.id.btn_auto_ac_middle:
                        inflateSecondImage(R.layout.juke_2019_ac_middle, "juke_ac_auto_middle.png");
                        break;

                    case R.id.btn_auto_ac_right:
                        inflateSecondImage(R.layout.juke_2019_ac_right, "juke_ac_auto_right.png");
                        break;


                    case R.id.btn_manual_ac_left:
                        inflateSecondImage(R.layout.juke_2019_manual_ac_left, "juke_ac_manual_left_01.png");
                        break;

                    case R.id.btn_manual_ac_middle:
                        inflateSecondImage(R.layout.juke_2019_manual_ac_middle, "juke_ac_manual_middle_01.png");
                        break;

                    case R.id.btn_manual_ac_right:
                        inflateSecondImage(R.layout.juke_2019_manual_ac_right, "juke_ac_manual_left_02.png");
                        break;


                    default:
                        break;
                }
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
            handler.post(new Runnable() {
                public void run() {

                    if (DataCheck.JukeF16.getInstance().SOS.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_sos, "sos.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.SOS));

                    } else if (DataCheck.JukeF16.getInstance().GEAR_SELECTOR_PADDLES_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_gear_selector_paddles_right, "gear_selector_paddles_full.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.GEAR_SELECTOR));

                    } else if (DataCheck.JukeF16.getInstance().D_MODE_BUTTON.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_d_mode, "d_mode_button.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.AUTO_AC_TYPE_B));

                    } else if (DataCheck.JukeF16.getInstance().GEAR_SELECTOR_PADDLES_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_gear_selector_paddles_right, "gear_selector_paddles_full.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.HEATED_FRONT));

                    } else if (DataCheck.JukeF16.getInstance().INSTRUMENT_PANEL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_instrument_panel, "");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.HEATED_REAR));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_AUTO_FULL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_ac_full, "juke_ac_auto_full.png");

                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_left));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_middle));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_auto_ac_right));

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.SWITCH));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_AUTO_LEFTL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_ac_left, "juke_ac_auto_left.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_AUTO_RIGHTL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_ac_right, "juke_ac_auto_right.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_AUTO_MIDDLEL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_ac_middle, "juke_ac_auto_middle.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_MANUAL_FULL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_ac_manual_full, "juke_ac_manual_full.png");
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_manual_ac_left));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_manual_ac_middle));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_manual_ac_right));

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_W_NAVI));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_MANUAL_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_manual_ac_left, "juke_ac_manual_left_01.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_MANUAL_MIDDLE.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_manual_ac_middle, "juke_ac_manual_middle_01.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_AC_MANUAL_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_manual_ac_right, "juke_ac_manual_left_02.png");

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
                }
            });
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }
}
