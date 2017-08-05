package com.example.mannas.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Mannas on 7/26/2017.
 */

public class Utility {

    public static Boolean isOffline(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if(info==null){
            return true;
        }else {
            return !info.isConnected();
        }
    }
}
