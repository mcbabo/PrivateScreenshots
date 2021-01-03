/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.arch.lifecycle.LiveData
 *  android.arch.lifecycle.MutableLiveData
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.graphics.Point
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 */
package com.shamanland.privatescreenshots.settings;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.graphics.Point;

public class SettingsManager {
    private boolean adFreePurchased = true;
    private MutableLiveData<Float> buttonAlphaObservable;
    private final SharedPreferences prefs;

    public SettingsManager(SharedPreferences sharedPreferences) {
        this.prefs = sharedPreferences;
    }

    private SharedPreferences getPrefs() {
        return this.prefs;
    }

    public long getAppInstalled() {
        SharedPreferences sharedPreferences = this.getPrefs();
        long l = sharedPreferences.getLong("1ad98142", -1L);
        if (l < 0L) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            long l2 = System.currentTimeMillis();
            editor.putLong("1ad98142", l2).apply();
            l = l2;
        }
        return l;
    }

    public float getButtonAlpha() {
        return Math.min((float)Math.max((float)0.2f, (float)this.getPrefs().getFloat("0924ceaa", 0.75f)), (float)1.0f);
    }

    public LiveData<Float> getButtonAlphaObservable() {
        if (this.buttonAlphaObservable == null) {
            this.buttonAlphaObservable = new MutableLiveData();
            this.buttonAlphaObservable.setValue((Object)Float.valueOf((float)this.getButtonAlpha()));
        }
        return this.buttonAlphaObservable;
    }

    public Point getButtonPosition(int n) {
        SharedPreferences sharedPreferences = this.getPrefs();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1ec338ba-");
        stringBuilder.append(n);
        int n2 = sharedPreferences.getInt(stringBuilder.toString(), -1);
        SharedPreferences sharedPreferences2 = this.getPrefs();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("aeb6ba58-");
        stringBuilder2.append(n);
        int n3 = sharedPreferences2.getInt(stringBuilder2.toString(), -1);
        if (n2 >= 0 && n3 >= 0) {
            return new Point(n2, n3);
        }
        return null;
    }

    public int getShakeShot() {
        return this.getPrefs().getInt("dcb8c3e7", 0);
    }

    public boolean isAdFreeAvailable() {
        return this.isAdFreePurchased() || this.isAdFreeTrialPeriod();
        {
        }
    }

    public boolean isAdFreePurchased() {
        return this.adFreePurchased;
    }

    protected boolean isAdFreeTrialPeriod() {
        long l = this.getAppInstalled();
        return System.currentTimeMillis() - l < 172800000L;
    }

    public boolean isChooseViewerAvailable() {
        return this.getPrefs().getBoolean("b605fa3e", false);
    }

    public boolean isPrivatePhotos() {
        return this.getPrefs().getBoolean("42a56c7b", false);
    }

    public boolean isUseExternalViewer() {
        return this.getPrefs().getBoolean("c6d89ac8", false);
    }

    public boolean reset() {
        return false | this.setUseExternalViewer(false) | this.setShakeShot(0) | this.setPrivatePhotos(false) | this.setButtonAlpha(0.75f);
    }

    public boolean setAdFreePurchased(boolean bl) {
        if (this.adFreePurchased != bl) {
            this.adFreePurchased = bl;
            return true;
        }
        return false;
    }

    public boolean setButtonAlpha(float f) {
        float f2 = this.getButtonAlpha();
        this.getPrefs().edit().putFloat("0924ceaa", f).apply();
        MutableLiveData<Float> mutableLiveData = this.buttonAlphaObservable;
        if (mutableLiveData != null) {
            mutableLiveData.setValue((Object)Float.valueOf((float)this.getButtonAlpha()));
        }
        return f2 != f;
    }

    public void setButtonPosition(int n, int n2, int n3) {
        SharedPreferences.Editor editor = this.getPrefs().edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1ec338ba-");
        stringBuilder.append(n);
        SharedPreferences.Editor editor2 = editor.putInt(stringBuilder.toString(), n2);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("aeb6ba58-");
        stringBuilder2.append(n);
        editor2.putInt(stringBuilder2.toString(), n3).apply();
    }

    public void setChooseViewerAvailable(boolean bl) {
        this.getPrefs().edit().putBoolean("b605fa3e", bl).apply();
    }

    public boolean setPrivatePhotos(boolean bl) {
        boolean bl2 = this.isPrivatePhotos();
        this.getPrefs().edit().putBoolean("42a56c7b", bl).apply();
        return bl2 != bl;
    }

    public boolean setShakeShot(int n) {
        boolean bl = this.getShakeShot() != n;
        this.getPrefs().edit().putInt("dcb8c3e7", n).apply();
        return bl;
    }

    public boolean setUseExternalViewer(boolean bl) {
        boolean bl2 = this.isUseExternalViewer() != bl;
        this.getPrefs().edit().putBoolean("c6d89ac8", bl).apply();
        return bl2;
    }
}

