/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer.debug;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.spongepowered.asm.mixin.transformer.debug.RuntimeDecompiler;

public class RuntimeDecompilerAsync
extends RuntimeDecompiler
implements Runnable,
Thread.UncaughtExceptionHandler {
    private final BlockingQueue<File> queue = new LinkedBlockingQueue<File>();
    private final Thread thread = new Thread((Runnable)this, "Decompiler thread");
    private boolean run = true;

    public RuntimeDecompilerAsync(File outputPath) {
        super(outputPath);
        this.thread.setDaemon(true);
        this.thread.setPriority(1);
        this.thread.setUncaughtExceptionHandler(this);
        this.thread.start();
    }

    @Override
    public void decompile(File file) {
        if (this.run) {
            this.queue.offer(file);
        } else {
            super.decompile(file);
        }
    }

    @Override
    public void run() {
        while (this.run) {
            try {
                File file = this.queue.take();
                super.decompile(file);
            }
            catch (InterruptedException ex2) {
                this.run = false;
            }
            catch (Exception ex3) {
                ex3.printStackTrace();
            }
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex2) {
        this.logger.error("Async decompiler encountered an error and will terminate. Further decompile requests will be handled synchronously. {} {}", ex2.getClass().getName(), ex2.getMessage());
        this.flush();
    }

    private void flush() {
        File file;
        this.run = false;
        while ((file = (File)this.queue.poll()) != null) {
            this.decompile(file);
        }
    }
}

