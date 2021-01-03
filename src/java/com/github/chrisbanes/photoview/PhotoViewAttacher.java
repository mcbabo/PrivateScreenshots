/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.Matrix
 *  android.graphics.Matrix$ScaleToFit
 *  android.graphics.RectF
 *  android.graphics.drawable.Drawable
 *  android.support.v4.view.MotionEventCompat
 *  android.view.GestureDetector
 *  android.view.GestureDetector$OnDoubleTapListener
 *  android.view.GestureDetector$OnGestureListener
 *  android.view.GestureDetector$SimpleOnGestureListener
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.View$OnLayoutChangeListener
 *  android.view.View$OnLongClickListener
 *  android.view.View$OnTouchListener
 *  android.view.ViewParent
 *  android.view.animation.AccelerateDecelerateInterpolator
 *  android.view.animation.Interpolator
 *  android.widget.ImageView
 *  android.widget.ImageView$ScaleType
 *  android.widget.OverScroller
 *  java.lang.ArrayIndexOutOfBoundsException
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.NoSuchFieldError
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.System
 */
package com.github.chrisbanes.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.OverScroller;
import com.github.chrisbanes.photoview.Compat;
import com.github.chrisbanes.photoview.CustomGestureDetector;
import com.github.chrisbanes.photoview.OnGestureListener;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.OnViewDragListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.Util;

