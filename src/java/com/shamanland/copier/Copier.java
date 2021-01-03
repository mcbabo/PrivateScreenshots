/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Service
 *  android.content.ClipData
 *  android.content.ClipboardManager
 *  android.content.Context
 *  android.content.Intent
 *  android.os.IBinder
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.copier;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.shamanland.common.R;
import com.shamanland.toaster.Toaster;

public class Copier
extends Service {
    public static Intent createCopyIntent(Context context, String string2) {
        Intent intent = new Intent(context, Copier.class);
        intent.putExtra("text", string2);
        return intent;
    }

    public static boolean hasClipboard(Context context) {
        return context.getSystemService("clipboard") instanceof ClipboardManager;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int n, int n2) {
        String string2 = intent.getStringExtra("text");
        ClipboardManager clipboardManager = (ClipboardManager)this.getSystemService("clipboard");
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, (CharSequence)string2));
            Toaster.toast(R.string.copied_to_clipboard);
        } else {
            Toaster.toast(R.string.clipboard_not_available);
        }
        this.stopSelf();
        return super.onStartCommand(intent, n, n2);
    }
}

