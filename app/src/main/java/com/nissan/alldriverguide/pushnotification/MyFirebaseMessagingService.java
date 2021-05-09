package com.nissan.alldriverguide.pushnotification;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nissan.alldriverguide.SplashScreenActivity;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.utils.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.text.TextUtils.isEmpty;

/**
 * Created by nirob on 11/14/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private CommonDao commonDao;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Logger.error(TAG, "IDSERVICE: Refreshed token:__**msg***__" + remoteMessage.getData());

        commonDao = CommonDao.getInstance();

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.error("DATA 1 Body", "________" + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage);
        }

        // Check if message contains a data payload.
        remoteMessage.getData();
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject object = new JSONObject(remoteMessage.getData());
                Logger.error("Push Json ", "________" + remoteMessage.getData());
                String carId = object.getString("car_id");
                String languageId = object.getString("language_id");
                String ePub_id = object.getString("epub_id");
                JSONArray array = new JSONArray(ePub_id);


                if (!isEmpty(carId)) {
                    if (commonDao.checkIfCarInstalledBeforePush(getBaseContext(), Integer.parseInt(carId)))
                        return;
                }

//                if (!isEmpty(carId) && !isEmpty(languageId) && !isEmpty(ePub_id)) {
//                    commonDao.makeAllPushEntryStatusChange(getBaseContext(), Integer.parseInt(carId), Integer.parseInt(languageId), Integer.parseInt(ePub_id));
//                }

//                if("11".equalsIgnoreCase(carId) || "12".equalsIgnoreCase(carId) || "13".equalsIgnoreCase(carId) || "14".equalsIgnoreCase(carId)) {
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        String ePubId = array.get(i).toString();
                        if (!isEmpty(carId) && !isEmpty(languageId) && !isEmpty(ePubId)) {
                            try {
                                commonDao.makeAllPushEntryStatusChange(getBaseContext(), Integer.parseInt(carId), Integer.parseInt(languageId), Integer.parseInt(ePubId));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            commonDao.insertNotificationData(getApplicationContext(), carId, languageId, ePubId);
                        } else {
                            Logger.error("Empty ID found", "_____________");
                        }
                    }
                }
                if (!isEmpty(object.getString("msg"))) {
                    handleDataMessage(object.getString("msg"));
                }
//                }

            } catch (Exception e) {
                Logger.error(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        if (intent == null)
            return;

        commonDao = CommonDao.getInstance();


        // Check if message contains a data payload.
        if (intent.getExtras() != null) {
            try {
                Bundle extras = intent.getExtras();
                String carId = extras.getString("car_id");
                String languageId = extras.getString("language_id");
                String ePub_id = extras.getString("epub_id");

                Logger.error("Car_id : " + carId + " Languag_id : " + languageId, "ePub_id : " + ePub_id);

                JSONArray array = new JSONArray(ePub_id);


                if (!isEmpty(carId)) {
                    if (commonDao.checkIfCarInstalledBeforePush(getBaseContext(), Integer.parseInt(carId)))
                        return;
                }

//                if (!isEmpty(carId) && !isEmpty(languageId) && !isEmpty(ePub_id)) {
//                    commonDao.makeAllPushEntryStatusChange(getBaseContext(), Integer.parseInt(carId), Integer.parseInt(languageId), Integer.parseInt(ePub_id));
//                }

//                if("11".equalsIgnoreCase(carId) || "12".equalsIgnoreCase(carId) || "13".equalsIgnoreCase(carId) || "14".equalsIgnoreCase(carId)) {
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        String ePubId = array.get(i).toString();
                        if (!isEmpty(carId) && !isEmpty(languageId) && !isEmpty(ePubId)) {
                            try {
                                commonDao.makeAllPushEntryStatusChange(getBaseContext(), Integer.parseInt(carId), Integer.parseInt(languageId), Integer.parseInt(ePubId));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            commonDao.insertNotificationData(getApplicationContext(), carId, languageId, ePubId);
                        } else {
                            Logger.error("Empty ID found", "_____________");
                        }
                    }
                }
                if (!isEmpty(extras.getString("msg"))) {
                    handleDataMessage(extras.getString("msg"));
                }
//                }

                Logger.error("CarFFFFFFFFFFFFFFFFFFFFFFF_id : " + carId + " Languag_id : " + languageId, "ePub_id : " + ePub_id);

            } catch (Exception e) {
                Logger.error(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            // app is in background, show the notification in notification tray
            Intent resultIntent = new Intent(Config.PUSH_NOTIFICATION);
            resultIntent.putExtra("message", Objects.requireNonNull(remoteMessage.getNotification()).getBody());

            showNotificationMessage(getApplicationContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), "timestamp", resultIntent);

        }  // If the app is in background, firebase itself handles the notification

    }

    private void handleDataMessage(String message) {
        try {
            if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                Logger.error(TAG, "handleDataMessage: ");
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                resultIntent.putExtra("message", message);

                showNotificationMessage(getApplicationContext(), "NDG Push Notification", message, "", resultIntent);

                // check for image attachment
                /*if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }*/
                Logger.error(TAG, "handleDataMessage: __________");
            }
        } catch (Exception e) {
            Logger.error(TAG, "Json Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
        Logger.error(TAG, "showNotificationMessage: ");
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
