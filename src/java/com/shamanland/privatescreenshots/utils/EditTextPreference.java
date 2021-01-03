/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.support.v7.preference.EditTextPreference
 *  android.support.v7.preference.Preference
 *  android.support.v7.preference.Preference$OnPreferenceClickListener
 *  android.util.AttributeSet
 */
package com.shamanland.privatescreenshots.utils;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public class EditTextPreference
extends android.support.v7.preference.EditTextPreference {
    private Preference.OnPreferenceClickListener listener;

    public EditTextPreference(Context context) {
        super(context);
    }

    public EditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public EditTextPreference(Context context, AttributeSet attributeSet, int n) {
        super(context, attributeSet, n);
    }

    public EditTextPreference(Context context, AttributeSet attributeSet, int n, int n2) {
        super(context, attributeSet, n, n2);
    }

    protected void onClick() {
        Preference.OnPreferenceClickListener onPreferenceClickListener = this.listener;
        if (onPreferenceClickListener == null || !onPreferenceClickListener.onPreferenceClick((Preference)this)) {
            super.onClick();
        }
    }

    public void setOverriddenOnPreferenceClickListener(Preference.OnPreferenceClickListener onPreferenceClickListener) {
        this.listener = onPreferenceClickListener;
    }
}

