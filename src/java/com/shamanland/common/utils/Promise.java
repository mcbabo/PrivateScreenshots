/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.arch.core.util.Function
 *  android.arch.lifecycle.LifecycleOwner
 *  android.arch.lifecycle.LiveData
 *  android.arch.lifecycle.MutableLiveData
 *  android.arch.lifecycle.Observer
 *  java.lang.Object
 */
package com.shamanland.common.utils;

import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

public class Promise<T> {
    protected final LiveData<T> liveData;

    private Promise(LiveData<T> liveData) {
        this.liveData = liveData;
    }

    public static <V> Promise<V> wrap(LiveData<V> liveData) {
        return new Promise<V>(liveData);
    }

    public <R> Promise<R> once(LifecycleOwner lifecycleOwner, final Function<T, R> function) {
        final MutableLiveData mutableLiveData = new MutableLiveData();
        this.liveData.observe(lifecycleOwner, new Observer<T>(){

            public void onChanged(T t) {
                Promise.this.liveData.removeObserver((Observer)this);
                mutableLiveData.setValue(function.apply(t));
            }
        });
        return Promise.wrap(mutableLiveData);
    }

}

