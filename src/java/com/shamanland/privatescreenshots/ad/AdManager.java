/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.SystemClock
 *  com.google.android.gms.ads.MobileAds
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.ad;

import android.content.Context;
import android.os.SystemClock;
import com.google.android.gms.ads.MobileAds;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.BuildConfig;
import com.shamanland.privatescreenshots.ad.InterstitialWrapper;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.utils.NetworkUtils;
import com.shamanland.remoteconfig.RemoteConfig;

public class AdManager {
    private final LazyRef<Analytics> analytics;
    private final Context context;
    private final long initAt;
    private boolean initialized;
    private final LazyRef<RemoteConfig> remoteConfig;
    private final LazyRef<SettingsManager> settingsManager;

    public AdManager(Context context, LazyRef<Analytics> lazyRef, LazyRef<RemoteConfig> lazyRef2, LazyRef<SettingsManager> lazyRef3) {
        this.context = context;
        this.analytics = lazyRef;
        this.remoteConfig = lazyRef2;
        this.settingsManager = lazyRef3;
        this.initAt = 5000L + SystemClock.uptimeMillis();
    }

    private void init() {
        if (!this.initialized) {
            MobileAds.initialize((Context)this.context, (String)BuildConfig.ADMOB_APP_ID);
            this.initialized = true;
        }
    }

    public boolean isBannerEnabled() {
        if (this.settingsManager.get().isAdFreeAvailable()) {
            return false;
        }
        if (!NetworkUtils.isConnected(this.context)) {
            return false;
        }
        if (!this.remoteConfig.get().getBoolean("ad_banner_enabled", false)) {
            return false;
        }
        if (SystemClock.uptimeMillis() < this.initAt) {
            return false;
        }
        this.init();
        return true;
    }

    public boolean shouldShowInterstitial(InterstitialWrapper interstitialWrapper, int n, int n2, int n3) {
        if (this.settingsManager.get().isAdFreeAvailable()) {
            return false;
        }
        double d = this.remoteConfig.get().getDouble("interstitial_multiplier_param", 1.0);
        if (d < 1.0) {
            d = 1.0;
        }
        double d2 = 1.0 + (double)n / d;
        int n4 = (int)(d2 * (double)((int)this.remoteConfig.get().getLong("interstitial_depth", 3L)));
        if (Math.abs((int)(n2 - Math.max((int)0, (int)n3))) + interstitialWrapper.getSkipped() <= n4) {
            interstitialWrapper.incSkipped();
            this.analytics.get().logEvent("ad_interstitial_skipped", "depth");
            return false;
        }
        long l = (long)(d2 * (double)this.remoteConfig.get().getLong("interstitial_delay", 5000L));
        if (SystemClock.uptimeMillis() - interstitialWrapper.getLastShownUptimeMillis() <= l) {
            interstitialWrapper.incSkipped();
            this.analytics.get().logEvent("ad_interstitial_skipped", "delay");
            return false;
        }
        if (n >= (int)this.remoteConfig.get().getLong("interstitial_max_per_screen", 5L)) {
            interstitialWrapper.incSkipped();
            this.analytics.get().logEvent("ad_interstitial_skipped", "max_per_screen");
            return false;
        }
        double d3 = this.remoteConfig.get().getDouble("interstitial_chance", 0.0);
        if (Math.random() >= d3) {
            interstitialWrapper.incSkipped();
            this.analytics.get().logEvent("ad_interstitial_skipped", "chance");
            return false;
        }
        if (SystemClock.uptimeMillis() < this.initAt) {
            return false;
        }
        this.init();
        return true;
    }
}

