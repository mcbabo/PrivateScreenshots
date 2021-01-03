/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.animation.Animator
 *  android.animation.Animator$AnimatorListener
 *  android.animation.TimeInterpolator
 *  android.arch.lifecycle.MutableLiveData
 *  android.content.Context
 *  android.content.Intent
 *  android.content.res.Resources
 *  android.graphics.Bitmap
 *  android.net.Uri
 *  android.os.Bundle
 *  android.os.Parcelable
 *  android.support.v4.app.FragmentManager
 *  android.text.SpannableString
 *  android.text.style.TextAppearanceSpan
 *  android.view.View
 *  android.view.ViewPropertyAnimator
 *  java.io.File
 *  java.io.Serializable
 *  java.lang.CharSequence
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Void
 *  java.lang.ref.SoftReference
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 */
package com.shamanland.privatescreenshots.utils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewPropertyAnimator;
import com.shamanland.common.ui.ConfirmationDialogFragment;
import com.shamanland.common.utils.Promise;
import com.shamanland.common.utils.Utils;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.DefaultAnimatorListener;
import java.io.File;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AppUtils {
    private static final String LOG_TAG = "AppUtils";
    private static final HashMap<File, SoftReference<Bitmap>> cache = new HashMap();

    public static Promise<Void> animateShaking(View view) {
        final MutableLiveData mutableLiveData = new MutableLiveData();
        view.animate().translationXBy((float)view.getResources().getDimensionPixelSize(2131165412)).setDuration(500L).setInterpolator(new TimeInterpolator(){

            public float getInterpolation(float f) {
                return (float)Math.sin((double)(3.141592653589793 * (double)(f * 16.0f)));
            }
        }).setListener((Animator.AnimatorListener)new DefaultAnimatorListener(){

            @Override
            public void onAnimationCancel(Animator animator) {
                mutableLiveData.setValue(null);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mutableLiveData.setValue(null);
            }
        }).start();
        return Promise.wrap(mutableLiveData);
    }

    public static Bitmap getFromCache(File file) {
        SoftReference softReference = (SoftReference)cache.get((Object)file);
        if (softReference != null) {
            Bitmap bitmap = (Bitmap)softReference.get();
            if (bitmap != null) {
                return bitmap;
            }
            cache.remove((Object)file);
        }
        if (cache.size() > 1000) {
            Iterator iterator = cache.entrySet().iterator();
            while (iterator.hasNext()) {
                if (((SoftReference)((Map.Entry)iterator.next()).getValue()).get() != null) continue;
                iterator.remove();
            }
        }
        return null;
    }

    public static /* varargs */ CharSequence getStyledText(Context context, int n, int n2, Object ... arrobject) {
        SpannableString spannableString = new SpannableString((CharSequence)context.getString(n2, arrobject));
        spannableString.setSpan((Object)new TextAppearanceSpan(context, n), 0, spannableString.length(), 33);
        return spannableString;
    }

    public static void putToCache(File file, Bitmap bitmap) {
        cache.put((Object)file, (Object)new SoftReference((Object)bitmap));
    }

    public static void share(Context context, Storage storage, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/png");
        intent.putExtra("android.intent.extra.STREAM", (Parcelable)storage.getUri(file));
        Utils.tryStartActivity(context, Intent.createChooser((Intent)intent, (CharSequence)context.getText(2131689689)), 2131689528);
    }

    public static void showDeleteDialog(Context context, FragmentManager fragmentManager, File file, String string2) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("23b0b557", (Serializable)file);
        ConfirmationDialogFragment.Builder builder = new ConfirmationDialogFragment.Builder().setId(string2).setTitle(context.getString(2131689558));
        Object[] arrobject = new Object[]{file.getName()};
        builder.setMessage(context.getString(2131689574, arrobject)).setPositiveText(context.getString(2131689557)).setNegativeText(context.getString(17039360)).setUseNeverShow(true).setExtras(bundle).show(fragmentManager);
    }

    public static void showDetailsDialog(Context context, FragmentManager fragmentManager, File file) {
        Object[] arrobject = new Object[]{file.getName(), Utils.formatBytesSize(file.length()), file.getParent()};
        String string2 = context.getString(2131689577, arrobject);
        new ConfirmationDialogFragment.Builder().setId("file_details").setTitle(context.getString(2131689588)).setMessage(string2).setPositiveText(context.getString(2131689655)).show(fragmentManager);
    }

}

