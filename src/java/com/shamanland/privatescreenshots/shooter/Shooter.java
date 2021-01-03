/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Notification
 *  android.app.NotificationManager
 *  android.app.PendingIntent
 *  android.content.Context
 *  android.content.Intent
 *  android.content.res.Resources
 *  android.graphics.Point
 *  android.hardware.display.VirtualDisplay
 *  android.hardware.display.VirtualDisplay$Callback
 *  android.media.ImageReader
 *  android.media.ImageReader$OnImageAvailableListener
 *  android.media.projection.MediaProjection
 *  android.media.projection.MediaProjection$Callback
 *  android.media.projection.MediaProjectionManager
 *  android.os.Handler
 *  android.support.v4.app.NotificationCompat
 *  android.support.v4.app.NotificationCompat$BigTextStyle
 *  android.support.v4.app.NotificationCompat$Builder
 *  android.support.v4.app.NotificationCompat$Style
 *  android.util.DisplayMetrics
 *  android.view.Display
 *  android.view.Surface
 *  android.view.WindowManager
 *  java.io.File
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.util.concurrent.Executor
 */
package com.shamanland.privatescreenshots.shooter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.main.Launcher;
import com.shamanland.privatescreenshots.saver.Saver;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.NotificationChannelsHelper;
import java.io.File;
import java.util.concurrent.Executor;

public class Shooter {
    static final String LOG_TAG = "Shooter";
    private final LazyRef<Analytics> analytics;
    private VirtualDisplay display;
    Listener listener;
    private MediaProjection projection;
    private MediaProjection.Callback projectionCallback;
    private ImageReader reader;
    Saver saver;

    public Shooter(final Context context, Intent intent, LazyRef<Storage> lazyRef, LazyRef<Analytics> lazyRef2, LazyRef<? extends Executor> lazyRef3) {
        MediaProjection mediaProjection;
        this.analytics = lazyRef2;
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)context.getSystemService("media_projection");
        WindowManager windowManager = (WindowManager)context.getSystemService("window");
        if (mediaProjectionManager != null && windowManager != null && (mediaProjection = (this.projection = mediaProjectionManager.getMediaProjection(-1, intent))) != null) {
            Saver saver;
            MediaProjection.Callback callback;
            this.projectionCallback = callback = new MediaProjection.Callback(){

                public void onStop() {
                    if (Shooter.this.listener != null) {
                        Shooter.this.listener.onStopped();
                    }
                }
            };
            mediaProjection.registerCallback(callback, null);
            Point point = new Point();
            windowManager.getDefaultDisplay().getRealSize(point);
            int n = point.x;
            int n2 = point.y;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int n3 = displayMetrics.densityDpi;
            this.reader = ImageReader.newInstance((int)n, (int)n2, (int)1, (int)1);
            this.display = this.projection.createVirtualDisplay("vd", n, n2, n3, 0, this.reader.getSurface(), null, null);
            this.saver = saver = new Saver(lazyRef, lazyRef2, lazyRef3, context.getFilesDir(), new Saver.ErrorListener(){

                @Override
                public void onBlackImage() {
                    NotificationManager notificationManager = (NotificationManager)context.getSystemService("notification");
                    if (notificationManager != null) {
                        String string2 = context.getString(2131689521);
                        String string3 = context.getString(2131689520);
                        Context context3 = context;
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context3, NotificationChannelsHelper.getImportantChannel(context3)).setSmallIcon(2131230848).setContentTitle((CharSequence)string2).setContentText((CharSequence)string3);
                        Context context2 = context;
                        notificationManager.notify(4098, builder.setContentIntent(PendingIntent.getActivity((Context)context2, (int)0, (Intent)new Intent(context2, Launcher.class), (int)0)).setColor(context.getResources().getColor(2131099724)).setStyle((NotificationCompat.Style)new NotificationCompat.BigTextStyle().bigText((CharSequence)string3)).setAutoCancel(true).build());
                    }
                }
            });
        }
        if (this.projection == null) {
            lazyRef2.get().logError("media_projection_null");
        }
    }

    public void destroy() {
        VirtualDisplay virtualDisplay;
        ImageReader imageReader;
        MediaProjection mediaProjection;
        Saver saver = this.saver;
        if (saver != null) {
            saver.destroy();
            this.saver = null;
        }
        if ((imageReader = this.reader) != null) {
            imageReader.setOnImageAvailableListener(null, null);
            this.reader.close();
            this.reader = null;
        }
        if ((virtualDisplay = this.display) != null) {
            virtualDisplay.release();
            this.display = null;
        }
        if ((mediaProjection = this.projection) != null) {
            mediaProjection.stop();
            this.projection.unregisterCallback(this.projectionCallback);
            this.projection = null;
            this.projectionCallback = null;
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /*
     * Exception decompiling
     */
    public void take() {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Invalid stack depths @ lbl118 : ALOAD_0 : trying to set 1 previously set to 0
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

    public static interface Listener {
        public void onNewStatus(Saver.Status var1);

        public void onStopped();
    }

}

