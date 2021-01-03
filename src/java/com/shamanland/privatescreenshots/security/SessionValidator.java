/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.Application
 *  android.app.Application$ActivityLifecycleCallbacks
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.os.Bundle
 *  android.os.Handler
 *  android.os.Looper
 *  android.os.SystemClock
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.security;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

public class SessionValidator
extends BroadcastReceiver
implements Application.ActivityLifecycleCallbacks {
    private static final String LOG_TAG = "SessionValidator";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean initialized;
    private final Runnable resetUnlocked = new Runnable(){

        public void run() {
            SessionValidator.this.onResetUnlocked();
        }
    };
    private long screenOff;
    private long unlocked;

    private static long timestamp() {
        return SystemClock.elapsedRealtime();
    }

    public void init(Application application) {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        application.registerReceiver((BroadcastReceiver)this, new IntentFilter("android.intent.action.SCREEN_OFF"));
        application.registerActivityLifecycleCallbacks((Application.ActivityLifecycleCallbacks)this);
    }

    public boolean isSessionValid() {
        long l = this.unlocked;
        return l > 0L && l > this.screenOff - 1000L;
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
        this.handler.postDelayed(this.resetUnlocked, 3000L);
    }

    public void onActivityResumed(Activity activity) {
        this.handler.removeCallbacks(this.resetUnlocked);
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.SCREEN_OFF".equals((Object)intent.getAction())) {
            this.screenOff = SessionValidator.timestamp();
        }
    }

    protected void onResetUnlocked() {
        this.unlocked = 0L;
    }

    public void onUnlocked() {
        this.unlocked = SessionValidator.timestamp();
    }

}

