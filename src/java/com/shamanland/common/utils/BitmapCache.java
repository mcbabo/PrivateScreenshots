/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$Config
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.ref.SoftReference
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 */
package com.shamanland.common.utils;

import android.graphics.Bitmap;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BitmapCache {
    private static final String LOG_TAG = "BitmapCache";
    private static final Map<BitmapKey, List<SoftReference<Bitmap>>> cache = new HashMap();

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Bitmap allocate(BitmapKey bitmapKey) {
        List<SoftReference<Bitmap>> list;
        List<SoftReference<Bitmap>> list2 = list = BitmapCache.getOrCreate(bitmapKey);
        synchronized (list2) {
            Bitmap bitmap;
            Iterator iterator = list.iterator();
            do {
                if (!iterator.hasNext()) {
                    return Bitmap.createBitmap((int)bitmapKey.width, (int)bitmapKey.height, (Bitmap.Config)bitmapKey.config);
                }
                bitmap = (Bitmap)((SoftReference)iterator.next()).get();
                iterator.remove();
            } while (bitmap == null);
            return bitmap;
        }
    }

    public static Bitmap createBitmap(int n, int n2, Bitmap.Config config) {
        return BitmapCache.allocate(new BitmapKey(n, n2, config));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<SoftReference<Bitmap>> getOrCreate(BitmapKey bitmapKey) {
        Class<BitmapCache> class_ = BitmapCache.class;
        synchronized (BitmapCache.class) {
            List list = (List)cache.get((Object)bitmapKey);
            if (list == null) {
                list = new ArrayList(2);
                cache.put((Object)bitmapKey, (Object)list);
            }
            // ** MonitorExit[var4_1] (shouldn't be in output)
            return list;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void release(Bitmap bitmap) {
        List<SoftReference<Bitmap>> list;
        List<SoftReference<Bitmap>> list2 = list = BitmapCache.getOrCreate(new BitmapKey(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig()));
        synchronized (list2) {
            list.add((Object)new SoftReference((Object)bitmap));
            return;
        }
    }

    static class BitmapKey {
        final Bitmap.Config config;
        final int height;
        final int width;

        public BitmapKey(int n, int n2, Bitmap.Config config) {
            this.width = n;
            this.height = n2;
            this.config = config;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object != null) {
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                BitmapKey bitmapKey = (BitmapKey)object;
                if (this.width != bitmapKey.width) {
                    return false;
                }
                if (this.height != bitmapKey.height) {
                    return false;
                }
                return this.config == bitmapKey.config;
            }
            return false;
        }

        public int hashCode() {
            return 31 * (31 * this.width + this.height) + this.config.hashCode();
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(BitmapKey.class.getSimpleName());
            stringBuilder.append(": width=");
            stringBuilder.append(this.width);
            stringBuilder.append(", height=");
            stringBuilder.append(this.height);
            stringBuilder.append(", config=");
            stringBuilder.append((Object)this.config);
            return stringBuilder.toString();
        }
    }

}

