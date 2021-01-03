/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.android.gms.ads.AdRequest
 *  com.google.android.gms.ads.AdRequest$Builder
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.utils;

import com.google.android.gms.ads.AdRequest;

public class AdHelper {
    public static AdRequest buildAdRequest() {
        return new AdRequest.Builder().addTestDevice("B18F43E0C7E5CB90B2E572B504EFF858").addTestDevice("A3D07CF425A7CBF8CE2522236910AA91").addTestDevice("F9D1C8C0A79F0835FEA036A3DEEF6F48").build();
    }
}

