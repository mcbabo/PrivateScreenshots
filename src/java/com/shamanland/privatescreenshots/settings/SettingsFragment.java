/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.KeyguardManager
 *  android.content.Context
 *  android.content.Intent
 *  android.os.Bundle
 *  android.os.Handler
 *  android.support.v14.preference.SwitchPreference
 *  android.support.v4.app.FragmentActivity
 *  android.support.v7.preference.CheckBoxPreference
 *  android.support.v7.preference.ListPreference
 *  android.support.v7.preference.Preference
 *  android.support.v7.preference.Preference$OnPreferenceChangeListener
 *  android.support.v7.preference.Preference$OnPreferenceClickListener
 *  android.support.v7.preference.SeekBarPreference
 *  android.view.View
 *  com.google.android.gms.auth.api.signin.GoogleSignIn
 *  com.google.android.gms.auth.api.signin.GoogleSignInAccount
 *  com.google.android.gms.drive.Drive
 *  com.google.android.gms.drive.DriveContents
 *  com.google.android.gms.drive.DriveFolder
 *  com.google.android.gms.drive.DriveResourceClient
 *  com.google.android.gms.drive.MetadataChangeSet
 *  com.google.android.gms.drive.MetadataChangeSet$Builder
 *  com.google.android.gms.tasks.OnCompleteListener
 *  com.google.android.gms.tasks.Task
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 */
package com.shamanland.privatescreenshots.settings;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SeekBarPreference;
import android.view.View;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shamanland.analytics.Analytics;
import com.shamanland.analytics.AnalyticsProperty;
import com.shamanland.common.ui.ConfirmationDialogFragment;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.base.BasePrefsFragment;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.security.Protector;
import com.shamanland.privatescreenshots.settings.SettingsActivity;
import com.shamanland.privatescreenshots.settings.SettingsManager;
import com.shamanland.privatescreenshots.utils.AppHelper;
import com.shamanland.privatescreenshots.utils.EditTextPreference;
import com.shamanland.toaster.Toaster;

