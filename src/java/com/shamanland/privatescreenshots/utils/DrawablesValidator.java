/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.Intent
 *  android.content.res.Resources
 *  android.content.res.Resources$NotFoundException
 *  android.graphics.drawable.Drawable
 *  android.os.Bundle
 *  android.util.DisplayMetrics
 *  android.util.TypedValue
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.widget.Button
 *  android.widget.LinearLayout
 *  android.widget.LinearLayout$LayoutParams
 *  android.widget.Space
 *  android.widget.TextView
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.common.utils.Utils;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;

public class DrawablesValidator
extends Activity {
    public static void ensureDrawablesValid(Activity activity) {
        try {
            activity.getDrawable(2131230872);
            return;
        }
        catch (Resources.NotFoundException notFoundException) {
            ComponentLocator.getInstance((Context)activity).getAnalytics().get().logError("drawable_pixel_not_found");
            activity.finish();
            activity.startActivity(new Intent((Context)activity, DrawablesValidator.class));
            return;
        }
    }

    private LinearLayout.LayoutParams lp(int n, int n2, int n3, int n4) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(n, n2);
        layoutParams.weight = n3;
        layoutParams.gravity = n4;
        return layoutParams;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int n = (int)TypedValue.applyDimension((int)1, (float)1.0f, (DisplayMetrics)this.getResources().getDisplayMetrics());
        int n2 = n * 8;
        int n3 = n * 16;
        int n4 = n * 80;
        LinearLayout linearLayout = new LinearLayout((Context)this);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        linearLayout.setPadding(n4, n3, n4, n3);
        Space space = new Space((Context)this);
        TextView textView = new TextView((Context)this);
        textView.setPadding(0, n2, 0, n2);
        textView.setTextSize(20.0f);
        textView.setText((CharSequence)"Re-install app");
        TextView textView2 = new TextView((Context)this);
        textView2.setPadding(0, n2, 0, n2);
        textView2.setTextSize(16.0f);
        textView2.setText((CharSequence)"This copy of app is corrupted and can't be launched.\n\nPlease, install original version from Google Play");
        Button button = new Button((Context)this);
        button.setPadding(n3, n2, n3, n2);
        button.setText((CharSequence)"Continue");
        button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                Utils.openMarket(view.getContext(), 2131689526);
                ComponentLocator.getInstance(view.getContext()).getAnalytics().get().logEvent("drawables_validator_continue");
            }
        });
        Space space2 = new Space((Context)this);
        linearLayout.addView((View)space, (ViewGroup.LayoutParams)this.lp(0, 0, 1, -1));
        linearLayout.addView((View)textView, (ViewGroup.LayoutParams)this.lp(-2, -2, 0, -1));
        linearLayout.addView((View)textView2, (ViewGroup.LayoutParams)this.lp(-1, -2, 0, -1));
        linearLayout.addView((View)button, (ViewGroup.LayoutParams)this.lp(-2, -2, 0, 8388613));
        linearLayout.addView((View)space2, (ViewGroup.LayoutParams)this.lp(-1, -2, 1, -1));
        this.setContentView((View)linearLayout);
    }

}

