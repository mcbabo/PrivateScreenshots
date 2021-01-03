/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.content.Context
 *  android.media.MediaScannerConnection
 *  android.os.AsyncTask
 *  java.io.Closeable
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileOutputStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.util.ArrayList
 *  java.util.Collection
 */
package com.shamanland.privatescreenshots.mover;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import com.shamanland.analytics.Analytics;
import com.shamanland.crane.Crane;
import com.shamanland.privatescreenshots.storage.Storage;
import com.shamanland.privatescreenshots.utils.ErrorParser;
import com.shamanland.toaster.Toaster;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class Mover
extends AsyncTask<Void, Void, Void> {
    private Analytics analytics;
    @SuppressLint(value={"StaticFieldLeak"})
    private Context context;
    private File dir;
    private Collection<File> files;
    private Listener listener;
    private Storage storage;
    private ArrayList<File> toBeRemoved;
    private ArrayList<String> toBeScanned;

    public Mover(Context context, Storage storage, Analytics analytics, File file, Collection<File> collection, Listener listener) {
        this.context = context;
        this.analytics = analytics;
        this.storage = storage;
        this.dir = file;
        this.files = collection;
        this.toBeRemoved = new ArrayList(collection.size());
        this.toBeScanned = new ArrayList(collection.size());
        this.listener = listener;
    }

    /*
     * Exception decompiling
     */
    protected static void close(Closeable var0) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Underrun type stack
        // org.benf.cfr.reader.b.a.c.e.a(StackSim.java:35)
        // org.benf.cfr.reader.b.b.af.a(OperationFactoryPop.java:20)
        // org.benf.cfr.reader.b.b.e.a(JVMInstr.java:315)
        // org.benf.cfr.reader.b.a.a.g.a(Op02WithProcessedDataAndRefs.java:195)
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

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private boolean copyFile(File file, File file2, byte[] arrby, boolean[] arrbl) {
        void var10_12;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        block15 : {
            block16 : {
                boolean bl;
                block17 : {
                    FileInputStream fileInputStream2;
                    block14 : {
                        block13 : {
                            fileInputStream = null;
                            bl = true;
                            fileInputStream2 = new FileInputStream(file);
                            fileOutputStream = new FileOutputStream(file2);
                            try {
                                int n;
                                while ((n = fileInputStream2.read(arrby)) > 0) {
                                    fileOutputStream.write(arrby, 0, n);
                                }
                            }
                            catch (Throwable throwable) {
                                break block13;
                            }
                            catch (Exception exception) {
                                break block14;
                            }
                            Mover.close((Closeable)fileInputStream2);
                            Mover.close((Closeable)fileOutputStream);
                            return bl;
                            catch (Throwable throwable) {
                                fileOutputStream = null;
                            }
                        }
                        fileInputStream = fileInputStream2;
                        break block15;
                        catch (Exception exception) {
                            fileOutputStream = null;
                        }
                    }
                    fileInputStream = fileInputStream2;
                    break block17;
                    catch (Throwable throwable) {
                        fileInputStream = null;
                        fileOutputStream = null;
                        break block15;
                    }
                    catch (Exception exception) {
                        fileOutputStream = null;
                    }
                }
                try {
                    void var9_17;
                    if (ErrorParser.findErrorNoSpace(var9_17.getMessage())) {
                        arrbl[0] = bl;
                        bl = false;
                    }
                    if (!bl) break block16;
                    Crane.report((Throwable)var9_17);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            Mover.close((Closeable)fileInputStream);
            Mover.close((Closeable)fileOutputStream);
            return false;
        }
        Mover.close(fileInputStream);
        Mover.close((Closeable)fileOutputStream);
        throw var10_12;
    }

    protected /* varargs */ Void doInBackground(Void ... arrvoid) {
        byte[] arrby = new byte[1024];
        boolean[] arrbl = new boolean[]{false};
        for (File file : this.files) {
            File file2;
            if (this.copyFile(file, file2 = new File(this.dir, file.getName()), arrby, arrbl)) {
                this.toBeRemoved.add((Object)file);
                this.toBeScanned.add((Object)file2.getAbsolutePath());
            }
            if (!arrbl[0]) continue;
            this.analytics.logError("mover_no_space_break");
            Toaster.toastLong(2131689653);
            break;
        }
        this.analytics.logEvent("moved_to_gallery", this.toBeRemoved.size());
        return null;
    }

    protected void onPostExecute(Void void_) {
        if (this.toBeRemoved.size() > 0) {
            this.storage.remove((Collection<File>)this.toBeRemoved);
            try {
                MediaScannerConnection.scanFile((Context)this.context.getApplicationContext(), (String[])((String[])this.toBeScanned.toArray((Object[])new String[0])), null, null);
            }
            catch (Exception exception) {
                Crane.report(exception);
            }
        }
        this.listener.onEndMoving(this.files.size(), this.toBeRemoved.size(), this.dir);
    }

    public static interface Listener {
        public void onEndMoving(int var1, int var2, File var3);
    }

}

