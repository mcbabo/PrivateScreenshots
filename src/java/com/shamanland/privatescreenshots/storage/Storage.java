/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.net.Uri
 *  java.io.File
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.List
 */
package com.shamanland.privatescreenshots.storage;

import android.net.Uri;
import java.io.File;
import java.util.Collection;
import java.util.List;

public interface Storage {
    public void addListener(Listener var1);

    public List<File> getFiles();

    public List<File> getFiles(boolean[] var1);

    public int getFilesCount();

    public int getFilesCountRaw();

    public File getFilesDir();

    public long getFilesSize();

    public File getThumb(File var1);

    public List<File> getThumbs(List<File> var1);

    public File getThumbsDir();

    public Uri getUri(File var1);

    public boolean isContentLocked();

    public boolean isMoving();

    public boolean moveToGallery(Collection<File> var1);

    public void remove(File var1);

    public void remove(Collection<File> var1);

    public void removeListener(Listener var1);

    public boolean saveNew(File var1, File var2);

    public static interface Listener {
        public void onEndMoving(int var1, int var2, File var3);

        public void onFileAdded(File var1);

        public void onFileRemoved(File var1);

        public void onFilesRemoved(Collection<File> var1);

        public void onStartMoving();
    }

}

