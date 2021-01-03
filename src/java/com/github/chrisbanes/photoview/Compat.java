/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.TargetApi
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.view.View
 *  java.lang.Object
 *  java.lang.Runnable
 */
package com.github.chrisbanes.photoview;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

class Compat {
    public static void postOnAnimation(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            Compat.postOnAnimationJellyBean(view, runnable);
            return;
        }
        view.postDelayed(runnable, 16L);
    }

    @TargetApi(value=16)
    private static void postOnAnimationJellyBean(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }
}

