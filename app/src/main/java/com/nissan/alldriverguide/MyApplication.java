package com.nissan.alldriverguide;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/*
 * Class for Google Analytics settings
 */
public class MyApplication extends MultiDexApplication {

    // The following line should be changed to include the correct property id.
    public static final String PROPERTY_ID = "UA-57902793-1";// UA-57795549-1 (Main)
    //    public static final String PROPERTY_ID = "UA-93701816-1";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        MyApplication.context = getApplicationContext();
        Fresco.initialize(getApplicationContext());
    }

    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER,
        ECOMMERCE_TRACKER,
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public MyApplication() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
                    .newTracker(PROPERTY_ID) : analytics
                    .newTracker(null);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    // auve
    public static Context getAppContext() {
        return MyApplication.context;
    }

}