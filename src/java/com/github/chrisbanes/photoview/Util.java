/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.graphics.drawable.Drawable
 *  android.widget.ImageView
 *  android.widget.ImageView$ScaleType
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.NoSuchFieldError
 *  java.lang.Object
 *  java.lang.String
 */
package com.github.chrisbanes.photoview;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

class Util {
    static void checkZoomLevels(float f, float f2, float f3) {
        if (!(f >= f2)) {
            if (!(f2 >= f3)) {
                return;
            }
            throw new IllegalArgumentException("Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
        throw new IllegalArgumentException("Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
    }

    static int getPointerIndex(int n) {
        return (n & 65280) >> 8;
    }

    static boolean hasDrawable(ImageView imageView) {
        return imageView.getDrawable() != null;
    }

    static boolean isSupportedScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        if (1.$SwitchMap$android$widget$ImageView$ScaleType[scaleType.ordinal()] != 1) {
            return true;
        }
        throw new IllegalStateException("Matrix scale type is not supported");
    }

}

