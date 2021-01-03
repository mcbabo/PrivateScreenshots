/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.Notification
 *  android.app.Notification$Builder
 *  android.app.PendingIntent
 *  android.app.Service
 *  android.arch.lifecycle.Lifecycle
 *  android.arch.lifecycle.Lifecycle$State
 *  android.arch.lifecycle.LifecycleOwner
 *  android.arch.lifecycle.LifecycleRegistry
 *  android.arch.lifecycle.LiveData
 *  android.arch.lifecycle.Observer
 *  android.content.BroadcastReceiver
 *  android.content.ComponentName
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.content.res.Configuration
 *  android.content.res.Resources
 *  android.graphics.Point
 *  android.hardware.SensorManager
 *  android.net.Uri
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.IBinder
 *  android.os.Parcelable
 *  android.util.DisplayMetrics
 *  android.view.LayoutInflater
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.View$OnTouchListener
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.view.WindowManager
 *  android.view.WindowManager$LayoutParams
 *  android.widget.Toast
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.NoSuchFieldError
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.util.Locale
 *  java.util.concurrent.Executor
 */
package com.shamanland.privatescreenshots.overlay;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import com.shamanland.analytics.Analytics;
import com.shamanland.analytics.AnalyticsProperty;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.main.Launcher;
import com.shamanland.privatescreenshots.overlay.OverlayMonitor;
import com.shamanland.privatescreenshots.saver.Saver;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.shooter.Shooter;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.ErrorParser;
import com.shamanland.privatescreenshots.utils.NotificationChannelsHelper;
import com.shamanland.privatescreenshots.utils.ProxyActivity;
import com.shamanland.privatescreenshots.utils.ShakeDetector;
import com.shamanland.toaster.Toaster;
import java.util.Locale;
import java.util.concurrent.Executor;

