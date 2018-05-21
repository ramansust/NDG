package com.nissan.alldriverguide.database;

import android.provider.BaseColumns;

/**
 * Created by shubha on 11/9/17.
 */

public class PushNotificationTableEntity implements BaseColumns {

    public static final String TABLE_NAME = "push_notification";
    public static final String CAR_ID = "car_id";
    public static final String LANGUAGE_ID = "lang_id";
    public static final String EPUB_ID = "epub_id";
    public static final String STATUS = "status";
    public static final String DATE_TIME = "date_time";
    public static final String UPDATE_AVAILABLE = "1";
    public static final String UPDATE_UNAVAILABLE = "0";

    public PushNotificationTableEntity() {

    }

}
