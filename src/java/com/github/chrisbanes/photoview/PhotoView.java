/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.Matrix
 *  android.graphics.RectF
 *  android.graphics.drawable.Drawable
 *  android.net.Uri
 *  android.util.AttributeSet
 *  android.view.GestureDetector
 *  android.view.GestureDetector$OnDoubleTapListener
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.View$OnLongClickListener
 *  android.widget.ImageView
 *  android.widget.ImageView$ScaleType
 */
package com.github.chrisbanes.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageView;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.OnViewDragListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

public class PhotoView
extends ImageView {
    private PhotoViewAttacher attacher;
    private ImageView.ScaleType pendingScaleType;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PhotoView(Context context, AttributeSet attributeSet, int n) {
        super(context, attributeSet, n);
        this.init();
    }

    private void init() {
        this.attacher = new PhotoViewAttacher(this);
        super.setScaleType(ImageView.ScaleType.MATRIX);
        ImageView.ScaleType scaleType = this.pendingScaleType;
        if (scaleType != null) {
            this.setScaleType(scaleType);
            this.pendingScaleType = null;
        }
    }

    public PhotoViewAttacher getAttacher() {
        return this.attacher;
    }

    public RectF getDisplayRect() {
        return this.attacher.getDisplayRect();
    }

    public Matrix getImageMatrix() {
        return this.attacher.getImageMatrix();
    }

    public float getMaximumScale() {
        return this.attacher.getMaximumScale();
    }

    public float getMediumScale() {
        return this.attacher.getMediumScale();
    }

    public float getMinimumScale() {
        return this.attacher.getMinimumScale();
    }

    public float getScale() {
        return this.attacher.getScale();
    }

    public ImageView.ScaleType getScaleType() {
        return this.attacher.getScaleType();
    }

    public void setAllowParentInterceptOnEdge(boolean bl) {
        this.attacher.setAllowParentInterceptOnEdge(bl);
    }

    protected boolean setFrame(int n, int n2, int n3, int n4) {
        boolean bl = super.setFrame(n, n2, n3, n4);
        if (bl) {
            this.attacher.update();
        }
        return bl;
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher != null) {
            photoViewAttacher.update();
        }
    }

    public void setImageResource(int n) {
        super.setImageResource(n);
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher != null) {
            photoViewAttacher.update();
        }
    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher != null) {
            photoViewAttacher.update();
        }
    }

    public void setMaximumScale(float f) {
        this.attacher.setMaximumScale(f);
    }

    public void setMediumScale(float f) {
        this.attacher.setMediumScale(f);
    }

    public void setMinimumScale(float f) {
        this.attacher.setMinimumScale(f);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.attacher.setOnClickListener(onClickListener);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        this.attacher.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.attacher.setOnLongClickListener(onLongClickListener);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener onMatrixChangedListener) {
        this.attacher.setOnMatrixChangeListener(onMatrixChangedListener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener onOutsidePhotoTapListener) {
        this.attacher.setOnOutsidePhotoTapListener(onOutsidePhotoTapListener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.attacher.setOnPhotoTapListener(onPhotoTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        this.attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.attacher.setOnSingleFlingListener(onSingleFlingListener);
    }

    public void setOnViewDragListener(OnViewDragListener onViewDragListener) {
        this.attacher.setOnViewDragListener(onViewDragListener);
    }

    public void setOnViewTapListener(OnViewTapListener onViewTapListener) {
        this.attacher.setOnViewTapListener(onViewTapListener);
    }

    public void setRotationBy(float f) {
        this.attacher.setRotationBy(f);
    }

    public void setRotationTo(float f) {
        this.attacher.setRotationTo(f);
    }

    public void setScale(float f) {
        this.attacher.setScale(f);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        PhotoViewAttacher photoViewAttacher = this.attacher;
        if (photoViewAttacher == null) {
            this.pendingScaleType = scaleType;
            return;
        }
        photoViewAttacher.setScaleType(scaleType);
    }

    public void setUseMediumScale(boolean bl) {
        this.attacher.setUseMediumScale(bl);
    }

    public void setZoomTransitionDuration(int n) {
        this.attacher.setZoomTransitionDuration(n);
    }

    public void setZoomable(boolean bl) {
        this.attacher.setZoomable(bl);
    }
}