public class OverlayService
extends Service
implements LifecycleOwner {
    static final String LOG_TAG = "OverlayService";
    LazyRef<Analytics> analytics;
    View container;
    protected final Runnable containerSetVisibleTrigger = new Runnable(){

        public void run() {
            if (OverlayService.this.container != null) {
                OverlayService.this.container.setVisibility(0);
            }
        }
    };
    Intent initialIntent;
    int initialOrientation;
    private final LifecycleRegistry lifecycle = new LifecycleRegistry((LifecycleOwner)this);
    LazyRef<OverlayMonitor> overlayMonitor;
    BroadcastReceiver screenStateReceiver;
    ShakeDetector sd;
    LazyRef<SettingsManager> settingsManager;
    Shooter shooter;
    private final Runnable shooterTakeTrigger = new Runnable(){

        public void run() {
            if (OverlayService.this.shooter != null) {
                OverlayService.this.shooter.take();
            }
        }
    };
    private boolean startForegroundCalled;
    LazyRef<Storage> storage;
    LazyRef<Executor> threadPool;
    protected WindowManager wm;

    @SuppressLint(value={"RtlHardcoded"})
    private WindowManager.LayoutParams createLayoutParams(int n, int n2) {
        int n3 = Build.VERSION.SDK_INT >= 26 ? 2038 : 2003;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, n3, 40, -3);
        layoutParams.gravity = 51;
        layoutParams.x = n;
        layoutParams.y = n2;
        return layoutParams;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private void destroyScreenStateReceiver() {
        var1_1 = this.screenStateReceiver;
        if (var1_1 == null) return;
        this.unregisterReceiver(var1_1);
lbl5: // 2 sources:
        do {
            this.screenStateReceiver = null;
            return;
            break;
        } while (true);
        {
            catch (Throwable var3_2) {
            }
            catch (Exception var2_3) {}
            {
                Crane.report(var2_3);
                ** continue;
            }
        }
        this.screenStateReceiver = null;
        throw var3_2;
    }

    @SuppressLint(value={"ClickableViewAccessibility", "InflateParams"})
    private void initContainer() {
        this.wm = (WindowManager)this.getSystemService("window");
        if (this.wm == null) {
            this.analytics.get().logError("window_manager_null");
            Toaster.toast(2131689708);
            return;
        }
        if (this.container != null) {
            this.analytics.get().logError("overlay_init_container_exists");
            return;
        }
        this.container = LayoutInflater.from((Context)this).inflate(2131492954, null);
        if (this.container == null) {
            this.analytics.get().logError("overlay_cant_inflate");
            Toaster.toast(2131689708);
            return;
        }
        this.overlayMonitor.get().setView(this.container);
        this.container.setAlpha(this.settingsManager.get().getButtonAlpha());
        DisplayMetrics displayMetrics = this.container.getResources().getDisplayMetrics();
        final int n = displayMetrics.widthPixels;
        final int n2 = displayMetrics.heightPixels;
        Point point = this.settingsManager.get().getButtonPosition(this.initialOrientation);
        if (point == null) {
            point = new Point(n * 95 / 100, n2 * 20 / 100);
        }
        this.container.findViewById(2131296469).setOnTouchListener(new View.OnTouchListener(){

            @SuppressLint(value={"ClickableViewAccessibility"})
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (OverlayService.this.container == null) {
                    OverlayService.this.analytics.get().logError("overlay_take_touch_null");
                    return false;
                }
                if (motionEvent.getAction() != 0) {
                    return false;
                }
                OverlayService.this.performTake("touch");
                return true;
            }
        });
        this.container.findViewById(2131296335).setOnTouchListener(new View.OnTouchListener(){
            float downX;
            float downY;
            int maxX;
            int maxY;
            int originX;
            int originY;

            /*
             * Unable to fully structure code
             * Enabled aggressive block sorting
             * Lifted jumps to return sites
             */
            public boolean onTouch(View var1_1, MotionEvent var2_2) {
                if (OverlayService.this.container == null) {
                    OverlayService.this.analytics.get().logError(new Object[]{"overlay_drag_touch_null"});
                    return false;
                }
                switch (var2_2.getAction()) {
                    default: {
                        return false;
                    }
                    case 2: {
                        var5_3 = false;
                        ** GOTO lbl12
                    }
                    case 1: 
                    case 3: {
                        var5_3 = true;
lbl12: // 2 sources:
                        var6_4 = OverlayService.this.container.getLayoutParams();
                        if (var6_4 instanceof WindowManager.LayoutParams == false) return true;
                        var7_5 = (int)(var2_2.getRawX() - this.downX);
                        var8_6 = (int)(var2_2.getRawY() - this.downY);
                        var9_7 = (WindowManager.LayoutParams)var6_4;
                        var9_7.x = Math.min((int)Math.max((int)0, (int)(var7_5 + this.originX)), (int)this.maxX);
                        var9_7.y = Math.min((int)Math.max((int)0, (int)(var8_6 + this.originY)), (int)this.maxY);
                        OverlayService.this.wm.updateViewLayout(OverlayService.this.container, (ViewGroup.LayoutParams)var9_7);
                        if (var5_3 == false) return true;
                        if (var9_7.x < n / 3) {
                            var10_8 = new StringBuilder();
                            var10_8.append("");
                            var10_8.append("l");
                            var13_9 = var10_8.toString();
                        } else if (var9_7.x > 2 * n / 3) {
                            var24_10 = new StringBuilder();
                            var24_10.append("");
                            var24_10.append("r");
                            var13_9 = var24_10.toString();
                        } else {
                            var27_11 = new StringBuilder();
                            var27_11.append("");
                            var27_11.append("c");
                            var13_9 = var27_11.toString();
                        }
                        if (var9_7.y < n2 / 3) {
                            var14_12 = new StringBuilder();
                            var14_12.append(var13_9);
                            var14_12.append("t");
                            var17_13 = var14_12.toString();
                        } else if (var9_7.y > 2 * n2 / 3) {
                            var18_14 = new StringBuilder();
                            var18_14.append(var13_9);
                            var18_14.append("b");
                            var17_13 = var18_14.toString();
                        } else {
                            var21_15 = new StringBuilder();
                            var21_15.append(var13_9);
                            var21_15.append("c");
                            var17_13 = var21_15.toString();
                        }
                        if (OverlayService.this.initialOrientation == 1) {
                            OverlayService.this.analytics.get().setProperty(AnalyticsProperty.PROPERTY_11, "over_but_port", var17_13);
                        } else {
                            OverlayService.this.analytics.get().setProperty(AnalyticsProperty.PROPERTY_12, "over_but_land", var17_13);
                        }
                        OverlayService.this.analytics.get().logEvent("overlay_drag_finished");
                        OverlayService.this.settingsManager.get().setButtonPosition(OverlayService.this.initialOrientation, var9_7.x, var9_7.y);
                        return true;
                    }
                    case 0: 
                }
                this.downX = var2_2.getRawX();
                this.downY = var2_2.getRawY();
                var3_16 = OverlayService.this.container.getLayoutParams();
                if (var3_16 instanceof WindowManager.LayoutParams) {
                    var4_17 = (WindowManager.LayoutParams)var3_16;
                    this.originX = var4_17.x;
                    this.originY = var4_17.y;
                    this.maxX = n - OverlayService.this.container.getWidth();
                    this.maxY = n2 - OverlayService.this.container.getHeight();
                    return true;
                }
                OverlayService.this.analytics.get().logError(new Object[]{"overlay_drag_wrong_lp"});
                Toaster.toast(2131689708);
                return false;
            }
        });
        this.wm.addView(this.container, (ViewGroup.LayoutParams)this.createLayoutParams(point.x, point.y));
    }

    private void initScreenStateReceiver() {
        BroadcastReceiver broadcastReceiver;
        this.destroyScreenStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.screenStateReceiver = broadcastReceiver = new BroadcastReceiver(){

            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.SCREEN_ON".equals((Object)intent.getAction())) {
                    OverlayService.this.onScreenOn();
                    return;
                }
                if ("android.intent.action.SCREEN_OFF".equals((Object)intent.getAction())) {
                    OverlayService.this.onScreenOff();
                }
            }
        };
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initShakeDetector() {
        int n = this.settingsManager.get().getShakeShot();
        if (n == 0) {
            return;
        }
        SensorManager sensorManager = (SensorManager)this.getSystemService("sensor");
        if (sensorManager != null) {
            this.sd = new ShakeDetector(new ShakeDetector.Listener(){

                @Override
                public void hearShake() {
                    OverlayService.this.performTake("shake");
                }
            });
            switch (n) {
                default: {
                    break;
                }
                case 3: {
                    this.sd.setSensitivity(15);
                    break;
                }
                case 2: {
                    this.sd.setSensitivity(13);
                    break;
                }
                case 1: {
                    this.sd.setSensitivity(11);
                }
            }
            this.sd.start(sensorManager);
        }
    }

    private void initShooter(Intent intent) {
        Shooter shooter;
        this.shooter = shooter = new Shooter((Context)this, intent, this.storage, this.analytics, this.threadPool);
        this.shooter.setListener(new Shooter.Listener(){

            /*
             * Unable to fully structure code
             * Enabled aggressive block sorting
             * Lifted jumps to return sites
             */
            @Override
            public void onNewStatus(Saver.Status var1_1) {
                if (OverlayService.this.container != null) {
                    var7_2 = var1_1 == Saver.Status.OOM ? 500L : 50L;
                    OverlayService.this.container.postDelayed(OverlayService.this.containerSetVisibleTrigger, var7_2);
                }
                switch (9.$SwitchMap$com$shamanland$privatescreenshots$saver$Saver$Status[var1_1.ordinal()]) {
                    default: {
                        ** GOTO lbl13
                    }
                    case 2: 
                    case 3: {
                        var2_3 = new StringBuilder();
                        var2_3.append("overlay_shooter_status_restart_");
                        var2_3.append(var1_1.name().toLowerCase(Locale.US));
                        Crane.log(var2_3.toString());
                        OverlayService.this.restart();
lbl13: // 2 sources:
                        var5_4 = OverlayService.this.analytics.get();
                        var6_5 = new Object[]{var1_1.name().toLowerCase(Locale.US)};
                        var5_4.logEvent("shooter_status", var6_5);
                        Toaster.toast(var1_1.stringId);
                    }
                    case 1: 
                }
            }

            @Override
            public void onStopped() {
                Crane.log("overlay_shooter_stopped");
                OverlayService.this.stopMe();
            }
        });
    }

    private void startForeground() {
        Notification.Builder builder = new Notification.Builder((Context)this).setSmallIcon(2131230844).setContentTitle((CharSequence)this.getString(2131689685)).setContentText((CharSequence)this.getString(2131689704)).setContentIntent(PendingIntent.getActivity((Context)this, (int)0, (Intent)new Intent((Context)this, Launcher.class).setAction("9b959b25"), (int)0)).setColor(this.getResources().getColor(2131099672));
        if (Build.VERSION.SDK_INT >= 26) {
            builder.setChannelId(NotificationChannelsHelper.getDefaultChannel(this.getApplicationContext()));
        }
        this.startForeground(4097, builder.build());
        this.startForegroundCalled = true;
    }

    public static void stop(Context context) {
        if (!ProxyActivity.startServiceTricky(context, new Intent("12875bd698fea319", null, context, OverlayService.class))) {
            Toast.makeText((Context)context, (int)2131689708, (int)0).show();
        }
    }

    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.initialOrientation != configuration.orientation) {
            Crane.log("overlay_cfg_changed_restart");
            this.restart();
        }
    }

    public void onCreate() {
        super.onCreate();
        Crane.log("overlay_create_begin");
        this.startForeground();
        this.lifecycle.markState(Lifecycle.State.RESUMED);
        ComponentLocator componentLocator = ComponentLocator.getInstance((Context)this);
        this.settingsManager = componentLocator.getSettingsManager();
        this.storage = componentLocator.getStorage();
        this.threadPool = componentLocator.getThreadPool();
        this.analytics = componentLocator.getAnalytics();
        this.overlayMonitor = componentLocator.getOverlayMonitor();
        this.initShakeDetector();
        this.initScreenStateReceiver();
        this.settingsManager.get().getButtonAlphaObservable().observe((LifecycleOwner)this, (Observer)new Observer<Float>(){

            public void onChanged(Float f) {
                if (OverlayService.this.container != null && f != null) {
                    OverlayService.this.container.setAlpha(f.floatValue());
                }
            }
        });
        Crane.log("overlay_create_end");
    }

    public void onDestroy() {
        View view;
        Shooter shooter;
        super.onDestroy();
        this.lifecycle.markState(Lifecycle.State.DESTROYED);
        this.destroyScreenStateReceiver();
        ShakeDetector shakeDetector = this.sd;
        if (shakeDetector != null) {
            shakeDetector.stop();
            this.sd = null;
        }
        if ((shooter = this.shooter) != null) {
            shooter.setListener(null);
            this.shooter.destroy();
            this.shooter = null;
        }
        if ((view = this.container) != null) {
            try {
                if (this.wm != null) {
                    this.wm.removeView(view);
                }
            }
            catch (Exception exception) {
                if (ErrorParser.findViewNotAttached(exception.getMessage())) {
                    this.analytics.get().logError("overlay_view_not_attached");
                }
                Crane.report(exception);
            }
            this.container = null;
            this.overlayMonitor.get().setView(this.container);
        }
    }

    protected void onScreenOff() {
        Crane.log("overlay_screen_off");
        ShakeDetector shakeDetector = this.sd;
        if (shakeDetector != null) {
            shakeDetector.stop();
        }
    }

    protected void onScreenOn() {
        SensorManager sensorManager;
        Crane.log("overlay_screen_on");
        if (this.sd != null && (sensorManager = (SensorManager)this.getSystemService("sensor")) != null) {
            this.sd.start(sensorManager);
        }
    }

    public int onStartCommand(Intent intent, int n, int n2) {
        Intent intent2;
        if (intent != null && "12875bd698fea319".equals((Object)intent.getAction())) {
            Crane.log("overlay_start_cmd_stop");
            this.stopMe();
            return 2;
        }
        if (intent != null && intent.getBooleanExtra("0d1b1131", false)) {
            Crane.log("overlay_start_cmd_restart");
            this.restart();
            return 2;
        }
        if (this.shooter == null && intent != null && (intent2 = (Intent)intent.getParcelableExtra("1a318656")) != null) {
            this.initialIntent = intent;
            this.initialOrientation = this.getResources().getConfiguration().orientation;
            Crane.log("overlay_start_cmd_init_shooter");
            try {
                this.initShooter(intent2);
            }
            catch (Exception exception) {
                Crane.log("overlay_start_cmd_init_shooter_fail");
                Crane.report(exception);
                Toaster.toast(2131689708);
            }
        }
        if (this.shooter != null) {
            Crane.log("overlay_start_cmd_init_cont");
            try {
                this.initContainer();
                return 2;
            }
            catch (Exception exception) {
                Crane.log("overlay_start_cmd_init_cont_fail");
                this.stopMe();
                if (ErrorParser.findNotAllowedSystemAlertWindow(exception.getMessage())) {
                    this.analytics.get().logError("overlay_not_all_sys_alert");
                    Toaster.toast(2131689568);
                    return 2;
                }
                if (ErrorParser.findPermissionDeniedWindow(exception.getMessage())) {
                    this.analytics.get().logError("no_perm_window_type");
                    Toaster.toast(2131689568);
                    return 2;
                }
                Crane.report(exception);
                Toaster.toast(2131689708);
                return 2;
            }
        }
        Crane.log("overlay_start_cmd_shooter_null");
        this.stopMe();
        return 2;
    }

    protected void performTake(String string2) {
        View view = this.container;
        if (view == null) {
            return;
        }
        if (view.getVisibility() != 0) {
            return;
        }
        this.analytics.get().logEvent("shoot", string2);
        this.container.setVisibility(4);
        this.container.postDelayed(this.shooterTakeTrigger, 100L);
    }

    void restart() {
        this.stopMe();
        Intent intent = this.initialIntent;
        if (intent != null) {
            this.startService(intent);
        }
    }

    protected void stopMe() {
        if (!this.startForegroundCalled) {
            this.startForeground();
        }
        this.stopSelf();
    }

}