public class SettingsFragment
extends BasePrefsFragment {
    SwitchPreference adFree;
    LazyRef<Analytics> analytics;
    CheckBoxPreference authRequired;
    EditTextPreference authValidity;
    boolean authValidityClickConfirmed;
    SeekBarPreference buttonAlpha;
    boolean changesForLauncher;
    boolean changesForService;
    Preference payments;
    CheckBoxPreference privatePhotos;
    LazyRef<Protector> protector;
    LazyRef<SettingsManager> settingsManager;
    ListPreference shakeShot;
    CheckBoxPreference useExternalViewer;

    private void initNeverAsk() {
        this.initNeverAsk(2131689623, "delete_single");
        this.initNeverAsk(2131689624, "b0db0732");
        this.initNeverAsk(2131689622, "delete_selected");
        this.initNeverAsk(2131689626, "move_to_gallery");
        this.initNeverAsk(2131689627, "move_to_gallery_result");
    }

    private void initNeverAsk(int n, final String string2) {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference)this.findPreference(n);
        checkBoxPreference.setChecked(ConfirmationDialogFragment.isNeverAskAgain(this.getContext(), string2));
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

            public boolean onPreferenceChange(Preference preference, Object object) {
                if (object instanceof Boolean) {
                    ConfirmationDialogFragment.setNeverAskAgain(SettingsFragment.this.getContext(), string2, (Boolean)object);
                }
                return true;
            }
        });
    }

    private void initPayments() {
        this.payments = this.findPreference(2131689628);
        this.adFree = (SwitchPreference)this.findPreference(2131689618);
        this.adFree.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

            public boolean onPreferenceChange(Preference preference, Object object) {
                FragmentActivity fragmentActivity;
                if (Boolean.TRUE.equals(object) && (fragmentActivity = SettingsFragment.this.getActivity()) instanceof SettingsActivity) {
                    SettingsFragment.this.analytics.get().logEvent("buy_ad_free", "settings");
                    ((SettingsActivity)fragmentActivity).appHelper.buyAdFree();
                    return true;
                }
                return false;
            }
        });
        if (this.settingsManager.get().isAdFreeAvailable()) {
            this.payments.setVisible(false);
            this.adFree.setVisible(false);
            return;
        }
        this.payments.setVisible(true);
        this.adFree.setVisible(true);
        this.adFree.setChecked(false);
    }

    private void initSecurity() {
        if (Protector.isAvailable()) {
            this.authRequired = (CheckBoxPreference)this.findPreference(2131689619);
            this.authValidity = (EditTextPreference)((Object)this.findPreference(2131689620));
            this.authValidity.setOverriddenOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

                public boolean onPreferenceClick(Preference preference) {
                    if (SettingsFragment.this.protector.get().isLocked()) {
                        SettingsFragment.this.onProtectorLocked(12288);
                        return true;
                    }
                    return false;
                }
            });
            this.authValidity.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

                /*
                 * Exception decompiling
                 */
                public boolean onPreferenceChange(Preference var1, Object var2) {
                    // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                    // org.benf.cfr.reader.util.ConfusedCFRException: Invalid stack depths @ lbl11 : ALOAD_0 : trying to set 1 previously set to 0
                    // org.benf.cfr.reader.b.a.a.g.a(Op02WithProcessedDataAndRefs.java:203)
                    // org.benf.cfr.reader.b.a.a.g.a(Op02WithProcessedDataAndRefs.java:1489)
                    // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:308)
                    // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:182)
                    // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:127)
                    // org.benf.cfr.reader.entities.attributes.f.c(AttributeCode.java:96)
                    // org.benf.cfr.reader.entities.g.p(Method.java:396)
                    // org.benf.cfr.reader.entities.d.e(ClassFile.java:890)
                    // org.benf.cfr.reader.entities.d.c(ClassFile.java:773)
                    // org.benf.cfr.reader.entities.d.e(ClassFile.java:870)
                    // org.benf.cfr.reader.entities.d.b(ClassFile.java:792)
                    // org.benf.cfr.reader.b.a(Driver.java:128)
                    // org.benf.cfr.reader.a.a(CfrDriverImpl.java:63)
                    // com.njlabs.showjava.decompilers.JavaExtractionWorker.decompileWithCFR(JavaExtractionWorker.kt:61)
                    // com.njlabs.showjava.decompilers.JavaExtractionWorker.doWork(JavaExtractionWorker.kt:130)
                    // com.njlabs.showjava.decompilers.BaseDecompiler.withAttempt(BaseDecompiler.kt:108)
                    // com.njlabs.showjava.workers.DecompilerWorker$b.run(DecompilerWorker.kt:118)
                    // java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
                    // java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
                    // java.lang.Thread.run(Thread.java:919)
                    throw new IllegalStateException("Decompilation failed");
                }
            });
            this.authRequired.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

                public boolean onPreferenceChange(Preference preference, Object object) {
                    if (object instanceof Boolean) {
                        SettingsFragment.this.setProtectorEnabled((Boolean)object, false);
                        return true;
                    }
                    return false;
                }
            });
            return;
        }
        this.findPreference(2131689630).setVisible(false);
        this.findPreference(2131689619).setVisible(false);
        this.findPreference(2131689620).setVisible(false);
    }

    @SuppressLint(value={"RtlHardcoded"})
    private void initUiOptions() {
        this.buttonAlpha = (SeekBarPreference)this.findPreference(2131689621);
        this.buttonAlpha.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

            public boolean onPreferenceChange(Preference preference, Object object) {
                if (object instanceof Integer) {
                    Integer n = (Integer)object;
                    float f = (float)n.intValue() / 100.0f;
                    SettingsFragment.this.analytics.get().setProperty(AnalyticsProperty.PROPERTY_10, "settings_button_alpha", String.valueOf((int)(10 * (n / 10))));
                    SettingsFragment.this.settingsManager.get().setButtonAlpha(f);
                    return true;
                }
                return false;
            }
        });
        CheckBoxPreference checkBoxPreference = this.privatePhotos = (CheckBoxPreference)this.findPreference(2131689629);
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

                public boolean onPreferenceChange(Preference preference, Object object) {
                    if (object instanceof Boolean) {
                        SettingsFragment settingsFragment = SettingsFragment.this;
                        settingsFragment.changesForLauncher |= SettingsFragment.this.settingsManager.get().setPrivatePhotos((Boolean)object);
                        return true;
                    }
                    return false;
                }
            });
        }
        this.shakeShot = (ListPreference)this.findPreference(2131689631);
        this.shakeShot.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

            /*
             * Unable to fully structure code
             * Enabled aggressive block sorting
             * Lifted jumps to return sites
             */
            public boolean onPreferenceChange(Preference var1_1, Object var2_2) {
                block11 : {
                    block8 : {
                        block9 : {
                            block10 : {
                                var3_3 = String.valueOf((Object)var2_2);
                                var4_4 = var3_3.hashCode();
                                if (var4_4 == -1078030475) break block8;
                                if (var4_4 == 3195115) break block9;
                                if (var4_4 == 102970646) break block10;
                                if (var4_4 != 270940796 || !var3_3.equals((Object)"disabled")) ** GOTO lbl-1000
                                var5_5 = 0;
                                break block11;
                            }
                            if (!var3_3.equals((Object)"light")) ** GOTO lbl-1000
                            var5_5 = 1;
                            break block11;
                        }
                        if (!var3_3.equals((Object)"hard")) ** GOTO lbl-1000
                        var5_5 = 3;
                        break block11;
                    }
                    if (var3_3.equals((Object)"medium")) {
                        var5_5 = 2;
                    } else lbl-1000: // 4 sources:
                    {
                        var5_5 = -1;
                    }
                }
                switch (var5_5) {
                    default: {
                        return false;
                    }
                    case 3: {
                        SettingsFragment.this.shakeShot.setSummary((CharSequence)SettingsFragment.this.getString(2131689605));
                        var9_6 = SettingsFragment.this;
                        var9_6.changesForService |= SettingsFragment.this.settingsManager.get().setShakeShot(3);
                        return true;
                    }
                    case 2: {
                        SettingsFragment.this.shakeShot.setSummary((CharSequence)SettingsFragment.this.getString(2131689636));
                        var8_7 = SettingsFragment.this;
                        var8_7.changesForService |= SettingsFragment.this.settingsManager.get().setShakeShot(2);
                        return true;
                    }
                    case 1: {
                        SettingsFragment.this.shakeShot.setSummary((CharSequence)SettingsFragment.this.getString(2131689635));
                        var7_8 = SettingsFragment.this;
                        var7_8.changesForService |= SettingsFragment.this.settingsManager.get().setShakeShot(1);
                        return true;
                    }
                    case 0: 
                }
                SettingsFragment.this.shakeShot.setSummary((CharSequence)SettingsFragment.this.getString(2131689687));
                var6_9 = SettingsFragment.this;
                var6_9.changesForService |= SettingsFragment.this.settingsManager.get().setShakeShot(0);
                return true;
            }
        });
        if (this.settingsManager.get().isChooseViewerAvailable()) {
            this.useExternalViewer = (CheckBoxPreference)this.findPreference(2131689632);
            this.useExternalViewer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

                public boolean onPreferenceChange(Preference preference, Object object) {
                    if (object instanceof Boolean) {
                        SettingsFragment.this.settingsManager.get().setUseExternalViewer((Boolean)object);
                        return true;
                    }
                    return false;
                }
            });
            return;
        }
        this.findPreference(2131689632).setVisible(false);
    }

    private void onSignedIn(GoogleSignInAccount googleSignInAccount) {
        final DriveResourceClient driveResourceClient = Drive.getDriveResourceClient((Context)this.getContextNonNull(), (GoogleSignInAccount)googleSignInAccount);
        driveResourceClient.getRootFolder().addOnCompleteListener((OnCompleteListener)new OnCompleteListener<DriveFolder>(){

            public void onComplete(Task<DriveFolder> task) {
                if (task.isSuccessful()) {
                    DriveFolder driveFolder = (DriveFolder)task.getResult();
                    if (driveFolder != null) {
                        driveResourceClient.createFile(driveFolder, new MetadataChangeSet.Builder().setMimeType("image/png").setTitle("Android Photo.png").build(), null);
                        return;
                    }
                } else {
                    Toaster.toast("Can't access Drive");
                }
            }
        });
    }

    private void postRefreshAuthOptions() {
        new Handler().post(new Runnable(){

            public void run() {
                SettingsFragment.this.refreshAuthOptions();
            }
        });
    }

    private void refreshNeverAsk() {
        this.initNeverAsk();
    }

    @SuppressLint(value={"RtlHardcoded"})
    private void refreshUiOptions() {
        this.buttonAlpha.setValue((int)(100.0f * this.settingsManager.get().getButtonAlpha()));
        this.buttonAlpha.setMin(20);
        this.buttonAlpha.setMax(100);
        CheckBoxPreference checkBoxPreference = this.privatePhotos;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(this.settingsManager.get().isPrivatePhotos());
        }
        int n = this.settingsManager.get().getShakeShot();
        this.shakeShot.setValueIndex(n);
        if (n == 0) {
            this.shakeShot.setSummary(this.getText(2131689687));
        } else {
            ListPreference listPreference = this.shakeShot;
            listPreference.setSummary(listPreference.getEntries()[n]);
        }
        if (this.settingsManager.get().isChooseViewerAvailable()) {
            this.useExternalViewer.setChecked(this.settingsManager.get().isUseExternalViewer());
        }
    }

    public <T extends Preference> T findPreference(int n) {
        return (T)this.findPreference(this.getText(n));
    }

    public boolean hasChanges() {
        return this.changesForService || this.changesForLauncher;
        {
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @SuppressLint(value={"RestrictedApi"})
    public void onActivityResult(int n, int n2, Intent intent) {
        super.onActivityResult(n, n2, intent);
        int n3 = n & 4095;
        if (n2 != -1) return;
        int n4 = n & 61440;
        if (n4 != 4096) {
            if (n4 != 8192) {
                if (n4 != 12288) {
                    if (n4 != 16384) {
                        if (n4 != 20480) {
                            return;
                        }
                        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount((Context)this.getContextNonNull());
                        if (googleSignInAccount == null) return;
                        this.onSignedIn(googleSignInAccount);
                        return;
                    }
                    this.reset();
                    return;
                }
                if (n3 == 0) {
                    this.authValidityClickConfirmed = true;
                    return;
                }
                this.setProtectorValidity(n3, true);
                return;
            }
            this.setProtectorEnabled(false, true);
            return;
        }
        this.setProtectorEnabled(true, true);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.analytics = this.getComponentLocator().getAnalytics();
        this.protector = this.getComponentLocator().getProtector();
        this.settingsManager = this.getComponentLocator().getSettingsManager();
        if (bundle != null) {
            this.authValidityClickConfirmed = bundle.getBoolean("e67f4303");
            this.changesForService = bundle.getBoolean("b3bcfd96");
            this.changesForLauncher = bundle.getBoolean("14bb6fc2");
        }
    }

    public void onCreatePreferences(Bundle bundle, String string2) {
        this.addPreferencesFromResource(2131886081);
    }

    protected void onProtectorLocked(int n) {
        KeyguardManager keyguardManager = (KeyguardManager)this.getContextNonNull().getSystemService("keyguard");
        if (keyguardManager != null) {
            Object[] arrobject = new Object[]{this.getString(2131689512)};
            this.startActivityForResult(keyguardManager.createConfirmDeviceCredentialIntent((CharSequence)this.getString(2131689573, arrobject), null), n);
        }
    }

    @SuppressLint(value={"RestrictedApi"})
    public void onResume() {
        super.onResume();
        this.refresh();
        if (this.authValidityClickConfirmed) {
            this.authValidityClickConfirmed = false;
            this.authValidity.performClick();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("e67f4303", this.authValidityClickConfirmed);
        bundle.putBoolean("b3bcfd96", this.changesForService);
        bundle.putBoolean("14bb6fc2", this.changesForLauncher);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.setDividerHeight(0);
        this.initPayments();
        this.initUiOptions();
        this.initNeverAsk();
        this.initSecurity();
    }

    protected void refresh() {
        this.refreshPayments();
        this.refreshUiOptions();
        this.refreshNeverAsk();
        this.refreshAuthOptions();
    }

    protected void refreshAuthOptions() {
        if (Protector.isAvailable()) {
            this.authRequired.setChecked(this.protector.get().isEnabled());
            this.authValidity.setEnabled(this.protector.get().isEnabled());
            String string2 = String.valueOf((int)this.protector.get().getAuthenticationValidityDuration());
            this.authValidity.setText(string2);
            this.authValidity.setSummary((CharSequence)string2);
        }
    }

    public void refreshPayments() {
        this.initPayments();
    }

    public void reset() {
        if (this.protector.get().isLocked()) {
            this.onProtectorLocked(16384);
            return;
        }
        this.changesForService = true;
        this.changesForLauncher = true;
        this.settingsManager.get().reset();
        if (Protector.isAvailable()) {
            this.setProtectorEnabled(false, true);
            this.setProtectorValidity(30, true);
        }
        Context context = this.getContext();
        ConfirmationDialogFragment.setNeverAskAgain(context, "delete_single", false);
        ConfirmationDialogFragment.setNeverAskAgain(context, "b0db0732", false);
        ConfirmationDialogFragment.setNeverAskAgain(context, "delete_selected", false);
        ConfirmationDialogFragment.setNeverAskAgain(context, "move_to_gallery", false);
        ConfirmationDialogFragment.setNeverAskAgain(context, "move_to_gallery_result", false);
        this.refresh();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    protected void setProtectorEnabled(boolean var1_1, boolean var2_2) {
        block9 : {
            var3_3 = var1_1 != false ? 10 : 0;
            var4_4 = var3_3 + var2_2;
            var5_5 = this.analytics.get();
            var6_6 = new Object[]{String.valueOf((int)var4_4)};
            var5_5.logEvent("c9c5990d", var6_6);
            try {
                this.changesForLauncher |= this.protector.get().setEnabled(var1_1);
                break block9;
            }
            catch (Throwable var8_7) {
                ** GOTO lbl28
            }
            catch (Protector.ProtectorException var7_8) {
                block10 : {
                    switch (var7_8.code) {
                        default: {
                            Crane.report((Throwable)var7_8);
                            ** break;
                        }
                        case 2: {
                            if (var2_2) {
                                Toaster.toast(2131689705);
                                break;
                            }
                            break block10;
                        }
                        case 1: {
                            Toaster.toast(2131689649);
                            break;
                        }
                    }
lbl26: // 1 sources:
                    Toaster.toast(2131689708);
lbl28: // 1 sources:
                    this.postRefreshAuthOptions();
                    throw var8_7;
                }
                var9_9 = var1_1 != false ? 4096 : 8192;
                this.onProtectorLocked(var9_9);
            }
        }
        this.postRefreshAuthOptions();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void setProtectorValidity(int n, boolean bl) {
        try {
            try {
                this.changesForLauncher |= this.protector.get().setAuthenticationValidityDuration(n);
            }
            catch (Protector.ProtectorException protectorException) {
                if (protectorException.code != 2) {
                    Crane.report((Throwable)protectorException);
                    Toaster.toast(2131689708);
                }
                if (bl) {
                    Toaster.toast(2131689705);
                }
                this.onProtectorLocked(12288 | n & 4095);
            }
        }
        catch (Throwable throwable2) {}
        this.postRefreshAuthOptions();
        return;
        this.postRefreshAuthOptions();
        throw throwable2;
    }

}

