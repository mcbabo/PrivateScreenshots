/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 *  com.google.android.gms.tasks.OnCompleteListener
 *  com.google.android.gms.tasks.Task
 *  com.google.firebase.remoteconfig.FirebaseRemoteConfig
 *  com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
 *  com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings$Builder
 *  com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Void
 *  java.util.HashMap
 *  java.util.Map
 */
package com.shamanland.remoteconfig;

import android.text.TextUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import java.util.HashMap;
import java.util.Map;

public class RemoteConfig {
    protected final FirebaseRemoteConfig config;
    private final Map<String, Object> defaults;
    private final String keyPrefix;

    public RemoteConfig(final FirebaseRemoteConfig firebaseRemoteConfig, String string2, boolean bl) {
        this.config = firebaseRemoteConfig;
        this.keyPrefix = string2;
        this.defaults = new HashMap();
        if (bl) {
            firebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build());
        }
        long l = bl ? 0L : 86400L;
        firebaseRemoteConfig.fetch(l).addOnCompleteListener((OnCompleteListener)new OnCompleteListener<Void>(){

            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseRemoteConfig.activateFetched();
                }
            }
        });
    }

    private FirebaseRemoteConfigValue updateDefaultsAndGet(String string2, Object object) {
        if (!TextUtils.isEmpty((CharSequence)this.keyPrefix)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.keyPrefix);
            stringBuilder.append(string2);
            string2 = stringBuilder.toString();
        }
        if (object != null) {
            this.defaults.put((Object)string2, object);
            this.config.setDefaults(this.defaults);
        }
        return this.config.getValue(string2);
    }

    public boolean getBoolean(String string2, boolean bl) {
        FirebaseRemoteConfigValue firebaseRemoteConfigValue = this.updateDefaultsAndGet(string2, bl);
        if (firebaseRemoteConfigValue.getSource() == 0) {
            return bl;
        }
        try {
            boolean bl2 = firebaseRemoteConfigValue.asBoolean();
            return bl2;
        }
        catch (Exception exception) {
            return bl;
        }
    }

    public double getDouble(String string2, double d) {
        FirebaseRemoteConfigValue firebaseRemoteConfigValue = this.updateDefaultsAndGet(string2, d);
        if (firebaseRemoteConfigValue.getSource() == 0) {
            return d;
        }
        try {
            double d2 = firebaseRemoteConfigValue.asDouble();
            return d2;
        }
        catch (Exception exception) {
            return d;
        }
    }

    public long getLong(String string2, long l) {
        FirebaseRemoteConfigValue firebaseRemoteConfigValue = this.updateDefaultsAndGet(string2, l);
        if (firebaseRemoteConfigValue.getSource() == 0) {
            return l;
        }
        try {
            long l2 = firebaseRemoteConfigValue.asLong();
            return l2;
        }
        catch (Exception exception) {
            return l;
        }
    }

    public String getString(String string2, String string3) {
        FirebaseRemoteConfigValue firebaseRemoteConfigValue = this.updateDefaultsAndGet(string2, string3);
        if (firebaseRemoteConfigValue.getSource() == 0) {
            return string3;
        }
        try {
            String string4 = firebaseRemoteConfigValue.asString();
            return string4;
        }
        catch (Exception exception) {
            return string3;
        }
    }

}

