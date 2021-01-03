/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.app.KeyguardManager
 *  android.content.Context
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.security.keystore.KeyGenParameterSpec
 *  android.security.keystore.KeyGenParameterSpec$Builder
 *  android.security.keystore.UserNotAuthenticatedException
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.security.InvalidKeyException
 *  java.security.Key
 *  java.security.KeyStore
 *  java.security.KeyStore$LoadStoreParameter
 *  java.security.UnrecoverableKeyException
 *  java.security.spec.AlgorithmParameterSpec
 *  javax.crypto.Cipher
 *  javax.crypto.KeyGenerator
 *  javax.crypto.SecretKey
 */
package com.shamanland.privatescreenshots.security;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.UserNotAuthenticatedException;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.utils.ErrorParser;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Protector {
    private static final String LOG_TAG = "Protector";
    private final LazyRef<Analytics> analytics;
    private final Context context;
    private final KeyguardManager keyguard;

    public Protector(Context context, LazyRef<Analytics> lazyRef) {
        this.context = context;
        this.keyguard = (KeyguardManager)context.getSystemService("keyguard");
        this.analytics = lazyRef;
    }

    private SecretKey createKey(int n, Throwable[] arrthrowable) {
        if (Protector.isAvailable()) {
            return this.createKeyImpl(n, arrthrowable);
        }
        return null;
    }

    private SecretKey createKeyImpl(int n, Throwable[] arrthrowable) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance((String)"AES", (String)"AndroidKeyStore");
            keyGenerator.init((AlgorithmParameterSpec)new KeyGenParameterSpec.Builder("a447e528", 3).setBlockModes(new String[]{"CBC"}).setUserAuthenticationRequired(true).setUserAuthenticationValidityDurationSeconds(n).setEncryptionPaddings(new String[]{"PKCS7Padding"}).build());
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey;
        }
        catch (Exception exception) {
            arrthrowable[0] = exception;
            return null;
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private byte[] encryptImpl(byte[] arrby) throws EncryptException {
        void var4_10;
        Throwable[] arrthrowable = new Throwable[]{null};
        try {
            KeyStore keyStore = KeyStore.getInstance((String)"AndroidKeyStore");
            keyStore.load(null);
            Key key = keyStore.getKey("a447e528", null);
            Cipher cipher = Cipher.getInstance((String)"AES/CBC/PKCS7Padding");
            cipher.init(1, key);
            return cipher.doFinal(arrby);
        }
        catch (Exception exception) {
            throw new EncryptException(exception, 3);
        }
        catch (UnrecoverableKeyException unrecoverableKeyException) {
        }
        catch (InvalidKeyException invalidKeyException) {
            // empty catch block
        }
        if (this.createKey(this.getAuthenticationValidityDuration(), arrthrowable) != null) throw new EncryptException((Throwable)var4_10, 2);
        if (arrthrowable[0] == null) throw new EncryptException((Throwable)var4_10, 4);
        throw new EncryptException(arrthrowable[0], 4);
        catch (UserNotAuthenticatedException userNotAuthenticatedException) {
            throw new EncryptException(userNotAuthenticatedException, 1);
        }
    }

    private boolean hasKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance((String)"AndroidKeyStore");
            keyStore.load(null);
            Key key = keyStore.getKey("a447e528", null);
            return key != null;
        }
        catch (Exception exception) {
            if ("java.security.UnrecoverableKeyException".equals((Object)exception.getClass().getName())) {
                this.analytics.get().logError("protector_has_key_unrecoverable");
                return false;
            }
            Crane.report(exception);
            return false;
        }
    }

    public static boolean isAvailable() {
        return Build.VERSION.SDK_INT >= 23;
    }

    private void removeKey(Throwable[] arrthrowable) {
        try {
            KeyStore keyStore = KeyStore.getInstance((String)"AndroidKeyStore");
            keyStore.load(null);
            keyStore.deleteEntry("a447e528");
            return;
        }
        catch (Exception exception) {
            if (arrthrowable.length > 0) {
                arrthrowable[0] = exception;
                return;
            }
            Crane.report(exception);
            return;
        }
    }

    protected byte[] encrypt(byte[] arrby) throws EncryptException {
        if (Protector.isAvailable()) {
            return this.encryptImpl(arrby);
        }
        return null;
    }

    public int getAuthenticationValidityDuration() {
        return this.getPrefs().getInt("4d3c1e33", 30);
    }

    public SharedPreferences getPrefs() {
        return this.context.getSharedPreferences("be9a6887", 0);
    }

    public boolean isEnabled() {
        return this.keyguard.isKeyguardSecure() && this.hasKey();
    }

    public boolean isLocked() {
        return this.isLocked(null);
    }

    public boolean isLocked(Throwable[] arrthrowable) {
        if (Protector.isAvailable() && this.isEnabled()) {
            try {
                byte[] arrby = this.encrypt(new byte[1]);
                return arrby == null;
            }
            catch (EncryptException encryptException) {
                if (arrthrowable != null && arrthrowable.length > 0) {
                    arrthrowable[0] = encryptException;
                    return true;
                }
                if (encryptException.code == 3 || encryptException.code == 4) {
                    Crane.report((Throwable)encryptException);
                }
                return true;
            }
        }
        return false;
    }

    public boolean setAuthenticationValidityDuration(int n) throws ProtectorException {
        int n2 = this.getAuthenticationValidityDuration();
        int n3 = Math.min((int)Math.max((int)3, (int)n), (int)300);
        Throwable[] arrthrowable = new Throwable[]{null};
        if (n2 != n3) {
            if (!this.isLocked(arrthrowable)) {
                if (this.createKey(n3, arrthrowable) != null) {
                    this.getPrefs().edit().putInt("4d3c1e33", n3).apply();
                    Analytics analytics = this.analytics.get();
                    Object[] arrobject = new Object[]{String.valueOf((int)(10 * (n3 / 10)))};
                    analytics.logEvent("protector_auth_validity", arrobject);
                    return true;
                }
                throw new ProtectorException(arrthrowable[0], 3);
            }
            throw new ProtectorException(arrthrowable[0], 2);
        }
        return false;
    }

    public boolean setEnabled(boolean bl) throws ProtectorException {
        block6 : {
            Throwable[] arrthrowable;
            block10 : {
                block11 : {
                    block9 : {
                        block7 : {
                            block8 : {
                                boolean bl2 = this.isEnabled();
                                arrthrowable = new Throwable[]{null};
                                if (bl2 == bl) break block6;
                                if (!bl) break block7;
                                if (!this.keyguard.isKeyguardSecure()) break block8;
                                if (this.createKey(this.getAuthenticationValidityDuration(), arrthrowable) == null) {
                                    if (arrthrowable[0] != null && ErrorParser.findNoLockScreen(arrthrowable[0].toString())) {
                                        throw new ProtectorException(arrthrowable[0], 1);
                                    }
                                    throw new ProtectorException(arrthrowable[0], 3);
                                }
                                break block9;
                            }
                            throw new ProtectorException(null, 1);
                        }
                        if (this.isLocked(arrthrowable)) break block10;
                        this.removeKey(arrthrowable);
                        if (this.hasKey()) break block11;
                    }
                    return true;
                }
                throw new ProtectorException(arrthrowable[0], 3);
            }
            throw new ProtectorException(arrthrowable[0], 2);
        }
        return false;
    }

    public static class EncryptException
    extends Exception {
        public final int code;

        public EncryptException(Throwable throwable, int n) {
            super(String.valueOf((int)n), throwable);
            this.code = n;
        }
    }

    public static class ProtectorException
    extends Exception {
        public final int code;

        public ProtectorException(Throwable throwable, int n) {
            super(String.valueOf((int)n), throwable);
            this.code = n;
        }
    }

}

