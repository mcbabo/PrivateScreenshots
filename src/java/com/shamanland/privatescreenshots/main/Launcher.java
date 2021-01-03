/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.KeyguardManager
 *  android.arch.core.util.Function
 *  android.arch.lifecycle.LifecycleOwner
 *  android.content.ComponentName
 *  android.content.Context
 *  android.content.Intent
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.content.pm.PackageManager
 *  android.content.res.ColorStateList
 *  android.content.res.Resources
 *  android.graphics.drawable.Drawable
 *  android.graphics.drawable.StateListDrawable
 *  android.media.projection.MediaProjectionManager
 *  android.net.Uri
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.Handler
 *  android.os.Parcelable
 *  android.os.SystemClock
 *  android.provider.Settings
 *  android.support.v4.app.DialogFragment
 *  android.support.v4.app.Fragment
 *  android.support.v4.app.FragmentManager
 *  android.support.v4.hardware.fingerprint.FingerprintManagerCompat
 *  android.support.v4.hardware.fingerprint.FingerprintManagerCompat$AuthenticationCallback
 *  android.support.v4.hardware.fingerprint.FingerprintManagerCompat$AuthenticationResult
 *  android.support.v4.hardware.fingerprint.FingerprintManagerCompat$CryptoObject
 *  android.support.v4.os.CancellationSignal
 *  android.support.v7.view.ActionMode
 *  android.support.v7.view.ActionMode$Callback
 *  android.support.v7.widget.GridLayoutManager
 *  android.support.v7.widget.GridLayoutManager$SpanSizeLookup
 *  android.support.v7.widget.RecyclerView
 *  android.support.v7.widget.RecyclerView$Adapter
 *  android.support.v7.widget.RecyclerView$AdapterDataObserver
 *  android.support.v7.widget.RecyclerView$LayoutManager
 *  android.text.TextUtils
 *  android.view.Menu
 *  android.view.MenuItem
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewStub
 *  android.widget.ImageView
 *  android.widget.PopupMenu
 *  android.widget.PopupMenu$OnMenuItemClickListener
 *  android.widget.TextView
 *  android.widget.Toast
 *  com.google.firebase.perf.FirebasePerformance
 *  com.google.firebase.perf.metrics.Trace
 *  java.io.File
 *  java.io.Serializable
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 */
package com.shamanland.privatescreenshots.main;

