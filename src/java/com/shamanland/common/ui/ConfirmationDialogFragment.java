/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.AlertDialog
 *  android.app.AlertDialog$Builder
 *  android.app.Dialog
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.content.Intent
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.os.Bundle
 *  android.os.Parcelable
 *  android.support.v4.app.FragmentActivity
 *  android.support.v4.app.FragmentManager
 *  android.text.TextUtils
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.Window
 *  android.widget.Button
 *  android.widget.CheckBox
 *  android.widget.CompoundButton
 *  android.widget.CompoundButton$OnCheckedChangeListener
 *  android.widget.TextView
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 */
package com.shamanland.common.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.shamanland.common.R;
import com.shamanland.common.ui.BaseDialogFragment;
import com.shamanland.common.utils.Utils;

public class ConfirmationDialogFragment
extends BaseDialogFragment {
    protected static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("427f6e67", 0);
    }

    public static boolean isNeverAskAgain(Context context, String string2) {
        return ConfirmationDialogFragment.getPrefs(context).getBoolean(string2, false);
    }

    public static void setNeverAskAgain(Context context, String string2, boolean bl) {
        ConfirmationDialogFragment.getPrefs(context).edit().putBoolean(string2, bl).apply();
    }

    void notifyNegative(String string2) {
        Listener listener = this.castOwner(Listener.class);
        if (listener != null) {
            listener.onNegative(string2, this.getArguments().getBundle("ebd055ca"));
        }
    }

    void notifyPositive(String string2) {
        Listener listener = this.castOwner(Listener.class);
        if (listener != null) {
            listener.onPositive(string2, this.getArguments().getBundle("ebd055ca"));
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Window window;
        Bundle bundle2 = this.getArguments();
        if (bundle2 == null) {
            return super.onCreateDialog(bundle);
        }
        final String string2 = bundle2.getString("3480cd98");
        boolean bl = bundle2.getBoolean("791259f4");
        if (bl && TextUtils.isEmpty((CharSequence)string2)) {
            bl = false;
        }
        final Intent intent = (Intent)bundle2.getParcelable("a3a4e149");
        final Intent intent2 = (Intent)bundle2.getParcelable("a89074d7");
        final boolean bl2 = bundle2.getBoolean("a6179672");
        final boolean bl3 = bundle2.getBoolean("b1d70e7d");
        View view = LayoutInflater.from((Context)this.getContext()).inflate(R.layout.f_confirmation, null);
        ((TextView)view.findViewById(R.id.message)).setText((CharSequence)bundle2.getString("f3799a4b"));
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.never_ask_again);
        int n = bl ? 0 : 8;
        checkBox.setVisibility(n);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity()).setTitle((CharSequence)bundle2.getString("3c2cb2ac")).setView(view);
        String string3 = bundle2.getString("7caa1876");
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                if (string2 != null) {
                    if (checkBox.isChecked()) {
                        ConfirmationDialogFragment.setNeverAskAgain(ConfirmationDialogFragment.this.getContext(), string2, true);
                    }
                    ConfirmationDialogFragment.this.notifyPositive(string2);
                }
                if (intent != null) {
                    if (bl2) {
                        Utils.tryStartService((Context)ConfirmationDialogFragment.this.getActivity(), intent, R.string.unknown_error);
                        return;
                    }
                    Utils.tryStartActivity((Context)ConfirmationDialogFragment.this.getActivity(), intent, R.string.unknown_error);
                }
            }
        };
        final AlertDialog alertDialog = builder.setPositiveButton((CharSequence)string3, onClickListener).setNegativeButton((CharSequence)bundle2.getString("078a8c21"), new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                String string22 = string2;
                if (string22 != null) {
                    ConfirmationDialogFragment.this.notifyNegative(string22);
                }
                if (intent2 != null) {
                    if (bl3) {
                        Utils.tryStartService((Context)ConfirmationDialogFragment.this.getActivity(), intent2, R.string.unknown_error);
                        return;
                    }
                    Utils.tryStartActivity((Context)ConfirmationDialogFragment.this.getActivity(), intent2, R.string.unknown_error);
                }
            }
        }).create();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            public void onCheckedChanged(CompoundButton compoundButton, boolean bl) {
                alertDialog.getButton(-2).setEnabled(bl ^ true);
            }
        });
        if (bundle2.getBoolean("7d4b64f9")) {
            alertDialog.setCanceledOnTouchOutside(false);
            this.setCancelable(false);
        }
        if ((window = alertDialog.getWindow()) != null) {
            window.addFlags(2);
            window.setDimAmount(0.5f);
        }
        return alertDialog;
    }

    public static class Builder {
        private Bundle extras;
        private String id;
        private String message;
        private Intent negativeIntent;
        private boolean negativeIntentService;
        private String negativeText;
        private Intent positiveIntent;
        private boolean positiveIntentService;
        private String positiveText;
        private boolean preventCancel;
        private String title;
        private boolean useNeverShow;

        public Builder setExtras(Bundle bundle) {
            this.extras = bundle;
            return this;
        }

        public Builder setId(String string2) {
            this.id = string2;
            return this;
        }

        public Builder setMessage(String string2) {
            this.message = string2;
            return this;
        }

        public Builder setNegativeIntent(Intent intent) {
            this.negativeIntent = intent;
            return this;
        }

        public Builder setNegativeIntentService(boolean bl) {
            this.negativeIntentService = bl;
            return this;
        }

        public Builder setNegativeText(String string2) {
            this.negativeText = string2;
            return this;
        }

        public Builder setPositiveText(String string2) {
            this.positiveText = string2;
            return this;
        }

        public Builder setPreventCancel(boolean bl) {
            this.preventCancel = bl;
            return this;
        }

        public Builder setTitle(String string2) {
            this.title = string2;
            return this;
        }

        public Builder setUseNeverShow(boolean bl) {
            this.useNeverShow = bl;
            return this;
        }

        public void show(FragmentManager fragmentManager) {
            Bundle bundle = new Bundle();
            bundle.putString("3480cd98", this.id);
            bundle.putString("3c2cb2ac", this.title);
            bundle.putString("f3799a4b", this.message);
            bundle.putString("7caa1876", this.positiveText);
            bundle.putString("078a8c21", this.negativeText);
            bundle.putParcelable("a3a4e149", (Parcelable)this.positiveIntent);
            bundle.putParcelable("a89074d7", (Parcelable)this.negativeIntent);
            bundle.putBoolean("a6179672", this.positiveIntentService);
            bundle.putBoolean("b1d70e7d", this.negativeIntentService);
            bundle.putBoolean("791259f4", this.useNeverShow);
            bundle.putBoolean("7d4b64f9", this.preventCancel);
            bundle.putBundle("ebd055ca", this.extras);
            ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
            confirmationDialogFragment.setArguments(bundle);
            confirmationDialogFragment.show(fragmentManager, ConfirmationDialogFragment.class.getName());
        }
    }

    public static interface Listener {
        public void onNegative(String var1, Bundle var2);

        public void onPositive(String var1, Bundle var2);
    }

}

