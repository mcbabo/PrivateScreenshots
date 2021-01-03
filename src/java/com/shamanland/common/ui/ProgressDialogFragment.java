/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.Dialog
 *  android.app.ProgressDialog
 *  android.content.Context
 *  android.os.Bundle
 *  android.support.v4.app.DialogFragment
 *  android.support.v4.app.FragmentManager
 *  java.lang.CharSequence
 *  java.lang.String
 */
package com.shamanland.common.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class ProgressDialogFragment
extends DialogFragment {
    public static void show(FragmentManager fragmentManager, String string2, String string3) {
        Bundle bundle = new Bundle();
        bundle.putString("bf4d486c", string2);
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setArguments(bundle);
        progressDialogFragment.show(fragmentManager, string3);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        this.setCancelable(false);
        ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage((CharSequence)this.getArguments().getString("bf4d486c"));
        return progressDialog;
    }
}

