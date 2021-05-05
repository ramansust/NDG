package com.nissan.alldriverguide.internetconnection;

import android.content.Context;
import android.net.ConnectivityManager;

public class DetectConnection {

    /**
     * The internet connection available or not
     *
     * @param context need for get system service
     * @return true if net is connected otherwise false
     */
    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected();
    }
}
