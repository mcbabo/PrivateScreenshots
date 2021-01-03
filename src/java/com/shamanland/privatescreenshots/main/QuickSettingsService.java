/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.TargetApi
 *  android.arch.lifecycle.Lifecycle
 *  android.arch.lifecycle.Lifecycle$State
 *  android.arch.lifecycle.LifecycleOwner
 *  android.arch.lifecycle.LifecycleRegistry
 *  android.arch.lifecycle.LiveData
 *  android.arch.lifecycle.Observer
 *  android.content.Context
 *  android.content.Intent
 *  android.service.quicksettings.Tile
 *  android.service.quicksettings.TileService
 *  android.view.View
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.main;

import android.annotation.TargetApi;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.View;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.main.Launcher;
import com.shamanland.privatescreenshots.overlay.OverlayMonitor;
import com.shamanland.toaster.Toaster;

@TargetApi(value=24)
public class QuickSettingsService
extends TileService
implements LifecycleOwner {
    private final LifecycleRegistry lifecycle = new LifecycleRegistry((LifecycleOwner)this);
    private OverlayMonitor overlayMonitor;

    public QuickSettingsService() {
        this.lifecycle.markState(Lifecycle.State.INITIALIZED);
    }

    private Analytics getAnalytics() {
        return ComponentLocator.getInstance(this.getApplicationContext()).getAnalytics().get();
    }

    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    public void onClick() {
        if (this.isLocked()) {
            return;
        }
        OverlayMonitor overlayMonitor = this.overlayMonitor;
        if (overlayMonitor == null) {
            return;
        }
        if (overlayMonitor.isActive()) {
            this.getAnalytics().logEvent("qs_tile_clicked", "stop");
            this.overlayMonitor.stop();
            return;
        }
        this.getAnalytics().logEvent("qs_tile_clicked", "start");
        Intent intent = new Intent(this.getApplicationContext(), Launcher.class);
        intent.addFlags(268435456);
        intent.setAction("d457d020");
        try {
            this.startActivityAndCollapse(intent);
            return;
        }
        catch (RuntimeException runtimeException) {
            Crane.report(runtimeException);
            Toaster.toast(2131689708);
            return;
        }
    }

    public void onCreate() {
        super.onCreate();
        this.overlayMonitor = ComponentLocator.getInstance(this.getApplicationContext()).getOverlayMonitor().get();
        this.overlayMonitor.getView().observe((LifecycleOwner)this, (Observer)new Observer<View>(){

            public void onChanged(View view) {
                Tile tile = QuickSettingsService.this.getQsTile();
                if (tile == null) {
                    return;
                }
                if (view != null) {
                    tile.setState(2);
                } else {
                    tile.setState(1);
                }
                tile.updateTile();
            }
        });
    }

    public void onStartListening() {
        this.lifecycle.markState(Lifecycle.State.RESUMED);
        Tile tile = this.getQsTile();
        tile.setLabel((CharSequence)this.getString(2131689682));
        tile.updateTile();
    }

    public void onStopListening() {
        super.onStopListening();
        this.lifecycle.markState(Lifecycle.State.CREATED);
    }

    public void onTileAdded() {
        super.onTileAdded();
        this.getAnalytics().logEvent("qs_tile_added");
    }

    public void onTileRemoved() {
        super.onTileRemoved();
        this.getAnalytics().logEvent("qs_tile_removed");
    }

}

