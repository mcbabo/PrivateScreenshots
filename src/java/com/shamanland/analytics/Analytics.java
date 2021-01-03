/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.Bundle
 *  com.google.firebase.analytics.FirebaseAnalytics
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Calendar
 *  java.util.TimeZone
 */
package com.shamanland.analytics;

import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.shamanland.analytics.AnalyticsProperty;
import com.shamanland.crane.Crane;
import java.util.Calendar;
import java.util.TimeZone;

public class Analytics {
    private static final String LOG_TAG = "Analytics";
    private final FirebaseAnalytics fa;

    /*
     * Exception decompiling
     */
    public Analytics(FirebaseAnalytics var1, Context var2) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Invalid stack depths @ lbl209 : RETURN : trying to set 1 previously set to 0
        // org.benf.cfr.reader.b.a.a.g.a(Op02WithProcessedDataAndRefs.java:203)
        // org.benf.cfr.reader.b.a.a.g.a(Op02WithProcessedDataAndRefs.java:1489)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:308)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:182)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:127)
        // org.benf.cfr.reader.entities.attributes.f.c(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.g.p(Method.java:396)
        // org.benf.cfr.reader.entities.d.e(ClassFile.java:890)
        // org.benf.cfr.reader.entities.d.b(ClassFile.java:792)
        // org.benf.cfr.reader.b.a(Driver.java:128)
        // org.benf.cfr.reader.a.a(CfrDriverImpl.java:63)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.decompileWithCFR(JavaExtractionWorker.kt:61)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.doWork(JavaExtractionWorker.kt:130)
        // com.njlabs.showjava.decompilers.BaseDecompiler.withAttempt(BaseDecompiler.kt:108)
        // com.njlabs.showjava.workers.DecompilerWorker$b.run(DecompilerWorker.kt:118)
        // java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
        // java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
        // java.lang.Thread.run(Thread.java:919)
        throw new IllegalStateException("Decompilation failed");
    }

    public static int getCalendarOffset() {
        try {
            Calendar calendar = Calendar.getInstance();
            int n = calendar.getTimeZone().getOffset(calendar.getTimeInMillis());
            return n;
        }
        catch (Exception exception) {
            Crane.report(exception);
            return Integer.MAX_VALUE;
        }
    }

    public /* varargs */ void logError(Object ... arrobject) {
        if (arrobject != null && arrobject.length >= 1) {
            this.logEvent("error_x", arrobject);
            return;
        }
        throw new IllegalArgumentException();
    }

    public void logEvent(String string2) {
        this.fa.logEvent(string2, Bundle.EMPTY);
    }

    public void logEvent(String string2, long l) {
        Bundle bundle = new Bundle();
        bundle.putLong("value", l);
        this.fa.logEvent(string2, bundle);
    }

    public /* varargs */ void logEvent(String string2, Object ... arrobject) {
        Bundle bundle = new Bundle();
        if (arrobject != null) {
            int n = 0;
            int n2 = arrobject.length;
            while (n < n2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("param");
                int n3 = n + 1;
                stringBuilder.append(n3);
                bundle.putString(stringBuilder.toString(), String.valueOf((Object)arrobject[n]));
                n = n3;
            }
        }
        this.fa.logEvent(string2, bundle);
    }

    public void setProperty(AnalyticsProperty analyticsProperty, String string2, String string3) {
        FirebaseAnalytics firebaseAnalytics = this.fa;
        StringBuilder stringBuilder = new StringBuilder();
        String string4 = analyticsProperty.id < 10 ? "property0" : "property";
        stringBuilder.append(string4);
        stringBuilder.append(analyticsProperty.id);
        String string5 = stringBuilder.toString();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(string2);
        stringBuilder2.append("=");
        stringBuilder2.append(string3);
        firebaseAnalytics.setUserProperty(string5, stringBuilder2.toString());
    }
}

