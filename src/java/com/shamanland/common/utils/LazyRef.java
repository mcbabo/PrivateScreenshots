/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package com.shamanland.common.utils;

public abstract class LazyRef<T> {
    private T instance;

    protected abstract T acquireRef();

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final T get() {
        if (this.instance != null) return this.instance;
        LazyRef lazyRef = this;
        synchronized (lazyRef) {
            if (this.instance != null) return this.instance;
            this.instance = this.acquireRef();
            return this.instance;
        }
    }
}

