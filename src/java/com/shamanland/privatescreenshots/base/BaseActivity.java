/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.Application
 *  android.content.Context
 *  android.os.Bundle
 *  android.support.v7.app.AppCompatActivity
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.security.SessionValidator;

@SuppressLint(value={"Registered"})
public class BaseActivity
extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";
    private boolean paused;

    protected ComponentLocator getComponentLocator() {
        return ComponentLocator.getInstance((Context)this);
    }

    public boolean isPaused() {
        return this.paused;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.getComponentLocator().getSessionValidator().get().init(this.getApplication());
    }

    protected void onPause() {
        super.onPause();
        this.paused = true;
    }

    protected void onResume() {
        super.onResume();
        this.paused = false;
    }
}

