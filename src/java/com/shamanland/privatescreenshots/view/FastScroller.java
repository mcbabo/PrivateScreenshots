/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.animation.Animator
 *  android.animation.Animator$AnimatorListener
 *  android.animation.AnimatorListenerAdapter
 *  android.animation.ValueAnimator
 *  android.animation.ValueAnimator$AnimatorUpdateListener
 *  android.annotation.SuppressLint
 *  android.graphics.Canvas
 *  android.graphics.drawable.Drawable
 *  android.graphics.drawable.StateListDrawable
 *  android.support.v4.view.ViewCompat
 *  android.support.v7.widget.RecyclerView
 *  android.support.v7.widget.RecyclerView$ItemDecoration
 *  android.support.v7.widget.RecyclerView$OnItemTouchListener
 *  android.support.v7.widget.RecyclerView$OnScrollListener
 *  android.support.v7.widget.RecyclerView$State
 *  android.view.MotionEvent
 *  android.view.View
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 */
package com.shamanland.privatescreenshots.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

public class FastScroller
extends RecyclerView.ItemDecoration
implements RecyclerView.OnItemTouchListener {
    private static final int[] EMPTY_STATE_SET;
    private static final int[] PRESSED_STATE_SET;
    protected int mAnimationState = 0;
    private int mDragState = 0;
    private final Runnable mHideRunnable = new Runnable(){

        public void run() {
            FastScroller.this.hide(500);
        }
    };
    float mHorizontalDragX;
    private final int[] mHorizontalRange = new int[2];
    int mHorizontalThumbCenterX;
    private final StateListDrawable mHorizontalThumbDrawable;
    private final int mHorizontalThumbHeight;
    int mHorizontalThumbWidth;
    private final Drawable mHorizontalTrackDrawable;
    private final int mHorizontalTrackHeight;
    private final int mMargin;
    private boolean mNeedHorizontalScrollbar = false;
    private boolean mNeedVerticalScrollbar = false;
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener(){

        public void onScrolled(RecyclerView recyclerView, int n, int n2) {
            FastScroller.this.updateScrollPosition(recyclerView.computeHorizontalScrollOffset(), recyclerView.computeVerticalScrollOffset());
        }
    };
    private RecyclerView mRecyclerView;
    private int mRecyclerViewHeight = 0;
    private int mRecyclerViewWidth = 0;
    private final int mScrollbarMinimumRange;
    protected final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat((float[])new float[]{0.0f, 1.0f});
    private int mState = 0;
    float mVerticalDragY;
    private final int[] mVerticalRange = new int[2];
    int mVerticalThumbCenterY;
    protected final StateListDrawable mVerticalThumbDrawable;
    int mVerticalThumbHeight;
    private final int mVerticalThumbWidth;
    protected final Drawable mVerticalTrackDrawable;
    private final int mVerticalTrackWidth;

    static {
        PRESSED_STATE_SET = new int[]{16842919};
        EMPTY_STATE_SET = new int[0];
    }

    public FastScroller(RecyclerView recyclerView, StateListDrawable stateListDrawable, Drawable drawable, StateListDrawable stateListDrawable2, Drawable drawable2, int n, int n2, int n3) {
        this.mVerticalThumbDrawable = stateListDrawable;
        this.mVerticalTrackDrawable = drawable;
        this.mHorizontalThumbDrawable = stateListDrawable2;
        this.mHorizontalTrackDrawable = drawable2;
        this.mVerticalThumbWidth = Math.max((int)n, (int)stateListDrawable.getIntrinsicWidth());
        this.mVerticalTrackWidth = Math.max((int)n, (int)drawable.getIntrinsicWidth());
        this.mHorizontalThumbHeight = Math.max((int)n, (int)stateListDrawable2.getIntrinsicWidth());
        this.mHorizontalTrackHeight = Math.max((int)n, (int)drawable2.getIntrinsicWidth());
        this.mScrollbarMinimumRange = n2;
        this.mMargin = n3;
        this.mVerticalThumbDrawable.setAlpha(255);
        this.mVerticalTrackDrawable.setAlpha(255);
        this.mShowHideAnimator.addListener((Animator.AnimatorListener)new AnimatorListener());
        this.mShowHideAnimator.addUpdateListener((ValueAnimator.AnimatorUpdateListener)new AnimatorUpdater());
        this.attachToRecyclerView(recyclerView);
    }

    private void cancelHide() {
        this.mRecyclerView.removeCallbacks(this.mHideRunnable);
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration((RecyclerView.ItemDecoration)this);
        this.mRecyclerView.removeOnItemTouchListener((RecyclerView.OnItemTouchListener)this);
        this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
        this.cancelHide();
    }

    private void drawHorizontalScrollbar(Canvas canvas) {
        int n = this.mRecyclerViewHeight;
        int n2 = this.mHorizontalThumbHeight;
        int n3 = n - n2;
        int n4 = this.mHorizontalThumbCenterX;
        int n5 = this.mHorizontalThumbWidth;
        int n6 = n4 - n5 / 2;
        this.mHorizontalThumbDrawable.setBounds(0, 0, n5, n2);
        this.mHorizontalTrackDrawable.setBounds(0, 0, this.mRecyclerViewWidth, this.mHorizontalTrackHeight);
        canvas.translate(0.0f, (float)n3);
        this.mHorizontalTrackDrawable.draw(canvas);
        canvas.translate((float)n6, 0.0f);
        this.mHorizontalThumbDrawable.draw(canvas);
        canvas.translate((float)(-n6), (float)(-n3));
    }

    private void drawVerticalScrollbar(Canvas canvas) {
        int n = this.mRecyclerViewWidth;
        int n2 = this.mVerticalThumbWidth;
        int n3 = n - n2;
        int n4 = this.mVerticalThumbCenterY;
        int n5 = this.mVerticalThumbHeight;
        int n6 = n4 - n5 / 2;
        this.mVerticalThumbDrawable.setBounds(0, 0, n2, n5);
        this.mVerticalTrackDrawable.setBounds(0, 0, this.mVerticalTrackWidth, this.mRecyclerViewHeight);
        if (this.isLayoutRTL()) {
            this.mVerticalTrackDrawable.draw(canvas);
            canvas.translate((float)this.mVerticalThumbWidth, (float)n6);
            canvas.scale(-1.0f, 1.0f);
            this.mVerticalThumbDrawable.draw(canvas);
            canvas.scale(1.0f, 1.0f);
            canvas.translate((float)(-this.mVerticalThumbWidth), (float)(-n6));
            return;
        }
        canvas.translate((float)n3, 0.0f);
        this.mVerticalTrackDrawable.draw(canvas);
        canvas.translate(0.0f, (float)n6);
        this.mVerticalThumbDrawable.draw(canvas);
        canvas.translate((float)(-n3), (float)(-n6));
    }

    private int[] getHorizontalRange() {
        int n;
        int[] arrn = this.mHorizontalRange;
        arrn[0] = n = this.mMargin;
        arrn[1] = this.mRecyclerViewWidth - n;
        return arrn;
    }

    private int[] getVerticalRange() {
        int n;
        int[] arrn = this.mVerticalRange;
        arrn[0] = n = this.mMargin;
        arrn[1] = this.mRecyclerViewHeight - n;
        return arrn;
    }

    private void horizontalScrollTo(float f) {
        int[] arrn = this.getHorizontalRange();
        float f2 = Math.max((float)arrn[0], (float)Math.min((float)arrn[1], (float)f));
        if (Math.abs((float)((float)this.mHorizontalThumbCenterX - f2)) < 2.0f) {
            return;
        }
        int n = this.scrollTo(this.mHorizontalDragX, f2, arrn, this.mRecyclerView.computeHorizontalScrollRange(), this.mRecyclerView.computeHorizontalScrollOffset(), this.mRecyclerViewWidth);
        if (n != 0) {
            this.mRecyclerView.scrollBy(n, 0);
        }
        this.mHorizontalDragX = f2;
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection((View)this.mRecyclerView) == 1;
    }

    private void resetHideDelay(int n) {
        this.cancelHide();
        this.mRecyclerView.postDelayed(this.mHideRunnable, (long)n);
    }

    private int scrollTo(float f, float f2, int[] arrn, int n, int n2, int n3) {
        int n4 = arrn[1] - arrn[0];
        if (n4 == 0) {
            return 0;
        }
        float f3 = (f2 - f) / (float)n4;
        int n5 = n - n3;
        int n6 = (int)(f3 * (float)n5);
        int n7 = n2 + n6;
        if (n7 < n5 && n7 >= 0) {
            return n6;
        }
        return 0;
    }

    private void setupCallbacks() {
        this.mRecyclerView.addItemDecoration((RecyclerView.ItemDecoration)this);
        this.mRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener)this);
        this.mRecyclerView.addOnScrollListener(this.mOnScrollListener);
    }

    private void verticalScrollTo(float f) {
        int[] arrn = this.getVerticalRange();
        float f2 = Math.max((float)arrn[0], (float)Math.min((float)arrn[1], (float)f));
        if (Math.abs((float)((float)this.mVerticalThumbCenterY - f2)) < 2.0f) {
            return;
        }
        int n = this.scrollTo(this.mVerticalDragY, f2, arrn, this.mRecyclerView.computeVerticalScrollRange(), this.mRecyclerView.computeVerticalScrollOffset(), this.mRecyclerViewHeight);
        if (n != 0) {
            this.mRecyclerView.scrollBy(0, n);
        }
        this.mVerticalDragY = f2;
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 == recyclerView) {
            return;
        }
        if (recyclerView2 != null) {
            this.destroyCallbacks();
        }
        this.mRecyclerView = recyclerView;
        if (this.mRecyclerView != null) {
            this.setupCallbacks();
        }
    }

    @SuppressLint(value={"SwitchIntDef"})
    void hide(int n) {
        switch (this.mAnimationState) {
            default: {
                return;
            }
            case 1: {
                this.mShowHideAnimator.cancel();
            }
            case 2: 
        }
        this.mAnimationState = 3;
        ValueAnimator valueAnimator = this.mShowHideAnimator;
        float[] arrf = new float[]{((Float)valueAnimator.getAnimatedValue()).floatValue(), 0.0f};
        valueAnimator.setFloatValues(arrf);
        this.mShowHideAnimator.setDuration((long)n);
        this.mShowHideAnimator.start();
    }

    boolean isPointInsideHorizontalThumb(float f, float f2) {
        int n;
        int n2;
        return f2 >= (float)(this.mRecyclerViewHeight - this.mHorizontalThumbHeight) && f >= (float)((n = this.mHorizontalThumbCenterX) - (n2 = this.mHorizontalThumbWidth) / 2) && f <= (float)(n + n2 / 2);
    }

    boolean isPointInsideVerticalThumb(float f, float f2) {
        int n;
        int n2;
        return (this.isLayoutRTL() ? f <= (float)(this.mVerticalThumbWidth / 2) : f >= (float)(this.mRecyclerViewWidth - this.mVerticalThumbWidth)) && f2 >= (float)((n = this.mVerticalThumbCenterY) - (n2 = this.mVerticalThumbHeight) / 2) && f2 <= (float)(n + n2 / 2);
    }

    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        if (this.mRecyclerViewWidth == this.mRecyclerView.getWidth() && this.mRecyclerViewHeight == this.mRecyclerView.getHeight()) {
            if (this.mAnimationState != 0) {
                if (this.mNeedVerticalScrollbar) {
                    this.drawVerticalScrollbar(canvas);
                }
                if (this.mNeedHorizontalScrollbar) {
                    this.drawHorizontalScrollbar(canvas);
                }
            }
            return;
        }
        this.mRecyclerViewWidth = this.mRecyclerView.getWidth();
        this.mRecyclerViewHeight = this.mRecyclerView.getHeight();
        this.setState(0);
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        boolean bl;
        block6 : {
            int n;
            block5 : {
                boolean bl2;
                block7 : {
                    n = this.mState;
                    if (n != 1) break block5;
                    boolean bl3 = this.isPointInsideVerticalThumb(motionEvent.getX(), motionEvent.getY());
                    bl2 = this.isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY());
                    int n2 = motionEvent.getAction();
                    bl = false;
                    if (n2 != 0) break block6;
                    if (bl3) break block7;
                    bl = false;
                    if (!bl2) break block6;
                }
                if (bl2) {
                    this.mDragState = 1;
                    this.mHorizontalDragX = (int)motionEvent.getX();
                } else {
                    this.mDragState = 2;
                    this.mVerticalDragY = (int)motionEvent.getY();
                }
                this.setState(2);
                return true;
                break block6;
            }
            bl = false;
            if (n == 2) {
                bl = true;
            }
        }
        return bl;
    }

    public void onRequestDisallowInterceptTouchEvent(boolean bl) {
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (this.mState == 0) {
            return;
        }
        if (motionEvent.getAction() == 0) {
            boolean bl = this.isPointInsideVerticalThumb(motionEvent.getX(), motionEvent.getY());
            boolean bl2 = this.isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY());
            if (bl || bl2) {
                if (bl2) {
                    this.mDragState = 1;
                    this.mHorizontalDragX = (int)motionEvent.getX();
                } else {
                    this.mDragState = 2;
                    this.mVerticalDragY = (int)motionEvent.getY();
                }
                this.setState(2);
                return;
            }
        } else {
            if (motionEvent.getAction() == 1 && this.mState == 2) {
                this.mVerticalDragY = 0.0f;
                this.mHorizontalDragX = 0.0f;
                this.setState(1);
                this.mDragState = 0;
                return;
            }
            if (motionEvent.getAction() == 2 && this.mState == 2) {
                this.show();
                if (this.mDragState == 1) {
                    this.horizontalScrollTo(motionEvent.getX());
                }
                if (this.mDragState == 2) {
                    this.verticalScrollTo(motionEvent.getY());
                }
            }
        }
    }

    protected void requestRedraw() {
        this.mRecyclerView.invalidate();
    }

    protected void setState(int n) {
        if (n == 2 && this.mState != 2) {
            this.mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
            this.cancelHide();
        }
        if (n == 0) {
            this.requestRedraw();
        } else {
            this.show();
        }
        if (this.mState == 2 && n != 2) {
            this.mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            this.resetHideDelay(1200);
        } else if (n == 1) {
            this.resetHideDelay(1500);
        }
        this.mState = n;
    }

    @SuppressLint(value={"SwitchIntDef"})
    public void show() {
        int n = this.mAnimationState;
        if (n != 0) {
            if (n != 3) {
                return;
            }
            this.mShowHideAnimator.cancel();
        }
        this.mAnimationState = 1;
        ValueAnimator valueAnimator = this.mShowHideAnimator;
        float[] arrf = new float[]{((Float)valueAnimator.getAnimatedValue()).floatValue(), 1.0f};
        valueAnimator.setFloatValues(arrf);
        this.mShowHideAnimator.setDuration(500L);
        this.mShowHideAnimator.setStartDelay(0L);
        this.mShowHideAnimator.start();
    }

    void updateScrollPosition(int n, int n2) {
        int n3;
        int n4;
        int n5 = this.mRecyclerView.computeVerticalScrollRange();
        int n6 = n5 - (n4 = this.mRecyclerViewHeight);
        boolean bl = n6 > 0 && n4 >= this.mScrollbarMinimumRange;
        this.mNeedVerticalScrollbar = bl;
        int n7 = this.mRecyclerView.computeHorizontalScrollRange();
        int n8 = this.mRecyclerViewWidth;
        boolean bl2 = n7 - n8 > 0 && n8 >= this.mScrollbarMinimumRange;
        this.mNeedHorizontalScrollbar = bl2;
        if (!this.mNeedVerticalScrollbar && !this.mNeedHorizontalScrollbar) {
            if (this.mState != 0) {
                this.setState(0);
            }
            return;
        }
        if (this.mNeedVerticalScrollbar) {
            int n9 = this.mVerticalThumbHeight = 2 * this.mVerticalThumbWidth;
            this.mVerticalThumbCenterY = (int)((float)(n9 / 2) + (float)(n2 * (n4 - n9)) / (float)n6);
        }
        if (this.mNeedHorizontalScrollbar) {
            float f = n;
            float f2 = n8;
            this.mHorizontalThumbCenterX = (int)(f2 * (f + f2 / 2.0f) / (float)n7);
            this.mHorizontalThumbWidth = Math.min((int)n8, (int)(n8 * n8 / n7));
        }
        if ((n3 = this.mState) == 0 || n3 == 1) {
            this.setState(1);
        }
    }

    private class AnimatorListener
    extends AnimatorListenerAdapter {
        private boolean mCanceled = false;

        private AnimatorListener() {
        }

        public void onAnimationCancel(Animator animator) {
            this.mCanceled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.mCanceled) {
                this.mCanceled = false;
                return;
            }
            if (((Float)FastScroller.this.mShowHideAnimator.getAnimatedValue()).floatValue() == 0.0f) {
                FastScroller fastScroller = FastScroller.this;
                fastScroller.mAnimationState = 0;
                fastScroller.setState(0);
                return;
            }
            FastScroller fastScroller = FastScroller.this;
            fastScroller.mAnimationState = 2;
            fastScroller.requestRedraw();
        }
    }

    private class AnimatorUpdater
    implements ValueAnimator.AnimatorUpdateListener {
        private AnimatorUpdater() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int n = (int)(255.0f * ((Float)valueAnimator.getAnimatedValue()).floatValue());
            FastScroller.this.mVerticalThumbDrawable.setAlpha(n);
            FastScroller.this.mVerticalTrackDrawable.setAlpha(n);
            FastScroller.this.requestRedraw();
        }
    }

}

