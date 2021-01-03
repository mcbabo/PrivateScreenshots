/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.SystemClock
 *  com.google.android.gms.ads.AdListener
 *  com.google.android.gms.ads.AdRequest
 *  com.google.android.gms.ads.InterstitialAd
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.ad;

import android.content.Context;
import android.os.SystemClock;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.shamanland.dev.Dev;

public class InterstitialWrapper
extends AdListener {
    private static final String LOG_TAG = "InterstitialWrapper";
    private final InterstitialAd ad;
    private long lastShownUptimeMillis;
    private AdListener listener;
    private final AdRequest request;
    private int skipped;

    public InterstitialWrapper(Context context, String string2, AdRequest adRequest) {
        InterstitialAd interstitialAd = this.ad = new InterstitialAd(context.getApplicationContext());
        if (Dev.isDebug(context)) {
            string2 = "ca-app-pub-3940256099942544/1033173712";
        }
        interstitialAd.setAdUnitId(string2);
        this.ad.setAdListener((AdListener)this);
        this.request = adRequest;
    }

    public long getLastShownUptimeMillis() {
        return this.lastShownUptimeMillis;
    }

    public int getSkipped() {
        return this.skipped;
    }

    public void incSkipped() {
        this.skipped = 1 + this.skipped;
    }

    public boolean isLoaded() {
        return this.ad.isLoaded();
    }

    public void loadAd() {
        if (!this.ad.isLoaded() && !this.ad.isLoading()) {
            this.ad.loadAd(this.request);
        }
    }

    public void onAdClicked() {
        AdListener adListener = this.listener;
        if (adListener != null) {
            adListener.onAdClicked();
        }
    }

    public void onAdClosed() {
        this.lastShownUptimeMillis = SystemClock.uptimeMillis();
        this.skipped = 0;
        AdListener adListener = this.listener;
        if (adListener != null) {
            adListener.onAdClosed();
        }
    }

    public void onAdFailedToLoad(int n) {
        AdListener adListener = this.listener;
        if (adListener != null) {
            adListener.onAdFailedToLoad(n);
        }
    }

    public void onAdImpression() {
        AdListener adListener = this.listener;
        if (adListener != null) {
            adListener.onAdImpression();
        }
    }

    public void onAdLeftApplication() {
        AdListener adListener = this.listener;
        if (adListener != null) {
            adListener.onAdLeftApplication();
        }
    }

    public void onAdLoaded() {
        AdListener adListener = this.listener;
        if (adListener != null) {
            adListener.onAdLoaded();
        }
    }

    public void onAdOpened() {
        AdListener adListener = this.listener;
        if (adListener != null) {
            adListener.onAdOpened();
        }
    }

    public void setAdListener(AdListener adListener) {
        this.listener = adListener;
    }

    public void show() {
        if (this.ad.isLoaded()) {
            this.lastShownUptimeMillis = SystemClock.uptimeMillis();
            this.skipped = 0;
            this.ad.show();
        }
    }
}

