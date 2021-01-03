/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 */
package com.shamanland.crane;

public class Crane {
    protected static final String LOG_TAG = "Crane";
    private static CrashReporter crashReporterInstance = new CrashReporter(){

        @Override
        public void log(String string2) {
        }

        @Override
        public void report(Throwable throwable) {
        }
    };

    public static void log(String string2) {
        CrashReporter crashReporter = crashReporterInstance;
        if (crashReporter != null) {
            crashReporter.log(string2);
        }
    }

    public static void report(Throwable throwable) {
        CrashReporter crashReporter = crashReporterInstance;
        if (crashReporter != null) {
            crashReporter.report(throwable);
        }
    }

    public static void setCrashReporter(CrashReporter crashReporter) {
        crashReporterInstance = crashReporter;
    }

    public static interface CrashReporter {
        public void log(String var1);

        public void report(Throwable var1);
    }

}

