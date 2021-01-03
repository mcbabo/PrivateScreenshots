/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.System
 *  java.util.ArrayDeque
 */
package com.shamanland.common.utils;

import java.util.ArrayDeque;

public class FrequencyCounter {
    private static ArrayDeque<Long> timings = new ArrayDeque();

    public static void clear() {
        timings.clear();
    }

    public static double get() {
        timings.addLast((Object)System.currentTimeMillis());
        if (timings.size() > 10) {
            timings.removeFirst();
            return (double)(1000 * timings.size()) / (double)((Long)timings.peekLast() - (Long)timings.peekFirst());
        }
        return 0.0;
    }
}

