/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.SharedPreferences
 *  com.google.firebase.analytics.FirebaseAnalytics
 *  com.google.firebase.remoteconfig.FirebaseRemoteConfig
 *  java.lang.Object
 *  java.lang.String
 *  java.util.concurrent.Executor
 */
package com.shamanland.privatescreenshots.componentlocator;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.common.utils.Utils;
import com.shamanland.dev.Dev;
import com.shamanland.privatescreenshots.ad.AdManager;
import com.shamanland.privatescreenshots.ad.InterstitialHolder;
import com.shamanland.privatescreenshots.overlay.OverlayMonitor;
import com.shamanland.privatescreenshots.security.Protector;
import com.shamanland.privatescreenshots.security.SessionValidator;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.storage.impl.StorageImpl;
import com.shamanland.privatescreenshots.utils.AsyncBitmapDecoder;
import com.shamanland.remoteconfig.RemoteConfig;
import java.util.concurrent.Executor;

public class ComponentLocator {
    private static ComponentLocator instance;
    private final LazyRef<AdManager> adManager;
    private final LazyRef<Analytics> analytics;
    private final LazyRef<AsyncBitmapDecoder> asyncBitmapDecoder = new LazyRef<AsyncBitmapDecoder>(){

        @Override
        protected AsyncBitmapDecoder acquireRef() {
            return new AsyncBitmapDecoder(ComponentLocator.this.getThreadPool(), Utils.getOptimalMaxPoolSize());
        }
    };
    private final LazyRef<InterstitialHolder> interstitialHolder;
    private final LazyRef<OverlayMonitor> overlayMonitor;
    private final LazyRef<Protector> protector;
    private final LazyRef<RemoteConfig> remoteConfig;
    private final LazyRef<SessionValidator> sessionValidator;
    private final LazyRef<SettingsManager> settingsManager;
    private final LazyRef<Storage> storage;
    private final LazyRef<Executor> threadPool = new LazyRef<Executor>(){

        @Override
        protected Executor acquireRef() {
            return Utils.createThreadPool(0, Utils.getOptimalMaxPoolSize(), "af017197", 30L);
        }
    };

    private ComponentLocator(final Context context) {
        this.adManager = new LazyRef<AdManager>(){

            @Override
            protected AdManager acquireRef() {
                return new AdManager(context, ComponentLocator.this.getAnalytics(), ComponentLocator.this.getRemoteConfig(), ComponentLocator.this.getSettingsManager());
            }
        };
        this.settingsManager = new LazyRef<SettingsManager>(){

            @Override
            protected SettingsManager acquireRef() {
                return new SettingsManager(context.getSharedPreferences("92fcec9b", 0));
            }
        };
        this.storage = new LazyRef<Storage>(){

            @Override
            protected Storage acquireRef() {
                return new StorageImpl(context, ComponentLocator.this.getAnalytics(), ComponentLocator.this.getSessionValidator(), ComponentLocator.this.getProtector());
            }
        };
        this.protector = new LazyRef<Protector>(){

            @Override
            protected Protector acquireRef() {
                return new Protector(context, ComponentLocator.this.getAnalytics());
            }
        };
        this.sessionValidator = new LazyRef<SessionValidator>(){

            @Override
            protected SessionValidator acquireRef() {
                return new SessionValidator();
            }
        };
        this.analytics = new LazyRef<Analytics>(){

            @Override
            protected Analytics acquireRef() {
                return new Analytics(FirebaseAnalytics.getInstance((Context)context), context);
            }
        };
        this.remoteConfig = new LazyRef<RemoteConfig>(){

            @Override
            protected RemoteConfig acquireRef() {
                String string2 = Utils.getManifestMetadata(context, "com.shamanland.metadata.rc.prefix", null);
                if (string2 == null) {
                    string2 = "";
                }
                return new RemoteConfig(FirebaseRemoteConfig.getInstance(), string2, Dev.isDebug(context));
            }
        };
        this.interstitialHolder = new LazyRef<InterstitialHolder>(){

            @Override
            protected InterstitialHolder acquireRef() {
                return new InterstitialHolder();
            }
        };
        this.overlayMonitor = new LazyRef<OverlayMonitor>(){

            @Override
            protected OverlayMonitor acquireRef() {
                return new OverlayMonitor(context);
            }
        };
    }

    public static ComponentLocator getInstance(Context context) {
        if (instance == null) {
            instance = new ComponentLocator(context.getApplicationContext());
        }
        return instance;
    }

    public LazyRef<AdManager> getAdManager() {
        return this.adManager;
    }

    public LazyRef<Analytics> getAnalytics() {
        return this.analytics;
    }

    public LazyRef<AsyncBitmapDecoder> getAsyncBitmapDecoder() {
        return this.asyncBitmapDecoder;
    }

    public LazyRef<InterstitialHolder> getInterstitialHolder() {
        return this.interstitialHolder;
    }

    public LazyRef<OverlayMonitor> getOverlayMonitor() {
        return this.overlayMonitor;
    }

    public LazyRef<Protector> getProtector() {
        return this.protector;
    }

    public LazyRef<RemoteConfig> getRemoteConfig() {
        return this.remoteConfig;
    }

    public LazyRef<SessionValidator> getSessionValidator() {
        return this.sessionValidator;
    }

    public LazyRef<SettingsManager> getSettingsManager() {
        return this.settingsManager;
    }

    public LazyRef<Storage> getStorage() {
        return this.storage;
    }

    public LazyRef<Executor> getThreadPool() {
        return this.threadPool;
    }

}

