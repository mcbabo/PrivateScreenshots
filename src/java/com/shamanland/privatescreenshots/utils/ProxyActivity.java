/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.ComponentName
 *  android.content.Context
 *  android.content.Intent
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.Parcelable
 *  android.widget.Toast
 *  java.lang.Class
 *  java.lang.RuntimeException
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.base.BaseActivity;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;

public class ProxyActivity
extends BaseActivity {
    private static boolean startForegroundServiceSafe(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                context.startForegroundService(intent);
                return true;
            }
            catch (RuntimeException runtimeException) {
                Crane.report(runtimeException);
            }
        }
        return false;
    }

    public static boolean startServiceTricky(Context context, Intent intent) {
        try {
            context.startService(intent);
            return true;
        }
        catch (RuntimeException runtimeException) {
            Crane.report(runtimeException);
            return ProxyActivity.startServiceViaActivity(context, intent);
        }
    }

    private static boolean startServiceViaActivity(Context context, Intent intent) {
        try {
            Intent intent2 = new Intent(context, ProxyActivity.class);
            intent2.putExtra("06ce639c04ca0b0c", (Parcelable)intent);
            intent2.addFlags(268435456);
            intent2.addFlags(1073741824);
            intent2.addFlags(8388608);
            context.startActivity(intent2);
            return true;
        }
        catch (RuntimeException runtimeException) {
            Crane.report(runtimeException);
            return ProxyActivity.startForegroundServiceSafe(context, intent);
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        boolean bl;
        block8 : {
            super.onCreate(bundle);
            Analytics analytics = this.getComponentLocator().getAnalytics().get();
            Intent intent = this.getIntent();
            bl = true;
            if (intent != null) {
                Intent intent2 = (Intent)intent.getParcelableExtra("06ce639c04ca0b0c");
                if (intent2 != null) {
                    try {
                        this.startService(intent2);
                        analytics.logEvent("proxy_act_srv_started");
                        bl = false;
                    }
                    catch (RuntimeException runtimeException) {
                        Crane.report(runtimeException);
                        if (ProxyActivity.startForegroundServiceSafe((Context)this, intent2)) {
                            analytics.logEvent("proxy_act_srv_started_fg");
                            bl = false;
                            break block8;
                        }
                        analytics.logEvent("proxy_act_srv_not_started");
                    }
                } else {
                    analytics.logEvent("proxy_act_no_target_intent");
                }
            } else {
                analytics.logEvent("proxy_act_no_intent");
            }
        }
        if (bl) {
            Toast.makeText((Context)this.getApplicationContext(), (int)2131689708, (int)0).show();
        }
        this.finish();
        this.overridePendingTransition(0, 0);
    }
}

