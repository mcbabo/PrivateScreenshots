/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.res.AssetFileDescriptor
 *  android.database.Cursor
 *  android.database.MatrixCursor
 *  android.database.MatrixCursor$RowBuilder
 *  android.graphics.Point
 *  android.net.Uri
 *  android.os.CancellationSignal
 *  android.os.ParcelFileDescriptor
 *  android.provider.DocumentsContract
 *  android.provider.DocumentsProvider
 *  java.io.File
 *  java.io.FileNotFoundException
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package com.shamanland.privatescreenshots.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.common.utils.Utils;
import com.shamanland.privatescreenshots.componentlocator.ComponentLocator;
import com.shamanland.privatescreenshots.storage.ConfirmCredentialsActivity;
import com.shamanland.privatescreenshots.storage.Storage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Provider
extends DocumentsProvider {
    private static final String[] DEFAULT_DOCUMENT_PROJECTION;
    private static final String[] DEFAULT_ROOT_PROJECTION;
    private static final String LOG_TAG = "Provider";

    static {
        DEFAULT_ROOT_PROJECTION = new String[]{"root_id", "mime_types", "flags", "icon", "title", "summary", "document_id"};
        DEFAULT_DOCUMENT_PROJECTION = new String[]{"document_id", "mime_type", "_display_name", "last_modified", "flags", "_size"};
    }

    protected LazyRef<Analytics> getAnalytics(Context context) {
        return ComponentLocator.getInstance(context).getAnalytics();
    }

    protected LazyRef<Storage> getStorage(Context context) {
        return ComponentLocator.getInstance(context).getStorage();
    }

    public boolean onCreate() {
        return true;
    }

    public ParcelFileDescriptor openDocument(String string2, String string3, CancellationSignal cancellationSignal) throws FileNotFoundException {
        Context context = this.getContext();
        if (context == null) {
            return null;
        }
        this.getAnalytics(context).get().logEvent("provider_open");
        return ParcelFileDescriptor.open((File)new File(this.getStorage(context).get().getFilesDir(), string2), (int)268435456);
    }

    public AssetFileDescriptor openDocumentThumbnail(String string2, Point point, CancellationSignal cancellationSignal) throws FileNotFoundException {
        Context context = this.getContext();
        if (context == null) {
            return null;
        }
        File file = new File(this.getStorage(context).get().getThumbsDir(), string2);
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open((File)file, (int)268435456);
        AssetFileDescriptor assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptor, 0L, file.length());
        return assetFileDescriptor;
    }

    public Cursor queryChildDocuments(String string2, String[] arrstring, String string3) {
        if (arrstring == null) {
            arrstring = DEFAULT_DOCUMENT_PROJECTION;
        }
        MatrixCursor matrixCursor = new MatrixCursor(arrstring, 1);
        Context context = this.getContext();
        if (context == null) {
            return matrixCursor;
        }
        if ("472eca83".equals((Object)string2)) {
            boolean[] arrbl = new boolean[]{false};
            for (File file : this.getStorage(context).get().getFiles(arrbl)) {
                MatrixCursor.RowBuilder rowBuilder = matrixCursor.newRow();
                rowBuilder.add("mime_type", (Object)"image/png");
                rowBuilder.add("document_id", (Object)file.getName());
                rowBuilder.add("_display_name", (Object)file.getName());
                rowBuilder.add("flags", (Object)1);
            }
            if (arrbl[0] && ConfirmCredentialsActivity.canLaunchNow(context)) {
                Intent intent = new Intent(context, ConfirmCredentialsActivity.class);
                intent.addFlags(268435456);
                Utils.tryStartActivity(context, intent, 2131689708);
            }
        }
        matrixCursor.setNotificationUri(context.getContentResolver(), DocumentsContract.buildChildDocumentsUri((String)context.getPackageName(), (String)"472eca83"));
        return matrixCursor;
    }

    public Cursor queryDocument(String string2, String[] arrstring) {
        if (arrstring == null) {
            arrstring = DEFAULT_DOCUMENT_PROJECTION;
        }
        MatrixCursor matrixCursor = new MatrixCursor(arrstring, 1);
        Context context = this.getContext();
        if (context == null) {
            return matrixCursor;
        }
        if ("472eca83".equals((Object)string2)) {
            MatrixCursor.RowBuilder rowBuilder = matrixCursor.newRow();
            rowBuilder.add("mime_type", (Object)"vnd.android.document/directory");
            rowBuilder.add("document_id", (Object)string2);
            rowBuilder.add("_display_name", (Object)context.getString(2131689512));
        }
        return matrixCursor;
    }

    public Cursor queryRoots(String[] arrstring) {
        if (arrstring == null) {
            arrstring = DEFAULT_ROOT_PROJECTION;
        }
        MatrixCursor matrixCursor = new MatrixCursor(arrstring, 1);
        Context context = this.getContext();
        if (context == null) {
            return matrixCursor;
        }
        this.getAnalytics(context).get().logEvent("provider_root");
        MatrixCursor.RowBuilder rowBuilder = matrixCursor.newRow();
        rowBuilder.add("root_id", (Object)"472eca83");
        rowBuilder.add("flags", (Object)2);
        rowBuilder.add("title", (Object)context.getString(2131689512));
        rowBuilder.add("document_id", (Object)"472eca83");
        rowBuilder.add("mime_types", (Object)"image/png");
        rowBuilder.add("icon", (Object)2131623937);
        return matrixCursor;
    }
}

