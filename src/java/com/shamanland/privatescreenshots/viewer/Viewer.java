/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.animation.Animator
 *  android.animation.Animator$AnimatorListener
 *  android.content.Context
 *  android.content.Intent
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.graphics.drawable.Drawable
 *  android.graphics.drawable.GradientDrawable
 *  android.graphics.drawable.GradientDrawable$Orientation
 *  android.os.Bundle
 *  android.support.v4.app.FragmentManager
 *  android.support.v4.view.PagerAdapter
 *  android.support.v4.view.ViewPager
 *  android.support.v4.view.ViewPager$OnPageChangeListener
 *  android.support.v4.view.ViewPagerEx
 *  android.support.v7.app.ActionBar
 *  android.support.v7.widget.Toolbar
 *  android.view.Menu
 *  android.view.MenuItem
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.View$OnSystemUiVisibilityChangeListener
 *  android.view.ViewPropertyAnimator
 *  android.view.Window
 *  com.google.android.gms.ads.AdListener
 *  com.google.android.gms.ads.AdRequest
 *  java.io.File
 *  java.io.Serializable
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 */
package com.shamanland.privatescreenshots.viewer;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPagerEx;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.ui.ConfirmationDialogFragment;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.BuildConfig;
import com.shamanland.privatescreenshots.ad.AdManager;
import com.shamanland.privatescreenshots.ad.InterstitialHolder;
import com.shamanland.privatescreenshots.ad.InterstitialWrapper;
import com.shamanland.privatescreenshots.base.BaseActivity;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.AdHelper;
import com.shamanland.privatescreenshots.utils.AppHelper;
import com.shamanland.privatescreenshots.utils.AppUtils;
import com.shamanland.privatescreenshots.utils.AsyncBitmapDecoder;
import com.shamanland.privatescreenshots.utils.DefaultAnimatorListener;
import com.shamanland.privatescreenshots.viewer.PageAdapter;
import com.shamanland.toaster.Toaster;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Viewer
extends BaseActivity
implements ViewPager.OnPageChangeListener,
ConfirmationDialogFragment.Listener,
Storage.Listener {
    protected static final String LOG_TAG = "Viewer";
    protected LazyRef<AdManager> adManager;
    protected int adShownTimes;
    protected PageAdapter adapter;
    protected LazyRef<Analytics> analytics;
    protected AppHelper appHelper;
    protected InterstitialWrapper interstitialAd;
    protected int lastAdIndex;
    protected boolean needToShowSorryAboutThatAd;
    protected boolean paused;
    protected LazyRef<SettingsManager> settingsManager;
    protected LazyRef<Storage> storage;
    protected ViewPagerEx viewPager;

    public static Intent createIntent(Context context, File file) {
        Intent intent = new Intent(context, Viewer.class);
        String string2 = file != null ? file.getAbsolutePath() : null;
        intent.putExtra("file", string2);
        return intent;
    }

    private void loadInterstitialAd() {
        if (!this.settingsManager.get().isAdFreeAvailable()) {
            this.interstitialAd.loadAd();
        }
    }

    private void onCreateUnsafe(Bundle bundle) {
        this.interstitialAd = this.getComponentLocator().getInterstitialHolder().get().getAd((Context)this, BuildConfig.INTERSTITIAL_AD_UNIT_ID, AdHelper.buildAdRequest());
        this.interstitialAd.setAdListener(new AdListener(){

            public void onAdClosed() {
                Viewer.this.onInterstitialAdClosed();
            }
        });
        this.loadInterstitialAd();
        this.setContentView(2131492866);
        this.setSupportActionBar((Toolbar)this.findViewById(2131296484));
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable((Drawable)new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{1711276032, 0}));
        }
        this.viewPager = (ViewPagerEx)this.findViewById(2131296499);
        final View view = this.findViewById(2131296293);
        view.setBackground((Drawable)new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{1711276032, 0}));
        this.findViewById(2131296300).setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                if (Viewer.this.isPaused()) {
                    return;
                }
                File file = Viewer.this.getCurrentFile();
                if (file != null) {
                    Viewer.this.share(file);
                    return;
                }
                Viewer.this.analytics.get().logError("share_file_null");
            }
        });
        this.findViewById(2131296301).setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                if (Viewer.this.isPaused()) {
                    return;
                }
                File file = Viewer.this.getCurrentFile();
                if (file != null) {
                    Viewer.this.showDetailsDialog(file);
                    return;
                }
                Viewer.this.analytics.get().logError("details_file_null");
            }
        });
        List<File> list = this.storage.get().getFiles();
        List<File> list2 = this.storage.get().getThumbs(list);
        int n = -1;
        String string2 = this.getIntent().getStringExtra("file");
        if (string2 != null) {
            n = list.indexOf((Object)new File(string2));
        }
        if (bundle != null) {
            this.lastAdIndex = bundle.getInt("61ba5303", n);
            this.adShownTimes = bundle.getInt("0d97147d", 0);
        } else {
            this.lastAdIndex = n;
            this.adShownTimes = 0;
        }
        this.adapter = new PageAdapter(this.getComponentLocator().getAsyncBitmapDecoder());
        this.adapter.setFiles(list, list2);
        this.adapter.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                if (Viewer.this.isPaused()) {
                    return;
                }
                Viewer.this.toggleFullScreen();
            }
        });
        this.viewPager.setAdapter((PagerAdapter)this.adapter);
        if (n >= 0) {
            this.viewPager.setCurrentItem(n, false);
        }
        this.interstitialAd.incSkipped();
        final DefaultAnimatorListener defaultAnimatorListener = new DefaultAnimatorListener(){

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(8);
            }
        };
        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){

            public void onSystemUiVisibilityChange(int n) {
                if (Viewer.this.isPaused()) {
                    return;
                }
                if ((n & 2) != 0) {
                    ActionBar actionBar2 = actionBar;
                    if (actionBar2 != null) {
                        actionBar2.hide();
                    }
                    view.setVisibility(0);
                    view.setAlpha(1.0f);
                    view.animate().alpha(0.0f).setListener(defaultAnimatorListener).start();
                    return;
                }
                ActionBar actionBar3 = actionBar;
                if (actionBar3 != null) {
                    actionBar3.show();
                }
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.animate().alpha(1.0f).setListener(null).start();
            }
        });
    }

    private void showInterstitial() {
        if (this.interstitialAd.isLoaded()) {
            this.lastAdIndex = this.viewPager.getCurrentItem();
            this.adShownTimes = 1 + this.adShownTimes;
            this.analytics.get().logEvent("ad_interstitial");
            this.interstitialAd.show();
            return;
        }
        this.analytics.get().logEvent("ad_interstitial_not_loaded");
        this.loadInterstitialAd();
    }

    private void showSorryAboutThatAd() {
        this.needToShowSorryAboutThatAd = false;
        this.getPrefs().edit().putBoolean("54b23fc5", true).apply();
        ConfirmationDialogFragment.Builder builder = new ConfirmationDialogFragment.Builder().setId("54b23fc5").setTitle(this.getString(2131689692));
        Object[] arrobject = new Object[]{this.getString(2131689512)};
        builder.setMessage(this.getString(2131689583, arrobject)).setPositiveText(this.getString(2131689706)).setNegativeText(this.getString(2131689613)).setPreventCancel(true).show(this.getSupportFragmentManager());
    }

    public File getCurrentFile() {
        int n = this.viewPager.getCurrentItem();
        if (n >= 0 && n < this.adapter.getCount()) {
            return this.adapter.getItem((int)n).file;
        }
        return null;
    }

    protected SharedPreferences getPrefs() {
        return this.getSharedPreferences("e422778e", 0);
    }

    @Override
    public boolean isPaused() {
        return this.paused;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        AppHelper appHelper;
        super.onCreate(bundle);
        this.analytics = this.getComponentLocator().getAnalytics();
        this.adManager = this.getComponentLocator().getAdManager();
        this.settingsManager = this.getComponentLocator().getSettingsManager();
        this.storage = this.getComponentLocator().getStorage();
        this.appHelper = appHelper = new AppHelper(this, null, this.settingsManager, this.storage, this.analytics);
        this.appHelper.onCreate(bundle);
        try {
            this.onCreateUnsafe(bundle);
            return;
        }
        catch (Exception exception) {
            Crane.report(exception);
            this.settingsManager.get().setChooseViewerAvailable(true);
            this.settingsManager.get().setUseExternalViewer(true);
            this.finish();
            Toaster.toast(2131689525);
            this.analytics.get().logError("viewer_crashed");
            return;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 2131296389, 0, 2131689638);
        menu.add(0, 2131296325, 0, 2131689557);
        return true;
    }

    public void onDeleteSingle(File file) {
        AppUtils.showDeleteDialog((Context)this, this.getSupportFragmentManager(), file, "b0db0732");
    }

    public void onDeleteSingleConfirmed(File file) {
        this.storage.get().remove(file);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.appHelper.onDestroy();
        this.interstitialAd.setAdListener(null);
    }

    @Override
    public void onEndMoving(int n, int n2, File file) {
        this.appHelper.onEndMoving(n, n2, file);
    }

    @Override
    public void onFileAdded(File file) {
        this.refreshAdapter();
    }

    @Override
    public void onFileRemoved(File file) {
        this.refreshAdapter();
    }

    @Override
    public void onFilesRemoved(Collection<File> collection) {
        this.refreshAdapter();
    }

    protected void onInterstitialAdClosed() {
        if (!this.getPrefs().getBoolean("54b23fc5", false)) {
            this.needToShowSorryAboutThatAd = true;
        }
        this.loadInterstitialAd();
    }

    @Override
    public void onNegative(String string2, Bundle bundle) {
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.isPaused()) {
            return true;
        }
        int n = menuItem.getItemId();
        if (n != 16908332) {
            if (n != 2131296325) {
                if (n != 2131296389) {
                    return super.onOptionsItemSelected(menuItem);
                }
                File file = this.getCurrentFile();
                if (file != null) {
                    this.analytics.get().logEvent("move_to_gallery_viewer");
                    this.appHelper.moveToGallery((Collection<File>)Collections.singleton((Object)file));
                    return true;
                }
                this.analytics.get().logError("move_file_null");
                return true;
            }
            File file = this.getCurrentFile();
            if (file != null) {
                if (ConfirmationDialogFragment.isNeverAskAgain((Context)this, "b0db0732")) {
                    this.onDeleteSingleConfirmed(file);
                    return true;
                }
                this.onDeleteSingle(file);
                return true;
            }
            this.analytics.get().logError("delete_file_null");
            return true;
        }
        this.finish();
        return true;
    }

    public void onPageScrollStateChanged(int n) {
    }

    public void onPageScrolled(int n, float f, int n2) {
    }

    public void onPageSelected(int n) {
        if (this.isPaused()) {
            return;
        }
        if (this.adManager.get().shouldShowInterstitial(this.interstitialAd, this.adShownTimes, this.viewPager.getCurrentItem(), this.lastAdIndex)) {
            this.showInterstitial();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.paused = true;
        this.storage.get().removeListener(this);
        ViewPagerEx viewPagerEx = this.viewPager;
        if (viewPagerEx != null) {
            viewPagerEx.removeOnPageChangeListener((ViewPager.OnPageChangeListener)this);
        }
    }

    @Override
    public void onPositive(String string2, Bundle bundle) {
        if (this.isPaused()) {
            return;
        }
        int n = -1;
        int n2 = string2.hashCode();
        if (n2 != -361328268) {
            if (n2 == 1602129330 && string2.equals((Object)"b0db0732")) {
                n = 0;
            }
        } else if (string2.equals((Object)"54b23fc5")) {
            n = 1;
        }
        switch (n) {
            default: {
                this.appHelper.onPositive(string2, bundle);
                return;
            }
            case 1: {
                this.analytics.get().logEvent("buy_ad_free", "viewer");
                this.appHelper.buyAdFree();
                return;
            }
            case 0: 
        }
        this.analytics.get().logEvent("delete_single_confirm_viewer");
        this.onDeleteSingleConfirmed((File)bundle.getSerializable("23b0b557"));
    }

    protected void onPostResume() {
        super.onPostResume();
        if (this.needToShowSorryAboutThatAd) {
            this.showSorryAboutThatAd();
        }
    }

    public void onRequestPermissionsResult(int n, String[] arrstring, int[] arrn) {
        super.onRequestPermissionsResult(n, arrstring, arrn);
        this.appHelper.onRequestPermissionsResult(n, arrstring, arrn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused = false;
        this.storage.get().addListener(this);
        this.viewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener)this);
        if (this.adapter.getCount() < 1) {
            this.finish();
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.appHelper.onSaveInstanceState(bundle);
        bundle.putInt("61ba5303", this.lastAdIndex);
        bundle.putInt("0d97147d", this.adShownTimes);
    }

    @Override
    public void onStartMoving() {
    }

    protected void refreshAdapter() {
        List<File> list = this.storage.get().getFiles();
        List<File> list2 = this.storage.get().getThumbs(list);
        if (list.size() > 0) {
            this.adapter.setFiles(list, list2);
            return;
        }
        this.finish();
    }

    protected void share(File file) {
        AppUtils.share((Context)this, this.storage.get(), file);
    }

    protected void showDetailsDialog(File file) {
        AppUtils.showDetailsDialog((Context)this, this.getSupportFragmentManager(), file);
    }

    public void toggleFullScreen() {
        int n = 2048 ^ (2 ^ (4 ^ this.getWindow().getDecorView().getSystemUiVisibility()));
        this.getWindow().getDecorView().setSystemUiVisibility(n);
    }

}

