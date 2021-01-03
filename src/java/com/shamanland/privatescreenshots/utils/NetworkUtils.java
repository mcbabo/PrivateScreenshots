/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.net.ConnectivityManager
 *  android.net.NetworkInfo
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
        if (connectivityManager != null && (networkInfo = connectivityManager.getActiveNetworkInfo()) != null) {
            return networkInfo.isConnected();
        }
        return false;
    }
}