public class PhotoViewAttacher
implements View.OnLayoutChangeListener,
View.OnTouchListener {
    private static float DEFAULT_MAX_SCALE = 3.0f;
    private static float DEFAULT_MID_SCALE = 1.75f;
    private static float DEFAULT_MIN_SCALE = 1.0f;
    private static int DEFAULT_ZOOM_DURATION = 200;
    private static int SINGLE_TOUCH = 1;
    private boolean mAllowParentInterceptOnEdge = true;
    private final Matrix mBaseMatrix = new Matrix();
    private float mBaseRotation;
    private boolean mBlockParentIntercept = false;
    private FlingRunnable mCurrentFlingRunnable;
    private final RectF mDisplayRect = new RectF();
    private final Matrix mDrawMatrix = new Matrix();
    private GestureDetector mGestureDetector;
    private ImageView mImageView;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private View.OnLongClickListener mLongClickListener;
    private OnMatrixChangedListener mMatrixChangeListener;
    private final float[] mMatrixValues = new float[9];
    private float mMaxScale = DEFAULT_MAX_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private View.OnClickListener mOnClickListener;
    private OnViewDragListener mOnViewDragListener;
    private OnOutsidePhotoTapListener mOutsidePhotoTapListener;
    private OnPhotoTapListener mPhotoTapListener;
    private OnScaleChangedListener mScaleChangeListener;
    private CustomGestureDetector mScaleDragDetector;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;
    private int mScrollEdge = 2;
    private OnSingleFlingListener mSingleFlingListener;
    private final Matrix mSuppMatrix = new Matrix();
    private boolean mUseMediumScale = true;
    private OnViewTapListener mViewTapListener;
    private int mZoomDuration = DEFAULT_ZOOM_DURATION;
    private boolean mZoomEnabled = true;
    private OnGestureListener onGestureListener = new OnGestureListener(){

        @Override
        public void onDrag(float f, float f2) {
            if (PhotoViewAttacher.this.mScaleDragDetector.isScaling()) {
                return;
            }
            if (PhotoViewAttacher.this.mOnViewDragListener != null) {
                PhotoViewAttacher.this.mOnViewDragListener.onDrag(f, f2);
            }
            PhotoViewAttacher.this.mSuppMatrix.postTranslate(f, f2);
            PhotoViewAttacher.this.checkAndDisplayMatrix();
            ViewParent viewParent = PhotoViewAttacher.this.mImageView.getParent();
            if (PhotoViewAttacher.this.mAllowParentInterceptOnEdge && !PhotoViewAttacher.this.mScaleDragDetector.isScaling() && !PhotoViewAttacher.this.mBlockParentIntercept) {
                if ((PhotoViewAttacher.this.mScrollEdge == 2 || PhotoViewAttacher.this.mScrollEdge == 0 && f >= 1.0f || PhotoViewAttacher.this.mScrollEdge == 1 && f <= -1.0f) && viewParent != null) {
                    viewParent.requestDisallowInterceptTouchEvent(false);
                    return;
                }
            } else if (viewParent != null) {
                viewParent.requestDisallowInterceptTouchEvent(true);
            }
        }

        @Override
        public void onFling(float f, float f2, float f3, float f4) {
            PhotoViewAttacher photoViewAttacher = PhotoViewAttacher.this;
            photoViewAttacher.mCurrentFlingRunnable = photoViewAttacher.new FlingRunnable(photoViewAttacher.mImageView.getContext());
            FlingRunnable flingRunnable = PhotoViewAttacher.this.mCurrentFlingRunnable;
            PhotoViewAttacher photoViewAttacher2 = PhotoViewAttacher.this;
            int n = photoViewAttacher2.getImageViewWidth(photoViewAttacher2.mImageView);
            PhotoViewAttacher photoViewAttacher3 = PhotoViewAttacher.this;
            flingRunnable.fling(n, photoViewAttacher3.getImageViewHeight(photoViewAttacher3.mImageView), (int)f3, (int)f4);
            PhotoViewAttacher.this.mImageView.post((Runnable)PhotoViewAttacher.this.mCurrentFlingRunnable);
        }

        @Override
        public void onScale(float f, float f2, float f3) {
            if ((PhotoViewAttacher.this.getScale() < PhotoViewAttacher.this.mMaxScale || f < 1.0f) && (PhotoViewAttacher.this.getScale() > PhotoViewAttacher.this.mMinScale || f > 1.0f)) {
                if (PhotoViewAttacher.this.mScaleChangeListener != null) {
                    PhotoViewAttacher.this.mScaleChangeListener.onScaleChange(f, f2, f3);
                }
                PhotoViewAttacher.this.mSuppMatrix.postScale(f, f, f2, f3);
                PhotoViewAttacher.this.checkAndDisplayMatrix();
            }
        }
    };

    public PhotoViewAttacher(ImageView imageView) {
        this.mImageView = imageView;
        imageView.setOnTouchListener((View.OnTouchListener)this);
        imageView.addOnLayoutChangeListener((View.OnLayoutChangeListener)this);
        if (imageView.isInEditMode()) {
            return;
        }
        this.mBaseRotation = 0.0f;
        this.mScaleDragDetector = new CustomGestureDetector(imageView.getContext(), this.onGestureListener);
        this.mGestureDetector = new GestureDetector(imageView.getContext(), (GestureDetector.OnGestureListener)new GestureDetector.SimpleOnGestureListener(){

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (PhotoViewAttacher.this.mSingleFlingListener != null) {
                    if (PhotoViewAttacher.this.getScale() > DEFAULT_MIN_SCALE) {
                        return false;
                    }
                    if (MotionEventCompat.getPointerCount((MotionEvent)motionEvent) <= SINGLE_TOUCH) {
                        if (MotionEventCompat.getPointerCount((MotionEvent)motionEvent2) > SINGLE_TOUCH) {
                            return false;
                        }
                        return PhotoViewAttacher.this.mSingleFlingListener.onFling(motionEvent, motionEvent2, f, f2);
                    }
                    return false;
                }
                return false;
            }

            public void onLongPress(MotionEvent motionEvent) {
                if (PhotoViewAttacher.this.mLongClickListener != null) {
                    PhotoViewAttacher.this.mLongClickListener.onLongClick((View)PhotoViewAttacher.this.mImageView);
                }
            }
        });
        this.mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener(){

            public boolean onDoubleTap(MotionEvent motionEvent) {
                float f;
                float f2;
                block9 : {
                    float f3;
                    block6 : {
                        block8 : {
                            block7 : {
                                try {
                                    f3 = PhotoViewAttacher.this.getScale();
                                    f = motionEvent.getX();
                                    f2 = motionEvent.getY();
                                    if (!PhotoViewAttacher.this.useMidScale()) break block6;
                                    if (!(f3 < PhotoViewAttacher.this.getMediumScale())) break block7;
                                    PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMediumScale(), f, f2, true);
                                    return true;
                                }
                                catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
                            }
                            if (!(f3 >= PhotoViewAttacher.this.getMediumScale()) || !(f3 < PhotoViewAttacher.this.getMaximumScale())) break block8;
                            PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMaximumScale(), f, f2, true);
                            return true;
                        }
                        PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMinimumScale(), f, f2, true);
                        return true;
                    }
                    if (!(f3 < PhotoViewAttacher.this.getMaximumScale())) break block9;
                    PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMaximumScale(), f, f2, true);
                    return true;
                }
                PhotoViewAttacher.this.setScale(PhotoViewAttacher.this.getMinimumScale(), f, f2, true);
                return true;
            }

            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }

            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (PhotoViewAttacher.this.mOnClickListener != null) {
                    PhotoViewAttacher.this.mOnClickListener.onClick((View)PhotoViewAttacher.this.mImageView);
                }
                RectF rectF = PhotoViewAttacher.this.getDisplayRect();
                float f = motionEvent.getX();
                float f2 = motionEvent.getY();
                if (PhotoViewAttacher.this.mViewTapListener != null) {
                    PhotoViewAttacher.this.mViewTapListener.onViewTap((View)PhotoViewAttacher.this.mImageView, f, f2);
                }
                if (rectF != null) {
                    if (rectF.contains(f, f2)) {
                        float f3 = (f - rectF.left) / rectF.width();
                        float f4 = (f2 - rectF.top) / rectF.height();
                        if (PhotoViewAttacher.this.mPhotoTapListener != null) {
                            PhotoViewAttacher.this.mPhotoTapListener.onPhotoTap(PhotoViewAttacher.this.mImageView, f3, f4);
                        }
                        return true;
                    }
                    if (PhotoViewAttacher.this.mOutsidePhotoTapListener != null) {
                        PhotoViewAttacher.this.mOutsidePhotoTapListener.onOutsidePhotoTap(PhotoViewAttacher.this.mImageView);
                    }
                }
                return false;
            }
        });
    }

    private void cancelFling() {
        FlingRunnable flingRunnable = this.mCurrentFlingRunnable;
        if (flingRunnable != null) {
            flingRunnable.cancelFling();
            this.mCurrentFlingRunnable = null;
        }
    }

    private void checkAndDisplayMatrix() {
        if (this.checkMatrixBounds()) {
            this.setImageViewMatrix(this.getDrawMatrix());
        }
    }

    private boolean checkMatrixBounds() {
        float f;
        RectF rectF = this.getDisplayRect(this.getDrawMatrix());
        if (rectF == null) {
            return false;
        }
        float f2 = rectF.height();
        float f3 = rectF.width();
        float f4 = this.getImageViewHeight(this.mImageView);
        float f5 = 0.0f;
        if (f2 <= f4) {
            switch (4.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
                default: {
                    f = (f4 - f2) / 2.0f - rectF.top;
                    break;
                }
                case 3: {
                    f = f4 - f2 - rectF.top;
                    break;
                }
                case 2: {
                    f = -rectF.top;
                    break;
                }
            }
        } else {
            f = rectF.top > 0.0f ? -rectF.top : (rectF.bottom < f4 ? f4 - rectF.bottom : 0.0f);
        }
        float f6 = this.getImageViewWidth(this.mImageView);
        if (f3 <= f6) {
            switch (4.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
                default: {
                    f5 = (f6 - f3) / 2.0f - rectF.left;
                    break;
                }
                case 3: {
                    f5 = f6 - f3 - rectF.left;
                    break;
                }
                case 2: {
                    f5 = -rectF.left;
                }
            }
            this.mScrollEdge = 2;
        } else if (rectF.left > 0.0f) {
            this.mScrollEdge = 0;
            f5 = -rectF.left;
        } else if (rectF.right < f6) {
            f5 = f6 - rectF.right;
            this.mScrollEdge = 1;
        } else {
            this.mScrollEdge = -1;
        }
        this.mSuppMatrix.postTranslate(f5, f);
        return true;
    }

    private RectF getDisplayRect(Matrix matrix) {
        Drawable drawable = this.mImageView.getDrawable();
        if (drawable != null) {
            this.mDisplayRect.set(0.0f, 0.0f, (float)drawable.getIntrinsicWidth(), (float)drawable.getIntrinsicHeight());
            matrix.mapRect(this.mDisplayRect);
            return this.mDisplayRect;
        }
        return null;
    }

    private Matrix getDrawMatrix() {
        this.mDrawMatrix.set(this.mBaseMatrix);
        this.mDrawMatrix.postConcat(this.mSuppMatrix);
        return this.mDrawMatrix;
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private float getValue(Matrix matrix, int n) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[n];
    }

    private void resetMatrix() {
        this.mSuppMatrix.reset();
        this.setRotationBy(this.mBaseRotation);
        this.setImageViewMatrix(this.getDrawMatrix());
        this.checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        RectF rectF;
        this.mImageView.setImageMatrix(matrix);
        if (this.mMatrixChangeListener != null && (rectF = this.getDisplayRect(matrix)) != null) {
            this.mMatrixChangeListener.onMatrixChanged(rectF);
        }
    }

    private void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        float f = this.getImageViewWidth(this.mImageView);
        float f2 = this.getImageViewHeight(this.mImageView);
        int n = drawable.getIntrinsicWidth();
        int n2 = drawable.getIntrinsicHeight();
        this.mBaseMatrix.reset();
        float f3 = n;
        float f4 = f / f3;
        float f5 = n2;
        float f6 = f2 / f5;
        if (this.mScaleType == ImageView.ScaleType.CENTER) {
            this.mBaseMatrix.postTranslate((f - f3) / 2.0f, (f2 - f5) / 2.0f);
        } else if (this.mScaleType == ImageView.ScaleType.CENTER_CROP) {
            float f7 = Math.max((float)f4, (float)f6);
            this.mBaseMatrix.postScale(f7, f7);
            this.mBaseMatrix.postTranslate((f - f3 * f7) / 2.0f, (f2 - f5 * f7) / 2.0f);
        } else if (this.mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
            float f8 = Math.min((float)1.0f, (float)Math.min((float)f4, (float)f6));
            this.mBaseMatrix.postScale(f8, f8);
            this.mBaseMatrix.postTranslate((f - f3 * f8) / 2.0f, (f2 - f5 * f8) / 2.0f);
        } else {
            RectF rectF = new RectF(0.0f, 0.0f, f3, f5);
            RectF rectF2 = new RectF(0.0f, 0.0f, f, f2);
            if ((int)this.mBaseRotation % 180 != 0) {
                rectF = new RectF(0.0f, 0.0f, f5, f3);
            }
            switch (4.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
                default: {
                    break;
                }
                case 4: {
                    this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.FILL);
                    break;
                }
                case 3: {
                    this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.END);
                    break;
                }
                case 2: {
                    this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.START);
                    break;
                }
                case 1: {
                    this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.CENTER);
                }
            }
        }
        this.resetMatrix();
    }

    public RectF getDisplayRect() {
        this.checkMatrixBounds();
        return this.getDisplayRect(this.getDrawMatrix());
    }

    public Matrix getImageMatrix() {
        return this.mDrawMatrix;
    }

    public float getMaximumScale() {
        return this.mMaxScale;
    }

    public float getMediumScale() {
        return this.mMidScale;
    }

    public float getMinimumScale() {
        return this.mMinScale;
    }

    public float getScale() {
        return (float)Math.sqrt((double)((float)Math.pow((double)this.getValue(this.mSuppMatrix, 0), (double)2.0) + (float)Math.pow((double)this.getValue(this.mSuppMatrix, 3), (double)2.0)));
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void onLayoutChange(View view, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        if (n != n5 || n2 != n6 || n3 != n7 || n4 != n8) {
            this.updateBaseMatrix(this.mImageView.getDrawable());
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public boolean onTouch(View var1_1, MotionEvent var2_2) {
        var3_3 = this.mZoomEnabled;
        var4_4 = false;
        if (var3_3 == false) return var4_4;
        var5_5 = Util.hasDrawable((ImageView)var1_1);
        var4_4 = false;
        if (var5_5 == false) return var4_4;
        var6_6 = var2_2.getAction();
        if (var6_6 != 3) {
            switch (var6_6) {
                default: {
                    ** break;
                }
                case 0: {
                    var22_7 = var1_1.getParent();
                    if (var22_7 != null) {
                        var22_7.requestDisallowInterceptTouchEvent(true);
                    }
                    this.cancelFling();
                    ** break;
                }
                case 1: 
            }
        }
        if (this.getScale() < this.mMinScale) {
            var19_8 = this.getDisplayRect();
            if (var19_8 != null) {
                var20_9 = new AnimatedZoomRunnable(this.getScale(), this.mMinScale, var19_8.centerX(), var19_8.centerY());
                var1_1.post((Runnable)var20_9);
                var7_10 = true;
            } else {
                var7_10 = false;
            }
        } else if (this.getScale() > this.mMaxScale && (var16_11 = this.getDisplayRect()) != null) {
            var17_12 = new AnimatedZoomRunnable(this.getScale(), this.mMaxScale, var16_11.centerX(), var16_11.centerY());
            var1_1.post((Runnable)var17_12);
            var7_10 = true;
        } else lbl-1000: // 3 sources:
        {
            var7_10 = false;
        }
        var8_13 = this.mScaleDragDetector;
        if (var8_13 != null) {
            var10_14 = var8_13.isScaling();
            var11_15 = this.mScaleDragDetector.isDragging();
            var12_16 = this.mScaleDragDetector.onTouchEvent(var2_2);
            var13_17 = var10_14 == false && this.mScaleDragDetector.isScaling() == false;
            var14_18 = var11_15 == false && this.mScaleDragDetector.isDragging() == false;
            var15_19 = false;
            if (var13_17) {
                var15_19 = false;
                if (var14_18) {
                    var15_19 = true;
                }
            }
            this.mBlockParentIntercept = var15_19;
            var4_4 = var12_16;
        } else {
            var4_4 = var7_10;
        }
        var9_20 = this.mGestureDetector;
        if (var9_20 == null) return var4_4;
        if (var9_20.onTouchEvent(var2_2) == false) return var4_4;
        return true;
    }

    public void setAllowParentInterceptOnEdge(boolean bl) {
        this.mAllowParentInterceptOnEdge = bl;
    }

    public void setMaximumScale(float f) {
        Util.checkZoomLevels(this.mMinScale, this.mMidScale, f);
        this.mMaxScale = f;
    }

    public void setMediumScale(float f) {
        Util.checkZoomLevels(this.mMinScale, f, this.mMaxScale);
        this.mMidScale = f;
    }

    public void setMinimumScale(float f) {
        Util.checkZoomLevels(f, this.mMidScale, this.mMaxScale);
        this.mMinScale = f;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        this.mGestureDetector.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mLongClickListener = onLongClickListener;
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener onMatrixChangedListener) {
        this.mMatrixChangeListener = onMatrixChangedListener;
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener onOutsidePhotoTapListener) {
        this.mOutsidePhotoTapListener = onOutsidePhotoTapListener;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.mPhotoTapListener = onPhotoTapListener;
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        this.mScaleChangeListener = onScaleChangedListener;
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener;
    }

    public void setOnViewDragListener(OnViewDragListener onViewDragListener) {
        this.mOnViewDragListener = onViewDragListener;
    }

    public void setOnViewTapListener(OnViewTapListener onViewTapListener) {
        this.mViewTapListener = onViewTapListener;
    }

    public void setRotationBy(float f) {
        this.mSuppMatrix.postRotate(f % 360.0f);
        this.checkAndDisplayMatrix();
    }

    public void setRotationTo(float f) {
        this.mSuppMatrix.setRotate(f % 360.0f);
        this.checkAndDisplayMatrix();
    }

    public void setScale(float f) {
        this.setScale(f, false);
    }

    public void setScale(float f, float f2, float f3, boolean bl) {
        if (!(f < this.mMinScale) && !(f > this.mMaxScale)) {
            if (bl) {
                ImageView imageView = this.mImageView;
                AnimatedZoomRunnable animatedZoomRunnable = new AnimatedZoomRunnable(this.getScale(), f, f2, f3);
                imageView.post((Runnable)animatedZoomRunnable);
                return;
            }
            this.mSuppMatrix.setScale(f, f, f2, f3);
            this.checkAndDisplayMatrix();
            return;
        }
        throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
    }

    public void setScale(float f, boolean bl) {
        this.setScale(f, this.mImageView.getRight() / 2, this.mImageView.getBottom() / 2, bl);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if (Util.isSupportedScaleType(scaleType) && scaleType != this.mScaleType) {
            this.mScaleType = scaleType;
            this.update();
        }
    }

    public void setUseMediumScale(boolean bl) {
        this.mUseMediumScale = bl;
    }

    public void setZoomTransitionDuration(int n) {
        this.mZoomDuration = n;
    }

    public void setZoomable(boolean bl) {
        this.mZoomEnabled = bl;
        this.update();
    }

    public void update() {
        if (this.mZoomEnabled) {
            this.updateBaseMatrix(this.mImageView.getDrawable());
            return;
        }
        this.resetMatrix();
    }

    public boolean useMidScale() {
        return this.mUseMediumScale;
    }

    private class AnimatedZoomRunnable
    implements Runnable {
        private final float mFocalX;
        private final float mFocalY;
        private final long mStartTime;
        private final float mZoomEnd;
        private final float mZoomStart;

        public AnimatedZoomRunnable(float f, float f2, float f3, float f4) {
            this.mFocalX = f3;
            this.mFocalY = f4;
            this.mStartTime = System.currentTimeMillis();
            this.mZoomStart = f;
            this.mZoomEnd = f2;
        }

        private float interpolate() {
            float f = Math.min((float)1.0f, (float)(1.0f * (float)(System.currentTimeMillis() - this.mStartTime) / (float)PhotoViewAttacher.this.mZoomDuration));
            return PhotoViewAttacher.this.mInterpolator.getInterpolation(f);
        }

        public void run() {
            float f = this.interpolate();
            float f2 = this.mZoomStart;
            float f3 = (f2 + f * (this.mZoomEnd - f2)) / PhotoViewAttacher.this.getScale();
            PhotoViewAttacher.this.onGestureListener.onScale(f3, this.mFocalX, this.mFocalY);
            if (f < 1.0f) {
                Compat.postOnAnimation((View)PhotoViewAttacher.this.mImageView, this);
            }
        }
    }

    private class FlingRunnable
    implements Runnable {
        private int mCurrentX;
        private int mCurrentY;
        private final OverScroller mScroller;

        public FlingRunnable(Context context) {
            this.mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            this.mScroller.forceFinished(true);
        }

        public void fling(int n, int n2, int n3, int n4) {
            int n5;
            int n6;
            int n7;
            int n8;
            RectF rectF = PhotoViewAttacher.this.getDisplayRect();
            if (rectF == null) {
                return;
            }
            int n9 = Math.round((float)(-rectF.left));
            float f = n;
            if (f < rectF.width()) {
                n6 = Math.round((float)(rectF.width() - f));
                n7 = 0;
            } else {
                n6 = n7 = n9;
            }
            int n10 = Math.round((float)(-rectF.top));
            float f2 = n2;
            if (f2 < rectF.height()) {
                n5 = Math.round((float)(rectF.height() - f2));
                n8 = 0;
            } else {
                n5 = n8 = n10;
            }
            this.mCurrentX = n9;
            this.mCurrentY = n10;
            if (n9 != n6 || n10 != n5) {
                this.mScroller.fling(n9, n10, n3, n4, n7, n6, n8, n5, 0, 0);
            }
        }

        public void run() {
            if (this.mScroller.isFinished()) {
                return;
            }
            if (this.mScroller.computeScrollOffset()) {
                int n = this.mScroller.getCurrX();
                int n2 = this.mScroller.getCurrY();
                PhotoViewAttacher.this.mSuppMatrix.postTranslate((float)(this.mCurrentX - n), (float)(this.mCurrentY - n2));
                PhotoViewAttacher.this.checkAndDisplayMatrix();
                this.mCurrentX = n;
                this.mCurrentY = n2;
                Compat.postOnAnimation((View)PhotoViewAttacher.this.mImageView, this);
            }
        }
    }

}

