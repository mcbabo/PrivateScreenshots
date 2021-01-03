/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Application
 *  android.content.Context
 *  android.os.Handler
 *  com.crashlytics.android.Crashlytics
 *  com.google.android.gms.ads.AdActivity
 *  com.google.android.gms.ads.AdActivity$Reporter
 *  com.google.firebase.analytics.FirebaseAnalytics
 *  io.fabric.sdk.android.Fabric
 *  io.fabric.sdk.android.Kit
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.StackTraceElement
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.util.Locale
 */
package com.shamanland.privatescreenshots;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import java.util.Locale;

public class PsApp
extends Application {
    protected static final String LOG_TAG = "PsApp";
    protected Crashlytics crashlytics;

    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new Thread.UncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler()){
            final /* synthetic */ Thread.UncaughtExceptionHandler val$defaultHandler;
            {
                this.val$defaultHandler = uncaughtExceptionHandler;
            }

            public void uncaughtException(Thread thread, Throwable throwable) {
                if (PsApp.this.crashlytics == null) {
                    Analytics analytics = new Analytics(FirebaseAnalytics.getInstance((Context)PsApp.this.getApplicationContext()), PsApp.this.getApplicationContext());
                    StackTraceElement[] arrstackTraceElement = throwable.getStackTrace();
                    if (arrstackTraceElement != null && arrstackTraceElement.length > 0) {
                        StackTraceElement stackTraceElement;
                        String string2;
                        String string3;
                        block9 : {
                            int n = arrstackTraceElement.length;
                            for (int i = 0; i < n; ++i) {
                                stackTraceElement = arrstackTraceElement[i];
                                if (stackTraceElement.getClassName() == null || !stackTraceElement.getClassName().contains((CharSequence)".shamanland.")) {
                                    continue;
                                }
                                break block9;
                            }
                            stackTraceElement = null;
                        }
                        if (stackTraceElement == null) {
                            stackTraceElement = arrstackTraceElement[0];
                        }
                        if ((string2 = stackTraceElement.getClassName()) != null) {
                            int n = string2.lastIndexOf(46);
                            string3 = null;
                            if (n >= 0) {
                                string3 = n > string2.length() - 2 ? null : string2.substring(n + 1);
                            }
                        } else {
                            string3 = string2;
                        }
                        Object[] arrobject = new Object[2];
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("uncaught_");
                        stringBuilder.append(throwable.getClass().getSimpleName().toLowerCase(Locale.US));
                        arrobject[0] = stringBuilder.toString();
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(string3);
                        stringBuilder2.append("_");
                        stringBuilder2.append(stackTraceElement.getMethodName());
                        stringBuilder2.append("_");
                        stringBuilder2.append(stackTraceElement.getLineNumber());
                        arrobject[1] = stringBuilder2.toString();
                        analytics.logError(arrobject);
                    } else {
                        analytics.logError("uncaught_no_stack");
                    }
                }
                this.val$defaultHandler.uncaughtException(thread, throwable);
            }
        });
        Crane.setCrashReporter(new Crane.CrashReporter(){

            @Override
            public void log(String string2) {
                if (PsApp.this.crashlytics != null) {
                    Crashlytics.log((String)string2);
                }
            }

            @Override
            public void report(Throwable throwable) {
                if (PsApp.this.crashlytics != null) {
                    Crashlytics.logException((Throwable)throwable);
                }
            }
        });
        new Handler().postDelayed(new Runnable(){

            public void run() {
                Crashlytics crashlytics;
                Context context = PsApp.this.getApplicationContext();
                Kit[] arrkit = new Kit[1];
                PsApp psApp = PsApp.this;
                psApp.crashlytics = crashlytics = new Crashlytics();
                arrkit[0] = crashlytics;
                Fabric.with((Context)context, (Kit[])arrkit);
            }
        }, 5000L);
        AdActivity.reporter = new AdActivity.Reporter(){

            public void report(Throwable throwable, String string2) {
                if (string2 != null) {
                    ComponentLocator.getInstance(PsApp.this.getApplicationContext()).getAnalytics().get().logError(string2);
                    return;
                }
                if (throwable != null) {
                    Crane.report(throwable);
                }
            }
        };
    }

}

