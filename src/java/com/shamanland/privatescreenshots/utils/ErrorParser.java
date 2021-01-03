/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 */
package com.shamanland.privatescreenshots.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorParser {
    static final Pattern ENOENT;
    static final Pattern ENOSPC;
    static final Pattern FORMAT;
    static final Pattern NOT_ALLOWERD_SYSTEM_ALERT_WINDOW;
    static final Pattern NOT_PERMISSION_WINDOW;
    static final Pattern NO_LOCK_SCREEN;
    static final Pattern VIEW_NOT_ATTACHED;

    static {
        FORMAT = Pattern.compile((String)".*producer.*format.*\\s0x([0-9a-f]+)\\s.*configured.*format.*\\s0x([0-9a-f]+)");
        ENOENT = Pattern.compile((String)"\\sENOENT\\s");
        ENOSPC = Pattern.compile((String)"\\sENOSPC\\s");
        NOT_ALLOWERD_SYSTEM_ALERT_WINDOW = Pattern.compile((String)"not allowed.*SYSTEM_ALERT_WINDOW");
        NOT_PERMISSION_WINDOW = Pattern.compile((String)"permission denied for (this )?window type");
        VIEW_NOT_ATTACHED = Pattern.compile((String)"[vV]iew.*not attached to window manager");
        NO_LOCK_SCREEN = Pattern.compile((String)"lock screen must be enabled");
    }

    public static boolean findErrorNoEntity(String string2) {
        return string2 != null && ENOENT.matcher((CharSequence)string2).find();
    }

    public static boolean findErrorNoSpace(String string2) {
        return string2 != null && ENOSPC.matcher((CharSequence)string2).find();
    }

    public static boolean findNoLockScreen(String string2) {
        return string2 != null && NO_LOCK_SCREEN.matcher((CharSequence)string2).find();
    }

    public static boolean findNotAllowedSystemAlertWindow(String string2) {
        return string2 != null && NOT_ALLOWERD_SYSTEM_ALERT_WINDOW.matcher((CharSequence)string2).find();
    }

    public static boolean findPermissionDeniedWindow(String string2) {
        return string2 != null && NOT_PERMISSION_WINDOW.matcher((CharSequence)string2).find();
    }

    /*
     * Exception decompiling
     */
    public static boolean findProducerFormat(String var0, int[] var1) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Invalid stack depths @ lbl22.1 : ICONST_0 : trying to set 0 previously set to 1
        // org.benf.cfr.reader.b.a.a.g.a(Op02WithProcessedDataAndRefs.java:203)
        // org.benf.cfr.reader.b.a.a.g.a(Op02WithProcessedDataAndRefs.java:1489)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:308)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:182)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:127)
        // org.benf.cfr.reader.entities.attributes.f.c(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.g.p(Method.java:396)
        // org.benf.cfr.reader.entities.d.e(ClassFile.java:890)
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

    public static boolean findViewNotAttached(String string2) {
        return string2 != null && VIEW_NOT_ATTACHED.matcher((CharSequence)string2).find();
    }
}

