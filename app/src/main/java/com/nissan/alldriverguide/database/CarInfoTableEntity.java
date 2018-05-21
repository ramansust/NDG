package com.nissan.alldriverguide.database;

import android.provider.BaseColumns;

/**
 *
 * @author Raman
 * @version 1.0
 *
 */

public class CarInfoTableEntity implements BaseColumns {

    public static final String TABLE_NAME = "car_info";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String DATE_TIME = "dateTime";
    public static final String REGION = "region";
    public static final String LANGUAGE = "language";
    public static final String VERSION_NAME = "version_name";
    public static final String VERSION_CODE = "version_code";

    public CarInfoTableEntity() {

    }
}
