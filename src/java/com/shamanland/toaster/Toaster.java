/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.Handler
 *  android.os.Handler$Callback
 *  android.os.Looper
 *  android.os.Message
 *  android.widget.Toast
 *  java.lang.AssertionError
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Method
 */
package com.shamanland.toaster;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.shamanland.crane.Crane;
import java.lang.reflect.Method;

public final class Toaster
implements Handler.Callback {
    private static Context context;
    private static Handler handler;

    private Toaster() {
    }

    public static Context getContext() {
        Context context = Toaster.context;
        if (context == null) {
            block3 : {
                Context context2;
                try {
                    context2 = (Context)Class.forName((String)"android.app.ActivityThread").getDeclaredMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
                    if (context2 == null) break block3;
                }
                catch (Exception exception) {
                    throw new AssertionError((Object)exception);
                }
                Toaster.context = context2;
                return context2;
            }
            throw new IllegalStateException();
        }
        return context;
    }

    public static Handler getHandler() {
        Handler handler = Toaster.handler;
        if (handler == null) {
            Toaster.handler = handler = new Handler(Looper.getMainLooper(), (Handler.Callback)new Toaster());
        }
        return handler;
    }

    private static void sendMessage(CharSequence charSequence, int n) {
        Message.obtain((Handler)Toaster.getHandler(), (int)n, (Object)charSequence).sendToTarget();
    }

    public static void toast(int n) {
        Toaster.sendMessage(Toaster.getContext().getText(n), 0);
    }

    public static void toast(CharSequence charSequence) {
        Toaster.sendMessage(charSequence, 0);
    }

    public static void toastLong(int n) {
        Toaster.sendMessage(Toaster.getContext().getText(n), 1);
    }

    public boolean handleMessage(Message message) {
        try {
            Toast.makeText((Context)Toaster.getContext(), (CharSequence)((CharSequence)message.obj), (int)message.what).show();
        }
        catch (Exception exception) {
            Crane.report(exception);
        }
        return true;
    }
}

