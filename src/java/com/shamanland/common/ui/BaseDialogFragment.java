/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.support.v4.app.DialogFragment
 *  android.support.v4.app.Fragment
 *  android.support.v4.app.FragmentActivity
 *  java.lang.Class
 *  java.lang.Object
 */
package com.shamanland.common.ui;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class BaseDialogFragment
extends DialogFragment {
    public <T> T castOwner(Class<T> class_) {
        Fragment fragment = this.getParentFragment();
        if (class_.isInstance((Object)fragment)) {
            return (T)class_.cast((Object)fragment);
        }
        FragmentActivity fragmentActivity = this.getActivity();
        if (class_.isInstance((Object)fragmentActivity)) {
            return (T)class_.cast((Object)fragmentActivity);
        }
        return null;
    }
}

