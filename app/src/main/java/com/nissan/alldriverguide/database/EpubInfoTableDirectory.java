package com.nissan.alldriverguide.database;

import android.provider.BaseColumns;

/**
 * Created by Rohan on 9/16/2017.
 */

public class EpubInfoTableDirectory implements BaseColumns {

    public static final String TABLE_NAME = "epub_info";
    public static final String TITLE = "title";
    public static final String TAG = "tag";
    public static final String LINK = "link";
    public static final String CARTYPE = "car_type";
    public static final String EPUBTYPE = "epub_type";
    public static final String INDEX = "index_keyword";

    public EpubInfoTableDirectory() {
    }
}
