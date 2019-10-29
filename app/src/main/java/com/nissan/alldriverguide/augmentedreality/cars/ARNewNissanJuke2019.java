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

//                    Auto AC
                    case R.id.btn_auto_ac_left:
                        inflateSecondImage(R.layout.juke_2019_ac_left, "juke_ac_auto_left.png");
                        break;

                    case R.id.btn_auto_ac_middle:
                        inflateSecondImage(R.layout.juke_2019_ac_middle, "juke_ac_auto_middle.png");
                        break;

                    case R.id.btn_auto_ac_right:
                        inflateSecondImage(R.layout.juke_2019_ac_right, "juke_ac_auto_right.png");
                        break;

//                  Manual AC
                    case R.id.btn_manual_ac_left:
                        inflateSecondImage(R.layout.juke_2019_manual_ac_left, "juke_ac_manual_left_01.png");
                        break;

                    case R.id.btn_manual_ac_middle:
                        inflateSecondImage(R.layout.juke_2019_manual_ac_middle, "juke_ac_manual_middle_01.png");
                        break;

                    case R.id.btn_manual_ac_right:
                        inflateSecondImage(R.layout.juke_2019_manual_ac_right, "juke_ac_manual_left_02.png");
                        break;

//                   With Navigation
                    case R.id.btn_radio_navi_left:
                        inflateSecondImage(R.layout.juke_2019_radio_navi_left, "juke_radio_navi_left_01.png");
                        break;

                    case R.id.btn_radio_navi_middle:
                        inflateSecondImage(R.layout.juke_2019_radio_navi_middle, "juke_radio_navi_middle_01.png");
                        break;

                    case R.id.btn_radio_navi_right:
                        inflateSecondImage(R.layout.juke_2019_radio_navi_right, "juke_radio_navi_right_01.png");
                        break;
                    //Without Navigation
                    case R.id.btn_radio_wo_navi_left:
                        inflateSecondImage(R.layout.juke_2019_radio_wo_navi_left, "juke_radio_wo_navi_left.png");
                        break;

                    case R.id.btn_radio_wo_navi_middle:
                        inflateSecondImage(R.layout.juke_2019_radio_wo_navi_middle, "juke_radio_wo_navi_middle.png");
                        break;

                    case R.id.btn_radio_wo_navi_right:
                        inflateSecondImage(R.layout.juke_2019_radio_wo_navi_right, "juke_radio_wo_navi_right.png");
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
                        detectImage(R.layout.juke_2019_sos, "sos.png"); // detection star is so poor to detect image ... improve detection image quality in Vuforia

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

                        detectImage(R.layout.juke_2019_instrument_panel, ""); //no pop image for instrument panel @contact to UI/UX team

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

                    } else if (DataCheck.JukeF16.getInstance().JUKE_COMBIMETER.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_combimeter, "juke_combimeter.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.RADIO_WO_NAVI));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_NAVI_FULL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_navi_full, "juke_radio_navi_full_01.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_navi_left));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_navi_middle));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_navi_right));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_NAVI_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_navi_left, "juke_radio_navi_left_01.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_NAVI_MIDDLEL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_navi_middle, "juke_radio_navi_middle_01.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_NAVI_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_navi_right, "juke_radio_navi_right_01.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.CONNECT));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_WO_NAVI_FULL.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_wo_navi_full, "juke_radio_wo_navi_full.png");

                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_left));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_middle));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_right));

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.STEERING_LEFT));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_WO_NAVI_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_wo_navi_left, "juke_radio_wo_navi_left.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.STEERING_RIGHT));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_WO_NAVI_MIDDLE.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_wo_navi_middle, "juke_radio_wo_navi_middle.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.TRIP_RESET));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_RADIO_WO_NAVI_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_radio_wo_navi_right, "juke_radio_wo_navi_right.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.PARK_ASSIST));

                    } else if (DataCheck.JukeF16.getInstance().PARKING_BRAKE.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_parking_brake, "parking_brake.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.PARKING_BRAKE));

                    } else if (DataCheck.JukeF16.getInstance().PUSH_BUTTON_IGNITION_SWITCH.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_ignition_switch, "start_stop_button.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.INTELLIGENT_PARK_ASSIST));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_STEERING_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_steering_wheel_left, "juke_steering_left.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.COMBINATION_METER));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_STEERING_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_steering_wheel_right, "juke_steering_right.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.COMBINATION_METER));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_STEERING_RIGHT_2.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_steering_wheel_right_2, "juke_steering_right_02.png");

                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Analytics.COMBINATION_METER));

                    } else if (DataCheck.JukeF16.getInstance().JUKE_STEERING_RIGHT_3.contains(userDataToCompare)) {

                        detectImage(R.layout.juke_2019_steering_wheel_right_3, "juke_steering_right_03.png");

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
