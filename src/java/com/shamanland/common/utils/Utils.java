/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.ComponentName
 *  android.content.Context
 *  android.content.Intent
 *  android.content.Intent$ShortcutIconResource
 *  android.content.pm.PackageManager
 *  android.content.pm.ResolveInfo
 *  android.net.Uri
 *  android.os.Parcelable
 *  android.os.TransactionTooLargeException
 *  android.text.TextUtils
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.InterruptedException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.util.Locale
 *  java.util.concurrent.BlockingQueue
 *  java.util.concurrent.LinkedTransferQueue
 *  java.util.concurrent.RejectedExecutionHandler
 *  java.util.concurrent.ThreadFactory
 *  java.util.concurrent.ThreadPoolExecutor
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.atomic.AtomicInteger
 */
package com.shamanland.common.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.os.TransactionTooLargeException;
import android.text.TextUtils;
import com.shamanland.crane.Crane;
import com.shamanland.toaster.Toaster;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    private static final String LOG_TAG = "Utils";

    public static ThreadPoolExecutor createThreadPool(int n, int n2, final String string2, long l) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(n, n2, l, TimeUnit.SECONDS, (BlockingQueue)new LinkedTransferQueue<Runnable>(){

            public boolean offer(Runnable runnable) {
                return this.tryTransfer((Object)runnable);
            }
        }, new ThreadFactory(){
            final AtomicInteger counter = new AtomicInteger();

            public Thread newThread(Runnable runnable) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(string2);
                stringBuilder.append("-");
                stringBuilder.append(this.counter.incrementAndGet());
                return new Thread(runnable, stringBuilder.toString());
            }
        }, new RejectedExecutionHandler(){

            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                try {
                    threadPoolExecutor.getQueue().put((Object)runnable);
                    return;
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    public static String formatBytesSize(long l) {
        if (l > 0x40000000L) {
            Locale locale = Locale.US;
            Object[] arrobject = new Object[]{(double)l / 1.073741824E9};
            return String.format((Locale)locale, (String)"%.2f Gb", (Object[])arrobject);
        }
        if (l > 0x100000L) {
            Locale locale = Locale.US;
            Object[] arrobject = new Object[]{(double)l / 1048576.0};
            return String.format((Locale)locale, (String)"%.2f Mb", (Object[])arrobject);
        }
        if (l > 1024L) {
            Locale locale = Locale.US;
            Object[] arrobject = new Object[]{l / 1024L};
            return String.format((Locale)locale, (String)"%d Kb", (Object[])arrobject);
        }
        Locale locale = Locale.US;
        Object[] arrobject = new Object[]{l};
        return String.format((Locale)locale, (String)"%d b", (Object[])arrobject);
    }

    /*
     * Exception decompiling
     */
    public static String getManifestMetadata(Context var0, String var1, String var2) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Invalid stack depths @ lbl21.1 : ALOAD_2 : trying to set 1 previously set to 0
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

    public static Intent getMarketIntent(Context context) {
        return Utils.getMarketIntent(context, context.getPackageName());
    }

    public static Intent getMarketIntent(Context context, String string2) {
        return Utils.getMarketIntent(context, string2, null);
    }

    public static Intent getMarketIntent(Context context, String string2, String string3) {
        String string4;
        if (!TextUtils.isEmpty((CharSequence)string3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("&referrer=");
            stringBuilder.append(string3);
            string4 = stringBuilder.toString();
        } else {
            string4 = "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("market://details?id=");
        stringBuilder.append(string2);
        stringBuilder.append(string4);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse((String)stringBuilder.toString()));
        if (context.getPackageManager().resolveActivity(intent, 0) == null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("https://play.google.com/store/apps/details?id=");
            stringBuilder2.append(string2);
            stringBuilder2.append(string4);
            intent = new Intent("android.intent.action.VIEW", Uri.parse((String)stringBuilder2.toString()));
        }
        return intent;
    }

    public static int getOptimalMaxPoolSize() {
        return Math.max((int)2, (int)(1 + 2 * Runtime.getRuntime().availableProcessors()));
    }

    public static boolean hasInstanceOf(Throwable throwable, Class<? extends Throwable> class_) {
        while (throwable != null) {
            if (class_.isInstance((Object)throwable)) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }

    public static void installShortcut(Context context, Intent intent, int n, int n2) {
        Intent intent2 = new Intent();
        intent2.putExtra("android.intent.extra.shortcut.INTENT", (Parcelable)intent);
        intent2.putExtra("android.intent.extra.shortcut.NAME", context.getString(n));
        intent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", (Parcelable)Intent.ShortcutIconResource.fromContext((Context)context, (int)n2));
        intent2.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(intent2);
    }

    public static boolean openMarket(Context context, int n) {
        try {
            context.startActivity(Utils.getMarketIntent(context));
            return true;
        }
        catch (Exception exception) {
            Toaster.toast(n);
            Crane.report(exception);
            return false;
        }
    }

    public static boolean tryStartActivity(Context context, Intent intent, int n) {
        return Utils.tryStartActivity(context, intent, n, n);
    }

    public static boolean tryStartActivity(Context context, Intent intent, int n, int n2) {
        boolean bl = true;
        try {
            context.startActivity(intent);
            return bl;
        }
        catch (Exception exception) {
            if (Utils.hasInstanceOf(exception, TransactionTooLargeException.class)) {
                Toaster.toast(n2);
                if (n2 != n) {
                    bl = false;
                }
            } else {
                Toaster.toast(n);
            }
            if (bl) {
                Crane.report(exception);
            }
            return false;
        }
    }

    public static boolean tryStartActivityForResult(Activity activity, Intent intent, int n, int n2) {
        try {
            activity.startActivityForResult(intent, n);
            return true;
        }
        catch (Exception exception) {
            Toaster.toast(n2);
            Crane.report(exception);
            return false;
        }
    }

    public static boolean tryStartService(Context context, Intent intent, int n) {
        try {
            context.startService(intent);
            return true;
        }
        catch (Exception exception) {
            Toaster.toast(n);
            Crane.report(exception);
            return false;
        }
    }

}

