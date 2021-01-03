/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Resources
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.provider.Settings
 *  android.util.AttributeSet
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.widget.LinearLayout
 *  android.widget.TextView
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.main;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Tutorial
extends LinearLayout {
    public Tutorial(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void addView(LayoutInflater layoutInflater, int n) {
        View view = layoutInflater.inflate(2131492953, (ViewGroup)this, false);
        this.addView(view);
        TextView textView = (TextView)view.findViewById(2131296401);
        Resources resources = this.getResources();
        Object[] arrobject = new Object[]{this.getChildCount()};
        textView.setText((CharSequence)resources.getString(2131689596, arrobject));
        ((TextView)view.findViewById(2131296471)).setText(n);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater layoutInflater = LayoutInflater.from((Context)this.getContext());
        this.addView(layoutInflater, 2131689699);
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays((Context)this.getContext())) {
            this.addView(layoutInflater, 2131689697);
        }
        this.addView(layoutInflater, 2131689696);
        this.addView(layoutInflater, 2131689698);
        this.addView(layoutInflater, 2131689700);
    }
}

