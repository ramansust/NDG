/*
 * Copyright (c) 16/10/2019
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
import com.nissan.alldriverguide.utils.Values;
import com.nissan.alldriverguide.vuforia.SampleApplicationSession;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;

public class ARLeaf2019Formated extends ARCommon {

    public ARLeaf2019Formated(ImageTargetActivity activity, SampleApplicationSession session) {
        super(activity, session);
    }

    @Override
    public void buttonEventInitial(View img_view) {
        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_ev_navi_left:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_ev_navi_system_left, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_ev_navi_system_left.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_ev_navi_middle:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_ev_navi_system_middle, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_ev_navi_system_middle.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_ev_navi_right:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_ev_navi_system_right, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_ev_navi_system_right.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_left:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2019_radio_w_navi_left, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_navi_left.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_middle:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_radio_w_navi_middle, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_navi_middle.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_right:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_radio_w_navi_right, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_navi_right.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_left:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2019_radio_wo_navi_left, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_radio_wo_navi_left.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_middle:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2019_radio_wo_navi_middle, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_radio_wo_navi_middle.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_radio_wo_navi_right:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2019_radio_wo_navi_right, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_radio_wo_navi_right.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_left:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_nissan_connect_left, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_nissan_connect_left.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_middle:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_nissan_connect_middle, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_nissan_connect_middle.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
                        break;

                    case R.id.btn_connect_right:

                        mActivity.layoutCameraView.removeAllViews();
                        ImageTargetActivity.inflatedLayout_second = inflater.inflate(R.layout.leaf_2017_nissan_connect_right, null, false);
                        setBackground(ImageTargetActivity.inflatedLayout_second, drawables + "leaf2019_nissan_connect_right.png");
                        mActivity.layoutCameraView.addView(ImageTargetActivity.inflatedLayout_second);
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

            Log.e("USER_DATA_MB", userDataToCompare.toString());

            Handler handler = new Handler(mActivity.getMainLooper());
            handler.post(new Runnable() {
                public void run() {

                    if (DataCheck.Leaf2019.POWER_SWITCH.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_power_switch, "leaf2019_power_switch.png");

                        Values.ar_value = Analytics.START_STOP_IGNITION;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.AUTO_AC_FULL_TYPE_A.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_auto_ac_type_a, "leaf2019_auto_ac_full_type_a.png");

                        Values.ar_value = Analytics.AUTO_AC_TYPE_A;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.AUTO_AC_FULL_TYPE_B.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_auto_ac_type_b, "leaf2019_auto_ac_full_type_b.png");

                        Values.ar_value = Analytics.AUTO_AC_TYPE_B;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.HEATED_SEATS_FRONT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_heated_seats_front, "leaf2019_heated_seats_front.png");

                        Values.ar_value = Analytics.HEATED_FRONT;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.HEATED_SEATS_REAR.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_heated_seats_rear, "leaf2019_heated_seats_rear.png");

                        Values.ar_value = Analytics.HEATED_REAR;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.MULTI_SWITCH.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_multiswitch, "leaf2019_multiswitch.png");

                        Values.ar_value = Analytics.SWITCH;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.NAVI_UNIT_FULL.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_radio_w_navi_main, "leaf2019_navi_full.png");

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_left));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_middle));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_right));

                    } else if (DataCheck.Leaf2019.NAVI_UNIT_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2019_radio_w_navi_left, "leaf2019_navi_left.png");

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.NAVI_UNIT_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_radio_w_navi_right, "leaf2019_navi_right.png");

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.NAVI_UNIT_MIDDLE.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_radio_w_navi_middle, "leaf2019_navi_middle.png");

                        Values.ar_value = Analytics.RADIO_W_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_FULL.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2019_radio_wo_navi_full, "leaf2019_radio_wo_navi_full.png");

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_left));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_middle));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_radio_wo_navi_right));

                    } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2019_radio_wo_navi_left, "leaf2019_radio_wo_navi_left.png");

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_MIDDLE.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2019_radio_wo_navi_middle, "leaf2019_radio_wo_navi_middle.png");

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.RADIO_WO_NAVI_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2019_radio_wo_navi_right, "leaf2019_radio_wo_navi_right.png");

                        Values.ar_value = Analytics.RADIO_WO_NAVI;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.NISSAN_CONNECT_FULL.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_nissan_connect_main, "leaf2019_nissan_connect_full.png");

                        Values.ar_value = Analytics.CONNECT;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_connect_left));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_connect_middle));
                        buttonEventInitial(ImageTargetActivity.inflatedLayout.findViewById(R.id.btn_connect_right));

                    } else if (DataCheck.Leaf2019.NISSAN_CONNECT_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_nissan_connect_left, "leaf2019_nissan_connect_left.png");

                        Values.ar_value = Analytics.CONNECT;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.NISSAN_CONNECT_MIDDLE.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_nissan_connect_middle, "leaf2019_nissan_connect_middle.png");

                        Values.ar_value = Analytics.CONNECT;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.NISSAN_CONNECT_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_nissan_connect_right, "leaf2019_nissan_connect_right.png");

                        Values.ar_value = Analytics.CONNECT;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.STEERING_WHEEL_LEFT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_steering_wheel_left, "leaf2019_steering_wheel_left.png");

                        Values.ar_value = Analytics.STEERING_LEFT;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.STEERING_WHEEL_RIGHT.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_steering_wheel_right, "leaf2019_steering_wheel_right.png");

                        Values.ar_value = Analytics.STEERING_RIGHT;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.TRIP_RESET.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_tripreset, "leaf2019_trip_reset.png");

                        Values.ar_value = Analytics.TRIP_RESET;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.PARK_ASSIST.contains(userDataToCompare)) {


                        detectImage(R.layout.leaf_2017_park_assist, "leaf2019_park_assist.png");

                        Values.ar_value = Analytics.PARK_ASSIST;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.PARKING_BRAKE.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_parking_brake, "leaf2019_parking_brake.png");

                        Values.ar_value = Analytics.PARKING_BRAKE;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.INTELLIGENT_PARK_ASSIST.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_intelligent_park_assist, "leaf2019_intelligent_park_assist.png");

                        Values.ar_value = Analytics.INTELLIGENT_PARK_ASSIST;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else if (DataCheck.Leaf2019.COMBIMETER.contains(userDataToCompare)) {

                        detectImage(R.layout.leaf_2017_combimeter, "leaf2019_combimeter.png");

                        Values.ar_value = Analytics.COMBINATION_METER;
                        mActivity.sendMsgToGoogleAnalytics(mActivity.getGoogleAnalyticeName(Values.ar_value));

                    } else {
                        ImageTargetActivity.isDetected = false;
                    }
                }
            });

        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

    }
}
