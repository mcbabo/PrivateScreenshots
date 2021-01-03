/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.view.MotionEvent
 *  android.view.ScaleGestureDetector
 *  android.view.ScaleGestureDetector$OnScaleGestureListener
 *  android.view.VelocityTracker
 *  android.view.ViewConfiguration
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 */
package com.github.chrisbanes.photoview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.github.chrisbanes.photoview.OnGestureListener;
import com.github.chrisbanes.photoview.Util;

class CustomGestureDetector {
    private int mActivePointerId = -1;
    private int mActivePointerIndex = 0;
    private final ScaleGestureDetector mDetector;
    private boolean mIsDragging;
    private float mLastTouchX;
    private float mLastTouchY;
    private OnGestureListener mListener;
    private final float mMinimumVelocity;
    private final float mTouchSlop;
    private VelocityTracker mVelocityTracker;

    CustomGestureDetector(Context context, OnGestureListener onGestureListener) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get((Context)context);
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mListener = onGestureListener;
        this.mDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener(){

            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                float f = scaleGestureDetector.getScaleFactor();
                if (!Float.isNaN((float)f) && !Float.isInfinite((float)f)) {
                    CustomGestureDetector.this.mListener.onScale(f, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                    return true;
                }
                return false;
            }

            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            }
        });
    }

    private float getActiveX(MotionEvent motionEvent) {
        try {
            float f = motionEvent.getX(this.mActivePointerIndex);
            return f;
        }
        catch (Exception exception) {
            return motionEvent.getX();
        }
    }

    private float getActiveY(MotionEvent motionEvent) {
        try {
            float f = motionEvent.getY(this.mActivePointerIndex);
            return f;
        }
        catch (Exception exception) {
            return motionEvent.getY();
        }
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        int n;
        block17 : {
            block16 : {
                int n2 = 255 & motionEvent.getAction();
                if (n2 == 6) break block16;
                switch (n2) {
                    default: {
                        break;
                    }
                    case 3: {
                        this.mActivePointerId = -1;
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        if (velocityTracker != null) {
                            velocityTracker.recycle();
                            this.mVelocityTracker = null;
                            break;
                        }
                        break block17;
                    }
                    case 2: {
                        float f = this.getActiveX(motionEvent);
                        float f2 = this.getActiveY(motionEvent);
                        float f3 = f - this.mLastTouchX;
                        float f4 = f2 - this.mLastTouchY;
                        if (!this.mIsDragging) {
                            boolean bl = Math.sqrt((double)(f3 * f3 + f4 * f4)) >= (double)this.mTouchSlop;
                            this.mIsDragging = bl;
                        }
                        if (this.mIsDragging) {
                            this.mListener.onDrag(f3, f4);
                            this.mLastTouchX = f;
                            this.mLastTouchY = f2;
                            VelocityTracker velocityTracker = this.mVelocityTracker;
                            if (velocityTracker != null) {
                                velocityTracker.addMovement(motionEvent);
                                break;
                            }
                        }
                        break block17;
                    }
                    case 1: {
                        VelocityTracker velocityTracker;
                        this.mActivePointerId = -1;
                        if (this.mIsDragging && this.mVelocityTracker != null) {
                            this.mLastTouchX = this.getActiveX(motionEvent);
                            this.mLastTouchY = this.getActiveY(motionEvent);
                            this.mVelocityTracker.addMovement(motionEvent);
                            this.mVelocityTracker.computeCurrentVelocity(1000);
                            float f = this.mVelocityTracker.getXVelocity();
                            float f5 = this.mVelocityTracker.getYVelocity();
                            if (Math.max((float)Math.abs((float)f), (float)Math.abs((float)f5)) >= this.mMinimumVelocity) {
                                this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -f, -f5);
                            }
                        }
                        if ((velocityTracker = this.mVelocityTracker) != null) {
                            velocityTracker.recycle();
                            this.mVelocityTracker = null;
                            break;
                        }
                        break block17;
                    }
                    case 0: {
                        this.mActivePointerId = motionEvent.getPointerId(0);
                        VelocityTracker velocityTracker = this.mVelocityTracker = VelocityTracker.obtain();
                        if (velocityTracker != null) {
                            velocityTracker.addMovement(motionEvent);
                        }
                        this.mLastTouchX = this.getActiveX(motionEvent);
                        this.mLastTouchY = this.getActiveY(motionEvent);
                        this.mIsDragging = false;
                        break;
                    }
                }
                break block17;
            }
            int n3 = Util.getPointerIndex(motionEvent.getAction());
            if (motionEvent.getPointerId(n3) == this.mActivePointerId) {
                int n4 = n3 == 0 ? 1 : 0;
                this.mActivePointerId = motionEvent.getPointerId(n4);
                this.mLastTouchX = motionEvent.getX(n4);
                this.mLastTouchY = motionEvent.getY(n4);
            }
        }
        if ((n = this.mActivePointerId) == -1) {
            n = 0;
        }
        this.mActivePointerIndex = motionEvent.findPointerIndex(n);
        return true;
    }

    public boolean isDragging() {
        return this.mIsDragging;
    }

    public boolean isScaling() {
        return this.mDetector.isInProgress();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            this.mDetector.onTouchEvent(motionEvent);
            boolean bl = this.processTouchEvent(motionEvent);
            return bl;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return true;
        }
    }

}

