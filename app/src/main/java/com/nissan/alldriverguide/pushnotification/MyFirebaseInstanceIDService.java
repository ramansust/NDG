package com.nissan.alldriverguide.pushnotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteAPI;
import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import static android.text.TextUtils.isEmpty;

/**
 * Created by nirob on 11/14/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Logger.error(TAG, "IDSERVICE: Refreshed token:__*****__" + refreshedToken);

        String previousRegisteredId = getPreviousRegisteredIdFromPref();

        if (refreshedToken != null && !isEmpty(refreshedToken)) {

            if (!refreshedToken.equals(previousRegisteredId)) {
                new ApiCall().postDeviceRegistrationForPush(NissanApp.getInstance().getDeviceID(getApplicationContext()), refreshedToken, Values.DEVICE_TYPE, new CompleteAPI() {
                    @Override
                    public void onDownloaded(ResponseInfo responseInfo) {
                        if (Values.SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
                            Logger.error("IDSERVICE: Device registration Successful", "________________________________" + "refresh token");
                            // Saving reg id to shared preferences
                            storeRegIdInPref(refreshedToken);

                            // sending reg id to your server
                            sendRegistrationToServer(refreshedToken);
                            if (!new PreferenceUtil(getApplicationContext()).getPushRegistrationStatus()) {
                                new PreferenceUtil(getApplicationContext()).setPushRegistrationStatus(true);
                            }
                        }
                    }

                    @Override
                    public void onFailed(String failedReason) {
                        Logger.error("IDSERVICE: Device registration failed_refresh_token", "_____________________" + failedReason);
                    }
                });
            }

        }


        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Logger.error(TAG, "IDSERVICE: sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }

    private String getPreviousRegisteredIdFromPref() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        return pref.getString("regId", "");
    }
}

