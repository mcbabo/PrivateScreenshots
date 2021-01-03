/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.pm.PackageInfo
 *  android.content.pm.PackageManager
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.dev;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Dev {
    public static boolean isDebug(Context context) {
        return Dev.isDev(context);
    }

    public static boolean isDev(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.shamanland.dev", 0);
            boolean bl = false;
            if (packageInfo != null) {
                bl = true;
            }
            return bl;
        }
        catch (Exception exception) {
            return false;
        }
    }
}

