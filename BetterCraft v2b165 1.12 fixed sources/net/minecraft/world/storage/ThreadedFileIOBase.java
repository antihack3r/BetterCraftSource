// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.storage;

import java.util.Collections;
import com.google.common.collect.Lists;
import java.util.List;

public class ThreadedFileIOBase implements Runnable
{
    private static final ThreadedFileIOBase INSTANCE;
    private final List<IThreadedFileIO> threadedIOQueue;
    private volatile long writeQueuedCounter;
    private volatile long savedIOCounter;
    private volatile boolean isThreadWaiting;
    
    static {
        INSTANCE = new ThreadedFileIOBase();
    }
    
    private ThreadedFileIOBase() {
        this.threadedIOQueue = Collections.synchronizedList((List<IThreadedFileIO>)Lists.newArrayList());
        final Thread thread = new Thread(this, "File IO Thread");
        thread.setPriority(1);
        thread.start();
    }
    
    public static ThreadedFileIOBase getThreadedIOInstance() {
        return ThreadedFileIOBase.INSTANCE;
    }
    
    @Override
    public void run() {
        while (true) {
            this.processQueue();
        }
    }
    
    private void processQueue() {
        for (int i = 0; i < this.threadedIOQueue.size(); ++i) {
            final IThreadedFileIO ithreadedfileio = this.threadedIOQueue.get(i);
            final boolean flag = ithreadedfileio.writeNextIO();
            if (!flag) {
                this.threadedIOQueue.remove(i--);
                ++this.savedIOCounter;
            }
            try {
                Thread.sleep(this.isThreadWaiting ? 0L : 10L);
            }
            catch (final InterruptedException interruptedexception1) {
                interruptedexception1.printStackTrace();
            }
        }
        if (this.threadedIOQueue.isEmpty()) {
            try {
                Thread.sleep(25L);
            }
            catch (final InterruptedException interruptedexception2) {
                interruptedexception2.printStackTrace();
            }
        }
    }
    
    public void queueIO(final IThreadedFileIO fileIo) {
        if (!this.threadedIOQueue.contains(fileIo)) {
            ++this.writeQueuedCounter;
            this.threadedIOQueue.add(fileIo);
        }
    }
    
    public void waitForFinish() throws InterruptedException {
        this.isThreadWaiting = true;
        while (this.writeQueuedCounter != this.savedIOCounter) {
            Thread.sleep(10L);
        }
        this.isThreadWaiting = false;
    }
}
