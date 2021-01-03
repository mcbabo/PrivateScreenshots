/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.KeyguardManager
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.database.ContentObserver
 *  android.net.Uri
 *  android.os.Bundle
 *  android.provider.DocumentsContract
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 */
package com.shamanland.privatescreenshots.storage;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.base.BaseActivity;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.security.SessionValidator;

public class ConfirmCredentialsActivity
extends BaseActivity {
    private LazyRef<Analytics> analytics;
    private boolean finish;

    public static boolean canLaunchNow(Context context) {
        long l = 3000L + context.getSharedPreferences("b1002f22694724e3", 0).getLong("737e122d44c0b1ab", 0L) LCMP System.currentTimeMillis();
        boolean bl = false;
        if (l < 0) {
            bl = true;
        }
        return bl;
    }

    @SuppressLint(value={"ApplySharedPref"})
    private static void saveTimestamp(Context context) {
        context.getSharedPreferences("b1002f22694724e3", 0).edit().putLong("737e122d44c0b1ab", System.currentTimeMillis()).commit();
    }

    protected void onActivityResult(int n, int n2, Intent intent) {
        super.onActivityResult(n, n2, intent);
        ConfirmCredentialsActivity.saveTimestamp(this.getApplicationContext());
        if (n2 == -1) {
            this.analytics.get().logEvent("confirm_credentials_result", "ok");
            this.getComponentLocator().getSessionValidator().get().onUnlocked();
            this.getContentResolver().notifyChange(DocumentsContract.buildChildDocumentsUri((String)this.getPackageName(), (String)"472eca83"), null);
        } else {
            this.analytics.get().logEvent("confirm_credentials_result", "cancel");
        }
        this.finish = true;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ConfirmCredentialsActivity.saveTimestamp(this.getApplicationContext());
        this.analytics = this.getComponentLocator().getAnalytics();
        if (bundle == null) {
            KeyguardManager keyguardManager = (KeyguardManager)this.getSystemService("keyguard");
            if (keyguardManager != null) {
                Object[] arrobject = new Object[]{this.getString(2131689512)};
                String string2 = this.getString(2131689573, arrobject);
                Object[] arrobject2 = new Object[]{this.getString(2131689512)};
                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent((CharSequence)string2, (CharSequence)this.getString(2131689572, arrobject2));
                if (intent != null) {
                    this.analytics.get().logEvent("confirm_credentials");
                    this.startActivityForResult(intent, 1);
                    return;
                }
                this.analytics.get().logError("keyguard_null");
                this.finishAndRemoveTask();
                return;
            }
            this.analytics.get().logError("keyguard_null");
            this.finishAndRemoveTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.finish) {
            this.finishAndRemoveTask();
        }
    }
}

