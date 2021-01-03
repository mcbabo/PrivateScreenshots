/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.util.SparseArray
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.ref.SoftReference
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 */
package com.shamanland.common.utils;

import android.util.SparseArray;
import java.lang.ref.SoftReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cache {
    private static final String LOG_TAG = "Cache";
    private static final SparseArray<List<SoftReference<ByteBuffer>>> cache = new SparseArray();

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static ByteBuffer allocate(int n) {
        List<SoftReference<ByteBuffer>> list;
        List<SoftReference<ByteBuffer>> list2 = list = Cache.getOrCreate(n);
        synchronized (list2) {
            ByteBuffer byteBuffer;
            Iterator iterator = list.iterator();
            do {
                if (!iterator.hasNext()) {
                    return ByteBuffer.allocate((int)n);
                }
                byteBuffer = (ByteBuffer)((SoftReference)iterator.next()).get();
                if (byteBuffer == null) {
                    iterator.remove();
                    continue;
                }
                if (byteBuffer.capacity() == n) break;
            } while (true);
            iterator.remove();
            return byteBuffer;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<SoftReference<ByteBuffer>> getOrCreate(int n) {
        Class<Cache> class_ = Cache.class;
        synchronized (Cache.class) {
            List list = (List)cache.get(n);
            if (list == null) {
                list = new ArrayList(2);
                cache.put(n, (Object)list);
            }
            // ** MonitorExit[var3_1] (shouldn't be in output)
            return list;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void release(ByteBuffer byteBuffer) {
        List<SoftReference<ByteBuffer>> list;
        List<SoftReference<ByteBuffer>> list2 = list = Cache.getOrCreate(byteBuffer.capacity());
        synchronized (list2) {
            byteBuffer.rewind();
            list.add((Object)new SoftReference((Object)byteBuffer));
            return;
        }
    }
}

