/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.NotificationChannel
 *  android.app.NotificationManager
 *  android.content.Context
 *  android.media.AudioAttributes
 *  android.media.AudioAttributes$Builder
 *  android.media.RingtoneManager
 *  android.net.Uri
 *  android.os.Build
 *  android.os.Build$VERSION
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class NotificationChannelsHelper {
    public static String getDefaultChannel(Context context) {
        Object object;
        if (Build.VERSION.SDK_INT >= 26 && (object = context.getSystemService("notification")) instanceof NotificationManager) {
            NotificationChannel notificationChannel = new NotificationChannel("default", (CharSequence)context.getString(2131689555), 3);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            ((NotificationManager)object).createNotificationChannel(notificationChannel);
        }
        return "default";
    }

    public static String getImportantChannel(Context context) {
        Object object;
        if (Build.VERSION.SDK_INT >= 26 && (object = context.getSystemService("notification")) instanceof NotificationManager) {
            NotificationChannel notificationChannel = new NotificationChannel("important", (CharSequence)context.getString(2131689615), 4);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(RingtoneManager.getDefaultUri((int)2), new AudioAttributes.Builder().setUsage(5).build());
            ((NotificationManager)object).createNotificationChannel(notificationChannel);
        }
        return "important";
    }
}

