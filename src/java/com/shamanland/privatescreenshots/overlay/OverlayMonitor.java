/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.arch.lifecycle.LiveData
 *  android.arch.lifecycle.MutableLiveData
 *  android.content.Context
 *  android.view.View
 *  java.lang.Object
 */
package com.shamanland.privatescreenshots.overlay;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.view.View;
import com.shamanland.privatescreenshots.overlay.OverlayService;

public class OverlayMonitor {
    private final Context context;
    private final MutableLiveData<View> view;

    public OverlayMonitor(Context context) {
        this.context = context;
        this.view = new MutableLiveData();
        this.view.setValue(null);
    }

    public LiveData<View> getView() {
        return this.view;
    }

    public boolean isActive() {
        return this.view.getValue() != null;
    }

    public void setView(View view) {
        this.view.setValue((Object)view);
    }

    public void stop() {
        OverlayService.stop(this.context);
    }
}

