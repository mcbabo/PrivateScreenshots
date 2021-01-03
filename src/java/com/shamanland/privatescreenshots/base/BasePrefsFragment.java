/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.support.v7.preference.PreferenceFragmentCompat
 *  java.lang.IllegalStateException
 */
package com.shamanland.privatescreenshots.base;

import android.content.Context;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;

public abstract class BasePrefsFragment
extends PreferenceFragmentCompat {
    protected ComponentLocator getComponentLocator() {
        return ComponentLocator.getInstance(this.getContextNonNull());
    }

    protected Context getContextNonNull() {
        Context context = this.getContext();
        if (context != null) {
            return context;
        }
        throw new IllegalStateException();
    }
}

