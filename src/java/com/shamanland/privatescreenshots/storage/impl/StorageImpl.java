/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.net.Uri
 *  android.os.AsyncTask
 *  android.os.Environment
 *  android.support.v4.content.FileProvider
 *  com.google.firebase.perf.FirebasePerformance
 *  com.google.firebase.perf.metrics.Trace
 *  java.io.File
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Void
 *  java.text.SimpleDateFormat
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Calendar
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.Date
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 */
package com.shamanland.privatescreenshots.storage.impl;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.shamanland.analytics.Analytics;
import com.shamanland.analytics.AnalyticsProperty;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.mover.Mover;
import com.shamanland.privatescreenshots.security.Protector;
import com.shamanland.privatescreenshots.security.SessionValidator;
import com.shamanland.privatescreenshots.storage.Storage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class StorageImpl
implements Storage {
    private static final int[] mileStones = new int[]{-1, 9, 19, 49, 99, 199, 499, 999, 1999, 2999, 3999, 4999, 5999, 6999, 7999, 8999, 9999};
    private final LazyRef<Analytics> analytics;
    private final Context context;
    private File files;
    private final List<Storage.Listener> listeners;
    private boolean moving;
    private final LazyRef<Protector> protector;
    private final LazyRef<SessionValidator> sessionValidator;
    private File thumbs;

    public StorageImpl(Context context, LazyRef<Analytics> lazyRef, LazyRef<SessionValidator> lazyRef2, LazyRef<Protector> lazyRef3) {
        this.context = context;
        this.analytics = lazyRef;
        this.sessionValidator = lazyRef2;
        this.protector = lazyRef3;
        this.listeners = new ArrayList(2);
    }

    private String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US).format(Calendar.getInstance().getTime());
    }

    private void remove(File file, boolean bl) {
        this.getThumb(file).delete();
        file.delete();
        if (bl) {
            this.notifyFileRemoved(file);
        }
    }

    private void trackFilesCount(int n) {
        int n2 = Arrays.binarySearch((int[])mileStones, (int)n);
        if (n2 < 0) {
            n2 = -(n2 + 1);
        }
        if (n2 < 1) {
            return;
        }
        int n3 = 1 + mileStones[n2 - 1];
        this.analytics.get().setProperty(AnalyticsProperty.PROPERTY_13, "files_at_least", String.valueOf((int)n3));
    }

    @Override
    public void addListener(Storage.Listener listener) {
        this.listeners.add((Object)listener);
    }

    @Override
    public List<File> getFiles() {
        return this.getFiles(null);
    }

    @Override
    public List<File> getFiles(boolean[] arrbl) {
        Trace trace = FirebasePerformance.startTrace((String)"9a09e5a6");
        if (this.isContentLocked()) {
            if (arrbl != null && arrbl.length > 0) {
                arrbl[0] = true;
            }
            List list = Collections.emptyList();
            trace.stop();
            return list;
        }
        Object[] arrobject = this.getFilesDir().listFiles();
        if (arrobject == null) {
            List list = Collections.emptyList();
            trace.stop();
            return list;
        }
        Arrays.sort((Object[])arrobject, (Comparator)new Comparator<File>(){

            public int compare(File file, File file2) {
                return file2.getName().compareTo(file.getName());
            }
        });
        this.trackFilesCount(arrobject.length);
        List list = Arrays.asList((Object[])arrobject);
        trace.stop();
        return list;
    }

    @Override
    public int getFilesCount() {
        if (this.isContentLocked()) {
            return 0;
        }
        return this.getFilesCountRaw();
    }

    @Override
    public int getFilesCountRaw() {
        File[] arrfile = this.getFilesDir().listFiles();
        if (arrfile != null) {
            return arrfile.length;
        }
        return 0;
    }

    @Override
    public File getFilesDir() {
        if (this.files == null) {
            this.files = new File(this.context.getFilesDir(), "files");
            this.files.mkdirs();
        }
        return this.files;
    }

    @Override
    public long getFilesSize() {
        Iterator iterator = this.getFiles().iterator();
        long l = 0L;
        while (iterator.hasNext()) {
            File file = (File)iterator.next();
            l = l + file.length() + this.getThumb(file).length();
        }
        return l;
    }

    @Override
    public File getThumb(File file) {
        return new File(this.getThumbsDir(), file.getName());
    }

    @Override
    public List<File> getThumbs(List<File> list) {
        ArrayList arrayList = new ArrayList(list.size());
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            arrayList.add((Object)this.getThumb((File)iterator.next()));
        }
        return arrayList;
    }

    @Override
    public File getThumbsDir() {
        if (this.thumbs == null) {
            this.thumbs = new File(this.context.getFilesDir(), "thumbs");
            this.thumbs.mkdirs();
        }
        return this.thumbs;
    }

    @Override
    public Uri getUri(File file) {
        Context context = this.context;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.context.getPackageName());
        stringBuilder.append(".files");
        return FileProvider.getUriForFile((Context)context, (String)stringBuilder.toString(), (File)file);
    }

    @Override
    public boolean isContentLocked() {
        return !this.sessionValidator.get().isSessionValid() && this.protector.get().isLocked();
    }

    @Override
    public boolean isMoving() {
        return this.moving;
    }

    @Override
    public boolean moveToGallery(Collection<File> collection) {
        File file = new File(Environment.getExternalStoragePublicDirectory((String)Environment.DIRECTORY_PICTURES), "Screenshots");
        if (!file.exists() && !file.mkdirs()) {
            return false;
        }
        Mover mover = new Mover(this.context, this, this.analytics.get(), file, collection, new Mover.Listener(){

            @Override
            public void onEndMoving(int n, int n2, File file) {
                StorageImpl.this.notifyEndMoving(n, n2, file);
            }
        });
        mover.execute((Object[])new Void[0]);
        this.notifyStartMoving();
        return true;
    }

    protected void notifyEndMoving(int n, int n2, File file) {
        this.moving = false;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            ((Storage.Listener)iterator.next()).onEndMoving(n, n2, file);
        }
    }

    protected void notifyFileAdded(File file) {
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            ((Storage.Listener)iterator.next()).onFileAdded(file);
        }
    }

    protected void notifyFileRemoved(File file) {
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            ((Storage.Listener)iterator.next()).onFileRemoved(file);
        }
    }

    protected void notifyFilesRemoved(Collection<File> collection) {
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            ((Storage.Listener)iterator.next()).onFilesRemoved(collection);
        }
    }

    protected void notifyStartMoving() {
        this.moving = true;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            ((Storage.Listener)iterator.next()).onStartMoving();
        }
    }

    @Override
    public void remove(File file) {
        this.remove(file, true);
    }

    @Override
    public void remove(Collection<File> collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.remove((File)iterator.next(), false);
        }
        this.notifyFilesRemoved(collection);
    }

    @Override
    public void removeListener(Storage.Listener listener) {
        this.listeners.remove((Object)listener);
    }

    @Override
    public boolean saveNew(File file, File file2) {
        String string2 = this.getTime();
        File file3 = new File(this.getFilesDir(), String.format((Locale)Locale.US, (String)"%s.png", (Object[])new Object[]{string2}));
        int n = 0;
        while (file3.exists()) {
            File file4 = this.getFilesDir();
            Locale locale = Locale.US;
            Object[] arrobject = new Object[]{string2, ++n};
            file3 = new File(file4, String.format((Locale)locale, (String)"%s (%d).png", (Object[])arrobject));
        }
        if (file.renameTo(file3) & file2.renameTo(this.getThumb(file3))) {
            this.notifyFileAdded(file3);
            return true;
        }
        this.remove(file3, false);
        file.delete();
        file2.delete();
        return false;
    }

}

