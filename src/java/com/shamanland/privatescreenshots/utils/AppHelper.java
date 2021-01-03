/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.Intent
 *  android.content.ServiceConnection
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.Handler
 *  android.support.v4.app.FragmentManager
 *  android.view.Menu
 *  android.view.MenuItem
 *  com.android.billingclient.api.BillingClient
 *  com.android.billingclient.api.BillingClient$Builder
 *  com.android.billingclient.api.BillingClientStateListener
 *  com.android.billingclient.api.BillingFlowParams
 *  com.android.billingclient.api.BillingFlowParams$Builder
 *  com.android.billingclient.api.Purchase
 *  com.android.billingclient.api.Purchase$PurchasesResult
 *  com.android.billingclient.api.PurchasesUpdatedListener
 *  java.io.File
 *  java.io.Serializable
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.reflect.Field
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 */
package com.shamanland.privatescreenshots.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.ui.ConfirmationDialogFragment;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.copier.Copier;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.base.BaseActivity;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.toaster.Toaster;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AppHelper
implements BillingClientStateListener,
PurchasesUpdatedListener {
    protected static final String LOG_TAG = "AppHelper";
    protected final BaseActivity activity;
    protected String adFreePurchaseToken;
    protected final LazyRef<Analytics> analytics;
    protected BillingClient billingClient;
    protected final Client client;
    protected boolean connectionStarted;
    protected final LazyRef<SettingsManager> settingsManager;
    protected final LazyRef<Storage> storage;
    protected ArrayList<File> toBeMoved;

    public AppHelper(BaseActivity baseActivity, Client client, LazyRef<SettingsManager> lazyRef, LazyRef<Storage> lazyRef2, LazyRef<Analytics> lazyRef3) {
        this.activity = baseActivity;
        this.client = client;
        this.settingsManager = lazyRef;
        this.storage = lazyRef2;
        this.analytics = lazyRef3;
    }

    private void handlePurchases(int n, List<Purchase> list) {
        if (n == 0 && list != null) {
            Client client;
            boolean bl = false;
            for (Purchase purchase : list) {
                if (!"1c4b529c".equals((Object)purchase.getSku())) continue;
                this.adFreePurchaseToken = purchase.getPurchaseToken();
                bl = true;
            }
            if (this.settingsManager.get().setAdFreePurchased(bl) && (client = this.client) != null) {
                client.onRefreshRequired();
            }
        }
    }

    private void onMoveToGalleryConfirmed(Collection<File> collection) {
        if (Build.VERSION.SDK_INT >= 23 && this.activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            this.toBeMoved = new ArrayList(collection);
            this.activity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 300);
            return;
        }
        if (this.storage.get().moveToGallery(collection)) {
            this.analytics.get().logEvent("move_to_gallery_confirmed", collection.size());
            Client client = this.client;
            if (client != null) {
                client.onMoveToGalleryConfirmed();
                return;
            }
        } else {
            this.analytics.get().logError("move_to_gallery_false");
            Toaster.toast(2131689708);
        }
    }

    public void buyAdFree() {
        if (this.billingClient.isReady()) {
            if (this.billingClient.launchBillingFlow((Activity)this.activity, BillingFlowParams.newBuilder().setSku("1c4b529c").setType("inapp").build()) != 0) {
                Toaster.toast(2131689601);
                return;
            }
        } else {
            Toaster.toast(2131689602);
        }
    }

    protected SharedPreferences getPrefs() {
        return this.activity.getSharedPreferences("f5993083", 0);
    }

    public void moveToGallery(Collection<File> collection) {
        if (this.getPrefs().getBoolean("ffcbbda9", true)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("219c8cb7", (Serializable)new ArrayList(collection));
            new ConfirmationDialogFragment.Builder().setId("move_to_gallery_acknowledge").setTitle(this.activity.getString(2131689616)).setMessage(this.activity.getString(2131689639)).setPositiveText(this.activity.getString(2131689614)).setNegativeText(this.activity.getString(17039360)).setExtras(bundle).show(this.activity.getSupportFragmentManager());
            return;
        }
        if (ConfirmationDialogFragment.isNeverAskAgain((Context)this.activity, "move_to_gallery")) {
            this.onMoveToGalleryConfirmed(collection);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("219c8cb7", (Serializable)new ArrayList(collection));
        ConfirmationDialogFragment.Builder builder = new ConfirmationDialogFragment.Builder().setId("move_to_gallery").setTitle(this.activity.getString(2131689638));
        BaseActivity baseActivity = this.activity;
        int n = collection.size() > 1 ? 2131689640 : 2131689641;
        builder.setMessage(baseActivity.getString(n)).setPositiveText(this.activity.getString(2131689637)).setNegativeText(this.activity.getString(17039360)).setUseNeverShow(true).setExtras(bundle).show(this.activity.getSupportFragmentManager());
    }

    public void onBillingServiceDisconnected() {
        this.analytics.get().logError("billing_disconnected");
    }

    public void onBillingSetupFinished(int n) {
        if (n == 0) {
            Purchase.PurchasesResult purchasesResult = this.billingClient.queryPurchases("inapp");
            this.handlePurchases(purchasesResult.getResponseCode(), (List<Purchase>)purchasesResult.getPurchasesList());
            return;
        }
        Analytics analytics = this.analytics.get();
        Object[] arrobject = new Object[1];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("billing_setup_");
        stringBuilder.append(n);
        arrobject[0] = stringBuilder.toString();
        analytics.logError(arrobject);
    }

    public void onCreate(Bundle bundle) {
        this.onCreate(bundle, 0L);
    }

    public void onCreate(Bundle bundle, long l) {
        if (bundle != null) {
            this.toBeMoved = (ArrayList)bundle.getSerializable("f75099fb");
        }
        this.billingClient = BillingClient.newBuilder((Context)this.activity).setListener((PurchasesUpdatedListener)this).build();
        new Handler().postDelayed(new Runnable(){

            public void run() {
                if (AppHelper.this.activity.isPaused()) {
                    return;
                }
                AppHelper.this.billingClient.startConnection((BillingClientStateListener)AppHelper.this);
                AppHelper.this.connectionStarted = true;
            }
        }, l);
    }

    public void onCreateOptionsMenu(Menu menu) {
    }

    public void onDestroy() {
        Field[] arrfield = this.billingClient.getClass().getDeclaredFields();
        int n = arrfield.length;
        for (int i = 0; i < n; ++i) {
            Field field = arrfield[i];
            if (field.getType() != ServiceConnection.class) continue;
            field.setAccessible(true);
            ServiceConnection serviceConnection = (ServiceConnection)field.get((Object)this.billingClient);
            field.set((Object)this.billingClient, null);
            if (serviceConnection == null) continue;
            try {
                if (!this.connectionStarted) continue;
                this.activity.getApplicationContext().unbindService(serviceConnection);
            }
            catch (Exception exception) {
                if (exception.getMessage() != null && exception.getMessage().contains((CharSequence)"ervice not registered")) {
                    this.analytics.get().logError("apphelper_destroy_srv_not_reg");
                    break;
                }
                Crane.report(exception);
                break;
            }
            continue;
        }
        this.billingClient.endConnection();
    }

    public void onEndMoving(int n, int n2, File file) {
        int n3;
        int n4;
        if (n2 < 1) {
            n4 = 2131689591;
            n3 = 2131689580;
        } else if (n2 < n) {
            n4 = 2131689691;
            n3 = 2131689582;
        } else if (n2 > 1) {
            n4 = 2131689590;
            n3 = 2131689579;
        } else {
            n4 = 2131689589;
            n3 = 2131689578;
        }
        if (ConfirmationDialogFragment.isNeverAskAgain((Context)this.activity, "move_to_gallery_result")) {
            Toaster.toast(n4);
            return;
        }
        ConfirmationDialogFragment.Builder builder = new ConfirmationDialogFragment.Builder().setId("move_to_gallery_result").setTitle(this.activity.getString(n4));
        BaseActivity baseActivity = this.activity;
        Object[] arrobject = new Object[]{file.getAbsolutePath()};
        ConfirmationDialogFragment.Builder builder2 = builder.setMessage(baseActivity.getString(n3, arrobject)).setPositiveText(this.activity.getString(2131689604)).setUseNeverShow(true);
        if (Copier.hasClipboard((Context)this.activity)) {
            builder2.setNegativeText(this.activity.getString(2131689554)).setNegativeIntent(Copier.createCopyIntent((Context)this.activity, file.getAbsolutePath())).setNegativeIntentService(true);
        }
        builder2.show(this.activity.getSupportFragmentManager());
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        menuItem.getItemId();
        return false;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public void onPositive(String var1_1, Bundle var2_2) {
        block7 : {
            block6 : {
                var3_3 = var1_1.hashCode();
                if (var3_3 == -1029192295) break block6;
                if (var3_3 != -406864196 || !var1_1.equals((Object)"move_to_gallery")) ** GOTO lbl-1000
                var4_4 = 1;
                break block7;
            }
            if (var1_1.equals((Object)"move_to_gallery_acknowledge")) {
                var4_4 = 0;
            } else lbl-1000: // 2 sources:
            {
                var4_4 = -1;
            }
        }
        switch (var4_4) {
            default: {
                return;
            }
            case 1: {
                this.analytics.get().logEvent("move_to_gallery_confirm");
                this.onMoveToGalleryConfirmed((Collection<File>)((Collection)var2_2.getSerializable("219c8cb7")));
                return;
            }
            case 0: 
        }
        this.analytics.get().logEvent("move_to_gallery_acknowledge_confirm");
        this.getPrefs().edit().putBoolean("ffcbbda9", false).apply();
        this.moveToGallery((Collection<File>)((Collection)var2_2.getSerializable("219c8cb7")));
    }

    public void onPurchasesUpdated(int n, List<Purchase> list) {
        this.handlePurchases(n, list);
        if (n != 0) {
            Client client = this.client;
            if (client != null) {
                client.onPurchasesErrors(n);
                return;
            }
        } else {
            Analytics analytics = this.analytics.get();
            Object[] arrobject = new Object[1];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("billing_updated_");
            stringBuilder.append(n);
            arrobject[0] = stringBuilder.toString();
            analytics.logError(arrobject);
        }
    }

    public void onRequestPermissionsResult(int n, String[] arrstring, int[] arrn) {
        boolean bl;
        block4 : {
            int n2 = arrn.length;
            for (int i = 0; i < n2; ++i) {
                if (arrn[i] == 0) continue;
                bl = false;
                break block4;
            }
            bl = true;
        }
        if (n == 300) {
            if (bl) {
                ArrayList<File> arrayList = this.toBeMoved;
                if (arrayList != null && arrayList.size() > 0) {
                    this.onMoveToGalleryConfirmed((Collection<File>)this.toBeMoved);
                    this.toBeMoved = null;
                    return;
                }
                this.analytics.get().logError("mtg_perm_ok_no_files");
                Toaster.toast(2131689647);
                return;
            }
            this.analytics.get().logError("mtg_perm_no");
            Toaster.toast(2131689651);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable("f75099fb", this.toBeMoved);
    }

    public static interface Client {
        public void onMoveToGalleryConfirmed();

        public void onPurchasesErrors(int var1);

        public void onRefreshRequired();
    }

}

