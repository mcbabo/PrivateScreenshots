/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$Config
 *  android.graphics.BitmapFactory
 *  android.graphics.BitmapFactory$Options
 *  android.os.Handler
 *  android.util.Pair
 *  com.google.firebase.perf.FirebasePerformance
 *  com.google.firebase.perf.metrics.Trace
 *  java.io.File
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.ref.WeakReference
 *  java.util.Iterator
 *  java.util.LinkedHashMap
 *  java.util.Set
 *  java.util.concurrent.Executor
 */
package com.shamanland.privatescreenshots.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Pair;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.Executor;

public class AsyncBitmapDecoder {
    private static final String LOG_TAG = "AsyncBitmapDecoder";
    private final LazyRef<Executor> executor;
    private final Handler handler = new Handler();
    private final int maxThreads;
    private final LinkedHashMap<File, Pair<Bitmap.Config, WeakReference<Listener>>> pending = new LinkedHashMap();
    private int scheduled;

    public AsyncBitmapDecoder(LazyRef<Executor> lazyRef, int n) {
        this.executor = lazyRef;
        this.maxThreads = n;
    }

    private void scheduleDecode(final File file, final Bitmap.Config config, final Listener listener) {
        this.scheduled = 1 + this.scheduled;
        this.executor.get().execute(new Runnable(){

            public void run() {
                AsyncBitmapDecoder.this.performDecode(file, config, listener);
            }
        });
    }

    public void cancel(File file) {
        if (file != null) {
            this.pending.remove((Object)file);
        }
    }

    public void decode(File file, Bitmap.Config config, Listener listener) {
        if (this.scheduled < this.maxThreads) {
            this.scheduleDecode(file, config, listener);
            return;
        }
        this.pending.put((Object)file, (Object)new Pair((Object)config, (Object)new WeakReference((Object)listener)));
    }

    protected void notifyDecoded(Listener listener, File file, Bitmap bitmap) {
        this.scheduled = -1 + this.scheduled;
        if (this.scheduled >= 0) {
            listener.onDecodeFinished(file, bitmap);
            this.processPending();
            return;
        }
        throw new IllegalStateException();
    }

    protected void performDecode(final File file, Bitmap.Config config, final Listener listener) {
        Trace trace = FirebasePerformance.startTrace((String)"454d7d46");
        final Bitmap[] arrbitmap = new Bitmap[]{null};
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = config;
            arrbitmap[0] = BitmapFactory.decodeFile((String)file.getAbsolutePath(), (BitmapFactory.Options)options);
        }
        catch (Exception exception) {
            Crane.report(exception);
        }
        this.handler.post(new Runnable(){

            public void run() {
                AsyncBitmapDecoder.this.notifyDecoded(listener, file, arrbitmap[0]);
            }
        });
        trace.stop();
    }

    protected void processPending() {
        if (this.pending.size() > 0) {
            File file = (File)this.pending.keySet().iterator().next();
            Pair pair = (Pair)this.pending.remove((Object)file);
            Listener listener = (Listener)((WeakReference)pair.second).get();
            if (listener != null) {
                this.scheduleDecode(file, (Bitmap.Config)pair.first, listener);
            }
        }
    }

    public static interface Listener {
        public void onDecodeFinished(File var1, Bitmap var2);
    }

}

