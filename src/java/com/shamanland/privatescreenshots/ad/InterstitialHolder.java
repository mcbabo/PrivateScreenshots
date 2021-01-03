/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  com.google.android.gms.ads.AdRequest
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 */
package com.shamanland.privatescreenshots.ad;

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.shamanland.privatescreenshots.ad.InterstitialWrapper;
import java.util.HashMap;
import java.util.Map;

public class InterstitialHolder {
    private final Map<String, InterstitialWrapper> ads = new HashMap();

    public InterstitialWrapper getAd(Context context, String string2, AdRequest adRequest) {
        InterstitialWrapper interstitialWrapper = (InterstitialWrapper)((Object)this.ads.get((Object)string2));
        if (interstitialWrapper == null) {
            interstitialWrapper = new InterstitialWrapper(context, string2, adRequest);
            this.ads.put((Object)string2, (Object)interstitialWrapper);
        }
        return interstitialWrapper;
    }
}

