/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.content.Context
 *  android.content.res.Resources
 *  android.util.DisplayMetrics
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.widget.FrameLayout
 *  android.widget.FrameLayout$LayoutParams
 *  android.widget.TextView
 *  com.google.android.gms.ads.AdRequest
 *  com.google.android.gms.ads.AdSize
 *  com.google.android.gms.ads.AdView
 *  java.lang.CharSequence
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package com.shamanland.privatescreenshots.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.shamanland.dev.Dev;
import com.shamanland.privatescreenshots.BuildConfig;
import com.shamanland.privatescreenshots.utils.AdHelper;

public class BannerView
extends FrameLayout {
    protected static final String LOG_TAG = "BannerView";
    private int adIndex;
    protected TextView debugTextView;
    protected boolean initialized;

    public BannerView(Context context) {
        super(context);
    }

    private void initAdMob(int n, int n2) {
        this.initialized = true;
        if (n2 < 32) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        layoutParams.gravity = 17;
        AdView adView = new AdView(this.getContext());
        this.addView((View)adView, (ViewGroup.LayoutParams)layoutParams);
        if (Dev.isDebug(this.getContext())) {
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        } else {
            String string2 = this.adIndex == 1 ? BuildConfig.BANNER_1_AD_UNIT_ID : BuildConfig.BANNER_2_AD_UNIT_ID;
            adView.setAdUnitId(string2);
        }
        adView.setAdSize(new AdSize(n, n2));
        adView.loadAd(AdHelper.buildAdRequest());
    }

    @SuppressLint(value={"SetTextI18n"})
    protected void onSizeChanged(int n, int n2, int n3, int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (this.initialized) {
            return;
        }
        int n5 = this.getResources().getDisplayMetrics().densityDpi;
        int n6 = n * 160 / n5;
        int n7 = n2 * 160 / n5;
        if (n6 > 0 && n7 > 0) {
            this.initAdMob(n6, n7);
        }
        if (Dev.isDebug(this.getContext())) {
            if (this.debugTextView == null) {
                TextView textView;
                this.setBackgroundColor(1145324612);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
                layoutParams.gravity = 17;
                this.debugTextView = textView = new TextView(this.getContext());
                this.addView((View)textView, (ViewGroup.LayoutParams)layoutParams);
            }
            TextView textView = this.debugTextView;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(n6);
            stringBuilder.append(" x ");
            stringBuilder.append(n7);
            textView.setText((CharSequence)stringBuilder.toString());
            this.debugTextView.bringToFront();
        }
    }

    public void setAdIndex(int n) {
        this.adIndex = n;
    }
}