import android.app.Activity;
import android.app.KeyguardManager;
import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.ui.ConfirmationDialogFragment;
import com.shamanland.common.ui.ProgressDialogFragment;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.common.utils.Promise;
import com.shamanland.common.utils.Utils;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.ad.AdManager;
import com.shamanland.privatescreenshots.adapter.Adapter;
import com.shamanland.privatescreenshots.base.BaseActivity;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.overlay.OverlayMonitor;
import com.shamanland.privatescreenshots.overlay.OverlayService;
import com.shamanland.privatescreenshots.security.Protector;
import com.shamanland.privatescreenshots.security.SessionValidator;
import com.shamanland.privatescreenshots.settings.SettingsActivity;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.AppHelper;
import com.shamanland.privatescreenshots.utils.AppUtils;
import com.shamanland.privatescreenshots.utils.AsyncBitmapDecoder;
import com.shamanland.privatescreenshots.utils.DrawablesValidator;
import com.shamanland.privatescreenshots.utils.ProxyActivity;
import com.shamanland.privatescreenshots.view.FastScroller;
import com.shamanland.privatescreenshots.viewer.Viewer;
import com.shamanland.remoteconfig.RemoteConfig;
import com.shamanland.toaster.Toaster;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Launcher
extends BaseActivity
implements ActionMode.Callback,
ConfirmationDialogFragment.Listener,
Adapter.Client,
Storage.Listener,
AppHelper.Client {
    protected static final String LOG_TAG = "Launcher";
    protected Adapter adapter;
    protected LazyRef<Analytics> analytics;
    protected AppHelper appHelper;
    private CancellationSignal cancellationSignal;
    protected View confirmCredentials;
    protected boolean fastAction;
    protected boolean fileOpened;
    private FingerprintManagerCompat.AuthenticationCallback fingerprintCallback;
    protected boolean fingerprintError;
    protected ImageView fingerprintIcon;
    protected boolean fingerprintListening;
    private FingerprintManagerCompat fingerprintManager;
    protected TextView fingerprintMessage;
    protected MediaProjectionManager mpm;
    protected RecyclerView.AdapterDataObserver observer;
    protected LazyRef<OverlayMonitor> overlayMonitor;
    protected boolean paused;
    protected Intent projectionData;
    protected LazyRef<Protector> protector;
    protected RecyclerView recycler;
    protected LazyRef<RemoteConfig> remoteConfig;
    protected ActionMode selectionMode;
    protected boolean selfCancelled;
    protected LazyRef<SessionValidator> sessionValidator;
    protected LazyRef<SettingsManager> settingsManager;
    protected LazyRef<Storage> storage;
    protected View tutorial;

    private boolean canDrawOverlays() {
        try {
            boolean bl = Settings.canDrawOverlays((Context)this);
            return bl;
        }
        catch (Exception exception) {
            Crane.report(exception);
            return false;
        }
    }

    private void dismissMovingDialog() {
        Fragment fragment = this.getSupportFragmentManager().findFragmentByTag("cb7c4d42");
        if (fragment instanceof DialogFragment) {
            ((DialogFragment)fragment).dismissAllowingStateLoss();
        }
    }

    private FingerprintManagerCompat.AuthenticationCallback getFingerprintCallback() {
        if (this.fingerprintCallback == null) {
            this.fingerprintCallback = new FingerprintManagerCompat.AuthenticationCallback(){

                public void onAuthenticationError(int n, CharSequence charSequence) {
                    Launcher.this.onAuthenticationError(n, charSequence);
                }

                public void onAuthenticationFailed() {
                    Launcher.this.onAuthenticationFailed();
                }

                public void onAuthenticationHelp(int n, CharSequence charSequence) {
                    Launcher.this.onAuthenticationHelp(n, charSequence);
                }

                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult authenticationResult) {
                    Launcher.this.onAuthenticationSucceeded(authenticationResult);
                }
            };
        }
        return this.fingerprintCallback;
    }

    private SharedPreferences getPrefs() {
        return this.getSharedPreferences("35724177", 0);
    }

    private int getRateResult() {
        long l = this.getPrefs().getLong("114a2049", 0L);
        if (l > 0L) {
            return SystemClock.uptimeMillis() > l + this.remoteConfig.get().getLong("probably_rated_delay", 6000L);
        }
        return -1;
    }

    private void handleAskToRateConditions() {
        Trace trace = FirebasePerformance.startTrace((String)"59641684");
        if (!this.shouldAskToRate()) {
            trace.stop();
            return;
        }
        int n = this.getRateResult();
        if (n == 1) {
            this.analytics.get().logEvent("probably_rated");
            this.getPrefs().edit().putBoolean("8e591b54", true).apply();
            this.supportInvalidateOptionsMenu();
        } else if (n == 0) {
            this.analytics.get().logEvent("review_not_received");
            Toast.makeText((Context)this.getApplicationContext(), (int)2131689674, (int)0).show();
        } else if (this.isGoodTimeToAsk()) {
            String string2;
            long l = this.getPrefs().getLong("3e0503f1", 0L) LCMP 0L;
            boolean bl = false;
            if (l == false) {
                bl = true;
            }
            this.getPrefs().edit().putLong("3e0503f1", System.currentTimeMillis()).apply();
            String string3 = this.remoteConfig.get().getString("ask_to_rate_title", null);
            if (TextUtils.isEmpty((CharSequence)string3)) {
                string3 = this.getString(2131689515);
            }
            if (TextUtils.isEmpty((CharSequence)(string2 = this.remoteConfig.get().getString("ask_to_rate_message", null)))) {
                string2 = this.getString(2131689514);
            }
            new ConfirmationDialogFragment.Builder().setId("cea38a75").setTitle(string3).setMessage(string2).setPositiveText(this.getString(2131689634)).setNegativeText(this.getString(2131689672)).setUseNeverShow(bl ^ true).setPreventCancel(true).show(this.getSupportFragmentManager());
        }
        trace.stop();
    }

    private boolean hasMovingDialog() {
        return this.getSupportFragmentManager().findFragmentByTag("cb7c4d42") instanceof DialogFragment;
    }

    private void initTutorial(final View view) {
        view.findViewById(2131296358).setOnClickListener(new View.OnClickListener(){

            public void onClick(View view3) {
                Launcher.this.analytics.get().logEvent("faq", "privacy");
                View view2 = view.findViewById(2131296359);
                int n = view2.getVisibility() != 0 ? 0 : 8;
                view2.setVisibility(n);
            }
        });
        view.findViewById(2131296360).setOnClickListener(new View.OnClickListener(){

            public void onClick(View view3) {
                Launcher.this.analytics.get().logEvent("faq", "how_it_works");
                View view2 = view.findViewById(2131296361);
                int n = view2.getVisibility() != 0 ? 0 : 8;
                view2.setVisibility(n);
            }
        });
        view.findViewById(2131296362).setOnClickListener(new View.OnClickListener(){

            public void onClick(View view3) {
                Launcher.this.analytics.get().logEvent("faq", "how_to_use");
                View view2 = view.findViewById(2131296363);
                int n = view2.getVisibility() != 0 ? 0 : 8;
                view2.setVisibility(n);
            }
        });
    }

    private boolean isGoodTimeToAsk() {
        if (this.fileOpened) {
            long l = this.getPrefs().getLong("3e0503f1", 0L);
            long l2 = this.remoteConfig.get().getLong("ask_to_rate_interval", 86400000L);
            if (l <= 0L) {
                return true;
            }
            boolean bl = System.currentTimeMillis() > l + l2;
            if (bl) {
                double d = this.remoteConfig.get().getDouble("ask_to_rate_chance", 0.0);
                if (Math.random() < d) {
                    return true;
                }
                bl = false;
            }
            return bl;
        }
        return false;
    }

    private void navigateToMarket() {
        this.getPrefs().edit().putLong("114a2049", SystemClock.uptimeMillis()).apply();
        if (!Utils.openMarket((Context)this, 2131689526)) {
            this.analytics.get().logError("cant_open_market");
        }
    }

    private void resetAskToRateConditions() {
        this.fileOpened = false;
        this.getPrefs().edit().remove("114a2049").apply();
    }

    private void resumeListeningFingerprint() {
        if (this.fingerprintListening) {
            this.startListeningFingerprint();
        }
    }

    private boolean shouldAskToRate() {
        if (this.settingsManager.get().isAdFreePurchased()) {
            return false;
        }
        if (ConfirmationDialogFragment.isNeverAskAgain((Context)this, "cea38a75")) {
            return false;
        }
        boolean bl = this.getPrefs().getBoolean("8e591b54", false);
        boolean bl2 = (long)this.storage.get().getFilesCountRaw() > this.remoteConfig.get().getLong("stable_user_files_count", 10L);
        boolean bl3 = System.currentTimeMillis() > this.settingsManager.get().getAppInstalled() + this.remoteConfig.get().getLong("stable_user_lifetime", 86400000L);
        boolean bl4 = false;
        if (!bl) {
            bl4 = false;
            if (bl2) {
                bl4 = false;
                if (bl3) {
                    bl4 = true;
                }
            }
        }
        return bl4;
    }

    private void showMovingDialog() {
        if (!this.hasMovingDialog()) {
            ProgressDialogFragment.show(this.getSupportFragmentManager(), this.getString(2131689644), "cb7c4d42");
        }
    }

    @Override
    public File getThumb(File file) {
        return this.storage.get().getThumb(file);
    }

    protected void handleIntent(Intent intent) {
        String string2 = intent.getAction();
        if (string2 == null) {
            return;
        }
        int n = -1;
        int n2 = string2.hashCode();
        if (n2 != -1335908996) {
            if (n2 == -980409103 && string2.equals((Object)"9b959b25")) {
                n = 0;
            }
        } else if (string2.equals((Object)"d457d020")) {
            n = 1;
        }
        switch (n) {
            default: {
                return;
            }
            case 1: {
                this.analytics.get().logEvent("fast_action");
                this.toggle(true);
                return;
            }
            case 0: 
        }
        this.analytics.get().logEvent("stop_action");
        this.toggle(false);
    }

    @Override
    public boolean isPaused() {
        return this.paused;
    }

    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        int n = menuItem.getItemId();
        if (n != 2131296286) {
            if (n != 2131296325) {
                if (n != 2131296389) {
                    if (n != 2131296441) {
                        return false;
                    }
                    this.analytics.get().logEvent("share_selected", this.adapter.getSelectedCount());
                    if (this.adapter.getSelectedCount() > 1) {
                        this.share((Collection<File>)this.adapter.getSelected());
                    } else if (this.adapter.getSelectedCount() > 0) {
                        this.share((File)this.adapter.getSelected().get(0));
                    }
                    this.selectionMode.finish();
                    return false;
                }
                this.analytics.get().logEvent("move_selected", this.adapter.getSelectedCount());
                if (this.adapter.getSelectedCount() > 0) {
                    this.appHelper.moveToGallery((Collection<File>)this.adapter.getSelected());
                    return false;
                }
            } else {
                this.analytics.get().logEvent("delete_selected", this.adapter.getSelectedCount());
                if (this.adapter.getSelectedCount() > 1) {
                    if (ConfirmationDialogFragment.isNeverAskAgain((Context)this, "delete_selected")) {
                        this.onDeleteSelectedConfirmed();
                        return false;
                    }
                    ConfirmationDialogFragment.Builder builder = new ConfirmationDialogFragment.Builder().setId("delete_selected");
                    Object[] arrobject = new Object[]{this.adapter.getSelectedCount()};
                    builder.setTitle(this.getString(2131689575, arrobject)).setMessage(this.getString(2131689560)).setPositiveText(this.getString(2131689557)).setNegativeText(this.getString(17039360)).setUseNeverShow(true).show(this.getSupportFragmentManager());
                    return false;
                }
                if (this.adapter.getSelectedCount() > 0) {
                    this.onDeleteSingle((File)this.adapter.getSelected().get(0));
                    return false;
                }
            }
        } else {
            this.analytics.get().logEvent("select_all");
            this.adapter.selectAll();
        }
        return false;
    }

    protected void onActivityResult(int n, int n2, Intent intent) {
        super.onActivityResult(n, n2, intent);
        switch (n) {
            default: {
                return;
            }
            case 5: {
                if (n2 != -1) break;
                if (intent.getBooleanExtra("b3bcfd96", false)) {
                    Intent intent2 = new Intent((Context)this, OverlayService.class);
                    intent2.putExtra("0d1b1131", true);
                    this.startService(intent2);
                }
                if (!intent.getBooleanExtra("14bb6fc2", false)) break;
                this.refreshData();
                this.supportInvalidateOptionsMenu();
                return;
            }
            case 4: {
                if (n2 != -1) break;
                this.sessionValidator.get().onUnlocked();
                return;
            }
            case 2: {
                if (Build.VERSION.SDK_INT < 23) break;
                if (Settings.canDrawOverlays((Context)this)) {
                    this.startOverlay();
                    return;
                }
                this.analytics.get().logError("overlay_perm_no");
                Toaster.toast(2131689650);
                return;
            }
            case 1: {
                if (n2 == -1) {
                    this.projectionData = intent;
                    this.startOverlay();
                    return;
                }
                this.analytics.get().logError("projection_perm_no");
                Toaster.toast(2131689652);
            }
        }
    }

    @Override
    public void onAdMenuClicked(final int n, View view) {
        if (this.isPaused()) {
            return;
        }
        this.analytics.get().logEvent("ad_menu");
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

            public boolean onMenuItemClick(MenuItem menuItem) {
                if (Launcher.this.isPaused()) {
                    return true;
                }
                switch (menuItem.getItemId()) {
                    default: {
                        return false;
                    }
                    case 2131296355: {
                        Launcher.this.analytics.get().logEvent("buy_ad_free", "launcher");
                        Launcher.this.appHelper.buyAdFree();
                        return true;
                    }
                    case 2131296354: 
                }
                Launcher.this.analytics.get().logEvent("ad_menu_hide_now");
                Launcher.this.adapter.hideAd(n);
                return true;
            }
        });
        popupMenu.inflate(2131558400);
        popupMenu.show();
    }

    protected void onAuthenticationError(int n, CharSequence charSequence) {
        if (this.selfCancelled) {
            return;
        }
        this.analytics.get().logEvent("fingerprint_auth_error", n);
        this.fingerprintError = true;
        TextView textView = this.fingerprintMessage;
        if (textView != null) {
            textView.setText(charSequence);
        }
        this.setFingerprintIcon(2131099724, true);
        this.stopListeningFingerprint();
    }

    protected void onAuthenticationFailed() {
        TextView textView;
        this.analytics.get().logEvent("fingerprint_auth_failed");
        ImageView imageView = this.fingerprintIcon;
        if (imageView != null) {
            imageView.setImageTintList(ColorStateList.valueOf((int)this.getResources().getColor(2131099724)));
            AppUtils.animateShaking((View)this.fingerprintIcon).once((LifecycleOwner)this, new Function<Void, Void>(){

                public Void apply(Void void_) {
                    if (Launcher.this.fingerprintIcon != null) {
                        Launcher.this.fingerprintIcon.setImageTintList(ColorStateList.valueOf((int)Launcher.this.getResources().getColor(2131099777)));
                    }
                    return null;
                }
            });
        }
        if ((textView = this.fingerprintMessage) != null) {
            textView.setText(2131689593);
        }
    }

    protected void onAuthenticationHelp(int n, CharSequence charSequence) {
        TextView textView = this.fingerprintMessage;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    protected void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult authenticationResult) {
        this.analytics.get().logEvent("fingerprint_auth_success");
        this.sessionValidator.get().onUnlocked();
        this.setFingerprintIcon(2131099792, true);
        TextView textView = this.fingerprintMessage;
        if (textView != null) {
            textView.setText(null);
        }
        new Handler().post(new Runnable(){

            public void run() {
                if (Launcher.this.isPaused()) {
                    return;
                }
                Launcher.this.onConfirmCredentials();
            }
        });
    }

    protected void onConfirmCredentials() {
        this.analytics.get().logEvent("confirm_credentials");
        if (this.storage.get().isContentLocked()) {
            KeyguardManager keyguardManager = (KeyguardManager)this.getSystemService("keyguard");
            if (keyguardManager != null) {
                Object[] arrobject = new Object[]{this.getString(2131689512)};
                this.startActivityForResult(keyguardManager.createConfirmDeviceCredentialIntent((CharSequence)this.getString(2131689573, arrobject), (CharSequence)this.getString(2131689670)), 4);
                return;
            }
            this.analytics.get().logError("keyguard_null");
            Toaster.toast(2131689708);
            return;
        }
        this.refreshData();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        RecyclerView.AdapterDataObserver adapterDataObserver;
        Adapter adapter;
        AppHelper appHelper;
        super.onCreate(bundle);
        DrawablesValidator.ensureDrawablesValid((Activity)this);
        this.analytics = this.getComponentLocator().getAnalytics();
        this.protector = this.getComponentLocator().getProtector();
        this.settingsManager = this.getComponentLocator().getSettingsManager();
        this.sessionValidator = this.getComponentLocator().getSessionValidator();
        this.storage = this.getComponentLocator().getStorage();
        this.remoteConfig = this.getComponentLocator().getRemoteConfig();
        this.overlayMonitor = this.getComponentLocator().getOverlayMonitor();
        this.appHelper = appHelper = new AppHelper(this, this, this.settingsManager, this.storage, this.analytics);
        this.appHelper.onCreate(bundle, 3000L);
        this.analytics = this.getComponentLocator().getAnalytics();
        this.mpm = (MediaProjectionManager)this.getSystemService("media_projection");
        if (bundle != null) {
            this.fastAction = bundle.getBoolean("d457d020");
            this.fileOpened = bundle.getBoolean("b40ca4c0");
            this.fingerprintListening = bundle.getBoolean("085fc723");
        }
        this.setContentView(2131492864);
        this.setTitle(AppUtils.getStyledText((Context)this, 2131755016, 2131689512, new Object[0]));
        this.tutorial = this.findViewById(2131296493);
        this.confirmCredentials = this.findViewById(2131296313);
        int n = this.getResources().getInteger(2131361801);
        this.adapter = adapter = new Adapter((Context)this, this, this.analytics, this.getComponentLocator().getAsyncBitmapDecoder(), this.getComponentLocator().getAdManager(), this.getComponentLocator().getRemoteConfig(), n);
        GridLayoutManager gridLayoutManager = new GridLayoutManager((Context)this, n, 1, false);
        gridLayoutManager.setSpanSizeLookup(this.adapter.getGridSpanSizeLookup());
        this.recycler = (RecyclerView)this.findViewById(2131296411);
        StateListDrawable stateListDrawable = (StateListDrawable)this.getDrawable(2131230874);
        StateListDrawable stateListDrawable2 = (StateListDrawable)this.getDrawable(2131230855);
        StateListDrawable stateListDrawable3 = (StateListDrawable)this.getDrawable(2131230874);
        StateListDrawable stateListDrawable4 = (StateListDrawable)this.getDrawable(2131230855);
        if (stateListDrawable != null && stateListDrawable2 != null && stateListDrawable3 != null && stateListDrawable4 != null) {
            new FastScroller(this.recycler, stateListDrawable, (Drawable)stateListDrawable2, stateListDrawable3, (Drawable)stateListDrawable4, this.getResources().getDimensionPixelSize(2131165322), this.getResources().getDimensionPixelSize(2131165324), this.getResources().getDimensionPixelOffset(2131165323));
        }
        this.recycler.setAdapter((RecyclerView.Adapter)this.adapter);
        this.recycler.setLayoutManager((RecyclerView.LayoutManager)gridLayoutManager);
        Adapter adapter2 = this.adapter;
        this.observer = adapterDataObserver = new RecyclerView.AdapterDataObserver(){

            public void onChanged() {
                Launcher.this.updateState();
            }

            public void onItemRangeChanged(int n, int n2) {
                Launcher.this.updateState();
            }

            public void onItemRangeChanged(int n, int n2, Object object) {
                Launcher.this.updateState();
            }

            public void onItemRangeInserted(int n, int n2) {
                Launcher.this.updateState();
            }

            public void onItemRangeMoved(int n, int n2, int n3) {
                Launcher.this.updateState();
            }

            public void onItemRangeRemoved(int n, int n2) {
                Launcher.this.updateState();
            }
        };
        adapter2.registerAdapterDataObserver(adapterDataObserver);
        this.handleIntent(this.getIntent());
    }

    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuItem menuItem = menu.add(0, 2131296286, 0, 2131689511);
        menuItem.setIcon(2131230853);
        menuItem.setShowAsAction(1);
        MenuItem menuItem2 = menu.add(0, 2131296441, 0, 2131689689);
        menuItem2.setIcon(2131230854);
        menuItem2.setShowAsAction(1);
        menu.add(0, 2131296389, 0, 2131689638).setShowAsAction(0);
        menu.add(0, 2131296325, 0, 2131689557).setShowAsAction(0);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0, 2131296459, 0, 2131689694);
        menuItem.setShowAsAction(2);
        menuItem.setIcon(2131230844);
        if (this.settingsManager.get().isPrivatePhotos()) {
            MenuItem menuItem2 = menu.add(0, 2131296470, 0, 2131689703);
            menuItem2.setShowAsAction(2);
            menuItem2.setIcon(2131230841);
        }
        menu.add(0, 2131296440, 0, 2131689686);
        if (this.storage.get().getFilesCount() > 0) {
            menu.add(0, 2131296334, 0, 2131689566);
        }
        if (Build.VERSION.SDK_INT < 26) {
            menu.add(0, 2131296371, 0, 2131689617);
        }
        if (this.shouldAskToRate()) {
            menu.add(0, 2131296410, 0, 2131689671);
        }
        this.appHelper.onCreateOptionsMenu(menu);
        return true;
    }

    public void onDeleteSelectedConfirmed() {
        if (this.adapter.hasSelected()) {
            this.storage.get().remove((Collection<File>)this.adapter.getSelected());
        }
    }

    public void onDeleteSingle(File file) {
        AppUtils.showDeleteDialog((Context)this, this.getSupportFragmentManager(), file, "delete_single");
    }

    public void onDeleteSingleConfirmed(File file) {
        this.storage.get().remove(file);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.appHelper.onDestroy();
        this.adapter.unregisterAdapterDataObserver(this.observer);
    }

    public void onDestroyActionMode(ActionMode actionMode) {
        this.adapter.selectNone();
    }

    @Override
    public void onEndMoving(int n, int n2, File file) {
        this.dismissMovingDialog();
        this.appHelper.onEndMoving(n, n2, file);
    }

    @Override
    public void onFileAdded(File file) {
        this.adapter.onFileAdded(file);
        if (this.storage.get().getFilesCount() < 2) {
            this.supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onFileMenuClicked(final File file, View view) {
        if (this.isPaused()) {
            return;
        }
        this.analytics.get().logEvent("item_menu");
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

            public boolean onMenuItemClick(MenuItem menuItem) {
                if (Launcher.this.isPaused()) {
                    return true;
                }
                int n = menuItem.getItemId();
                if (n != 2131296325) {
                    if (n != 2131296332) {
                        if (n != 2131296389) {
                            if (n != 2131296441) {
                                return false;
                            }
                            Launcher.this.analytics.get().logEvent("item_menu_clicked", "share");
                            Launcher.this.share(file);
                            return true;
                        }
                        Launcher.this.analytics.get().logEvent("item_menu_clicked", "move");
                        Launcher.this.appHelper.moveToGallery((Collection<File>)Collections.singleton((Object)file));
                        return true;
                    }
                    Launcher.this.analytics.get().logEvent("item_menu_clicked", "detalis");
                    Launcher.this.showDetailsDialog(file);
                    return true;
                }
                Launcher.this.analytics.get().logEvent("item_menu_clicked", "delete");
                if (ConfirmationDialogFragment.isNeverAskAgain((Context)Launcher.this, "delete_single")) {
                    Launcher.this.onDeleteSingleConfirmed(file);
                    return true;
                }
                Launcher.this.onDeleteSingle(file);
                return true;
            }
        });
        popupMenu.inflate(2131558401);
        popupMenu.show();
    }

    @Override
    public void onFileRemoved(File file) {
        this.adapter.onFileRemoved(file);
        if (this.storage.get().getFilesCount() < 2) {
            this.supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onFilesRemoved(Collection<File> collection) {
        this.adapter.onFilesRemoved(collection);
        if (this.storage.get().getFilesCount() < 2) {
            this.supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onItemClicked(int n) {
        if (this.isPaused()) {
            return;
        }
        this.analytics.get().logEvent("item_clicked");
        if (this.adapter.hasSelected()) {
            this.adapter.toggleSelection(n);
            return;
        }
        File file = this.adapter.getFile(n);
        if (file != null) {
            this.openFile(file);
            return;
        }
        this.analytics.get().logError("file_null");
    }

    @Override
    public void onItemLongClicked(int n) {
        if (this.isPaused()) {
            return;
        }
        this.analytics.get().logEvent("item_long_click");
        this.adapter.toggleSelection(n);
    }

    @Override
    public void onMoveToGalleryConfirmed() {
        ActionMode actionMode = this.selectionMode;
        if (actionMode != null) {
            actionMode.finish();
            this.selectionMode = null;
        }
    }

    @Override
    public void onNegative(String string2, Bundle bundle) {
        int n = string2.hashCode() == -1285986917 && string2.equals((Object)"cea38a75") ? 0 : -1;
        if (n != 0) {
            return;
        }
        this.analytics.get().logEvent("ask_to_rate_positive");
        this.navigateToMarket();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.handleIntent(intent);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.isPaused()) {
            return false;
        }
        switch (menuItem.getItemId()) {
            default: {
                break;
            }
            case 2131296470: {
                this.analytics.get().logEvent("take_photo");
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (intent.resolveActivity(this.getPackageManager()) == null) break;
                this.startActivityForResult(intent, 6);
                break;
            }
            case 2131296459: {
                this.analytics.get().logEvent("start_stop");
                this.toggle(false);
                break;
            }
            case 2131296440: {
                this.analytics.get().logEvent("settings");
                this.startActivityForResult(new Intent((Context)this, SettingsActivity.class), 5);
                break;
            }
            case 2131296410: {
                this.analytics.get().logEvent("rate_app");
                this.navigateToMarket();
                break;
            }
            case 2131296371: {
                this.analytics.get().logEvent("install_shortcut");
                Intent intent = new Intent((Context)this, Launcher.class);
                intent.setAction("d457d020");
                Utils.installShortcut((Context)this, intent, 2131689694, 2131623936);
                break;
            }
            case 2131296334: {
                this.analytics.get().logEvent("disk_usage");
                ConfirmationDialogFragment.Builder builder = new ConfirmationDialogFragment.Builder().setId("348c6729").setTitle(this.getString(2131689566));
                Object[] arrobject = new Object[]{this.storage.get().getFilesCount(), Utils.formatBytesSize(this.storage.get().getFilesSize())};
                builder.setMessage(this.getString(2131689576, arrobject)).setPositiveText(this.getString(2131689655)).show(this.getSupportFragmentManager());
            }
        }
        if (this.appHelper.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.paused = true;
        this.storage.get().removeListener(this);
        this.stopListeningFingerprint();
    }

    @Override
    public void onPositive(String string2, Bundle bundle) {
        if (this.isPaused()) {
            return;
        }
        int n = -1;
        int n2 = string2.hashCode();
        if (n2 != -63220388) {
            if (n2 == 60346031 && string2.equals((Object)"delete_selected")) {
                n = 1;
            }
        } else if (string2.equals((Object)"delete_single")) {
            n = 0;
        }
        switch (n) {
            default: {
                this.appHelper.onPositive(string2, bundle);
                return;
            }
            case 1: {
                this.analytics.get().logEvent("delete_selected_confirm", this.adapter.getSelectedCount());
                this.onDeleteSelectedConfirmed();
                return;
            }
            case 0: 
        }
        this.analytics.get().logEvent("delete_single_confirm_launcher");
        this.onDeleteSingleConfirmed((File)bundle.getSerializable("23b0b557"));
    }

    protected void onPostResume() {
        super.onPostResume();
        if (this.storage.get().isMoving()) {
            this.showMovingDialog();
        } else {
            this.dismissMovingDialog();
            this.handleAskToRateConditions();
        }
        this.resetAskToRateConditions();
    }

    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public void onPurchasesErrors(int n) {
    }

    @Override
    public void onRefreshRequired() {
        this.refreshData();
    }

    public void onRequestPermissionsResult(int n, String[] arrstring, int[] arrn) {
        this.appHelper.onRequestPermissionsResult(n, arrstring, arrn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.protector.get().isLocked()) {
            this.sessionValidator.get().onUnlocked();
        }
        this.paused = false;
        this.storage.get().addListener(this);
        this.refreshData();
        this.supportInvalidateOptionsMenu();
        this.resumeListeningFingerprint();
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.appHelper.onSaveInstanceState(bundle);
        bundle.putBoolean("d457d020", this.fastAction);
        bundle.putBoolean("b40ca4c0", this.fileOpened);
        bundle.putBoolean("085fc723", this.fingerprintListening);
    }

    @Override
    public void onStartMoving() {
        this.showMovingDialog();
    }

    protected void openFile(File file) {
        if (this.settingsManager.get().isUseExternalViewer()) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(this.storage.get().getUri(file));
            intent.addFlags(1);
            intent.addFlags(268435456);
            if (!Utils.tryStartActivity((Context)this, intent, 2131689525)) {
                this.analytics.get().logError("open_external");
                this.settingsManager.get().setUseExternalViewer(false);
                return;
            }
            this.fileOpened = true;
            return;
        }
        if (!Utils.tryStartActivity((Context)this, Viewer.createIntent((Context)this, file), 2131689525)) {
            this.analytics.get().logError("open_viewer");
            this.settingsManager.get().setUseExternalViewer(true);
            this.settingsManager.get().setChooseViewerAvailable(true);
            return;
        }
        this.fileOpened = true;
    }

    protected void refreshData() {
        this.adapter.setFiles(this.storage.get().getFiles());
    }

    protected void setFingerprintIcon(int n, boolean bl) {
        ImageView imageView = this.fingerprintIcon;
        if (imageView != null) {
            Runnable runnable;
            imageView.setImageTintList(ColorStateList.valueOf((int)this.getResources().getColor(n)));
            Object object = this.fingerprintIcon.getTag();
            if (object instanceof Runnable) {
                runnable = (Runnable)object;
            } else {
                runnable = new Runnable(){

                    public void run() {
                        Launcher.this.fingerprintIcon.setImageTintList(ColorStateList.valueOf((int)Launcher.this.getResources().getColor(2131099777)));
                    }
                };
                this.fingerprintIcon.setTag((Object)runnable);
            }
            this.fingerprintIcon.removeCallbacks(runnable);
            if (bl) {
                this.fingerprintIcon.postDelayed(runnable, 1000L);
            }
        }
    }

    public void share(File file) {
        AppUtils.share((Context)this, this.storage.get(), file);
    }

    public void share(Collection<File> collection) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        intent.setType("image/png");
        ArrayList arrayList = new ArrayList();
        for (File file : collection) {
            arrayList.add((Object)this.storage.get().getUri(file));
        }
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
        Utils.tryStartActivity((Context)this, intent, 2131689708, 2131689569);
    }

    protected void showDetailsDialog(File file) {
        AppUtils.showDetailsDialog((Context)this, this.getSupportFragmentManager(), file);
    }

    protected void startListeningFingerprint() {
        if (this.fingerprintManager == null) {
            this.fingerprintManager = FingerprintManagerCompat.from((Context)this.getApplicationContext());
        }
        this.fingerprintListening = true;
        if (this.cancellationSignal == null) {
            block5 : {
                this.cancellationSignal = new CancellationSignal();
                this.selfCancelled = false;
                TextView textView = this.fingerprintMessage;
                if (textView != null) {
                    textView.setText(null);
                }
                try {
                    this.fingerprintManager.authenticate(null, 0, this.cancellationSignal, this.getFingerprintCallback(), null);
                }
                catch (RuntimeException runtimeException) {
                    Crane.report(runtimeException);
                    this.stopListeningFingerprint();
                    TextView textView2 = this.fingerprintMessage;
                    if (textView2 == null) break block5;
                    textView2.setText(2131689708);
                }
            }
            this.setFingerprintIcon(2131099777, false);
        }
    }

    protected void startOverlay() {
        if (Build.VERSION.SDK_INT >= 23 && !this.canDrawOverlays()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(this.getPackageName());
            Utils.tryStartActivityForResult((Activity)this, new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse((String)stringBuilder.toString())), 2, 2131689568);
            return;
        }
        if (this.projectionData == null) {
            Utils.tryStartActivityForResult((Activity)this, this.mpm.createScreenCaptureIntent(), 1, 2131689567);
            return;
        }
        Intent intent = new Intent((Context)this, OverlayService.class);
        intent.putExtra("1a318656", (Parcelable)this.projectionData);
        if (!ProxyActivity.startServiceTricky((Context)this, intent)) {
            Toast.makeText((Context)this.getApplicationContext(), (int)2131689708, (int)0).show();
        }
        if (this.fastAction) {
            this.finish();
        }
    }

    protected void stopListeningFingerprint() {
        this.fingerprintListening = false;
        this.fingerprintError = false;
        CancellationSignal cancellationSignal = this.cancellationSignal;
        if (cancellationSignal != null) {
            this.selfCancelled = true;
            cancellationSignal.cancel();
            this.cancellationSignal = null;
        }
    }

    protected void toggle(boolean bl) {
        this.fastAction = bl;
        if (this.overlayMonitor.get().isActive()) {
            this.overlayMonitor.get().stop();
            if (bl) {
                this.finish();
                return;
            }
        } else {
            this.startOverlay();
        }
    }

    protected void updateState() {
        if (this.storage.get().isContentLocked()) {
            View view = this.confirmCredentials;
            if (view instanceof ViewStub) {
                if (this.fingerprintManager == null) {
                    this.fingerprintManager = FingerprintManagerCompat.from((Context)this.getApplicationContext());
                }
                boolean bl = this.fingerprintManager.isHardwareDetected() && this.fingerprintManager.hasEnrolledFingerprints();
                this.confirmCredentials = ((ViewStub)this.confirmCredentials).inflate();
                this.confirmCredentials.findViewById(2131296312).setOnClickListener(new View.OnClickListener(){

                    public void onClick(View view) {
                        if (Launcher.this.isPaused()) {
                            return;
                        }
                        Launcher.this.onConfirmCredentials();
                    }
                });
                TextView textView = (TextView)this.confirmCredentials.findViewById(2131296314);
                this.fingerprintIcon = (ImageView)this.confirmCredentials.findViewById(2131296348);
                this.fingerprintMessage = (TextView)this.confirmCredentials.findViewById(2131296349);
                if (bl) {
                    this.fingerprintIcon.setVisibility(0);
                    this.fingerprintMessage.setVisibility(0);
                    textView.setText(2131689552);
                } else {
                    this.fingerprintIcon.setVisibility(4);
                    this.fingerprintMessage.setVisibility(4);
                    textView.setText(2131689551);
                }
                this.fingerprintIcon.setOnClickListener(new View.OnClickListener(){

                    public void onClick(View view) {
                        if (Launcher.this.isPaused()) {
                            return;
                        }
                        Launcher.this.analytics.get().logEvent("fingerprint_icon_clicked");
                        Launcher.this.startListeningFingerprint();
                    }
                });
            } else {
                view.setVisibility(0);
            }
            this.startListeningFingerprint();
            this.tutorial.setVisibility(8);
            this.recycler.setVisibility(8);
        } else {
            this.stopListeningFingerprint();
            this.confirmCredentials.setVisibility(8);
            if (this.adapter.getItemCount() < 1) {
                View view = this.tutorial;
                if (view instanceof ViewStub) {
                    this.tutorial = ((ViewStub)view).inflate();
                    this.initTutorial(this.tutorial);
                } else {
                    view.setVisibility(0);
                }
                this.recycler.setVisibility(8);
            } else {
                this.tutorial.setVisibility(8);
                this.recycler.setVisibility(0);
            }
        }
        if (this.adapter.getSelectedCount() > 0) {
            if (this.selectionMode == null) {
                this.selectionMode = this.startSupportActionMode((ActionMode.Callback)this);
            }
            ActionMode actionMode = this.selectionMode;
            Object[] arrobject = new Object[]{this.adapter.getSelectedCount()};
            actionMode.setTitle(AppUtils.getStyledText((Context)this, 2131755016, 2131689581, arrobject));
            return;
        }
        this.onMoveToGalleryConfirmed();
    }

}

