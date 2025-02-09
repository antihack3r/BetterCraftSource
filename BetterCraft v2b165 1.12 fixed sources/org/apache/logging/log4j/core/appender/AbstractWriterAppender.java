// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.appender;

import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LogEvent;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.Serializable;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.StringLayout;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public abstract class AbstractWriterAppender<M extends WriterManager> extends AbstractAppender
{
    protected final boolean immediateFlush;
    private final M manager;
    private final ReadWriteLock readWriteLock;
    private final Lock readLock;
    
    protected AbstractWriterAppender(final String name, final StringLayout layout, final Filter filter, final boolean ignoreExceptions, final boolean immediateFlush, final M manager) {
        super(name, filter, layout, ignoreExceptions);
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = this.readWriteLock.readLock();
        this.manager = manager;
        this.immediateFlush = immediateFlush;
    }
    
    @Override
    public void append(final LogEvent event) {
        this.readLock.lock();
        try {
            final String str = this.getStringLayout().toSerializable(event);
            if (str.length() > 0) {
                this.manager.write(str);
                if (this.immediateFlush || event.isEndOfBatch()) {
                    this.manager.flush();
                }
            }
        }
        catch (final AppenderLoggingException ex) {
            this.error("Unable to write " + this.manager.getName() + " for appender " + this.getName() + ": " + ex);
            throw ex;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public M getManager() {
        return this.manager;
    }
    
    public StringLayout getStringLayout() {
        return (StringLayout)this.getLayout();
    }
    
    @Override
    public void start() {
        if (this.getLayout() == null) {
            AbstractWriterAppender.LOGGER.error("No layout set for the appender named [{}].", this.getName());
        }
        if (this.manager == null) {
            AbstractWriterAppender.LOGGER.error("No OutputStreamManager set for the appender named [{}].", this.getName());
        }
        super.start();
    }
    
    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        this.setStopping();
        boolean stopped = super.stop(timeout, timeUnit, false);
        stopped &= this.manager.stop(timeout, timeUnit);
        this.setStopped();
        return stopped;
    }
}
