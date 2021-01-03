/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.Intent
 *  android.os.Bundle
 *  android.support.v4.app.Fragment
 *  android.support.v4.app.FragmentManager
 *  android.support.v4.app.FragmentTransaction
 *  android.support.v7.app.ActionBar
 *  android.util.Log
 *  android.view.Menu
 *  android.view.MenuItem
 *  com.google.android.gms.tasks.OnSuccessListener
 *  com.google.android.gms.tasks.Task
 *  com.google.firebase.iid.FirebaseInstanceId
 *  com.google.firebase.iid.InstanceIdResult
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.privatescreenshots.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.FrequencyCounter;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.base.BaseActivity;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.settings.SettingsFragment;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.AppHelper;
import com.shamanland.privatescreenshots.utils.AppUtils;

public class SettingsActivity
extends BaseActivity
implements AppHelper.Client {
    protected static final String LOG_TAG = "SettingsActivity";
    protected AppHelper appHelper;
    protected SettingsFragment settingsFragment;

    public void finish() {
        SettingsFragment settingsFragment = this.settingsFragment;
        if (settingsFragment != null && settingsFragment.hasChanges()) {
            Intent intent = new Intent();
            intent.putExtra("b3bcfd96", this.settingsFragment.changesForService);
            intent.putExtra("14bb6fc2", this.settingsFragment.changesForLauncher);
            this.setResult(-1, intent);
        }
        super.finish();
    }

    protected void onActivityResult(int n, int n2, Intent intent) {
        super.onActivityResult(n, n2, intent);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        AppHelper appHelper;
        ActionBar actionBar;
        super.onCreate(bundle);
        LazyRef<SettingsManager> lazyRef = this.getComponentLocator().getSettingsManager();
        LazyRef<Storage> lazyRef2 = this.getComponentLocator().getStorage();
        LazyRef<Analytics> lazyRef3 = this.getComponentLocator().getAnalytics();
        this.appHelper = appHelper = new AppHelper(this, this, lazyRef, lazyRef2, lazyRef3);
        this.appHelper.onCreate(bundle);
        this.setContentView(2131492865);
        this.setTitle(AppUtils.getStyledText((Context)this, 2131755016, 2131689686, new Object[0]));
        this.settingsFragment = (SettingsFragment)this.getSupportFragmentManager().findFragmentByTag("settings");
        if (this.settingsFragment == null) {
            SettingsFragment settingsFragment;
            FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
            this.settingsFragment = settingsFragment = new SettingsFragment();
            fragmentTransaction.add(2131296315, (Fragment)settingsFragment, "settings").commit();
        }
        if ((actionBar = this.getSupportActionBar()) != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 2131296413, 0, 2131689673);
        this.appHelper.onCreateOptionsMenu(menu);
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        this.appHelper.onDestroy();
    }

    @Override
    public void onMoveToGalleryConfirmed() {
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int n = menuItem.getItemId();
        if (n != 16908332) {
            if (n != 2131296413) {
                if (this.appHelper.onOptionsItemSelected(menuItem)) {
                    return true;
                }
                return super.onOptionsItemSelected(menuItem);
            }
            this.settingsFragment.reset();
            return true;
        }
        this.finish();
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (FrequencyCounter.get() > 2.0) {
            FrequencyCounter.clear();
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity)this, (OnSuccessListener)new OnSuccessListener<InstanceIdResult>(){

                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Log.v((String)"FIDTKN", (String)instanceIdResult.getToken());
                }
            });
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPurchasesErrors(int n) {
        SettingsFragment settingsFragment = this.settingsFragment;
        if (settingsFragment != null) {
            settingsFragment.refreshPayments();
        }
    }

    @Override
    public void onRefreshRequired() {
        SettingsFragment settingsFragment = this.settingsFragment;
        if (settingsFragment != null) {
            settingsFragment.refreshPayments();
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.appHelper.onSaveInstanceState(bundle);
    }

}

