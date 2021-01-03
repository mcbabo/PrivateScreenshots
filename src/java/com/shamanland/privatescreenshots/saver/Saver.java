/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$CompressFormat
 *  android.graphics.Bitmap$Config
 *  android.media.Image
 *  android.media.Image$Plane
 *  android.os.Handler
 *  android.os.Looper
 *  android.os.SystemClock
 *  com.google.firebase.perf.FirebasePerformance
 *  com.google.firebase.perf.metrics.Trace
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.lang.Enum
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.OutOfMemoryError
 *  java.lang.Runnable
 *  java.lang.String
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.util.concurrent.Executor
 */
package com.shamanland.privatescreenshots.saver;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.BitmapCache;
import com.shamanland.common.utils.Cache;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.saver.BlackScreenException;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.ErrorParser;
import com.shamanland.toaster.Toaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class Saver {
    protected static final String LOG_TAG = "Saver";
    final LazyRef<Analytics> analytics;
    boolean destroyed;
    final ErrorListener errorListener;
    final LazyRef<? extends Executor> executor;
    final File filesDir;
    final Handler mainThread;
    final LazyRef<Storage> storage;

    public Saver(LazyRef<Storage> lazyRef, LazyRef<Analytics> lazyRef2, LazyRef<? extends Executor> lazyRef3, File file, ErrorListener errorListener) {
        this.storage = lazyRef;
        this.analytics = lazyRef2;
        this.executor = lazyRef3;
        this.filesDir = file;
        this.errorListener = errorListener;
        this.mainThread = new Handler(Looper.getMainLooper());
    }

    public static ByteBuffer clone(ByteBuffer byteBuffer) {
        ByteBuffer byteBuffer2 = Cache.allocate(byteBuffer.capacity());
        byteBuffer.rewind();
        byteBuffer2.put(byteBuffer);
        byteBuffer.rewind();
        byteBuffer2.flip();
        return byteBuffer2;
    }

    private static boolean isBlackImage(int n, int n2, ByteBuffer byteBuffer, int n3, int n4) {
        int n5 = n * n3;
        byte[] arrby = new byte[n5];
        int n6 = 30 * (n5 * n2) / 100;
        int n7 = n2 / 3;
        int n8 = 0;
        for (int i = 0; i < 3; ++i) {
            int n9 = n8;
            for (int j = 0; j < n7; ++j) {
                byteBuffer.position(n4 * (i + j * 3));
                byteBuffer.get(arrby, 0, n5);
                int n10 = n9;
                for (int k = 0; k < n5; ++k) {
                    if (arrby[k] == 0) continue;
                    ++n10;
                }
                if (n10 > n6) {
                    return false;
                }
                n9 = n10;
            }
            n8 = n9;
        }
        return true;
    }

    public void destroy() {
        this.destroyed = true;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @SuppressLint(value={"WrongThread"})
    File[] performSaveImage(int n, int n2, ByteBuffer byteBuffer, int n3, int n4) throws IOException {
        SystemClock.uptimeMillis();
        int n5 = n + (n4 - n3 * n) / n3;
        try {
            if (!Saver.isBlackImage(n, n2, byteBuffer, n3, n4)) {
                byteBuffer.position(0);
                Bitmap bitmap = BitmapCache.createBitmap(n5, n2, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer((Buffer)byteBuffer);
                Bitmap bitmap2 = Bitmap.createBitmap((Bitmap)bitmap, (int)0, (int)0, (int)n, (int)n2);
                BitmapCache.release(bitmap);
                File file = new File(this.filesDir, "tmp");
                file.mkdirs();
                File file2 = File.createTempFile((String)"tmp", (String)"tmp", (File)file);
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                bitmap2.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream)fileOutputStream);
                fileOutputStream.close();
                Bitmap bitmap3 = n > n2 ? Bitmap.createScaledBitmap((Bitmap)bitmap2, (int)512, (int)(n2 * 512 / n), (boolean)false) : Bitmap.createScaledBitmap((Bitmap)bitmap2, (int)(n * 512 / n2), (int)512, (boolean)false);
                File file3 = File.createTempFile((String)"tmp", (String)"tmp", (File)file);
                FileOutputStream fileOutputStream2 = new FileOutputStream(file3);
                bitmap3.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream)fileOutputStream2);
                bitmap3.recycle();
                fileOutputStream2.close();
                return new File[]{file2, file3};
            }
            this.errorListener.onBlackImage();
            throw new BlackScreenException();
        }
        finally {
            Cache.release(byteBuffer);
        }
    }

    public Status saveImage(Image image) {
        ByteBuffer byteBuffer;
        Trace trace = FirebasePerformance.startTrace((String)"99df9174");
        if (this.destroyed) {
            Status status = Status.DESTROYED;
            trace.stop();
            return status;
        }
        Image.Plane[] arrplane = image.getPlanes();
        if (arrplane.length < 1) {
            Status status = Status.WRONG_IMAGE;
            trace.stop();
            return status;
        }
        final int n = arrplane[0].getPixelStride();
        final int n2 = arrplane[0].getRowStride();
        try {
            byteBuffer = Saver.clone(arrplane[0].getBuffer());
        }
        catch (OutOfMemoryError outOfMemoryError) {
            this.analytics.get().logError("saver_oom_1");
            Status status = Status.OOM;
            trace.stop();
            return status;
        }
        final int n3 = image.getWidth();
        final int n4 = image.getHeight();
        Executor executor = this.executor.get();
        Runnable runnable = new Runnable(){

            public void run() {
                Saver.this.saveImageImpl(n3, n4, byteBuffer, n, n2);
            }
        };
        executor.execute(runnable);
        Status status = Status.OK;
        trace.stop();
        return status;
    }

    protected void saveImageImpl(int n, int n2, ByteBuffer byteBuffer, int n3, int n4) {
        try {
            final File[] arrfile = this.performSaveImage(n, n2, byteBuffer, n3, n4);
            this.mainThread.post(new Runnable(){

                public void run() {
                    Saver saver = Saver.this;
                    File[] arrfile2 = arrfile;
                    saver.saveToStorage(arrfile2[0], arrfile2[1]);
                }
            });
            return;
        }
        catch (Exception exception) {
            if (ErrorParser.findErrorNoEntity(exception.getMessage())) {
                this.analytics.get().logError("saver_no_entity");
                return;
            }
            Crane.report(exception);
            return;
        }
        catch (BlackScreenException blackScreenException) {
            this.analytics.get().logError("black_screen_detected");
            return;
        }
        catch (OutOfMemoryError outOfMemoryError) {
            Toaster.toast(2131689654);
            this.analytics.get().logError("saver_oom_2");
            return;
        }
    }

    void saveToStorage(File file, File file2) {
        long l = file.length() + file2.length();
        if (this.storage.get().saveNew(file, file2)) {
            this.analytics.get().logEvent("screenshot_saved", l);
            return;
        }
        Toaster.toast(2131689527);
        this.analytics.get().logError("screenshot_not_saved");
    }

    public static interface ErrorListener {
        public void onBlackImage();
    }

    public static final class Status
    extends Enum<Status> {
        private static final /* synthetic */ Status[] $VALUES;
        public static final /* enum */ Status DESTROYED;
        public static final /* enum */ Status NO_DATA;
        public static final /* enum */ Status OK;
        public static final /* enum */ Status OOM;
        public static final /* enum */ Status UNKNOWN_ERROR;
        public static final /* enum */ Status WRONG_BUFFER_FORMAT;
        public static final /* enum */ Status WRONG_IMAGE;
        public final int stringId;

        static {
            OK = new Status(0);
            NO_DATA = new Status(2131689648);
            OOM = new Status(2131689654);
            WRONG_BUFFER_FORMAT = new Status(2131689715);
            WRONG_IMAGE = new Status(2131689716);
            DESTROYED = new Status(2131689563);
            UNKNOWN_ERROR = new Status(2131689708);
            Status[] arrstatus = new Status[]{OK, NO_DATA, OOM, WRONG_BUFFER_FORMAT, WRONG_IMAGE, DESTROYED, UNKNOWN_ERROR};
            $VALUES = arrstatus;
        }

        private Status(int n2) {
            this.stringId = n2;
        }

        public static Status valueOf(String string2) {
            return (Status)Enum.valueOf(Status.class, (String)string2);
        }

        public static Status[] values() {
            return (Status[])$VALUES.clone();
        }
    }

}

